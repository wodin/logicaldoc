package com.logicaldoc.testbench;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;

/**
 * Generates database records browsing an existing filesystem in LogicalDOC's
 * format, and accessing an existing LogicalDOC DB.
 * <p>
 * <b>NOTE:</b> The file system must be compliant with the one used by
 * LogicalDOC to store document archive files, so folders must be named with
 * internal menu id.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class PrepareFileSystem {
	protected static Log log = LogFactory.getLog(PrepareFileSystem.class);

	private File rootFolder;

	// Directory containing the temporally generated files
	private File tempFolder;

	public PrepareFileSystem() {
		try {
			Properties conf = new Properties();
			conf.load(this.getClass().getResourceAsStream("/conf.properties"));
			this.rootFolder = new File(conf.getProperty("files.rootFolder"));
			this.tempFolder = new File(conf.getProperty("index.tempFolder"));
		} catch (IOException e) {
		}
	}

	public File getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(File rootFolder) {
		this.rootFolder = rootFolder;
	}

	/**
	 * Populates the full-text index
	 */
	public void prepare() {
		log.fatal("Start of filesystem prepare");

		try {
			addDocuments(tempFolder, "/");
			// Now we can delete the temporary folder.
			try {
				FileUtils.forceDelete(tempFolder.getParentFile());
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			log.error(e);
		}

		log.fatal("End of filesystem prepare");
	}

	/**
	 * Adds all documents inside the specified dir
	 * 
	 * @param dir The directory to browse
	 * @param path Path for 'path' field
	 * @throws SQLException
	 */
	private void addDocuments(File dir, String path) {
		long parentFolderId = Long.parseLong(dir.getName());
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && !files[i].getName().startsWith("doc_")) {
				// Recursive invocation
				addDocuments(files[i], path + "/" + parentFolderId);
			} else if (files[i].isDirectory() && files[i].getName().startsWith("doc_")) {
				try {
					long docId = insertDocument(files[i], (path + "/" + files[i].getParentFile()).replaceAll("//", "/"));
				} catch (Throwable e) {
					e.printStackTrace();
					log.error(e);
				}
			}
		}
	}

	private long insertDocument(File dir, String path) throws CorruptIndexException, IOException {
		File docFile = dir.listFiles()[0];
		long id = Long.parseLong(dir.getName().substring(dir.getName().lastIndexOf("_") + 1));

		File correctDir = getDirectory(id);
		correctDir.mkdir();
		correctDir.mkdirs();
		docFile.renameTo(new File(correctDir, "1.0"));
		if (docFile.exists())
			docFile.delete();
		return id;
	}

	/**
	 * Determines the folder where the document's file will be stored. This
	 * method is similar to the "getDirectory" method of the FSStorer class.
	 * 
	 * @param docId The document id.
	 * @return The directory in which will be inserted the doc.
	 */
	public File getDirectory(long docId) {
		String path = split(Long.toString(docId), '/', 3);
		path = rootFolder + "/" + path + "/doc";
		return new File(path);
	}

	/**
	 * Splits a string into tokens separated by a separator
	 * 
	 * @param src The source string
	 * @param separator The separator character
	 * @param tokenSize Size or each token
	 * @return
	 */
	public static String split(String src, char separator, int tokenSize) {
		StringBuffer sb = new StringBuffer();
		String[] tokens = split(src, tokenSize);
		for (int i = 0; i < tokens.length; i++) {
			if (sb.length() > 0)
				sb.append(separator);
			sb.append(tokens[i]);
		}
		return sb.toString();
	}

	/**
	 * Splits a string into an array of tokens
	 * 
	 * @param src The source string
	 * @param tokenSize size of each token
	 * @return
	 */
	public static String[] split(String src, int tokenSize) {
		ArrayList<String> buf = new ArrayList<String>();
		for (int i = 0; i < src.length(); i += tokenSize) {
			int j = i + tokenSize;
			if (j > src.length())
				j = src.length();
			buf.add(src.substring(i, j));
		}
		return buf.toArray(new String[] {});
	}

	public File getTempFolder() {
		return tempFolder;
	}

	public void setTempFolder(File tempFolder) {
		this.tempFolder = tempFolder;
	}
}