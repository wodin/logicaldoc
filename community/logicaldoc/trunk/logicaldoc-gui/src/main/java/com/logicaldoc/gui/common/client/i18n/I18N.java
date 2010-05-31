package com.logicaldoc.gui.common.client.i18n;

import java.util.LinkedHashMap;

import com.google.gwt.i18n.client.Dictionary;

/**
 * Retrieves i18n resources
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class I18N {
	private static String locale = "en";

	public static String message(String key) {
		try {
			return Dictionary.getDictionary("frontend_i18n").get(key.replaceAll("\\.", "_"));
		} catch (Throwable t) {
			return key;
		}
	}

	public static String message(String key, String val) {
		String tmp = message(key);
		return tmp.replaceAll("\\{0\\}", val);
	}

	public static String message(String key, String[] vals) {
		String tmp = message(key);
		for (int i = 0; i < vals.length; i++) {
			tmp = tmp.replaceAll("\\{" + i + "\\}", vals[i]);
		}
		return tmp;
	}

	public static String getLocale() {
		return locale;
	}

	public static void setLocale(String locale) {
		I18N.locale = locale;
	}

	public static LinkedHashMap<String, String> getSupportedLanguages(boolean addEmpty) {
		LinkedHashMap<String, String> languages = new LinkedHashMap<String, String>();
		if (addEmpty)
			languages.put("", " ");
		languages.put("en", I18N.message("langen"));
		languages.put("es", I18N.message("langes"));
		languages.put("it", I18N.message("langit"));
		languages.put("de", I18N.message("langde"));
		languages.put("fr", I18N.message("langfr"));
		return languages;
	}
}