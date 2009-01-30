package com.logicaldoc.util;

import java.io.UnsupportedEncodingException;

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
				// Do nothing
			}
		});

		byte[] buf = src.getBytes();
		det.DoIt(buf, buf.length, false);
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
		if (str == null)
			return null;

		//Count the number of question marks in the original string
		int qmCount=str.replaceAll("\\?", "").length();

		
		String[] encodings = detectEncodings(str);
		if(encodings==null || encodings.length==0)
			return str;
		
		String conversion = "";
		//Iterate over probable encodings
		for (int i = 0; i < encodings.length; i++) {
			try {
				conversion = new String(str.getBytes(), encodings[i]);
				//The character not correctly encoded appears as a question mark
				int qmCount2=conversion.replaceAll("\\?", "").length();
				if(qmCount2==qmCount){
					//No more question marks added, so we have found the correct encoding
					break;
				}
			} catch (UnsupportedEncodingException e) {
				return str;
			}
		}

		return conversion;
	}
}