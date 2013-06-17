package com.logicaldoc.core.document;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.parser.Parser;
import com.logicaldoc.core.parser.ParserFactory;
import com.logicaldoc.core.searchengine.SearchEngine;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Basic Implementation of <code>DocumentManager</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5
 */
public class DocumentManagerImpl implements DocumentManager {

	protected static Logger log = LoggerFactory.getLogger(DocumentManagerImpl.class);

	private DocumentDAO documentDAO;

	private FolderDAO folderDAO;

	private DocumentTemplateDAO documentTemplateDAO;

	private DocumentListenerManager listenerManager;

	private VersionDAO versionDAO;

	private UserDAO userDAO;

	private SearchEngine indexer;

	private Storer storer;

	private ContextProperties config;

	public void setListenerManager(DocumentListenerManager listenerManager) {
		this.listenerManager = listenerManager;
	}

	public void setDocumentDAO(DocumentDAO documentDAO) {
		this.documentDAO = documentDAO;
	}

	public void setDocumentTemplateDAO(DocumentTemplateDAO documentTemplateDAO) {
		this.documentTemplateDAO = documentTemplateDAO;
	}

	public void setIndexer(SearchEngine indexer) {
		this.indexer = indexer;
	}

	@Override
	public void checkin(long docId, File file, String filename, boolean release, Document docVO, History transaction)
			throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);
		assert (transaction.getComment() != null);

		// identify the document and folder
		Document document = documentDAO.findById(docId);
		document.setComment(transaction.getComment());

		if (document.getImmutable() == 0) {
			documentDAO.initialize(document);

			// Check CustomId uniqueness
			if (docVO != null && docVO.getCustomId() != null) {
				Document test = documentDAO.findByCustomId(docVO.getCustomId());
				if (test != null && test.getId() != docId)
					throw new Exception("Duplicated CustomID");
			}

			/*
			 * Now apply the metadata, if any
			 */
			if (docVO != null)
				document.copyAttributes(docVO);

			Map<String, Object> dictionary = new HashMap<String, Object>();

			log.debug("Invoke listeners before checkin");
			for (DocumentListener listener : listenerManager.getListeners()) {
				listener.beforeCheckin(document, transaction, dictionary);
			}

			document.setIndexed(AbstractDocument.INDEX_TO_INDEX);
			document.setSigned(0);
			if (document.getBarcoded() != AbstractDocument.BARCODE_SKIP)
				document.setBarcoded(AbstractDocument.BARCODE_TO_PROCESS);

			documentDAO.store(document);
			document = documentDAO.findById(document.getId());
			Folder folder = document.getFolder();
			documentDAO.initialize(document);
			
			// create some strings containing paths
			document.setFileName(filename);
			document.setType(FilenameUtils.getExtension(filename));

			// set other properties of the document
			document.setDate(new Date());
			document.setPublisher(transaction.getUserName());
			document.setPublisherId(transaction.getUserId());
			document.setStatus(Document.DOC_UNLOCKED);
			document.setLockUserId(null);
			document.setFolder(folder);
			document.setDigest(null);
			document.setFileSize(file.length());
			document.setExtResId(null);
			
			// Create new version (a new version number is created)
			Version version = Version.create(document, transaction.getUser(), transaction.getComment(),
					Version.EVENT_CHECKIN, release);
			document.setStatus(Document.DOC_UNLOCKED);
			if (documentDAO.store(document, transaction) == false)
				throw new Exception("Errors saving document " + document.getId());

			// store the document in the repository (on the file system)
			store(document, file);

			version.setFileSize(document.getFileSize());
			version.setDigest(null);
			versionDAO.store(version);
			log.debug("Stored version " + version.getVersion());

			log.debug("Invoke listeners after store");
			for (DocumentListener listener : listenerManager.getListeners()) {
				listener.afterCheckin(document, transaction, dictionary);
			}

			log.debug("Checked in document " + docId);
		}
	}

	@Override
	public void checkin(long docId, InputStream content, String filename, boolean release, Document docVO,
			History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);
		assert (transaction.getComment() != null);

		// Write content to temporary file, then delete it
		File tmp = File.createTempFile("checkin", "");
		try {
			FileUtil.writeFile(content, tmp.getPath());
			checkin(docId, tmp, filename, release, docVO, transaction);
		} finally {
			FileUtils.deleteQuietly(tmp);
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

		documentDAO.initialize(document);
		document.setLockUserId(transaction.getUser().getId());
		document.setStatus(status);
		document.setFolder(document.getFolder());

		// Modify document history entry
		documentDAO.store(document, transaction);

		log.debug("locked document " + docId);
	}

	private long store(Document doc, File file) throws IOException {
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);

		// Prepare the inputStream
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file), 2048);
		} catch (FileNotFoundException e) {
			return -1;
		}

		// stores it
		long stored = storer.store(is, doc.getId(), storer.getResourceName(doc, null, null));
		if (stored < 0)
			throw new IOException("Unable to store the document");

		return stored;
	}

	/**
	 * Utility method for document removal from index
	 * 
	 * @param doc
	 */
	@Override
	public void deleteFromIndex(Document doc) {
		try {
			long docId = doc.getId();

			// Physically remove the document from full-text index
			if (doc != null) {
				indexer.deleteHit(docId);
			}

			doc.setIndexed(AbstractDocument.INDEX_TO_INDEX);
			documentDAO.store(doc);

			// Check if there are some shortcuts associated to the indexing
			// document. They must be re-indexed.
			List<Long> shortcutIds = documentDAO.findShortcutIds(doc.getId());
			for (Long shortcutId : shortcutIds) {
				Document shortcutDoc = documentDAO.findById(shortcutId);
				indexer.deleteHit(shortcutId);
				shortcutDoc.setIndexed(AbstractDocument.INDEX_TO_INDEX);
				documentDAO.store(shortcutDoc);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Retrieves the document's content as a string
	 * 
	 * @param doc The document representation
	 * @return The document's content
	 */
	private String parseDocument(Document doc) {
		String content = null;

		// Check if the document is a shortcut
		if (doc.getDocRef() != null) {
			long docId = doc.getDocRef();
			doc = documentDAO.findById(docId);
		}

		// Parses the file where it is already stored
		Locale locale = doc.getLocale();
		String resource = storer.getResourceName(doc, null, null);
		Parser parser = ParserFactory.getParser(storer.getStream(doc.getId(), resource), doc.getFileName(), locale,
				null);

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
	public void reindex(long docId) throws Exception {
		Document doc = documentDAO.findById(docId);

		// If the 'doc' is a shortcut, it must not be re-indexed, because it is
		// re-indexed when it is analyzed the referenced doc
		if (doc == null || doc.getDocRef() != null)
			return;

		log.debug("Reindexing document " + docId + " - " + doc.getTitle());

		// Extract the content from the file. This may take very long time.
		String content = parseDocument(doc);

		// Check if there are some shortcuts associated to the indexing
		// document. They must be re-indexed.
		List<Long> shortcutIds = documentDAO.findShortcutIds(doc.getId());
		for (Long shortcutId : shortcutIds) {
			Document shortcutDoc = documentDAO.findById(shortcutId);
			shortcutDoc.setIndexed(AbstractDocument.INDEX_TO_INDEX);
			documentDAO.store(shortcutDoc);
		}

		// Add the document to the index (lucene 2.x doesn't support the update
		// operation)
		String resource = storer.getResourceName(doc.getId(), null, null);

		doc = documentDAO.findById(docId);
		documentDAO.initialize(doc);

		// This may take time
		indexer.addHit(doc, content);

		// For additional security update the DB directly
		documentDAO.jdbcUpdate("update ld_document set ld_indexed=" + AbstractDocument.INDEX_INDEXED + " where ld_id="
				+ docId);
		doc = documentDAO.findById(docId);

		for (Long shortcutId : shortcutIds) {
			try {
				Document shortcutDoc = documentDAO.findById(shortcutId);
				indexer.addHit(shortcutDoc, storer.getStream(doc.getId(), resource));

				// For additional security update the DB directly
				documentDAO.jdbcUpdate("update ld_document set ld_indexed=" + AbstractDocument.INDEX_INDEXED
						+ " where ld_id=" + shortcutId);
				shortcutDoc = documentDAO.findById(shortcutId);
			} catch (Throwable t) {

			}
		}
	}

	@Override
	public void update(Document doc, Document docVO, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);
		assert (doc != null);
		assert (docVO != null);
		try {
			documentDAO.initialize(doc);
			if (doc.getImmutable() == 0 || ((doc.getImmutable() == 1 && transaction.getUser().isInGroup("admin")))) {
				History renameTransaction = null;
				if (!doc.getTitle().equals(docVO.getTitle()) && docVO.getTitle() != null) {
					renameTransaction = (History) transaction.clone();
					renameTransaction.setFilenameOld(doc.getFileName());
					renameTransaction.setTitleOld(doc.getTitle());
					renameTransaction.setEvent(DocumentEvent.RENAMED.toString());
				}

				// Check CustomId uniqueness
				if (docVO.getCustomId() != null) {
					Document test = documentDAO.findByCustomId(docVO.getCustomId());
					if (test != null && test.getId() != doc.getId())
						throw new Exception("Duplicated CustomID");
					doc.setCustomId(docVO.getCustomId());
				}

				if (StringUtils.isNotEmpty(docVO.getFileName()))
					doc.setFileName(docVO.getFileName());
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
				doc.setIndexed(AbstractDocument.INDEX_TO_INDEX);
				doc.setBarcoded(docVO.getBarcoded());
				doc.setWorkflowStatus(docVO.getWorkflowStatus());

				// Save retention policies
				doc.setPublished(docVO.getPublished());
				doc.setStartPublishing(docVO.getStartPublishing());
				doc.setStopPublishing(docVO.getStopPublishing());

				// Intercept locale changes
				if (!doc.getLocale().equals(docVO.getLocale())) {
					indexer.deleteHit(doc.getId());
					doc.setLocale(docVO.getLocale());
				}

				// Ensure unique title in folder
				setUniqueTitleAndFilename(doc);

				doc.clearTags();
				doc.setTags(docVO.getTags());

				DocumentTemplate template = docVO.getTemplate();
				if (template == null && docVO.getTemplateId() != null)
					template = documentTemplateDAO.findById(docVO.getTemplateId());

				// Change the template and attributes
				if (template != null) {
					doc.setTemplate(template);
					doc.setTemplateId(template.getId());
					if (docVO.getAttributes() != null) {
						doc.getAttributes().clear();
						for (String attrName : docVO.getAttributes().keySet()) {
							if (template.getAttributes().get(attrName) != null) {
								ExtendedAttribute templateExtAttribute = template.getAttributes().get(attrName);
								ExtendedAttribute docExtendedAttribute = docVO.getAttributes().get(attrName);
								docExtendedAttribute.setMandatory(templateExtAttribute.getMandatory());
								docExtendedAttribute.setLabel(templateExtAttribute.getLabel());
								if (templateExtAttribute.getType() == docExtendedAttribute.getType()) {
									doc.getAttributes().put(attrName, docExtendedAttribute);
								} else {
									throw new Exception("The given type value is not correct for attribute: "
											+ attrName);
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
						Version.EVENT_CHANGED, false);

				// Modify document history entry
				doc.setVersion(version.getVersion());
				if (renameTransaction != null) {
					renameTransaction.setUser(transaction.getUser());
					documentDAO.store(doc, renameTransaction);
				} else {
					documentDAO.store(doc, transaction);
				}
				versionDAO.store(version);

				// Check if there are some shortcuts associated to the indexing
				// document. They must be re-indexed.
				List<Long> shortcutIds = documentDAO.findShortcutIds(doc.getId());
				for (Long shortcutId : shortcutIds) {
					Document shortcutDoc = documentDAO.findById(shortcutId);
					shortcutDoc.setIndexed(AbstractDocument.INDEX_TO_INDEX);
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

	@Override
	public void moveToFolder(Document doc, Folder folder, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);

		if (folder.equals(doc.getFolder()))
			return;

		if (doc.getImmutable() == 0 || ((doc.getImmutable() == 1 && transaction.getUser().isInGroup("admin")))) {
			documentDAO.initialize(doc);
			transaction.setPathOld(folderDAO.computePathExtended(doc.getFolder().getId()));
			transaction.setFilenameOld(doc.getFileName());
			transaction.setTitleOld(doc.getTitle());
			doc.setFolder(folder);
			setUniqueTitleAndFilename(doc);

			// The document needs to be reindexed
			if (doc.getIndexed() == AbstractDocument.INDEX_INDEXED) {
				doc.setIndexed(AbstractDocument.INDEX_TO_INDEX);
				indexer.deleteHit(doc.getId());

				// The same thing should be done on each shortcut
				documentDAO.jdbcUpdate("update ld_document set ld_indexed=" + AbstractDocument.INDEX_TO_INDEX
						+ " where ld_docref=" + doc.getId());
			}

			// To avoid 'optimistic locking failed' exceptions.
			// Perhaps no more needed with Hibernate 3.6.9
			// doc.setLastModified(new Date());

			// Modify document history entry
			if (transaction.getEvent().trim().isEmpty())
				transaction.setEvent(DocumentEvent.MOVED.toString());
			documentDAO.store(doc, transaction);

			Version version = Version.create(doc, transaction.getUser(), transaction.getComment(), Version.EVENT_MOVED,
					false);
			versionDAO.store(version);
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public Document create(File file, Document docVO, History transaction) throws Exception {
		assert (transaction != null);
		assert (docVO != null);
		assert (file != null);

		boolean documentSaved = false;
		try {
			String fallbackTitle = docVO.getFileName();
			String type = "unknown";
			int lastDotIndex = docVO.getFileName().lastIndexOf(".");
			if (lastDotIndex > 0) {
				fallbackTitle = docVO.getFileName().substring(0, lastDotIndex);
				type = docVO.getFileName().substring(lastDotIndex + 1).toLowerCase();
			}

			if (StringUtils.isEmpty(docVO.getTitle()))
				docVO.setTitle(fallbackTitle);

			setUniqueTitleAndFilename(docVO);

			if (docVO.getDate() == null)
				docVO.setDate(new Date());

			if (docVO.getSourceDate() == null)
				docVO.setSourceDate(docVO.getDate());

			if (docVO.getCreation() == null)
				docVO.setCreation(docVO.getDate());

			if (StringUtils.isNotEmpty(docVO.getPublisher()))
				docVO.setPublisher(docVO.getPublisher());
			else
				docVO.setPublisher(transaction.getUserName());

			if (docVO.getPublisherId() != 0L)
				docVO.setPublisherId(docVO.getPublisherId());
			else
				docVO.setPublisherId(transaction.getUserId());

			if (StringUtils.isNotEmpty(docVO.getCreator()))
				docVO.setCreator(docVO.getCreator());
			else
				docVO.setCreator(transaction.getUserName());

			if (docVO.getCreatorId() != 0L)
				docVO.setCreatorId(docVO.getCreatorId());
			else
				docVO.setCreatorId(transaction.getUserId());

			docVO.setStatus(Document.DOC_UNLOCKED);
			docVO.setType(type);
			docVO.setVersion(config.getProperty("document.startversion"));
			docVO.setFileVersion(docVO.getVersion());
			docVO.setFileSize(file.length());

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

			docVO.setId(0L);

			// Create the record
			documentSaved = documentDAO.store(docVO, transaction);

			if (documentSaved) {
				/* store the document into filesystem */
				try {
					store(docVO, file);
				} catch (Exception e) {
					documentDAO.delete(docVO.getId());
				}

				// Store the initial version (default 1.0)
				Version vers = Version.create(docVO, userDAO.findById(transaction.getUserId()),
						transaction.getComment(), Version.EVENT_STORED, true);
				versionDAO.store(vers);

				log.debug("Stored version " + vers.getVersion());
				return docVO;
			} else
				throw new Exception("Document not stored in the database");
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new Exception(e);
		}
	}

	@Override
	public Document create(InputStream content, Document docVO, History transaction) throws Exception {
		assert (transaction != null);
		assert (docVO != null);
		assert (content != null);

		// Write content to temporary file, then delete it
		File tmp = File.createTempFile("create", "");
		try {
			FileUtil.writeFile(content, tmp.getPath());
			return create(tmp, docVO, transaction);
		} finally {
			FileUtils.deleteQuietly(tmp);
		}
	}

	/**
	 * Avoid title and file name duplications in the same folder
	 */
	private void setUniqueTitleAndFilename(Document doc) {
		String originalTitle = doc.getTitle();
		String originalFileName = doc.getFileName();

		if (doc.getFileName().indexOf(".") != -1) {
			originalFileName = doc.getFileName().substring(0, doc.getFileName().lastIndexOf("."));
		}

		String ext = "";
		if (doc.getFileName().indexOf(".") != -1) {
			ext = doc.getFileName().substring(doc.getFileName().lastIndexOf("."));
		}

		/*
		 * These sets will contain the found collisions in the given folder
		 */
		final Set<String> titles = new HashSet<String>();
		final Set<String> files = new HashSet<String>();

		StringBuffer query = new StringBuffer(
				"select lower(ld_title), lower(ld_filename) from ld_document where ld_deleted=0 and ld_folderid=");
		query.append(Long.toString(doc.getFolder().getId()));
		query.append(" and (lower(ld_title) like '");
		query.append(SqlUtil.doubleQuotes(originalTitle.toLowerCase()));
		query.append("%' or lower(ld_filename) like '");
		query.append(SqlUtil.doubleQuotes(doc.getFileName().toLowerCase()));
		query.append("%') and not ld_id=");
		query.append(Long.toString(doc.getId()));

		// Execute the query to populate the sets
		SqlRowSet rs = documentDAO.queryForRowSet(query.toString(), null, null);
		while (rs.next()) {
			String title = rs.getString(1);
			String file = rs.getString(2);

			if (!titles.contains(title))
				titles.add(title);
			if (!files.contains(file))
				files.add(file);
		}

		int counter = 1;
		while (titles.contains(doc.getTitle().toLowerCase())) {
			doc.setTitle(originalTitle + "(" + (counter++) + ")");
		}

		counter = 1;
		while (files.contains(doc.getFileName().toLowerCase())) {
			doc.setFileName(originalFileName + "(" + (counter++) + ")" + ext);
		}
	}

	public Document copyToFolder(Document doc, Folder folder, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);

		// initialize the document
		documentDAO.initialize(doc);

		if (doc.getDocRef() != null) {
			return createShortcut(doc, folder, transaction);
		}

		String resource = storer.getResourceName(doc, null, null);
		InputStream is = storer.getStream(doc.getId(), resource);
		try {
			Document cloned = (Document) doc.clone();
			cloned.setId(0);
			cloned.setFolder(folder);
			cloned.setLastModified(null);
			if (cloned.getIndexed() == Document.INDEX_INDEXED)
				cloned.setIndexed(Document.INDEX_TO_INDEX);
			return create(is, cloned, transaction);
		} finally {
			if (is != null)
				is.close();
			is = null;
		}
	}

	@Override
	public void unlock(long docId, History transaction) throws Exception {
		assert (transaction != null);
		assert (transaction.getUser() != null);

		Document document = documentDAO.findById(docId);
		documentDAO.initialize(document);
		document.setLockUserId(null);
		document.setExtResId(null);
		document.setStatus(Document.DOC_UNLOCKED);
		if (transaction.getUser().isInGroup("admin")) {
			document.setImmutable(0);
		}

		// Modify document history entry
		transaction.setEvent(DocumentEvent.UNLOCKED.toString());
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
			transaction.setEvent(DocumentEvent.IMMUTABLE.toString());
			documentDAO.makeImmutable(docId, transaction);

			log.debug("The document " + docId + " has been marked as immutable ");
		} else {
			throw new Exception("Document is immutable");
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

		if (document.getImmutable() == 0 || ((doc.getImmutable() == 1 && transaction.getUser().isInGroup("admin")))) {
			documentDAO.initialize(document);
			if (title) {
				document.setTitle(newName);
			} else {
				document.setFileName(newName.trim());
				String extension = FilenameUtils.getExtension(newName.trim());
				if (StringUtils.isNotEmpty(extension)) {
					document.setType(FilenameUtils.getExtension(newName));
				} else {
					document.setType("unknown");
				}
			}

			setUniqueTitleAndFilename(document);
			document.setIndexed(AbstractDocument.INDEX_TO_INDEX);

			// Modify document history entry
			transaction.setEvent(DocumentEvent.RENAMED.toString());
			documentDAO.store(document, transaction);

			Version version = Version.create(document, transaction.getUser(), transaction.getComment(),
					Version.EVENT_RENAMED, false);
			versionDAO.store(version);

			// Check if there are some shortcuts associated to the indexing
			// document. They must be re-indexed.
			List<Long> shortcutIds = documentDAO.findShortcutIds(document.getId());
			for (Long shortcutId : shortcutIds) {
				Document shortcutDoc = documentDAO.findById(shortcutId);
				documentDAO.initialize(shortcutDoc);
				shortcutDoc.setIndexed(AbstractDocument.INDEX_TO_INDEX);
				documentDAO.store(shortcutDoc);
			}

			log.debug("Document filename renamed: " + document.getId());
		} else {
			throw new Exception("Document is immutable");
		}
	}

	@Override
	public Document createShortcut(Document doc, Folder folder, History transaction) throws Exception {
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

			setUniqueTitleAndFilename(shortcut);

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
			transaction.setEvent(DocumentEvent.SHORTCUT_STORED.toString());

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

	public void setStorer(Storer storer) {
		this.storer = storer;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}

	@Override
	public void changeIndexingStatus(Document doc, int status) {
		assert (status != AbstractDocument.INDEX_INDEXED);

		if (status == AbstractDocument.INDEX_SKIP && doc.getIndexed() == AbstractDocument.INDEX_SKIP)
			return;
		if (status == AbstractDocument.INDEX_TO_INDEX
				&& (doc.getIndexed() == AbstractDocument.INDEX_TO_INDEX || doc.getIndexed() == AbstractDocument.INDEX_INDEXED))
			return;

		documentDAO.initialize(doc);
		if (status == AbstractDocument.INDEX_SKIP && doc.getIndexed() == AbstractDocument.INDEX_INDEXED)
			deleteFromIndex(doc);
		doc.setIndexed(status);
		documentDAO.store(doc);
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setFolderDAO(FolderDAO folderDAO) {
		this.folderDAO = folderDAO;
	}

	@Override
	public void deleteVersion(long versionId) throws Exception {
		Version version = versionDAO.findById(versionId);
		assert (version != null);

		Document document = documentDAO.findById(version.getDocId());
		assert (document != null);

		/*
		 * Security check to avoid to delete the current version
		 */
		String currentVersion = document.getVersion();
		if (currentVersion.equals(version.getVersion()))
			return;

		// Iterate over the versions to check if the file is referenced by other
		// versions
		List<Version> versions = versionDAO.findByDocId(version.getDocId());
		boolean referenced = false;
		for (Version v : versions)
			if (v.getId() != versionId && version.getFileVersion().equals(v.getFileVersion())) {
				referenced = true;
				break;
			}

		// If no more referenced, can delete the document's resources
		if (!referenced) {
			List<String> resources = storer.listResources(version.getDocId(), version.getFileVersion());
			for (String resource : resources)
				try {
					storer.delete(version.getDocId(), resource);
				} catch (Throwable t) {
					log.warn("Unable to delete resource " + resource + " od document " + version.getDocId());
				}
		}

		versionDAO.delete(versionId);
	}
}