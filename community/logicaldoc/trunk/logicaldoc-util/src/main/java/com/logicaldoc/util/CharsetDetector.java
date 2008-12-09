package com.logicaldoc.util;

import java.io.UnsupportedEncodingException;

import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

/**
 * Utility class used to detect the encoding of a string and to apply encodings.
 * <p>
 * Basically this is a Facade on Mozilla jchardet
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class CharsetDetector {
	/**
	 * Determines the encoding of a string
	 * 
	 * @param src The string to check
	 * @return The encoding code (i.e.: UTF-8)
	 */
	public static String detectEncoding(String src) {
		String[] encodings = CharsetDetector.detectEncodings(src);
		if (encodings == null || encodings.length == 0)
			return "UTF-8";
		else
			return encodings[0];
	}

	/**
	 * Determines the probable encodings for a string
	 * 
	 * @param src The string to check
	 * @return The array of encodings starting from the most probable
	 */
	public static String[] detectEncodings(String src) {
		nsDetector det = new nsDetector();

		// Set an observer...
		// The Notify() will be called when a matching charset is found.
		det.Init(new nsICharsetDetectionObserver() {
			public void Notify(String charset) {
				HtmlCharsetDetector.found = true;
			}
		});

		byte[] buf = src.getBytes();
		boolean done = det.DoIt(buf, buf.length, false);
		det.DataEnd();

		return det.getProbableCharsets();
	}

	/**
	 * Detects the most probable encoding and converts the passed string
	 * 
	 * @param str The string to be converted
	 * @return The converted string
	 */
	public static String convert(String str) {
		try {
			return new String(str.getBytes(), detectEncoding(str));
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}
}