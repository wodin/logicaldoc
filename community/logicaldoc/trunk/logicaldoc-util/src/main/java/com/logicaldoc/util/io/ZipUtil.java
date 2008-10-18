package com.logicaldoc.util.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is for handling with zip-files.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 4.0
 */
public class ZipUtil {
	/**
	 * This method extracts all entries of a zip-file.
	 * 
	 * @param zipsource Path of the zip-file.
	 * @param target Path of the extracted files.
	 * @return True if successfully extracted.
	 */
	@SuppressWarnings("unchecked")
	public static boolean unzip(String zipsource, String target) {
		boolean result = true;
		try {
			if (!target.endsWith("/"))
				target = target + "/";
			ZipFile zip = new ZipFile(zipsource);
			Enumeration entries = zip.getEntries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				saveEntry(target, zip, entry);
			}

			zip.close();
		} catch (Exception e) {
			result = false;
			logError(e.getMessage());
		}
		return result;
	}

	/**
	 * This method extracts one entry of a zip-file.
	 * 
	 * @param zipsource Path of the zip-file.
	 * @param target Path of the extracted files.
	 * @param entry Name of the entry to be extracted.
	 * @return True if successfully extracted.
	 */
	public static boolean unzip(String zipsource, String target, String entry) {
		boolean result = true;

		try {
			if (!target.endsWith("/")) {
				target = target + "/";
			}

			ZipFile zip = new ZipFile(zipsource);
			ZipEntry zipe = new ZipEntry(entry);
			saveEntry(target, zip, zipe);
		} catch (Exception e) {
			result = false;
			logError(e.getMessage());
		}

		return result;
	}

	/**
	 * Extracts an entry from a zip file to a target directory.
	 * 
	 * @param target the base directory the entry should be extracted to
	 * @param zip the ZIP file
	 * @param entry the to be extracted entry in the ZIP file
	 */
	private static void saveEntry(String target, ZipFile zip, ZipEntry entry) throws Exception {
		String targetFileName = target + entry.getName();

		if (entry.isDirectory()) {
			File dir = new File(targetFileName);
			dir.mkdirs();
		} else {
			File file = new File(targetFileName);
			File dir = new File(file.getParent());
			dir.mkdirs();
			dir = null;
			file = null;

			InputStream in = zip.getInputStream(entry);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFileName));
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			in.close();
			bos.close();
		}
	}

	private static void logError(String message) {
		Log logger = LogFactory.getLog(ZipUtil.class);
		logger.error(message);
	}
}