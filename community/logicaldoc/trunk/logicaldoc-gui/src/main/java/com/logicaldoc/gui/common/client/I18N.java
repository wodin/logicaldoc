package com.logicaldoc.gui.common.client;

import com.google.gwt.i18n.client.Dictionary;

/**
 * Retrieves i18n resources
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class I18N {
	private static String language = "en";

	public static String getMessage(String key) {
		try {
			return Dictionary.getDictionary("frontend_i18n").get(key);
		} catch (Throwable t) {
			return key;
		}
	}

	public static String getMessage(String key, String val) {
		String tmp = getMessage(key);
		return tmp.replaceAll("\\{0\\}", val);
	}

	public static String getFormat(String key) {
		try {
			return Dictionary.getDictionary("format_i18n").get(key);
		} catch (Throwable t) {
			return "";
		}
	}

	public static String getLanguage() {
		return language;
	}

	public static void setLanguage(String language) {
		I18N.language = language;
	}
}