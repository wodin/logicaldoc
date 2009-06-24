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

	private MenuDAO menuDAO;

	private Indexer indexer;

	private Storer storer;

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
			
			// Encode the filename in the same way of when we insert a new document
			String myFilename = new String(filename);
			String encoding = "UTF-8";
			String[] encodings = CharsetDetector.detectEncodings(myFilename);
			if (encodings != null && encodings.length > 0)
				encoding = encodings[0];
			if ("UTF-8".equals(encoding)) {
				myFilename = new String(myFilename.getBytes(), "UTF-8");
			}

			// create some strings containing paths
			document.setFileName(myFilename);

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
			createHistoryEntry(document, user, History.EVENT_CHECKEDIN, "");

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
		lock(docId, Document.DOC_CHECKED_OUT, History.EVENT_CHECKEDOUT, user, "");
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
		createHistoryEntry(document, user, historyEvent, comment);

		log.debug("locked document " + docId);
	}

	@Override
	public Document create(File file, Menu folder, User user, Locale locale, boolean immediateIndexing)
			throws Exception {
		return create(file, folder, user, locale, "", null, "", "", "", "", "", null, immediateIndexing);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale,
			boolean immediateIndexing) throws Exception {
		String title = filename;
		if (StringUtils.isNotEmpty(filename) && filename.lastIndexOf(".") > 0)
			title = filename.substring(0, filename.lastIndexOf("."));
		return create(content, filename, folder, user, locale, title, null, "", "", "", "", "", null, immediateIndexing);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, boolean immediateIndexing) throws Exception {
		return create(content, filename, folder, user, locale, title, sourceDate, source, sourceAuthor, sourceType,
				coverage, versionDesc, tags, null, null, immediateIndexing);
	}

	@Override
	public Document create(File file, Menu folder, User user, Locale locale, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> tags, boolean immediateIndexing) throws Exception {
		return create(file, folder, user, locale, title, sourceDate, source, sourceAuthor, sourceType, coverage,
				versionDesc, tags, null, null, immediateIndexing);
	}

	private void store(Document doc, InputStream content) throws IOException {
		// Get file to upload inputStream
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);

		// stores it in folder
		storer.store(content, doc.getId(), doc.getFileVersion());
	}

	/**
	 * Utility method for document removal from index
	 * 
	 * @param doc
	 */
	@Override
	public void deleteFromIndex(Document doc) {
		if (doc.getImmutable() == 1)
			return;
		try {
			long docId = doc.getId();

			// Physically remove the document from full-text index
			if (doc != null) {
				indexer.deleteDocument(String.valueOf(docId), doc.getLocale());
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
		return storer.getFile(doc, fileVersion, suffix);
	}

	@Override
	public String getDocumentContent(Document doc) {
		String content = null;
		File file = getDocumentFile(doc);

		// Parses the file where it is already stored
		Locale locale = doc.getLocale();
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
	public void reindex(Document doc, Locale originalLocale) throws Exception {
		/* get search index entry */
		Locale locale = doc.getLocale();

		// Extract the content from the file
		String content = getDocumentContent(doc);

		// Remove the document from the index
		indexer.deleteDocument(String.valueOf(doc.getId()), originalLocale);
		doc.setIndexed(0);
		documentDAO.store(doc);

		// Add the document to the index (lucene 2.x doesn't support the update
		// operation)
		File file = getDocumentFile(doc);

		indexer.addFile(file, doc, content, locale);
		doc.setIndexed(1);
		documentDAO.store(doc);
	}

	@Override
	public void update(Document doc, User user, String title, String source, String sourceAuthor, Date sourceDate,
			String sourceType, String coverage, Locale locale, Set<String> tags, String sourceId, String object,
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

				// Always remove the document from index, so that it can be
				// re-indexed
				indexer.deleteDocument(Long.toString(doc.getId()), doc.getLocale());
				doc.setIndexed(0);

				// Intercept locale changes
				Locale oldLocale = doc.getLocale();
				doc.setLocale(locale);

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
				createHistoryEntry(doc, user, History.EVENT_CHANGED, "");
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
		indexer.deleteDocument(String.valueOf(document.getId()), document.getLocale());
		indexer.addFile(getDocumentFile(document), document);
		document.setIndexed(1);
		documentDAO.store(document);
	}

	/** Creates history entry saying username has checked in document (id) */
	private void createHistoryEntry(Document doc, User user, String eventType, String comment) {

		History history = new History();
		history.setDocId(doc.getId());
		history.setTitle(doc.getTitle());
		history.setVersion(doc.getVersion());
		history.setPath(decodeDocumentPath(doc));
		history.setDate(new Date());
		history.setUserId(user.getId());
		history.setUserName(user.getFullName());
		history.setEvent(eventType);
		history.setComment(comment);

		historyDAO.store(history);
	}

	@Override
	public String getDocumentContent(long docId) {
		Document doc = documentDAO.findById(docId);
		org.apache.lucene.document.Document luceneDoc = indexer.getDocument(Long.toString(docId), doc.getLocale());
		// If not found, search the document using it's menu id
		if (luceneDoc != null)
			return luceneDoc.get(LuceneDocument.FIELD_CONTENT);
		else
			return "";
	}

	@Override
	public void moveToFolder(Document doc, Menu folder, User user) throws Exception {
		if (folder.equals(doc.getFolder()))
			return;

		if (doc.getImmutable() == 0) {
			// get the start folder
			String startFolder = decodeDocumentPath(doc);
			
			documentDAO.initialize(doc);
			doc.setFolder(folder);
			setUniqueTitle(doc);
			setUniqueFilename(doc);
			documentDAO.store(doc);

            // Better description of the event Eg. Document moved from x to y
			String destFolder = decodeDocumentPath(doc);
			
			createHistoryEntry(doc, user, History.EVENT_MOVED, "Moved from " +startFolder + " to " + destFolder);
			
			Version version = Version.create(doc, user, "", Version.EVENT_MOVED, Version.VERSION_TYPE.NEW_SUBVERSION);
			versionDAO.store(version);

			if (doc.getIndexed() == 1) {
				Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
				org.apache.lucene.document.Document indexDocument = null;
				indexDocument = indexer.getDocument(String.valueOf(doc.getId()), doc.getLocale());
				if (indexDocument != null) {
					indexer.deleteDocument(String.valueOf(doc.getId()), doc.getLocale());
					indexDocument.removeField(LuceneDocument.FIELD_PATH);
					indexDocument.add(new Field(LuceneDocument.FIELD_PATH, doc.getPath(), Field.Store.YES,
							Field.Index.UN_TOKENIZED));
					indexer.addDocument(indexDocument, doc.getLocale());
				}
			}
		} else {
			throw new Exception("Document is immutable");
		}
	}

	
	private String decodeDocumentPath(Document doc) {
		
		if (doc == null)
			return "";
		
		String pathDecoded = doc.getFolder().getPathExtended() + "/" + doc.getFolder().getText();
		pathDecoded = pathDecoded.replaceAll("//", "/");
		pathDecoded = pathDecoded.replaceFirst("/menu.documents/", "/");
		pathDecoded = pathDecoded.replaceFirst("/menu.documents", "/");
		
		return pathDecoded;
	}
	

	@Override
	public Document create(File file, Menu folder, User user, Locale locale, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> tags, Long templateId, Map<String, String> extendedAttributes, boolean immediateIndexing)
			throws Exception {
		return create(file, folder, user, locale, title, sourceDate, source, sourceAuthor, sourceType, coverage,
				versionDesc, tags, templateId, extendedAttributes, null, null, null, null, immediateIndexing);

	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, String> extendedAttributes,
			boolean immediateIndexing) throws Exception {
		return create(content, filename, folder, user, locale, title, sourceDate, source, sourceAuthor, sourceType,
				coverage, versionDesc, tags, templateId, extendedAttributes, null, null, null, null, immediateIndexing);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, String> extendedAttributes,
			String sourceId, String object, String recipient, String customId, boolean immediateIndexing)
			throws Exception {

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
			doc.setLocale(locale);
			doc.setObject(object);
			doc.setSourceId(sourceId);
			doc.setRecipient(recipient);
			if (StringUtils.isNotBlank(customId))
				doc.setCustomId(customId);
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

			createHistoryEntry(doc, user, History.EVENT_STORED, "");

			File file = getDocumentFile(doc);
			if (immediateIndexing) {
				/* create search index entry */
				Locale loc = doc.getLocale();
				indexer.addFile(file, doc, getDocumentContent(doc), loc);
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
		File sourceFile = storer.getFile(doc.getId(), doc.getFileVersion());

		// initialize the document
		documentDAO.initialize(doc);

		InputStream is = new FileInputStream(sourceFile);
		try {
			return create(is, doc.getFileName(), folder, user, doc.getLocale(), doc.getTitle(), doc.getSourceDate(),
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
		createHistoryEntry(document, user, History.EVENT_UNLOCKED, comment);

		log.debug("Unlocked document " + docId);
	}

	@Override
	public void makeImmutable(long docId, User user, String reason) throws Exception {
		Document document = documentDAO.findById(docId);
		if (document.getImmutable() == 0) {
			documentDAO.makeImmutable(docId);
			createHistoryEntry(document, user, History.EVENT_IMMUTABLE, reason);
			log.debug("The document " + docId + " has been marked as immutable ");
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public List<Menu> deleteFolder(Menu folder, User user) throws Exception {
		List<Menu> deletableFolders = new ArrayList<Menu>();
		List<Menu> notDeletableFolders = new ArrayList<Menu>();
		List<Document> deletableDocs = new ArrayList<Document>();

		Set<Long> deletableIds = menuDAO.findMenuIdByUserIdAndPermission(user.getId(), Permission.DELETE,
				Menu.MENUTYPE_DIRECTORY);

		if (deletableIds.contains(folder.getId())) {
			deletableFolders.add(folder);
		} else {
			notDeletableFolders.add(folder);
			return notDeletableFolders;
		}

		try {
			// Retrieve all the sub-folders
			List<Menu> subfolders = menuDAO.findByParentId(folder.getId());

			for (Menu subfolder : subfolders) {
				if (deletableIds.contains(subfolder.getId())) {
					deletableFolders.add(subfolder);
				} else {
					notDeletableFolders.add(subfolder);
				}
			}

			for (Menu deletableFolder : deletableFolders) {
				boolean foundDocImmutable = false;
				boolean foundDocLocked = false;
				List<Document> docs = documentDAO.findByFolder(deletableFolder.getId());

				for (Document doc : docs) {
					if (doc.getImmutable() == 1) {
						foundDocImmutable = true;
						continue;
					}
					if (doc.getStatus() != Document.DOC_UNLOCKED || doc.getExportStatus() != Document.EXPORT_UNLOCKED) {
						foundDocLocked = true;
						continue;
					}
					deletableDocs.add(doc);
				}
				if (foundDocImmutable || foundDocLocked) {
					notDeletableFolders.add(deletableFolder);
				}
			}

			// Avoid deletion of the entire path of an undeletable folder
			for (Menu notDeletable : notDeletableFolders) {
				Menu parent = notDeletable;
				while (true) {
					if (deletableFolders.contains(parent))
						deletableFolders.remove(parent);
					if (parent.equals(folder))
						break;
					parent = menuDAO.findById(parent.getParentId());
				}
			}
			menuDAO.deleteAll(deletableFolders);
			documentDAO.deleteAll(deletableDocs);
			return notDeletableFolders;
		} catch (Throwable e) {
			log.error(e);
			return notDeletableFolders;
		}
	}

	@Override
	public Document create(File file, Menu folder, User user, Locale locale, String title, Date sourceDate,
			String source, String sourceAuthor, String sourceType, String coverage, String versionDesc,
			Set<String> tags, Long templateId, Map<String, String> extendedAttributes, String sourceId, String object,
			String recipient, String customId, boolean immediateIndexing) throws Exception {
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
			return create(is, filename, folder, user, locale, _title, sourceDate, source, sourceAuthor, sourceType,
					coverage, versionDesc, tags, templateId, extendedAttributes, sourceId, object, recipient, customId,
					immediateIndexing);
		} finally {
			is.close();
		}
	}

	public void rename(Document doc, User user, String newFilename) throws Exception {
		if (doc.getImmutable() == 0) {
			
			documentDAO.initialize(doc);
			String originalFilename = doc.getFileName();
			
			doc.setFileName(newFilename.trim());
			String extension = FilenameUtils.getExtension(newFilename.trim());
			if (StringUtils.isNotEmpty(extension))
				doc.setType(FilenameUtils.getExtension(newFilename));
			setUniqueFilename(doc);
			doc.setIndexed(0);
			documentDAO.store(doc);

			// create history entry for this RENAMED event
            // Better description of the event Eg. Renamed from x to y (Document renamed)
			createHistoryEntry(doc, user, History.EVENT_RENAMED, "Renamed from " + originalFilename + " to " + newFilename);
			 
			Version version = Version.create(doc, user, "", Version.EVENT_RENAMED, Version.VERSION_TYPE.NEW_SUBVERSION);
			versionDAO.store(version);

			// The document needs to be reindexed
			indexer.deleteDocument(Long.toString(doc.getId()), doc.getLocale());

			log.debug("Document filename renamed: " + doc.getId());
		} else {
			throw new Exception("Document is immutable");
		}
	}

	public void setVersionDAO(VersionDAO versionDAO) {
		this.versionDAO = versionDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}
}