package com.logicaldoc.util.io;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.crypto.digests.MD4Digest;
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

	/**
	 * Converts a string into a MD4 hash.
	 * 
	 * @param original the original string to be encrypted.
	 * @return the returned hash as bytes.
	 */
	public static String ctyptStringMD4(String original) {
		String copy = "";
		String pwd = original;
		if (pwd == null) {
			pwd = "";
		}

		try {
			MD4Digest md4 = new MD4Digest();
			byte[] pwdBytes = original.getBytes();
			md4.update(pwdBytes, 0, pwdBytes.length);
			byte[] encPwd = new byte[md4.getDigestSize()];
			md4.doFinal(encPwd, 0);
			copy = new BigInteger(1, encPwd).toString(16);
		} catch (Throwable nsae) {
			logError(nsae.getMessage());
		}
		return copy;
	}

	private static void logError(String message) {
		Logger logger = LoggerFactory.getLogger(CryptUtil.class);
		logger.error(message);
	}
}