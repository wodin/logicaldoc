package com.logicaldoc.core;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * This class is a utility to handle relevant system properties
 * 
 * @author Alessandro Gasparini
 * @version 1.0
 */
public final class SystemProperty {

	private static Properties properties = System.getProperties();

	public static final String LOGICALDOC_HOME = "LOGICALDOC_HOME";

	public static final String LOGICALDOC_APP_ROOTDIR = "logicaldoc.app.rootdir";
	
	public static final String LOGICALDOC_PLUGINSDIR = "logicaldoc.app.pluginsdir";

	/**
	 * Utility class, don't instantiate.
	 */
	private SystemProperty() {
	}

	/**
	 * @param name
	 * @param value
	 */
	public static void setProperty(String name, String value) {
		SystemProperty.properties.put(name, value);
		System.setProperty(name, value);
	}

	/**
	 * @param name
	 */
	public static String getProperty(String name) {
		return (String) SystemProperty.properties.get(name);
	}

	/**
	 * @param name
	 * @param defaultValue
	 */
	public static String getProperty(String name, String defaultValue) {
		String value = getProperty(name);
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}
		return value;
	}

	public static Properties getProperties() {
		return SystemProperty.properties;
	}

	/**
	 * The home directory of the application, where the data are stored
	 */
	public static String getApplicationHome() {
		return getProperty(LOGICALDOC_HOME);
	}

	/**
	 * The root application path, where the binaries are stored
	 */
	public static String getApplicationRoot() {
		return getProperty(LOGICALDOC_APP_ROOTDIR);
	}
	
	/**
	 * The directory where all pugins are placed
	 */
	public static String getPluginsDir() {
		return getProperty(LOGICALDOC_PLUGINSDIR);
	}
}