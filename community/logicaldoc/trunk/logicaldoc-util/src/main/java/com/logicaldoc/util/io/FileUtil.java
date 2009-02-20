package com.logicaldoc.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class manages I/O operations with files.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 4.0
 */
public class FileUtil {
	static final int BUFF_SIZE = 100000;

	static final byte[] buffer = new byte[BUFF_SIZE];

	protected static Log log = LogFactory.getLog(FileUtil.class);

	public static void writeFile(InputStream in, String filepath) throws Exception {
		OutputStream os = null;
		try {
			os = new FileOutputStream(filepath);

			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1)
						break;
					os.write(buffer, 0, amountRead);
				}
			}
		} catch (Exception e) {
			logError(e.getMessage());
		} finally {
			if (os != null)
				os.flush();
			try {
				if (in != null)
					in.close();
				if (os != null)
					os.close();
			} catch (IOException e) {
				logError(e.getMessage());
			}
		}
	}

	public static void writeFile(String text, String filepath) {
		BufferedOutputStream bos = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(filepath));
			bos.write(text.getBytes());
		} catch (Exception e) {
			logError(e.getLocalizedMessage());
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException ioe) {
					;
				}
			}
		}
	}

	public static void appendFile(String text, String filepath) {
		OutputStream bos = null;

		try {
			bos = new FileOutputStream(filepath, true);
			bos.write(text.getBytes());
		} catch (Exception e) {
			logError(e.getLocalizedMessage());
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException ioe) {
					;
				}
			}
		}
	}

	private static void logError(String message) {
		Log logger = LogFactory.getLog(FileUtil.class);
		logger.error(message);
	}

	/**
	 * This method calculates the digest of a file using the algorithm SHA-1.
	 * 
	 * @param file The file for which will be computed the digest
	 * @return digest
	 */
	public static String computeDigest(File file) {
		String digest = "";
		InputStream is = null;
		MessageDigest sha = null;

		try {
			is = new BufferedInputStream(new FileInputStream(file), BUFF_SIZE);
			if (is != null) {
				sha = MessageDigest.getInstance("SHA-1");
				byte[] message = new byte[BUFF_SIZE];
				int len = 0;
				while ((len = is.read(message)) != -1) {
					sha.update(message, 0, len);
				}
				byte[] messageDigest = sha.digest();
				// convert the array to String
				int size = messageDigest.length;
				StringBuffer buf = new StringBuffer();
				int unsignedValue = 0;
				String strUnsignedValue = null;
				for (int i = 0; i < size; i++) {
					// convert each messageDigest byte to unsigned
					unsignedValue = ((int) messageDigest[i]) & 0xff;
					strUnsignedValue = Integer.toHexString(unsignedValue);
					// at least two letters
					if (strUnsignedValue.length() == 1)
						buf.append("0");
					buf.append(strUnsignedValue);
				}
				digest = buf.toString();
				log.debug("Computed Digest: " + digest);

				return digest;
			}
		} catch (IOException io) {
			log.error("Error generating digest: ", io);
		} catch (Throwable t) {
			log.error("Error generating digest: ", t);
		}
		return null;
	}

	/**
	 * Writes the specified classpath resource into a file
	 * 
	 * @param resourceName Fully qualified resource name
	 * @param out The output file
	 * @throws IOException
	 */
	public static void copyResource(String resourceName, File out) throws IOException {
		InputStream is = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResource(
				resourceName).openStream());
		OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
		try {
			for (;;) {
				int b = is.read();
				if (b == -1)
					break;
				os.write(b);
			}
		} finally {
			is.close();
			os.close();
		}
	}
}