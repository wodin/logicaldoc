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
	public void checkin(long docId, File file, String filename, VERSION_TYPE versionType, boolean immediateIndexing,
			History transaction) throws Exception {
		FileInputStream is = new FileInputStream(file);
		try {
			checkin(docId, is, filename, versionType, immediateIndexing, transaction);
		} finally {
			is.close();
		}
	}

	@Override
	public void checkin(long docId, InputStream fileInputStream, String filename, VERSION_TYPE versionType,
			boolean immediateIndexing, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);
		assert (transaction.getComment() != null);

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
			document.setPublisher(transaction.getUserName());
			document.setPublisherId(transaction.getUserId());
			document.setStatus(Document.DOC_UNLOCKED);
			document.setType(document.getFileExtension());
			document.setLockUserId(null);
			document.setFolder(folder);

			// create new version
			Version version = Version.create(document, transaction.getUser(), transaction.getComment(),
					Version.EVENT_CHECKIN, versionType);
			if (documentDAO.store(document) == false)
				throw new Exception();

			// Store the version
			versionDAO.store(version);
			log.debug("Stored version " + version.getVersion());

			// Modify document history entry
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
	public void checkout(long docId, History transaction) throws Exception {
		lock(docId, Document.DOC_CHECKED_OUT, transaction);
	}

	@Override
	public void lock(long docId, int status, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);

		Document document = documentDAO.findById(docId);
		if (document.getStatus() != Document.DOC_UNLOCKED)
			throw new Exception("Document is locked");

		document.setLockUserId(transaction.getUserId());
		document.setStatus(status);
		document.setFolder(document.getFolder());

		// Modify document history entry
		documentDAO.store(document, transaction);

		log.debug("locked document " + docId);
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
	public void update(Document doc, Document docVO, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);
		assert (doc != null);
		assert (docVO != null);
		try {
			if (doc.getImmutable() == 0) {
				History renameTransaction = null;
				if (!doc.getTitle().equals(docVO.getTitle()) && docVO.getTitle() != null) {
					renameTransaction = (History) transaction.clone();
					renameTransaction.setEvent(History.EVENT_RENAMED);
				}

				doc.setTitle(docVO.getTitle());
				doc.setSource(docVO.getSource());
				doc.setSourceId(docVO.getSourceId());
				doc.setObject(docVO.getObject());
				doc.setSourceAuthor(docVO.getSourceAuthor());
				doc.setSourceDate(docVO.getSourceDate());
				doc.setSourceType(docVO.getSourceType());
				doc.setCoverage(docVO.getCoverage());
				doc.setRecipient(docVO.getRecipient());

				// The document must be re-indexed
				doc.setIndexed(0);

				// Intercept locale changes
				doc.setLocale(docVO.getLocale());

				// Ensure unique title in folder
				setUniqueTitle(doc);

				doc.clearTags();
				documentDAO.store(doc);
				doc.setTags(docVO.getTags());

				DocumentTemplate template = docVO.getTemplate();
				if (template == null && docVO.getTemplateId() != null)
					template = documentTemplateDAO.findById(docVO.getTemplateId());

				// Change the template and attributes
				if (template != null) {
					if (docVO.getAttributes() != null) {
						doc.getAttributes().clear();
						for (String attrName : docVO.getAttributes().keySet()) {
							if (template.getAttributes().get(attrName) != null) {
								ExtendedAttribute templateExtAttribute = template.getAttributes().get(attrName);
								ExtendedAttribute docExtendedAttribute = docVO.getAttributes().get(attrName);
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
				Version version = Version.create(doc, transaction.getUser(), transaction.getComment(),
						Version.EVENT_CHANGED, Version.VERSION_TYPE.NEW_SUBVERSION);

				// Modify document history entry
				documentDAO.store(doc, transaction);
				if (renameTransaction != null) {
					renameTransaction.setUser(transaction.getUser());
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
	public void moveToFolder(Document doc, Menu folder, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);

		if (folder.equals(doc.getFolder()))
			return;

		if (doc.getImmutable() == 0) {
			documentDAO.initialize(doc);
			doc.setFolder(folder);
			setUniqueTitle(doc);
			setUniqueFilename(doc);

			// Modify document history entry
			if (transaction.getEvent().trim().isEmpty())
				transaction.setEvent(History.EVENT_MOVED);
			documentDAO.store(doc, transaction);

			Version version = Version.create(doc, transaction.getUser(), transaction.getComment(), Version.EVENT_MOVED,
					Version.VERSION_TYPE.NEW_SUBVERSION);
			versionDAO.store(version);

			if (doc.getIndexed() == 1) {
				Indexer indexer = (Indexer) Context.getInstance().getBean(Indexer.class);
				org.apache.lucene.document.Document indexDocument = null;
				indexDocument = indexer.getDocument(String.valueOf(doc.getId()), doc.getLocale());
				if (indexDocument != null) {
					indexDocument.removeField(LuceneDocument.FIELD_FOLDER_ID);
					indexDocument.add(new Field(LuceneDocument.FIELD_FOLDER_ID, Long.toString(doc.getFolder().getId()),
							Field.Store.YES, Field.Index.NOT_ANALYZED));
					indexer.addDocument(indexDocument, doc.getLocale());

					// Make the same operation for the shortcuts
					if (documentDAO.findShortcutIds(doc.getId()).size() > 0) {
						org.apache.lucene.document.Document shortcutIndexDocument = null;
						for (Long shortcutId : documentDAO.findShortcutIds(doc.getId())) {
							shortcutIndexDocument = indexer.getDocument(String.valueOf(shortcutId), doc.getLocale());
							if (shortcutIndexDocument != null) {
								shortcutIndexDocument.removeField(LuceneDocument.FIELD_FOLDER_ID);
								shortcutIndexDocument.add(new Field(LuceneDocument.FIELD_FOLDER_ID, Long.toString(doc
										.getFolder().getId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
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
	public Document create(File file, Document docVO, History transaction, boolean immediateIndexing) throws Exception {

		InputStream is = new FileInputStream(file);
		try {
			return create(is, docVO, transaction, immediateIndexing);
		} finally {
			is.close();
		}
	}

	@Override
	public Document create(InputStream content, Document docVO, History transaction, boolean immediateIndexing)
			throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);
		assert (transaction.getComment() != null);
		assert (docVO != null);
		assert (content != null);

		try {
			docVO.setDate(new Date());

			String fallbackTitle = docVO.getFileName();
			String type = "unknown";
			int lastDotIndex = docVO.getFileName().lastIndexOf(".");
			if (lastDotIndex > 0) {
				fallbackTitle = docVO.getFileName().substring(0, lastDotIndex);
				type = docVO.getFileName().substring(lastDotIndex + 1).toLowerCase();
			}

			if (StringUtils.isEmpty(docVO.getTitle()))
				docVO.setTitle(fallbackTitle);

			setUniqueTitle(docVO);
			setUniqueFilename(docVO);

			if (docVO.getSourceDate() == null)
				docVO.setSourceDate(docVO.getDate());
			docVO.setPublisher(transaction.getUserName());
			docVO.setPublisherId(transaction.getUserId());
			docVO.setCreator(transaction.getUserName());
			docVO.setCreatorId(transaction.getUserId());
			docVO.setStatus(Document.DOC_UNLOCKED);
			docVO.setType(type);
			docVO.setVersion(config.getProperty("document.startversion"));
			docVO.setFileVersion(docVO.getVersion());

			if (docVO.getTemplate() == null && docVO.getTemplateId() != null)
				docVO.setTemplate(documentTemplateDAO.findById(docVO.getTemplateId()));

			/* Set template and extended attributes */
			if (docVO.getTemplate() != null) {
				for (String attrName : docVO.getAttributeNames()) {
					if (docVO.getTemplate().getAttributes().get(attrName) != null) {
						ExtendedAttribute templateExtAttribute = docVO.getTemplate().getAttributes().get(attrName);
						ExtendedAttribute docExtendedAttribute = docVO.getExtendedAttribute(attrName);
						if (templateExtAttribute.getType() == docExtendedAttribute.getType()) {
							docVO.getAttributes().put(attrName, docExtendedAttribute);
						} else {
							throw new Exception("The given type value is not correct.");
						}
					}
				}
			}

			// Modify document history entry
			documentDAO.store(docVO, transaction);

			/* store the document into filesystem */
			store(docVO, content);

			File file = getDocumentFile(docVO);
			if (immediateIndexing) {
				/* create search index entry */
				Locale loc = docVO.getLocale();
				indexer.addFile(file, docVO, getDocumentContent(docVO), loc);
				docVO.setIndexed(1);
			}
			docVO.setFileSize(file.length());

			documentDAO.store(docVO);

			// Store the initial version (default 1.0)
			Version vers = Version.create(docVO, transaction.getUser(), transaction.getComment(), Version.EVENT_STORED,
					Version.VERSION_TYPE.OLD_VERSION);
			versionDAO.store(vers);

			log.debug("Stored version " + vers.getVersion());
			return docVO;
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

	public Document copyToFolder(Document doc, Menu folder, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);

		// initialize the document
		documentDAO.initialize(doc);
		if (doc.getDocRef() != null) {
			return createShortcut(doc, folder, transaction);
		}

		File sourceFile = storer.getFile(doc.getId(), doc.getFileVersion());

		InputStream is = new FileInputStream(sourceFile);
		try {
			Document cloned = (Document) doc.clone();
			cloned.setId(0);
			cloned.setFolder(folder);
			return create(is, cloned, transaction, false);
		} finally {
			is.close();
		}
	}

	@Override
	public void unlock(long docId, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);

		Document document = documentDAO.findById(docId);
		document.setLockUserId(null);
		document.setStatus(Document.DOC_UNLOCKED);

		// Modify document history entry
		transaction.setEvent(History.EVENT_UNLOCKED);
		documentDAO.store(document, transaction);

		log.debug("Unlocked document " + docId);
	}

	@Override
	public void makeImmutable(long docId, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);

		Document document = documentDAO.findById(docId);
		if (document.getImmutable() == 0) {
			// Modify document history entry
			transaction.setEvent(History.EVENT_IMMUTABLE);
			documentDAO.makeImmutable(docId, transaction);

			log.debug("The document " + docId + " has been marked as immutable ");
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public List<Menu> deleteFolder(Menu folder, History transaction) throws Exception {
		assert (folder != null);
		assert (transaction != null);
		assert (transaction.getUser() != null);

		List<Menu> deletableFolders = new ArrayList<Menu>();
		List<Menu> notDeletableFolders = new ArrayList<Menu>();
		List<Document> deletableDocs = new ArrayList<Document>();

		Set<Long> deletableIds = menuDAO.findMenuIdByUserIdAndPermission(transaction.getUserId(), Permission.DELETE,
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
			menuDAO.deleteAll(deletableFolders, transaction);
			documentDAO.deleteAll(deletableDocs, transaction);
			return notDeletableFolders;
		} catch (Throwable e) {
			log.error(e);
			return notDeletableFolders;
		}
	}

	@Override
	public void rename(Document doc, String newName, boolean title, History transaction) throws Exception {
		assert (doc != null);
		assert (transaction != null);
		assert (transaction.getUser() != null);

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
			transaction.setEvent(History.EVENT_RENAMED);
			documentDAO.store(document, transaction);

			Version version = Version.create(document, transaction.getUser(), transaction.getComment(),
					Version.EVENT_RENAMED, Version.VERSION_TYPE.NEW_SUBVERSION);
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
	public Document createShortcut(Document doc, Menu folder, History transaction) throws Exception {
		assert (doc != null);
		assert (folder != null);
		assert (transaction != null);
		assert (transaction.getUser() != null);

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
			shortcut.setPublisher(transaction.getUserName());
			shortcut.setPublisherId(transaction.getUserId());
			shortcut.setCreator(transaction.getUserName());
			shortcut.setCreatorId(transaction.getUserId());
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

	@Override
	public void moveFolder(Menu folderToMove, Menu destParentFolder, History transaction) throws Exception {
		assert (folderToMove != null);
		assert (destParentFolder != null);
		assert (transaction != null);
		assert (transaction.getUser() != null);

		// Change the parent folder
		folderToMove.setParentId(destParentFolder.getId());

		// Ensure unique folder name in a folder
		menuDAO.setUniqueFolderName(folderToMove);

		// Modify folder history entry
		transaction.setEvent(History.EVENT_FOLDER_MOVED);

		menuDAO.store(folderToMove, transaction);
	}
}