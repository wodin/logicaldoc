package com.logicaldoc.util;

import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * Utility methods for Locale handling
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class LocaleUtil {

	/**
	 * Creates the locale from a string
	 * 
	 * @param str String in format <language>_<country>_<variant>
	 * @return
	 */
	public static Locale toLocale(String str) {
		if (StringUtils.isEmpty(str))
			return Locale.ENGLISH;

		str=str.replace('-', '_');
		
		String lang = "";
		String country = "";
		String variant = "";
		StringTokenizer st = new StringTokenizer(str, "_", false);
		lang = st.nextToken();
		if (st.hasMoreTokens())
			country = st.nextToken();
		if (st.hasMoreTokens())
			variant = st.nextToken();
		return new Locale(lang, country, variant);
	}
}
