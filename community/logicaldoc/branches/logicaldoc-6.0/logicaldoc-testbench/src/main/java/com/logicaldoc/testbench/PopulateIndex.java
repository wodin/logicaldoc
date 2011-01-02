package com.logicaldoc.testbench;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

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
public class PopulateIndex {
	public static final String FIELD_TAGS = "tags";

	public static final String FIELD_CONTENT = "content";

	public static final String FIELD_TYPE = "type";

	public static final String FIELD_FOLDERID = "folderId";

	public static final String FIELD_SOURCE_TYPE = "sourceType";

	public static final String FIELD_DATE = "date";

	public static final String FIELD_SOURCE_DATE = "sourceDate";

	public static final String FIELD_COVERAGE = "coverage";

	public static final String FIELD_SOURCE_AUTHOR = "sourceAuthor";

	public static final String FIELD_SOURCE = "source";

	public static final String FIELD_SIZE = "size";

	public static final String FIELD_TITLE = "title";

	public static final String FIELD_DOC_ID = "docId";

	public static final String FIELD_TEMPLATE_ID = "templateId";

	public static final String FIELD_CREATION = "creation";

	public static final String FIELD_CUSTOM_ID = "customId";

	protected static Log log = LogFactory.getLog(PopulateIndex.class);

	private String language;

	private File rootFolder;

	// Directory containing the temporally generated files
	private File tempFolder;

	private File indexFolder;

	private int count = 0;

	private long startDocId = 10000;

	private int ramBuffer = 16;

	private IndexWriter writer;

	public PopulateIndex() {
		try {
			Properties conf = new Properties();
			conf.load(this.getClass().getResourceAsStream("/conf.properties"));
			this.rootFolder = new File(conf.getProperty("files.rootFolder"));
			this.indexFolder = new File(conf.getProperty("index.indexFolder"));
			this.tempFolder = new File(conf.getProperty("index.tempFolder"));
			this.language = conf.getProperty("database.language");
			this.ramBuffer = Integer.parseInt(conf.getProperty("index.ramBuffer"));
			this.startDocId = Long.parseLong(conf.getProperty("files.startDocId"));
		} catch (IOException e) {
		}
	}

	public File getIndexFolder() {
		return indexFolder;
	}

	public void setIndexFolder(File indexFolder) {
		this.indexFolder = indexFolder;
	}

	public long getStartDocId() {
		return startDocId;
	}

	public void setStartDocId(long startDocId) {
		this.startDocId = startDocId;
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
	public void populate() {
		log.fatal("Start of index population");
		count = 0;
		Locale locale = new Locale(language);
		Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_30, locale.getDisplayName(Locale.ENGLISH));

		try {
			writer = new IndexWriter(new NIOFSDirectory(indexFolder), analyzer, false, MaxFieldLength.UNLIMITED);
			writer.setRAMBufferSizeMB(ramBuffer);
			addDocuments(tempFolder, "/");
			writer.optimize();

			// Now we can delete the temporary folder.
			try {
				FileUtils.forceDelete(tempFolder);
			} catch (Throwable e) {
				e.printStackTrace();
			}

		} catch (Throwable e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (Exception e) {
					log.error(e);
				}
		}

		log.fatal("End of index population");
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
					if ((count % 100 == 0) && docId > 0) {
						log.info("Added index document " + docId);
					}

				} catch (Throwable e) {
					e.printStackTrace();
					log.error(e);
				}
			}
		}
	}

	private long insertDocument(File dir, String path) throws CorruptIndexException, IOException {
		File docFile = dir.listFiles()[0];
		String filename = docFile.getName();
		String title = filename.substring(0, filename.lastIndexOf("."));
		String type = filename.substring(filename.lastIndexOf(".") + 1);
		long filesize = docFile.length();
		long id = Long.parseLong(dir.getName().substring(dir.getName().lastIndexOf("_") + 1));

		// Skip condition
		if (id < startDocId)
			return -1;

		Document doc = new Document();
		doc.add(new Field(FIELD_DOC_ID, String.valueOf(id), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(FIELD_TITLE, title, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(FIELD_SIZE, String.valueOf(filesize), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(FIELD_SOURCE, "LogicalDOC", Field.Store.NO, Field.Index.ANALYZED));
		doc.add(new Field(FIELD_SOURCE_AUTHOR, "admin", Field.Store.NO, Field.Index.ANALYZED));
		doc.add(new Field(FIELD_SOURCE_TYPE, "type", Field.Store.NO, Field.Index.ANALYZED));
		doc.add(new Field(FIELD_COVERAGE, "test", Field.Store.NO, Field.Index.ANALYZED));
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		doc.add(new Field(FIELD_SOURCE_DATE, df.format(docFile.lastModified()), Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		doc.add(new Field(FIELD_DATE, df.format(docFile.lastModified()), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(FIELD_TYPE, type, Field.Store.YES, Field.Index.NOT_ANALYZED));

		File xx = new File(path);
		doc.add(new Field(FIELD_FOLDERID, xx.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));

		String content = Util.parse(docFile);
		doc.add(new Field(FIELD_CONTENT, content, Field.Store.YES, Field.Index.ANALYZED));

		doc.add(new Field(FIELD_TAGS, Util.extractWordsAsString(5, content), Field.Store.YES, Field.Index.ANALYZED));

		doc.add(new Field(FIELD_CREATION, df.format(new Date().getTime()), Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field(FIELD_CUSTOM_ID, String.valueOf(id), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field(FIELD_TEMPLATE_ID, " ", Field.Store.YES, Field.Index.NOT_ANALYZED));

		writer.addDocument(doc);

		count++;
		return id;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getRamBuffer() {
		return ramBuffer;
	}

	public void setRamBuffer(int ramBuffer) {
		this.ramBuffer = ramBuffer;
	}

	public File getTempFolder() {
		return tempFolder;
	}

	public void setTempFolder(File tempFolder) {
		this.tempFolder = tempFolder;
	}
}