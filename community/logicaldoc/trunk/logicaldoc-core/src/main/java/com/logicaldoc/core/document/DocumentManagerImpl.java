package com.logicaldoc.core.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Field;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.Version.VERSION_TYPE;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
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
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;

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

	private VersionDAO versionDAO;

	private MenuDAO menuDAO;

	private Indexer indexer;

	private Storer storer;

	private PropertiesBean config;

	public void setListenerManager(DocumentListenerManager listenerManager) {
		this.listenerManager = listenerManager;
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setDocumentTemplateDAO(DocumentTemplateDAO documentTemplateDAO) {
		this.documentTemplateDAO = documentTemplateDAO;
	}

	public void setIndexer(Indexer indexer) {
		this.indexer = indexer;
	}

	@Override
	public void checkin(long docId, File file, String filename, User user, VERSION_TYPE versionType,
			String versionDesc, boolean immediateIndexing, History transaction) throws Exception {
		FileInputStream is = new FileInputStream(file);
		try {
			checkin(docId, is, filename, user, versionType, versionDesc, immediateIndexing, transaction);
		} finally {
			is.close();
		}
	}

	@Override
	public void checkin(long docId, InputStream fileInputStream, String filename, User user, VERSION_TYPE versionType,
			String versionDesc, boolean immediateIndexing, History transaction) throws Exception {
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

			// Modify document history entry
			transaction.setUserId(user.getId());
			transaction.setUserName(user.getFullName());
			transaction.setEvent(History.EVENT_CHECKEDIN);

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
	public void checkout(long docId, User user, History transaction) throws Exception {
		lock(docId, Document.DOC_CHECKED_OUT, user, transaction);
	}

	@Override
	public void lock(long docId, int status, User user, History transaction) throws Exception {
		Document document = documentDAO.findById(docId);
		if (document.getStatus() != Document.DOC_UNLOCKED)
			throw new Exception("Document is locked");

		document.setLockUserId(user.getId());
		document.setStatus(status);
		document.setFolder(document.getFolder());

		// Modify document history entry
		transaction.setUserId(user.getId());
		transaction.setUserName(user.getFullName());
		documentDAO.store(document, transaction);

		log.debug("locked document " + docId);
	}

	@Override
	public Document create(File file, String filename, Menu folder, User user, Locale locale,
			boolean immediateIndexing, History transaction) throws Exception {
		return create(file, filename, folder, user, locale, "", null, "", "", "", "", "", null, immediateIndexing,
				transaction);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale,
			boolean immediateIndexing, History transaction) throws Exception {
		String title = filename;
		if (StringUtils.isNotEmpty(filename) && filename.lastIndexOf(".") > 0)
			title = filename.substring(0, filename.lastIndexOf("."));
		return create(content, filename, folder, user, locale, title, null, "", "", "", "", "", null,
				immediateIndexing, transaction);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, boolean immediateIndexing, History transaction) throws Exception {
		return create(content, filename, folder, user, locale, title, sourceDate, source, sourceAuthor, sourceType,
				coverage, versionDesc, tags, null, null, immediateIndexing, transaction);
	}

	@Override
	public Document create(File file, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, boolean immediateIndexing, History transaction) throws Exception {
		return create(file, filename, folder, user, locale, title, sourceDate, source, sourceAuthor, sourceType,
				coverage, versionDesc, tags, null, null, immediateIndexing, transaction);
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

			// Check if there are some shortcuts associated to the indexing
			// document. They must be re-indexed.
			List<Long> shortcutIds = documentDAO.findShortcutIds(doc.getId());
			for (Long shortcutId : shortcutIds) {
				Document shortcutDoc = documentDAO.findById(shortcutId);
				indexer.deleteDocument(String.valueOf(shortcutId), doc.getLocale());
				shortcutDoc.setIndexed(0);
				documentDAO.store(shortcutDoc);
			}
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

		// Check if the document is a shortcut
		if (doc.getDocRef() != null) {
			long docId = doc.getDocRef();
			doc = documentDAO.findById(docId);
		}

		File file = getDocumentFile(doc);

		// Parses the file where it is already stored
		Locale locale = doc.getLocale();
		Parser parser = ParserFactory.getParser(file, doc.getFileName(), locale, null);

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
		// If the 'doc' is a shortcut, it must not be re-indexed, because it is
		// re-indexed when it is analyzed the referenced doc
		if (doc.getDocRef() != null)
			return;

		/* get search index entry */
		Locale locale = doc.getLocale();

		// Extract the content from the file
		String content = getDocumentContent(doc);

		// The document must be re-indexed
		doc.setIndexed(0);
		documentDAO.store(doc);

		// Check if there are some shortcuts associated to the indexing
		// document. They must be re-indexed.
		List<Long> shortcutIds = documentDAO.findShortcutIds(doc.getId());
		for (Long shortcutId : shortcutIds) {
			Document shortcutDoc = documentDAO.findById(shortcutId);
			shortcutDoc.setIndexed(0);
			documentDAO.store(shortcutDoc);
		}

		// Add the document to the index (lucene 2.x doesn't support the update
		// operation)
		File file = getDocumentFile(doc);

		for (Long shortcutId : shortcutIds) {
			Document shortcutDoc = documentDAO.findById(shortcutId);
			indexer.addFile(getDocumentFile(doc), shortcutDoc);
			shortcutDoc.setIndexed(1);
			documentDAO.store(shortcutDoc);
		}

		indexer.addFile(file, doc, content, locale);
		doc.setIndexed(1);
		documentDAO.store(doc);
	}

	@Override
	public void update(Document doc, User user, String title, String source, String sourceAuthor, Date sourceDate,
			String sourceType, String coverage, Locale locale, Set<String> tags, String sourceId, String object,
			String recipient, Long templateId, Map<String, ExtendedAttribute> attributes, History transaction)
			throws Exception {
		try {
			if (doc.getImmutable() == 0) {
				History renameTransaction = null;
				if (!doc.getTitle().equals(title) && title != null) {
					renameTransaction = (History) transaction.clone();
					renameTransaction.setEvent(History.EVENT_RENAMED);
				}

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

				// The document must be re-indexed
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
					DocumentTemplate template = documentTemplateDAO.findById(templateId);
					if (attributes != null) {
						doc.getAttributes().clear();
						for (String attrName : attributes.keySet()) {
							if (template.getAttributes().get(attrName) != null) {
								ExtendedAttribute templateExtAttribute = template.getAttributes().get(attrName);
								ExtendedAttribute docExtendedAttribute = attributes.get(attrName);
								docExtendedAttribute.setMandatory(templateExtAttribute.getMandatory());
								if (templateExtAttribute.getType() == docExtendedAttribute.getType()) {
									doc.getAttributes().put(attrName, docExtendedAttribute);
								} else {
									throw new Exception("The given type value is not correct.");
								}
							} else {
								throw new Exception("The attribute name '" + attrName
										+ "' is not correct for template '" + template.getName() + "'.");
							}
						}
					}
				} else {
					doc.setTemplate(null);
				}

				// create a new version
				Version version = Version.create(doc, user, "", Version.EVENT_CHANGED,
						Version.VERSION_TYPE.NEW_SUBVERSION);

				// Modify document history entry
				transaction.setUserId(user.getId());
				transaction.setUserName(user.getFullName());
				documentDAO.store(doc, transaction);
				if (renameTransaction != null) {
					renameTransaction.setUserId(user.getId());
					renameTransaction.setUserName(user.getFullName());
					documentDAO.store(doc, renameTransaction);
				}

				versionDAO.store(version);

				// Check if there are some shortcuts associated to the indexing
				// document. They must be re-indexed.
				List<Long> shortcutIds = documentDAO.findShortcutIds(doc.getId());
				for (Long shortcutId : shortcutIds) {
					Document shortcutDoc = documentDAO.findById(shortcutId);
					shortcutDoc.setIndexed(0);
					documentDAO.store(shortcutDoc);
				}
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
		indexer.addFile(getDocumentFile(document), document);
		document.setIndexed(1);
		documentDAO.store(document);

		// Check if there are some shortcuts associated to the indexing
		// document. They must be re-indexed.
		List<Long> shortcutIds = documentDAO.findShortcutIds(document.getId());
		for (Long shortcutId : shortcutIds) {
			Document shortcutDoc = documentDAO.findById(shortcutId);
			indexer.addFile(getDocumentFile(document), shortcutDoc);
			shortcutDoc.setIndexed(1);
			documentDAO.store(shortcutDoc);
		}
	}

	@Override
	public String getDocumentContent(long docId) {
		Document doc = documentDAO.findById(docId);
		// Check if the document is a shortcut
		if (doc.getDocRef() != null) {
			docId = doc.getDocRef();
			doc = documentDAO.findById(docId);
		}
		org.apache.lucene.document.Document luceneDoc = indexer.getDocument(Long.toString(docId), doc.getLocale());
		// If not found, search the document using it's menu id
		if (luceneDoc != null)
			return luceneDoc.get(LuceneDocument.FIELD_CONTENT);
		else
			return "";
	}

	@Override
	public void moveToFolder(Document doc, Menu folder, User user, History transaction) throws Exception {
		if (folder.equals(doc.getFolder()))
			return;

		if (doc.getImmutable() == 0) {
			documentDAO.initialize(doc);
			doc.setFolder(folder);
			setUniqueTitle(doc);
			setUniqueFilename(doc);

			// Modify document history entry
			transaction.setUserId(user.getId());
			transaction.setUserName(user.getFullName());
			if (transaction.getEvent().trim().isEmpty())
				transaction.setEvent(History.EVENT_MOVED);
			documentDAO.store(doc, transaction);

			Version version = Version.create(doc, user, "", Version.EVENT_MOVED, Version.VERSION_TYPE.NEW_SUBVERSION);
			versionDAO.store(version);

			if (doc.getIndexed() == 1) {
				Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
				org.apache.lucene.document.Document indexDocument = null;
				indexDocument = indexer.getDocument(String.valueOf(doc.getId()), doc.getLocale());
				if (indexDocument != null) {
					indexDocument.removeField(LuceneDocument.FIELD_PATH);
					indexDocument.add(new Field(LuceneDocument.FIELD_PATH, doc.getPath(), Field.Store.YES,
							Field.Index.UN_TOKENIZED));
					indexer.addDocument(indexDocument, doc.getLocale());

					// Make the same operation for the shortcuts
					if (documentDAO.findShortcutIds(doc.getId()).size() > 0) {
						org.apache.lucene.document.Document shortcutIndexDocument = null;
						for (Long shortcutId : documentDAO.findShortcutIds(doc.getId())) {
							shortcutIndexDocument = indexer.getDocument(String.valueOf(shortcutId), doc.getLocale());
							if (shortcutIndexDocument != null) {
								shortcutIndexDocument.removeField(LuceneDocument.FIELD_PATH);
								shortcutIndexDocument.add(new Field(LuceneDocument.FIELD_PATH, doc.getPath(),
										Field.Store.YES, Field.Index.UN_TOKENIZED));
								indexer.addDocument(shortcutIndexDocument, doc.getLocale());
							}
						}
					}
				}
			}
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public Document create(File file, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, ExtendedAttribute> extendedAttributes,
			boolean immediateIndexing, History transaction) throws Exception {
		return create(file, filename, folder, user, locale, title, sourceDate, source, sourceAuthor, sourceType,
				coverage, versionDesc, tags, templateId, extendedAttributes, null, null, null, null, immediateIndexing,
				transaction);

	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, ExtendedAttribute> extendedAttributes,
			boolean immediateIndexing, History transaction) throws Exception {
		return create(content, filename, folder, user, locale, title, sourceDate, source, sourceAuthor, sourceType,
				coverage, versionDesc, tags, templateId, extendedAttributes, null, null, null, null, immediateIndexing,
				transaction);
	}

	@Override
	public Document create(InputStream content, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, ExtendedAttribute> extendedAttributes,
			String sourceId, String object, String recipient, String customId, boolean immediateIndexing,
			History transaction) throws Exception {

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
			doc.setVersion(config.getProperty("document.startversion"));
			doc.setFileVersion(doc.getVersion());
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
			if (templateId != null && templateId.longValue() != 0) {
				DocumentTemplate template = documentTemplateDAO.findById(templateId);
				doc.setTemplate(template);

				if (extendedAttributes != null) {
					for (String attrName : extendedAttributes.keySet()) {
						if (template.getAttributes().get(attrName) != null) {
							ExtendedAttribute templateExtAttribute = template.getAttributes().get(attrName);
							ExtendedAttribute docExtendedAttribute = extendedAttributes.get(attrName);
							if (templateExtAttribute.getType() == docExtendedAttribute.getType()) {
								doc.getAttributes().put(attrName, docExtendedAttribute);
							} else {
								throw new Exception("The given type value is not correct.");
							}
						}
					}
				}
			}

			// Modify document history entry
			transaction.setUserId(user.getId());
			transaction.setUserName(user.getFullName());
			documentDAO.store(doc, transaction);

			/* store the document into filesystem */
			store(doc, content);

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

	public Document copyToFolder(Document doc, Menu folder, User user, History transaction) throws Exception {
		// initialize the document
		documentDAO.initialize(doc);
		if (doc.getDocRef() != null) {
			return createShortcut(doc, folder, user, transaction);
		}

		File sourceFile = storer.getFile(doc.getId(), doc.getFileVersion());

		InputStream is = new FileInputStream(sourceFile);
		try {
			return create(is, doc.getFileName(), folder, user, doc.getLocale(), doc.getTitle(), doc.getSourceDate(),
					doc.getSource(), doc.getSourceAuthor(), doc.getSourceType(), doc.getCoverage(), "", null, null,
					null, false, transaction);
		} finally {
			is.close();
		}
	}

	@Override
	public void unlock(long docId, User user, History transaction) throws Exception {
		Document document = documentDAO.findById(docId);
		document.setLockUserId(null);
		document.setStatus(Document.DOC_UNLOCKED);

		// Modify document history entry
		transaction.setUserId(user.getId());
		transaction.setUserName(user.getFullName());
		transaction.setEvent(History.EVENT_UNLOCKED);
		documentDAO.store(document, transaction);

		log.debug("Unlocked document " + docId);
	}

	@Override
	public void makeImmutable(long docId, User user, History transaction) throws Exception {
		Document document = documentDAO.findById(docId);
		if (document.getImmutable() == 0) {
			// Modify document history entry
			transaction.setUserId(user.getId());
			transaction.setUserName(user.getFullName());
			transaction.setEvent(History.EVENT_IMMUTABLE);
			documentDAO.makeImmutable(docId, transaction);

			log.debug("The document " + docId + " has been marked as immutable ");
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public List<Menu> deleteFolder(Menu folder, User user, History transaction) throws Exception {
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

			// Modify document history entry
			transaction.setUserId(user.getId());
			transaction.setUserName(user.getFullName());
			menuDAO.deleteAll(deletableFolders, transaction);
			documentDAO.deleteAll(deletableDocs, transaction);
			return notDeletableFolders;
		} catch (Throwable e) {
			log.error(e);
			return notDeletableFolders;
		}
	}

	@Override
	public Document create(File file, String filename, Menu folder, User user, Locale locale, String title,
			Date sourceDate, String source, String sourceAuthor, String sourceType, String coverage,
			String versionDesc, Set<String> tags, Long templateId, Map<String, ExtendedAttribute> extendedAttributes,
			String sourceId, String object, String recipient, String customId, boolean immediateIndexing,
			History transaction) throws Exception {
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
					immediateIndexing, transaction);
		} finally {
			is.close();
		}
	}

	public void rename(Document doc, User user, String newName, boolean title, History transaction) throws Exception {
		Document document = doc;
		if (doc.getDocRef() != null) {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			document = docDao.findById(doc.getDocRef());
		}

		if (document.getImmutable() == 0) {
			documentDAO.initialize(document);
			if (title) {
				document.setTitle(newName);
				setUniqueTitle(document);
			} else {
				document.setFileName(newName.trim());
				String extension = FilenameUtils.getExtension(newName.trim());
				if (StringUtils.isNotEmpty(extension))
					document.setType(FilenameUtils.getExtension(newName));
				setUniqueFilename(document);
			}
			document.setIndexed(0);

			// Modify document history entry
			transaction.setUserId(user.getId());
			transaction.setUserName(user.getFullName());
			transaction.setEvent(History.EVENT_RENAMED);
			documentDAO.store(document, transaction);

			Version version = Version.create(document, user, "", Version.EVENT_RENAMED,
					Version.VERSION_TYPE.NEW_SUBVERSION);
			versionDAO.store(version);

			// Check if there are some shortcuts associated to the indexing
			// document. They must be re-indexed.
			List<Long> shortcutIds = documentDAO.findShortcutIds(document.getId());
			for (Long shortcutId : shortcutIds) {
				Document shortcutDoc = documentDAO.findById(shortcutId);
				shortcutDoc.setIndexed(0);
				documentDAO.store(shortcutDoc);
			}

			log.debug("Document filename renamed: " + document.getId());
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public Document createShortcut(Document doc, Menu folder, User user, History transaction) throws Exception {
		try {
			// initialize the document
			documentDAO.initialize(doc);

			Document shortcut = new Document();
			shortcut.setFolder(folder);
			shortcut.setFileName(doc.getFileName());
			shortcut.setDate(new Date());

			String fallbackTitle = doc.getFileName();
			String type = "unknown";
			int lastDotIndex = doc.getFileName().lastIndexOf(".");
			if (lastDotIndex > 0) {
				fallbackTitle = doc.getFileName().substring(0, lastDotIndex);
				type = doc.getFileName().substring(lastDotIndex + 1).toLowerCase();
			}

			if (StringUtils.isNotEmpty(doc.getTitle())) {
				shortcut.setTitle(doc.getTitle());
			} else {
				shortcut.setTitle(fallbackTitle);
			}

			setUniqueTitle(shortcut);
			setUniqueFilename(shortcut);

			if (doc.getSourceDate() != null)
				shortcut.setSourceDate(doc.getSourceDate());
			else
				shortcut.setSourceDate(shortcut.getDate());
			shortcut.setPublisher(user.getFullName());
			shortcut.setPublisherId(user.getId());
			shortcut.setCreator(user.getFullName());
			shortcut.setCreatorId(user.getId());
			shortcut.setStatus(Document.DOC_UNLOCKED);
			shortcut.setType(type);
			shortcut.setSource(doc.getSource());
			shortcut.setSourceAuthor(doc.getSourceAuthor());
			shortcut.setSourceType(doc.getSourceType());
			shortcut.setCoverage(doc.getCoverage());
			shortcut.setLocale(doc.getLocale());
			shortcut.setObject(doc.getObject());
			shortcut.setSourceId(doc.getSourceId());
			shortcut.setRecipient(doc.getRecipient());

			// Set the Doc Reference
			if (doc.getDocRef() == null) {
				// Set the docref as the id of the original document
				shortcut.setDocRef(doc.getId());
			} else {
				// The doc is a shortcut, so we still copy a shortcut
				shortcut.setDocRef(doc.getDocRef());
			}

			// Modify document history entry
			transaction.setUserId(user.getId());
			transaction.setUserName(user.getFullName());
			transaction.setEvent(History.EVENT_SHORTCUT_STORED);

			documentDAO.store(shortcut, transaction);

			return shortcut;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
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

	public void setConfig(PropertiesBean config) {
		this.config = config;
	}

	public void moveFolder(Menu folderToMove, Menu destParentFolder, User user, History transaction)
			throws Exception {
		
		// Occorre spostare la cartella
		folderToMove.setParentId(destParentFolder.getId());
		
		List<Long> foldersInvolved = new ArrayList<Long>();
		
//		System.out.println("destParentFolder ID: " + destParentFolder.getId());
//		System.out.println("destParentFolder Path: " + destParentFolder.getPath());
		
		String oldPath = new String(folderToMove.getPath());
//		System.out.println("folderToMove Before Path: " + oldPath);
		folderToMove.setPath(destParentFolder.getPath() + "/" + destParentFolder.getId());
		String newPath = folderToMove.getPath();
//		System.out.println("folderToMove After Path: " + newPath);
		
		// Ensure unique folder name in a folder
		menuDAO.setUniqueFolderName(folderToMove);
		
		// a) Salvo la cartella attuale
		
		// Modify folder history entry
		transaction.setUserId(user.getId());
		transaction.setUserName(user.getFullName());
		transaction.setEvent(History.EVENT_FOLDER_MOVED);
		
		menuDAO.store(folderToMove, transaction);
		
		foldersInvolved.add(folderToMove.getId());
		
		// b) E' necessario modificare tutti i path delle cartelle figlie; 
		// potrebbe essere fatto con una operazione SQL diretta
		List<Menu> childrenFolders = menuDAO.findByParentId(folderToMove.getId());
		
//		System.out.println("ChildrenFolders:");
		for (Menu childDir : childrenFolders) {
//			System.out.println(childDir.getId());
//			System.out.println(childDir.getText());
//			System.out.println("childDir.getPath(): " + childDir.getPath());
			
			String childPath = childDir.getPath();
			childPath = childPath.substring(oldPath.length(), childPath.length());
			childPath = newPath + childPath;
//			System.out.println("childPath: " + childPath);
			childDir.setPath(childPath);
			
//			System.out.println("Old PathExtended: " + childDir.getPathExtended());
			menuDAO.store(childDir);
//			System.out.println("new PathExtended: " + childDir.getPathExtended());
			// NOTA: è necessario modificare il path extended ??
			// Dai test eseguiti Sembra di NO. (03/04/2010)
			
			foldersInvolved.add(childDir.getId());
		}
		
//		System.out.println("END ChildrenFolders:");
	
		
		// 2) valutare se modificare tutti i path dei documenti contenuti;
		// NON è necessario perchè il path del documento è calcolato sulla base del folder parent
		
		// 3) Devo impostare tutti i documenti figli come da RE-indicizzare
		
		StringBuilder sb = new StringBuilder();
		for (Iterator iterator = foldersInvolved.iterator(); iterator.hasNext();) {
			Long folderId = (Long) iterator.next();
			sb.append(folderId);
			if (iterator.hasNext())
				sb.append(",");
		}
//		System.out.println("Folders ids: " + sb);
		
		documentDAO.bulkUpdate("set ld_indexed=0 where ld_folderid in (" +sb +")", null);
	}
}