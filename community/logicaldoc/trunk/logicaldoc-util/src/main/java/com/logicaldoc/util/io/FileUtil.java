package com.logicaldoc.util.io;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
}