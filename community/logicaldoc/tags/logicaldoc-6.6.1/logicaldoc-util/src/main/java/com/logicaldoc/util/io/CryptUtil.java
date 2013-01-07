package com.logicaldoc.util.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains methods to decode and encode string.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class CryptUtil {
	/**
	 * This method encodes a given string.
	 * 
	 * @param original String to encode.
	 * @return Encoded string.
	 */
	public static String cryptString(String original) {
		String copy = "";

		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] digest = md.digest(original.getBytes());

			for (int i = 0; i < digest.length; i++) {
				copy += Integer.toHexString(digest[i] & 0xFF);
			}
		} catch (NoSuchAlgorithmException nsae) {
			logError(nsae.getMessage());
		}

		return copy;
	}

	private static void logError(String message) {
		Logger logger = LoggerFactory.getLogger(CryptUtil.class);
		logger.error(message);
	}
}