package com.logicaldoc.webservice.document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailAttachment;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.document.pdf.PdfConverterManager;
import com.logicaldoc.core.searchengine.SearchEngine;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.webservice.AbstractService;

/**
 * Document Web Service Implementation (SOAP)
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class DocumentServiceImpl extends AbstractService implements DocumentService {

	protected static Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Override
	public WSDocument create(String sid, WSDocument document, DataHandler content) throws Exception {
		User user = validateSession(sid);
		checkWriteEnable(user, document.getFolderId());

		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = fdao.findById(document.getFolderId());

		long rootId = fdao.findRoot(user.getTenantId()).getId();

		if (folder == null) {
			log.error("Folder " + document.getFolderId() + " not found");
			throw new Exception("error - folder not found");
		} else if (folder.getId() == rootId) {
			log.error("Cannot add documents in the root");
			throw new Exception("Cannot add documents in the root");
		}
		fdao.initialize(folder);

		Document doc = document.toDocument();
		doc.setTenantId(user.getTenantId());

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.STORED.toString());
		transaction.setComment(document.getComment());
		transaction.setUser(user);

		// Get file to upload inputStream
		InputStream stream = content.getInputStream();

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		doc = documentManager.create(stream, doc, transaction);
		return WSDocument.fromDocument(doc);
	}

	@Override
	public void checkin(String sid, long docId, String comment, String filename, boolean release, DataHandler content)
			throws Exception {
		User user = validateSession(sid);
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = ddao.findById(docId);
		if (document.getImmutable() == 1)
			throw new Exception("The document is immutable");
		Folder folder = document.getFolder();

		checkWriteEnable(user, folder.getId());

		if (document.getStatus() == Document.DOC_CHECKED_OUT
				&& (user.getId() == document.getLockUserId() || user.isInGroup("admin"))) {
			try {
				// Get file to upload inputStream
				InputStream stream = content.getInputStream();

				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(DocumentEvent.CHECKEDIN.toString());
				transaction.setUser(user);
				transaction.setComment(comment);

				// checkin the document; throws an exception if
				// something goes wrong
				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				documentManager.checkin(document.getId(), stream, filename, release, null, transaction);

				/* create positive log message */
				log.info("Document " + docId + " checked in");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new Exception(e);
			}
		} else {
			throw new Exception("document not checked in");
		}
	}

	@Override
	public void checkout(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		if (doc.getImmutable() == 1)
			throw new Exception("The document is immutable");

		checkWriteEnable(user, doc.getFolder().getId());
		checkDownloadEnable(user, doc.getFolder().getId());
		checkPublished(user, doc);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.CHECKEDOUT.toString());
		transaction.setComment("");
		transaction.setUser(user);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		documentManager.checkout(docId, transaction);
	}

	@Override
	public void delete(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		checkLocked(user, doc);
		checkPermission(Permission.DELETE, user, doc.getFolder().getId());
		checkPublished(user, doc);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.DELETED.toString());
		transaction.setComment("");
		transaction.setUser(user);

		docDao.delete(docId, transaction);
	}

	private void checkLocked(User user, Document doc) throws Exception {
		if (user.isInGroup("admin"))
			return;

		if (doc.getImmutable() == 1)
			throw new Exception("The document " + doc.getId() + " is immutable");

		if (doc.getStatus() != Document.DOC_UNLOCKED && user.getId() != doc.getLockUserId())
			throw new Exception("The document " + doc.getId() + " is locked");
	}

	@Override
	public DataHandler getContent(String sid, long docId) throws Exception {
		return getVersionContent(sid, docId, null);
	}

	@Override
	public DataHandler getVersionContent(String sid, long docId, String version) throws Exception {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);

		if (doc.getDocRef() != null)
			doc = docDao.findById(doc.getDocRef());

		String fileVersion = null;
		if (version != null) {
			VersionDAO vDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
			Version v = vDao.findByVersion(docId, version);
			fileVersion = v.getFileVersion();
		}

		return getResource(sid, docId, fileVersion, null);
	}

	@Override
	public DataHandler getResource(String sid, long docId, String fileVersion, String suffix) throws Exception {
		User user = validateSession(sid);
		try {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Document doc = docDao.findById(docId);
			checkReadEnable(user, doc.getFolder().getId());
			checkDownloadEnable(user, doc.getFolder().getId());
			checkPublished(user, doc);

			if (doc.getDocRef() != null)
				doc = docDao.findById(doc.getDocRef());

			Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
			String resourceName = storer.getResourceName(doc, fileVersion, suffix);

			if (!storer.exists(doc.getId(), resourceName)) {
				throw new FileNotFoundException(resourceName);
			}

			log.debug("Attach file " + resourceName);

			// Now we can append the 'document' attachment to the response
			byte[] bytes = storer.getBytes(doc.getId(), resourceName);

			String mime = "application/octet-stream";
			try {
				MagicMatch match = Magic.getMagicMatch(bytes, true);
				mime = match.getMimeType();
			} catch (Throwable t) {

			}

			DataHandler content = new DataHandler(new ByteArrayDataSource(bytes, mime));
			return content;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void createPdf(String sid, long docId, String fileVersion) throws Exception {
		User user = validateSession(sid);
		try {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Document doc = docDao.findById(docId);
			checkReadEnable(user, doc.getFolder().getId());

			if (doc.getDocRef() != null)
				doc = docDao.findById(doc.getDocRef());

			PdfConverterManager manager = (PdfConverterManager) Context.getInstance()
					.getBean(PdfConverterManager.class);
			manager.createPdf(doc, fileVersion);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public void uploadResource(String sid, long docId, String fileVersion, String suffix, DataHandler content)
			throws Exception {
		User user = validateSession(sid);

		try {
			if (StringUtils.isEmpty(suffix))
				throw new Exception("Please provide a suffix");

			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			Document doc = docDao.findById(docId);
			checkReadEnable(user, doc.getFolder().getId());
			checkWriteEnable(user, doc.getFolder().getId());

			if (doc.getDocRef() != null)
				doc = docDao.findById(doc.getDocRef());

			if (doc.getImmutable() == 1)
				throw new Exception("The document is immutable");

			if ("sign.p7m".equals(suffix.toLowerCase()))
				throw new Exception("You cannot upload a signature");

			Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
			String resource = storer.getResourceName(doc, fileVersion, suffix);

			log.debug("Attach file " + resource);

			storer.store(content.getInputStream(), doc.getId(), resource);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public WSDocument getDocument(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		if (doc == null)
			return null;
		checkReadEnable(user, doc.getFolder().getId());
		checkPublished(user, doc);

		docDao.initialize(doc);
		return WSDocument.fromDocument(doc);
	}

	@Override
	public WSDocument getDocumentByCustomId(String sid, String customId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findByCustomId(customId, user.getTenantId());
		if (doc == null)
			return null;
		checkReadEnable(user, doc.getFolder().getId());
		checkPublished(user, doc);

		docDao.initialize(doc);
		return WSDocument.fromDocument(doc);
	}

	@Override
	public boolean isReadable(String sid, long docId) throws Exception {
		User user = validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		if (doc == null)
			return false;

		checkReadEnable(user, doc.getFolder().getId());
		return true;
	}

	@Override
	public void lock(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		checkLocked(user, doc);
		checkWriteEnable(user, doc.getFolder().getId());
		checkPublished(user, doc);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.LOCKED.toString());
		transaction.setComment("");
		transaction.setUser(user);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		documentManager.lock(docId, Document.DOC_LOCKED, transaction);
	}

	@Override
	public void move(String sid, long docId, long folderId) throws Exception {
		User user = validateSession(sid);

		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		long rootId = fdao.findRoot(user.getTenantId()).getId();

		if (folderId == rootId) {
			log.error("Cannot move documents in the root");
			throw new Exception("Cannot move documents in the root");
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		checkPermission(Permission.DELETE, user, doc.getFolder().getId());
		checkPublished(user, doc);

		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = dao.findById(folderId);
		checkLocked(user, doc);
		checkWriteEnable(user, folder.getId());

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.MOVED.toString());
		transaction.setComment("");
		transaction.setUser(user);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		documentManager.moveToFolder(doc, folder, transaction);
	}

	@Override
	public void rename(String sid, long docId, String name) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		checkPermission(Permission.RENAME, user, doc.getFolder().getId());
		checkPublished(user, doc);

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setUser(user);
		manager.rename(doc, name, true, transaction);
	}

	@Override
	public void renameFile(String sid, long docId, String name) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		checkPermission(Permission.RENAME, user, doc.getFolder().getId());
		checkPublished(user, doc);
		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setUser(user);
		manager.rename(doc, name, false, transaction);
	}

	@Override
	public void restore(String sid, long docId, long folderId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

		History transaction = new History();
		transaction.setUser(user);
		transaction.setSessionId(sid);
		docDao.restore(docId, folderId, transaction);
	}

	@Override
	public void unlock(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		checkLocked(user, doc);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.UNLOCKED.toString());
		transaction.setComment("");
		transaction.setUser(user);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		documentManager.unlock(docId, transaction);
	}

	@Override
	public void update(String sid, WSDocument document) throws Exception {
		updateDocument(sid, document);
	}

	private void updateDocument(String sid, WSDocument document) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(document.getId());
		if (doc == null)
			throw new Exception("unexisting document " + document.getId());
		checkLocked(user, doc);
		checkWriteEnable(user, doc.getFolder().getId());
		checkPublished(user, doc);

		// Initialize the lazy loaded collections
		docDao.initialize(doc);
		doc.setCustomId(document.getCustomId());

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.CHANGED.toString());
		transaction.setComment(document.getComment());
		transaction.setUser(user);

		manager.update(doc, document.toDocument(), transaction);
	}

	@Override
	public WSDocument[] listDocuments(String sid, long folderId, String fileName) throws Exception {
		User user = validateSession(sid);
		checkReadEnable(user, folderId);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = docDao.findByFolder(folderId, null);
		if (docs.size() > 1)
			Collections.sort(docs, new Comparator<Document>() {
				@Override
				public int compare(Document o1, Document o2) {
					return o1.getTitle().compareTo(o2.getTitle());
				}
			});

		List<WSDocument> wsDocs = new ArrayList<WSDocument>();
		for (Document doc : docs) {
			try {
				checkPublished(user, doc);
				checkArchived(doc);
			} catch (Throwable t) {
				continue;
			}

			if (fileName != null && !FileUtil.matches(doc.getFileName(), new String[] { fileName }, null))
				continue;

			docDao.initialize(doc);
			wsDocs.add(WSDocument.fromDocument(doc));
		}

		return wsDocs.toArray(new WSDocument[0]);
	}

	@Override
	public WSDocument[] list(String sid, long folderId) throws Exception {
		return listDocuments(sid, folderId, null);
	}

	@Override
	public WSDocument[] getDocuments(String sid, Long[] docIds) throws Exception {
		User user = validateSession(sid);
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Collection<Long> folderIds = fdao.findFolderIdByUserId(user.getId(), null, true);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = docDao.findByIds(docIds, null);
		List<WSDocument> wsDocs = new ArrayList<WSDocument>();
		for (int i = 0; i < docs.size(); i++) {
			try {
				checkPublished(user, docs.get(i));
				checkArchived(docs.get(i));
			} catch (Throwable t) {
				continue;
			}
			docDao.initialize(docs.get(i));
			if (folderIds.contains(docs.get(i).getFolder().getId()))
				wsDocs.add(WSDocument.fromDocument(docs.get(i)));
		}

		return wsDocs.toArray(new WSDocument[0]);
	}

	@Override
	public WSDocument[] getVersions(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		if (doc == null)
			throw new Exception("unexisting document " + docId);

		checkReadEnable(user, doc.getFolder().getId());
		checkPublished(user, doc);

		VersionDAO versDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		List<Version> versions = versDao.findByDocId(docId);
		WSDocument[] wsVersions = new WSDocument[versions.size()];
		for (int i = 0; i < versions.size(); i++) {
			versDao.initialize(versions.get(i));
			wsVersions[i] = WSDocument.fromDocument(versions.get(i));
			wsVersions[i].setComment(versions.get(i).getComment());
		}

		return wsVersions;
	}

	@Override
	public WSDocument[] getRecentDocuments(String sid, Integer max) throws Exception {
		User user = validateSession(sid);

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		StringBuffer query = new StringBuffer(
				"select docId from History where deleted=0 and (docId is not NULL) and userId=" + user.getId());
		query.append(" order by date desc");
		List<Object> records = (List<Object>) dao.findByQuery(query.toString(), null, max);

		Set<Long> docIds = new HashSet<Long>();

		/*
		 * Iterate over records composing the response XML document
		 */
		for (Object record : records) {
			Long id = (Long) record;
			// Discard a record if already visited
			if (docIds.contains(id))
				continue;
			else
				docIds.add(id);
		}

		return getDocuments(sid, docIds.toArray(new Long[0]));
	}

	@Override
	public void sendEmail(String sid, Long[] docIds, String recipients, String subject, String message)
			throws Exception {
		User user = validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		EMail mail;
		try {
			mail = new EMail();
			mail.setTenantId(user.getTenantId());
			mail.setAccountId(-1);
			mail.setAuthor(user.getUserName());
			mail.setAuthorAddress(user.getEmail());
			mail.parseRecipients(recipients);
			for (Recipient recipient : mail.getRecipients()) {
				recipient.setRead(1);
			}
			mail.setFolder("outbox");
			mail.setMessageText(message);
			mail.setSentDate(new Date());
			mail.setSubject(subject);
			mail.setUserName(user.getUserName());

			/*
			 * Only readable documents can be sent
			 */
			List<Document> docs = new ArrayList<Document>();
			if (docIds != null && docIds.length > 0) {
				for (long id : docIds) {
					Document doc = docDao.findById(id);
					try {
						checkPublished(user, doc);
					} catch (Throwable t) {
						continue;
					}

					if (doc != null && folderDao.isReadEnable(doc.getFolder().getId(), user.getId())) {
						createAttachment(mail, doc);
						docs.add(doc);
					}
				}
			}

			// Send the message
			EMailSender sender = new EMailSender(user.getTenantId());
			sender.send(mail);

			for (Document doc : docs) {
				try {
					checkPublished(user, doc);
				} catch (Throwable t) {
					continue;
				}

				// Create the document history event
				HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
				History history = new History();
				history.setSessionId(sid);
				history.setDocId(doc.getId());
				history.setEvent(DocumentEvent.SENT.toString());
				history.setUser(user);
				history.setComment(StringUtils.abbreviate(recipients, 4000));
				history.setTitle(doc.getTitle());
				history.setVersion(doc.getVersion());
				history.setPath(folderDao.computePathExtended(doc.getFolder().getId()));
				dao.store(history);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	private void createAttachment(EMail email, Document doc) throws IOException {
		EMailAttachment att = new EMailAttachment();
		att.setIcon(doc.getIcon());
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
		String resource = storer.getResourceName(doc, null, null);
		att.setData(storer.getBytes(doc.getId(), resource));
		att.setFileName(doc.getFileName());
		String extension = doc.getFileExtension();
		att.setMimeType(MimeType.get(extension));

		if (att != null) {
			email.addAttachment(2 + email.getAttachments().size(), att);
		}
	}

	@Override
	public WSDocument createAlias(String sid, long docId, long folderId, String type) throws Exception {
		User user = validateSession(sid);

		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		long rootId = fdao.findRoot(user.getTenantId()).getId();

		if (folderId == rootId) {
			log.error("Cannot create alias in the root");
			throw new Exception("Cannot create alias in the root");
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document originalDoc = docDao.findById(docId);
		checkDownloadEnable(user, originalDoc.getFolder().getId());
		checkWriteEnable(user, folderId);

		FolderDAO mdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = mdao.findById(folderId);
		if (folder == null) {
			log.error("Folder " + folder + " not found");
			throw new Exception("error - folder not found");
		}

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.SHORTCUT_STORED.toString());
		transaction.setComment("");
		transaction.setUser(user);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		Document doc = documentManager.createAlias(originalDoc, folder, type, transaction);

		checkPublished(user, doc);

		return WSDocument.fromDocument(doc);
	}

	@Override
	public void reindex(String sid, long docId, String content) throws Exception {
		validateSession(sid);
		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		documentManager.reindex(docId, content);
	}

	@Override
	public WSDocument[] getAliases(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		Collection<Long> folderIds = null;
		if (!user.isInGroup("admin")) {
			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			folderIds = fdao.findFolderIdByUserId(user.getId(), null, true);
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = new ArrayList<Document>();
		if (user.isInGroup("admin"))
			docs = docDao.findByWhere("_entity.docRef=" + docId, null, null);
		else {
			String idsStr = folderIds.toString().replace('[', '(').replace(']', ')');
			docs = docDao.findByWhere("_entity.docRef=" + docId + " and _entity.id in " + idsStr, null, null);
		}

		List<WSDocument> wsDocs = new ArrayList<WSDocument>();
		for (int i = 0; i < docs.size(); i++) {
			docDao.initialize(docs.get(i));
			if (user.isInGroup("admin") || folderIds.contains(docs.get(i).getFolder().getId()))
				wsDocs.add(WSDocument.fromDocument(docs.get(i)));
		}

		return wsDocs.toArray(new WSDocument[0]);
	}

	@Override
	public long upload(String sid, Long docId, Long folderId, boolean release, String filename, DataHandler content)
			throws Exception {
		validateSession(sid);

		if (docId != null) {
			checkout(sid, docId);
			checkin(sid, docId, "", filename, release, content);
			return docId;
		} else {
			WSDocument doc = new WSDocument();
			doc.setFileName(filename);
			doc.setTitle(FilenameUtils.getBaseName(filename));
			doc.setFolderId(folderId);
			return create(sid, doc, content).getId();
		}

	}

	@Override
	public WSLink link(String sid, long doc1, long doc2, String type) throws Exception {
		User user = validateSession(sid);

		DocumentLinkDAO linkDao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		DocumentLink link = linkDao.findByDocIdsAndType(doc1, doc2, type);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

		Document document1 = docDao.findById(doc1);
		if (document1 == null)
			throw new Exception("Document with ID " + doc1 + " not found");
		Document document2 = docDao.findById(doc2);
		if (document2 == null)
			throw new Exception("Document with ID " + doc2 + " not found");

		checkWriteEnable(user, document2.getFolder().getId());

		if (link == null) {
			// The link doesn't exist and must be created
			link = new DocumentLink();
			link.setTenantId(document1.getTenantId());
			link.setDocument1(document1);
			link.setDocument2(document2);
			link.setType(type);
			linkDao.store(link);

			WSLink lnk = new WSLink();
			lnk.setId(link.getId());
			lnk.setDoc1(doc1);
			lnk.setDoc2(doc2);
			lnk.setType(type);
			return lnk;
		} else {
			throw new Exception("Documents already linked");
		}
	}

	@Override
	public WSLink[] getLinks(String sid, long docId) throws Exception {
		User user = validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		DocumentLinkDAO linkDao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);

		Document document = docDao.findById(docId);
		if (document == null)
			throw new Exception("Document with ID " + docId + " not found");

		checkReadEnable(user, document.getFolder().getId());
		List<DocumentLink> links = linkDao.findByDocId(docId);
		List<WSLink> lnks = new ArrayList<WSLink>();
		for (DocumentLink link : links) {
			WSLink lnk = new WSLink();
			lnk.setId(link.getId());
			lnk.setDoc1(link.getDocument1().getId());
			lnk.setDoc2(link.getDocument2().getId());
			lnk.setType(link.getType());
			lnks.add(lnk);
		}

		return lnks.toArray(new WSLink[0]);
	}

	@Override
	public void deleteLink(String sid, long id) throws Exception {
		validateSession(sid);
		DocumentLinkDAO linkDao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		linkDao.delete(id);
	}

	@Override
	public String getExtractedText(String sid, long docId) throws Exception {
		validateSession(sid);
		SearchEngine indexer = (SearchEngine) Context.getInstance().getBean(SearchEngine.class);
		return indexer.getHit(docId).getContent();
	}
}