package com.logicaldoc.web;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Log4jConfigurer;

import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.util.dbinit.DBInit;
import com.logicaldoc.util.io.ZipUtil;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * Listener that initialises relevant system stuffs during application startup
 * and session life cycle.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class ApplicationListener implements ServletContextListener, HttpSessionListener {

	private static Logger log = LoggerFactory.getLogger(ApplicationListener.class);

	public static boolean needRestart = false;

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@SuppressWarnings("deprecation")
	public void contextDestroyed(ServletContextEvent sce) {
		log.warn("Shutting down application");

		try {
			ContextProperties config = new ContextProperties();
			if (config.getProperty("jdbc.url").contains("jdbc:hsqldb")) {
				DBInit dbInit = new DBInit();
				dbInit.setDriver(config.getProperty("jdbc.driver"));
				dbInit.setUrl(config.getProperty("jdbc.url"));
				dbInit.setUsername(config.getProperty("jdbc.username"));
				dbInit.setPassword(config.getProperty("jdbc.password"));

				dbInit.executeSql("shutdown compact");
				log.warn("Embedded database stopped");
			}
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}

		try {
			ContextProperties config = new ContextProperties();
			Enumeration<Driver> drivers = DriverManager.getDrivers();
			Driver d = null;
			while (drivers.hasMoreElements()) {
				d = drivers.nextElement();
				try {
					DriverManager.deregisterDriver(d);
					log.warn(String.format("Driver %s unregistered", d.getClass().getName()));
				} catch (SQLException ex) {
					log.warn(String.format("Error unregistering driver %s", d), ex);
				}
			}
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}

//		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//		Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
//
//		for (Thread t : threadArray) {
//			synchronized (t) {
//				// if ((t.getName().startsWith("Abandoned connection cleanup")
//				// || t.getName().contains("webdav")
//				// || t.getName().startsWith("Scheduler_") ||
//				// t.getName().startsWith("Thread-"))
//				// && !Thread.currentThread().equals(t) && !t.isInterrupted())
//				if ((t.getName().toLowerCase().contains("abandoned connection") || t.getName().startsWith("Scheduler_"))
//						&& !Thread.currentThread().equals(t) && !t.isInterrupted())
//					try {
//						t.stop(); // don't complain, it works
//						log.warn("Killed thread " + t.getName());
//					} catch (Throwable e) {
//						log.warn("Error killing " + t.getName());
//					}
//			}
//		}

		log.warn("Application stopped");
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();

		// Initialize logging
		String log4jPath = null;
		try {
			URL configFile = null;
			try {
				configFile = LoggingConfigurator.class.getClassLoader().getResource("/log.xml");
			} catch (Throwable t) {
			}

			if (configFile == null)
				configFile = LoggingConfigurator.class.getClassLoader().getResource("log.xml");

			log4jPath = URLDecoder.decode(configFile.getPath(), "UTF-8");

			// Setup the correct logs folder
			ContextProperties config = new ContextProperties();
			LoggingConfigurator lconf = new LoggingConfigurator();
			lconf.setLogsRoot(config.getProperty("conf.logdir"));
			lconf.write();

			Log4jConfigurer.initLogging(log4jPath);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		// Prepare the plugins dir
		String pluginsDir = context.getRealPath("/WEB-INF/lib");

		// Initialize plugins
		PluginRegistry.getInstance().init(pluginsDir);

		// Reinitialize logging because some plugins may have added new
		// categories
		try {
			Log4jConfigurer.shutdownLogging();
			Log4jConfigurer.initLogging(log4jPath);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		// Clean the upload folder
		File uploadDir = new File(context.getRealPath("upload"));
		try {
			if (uploadDir.exists())
				FileUtils.forceDelete(uploadDir);
		} catch (IOException e) {
			log.warn(e.getMessage());
			e.printStackTrace();
		}

		needRestart = PluginRegistry.getInstance().isRestartRequired();

		// Try to unpack new plugins
		try {
			unpackPlugins(context);
		} catch (IOException e) {
			log.warn(e.getMessage());
			e.printStackTrace();
		}

		if (needRestart)
			System.out.println("The application needs to be restarted");
	}

	/**
	 * Unpacks the plugin that are newly installed and positioned in the plugins
	 * folder
	 * 
	 * @throws IOException
	 */
	private void unpackPlugins(ServletContext context) throws IOException {
		File pluginsDir = PluginRegistry.getPluginsDir();
		File[] archives = pluginsDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.contains("plugin"))
					return true;
				else
					return false;
			}
		});
		File webappDir = new File(context.getRealPath("/"));
		if (archives != null)
			for (File archive : archives) {
				System.out.println("Unpack plugin " + archive.getName());
				ZipUtil.unzip(archive.getPath(), webappDir.getPath());
				FileUtils.forceDelete(archive);
				needRestart = true;
			}
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {

	}

	/**
	 * Frees temporary upload folders.
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		String id = session.getId();
		File uploadFolder = new File(session.getServletContext().getRealPath("upload"));
		uploadFolder = new File(uploadFolder, id);
		try {
			if (uploadFolder.exists())
				FileUtils.forceDelete(uploadFolder);
		} catch (Throwable e) {
			log.warn(e.getMessage());
		}
	}
}