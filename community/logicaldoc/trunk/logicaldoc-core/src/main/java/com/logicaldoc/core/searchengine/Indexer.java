package com.logicaldoc.core.searchengine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CheckIndex;
import org.apache.lucene.index.CheckIndex.Status;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Class for indexing files and maintaining indexes.
 * 
 * @author Michael Scholz, Marco Meschieri, Alessandro Gasparini
 */
public class Indexer {

	private static final String WRITE_LOCK = "write.lock";

	public static final Version LUCENE_VERSION = Version.LUCENE_31;

	protected static Log log = LogFactory.getLog(Indexer.class);

	private static ContextProperties config;

	private DocumentDAO documentDao;

	private Indexer() {
	}

	public void setConfig(ContextProperties config) {
		Indexer.config = config;
	}

	/**
	 * Adds a new File into the archive
	 * 
	 * @param file
	 * @param document
	 * @param content
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	public synchronized void addFile(com.logicaldoc.core.document.Document document, String content, Locale locale)
			throws Exception {
		LuceneDocument lDoc = new LuceneDocument(document);
		log.info("document: " + document.getId());
		try {
			log.info("addFile: " + document.getFileName());
			Document doc = lDoc.getDocument(content);
			log.info("doc path: " + doc.getField(LuceneDocument.FIELD_FOLDER_ID).stringValue());
			addDocument(doc, locale);
		} catch (Throwable e) {
			log.error("Exception addFile: " + e.getLocalizedMessage(), e);
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * Adds a LuceneDocument to the index.
	 * 
	 * @throws Exception
	 */
	public void addDocument(Document doc, Locale locale) throws Exception {
		// First of all, remove old entries if any
		deleteDocument(doc.getField(LuceneDocument.FIELD_DOC_ID).stringValue(), locale);

		// Then add the record in the index
		String indexdir = config.getPropertyWithSubstitutions("conf.indexdir");
		Language language = LanguageManager.getInstance().getLanguage(locale);
		Analyzer analyzer = getAnalyzer(language);
		IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(getIndexDirectory(language), config);
			writer.addDocument(doc);
		} catch (Exception e) {
			log.error("Exception adding Document to Lucene index: " + indexdir + ", " + e.getMessage(), e);
			throw e;
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (Exception e) {
					log.error("Error closing index: " + language + ", " + e.getMessage(), e);
				}
		}
	}

	/**
	 * Creates an analyzer able to separate sub-words. This analyzer cannot be
	 * used during search because it will slow the search. But in indexing it is
	 * useful to tokenize sub-words.
	 */
	private static Analyzer getAnalyzer(Language language) {
		return new WordDelimiterAnalyzer(language.getAnalyzer());
	}

	/**
	 * Adds a new document to the index
	 * 
	 * @param input Stream of the document's file
	 * @param doc The document that we want to add
	 * @throws Exception
	 */
	public synchronized void addFile(InputStream input, com.logicaldoc.core.document.Document doc) throws Exception {
		com.logicaldoc.core.document.Document document = doc;

		Locale locale = document.getLocale();
		if (locale == null)
			locale = Locale.ENGLISH;
		Parser parser = ParserFactory.getParser(input, document.getFileName(), locale, null);
		if (parser == null) {
			return;
		}

		String content = parser.getContent();

		if (document.getDocRef() != null) {
			// This is a shortcut
			document = documentDao.findById(document.getDocRef());
			documentDao.initialize(document);
			document = (com.logicaldoc.core.document.Document) document.clone();
			document.setId(doc.getId());
			document.setDocRef(doc.getDocRef());
		}

		if (log.isInfoEnabled()) {
			log.info("addFile " + document.getId() + " " + document.getTitle() + " " + document.getFileVersion() + " "
					+ document.getPublisher() + " " + document.getStatus() + " " + document.getSource() + " "
					+ document.getSourceAuthor());
		}

		addFile(document, content, locale);
	}

	/**
	 * Launch optimization on a single Lucene Index identified by the language
	 */
	protected synchronized void optimize(Language language) {
		try {
			Analyzer analyzer = getAnalyzer(language);
			IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
			IndexWriter writer = new IndexWriter(getIndexDirectory(language), config);
			writer.optimize();
			writer.close();
		} catch (Exception e) {
			log.error("optimize " + e.getMessage(), e);
		}
	}

	/**
	 * Launch optimization on all the Lucene Indexes
	 */
	public synchronized void optimize() {
		log.warn("Started optimization for all indexes");

		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getActiveLanguages();
			for (Language language : languages) {
				Analyzer analyzer = getAnalyzer(language);
				IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
				IndexWriter writer = new IndexWriter(getIndexDirectory(language), config);
				writer.optimize();
				writer.close();
			}
		} catch (Exception e) {
			log.error("optimize " + e.getMessage(), e);
		}
		log.warn("Finished optimization for all indexes");
	}

	/**
	 * Checks the consistency of all activated indexes
	 */
	public String check() {
		log.warn("Start Checking indexes");
		StringBuffer buf = new StringBuffer();
		for (Language lang : LanguageManager.getInstance().getActiveLanguages()) {
			buf.append("----------- Check index " + lang + " --------------\n");
			buf.append(check(lang));
			buf.append("----------- End Check index " + lang + " ----------\n");
		}
		log.warn("Finished indexes check");
		return buf.toString();
	}

	/**
	 * Launch the check on a single Lucene Index identified by the language
	 */
	protected String check(Language language) {
		log.warn("Checking index " + language);

		ByteArrayOutputStream baos = null;
		String statMsg = "";
		try {
			CheckIndex ci = new CheckIndex(getIndexDirectory(language));

			// Prepare the output buffer
			baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);

			// Retrieve the status collecting all informations in a string
			Status status = null;
			ci.setInfoStream(ps);
			try {
				status = ci.checkIndex();
			} catch (Exception e) {
				ps.println("ERROR: caught exception, giving up.\n\n");
				log.error(e.getMessage());
			}

			// Elaborate the status showing needed informations
			if (status != null) {
				if (status.clean) {
					statMsg = "OK\n";
				} else if (status.toolOutOfDate) {
					statMsg = "ERROR: Can't check - tool out-of-date\n";
				} else {
					statMsg = "BAD: ";
					if (status.cantOpenSegments) {
						statMsg += "cantOpenSegments ";
					}
					if (status.missingSegments) {
						statMsg += "missingSegments ";
					}
					if (status.missingSegmentVersion) {
						statMsg += "missingSegVersion ";
					}
					if (status.numBadSegments > 0) {
						statMsg += "numBadSegments=" + status.numBadSegments + " ";
					}
					if (status.totLoseDocCount > 0) {
						statMsg += "lostDocCount=" + status.totLoseDocCount + " ";
					}
				}

				String content = "";
				try {
					content = baos.toString("UTF-8");
				} catch (UnsupportedEncodingException e) {
				}

				statMsg += "\n" + content;
			}
		} catch (Throwable t) {
			log.error(t.getMessage());
		} finally {
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {

				}
		}
		log.warn("Finished checking index " + language);
		return statMsg;
	}

	/**
	 * Deletes the entries of a document in the index of the search engine.
	 * 
	 * @param docId - DocID of the document.
	 * @param locale - Locale of the document.
	 */
	public synchronized void deleteDocument(String docId, Locale locale) {
		Language language = LanguageManager.getInstance().getLanguage(locale);
		try {
			IndexReader reader = IndexReader.open(getIndexDirectory(language), false);
			reader.deleteDocuments(new Term(LuceneDocument.FIELD_DOC_ID, docId));
			reader.close();
		} catch (IOException ioe) {
			log.error("deleteDocument " + ioe.getMessage(), ioe);
		}
	}

	/**
	 * Removed all passed documents from the index
	 * 
	 * @param docIds Collection of document identifiers
	 */
	public void deleteDocuments(Collection<Long> docIds) {
		List<Directory> indexes = getIndexDirectories();
		for (Directory index : indexes) {
			for (Long docId : docIds) {
				IndexReader reader = null;
				try {
					reader = IndexReader.open(index, false);
					reader.deleteDocuments(new Term(LuceneDocument.FIELD_DOC_ID, Long.toString(docId)));
				} catch (IOException ioe) {
					log.error("deleteDocument " + ioe.getMessage(), ioe);
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
						}
					}
				}
			}
		}
	}

	public Document getDocument(String docId, Locale locale) {
		Language language = LanguageManager.getInstance().getLanguage(locale);
		try {
			IndexReader reader = IndexReader.open(getIndexDirectory(language), true);
			IndexSearcher searcher = new IndexSearcher(reader);

			// Compose a query for docId
			QueryParser parser = new QueryParser(LUCENE_VERSION, LuceneDocument.FIELD_DOC_ID, new KeywordAnalyzer());
			Query query = parser.parse(docId);
			TopDocs hits = searcher.search(query, 1);
			if (hits.totalHits > 0)
				return searcher.doc(hits.scoreDocs[0].doc);

			reader.close();
		} catch (Exception e) {
			log.error("getDocument " + e.getMessage(), e);
		}
		return null;
	}

	public Document getDocument(String docId) {
		for (Language lang : LanguageManager.getInstance().getActiveLanguages()) {
			Document doc = getDocument(docId, lang.getLocale());
			if (doc != null)
				return doc;
		}
		return null;
	}

	/**
	 * This method can unlock a locked index.
	 */
	public synchronized void unlock() {
		List<Directory> indexes = getIndexDirectories();
		for (Directory index : indexes) {
			try {
				IndexWriter.unlock(index);
			} catch (Exception e) {
				log.error("unlock " + e.getMessage(), e);
			} finally {
				try {
					index.clearLock(WRITE_LOCK);
					if (index.fileExists(WRITE_LOCK))
						index.deleteFile(WRITE_LOCK);
				} catch (IOException e) {
					log.error("Unable to delete lock file write.lock", e);
				}
			}
		}

		// For further security try to delete lock files
		List<File> dirs = getIndexFolders();
		for (File file : dirs) {
			try {
				File lockFile = new File(file, WRITE_LOCK);
				if (lockFile.exists())
					FileUtils.forceDelete(lockFile);
			} catch (Exception e) {
				log.error("unlock " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Check if at least one index is locked
	 * 
	 * @return true if one or more indexes are locked
	 */
	public boolean isLocked() {
		boolean result = false;

		List<Directory> indexes = getIndexDirectories();
		for (Directory index : indexes) {
			try {
				if (IndexWriter.isLocked(index)) {
					result = true;
					break;
				}
			} catch (Exception e) {
				log.error("getCount " + e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * Returns the number of indexed documents in all indexes. Used for
	 * statistical output.
	 */
	public int getCount() {
		int count = 0;
		List<Directory> indexes = getIndexDirectories();
		for (Directory index : indexes) {
			IndexReader ir = null;
			try {
				ir = IndexReader.open(index, true);
				count += ir.numDocs();
				ir.close();
			} catch (Exception e) {
				log.error("getCount " + e.getMessage(), e);
			} finally {
				if (ir != null)
					try {
						ir.close();
					} catch (IOException e) {
					}
			}
		}
		return count;
	}

	/**
	 * Recreates all indexes(same as invoking dropIndexes and createIndexes)
	 */
	public void recreateIndexes() {
		dropIndexes();
		createIndexes();
	}

	/**
	 * Drops all indexes (one per language)
	 */
	public void dropIndexes() {
		List<File> indexes = getIndexFolders();
		for (File index : indexes) {
			try {
				FileUtils.deleteDirectory(index);
			} catch (Exception e) {
				log.error("dropIndexes " + e.getMessage(), e);
			}
		}
	}

	static Directory getIndexDirectory(String name) throws IOException {
		return new NIOFSDirectory(getIndexFolder(name));
	}

	static Directory getIndexDirectory(Language language) throws IOException {
		return getIndexDirectory(language.toString());
	}

	static File getIndexFolder(String name) throws IOException {
		File indexdir = new File(config.getPropertyWithSubstitutions("conf.indexdir"));
		return new File(indexdir, name);
	}

	/**
	 * Get all indexes folders
	 */
	public List<File> getIndexFolders() {
		List<File> dirs = new ArrayList<File>();
		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				File indexPath = getIndexFolder(language.toString());
				dirs.add(indexPath);
			}
		} catch (Exception e) {
			log.error("getIndexFolders " + e.getMessage(), e);
		}
		return dirs;
	}

	/**
	 * Get all indexes directories
	 */
	public List<Directory> getIndexDirectories() {
		List<Directory> dirs = new ArrayList<Directory>();
		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				dirs.add(getIndexDirectory(language));
			}
		} catch (Exception e) {
			log.error("getIndexDirectories " + e.getMessage(), e);
		}
		return dirs;
	}

	/**
	 * To be called on the context startup, this method creates all indexes and
	 * unlock the existing ones
	 */
	public void init() {
		createIndexes();
		unlock();
	}

	/**
	 * Create all indexes (one per language)
	 */
	public void createIndexes() {
		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				createIndex(language);
			}
		} catch (Exception e) {
			log.error("createIndexes " + e.getMessage(), e);
		}
	}

	public static void createIndex(Language language) throws CorruptIndexException, LockObtainFailedException,
			IOException {
		File indexPath = getIndexFolder(language.toString());
		if (!indexPath.exists()) {
			indexPath.mkdirs();
			indexPath.mkdir();
			IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, getAnalyzer(language));
			config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			IndexWriter writer = null;
			try {
				writer = new IndexWriter(getIndexDirectory(language), config);
			} finally {
				try {
					writer.close();
					IndexWriter.unlock(getIndexDirectory(language));
				} catch (Exception e) {
					log.warn(e.getMessage());
				}
			}
		}
	}

	public void setDocumentDao(DocumentDAO documentDao) {
		this.documentDao = documentDao;
	}
}