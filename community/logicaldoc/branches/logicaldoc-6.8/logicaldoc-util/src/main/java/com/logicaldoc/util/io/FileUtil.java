package com.logicaldoc.util.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages I/O operations with files.
 * 
 * @author Marco Meschieri - Logical Objects
 * @version 4.0
 */
public class FileUtil {
	static final int BUFF_SIZE = 100000;

	static final byte[] buffer = new byte[BUFF_SIZE];

	protected static Logger log = LoggerFactory.getLogger(FileUtil.class);

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
			bos.write(text.getBytes("UTF-8"));
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

	public static String computeDigest(InputStream is) {
		String digest = "";
		MessageDigest sha = null;

		try {
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
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	/**
	 * This method calculates the digest of a file using the algorithm SHA-1.
	 * 
	 * @param file The file for which will be computed the digest
	 * @return digest
	 */
	public static String computeDigest(File file) {
		InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(file), BUFF_SIZE);
			return computeDigest(is);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	/**
	 * This method calculates the digest of a file using the algorithm SHA-1.
	 * 
	 * @param file The file for which will be computed the digest
	 * @return digest
	 */
	public static byte[] computeSha1Hash(File file) {
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file), BUFF_SIZE);
			return computeSha1Hash(is);
		} catch (IOException io) {
			log.error(io.getMessage(), io);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	/**
	 * This method calculates the digest of a inputStram content using the
	 * algorithm SHA-1.
	 * 
	 * @param file The file for which will be computed the digest
	 * @return digest
	 */
	public static byte[] computeSha1Hash(InputStream is) {
		MessageDigest sha = null;
		try {
			if (is != null) {
				sha = MessageDigest.getInstance("SHA-1");
				byte[] message = new byte[BUFF_SIZE];
				int len = 0;
				while ((len = is.read(message)) != -1) {
					sha.update(message, 0, len);
				}
				byte[] messageDigest = sha.digest();
				return messageDigest;
			}
		} catch (IOException io) {
			log.error("Error generating SHA-1: ", io);
		} catch (Throwable t) {
			log.error("Error generating SHA-1: ", t);
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

	/**
	 * Computes the folder size as the sum of all files directly and indirectly
	 * contained.
	 */
	public static long getFolderSize(File folder) {
		long foldersize = 0;

		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				foldersize += getFolderSize(files[i]);
			} else {
				foldersize += files[i].length();
			}
		}
		return foldersize;
	}

	/**
	 * Renders a file size in a more readable behaviour taking into account the
	 * user locale. Depending on the size, the result will be presented in the
	 * following measure units: GB, MB, KB or Bytes
	 * 
	 * @param size Size to be rendered
	 * @param language The language for the format symbols
	 * @return
	 */
	public static String getDisplaySize(long size, String language) {
		String displaySize = "";
		Locale locale = new Locale("en");
		if (StringUtils.isNotEmpty(language))
			locale = new Locale(language);
		NumberFormat nf = new DecimalFormat("###,###,###.0", new DecimalFormatSymbols(locale));
		if (size > 1000000000) {
			displaySize = nf.format((double) size / 1024 / 1024 / 1024) + " GB";
		} else if (size > 1000000) {
			displaySize = nf.format((double) size / 1024 / 1024) + " MB";
		} else if (size > 1000) {
			displaySize = nf.format((double) size / 1024) + " KB";
		} else {
			displaySize = size + " Bytes";
		}
		return displaySize;
	}

	/**
	 * Renders a file size in a more readable behaviour taking into account the
	 * user locale. The size is always rendered in the KB(kilobyte) measure
	 * unit.
	 * 
	 * @param size Size to be rendered
	 * @param language The language for the format symbols
	 * @return
	 */
	public static String getDisplaySizeKB(long size, String language) {
		String displaySize = "";
		Locale locale = new Locale("en");
		if (StringUtils.isNotEmpty(language))
			locale = new Locale(language);
		NumberFormat nf = new DecimalFormat("###,###,##0.0", new DecimalFormatSymbols(locale));
		displaySize = nf.format((double) size / 1024) + " KB";
		return displaySize;
	}

	/**
	 * Check if a given filename matches the <code>includes</code> and not the
	 * <code>excludes</code>
	 * 
	 * @param filename The filename to consider
	 * @param includes list of includes expressions (eg. *.doc,*dummy*)
	 * @param excludes list of excludeses expressions (eg. *.doc,*dummy*)
	 * @return true only if the passed filename matches the includes and not the
	 *         excludes
	 */
	public static boolean matches(String filename, String[] includes, String[] excludes) {
		// First of all check if the filename must be excluded
		if (excludes != null)
			for (String s : excludes)
				if (SelectorUtils.match(s, filename, false))
					return false;

		// Then check if the filename must can be included
		if (includes != null)
			for (String s : includes)
				if (SelectorUtils.match(s, filename, false))
					return true;

		if (includes == null || includes.length == 0)
			return true;
		else
			return false;
	}

	/**
	 * Check if a given filename matches the <code>includes</code> and not the
	 * <code>excludes</code>
	 * 
	 * @param filename The filename to consider
	 * @param includes comma-separated list of includes expressions (eg.
	 *        *.doc,*dummy*)
	 * @param excludes comma-separated list of excludeses expressions (eg.
	 *        *.doc,*dummy*)
	 * @return true only if the passed filename matches the includes and not the
	 *         excludes
	 */
	public static boolean matches(String filename, String includes, String excludes) {
		List<String> inc = new ArrayList<String>();
		List<String> exc = new ArrayList<String>();

		StringTokenizer st;

		if (StringUtils.isNotEmpty(excludes)) {
			st = new StringTokenizer(excludes, ",", false);
			while (st.hasMoreTokens())
				exc.add(st.nextToken().trim());
		}

		if (StringUtils.isNotEmpty(includes)) {
			st = new StringTokenizer(includes, ",", false);
			while (st.hasMoreTokens())
				inc.add(st.nextToken().trim());
		}

		return matches(filename, inc.toArray(new String[0]), exc.toArray(new String[0]));
	}

	public static void writeUTF8(String content, File file, boolean append) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "UTF8"));
			out.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {

				}
		}
	}

	public static byte[] toByteArray(File file) {
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file), 2048);
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
		return null;
	}

	public static void replaceInFile(String sourcePath, String token, String newValue) throws Exception {
		boolean windows = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;

		try {
			File tmp = new File(sourcePath + ".tmp");
			File file = new File(sourcePath);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "", oldtext = "";
			while ((line = reader.readLine()) != null) {
				oldtext += line.replaceAll(token, newValue.replaceAll("\\\\", "\\\\\\\\"));
				if (windows && !sourcePath.endsWith(".sh"))
					oldtext += "\r";
				oldtext += "\n";
			}
			reader.close();

			// To replace a line in a file
			String newtext = oldtext.replaceAll(token, newValue.replaceAll("\\\\", "\\\\\\\\"));

			FileWriter writer = new FileWriter(tmp);
			writer.write(newtext);
			writer.close();

			file.delete();
			tmp.renameTo(file);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;

		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}
}