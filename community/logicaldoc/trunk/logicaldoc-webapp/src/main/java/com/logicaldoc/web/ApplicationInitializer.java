package com.logicaldoc.web;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.FileUtils;
import org.springframework.util.Log4jConfigurer;

import com.logicaldoc.util.config.LoggingConfigurator;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.io.ZipUtil;

/**
 * Listener that initialises relevant system stuffs during application startup
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class ApplicationInitializer implements ServletContextListener {

	public static boolean needRestart = false;

	/**
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		Log4jConfigurer.shutdownLogging();
	}

	/**
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();

		// Initialize logging
		String log4jPath = context.getRealPath("/WEB-INF/classes/ldoc-log4j.xml");

		try {
			// Setup the correct logs folder
			PropertiesBean config = new PropertiesBean();
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
		com.logicaldoc.util.PluginRegistry.getInstance().init(pluginsDir);

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
			e.printStackTrace();
		}

		// Try to unpack new plugins
		try {
			unpackPlugins(context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unpacks the plugin that are newly installed and positioned in the plugins
	 * folder
	 * 
	 * @throws IOException
	 */
	private void unpackPlugins(ServletContext context) throws IOException {
		PropertiesBean config = new PropertiesBean();
		File pluginsDir = new File(config.getProperty("conf.plugindir"));
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
		for (File archive : archives) {
			System.out.println("Unpack plugin " + archive.getName());
			ZipUtil.unzip(archive.getPath(), webappDir.getPath());
			FileUtils.forceDelete(archive);
			needRestart = true;
		}
		if (needRestart)
			System.out.println("The application needs to be restarted");
	}
}