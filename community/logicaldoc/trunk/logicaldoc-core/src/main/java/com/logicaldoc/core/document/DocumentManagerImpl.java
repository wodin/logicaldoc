package com.logicaldoc.core.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.FileBean;
import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.core.searchengine.store.Storer;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * Basic Implementation of <code>DocumentManager</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.5
 */
public class DocumentManagerImpl implements DocumentManager {
	protected static Log log = LogFactory.getLog(DocumentManagerImpl.class);

	private DocumentDAO documentDAO;

	private HistoryDAO historyDAO;

	private SettingsConfig settings;

	private Storer storer;

	private Indexer indexer;
	
	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	public void setSettings(SettingsConfig settings) {
		this.settings = settings;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	@Override
	public void checkin(long docId, File file, String filename, String username, VERSION_TYPE versionType,
			String versionDesc) throws Exception {
		FileInputStream is = new FileInputStream(file);
		try {
			checkin(docId, is, filename, username, versionType, versionDesc);
		} finally {
			is.close();
		}
	}

	@Override
	public void checkin(long docId, InputStream fileInputStream, String filename, String username,
			VERSION_TYPE versionType, String versionDesc) throws Exception {
		// identify the document and menu
		Document document = documentDAO.findByPrimaryKey(docId);
		Menu folder = document.getFolder();

		// create some strings containing paths
		String menuPath = folder.getMenuPath() + "/" + String.valueOf(docId);
		String completeDocPath = settings.getValue("docdir") + menuPath + "/";

		// rename the old current version file to the version name: "quelle.txt"
		// -> "2.0"
		if (!document.getType().equals("zip") || !document.getType().equals("jar")) {
			FileBean.renameFile(completeDocPath + document.getFileName(), completeDocPath + document.getVersion());
		}

		// extract file extension of the new file and select a file icon based
		// on the extension
		String extension = filename.substring(filename.lastIndexOf(".") + 1);
		document.setFileName(filename);

		// create new version
		Version version = createNewVersion(versionType, username, versionDesc, document.getVersion());
		String newVersion = version.getVersion();

		// set other properties of the document
		document.setDate(new Date());
		document.setPublisher(username);
		document.setStatus(Document.DOC_CHECKED_IN);
		document.setType(extension);
		document.setCheckoutUser("");
		document.setFolder(folder);
		document.addVersion(version);
		document.setVersion(newVersion);
		if (documentDAO.store(document) == false)
			throw new Exception();

		// create history entry for this checkin event
		createHistoryEntry(docId, username, History.CHECKIN);

		// create search index entry
		createIndexEntry(document, folder.getMenuId(), filename, completeDocPath);

		// store the document in the repository (on the file system)
		store(document, fileInputStream, filename, newVersion);

		log.debug("Checked in document " + docId);
	}

	@Override
	public void checkout(long docId, String username) throws Exception {
		Document document = documentDAO.findByPrimaryKey(docId);

		if (document.getStatus() == Document.DOC_CHECKED_IN) {
			document.setCheckoutUser(username);
			document.setStatus(Document.DOC_CHECKED_OUT);
			document.setFolder(document.getFolder());
			documentDAO.store(document);

			// create history entry for this checkout event
			createHistoryEntry(docId, username, History.CHECKOUT);

			log.debug("Checked out document " + docId);
		} else {
			throw new Exception("Document already checked out");
		}
	}

	@Override
	public Document create(File file, Menu parent, String username, String language) throws Exception {
		return create(file, parent, username, language, "", null, "", "", "", "", "", null);
	}

	@Override
	public Document create(InputStream content, String filename, Menu parent, String username, String language,
			String title, Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> keywords) throws Exception {
		try {
			Document doc = new Document();
			Version vers = new Version();
			doc.setFolder(parent);
			doc.setFileName(filename);
			doc.setDate(new Date());

			if (StringUtils.isNotEmpty(title)) {
				doc.setTitle(title);
			} else {
				doc.setTitle(filename.substring(0, filename.lastIndexOf(".")));
			}

			if (sourceDate != null)
				doc.setSourceDate(sourceDate);
			else
				doc.setSourceDate(doc.getDate());
			doc.setPublisher(username);
			doc.setStatus(Document.DOC_CHECKED_IN);
			doc.setType(filename.substring(filename.lastIndexOf(".") + 1));
			doc.setVersion("1.0");
			doc.setSource(source);
			doc.setSourceAuthor(sourceAuthor);
			doc.setSourceType(sourceType);
			doc.setCoverage(coverage);
			doc.setLanguage(language);
			if (keywords != null)
				doc.setKeywords(keywords);

			/* insert initial version 1.0 */
			vers.setVersion("1.0");
			vers.setComment(versionDesc);
			vers.setDate(doc.getDate());
			vers.setUser(username);

			doc.addVersion(vers);

			documentDAO.store(doc);

			// Makes menuPath
			String menupath = new StringBuilder(parent.getMenuPath()).append("/").append(parent.getMenuId()).toString();
			String path = new StringBuilder(settings.getValue("docdir")).append("/").append(menupath).append("/")
					.append(Long.toString(doc.getId())).append("/").toString();

			/* store the document */
			store(doc, content, filename, "1.0");

			createHistoryEntry(doc.getId(), username, History.STORED);

			/* create search index entry */
			String lang = doc.getLanguage();
			File file = new File(new StringBuilder(path).append("/").append(doc.getFileName()).toString());
			indexer.addFile(file, doc, getDocumentContent(doc), lang);

			doc.setFileSize(file.length());
			documentDAO.store(doc);
			return doc;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public Document create(File file, Menu parent, String username, String language, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> keywords) throws Exception {
		Locale locale = new Locale(language);
		Parser parser = ParserFactory.getParser(file, locale);
		String filename = file.getName();
		String _title = title;
		String _author = sourceAuthor;
		Set<String> _kwds = keywords;
		if (parser != null) {
			if (StringUtils.isEmpty(title)) {
				if (parser.getTitle().length() == 0)
					_title = filename.substring(0, filename.lastIndexOf("."));
				else
					_title = parser.getTitle();
			}
			if (StringUtils.isEmpty(title))
				_author = parser.getAuthor();
			String keys = parser.getKeywords();
			if (keys != null && keys.length() > 0) {
				if (keywords == null || keywords.isEmpty())
					_kwds = new HashSet<String>();
				_kwds = documentDAO.toKeywords(keys);
			}
		} else {
			if (StringUtils.isEmpty(title))
				title = filename;
		}

		InputStream is = new FileInputStream(file);
		try {
			return create(is, file.getName(), parent, username, language, _title, sourceDate, source, _author,
					sourceType, coverage, versionDesc, _kwds);
		} finally {
			is.close();
		}
	}

	private void store(Document doc, InputStream content, String filename, String version) throws IOException {
		Menu folder = doc.getFolder();

		// Makes menuPath
		String mpath = folder.getMenuPath() + "/" + String.valueOf(doc.getId());

		// Get file to upload inputStream
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);

		// stores it in folder
		storer.store(content, mpath, filename, version);
	}

	@Override
	public void delete(long docId) throws Exception {
		Document doc = documentDAO.findByPrimaryKey(docId);
		boolean result = documentDAO.delete(docId);
		if (!result)
			throw new Exception("Error deleting document");
		deleteDocument(doc);
	}

	/**
	 * Utility method for document removal from index and file system
	 * 
	 * @param doc
	 */
	private void deleteDocument(Document doc) {
		try {
			long docId = doc.getId();

			if (doc != null) {
				indexer.deleteDocument(String.valueOf(docId), doc.getLanguage());
			}

			Menu folder = doc.getFolder();
			String menupath = folder.getMenuPath() + "/" + String.valueOf(docId);

			// FileBean.deleteDir(path);
			storer.delete(menupath);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public File getDocumentFile(Document doc) {
		return getDocumentFile(doc, null);
	}

	@Override
	public File getDocumentFile(Document doc, String version) {
		Menu folder = doc.getFolder();
		String path = settings.getValue("docdir") + "/";
		path += (folder.getMenuPath() + "/" + doc.getId());

		/*
		 * Older versions of a document are stored in the same directory as the
		 * current version, but the filename is the version number without
		 * extension, e.g. "docId/2.1"
		 */
		String filename;
		if (StringUtils.isEmpty(version))
			filename = doc.getFileName();
		else
			filename = version;
		return new File(path, filename);
	}

	@Override
	public String getDocumentContent(Document doc) {
		String content = null;
		File file = getDocumentFile(doc);

		// Parses the file where it is already stored
		Locale locale = new Locale(doc.getLanguage());
		Parser parser = ParserFactory.getParser(file, locale);

		// and gets some fields
		if (parser != null) {
			content = parser.getContent();
		}
		if (content == null) {
			content = "";
		}
		return content;
	}

	@Override
	public void reindex(Document doc, String originalLanguage) throws Exception {
		/* get search index entry */
		String lang = doc.getLanguage();

		// Extract the content from the file
		String content = getDocumentContent(doc);

		// Remove the document from the index
		indexer.deleteDocument(String.valueOf(doc.getId()), originalLanguage);

		// Add the document to the index (lucene 2.x doesn't support the update
		// operation)
		File file = getDocumentFile(doc);
		indexer.addFile(file, doc, content, lang);
	}

	@Override
	public void update(Document doc, String username, String title, String source, String sourceAuthor,
			Date sourceDate, String sourceType, String coverage, String language, Set<String> keywords)
			throws Exception {
		try {
			doc.setTitle(title);
			doc.setSource(source);
			doc.setSourceAuthor(sourceAuthor);
			if (sourceDate != null)
				doc.setSourceDate(sourceDate);
			else
				doc.setSourceDate(null);
			doc.setSourceType(sourceType);
			doc.setCoverage(coverage);

			// Intercept language changes
			String oldLang = doc.getLanguage();
			doc.setLanguage(language);

			doc.clearKeywords();
			documentDAO.store(doc);
			doc.setKeywords(keywords);
			documentDAO.store(doc);

			/* create history entry */
			History history = new History();
			history.setDocId(doc.getId());
			history.setDate(new Date());
			history.setUsername(username);
			history.setEvent(History.CHANGED);
			historyDAO.store(history);

			// Launch document re-indexing
			reindex(doc, oldLang);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Creates a new version object and fills in the provided attributes
	 * 
	 * @param versionType either a new release, a new subversion or just the old
	 *        version
	 * @param username user creating the new version
	 * @param description change description
	 * @param docId version should belong to this document
	 * @param oldVersionName the previous version name
	 */
	private Version createNewVersion(Version.VERSION_TYPE versionType, String username, String description,
			String oldVersionName) {
		Version version = new Version();
		String newVersionName = version.getNewVersionName(oldVersionName, versionType);

		version.setVersion(newVersionName);
		version.setComment(description);
		version.setDate(new Date());
		version.setUser(username);

		return version;
	}

	/** Creates a new search index entry for the given document */
	private void createIndexEntry(Document document, long docId, String filename, String path) throws Exception {
		indexer.deleteDocument(String.valueOf(docId), document.getLanguage());
		indexer.addDirectory(new File(path + filename), document);
	}

	/** Creates history entry saying username has checked in document (id) */
	private void createHistoryEntry(long docId, String username, String eventType) {
		History history = new History();
		history.setDocId(docId);
		history.setDate(new Date());
		history.setUsername(username);
		history.setEvent(eventType);
		historyDAO.store(history);
	}

	@Override
	public String getDocumentContent(long docId) {
		Document doc = documentDAO.findByPrimaryKey(docId);
		org.apache.lucene.document.Document luceneDoc = indexer.getDocument(Long.toString(docId), doc.getLanguage());
		// First search the document using it's id
		if (luceneDoc == null)
			luceneDoc = indexer.getDocument(Long.toString(docId), doc.getLanguage());
		// If not found, search the document using it's menu id
		if (luceneDoc != null)
			return luceneDoc.get("content");
		else
			return "";
	}
}