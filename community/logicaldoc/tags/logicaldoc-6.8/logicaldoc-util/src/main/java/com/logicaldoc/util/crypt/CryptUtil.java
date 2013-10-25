package com.logicaldoc.util.crypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.crypto.digests.MD4Digest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.util.crypt.StringEncrypter.EncryptionException;

public class CryptUtil {
	private static Logger log = LoggerFactory.getLogger(CryptUtil.class);

	private String encryptionKey = "thekey";

	public CryptUtil(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public String encrypt(String str) {
		String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;
		try {
			StringEncrypter encrypter = new StringEncrypter(encryptionScheme, encryptionKey);
			return encrypter.encrypt(str);
		} catch (EncryptionException e) {
			return null;
		}
	}

	public String decrypt(String str) {
		String encryptionScheme = StringEncrypter.DESEDE_ENCRYPTION_SCHEME;
		try {
			StringEncrypter encrypter = new StringEncrypter(encryptionScheme, encryptionKey);
			return encrypter.decrypt(str);
		} catch (EncryptionException e) {
			return null;
		}
	}

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
			log.error(nsae.getMessage());
		}

		return copy;
	}

	/**
	 * Converts a string into a MD4 hash.
	 * 
	 * @param original the original string to be encrypted.
	 * @return the returned hash as bytes.
	 */
	public static String hashMD4(String original) {
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
			copy = getHex(encPwd).toLowerCase();
			// new BigInteger(1, encPwd).toString(16);
		} catch (Throwable nsae) {
			log.error(nsae.getMessage());
		}
		return copy;
	}

	/**
	 * Converts a string into a MD4 hash suitable for the NTLM v1 authentication
	 */
	public static String hashNTLM1(String original) {
		try {
			if (original == null) {
				original = "";
			}
			MD4Digest md4 = new MD4Digest();
			int len = original.length();
			byte[] pwdBytes = new byte[len * 2];

			for (int i = 0; i < len; i++) {
				char ch = original.charAt(i);
				pwdBytes[i * 2] = (byte) ch;
				pwdBytes[i * 2 + 1] = (byte) ((ch >> 8) & 0xFF);
			}

			md4.update(pwdBytes, 0, pwdBytes.length);
			byte[] encPwd = new byte[16];
			md4.doFinal(encPwd, 0);

			return CryptUtil.getHex(encPwd).substring(0, 32);
		} catch (Throwable nsae) {
			log.error(nsae.getMessage());
			return null;
		}
	}

	public static String getHex(byte[] raw) {
		String HEXES = "0123456789ABCDEF";
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
}
