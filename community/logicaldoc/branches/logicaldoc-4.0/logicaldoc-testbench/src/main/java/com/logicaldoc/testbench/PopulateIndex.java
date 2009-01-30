package com.logicaldoc.testbench;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;

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
	public static final String FIELD_KEYWORDS = "keywords";

	public static final String FIELD_CONTENT = "content";

	public static final String FIELD_TYPE = "type";

	public static final String FIELD_PATH = "path";

	public static final String FIELD_SOURCE_TYPE = "sourceType";

	public static final String FIELD_DATE = "date";

	public static final String FIELD_SOURCE_DATE = "sourceDate";

	public static final String FIELD_COVERAGE = "coverage";

	public static final String FIELD_SOURCE_AUTHOR = "sourceAuthor";

	public static final String FIELD_SOURCE = "source";

	public static final String FIELD_SIZE = "size";

	public static final String FIELD_TITLE = "title";

	public static final String FIELD_DOC_ID = "docId";

	protected static Log log = LogFactory.getLog(PopulateIndex.class);

	private String language;

	private File rootFolder;

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
		Analyzer analyzer = new SnowballAnalyzer(locale.getDisplayName(Locale.ENGLISH), Util.getStopwordsMap().get(
				language));

		try {
			writer = new IndexWriter(indexFolder, analyzer, false);
			writer.setRAMBufferSizeMB(ramBuffer);
			addDocuments(rootFolder, "/");
			writer.optimize();
		} catch (Throwable e) {
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
					long docId = insertDocument(files[i], path.replaceAll("//", "/"));
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
		doc.add(new Field(FIELD_DOC_ID, String.valueOf(id), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_TITLE, title, Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SIZE, String.valueOf(filesize), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_SOURCE, "LogicalDOC", Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SOURCE_AUTHOR, "admin", Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_SOURCE_TYPE, "type", Field.Store.NO, Field.Index.TOKENIZED));
		doc.add(new Field(FIELD_COVERAGE, "test", Field.Store.NO, Field.Index.TOKENIZED));
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		doc.add(new Field(FIELD_SOURCE_DATE, df.format(docFile.lastModified()), Field.Store.YES,
				Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_DATE, df.format(docFile.lastModified()), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_TYPE, type, Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field(FIELD_PATH, path, Field.Store.YES, Field.Index.UN_TOKENIZED));

		String content = Util.parse(docFile);
		doc.add(new Field(FIELD_CONTENT, content, Field.Store.YES, Field.Index.TOKENIZED));

		doc.add(new Field(FIELD_KEYWORDS, Util.extractWordsAsString(5, content), Field.Store.YES,
				Field.Index.TOKENIZED));

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
}