package com.logicaldoc.core.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Field;

import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.searchengine.Indexer;
import com.logicaldoc.core.searchengine.LuceneDocument;
import com.logicaldoc.core.searchengine.store.Storer;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.text.parser.Parser;
import com.logicaldoc.core.text.parser.ParserFactory;
import com.logicaldoc.util.CharsetDetector;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * Basic Implementation of <code>DocumentManager</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5
 */
public class DocumentManagerImpl implements DocumentManager {

	protected static Log log = LogFactory.getLog(DocumentManagerImpl.class);

	private DocumentDAO documentDAO;

	private DocumentTemplateDAO documentTemplateDAO;

	private DocumentListenerManager listenerManager;

	private HistoryDAO historyDAO;

	private VersionDAO versionDAO;

	private SettingsConfig settings;

	private Indexer indexer;

	public void setListenerManager(DocumentListenerManager listenerManager) {
		this.listenerManager = listenerManager;
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setDocumentTemplateDAO(DocumentTemplateDAO documentTemplateDAO) {
		this.documentTemplateDAO = documentTemplateDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	public void setSettings(SettingsConfig settings) {
		this.settings = settings;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	@Override
	public void checkin(long docId, File file, String filename, User user, VERSION_TYPE versionType,
			String versionDesc, boolean immediateIndexing) throws Exception {
		FileInputStream is = new FileInputStream(file);
		try {
			checkin(docId, is, filename, user, versionType, versionDesc, immediateIndexing);
		} finally {
			is.close();
		}
	}

	@Override
	public void checkin(long docId, InputStream fileInputStream, String filename, User user, VERSION_TYPE versionType,
			String versionDesc, boolean immediateIndexing) throws Exception {
		// identify the document and menu
		Document document = documentDAO.findById(docId);

		if (document.getImmutable() == 0) {
			documentDAO.initialize(document);

			Map<String, Object> dictionary = new HashMap<String, Object>();

			log.debug("Invoke listeners before checkin");
			for (DocumentListener listener : listenerManager.getListeners()) {
				listener.beforeCheckin(document, dictionary);
			}

			document.setIndexed(0);
			document.setSigned(0);
			documentDAO.store(document);

			Menu folder = document.getFolder();

			// create some strings containing paths
			document.setFileName(filename);

			// set other properties of the document
			document.setDate(new Date());
			document.setPublisher(user.getFullName());
			document.setPublisherId(user.getId());
			document.setStatus(Document.DOC_UNLOCKED);
			document.setType(document.getFileExtension());
			document.setLockUserId(null);
			document.setFolder(folder);

			// create new version
			Version version = Version.create(document, user, versionDesc, Version.EVENT_CHECKIN, versionType);
			if (documentDAO.store(document) == false)
				throw new Exception();

			// Store the version
			versionDAO.store(version);
			log.debug("Stored version " + version.getVersion());

			// create history entry for this checkin event
			createHistoryEntry(docId, user, History.EVENT_CHECKEDIN, "");

			// create search index entry
			if (immediateIndexing)
				createIndexEntry(document);

			// store the document in the repository (on the file system)
			store(document, fileInputStream);

			log.debug("Invoke listeners after store");
			for (DocumentListener listener : listenerManager.getListeners()) {
				listener.afterCheckin(document, dictionary);
			}

			log.debug("Checked in document " + docId);
		}
	}

	@Override
	public void checkout(long docId, User user) throws Exception {
		lock(docId, Document.DOC_CHECKED_OUT, user, "");
	}

	@Override
	public void lock(long docId, int status, User user, String comment) throws Exception {
		lock(docId, status, History.EVENT_LOCKED, user, comment);
	}

	@Override
	public void lock(long docId, int status, String historyEvent, User user, String comment) throws Exception {
		Document document = documentDAO.findById(docId);
		if (document.getStatus() != Document.DOC_UNLOCKED)
			throw new Exception("Document is locked");

		document.setLockUserId(user.getId());
		document.setStatus(status);
		document.setFolder(document.getFolder());
		documentDAO.store(document);

		// create history entry for this checkout event
		createHistoryEntry(docId, user, historyEvent, comment);

		log.debug("locked document " + docId);
	}

	@Override
	public Document create(File file, Menu folder, User user, String language, boolean immediateIndexing)
			throws Exception {
		return create(file, folder, user, language, "", null, "", "", "", "", "", null, immediateIndexing);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, String language,
			boolean immediateIndexing) throws Exception {
		String title = filename;
		if (StringUtils.isNotEmpty(filename) && filename.lastIndexOf(".") > 0)
			title = filename.substring(0, filename.lastIndexOf("."));
		return create(content, filename, folder, user, language, title, null, "", "", "", "", "", null,
				immediateIndexing);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, String language, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, boolean immediateIndexing) throws Exception {
		return create(content, filename, folder, user, language, title, sourceDate, source, sourceAuthor, sourceType,
				coverage, versionDesc, tags, null, null, immediateIndexing);
	}

	@Override
	public Document create(File file, Menu folder, User user, String language, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> tags, boolean immediateIndexing) throws Exception {
		return create(file, folder, user, language, title, sourceDate, source, sourceAuthor, sourceType, coverage,
				versionDesc, tags, null, null, immediateIndexing);
	}

	private void store(Document doc, InputStream content) throws IOException {
		// Makes path
		String path = doc.getPath() + "/doc_" + doc.getId();

		// Get file to upload inputStream
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);

		// stores it in folder
		storer.store(content, path, doc.getFileVersion());
	}

	@Override
	public void delete(long docId) throws Exception {
		Document doc = documentDAO.findById(docId);
		if (doc.getImmutable() == 1)
			throw new Exception("Document is immutable");
		deleteDocument(doc);
		boolean result = documentDAO.delete(docId);
		if (!result)
			throw new Exception("Database Error deleting document");
	}

	/**
	 * Utility method for document removal from index and file system
	 * 
	 * @param doc
	 */
	private void deleteDocument(Document doc) {
		if (doc.getImmutable() == 1)
			return;
		try {
			long docId = doc.getId();

			// Physically remove the document from full-text index
			if (doc != null) {
				indexer.deleteDocument(String.valueOf(docId), doc.getLanguage());
			}

			doc.setIndexed(0);
			documentDAO.store(doc);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public File getDocumentFile(Document doc) {
		return getDocumentFile(doc, null);
	}

	@Override
	public File getDocumentFile(long docId) {
		return getDocumentFile(docId, null);
	}

	@Override
	public File getDocumentFile(long docId, String fileVersion) {
		return getDocumentFile(docId, fileVersion, null);
	}

	@Override
	public File getDocumentFile(long docId, String fileVersion, String suffix) {
		Document doc = documentDAO.findById(docId);
		return getDocumentFile(doc, fileVersion, suffix);
	}

	@Override
	public File getDocumentFile(Document doc, String fileVersion) {
		return getDocumentFile(doc, fileVersion, null);
	}

	@Override
	public File getDocumentFile(Document doc, String fileVersion, String suffix) {
		String path = getDocFilePath(doc);

		/*
		 * All versions of a document are stored in the same directory as the
		 * current version, but the filename is the version number without
		 * extension, e.g. "docId/2.1"
		 */
		String filename;
		if (StringUtils.isEmpty(fileVersion))
			filename = doc.getFileVersion();
		else
			filename = fileVersion;
		if (StringUtils.isEmpty(filename))
			filename = doc.getVersion();

		/*
		 * Document's related resources are stored with a suffix, e.g.
		 * "docId/2.1-thumb.jpg"
		 */
		if (StringUtils.isNotEmpty(suffix))
			filename += "-" + suffix;
		return new File(path, filename);
	}

	@Override
	public String getDocumentContent(Document doc) {
		String content = null;
		File file = getDocumentFile(doc);

		// Parses the file where it is already stored
		Locale locale = new Locale(doc.getLanguage());
		Parser parser = ParserFactory.getParser(file, locale, FilenameUtils.getExtension(doc.getFileName()));

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
		doc.setIndexed(0);
		documentDAO.store(doc);

		// Add the document to the index (lucene 2.x doesn't support the update
		// operation)
		File file = getDocumentFile(doc);

		indexer.addFile(file, doc, content, lang);
		doc.setIndexed(1);
		documentDAO.store(doc);
	}

	@Override
	public void update(Document doc, User user, String title, String source, String sourceAuthor, Date sourceDate,
			String sourceType, String coverage, String language, Set<String> tags, String sourceId, String object,
			String recipient, Long templateId, Map<String, String> attributes) throws Exception {
		try {
			if (doc.getImmutable() == 0) {
				doc.setTitle(title);
				doc.setSource(source);
				doc.setSourceId(sourceId);
				doc.setObject(object);
				doc.setSourceAuthor(sourceAuthor);
				if (sourceDate != null)
					doc.setSourceDate(sourceDate);
				else
					doc.setSourceDate(null);
				doc.setSourceType(sourceType);
				doc.setCoverage(coverage);
				doc.setRecipient(recipient);

				// Intercept language changes
				String oldLang = doc.getLanguage();
				doc.setLanguage(language);

				// Ensure unique title in folder
				setUniqueTitle(doc);

				doc.clearTags();
				documentDAO.store(doc);
				doc.setTags(tags);

				// Change the template and attributes
				if (templateId != null) {
					DocumentTemplate template = documentTemplateDAO.findById(templateId.longValue());
					doc.setTemplate(template);
					doc.getAttributes().clear();
					if (attributes != null)
						for (String name : attributes.keySet()) {
							if (StringUtils.isNotEmpty(name))
								doc.setValue(name, attributes.get(name));
						}
				} else {
					doc.setTemplate(null);
				}

				// create a new version
				Version version = Version.create(doc, user, "", Version.EVENT_CHANGED,
						Version.VERSION_TYPE.NEW_SUBVERSION);

				documentDAO.store(doc);
				versionDAO.store(version);

				/* create history entry */
				History history = new History();
				history.setDocId(doc.getId());
				history.setDate(new Date());
				history.setUserName(user.getFullName());
				history.setUserId(user.getId());
				history.setEvent(History.EVENT_CHANGED);
				historyDAO.store(history);

				// Launch document re-indexing
				if (doc.getIndexed() == 1)
					reindex(doc, oldLang);
			} else {
				throw new Exception("Document is immutable");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	/** Creates a new search index entry for the given document */
	private void createIndexEntry(Document document) throws Exception {
		indexer.deleteDocument(String.valueOf(document.getId()), document.getLanguage());
		indexer.addFile(getDocumentFile(document), document);
		document.setIndexed(1);
		documentDAO.store(document);
	}

	/** Creates history entry saying username has checked in document (id) */
	private void createHistoryEntry(long docId, User user, String eventType, String reason) {
		History history = new History();
		history.setDocId(docId);
		history.setDate(new Date());
		history.setUserId(user.getId());
		history.setUserName(user.getFullName());
		history.setEvent(eventType);
		history.setComment(reason);
		historyDAO.store(history);
	}

	@Override
	public String getDocumentContent(long docId) {
		Document doc = documentDAO.findById(docId);
		org.apache.lucene.document.Document luceneDoc = indexer.getDocument(Long.toString(docId), doc.getLanguage());
		// If not found, search the document using it's menu id
		if (luceneDoc != null)
			return luceneDoc.get(LuceneDocument.FIELD_CONTENT);
		else
			return "";
	}

	/**
	 * Computes the directory path where the document file is stored
	 */
	private String getDocFilePath(Document doc) {
		String path = new StringBuilder(settings.getValue("docdir")).append("/").append(doc.getPath()).append("/doc_")
				.append(doc.getId()).append("/").toString();
		return path;
	}

	@Override
	public void moveToFolder(Document doc, Menu folder, User user) throws Exception {
		if (folder.equals(doc.getFolder()))
			return;

		if (doc.getImmutable() == 0) {

			// Get original document directory path
			String path = getDocFilePath(doc);
			String originalFileName = doc.getFileName();
			File originalDocDir = new File(path);

			documentDAO.initialize(doc);
			doc.setFolder(folder);
			setUniqueTitle(doc);
			setUniqueFilename(doc);
			documentDAO.store(doc);

			// Update the FS
			path = getDocFilePath(doc);

			File newDocDir = new File(path);

			FileUtils.moveDirectory(originalDocDir, newDocDir);

			if (!doc.getFileName().equals(originalFileName)) {
				File originalFile = new File(newDocDir, originalFileName);
				File destFile = new File(newDocDir, doc.getFileName());
				originalFile.renameTo(destFile);
			}

			createHistoryEntry(doc.getId(), user, History.EVENT_MOVED, "");
			Version version = Version.create(doc, user, "", Version.EVENT_MOVED, Version.VERSION_TYPE.NEW_SUBVERSION);
			versionDAO.store(version);

			if (doc.getIndexed() == 1) {
				Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
				org.apache.lucene.document.Document indexDocument = null;
				indexDocument = indexer.getDocument(String.valueOf(doc.getId()), doc.getLanguage());
				if (indexDocument != null) {
					indexer.deleteDocument(String.valueOf(doc.getId()), doc.getLanguage());
					indexDocument.removeField(LuceneDocument.FIELD_PATH);
					indexDocument.add(new Field(LuceneDocument.FIELD_PATH, doc.getPath(), Field.Store.YES,
							Field.Index.UN_TOKENIZED));
					indexer.addDocument(indexDocument, doc.getLanguage());
				}
			}
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public Document create(File file, Menu folder, User user, String language, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> tags, Long templateId, Map<String, String> extendedAttributes, boolean immediateIndexing)
			throws Exception {
		return create(file, folder, user, language, title, sourceDate, source, sourceAuthor, sourceType, coverage,
				versionDesc, tags, templateId, extendedAttributes, null, null, null, immediateIndexing);

	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, String language, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, String> extendedAttributes,
			boolean immediateIndexing) throws Exception {
		return create(content, filename, folder, user, language, title, sourceDate, source, sourceAuthor, sourceType,
				coverage, versionDesc, tags, templateId, extendedAttributes, null, null, null, immediateIndexing);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, String language, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, String> extendedAttributes,
			String sourceId, String object, String recipient, boolean immediateIndexing) throws Exception {

		try {
			Document doc = new Document();
			doc.setFolder(folder);
			doc.setFileName(filename);
			doc.setDate(new Date());

			String fallbackTitle = filename;
			String type = "unknown";
			int lastDotIndex = filename.lastIndexOf(".");
			if (lastDotIndex > 0) {
				fallbackTitle = filename.substring(0, lastDotIndex);
				type = filename.substring(lastDotIndex + 1).toLowerCase();
			}

			if (StringUtils.isNotEmpty(title)) {
				doc.setTitle(title);
			} else {
				doc.setTitle(fallbackTitle);
			}

			setUniqueTitle(doc);
			setUniqueFilename(doc);

			if (sourceDate != null)
				doc.setSourceDate(sourceDate);
			else
				doc.setSourceDate(doc.getDate());
			doc.setPublisher(user.getFullName());
			doc.setPublisherId(user.getId());
			doc.setCreator(user.getFullName());
			doc.setCreatorId(user.getId());
			doc.setStatus(Document.DOC_UNLOCKED);
			doc.setType(type);
			doc.setVersion("1.0");
			doc.setFileVersion("1.0");
			doc.setSource(source);
			doc.setSourceAuthor(sourceAuthor);
			doc.setSourceType(sourceType);
			doc.setCoverage(coverage);
			doc.setLanguage(language);
			doc.setObject(object);
			doc.setSourceId(sourceId);
			doc.setRecipient(recipient);
			if (tags != null)
				doc.setTags(tags);

			/* Set template and extended attributes */
			if (templateId != null) {
				DocumentTemplate template = documentTemplateDAO.findById(templateId);
				doc.setTemplate(template);
				if (extendedAttributes != null)
					doc.setAttributes(extendedAttributes);
			}
			documentDAO.store(doc);

			/* store the document */
			store(doc, content);

			createHistoryEntry(doc.getId(), user, History.EVENT_STORED, "");

			File file = getDocumentFile(doc);
			if (immediateIndexing) {
				/* create search index entry */
				String lang = doc.getLanguage();
				indexer.addFile(file, doc, getDocumentContent(doc), lang);
				doc.setIndexed(1);
			}
			doc.setFileSize(file.length());

			documentDAO.store(doc);

			// Store the initial version 1.0
			Version vers = Version.create(doc, user, versionDesc, Version.EVENT_STORED,
					Version.VERSION_TYPE.OLD_VERSION);
			versionDAO.store(vers);

			log.debug("Stored version " + vers.getVersion());
			return doc;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Avoid title duplications in the same folder
	 */
	private void setUniqueTitle(Document doc) {
		int counter = 1;
		String buf = doc.getTitle();
		Long excludeId = null;
		if (doc.getId() > 0)
			excludeId = doc.getId();
		while (documentDAO.findByTitleAndParentFolderId(doc.getFolder().getId(), doc.getTitle(), excludeId).size() > 0) {
			doc.setTitle(buf + "(" + (counter++) + ")");
		}
	}

	/**
	 * Avoid Filename duplications in the same folder
	 */
	private void setUniqueFilename(Document doc) {
		int counter = 1;

		String name = doc.getFileName();
		if (doc.getFileName().indexOf(".") != -1) {
			name = doc.getFileName().substring(0, doc.getFileName().lastIndexOf("."));
		}

		String ext = "";
		if (doc.getFileName().indexOf(".") != -1) {
			ext = doc.getFileName().substring(doc.getFileName().lastIndexOf("."));
		}

		Long excludeId = null;
		if (doc.getId() > 0)
			excludeId = doc.getId();
		while (documentDAO.findByFileNameAndParentFolderId(doc.getFolder().getId(), doc.getFileName(), excludeId)
				.size() > 0) {
			doc.setFileName(name + "(" + (counter++) + ")" + ext);
		}
	}

	public Document copyToFolder(Document doc, Menu folder, User user) throws Exception {
		if (doc.getImmutable() != 0) {
			throw new Exception("Document is immutable");
		}

		// Get original document directory path
		String path = getDocFilePath(doc);

		File sourceFile = new File(path, doc.getVersion());

		// initialize the document
		documentDAO.initialize(doc);

		InputStream is = new FileInputStream(sourceFile);
		try {
			return create(is, doc.getFileName(), folder, user, doc.getLanguage(), doc.getTitle(), doc.getSourceDate(),
					doc.getSource(), doc.getSourceAuthor(), doc.getSourceType(), doc.getCoverage(), "", null, null,
					null, false);
		} finally {
			is.close();
		}
	}

	@Override
	public void unlock(long docId, User user, String comment) throws Exception {
		Document document = documentDAO.findById(docId);
		document.setLockUserId(null);
		document.setStatus(Document.DOC_UNLOCKED);
		documentDAO.store(document);

		// create history entry for this UNLOCK event
		createHistoryEntry(docId, user, History.EVENT_UNLOCKED, comment);

		log.debug("Unlocked document " + docId);
	}

	@Override
	public void makeImmutable(long docId, User user, String reason) throws Exception {
		Document document = documentDAO.findById(docId);
		if (document.getImmutable() == 0) {
			documentDAO.makeImmutable(docId);
			createHistoryEntry(docId, user, History.EVENT_IMMUTABLE, reason);
			log.debug("The document " + docId + " has been marked as immutable ");
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public List<Menu> deleteFolder(Menu folder, User user) throws Exception {
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		List<Menu> deletableFolders = new ArrayList<Menu>();
		List<Menu> notDeletableFolders = new ArrayList<Menu>();

		if (mdao.isPermissionEnabled(Permission.DELETE, folder.getId(), user.getId())) {
			deletableFolders.add(folder);
		} else {
			notDeletableFolders.add(folder);
			return notDeletableFolders;
		}

		try {
			// Retrieve all the sub-folders
			List<Menu> subfolders = mdao.findByParentId(folder.getId());

			for (Menu subfolder : subfolders) {
				if (mdao.isPermissionEnabled(Permission.DELETE, subfolder.getId(), user.getId())) {
					deletableFolders.add(subfolder);
				} else {
					notDeletableFolders.add(subfolder);
				}
			}

			for (Menu deletableFolder : deletableFolders) {
				boolean foundDocImmutable = false;
				boolean foundDocLocked = false;
				DocumentDAO docdao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				List<Document> docs = docdao.findByFolder(deletableFolder.getId());
				for (Document doc : docs) {
					if (doc.getImmutable() == 1) {
						foundDocImmutable = true;
						continue;
					}
					if (doc.getStatus() != Document.DOC_UNLOCKED) {
						foundDocLocked = true;
						continue;
					}
					delete(doc.getId());
				}
				if (foundDocImmutable || foundDocLocked) {
					notDeletableFolders.add(deletableFolder);
				}
			}

			// Avoid deletion of the entire path of an undeleteble folder
			for (Menu notDeletable : notDeletableFolders) {
				Menu parent = notDeletable;
				while (true) {
					if (deletableFolders.contains(parent))
						deletableFolders.remove(parent);
					if (parent.equals(folder))
						break;
					parent = mdao.findById(parent.getParentId());
				}
			}

			for (Menu fld : deletableFolders) {
				mdao.delete(fld.getId());
			}
			return notDeletableFolders;
		} catch (Throwable e) {
			log.error(e);
			return notDeletableFolders;
		}
	}

	@Override
	public Document create(File file, Menu folder, User user, String language, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> tags, Long templateId, Map<String, String> extendedAttributes, String sourceId,
			String object, String recipient, boolean immediateIndexing) throws Exception {
		String filename = file.getName();
		String encoding = "UTF-8";
		String[] encodings = CharsetDetector.detectEncodings(filename);
		if (encodings != null && encodings.length > 0)
			encoding = encodings[0];
		if ("UTF-8".equals(encoding)) {
			filename = new String(filename.getBytes(), "UTF-8");
		}
		String _title = title;

		if (StringUtils.isEmpty(title)) {
			String fallbackTitle = filename;
			int lastDotIndex = filename.lastIndexOf(".");
			if (lastDotIndex > 0) {
				fallbackTitle = filename.substring(0, lastDotIndex);
			}
			_title = fallbackTitle;
		}

		InputStream is = new FileInputStream(file);
		try {
			return create(is, filename, folder, user, language, _title, sourceDate, source, sourceAuthor, sourceType,
					coverage, versionDesc, tags, templateId, extendedAttributes, sourceId, object, recipient,
					immediateIndexing);
		} finally {
			is.close();
		}
	}

	public void rename(Document doc, User user, String newFilename) throws Exception {
		if (doc.getImmutable() == 0) {
			documentDAO.initialize(doc);
			doc.setFileName(newFilename);
			setUniqueFilename(doc);
			documentDAO.store(doc);

			// create history entry for this RENAMED event
			createHistoryEntry(doc.getId(), user, History.EVENT_RENAMED, "");

			Version version = Version.create(doc, user, "", Version.EVENT_RENAMED, Version.VERSION_TYPE.NEW_SUBVERSION);
			versionDAO.store(version);

			log.debug("Document filename renamed: " + doc.getId());
		} else {
			throw new Exception("Document is immutable");
		}
	}

	public void setVersionDAO(VersionDAO versionDAO) {
		this.versionDAO = versionDAO;
	}
}