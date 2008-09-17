package com.logicaldoc.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class manages files and directories.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public class FileBean {
	static final int BUFF_SIZE = 100000;

	static final byte[] buffer = new byte[BUFF_SIZE];

	/**
	 * This method deletes a file.
	 * 
	 * @param filename Name of the file to be deleted.
	 * @return Success of delete.
	 */
	public static boolean deleteFile(String filename) {
		File f = new File(filename);
		boolean result = false;

		try {
			result = f.delete();
		} catch (Exception ex) {
			logError(ex.getMessage());
		}

		return result;
	} // end method deleteFile

	public static boolean createDir(String dirname) {
		File f = new File(dirname);
		boolean result = false;

		try {
			result = f.mkdirs();
		} catch (Exception ex) {
			logError(ex.getMessage());
		}

		return result;
	} // end method createDir

	public static boolean renameFile(String filename, String newfilename) {
		File f = new File(filename);
		File nf = new File(newfilename);
		boolean result = false;

		try {
			result = f.renameTo(nf);
		} catch (Exception ex) {
			logError(ex.getMessage());
		}

		return result;
	} // end method renameFile

	public static boolean copyFile(String filename, String newfilename) {
		boolean result = false;
		InputStream fis = null;
		OutputStream fos = null;

		try {
			File out = new File(newfilename);
			fis = new FileInputStream(filename);
			fos = new FileOutputStream(out);

			int buffer = 0;

			while ((buffer = fis.read()) != -1) {
				fos.write(buffer);
			}

			result = true;
		} catch (Exception ex) {
			logError(ex.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					;
				}
			}

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ioe) {
					;
				}
			}
		}

		return result;
	} // end method copyFile

	public static boolean copyDir(String dirname, String newdir) {
		boolean result = false;

		try {
			createDir(newdir);

			File f = new File(dirname);

			if (f.isDirectory()) {
				String[] files = f.list();

				for (int i = 0; i < files.length; i++) {
					File file = new File(new StringBuilder(dirname).append(File.separator).append(files[i]).toString());

					if (file.isDirectory()) {
						copyDir(new StringBuilder(f.getAbsolutePath()).append(File.separator).append(files[i])
								.toString(), new StringBuilder(newdir).append(File.separator).append(files[i])
								.toString());
					} else {
						copyFile(new StringBuilder(f.getAbsolutePath()).append(File.separator).append(files[i])
								.toString(), new StringBuilder(newdir).append(File.separator).append(files[i])
								.toString());
					}
				}
			} else {
				copyFile(dirname, newdir);
			}

			result = true;
		} catch (Exception e) {
			logError(e.getMessage());
		}

		return result;
	} // end method copyDir

	public static boolean deleteDir(String dirname) {
		try {
			return deleteDir(new File(dirname));
		} catch (Exception ex) {
			logError(ex.getMessage());
		}

		return false;
	} // end method deleteDir

	public static boolean deleteDir(File file) {
		boolean result = true;

		try {
			if (!file.exists()) {
				return true;
			}

			if (file.isDirectory()) {
				File[] files = file.listFiles();

				for (int i = 0; i < files.length; i++) {
					result = deleteDir(files[i]);
				}

				result = file.delete();
			} else {
				result = file.delete();
			}
		} catch (Exception ex) {
			logError(ex.getMessage());
			result = false;
		}

		return result;
	} // end method deleteDir

	public static boolean exists(String filename) {
		try {
			File file = new File(filename);
			return file.exists();
		} catch (Exception e) {
			logError(e.getMessage());
			return false;
		}
	} // end method exists

	public static boolean exists(URI filename) {
		try {
			File file = new File(filename);
			return file.exists();
		} catch (Exception e) {
			logError(e.getMessage());
			return false;
		}
	} // end method exists

	public static long getSize(String filename) {
		File f = new File(filename);

		if (f.length() > 1024) {
			return f.length();
		} else {
			return 1024;
		}
	} // end method getSize

	public static Date getLastModified(String filename) {
		File f = new File(filename);

		return new Date(f.lastModified());
	} // end method getLastModified

	public static StringBuffer readFile(String filename) {
		StringBuffer content = new StringBuffer();
		BufferedInputStream bis = null;

		try {
			File file = new File(filename);
			bis = new BufferedInputStream(new FileInputStream(file));

			int ichar = 0;

			while ((ichar = bis.read()) > 0) {
				content.append((char) ichar);
			}
		} catch (Exception ex) {
			logError(ex.getMessage());
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException ioe) {
					;
				}
			}
		}

		return content;
	} // end method readFile

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
	} // end method writeFile

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
	} // end method writeFile

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
		Log logger = LogFactory.getLog(FileBean.class);
		logger.error(message);
	}
}