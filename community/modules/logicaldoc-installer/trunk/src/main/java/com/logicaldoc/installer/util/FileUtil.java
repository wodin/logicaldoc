package com.logicaldoc.installer.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility methods related to files
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 5.1
 */
public class FileUtil {
	public static void copyFile(File in, File out) throws Exception {
		FileInputStream fis = new FileInputStream(in);
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}

	public static String readFileAsString(String filePath) throws java.io.IOException {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}

	public static boolean is64bit() {
		boolean is64bit = System.getProperty("os.arch").indexOf("64") != -1;
		return is64bit;
	}

	public static boolean isWindows() {
		boolean windows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
		return windows;
	}

	/**
	 * Writes the specified classpath resource into a file
	 * 
	 * @param resourceName Fully qualified resource name
	 * @param out The output file
	 * @throws IOException
	 */
	public static void copyResource(String resourceName, File out) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			try {
				is = new BufferedInputStream(FileUtil.class.getResource(resourceName).openStream());
			} catch (Exception e) {
				is = new BufferedInputStream(Thread.currentThread().getContextClassLoader().getResource(resourceName)
						.openStream());
			}
			os = new BufferedOutputStream(new FileOutputStream(out));

			for (;;) {
				int b = is.read();
				if (b == -1)
					break;
				os.write(b);
			}
		} finally {
			if (is != null)
				is.close();
			if (os != null)
				os.close();
		}
	}

	public static void writeToFile(InputStream is, File file) throws IOException {
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		int c;
		while ((c = is.read()) != -1) {
			out.writeByte(c);
		}
		is.close();
		out.flush();
		out.close();
	}
}
