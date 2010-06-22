package com.logicaldoc.gui.common.client.i18n;

import java.util.LinkedHashMap;

import com.google.gwt.i18n.client.Dictionary;
import com.logicaldoc.gui.common.client.beans.GUILanguage;

/**
 * Retrieves i18n resources
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class I18N {
	private static String locale = "en";

	private static GUILanguage[] languages;

	public static String message(String key) {
		try {
			return Dictionary.getDictionary("messages_i18n").get(key.replaceAll("\\.", "_"));
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
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (addEmpty)
			map.put("", " ");
		if (languages != null)
			for (GUILanguage l : languages) {
				map.put(l.getCode(), l.getDisplayName());
			}
		return map;
	}

	public GUILanguage[] getLanguages() {
		return languages;
	}

	public static void setLanguages(GUILanguage[] languages) {
		I18N.languages = languages;
	}
}