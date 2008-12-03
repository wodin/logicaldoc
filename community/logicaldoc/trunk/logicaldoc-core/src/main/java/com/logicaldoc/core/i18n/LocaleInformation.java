package com.logicaldoc.core.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * A central bean for handling all localisation information like data format.<p>
 * All settings are retrieved from resource bundle <code>i18n.localeInformation</code>
 * 
 * 
 * @author Sebastian Stein
 */
public class LocaleInformation {
	private static final String BUNDLE_NAME = "i18n.localeInformation";

	/**
	 * Retrieves a localised information for the specified locale.
	 * 
	 * @param key The name of the information
	 * @param locale The wanted locale
	 * @return The found information
	 */
	public static String getInfo(String key, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		return bundle.getString(key);
	}

	/**
	 * Retrieves a localised information for the specified language.
	 * 
	 * @param key The name of the information
	 * @param language The wanted language
	 * @return The found information
	 */
	public static String getInfo(String key, String language) {
		Locale locale = new Locale(language);
		return getInfo(key, locale);
	}

	/**
	 * Returns the standard date format for the specified language
	 */
	public static String getDateFormat(String language) {
		return getInfo("dateFormat", language);
	}
	
	/**
	 * Returns the short date format for the specified language
	 */
	public static String getDateFormatShort(String language) {
		return getInfo("dateFormatShort", language);
	}
}
