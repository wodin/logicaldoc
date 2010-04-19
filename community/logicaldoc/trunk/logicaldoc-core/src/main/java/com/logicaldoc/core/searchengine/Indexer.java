package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.IOException;
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
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * Class for indexing files and maintaining indexes.
 * 
 * @author Michael Scholz, Marco Meschieri, Alessandro Gasparini
 */
public class Indexer {

	protected static Log log = LogFactory.getLog(Indexer.class);

	private SettingsConfig settingsConfig;

	private DocumentDAO documentDao;

	private Indexer() {
	}

	public void setSettingsConfig(SettingsConfig settingsConfig) {
		this.settingsConfig = settingsConfig;
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
	public synchronized void addFile(File file, com.logicaldoc.core.document.Document document, String content,
			Locale locale) throws Exception {
		LuceneDocument lDoc = new LuceneDocument(document);
		log.info("document: " + document.getId());
		try {
			log.info("addFile: " + file.toString());
			Document doc = lDoc.getDocument(file, content);
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
		//First of all, remove old entries if any
		deleteDocument(doc.getField(LuceneDocument.FIELD_DOC_ID).stringValue(), locale);
		
		//Then add the record in the index
		String indexdir = settingsConfig.getValue("indexdir");
		Language language = LanguageManager.getInstance().getLanguage(locale);
		Analyzer analyzer = language.getAnalyzer();
		IndexWriter writer = null;
		try {
			File indexPath = new File(indexdir, language.getIndex());
			writer = new IndexWriter(indexPath, analyzer, false);
			writer.setSimilarity(new SquareSimilarity());
			writer.addDocument(doc);
		} catch (Exception e) {
			log.error("Exception adding Document to Lucene index: " + indexdir + ", " + e.getMessage(), e);
			throw e;
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (Exception e) {
					log.error("Error closing index: " + language.getIndex() + ", " + e.getMessage(), e);
				}
		}
	}

	/**
	 * Adds a new document to the index
	 * 
	 * @param file The document file
	 * @param doc The document that we want to add
	 * @throws Exception
	 */
	public synchronized void addFile(File file, com.logicaldoc.core.document.Document doc) throws Exception {
		com.logicaldoc.core.document.Document document = doc;

		Locale locale = document.getLocale();
		if (locale == null)
			locale = Locale.ENGLISH;
		Parser parser = ParserFactory.getParser(file, document.getFileName(), locale, null);
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

		addFile(file, document, content, locale);
	}

	/**
	 * Launch optimization on a single Lucene Index identified by the language
	 */
	protected synchronized void optimize(Language language) {
		String indexdir = settingsConfig.getValue("indexdir");
		try {
			Analyzer analyzer = language.getAnalyzer();
			File indexPath = new File(indexdir, language.getIndex());
			IndexWriter writer = new IndexWriter(indexPath, analyzer, false);
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

		String indexdir = settingsConfig.getValue("indexdir");
		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				Analyzer analyzer = language.getAnalyzer();
				File indexPath = new File(indexdir, language.getIndex());
				IndexWriter writer = new IndexWriter(indexPath, analyzer, false);
				writer.optimize();
				writer.close();
			}
		} catch (Exception e) {
			log.error("optimize " + e.getMessage(), e);
		}
		log.warn("Finished optimization for all indexes");
	}

	/**
	 * Deletes the entries of a document in the index of the search engine.
	 * 
	 * @param docId - DocID of the document.
	 * @param locale - Locale of the document.
	 */
	public synchronized void deleteDocument(String docId, Locale locale) {
		String indexdir = settingsConfig.getValue("indexdir");
		Language language = LanguageManager.getInstance().getLanguage(locale);
		File indexPath = new File(indexdir, language.getIndex());
		try {
			IndexReader reader = IndexReader.open(indexPath);
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
		List<File> indexes = getIndexes();
		for (File index : indexes) {
			for (Long docId : docIds) {
				IndexReader reader = null;
				try {
					reader = IndexReader.open(index);
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
		String indexdir = settingsConfig.getValue("indexdir");
		Language language = LanguageManager.getInstance().getLanguage(locale);
		File indexPath = new File(indexdir, language.getIndex());
		try {
			IndexReader reader = IndexReader.open(indexPath);
			Searcher searcher = new IndexSearcher(reader);

			// Compose a query for menuId
			QueryParser parser = new QueryParser(LuceneDocument.FIELD_DOC_ID, new KeywordAnalyzer());
			Query query = parser.parse(docId);
			Hits hits = searcher.search(query);

			// Iterate through the results:
			for (int i = 0; i < hits.length(); i++) {
				Document hitDoc = hits.doc(i);
				if (hitDoc.get(LuceneDocument.FIELD_DOC_ID).equals(docId)) {
					return hitDoc;
				}
			}
			searcher.search(query);
			reader.close();
		} catch (Exception e) {
			log.error("getDocument " + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * This method can unlock a locked index.
	 */
	public synchronized void unlock() {
		List<File> indexes = getIndexes();
		for (File index : indexes) {
			IndexReader ir = null;
			try {
				FSDirectory fsindexdir = FSDirectory.getDirectory(index);
				ir = IndexReader.open(fsindexdir);
				IndexReader.unlock(fsindexdir);
			} catch (Exception e) {
				log.error("getCount " + e.getMessage(), e);
			} finally {
				if (ir != null)
					try {
						ir.close();
					} catch (IOException e) {
					}
				File lockFile = new File(index, "write.lock");
				if (lockFile.exists())
					try {
						FileUtils.forceDelete(lockFile);
					} catch (IOException e) {
						log.error(e.getMessage());
					}
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

		List<File> indexes = getIndexes();
		for (File index : indexes) {
			IndexReader ir = null;
			try {
				FSDirectory fsindexdir = FSDirectory.getDirectory(index);
				ir = IndexReader.open(fsindexdir);
				if (IndexReader.isLocked(fsindexdir)) {
					result = true;
					break;
				}
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
		return result;
	}

	/**
	 * Returns the number of indexed documents in all indexes. Used for
	 * statistical output.
	 */
	public int getCount() {
		int count = 0;
		List<File> indexes = getIndexes();
		for (File index : indexes) {
			IndexReader ir = null;
			try {
				ir = IndexReader.open(index);
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
		List<File> indexes = getIndexes();
		for (File index : indexes) {
			try {
				FileUtils.deleteDirectory(index);
			} catch (Exception e) {
				log.error("dropIndexes " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Get all indexes dirs
	 */
	public List<File> getIndexes() {
		List<File> dirs = new ArrayList<File>();
		File indexdir = new File(settingsConfig.getValue("indexdir"));
		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				File indexPath = new File(indexdir, language.getIndex());
				dirs.add(indexPath);
			}
		} catch (Exception e) {
			log.error("getIndexes " + e.getMessage(), e);
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
		String indexdir = settingsConfig.getValue("indexdir");

		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				createIndex(new File(indexdir, language.getIndex()), language.getLocale());
			}
		} catch (Exception e) {
			log.error("createIndexes " + e.getMessage(), e);
		}
	}

	public static void createIndex(File indexPath, Locale locale) throws CorruptIndexException,
			LockObtainFailedException, IOException {
		if (!indexPath.exists()) {
			indexPath.mkdirs();
			indexPath.mkdir();
			new IndexWriter(indexPath, LanguageManager.getInstance().getLanguage(locale).getAnalyzer(), true);
		}
	}

	public void setDocumentDao(DocumentDAO documentDao) {
		this.documentDao = documentDao;
	}
}