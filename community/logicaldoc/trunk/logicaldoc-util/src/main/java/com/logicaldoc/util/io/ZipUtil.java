package com.logicaldoc.util.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

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
			ZipFile zip = new ZipFile(zipsource, "Cp850");
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

	private static void zipDir(File zipDir, ZipOutputStream zos, File startZipDir) {
		try {
			// get a listing of the directory content
			File[] dirList = zipDir.listFiles();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			// loop through dirList, and zip the files
			for (int i = 0; i < dirList.length; i++) {
				File f = dirList[i];
				if (f.isDirectory()) {
					// if the File object is a directory, call this
					// function again to add its content recursively
					zipDir(f, zos, startZipDir);
					// loop again
					continue;
				}
				// if we reached here, the File object f was not
				// a directory
				// create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(f);
				// create a new zip entry
				String path = f.getPath();
				if (!path.equals(startZipDir.getPath()))
					path = path.substring(startZipDir.getPath().length());
				if (path.startsWith(File.separator))
					path = path.substring(1);
				ZipEntry anEntry = new ZipEntry(path);
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				// close the Stream
				fis.close();
			}
		} catch (Exception e) {
			logError(e.getMessage());
		}
	}

	/**
	 * Compress a single file
	 * 
	 * @param src The source file
	 * @param dest The destination archive file
	 */
	public static void zipFile(File src, File dest) {
		try {
			// create a ZipOutputStream to zip the data to
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dest));

			FileInputStream fis = new FileInputStream(src);
			// create a new zip entry
			ZipEntry anEntry = new ZipEntry("/" + src.getName());
			// place the zip entry in the ZipOutputStream object
			zos.putNextEntry(anEntry);

			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;

			// now write the content of the file to the ZipOutputStream
			while ((bytesIn = fis.read(readBuffer)) != -1) {
				zos.write(readBuffer, 0, bytesIn);
			}
			// close the Stream
			fis.close();
			// close the stream
			zos.flush();
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
			logError(e.getMessage());
		}
	}

	/**
	 * Zips a folder into a .zip archive
	 */
	public static void zipFolder(File inFolder, File outFile) {
		try {
			// create a ZipOutputStream to zip the data to
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outFile));
			// assuming that there is a directory named inFolder (If there
			// isn't create one) in the same directory as the one the code
			// runs from,
			// call the zipDir method
			zipDir(inFolder, zos, inFolder);
			// close the stream
			zos.flush();
			zos.close();
		} catch (Exception e) {
			e.printStackTrace();
			logError(e.getMessage());
		}
	}
}