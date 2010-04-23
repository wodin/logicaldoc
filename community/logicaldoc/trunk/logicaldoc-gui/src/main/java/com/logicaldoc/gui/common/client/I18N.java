package com.logicaldoc.gui.common.client;

import java.util.LinkedHashMap;

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

	public static String getMessage(String key, String[] vals) {
		String tmp = getMessage(key);
		for (int i = 0; i < vals.length; i++) {
			tmp = tmp.replaceAll("\\{" + i + "\\}", vals[i]);
		}
		return tmp;
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

	public static LinkedHashMap<String, String> getSupportedLanguages(boolean addEmpty) {
		LinkedHashMap<String, String> languages = new LinkedHashMap<String, String>();
		if (addEmpty)
			languages.put("", " ");
		languages.put("en", I18N.getMessage("langen"));
		languages.put("es", I18N.getMessage("langes"));
		languages.put("it", I18N.getMessage("langit"));
		languages.put("de", I18N.getMessage("langde"));
		languages.put("fr", I18N.getMessage("langfr"));
		return languages;
	}
}