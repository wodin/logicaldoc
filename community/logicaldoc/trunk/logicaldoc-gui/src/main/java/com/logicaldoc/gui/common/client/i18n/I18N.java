package com.logicaldoc.gui.common.client.i18n;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUISession;
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

	private static GUIValuePair[] guiLanguages;

	private static HashMap<String, String> bundle = new HashMap<String, String>();

	private static DateTimeFormat dateFormatShort = null;

	private static DateTimeFormat dateFormat = null;

	public static String message(String key) {
		if (bundle.containsKey(key))
			return bundle.get(key);
		else
			return key;
	}

	public static String message(String key, String val) {
		String tmp = message(key);
		try {
			tmp = tmp.replaceAll("\\{0\\}", val);
		} catch (Throwable t) {
		}
		return tmp;
	}

	public static String message(String key, String[] vals) {
		String tmp = message(key);
		try {
			for (int i = 0; i < vals.length; i++) {
				tmp = tmp.replaceAll("\\{" + i + "\\}", vals[i]);
			}
		} catch (Throwable t) {
		}
		return tmp;

	}

	public static String getLocale() {
		return locale;
	}

	/**
	 * Computes the default suitable language for documents
	 */
	public static String getDefaultLocaleForDoc() {
		// Search for exact match
		for (GUIValuePair l : languages) {
			if (l.getCode().equals(locale))
				return l.getCode();
		}

		// Check the first 2 letters(the language)
		for (GUIValuePair l : languages) {
			if (l.getCode().startsWith(locale.substring(0, 2)))
				return l.getCode();
		}

		return languages[0].getCode();
	}

	public static char groupingSepator() {
		String gs = message("grouping_separator");
		return gs.charAt(gs.length() - 1);
	}

	public static char decimalSepator() {
		String gs = message("decimal_separator");
		return gs.charAt(gs.length() - 1);
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

	public static LinkedHashMap<String, String> getSupportedGuiLanguages(boolean addEmpty) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (addEmpty)
			map.put("", " ");
		if (guiLanguages != null)
			for (GUIValuePair l : guiLanguages) {
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
		setGuiLanguages(info.getSupportedGUILanguages());
		initBundle(info.getBundle());

		/*
		 * Prepare the date formatters
		 */
		dateFormatShort = DateTimeFormat.getFormat(message("format_dateshort"));
		dateFormat = DateTimeFormat.getFormat(message("format_date"));
	}

	public static void init(GUISession session) {
		init(session.getInfo());
		I18N.locale = session.getUser().getLanguage();
	}

	public static GUIValuePair[] getGuiLanguages() {
		return guiLanguages;
	}

	public static void setGuiLanguages(GUIValuePair[] guiLanguages) {
		I18N.guiLanguages = guiLanguages;
	}

	public static String formatDateShort(Date date) {
		if (date == null)
			return null;
		return dateFormatShort.format(new Date(date.getTime()));
	}

	public static String formatDate(Date date) {
		if (date == null)
			return null;
		return dateFormat.format(new Date(date.getTime()));
	}

	public static DateTimeFormat getDateFormatShort() {
		return dateFormatShort;
	}

	public static DateTimeFormat getDateFormat() {
		return dateFormat;
	}
}