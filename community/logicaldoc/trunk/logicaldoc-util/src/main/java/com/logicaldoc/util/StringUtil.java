package com.logicaldoc.util;

import java.util.ArrayList;

/**
 * Some utility methods specialized in string manipulation
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class StringUtil {

	/**
	 * Splits a string into tokens separated by a separator
	 * 
	 * @param src The source string
	 * @param separator The separator character
	 * @param tokenSize Size or each token
	 * @return
	 */
	public static String split(String src, char separator, int tokenSize) {
		StringBuffer sb = new StringBuffer();
		String[] tokens = split(src, tokenSize);
		for (int i = 0; i < tokens.length; i++) {
			if (sb.length() > 0)
				sb.append(separator);
			sb.append(tokens[i]);
		}
		return sb.toString();
	}

	/**
	 * Splits a string into an array of tokens
	 * 
	 * @param src The source string
	 * @param tokenSize size of each token
	 * @return
	 */
	public static String[] split(String src, int tokenSize) {
		ArrayList<String> buf = new ArrayList<String>();
		for (int i = 0; i < src.length(); i += tokenSize) {
			int j = i + tokenSize;
			if (j > src.length())
				j = src.length();
			buf.add(src.substring(i, j));
		}
		return buf.toArray(new String[] {});
	}
}