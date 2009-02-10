package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
	 * @param language
	 * @return
	 * @throws Exception
	 */
	public synchronized void addFile(File file, com.logicaldoc.core.document.Document document, String content,
			String language) throws Exception {
		LuceneDocument lDoc = new LuceneDocument(document);
		try {
			log.info("addFile: " + file.toString());
			Document doc = lDoc.getDocument(file, content);
			log.info("doc path: " + doc.getField(LuceneDocument.FIELD_PATH).stringValue());
			addDocument(doc, language);
		} catch (Throwable e) {
			log.error("Exception addFile: " + e.getLocalizedMessage(), e);
			throw new Exception(e.getMessage(), e);
		}
	}

	/**
	 * Adds a LuceneDocument to the index.
	 */
	public void addDocument(Document doc, String iso639_2) {
		String indexdir = settingsConfig.getValue("indexdir");
		Language language = LanguageManager.getInstance().getLanguage(iso639_2);
		Analyzer analyzer = LuceneAnalyzerFactory.getAnalyzer(language.getLanguage());
		IndexWriter writer = null;
		try {
			File indexPath = new File(indexdir, language.getIndex());
			writer = new IndexWriter(indexPath, analyzer, false);
			writer.setSimilarity(new SquareSimilarity());
			writer.addDocument(doc);
		} catch (Exception e) {
			log.error("Exception adding Document to Lucene index: " + indexdir + ", " + e.getMessage(), e);
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
		Locale locale = new Locale(doc.getLanguage());
		Parser parser = ParserFactory.getParser(file, locale, doc.getFileExtension());
		if (parser == null) {
			return;
		}

		String content = parser.getContent();

		String language = doc.getLanguage();
		if (StringUtils.isEmpty(language)) {
			language = "en";
		}

		if (log.isInfoEnabled()) {
			log.info("addFile " + doc.getId() + " " + doc.getTitle() + " " + doc.getFileVersion() + " "
					+ doc.getPublisher() + " " + doc.getStatus() + " " + doc.getSource() + " " + doc.getSourceAuthor());
		}
		addFile(file, doc, content, language);
	}

	/**
	 * Launch optimization on a single Lucene Index identified by the language
	 */
	protected synchronized void optimize(Language language) {
		String indexdir = settingsConfig.getValue("indexdir");
		try {
			Analyzer analyzer = LuceneAnalyzerFactory.getAnalyzer(language.getLanguage());
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
				Analyzer analyzer = LuceneAnalyzerFactory.getAnalyzer(language.getLanguage());
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
	 * Deletes the entries of a document in the index of the search engine then
	 * launch optimization on the language specific index
	 * 
	 * @param docId - DocID of the document.
	 * @param language - Language of the document.
	 */
	public synchronized void deleteDocument(String docId, String iso639_2) {
		String indexdir = settingsConfig.getValue("indexdir");
		Language language = LanguageManager.getInstance().getLanguage(iso639_2);
		File indexPath = new File(indexdir, language.getIndex());
		try {
			IndexReader reader = IndexReader.open(indexPath);
			reader.deleteDocuments(new Term(LuceneDocument.FIELD_DOC_ID, docId));
			reader.close();
		} catch (IOException ioe) {
			log.error("deleteDocument " + ioe.getMessage(), ioe);
		}
	}

	public Document getDocument(String docId, String iso639_2) {
		String indexdir = settingsConfig.getValue("indexdir");
		Language language = LanguageManager.getInstance().getLanguage(iso639_2);
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
		String indexdir = settingsConfig.getValue("indexdir");
		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				File indexPath = new File(indexdir, language.getIndex());

				FSDirectory fsindexdir = FSDirectory.getDirectory(indexPath);
				IndexReader ir = IndexReader.open(fsindexdir);
				IndexReader.unlock(fsindexdir);
				ir.close();
			}
		} catch (Exception e) {
			log.error("unlock " + e.getMessage(), e);
		}
	}

	/**
	 * Check if at least one index is locked
	 * 
	 * @return true if one or more indexes are locked
	 */
	public boolean isLocked() {
		boolean result = false;
		String indexdir = settingsConfig.getValue("indexdir");

		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				File indexPath = new File(indexdir, language.getIndex());
				FSDirectory fsindexdir = FSDirectory.getDirectory(indexPath);
				IndexReader ir = null;
				try {
					ir = IndexReader.open(fsindexdir);
					if (IndexReader.isLocked(fsindexdir)) {
						result = true;
						break;
					}
				} finally {
					if (ir != null)
						ir.close();
				}
			}
		} catch (Exception e) {
			log.error("isLocked " + e.getMessage(), e);
		}

		return result;
	}

	/**
	 * Returns the number of indexed documents in all indexes. Used for
	 * statistical output.
	 */
	public int getCount() {
		int count = 0;
		String indexdir = settingsConfig.getValue("indexdir");

		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				File indexPath = new File(indexdir, language.getIndex());
				IndexReader ir = IndexReader.open(indexPath);
				count += ir.numDocs();
				ir.close();
			}
		} catch (Exception e) {
			log.error("getCount " + e.getMessage(), e);
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
		String indexdir = settingsConfig.getValue("indexdir");

		try {
			// Get languages from LanguageManager
			Collection<Language> languages = LanguageManager.getInstance().getLanguages();
			for (Language language : languages) {
				File indexPath = new File(indexdir, language.getIndex());
				FileUtils.deleteDirectory(indexPath);
			}
		} catch (Exception e) {
			log.error("createIndexes " + e.getMessage(), e);
		}
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
				File indexPath = new File(indexdir, language.getIndex());
				indexPath.mkdirs();
				indexPath.mkdir();
				createIndex(indexPath, language.getLanguage());
			}
		} catch (Exception e) {
			log.error("createIndexes " + e.getMessage(), e);
		}
	}

	public static void createIndex(File indexPath, String iso639_2) throws CorruptIndexException,
			LockObtainFailedException, IOException {
		new IndexWriter(indexPath, LuceneAnalyzerFactory.getAnalyzer(iso639_2), true);
	}
}