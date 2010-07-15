package com.logicaldoc.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A class for retrieval of localized messages. All bundles declared in
 * ResourceBundle extension point. The first key match wins.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class I18N {
	private I18N() {
	}

	public static String getMessage(String key, String lang) {
		return message(key, new Locale(lang));
	}

	public static String message(String key, Locale locale) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages", locale);
			return bundle.getString(key);
		} catch (Throwable t) {
		}

		return key;
	}

	public static String getMessage(String key, Locale locale, Object[] values) {
		String msg = message(key, locale);
		return MessageFormat.format(msg, values);
	}
}
