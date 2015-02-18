package com.logicaldoc.util.crypt;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.logicaldoc.util.io.FileUtil;

public class CryptUtil {
	private static Logger log = LoggerFactory.getLogger(CryptUtil.class);

	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";

	public static final String DES_ENCRYPTION_SCHEME = "DES";

	public static final String DEFAULT_ENCRYPTION_KEY = "This is a fairly long phrase used to encrypt";

	private KeySpec keySpec;

	private SecretKeyFactory keyFactory;

	private Cipher cipher;

	private static final String UNICODE_FORMAT = "UTF8";

	public CryptUtil(String encryptionKey) throws EncryptionException {
		this(DES_ENCRYPTION_SCHEME, encryptionKey);
	}

	public CryptUtil(String encryptionScheme, String encryptionKey) throws EncryptionException {
		if (encryptionKey == null)
			throw new IllegalArgumentException("encryption key was null");
		try {
			String key = encryptionKey;
			if (encryptionKey.length() < 32)
				key = StringUtils.rightPad(encryptionKey, 32, '*');
			byte[] keyAsBytes = key.getBytes(UNICODE_FORMAT);
			if (encryptionScheme.equals(DESEDE_ENCRYPTION_SCHEME)) {
				keySpec = new DESedeKeySpec(keyAsBytes);
			} else if (encryptionScheme.equals(DES_ENCRYPTION_SCHEME)) {
				keySpec = new DESKeySpec(keyAsBytes);
			} else {
				throw new IllegalArgumentException("Encryption scheme not supported: " + encryptionScheme);
			}
			keyFactory = SecretKeyFactory.getInstance(encryptionScheme);
			cipher = Cipher.getInstance(encryptionScheme);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new EncryptionException(e);
		}
	}

	public void encrypt(File inputFile, File outputFile) throws EncryptionException {
		if (inputFile == null || !inputFile.exists())
			throw new IllegalArgumentException("Unencrypted file not found in " + inputFile.getPath());
		try {
			SecretKey key = keyFactory.generateSecret(keySpec);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] clearContent = FileUtils.readFileToByteArray(inputFile);
			byte[] encryptedContent = cipher.doFinal(clearContent);
			outputFile.mkdirs();
			FileUtil.strongDelete(outputFile);
			outputFile.createNewFile();
			FileUtils.writeByteArrayToFile(outputFile, encryptedContent);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new EncryptionException(e);
		}
	}

	public void decrypt(File inputFile, File outputFile) throws EncryptionException {
		try {
			if (inputFile == null || !inputFile.exists())
				throw new IllegalArgumentException("Encrypted file not found in " + inputFile.getPath());
			SecretKey key = keyFactory.generateSecret(keySpec);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] encryptedContent = FileUtils.readFileToByteArray(inputFile);
			byte[] clearContent = cipher.doFinal(encryptedContent);
			outputFile.mkdirs();
			FileUtil.strongDelete(outputFile);
			outputFile.createNewFile();
			FileUtils.writeByteArrayToFile(outputFile, clearContent);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new EncryptionException(e);
		}
	}

	public String encrypt(String unencryptedString) throws EncryptionException {
		if (unencryptedString == null || unencryptedString.trim().length() == 0)
			throw new IllegalArgumentException("unencrypted string was null or empty");
		try {
			SecretKey key = keyFactory.generateSecret(keySpec);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cleartext = unencryptedString.getBytes(UNICODE_FORMAT);
			byte[] ciphertext = cipher.doFinal(cleartext);
			BASE64Encoder base64encoder = new BASE64Encoder();
			return base64encoder.encode(ciphertext);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new EncryptionException(e);
		}
	}

	public String decrypt(String encryptedString) throws EncryptionException {
		if (encryptedString == null || encryptedString.trim().length() <= 0)
			throw new IllegalArgumentException("encrypted string was null or empty");
		try {
			SecretKey key = keyFactory.generateSecret(keySpec);
			cipher.init(Cipher.DECRYPT_MODE, key);
			BASE64Decoder base64decoder = new BASE64Decoder();
			byte[] cleartext = base64decoder.decodeBuffer(encryptedString);
			byte[] ciphertext = cipher.doFinal(cleartext);
			return bytes2String(ciphertext);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new EncryptionException(e);
		}
	}

	private static String bytes2String(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			stringBuffer.append((char) bytes[i]);
		}
		return stringBuffer.toString();
	}

	/**
	 * This method encodes a given string using the SHA algorythm
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

	public static class EncryptionException extends Exception {
		private static final long serialVersionUID = 1L;

		public EncryptionException(Throwable t) {
			super(t);
		}
	}
}
