package com.logicaldoc.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Log4jConfigurer;

/**
 * Listener that initialises relevant system stuffs during application startup
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class ApplicationInitializer implements ServletContextListener {

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
			Log4jConfigurer.initLogging(log4jPath);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		// Prepare the plugins dir
		String pluginsDir = context.getRealPath("/WEB-INF/plugins");
		
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
	}
}