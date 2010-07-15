package com.logicaldoc.gui.common.client.i18n;

import java.util.HashMap;
import java.util.LinkedHashMap;

import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;

/**
 * Retrieves i18n resources
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class I18N {
	private static String locale = "en";

	private static GUIValuePair[] languages;

	private static HashMap<String, String> bundle = new HashMap<String, String>();

	public static String message(String key) {
		// try {
		// return
		// Dictionary.getDictionary("messages_i18n").get(key.replaceAll("\\.",
		// "_"));
		// } catch (Throwable t) {
		// return key;
		// }
		if (bundle.containsKey(key))
			return bundle.get(key);
		else
			return key;
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
			for (GUIValuePair l : languages) {
				map.put(l.getCode(), l.getValue());
			}
		return map;
	}

	public GUIValuePair[] getLanguages() {
		return languages;
	}

	public static void setLanguages(GUIValuePair[] languages) {
		I18N.languages = languages;
	}

	public static void initBundle(GUIValuePair[] messages) {
		bundle.clear();
		for (GUIValuePair val : messages) {
			bundle.put(val.getCode(), val.getValue());
		}
	}

	public static void init(GUIInfo info) {
		setLanguages(info.getSupportedLanguages());
		initBundle(info.getBundle());
	}
}