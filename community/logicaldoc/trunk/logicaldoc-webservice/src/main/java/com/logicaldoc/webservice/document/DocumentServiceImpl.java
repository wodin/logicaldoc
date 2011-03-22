package com.logicaldoc.webservice.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailAttachment;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.webservice.AbstractService;

/**
 * Document Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class DocumentServiceImpl extends AbstractService implements DocumentService {

	public static Log log = LogFactory.getLog(DocumentServiceImpl.class);

	@Override
	public WSDocument create(String sid, WSDocument document, DataHandler content) throws Exception {
		User user = validateSession(sid);
		checkWriteEnable(user, document.getFolderId());

		FolderDAO mdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = mdao.findById(document.getFolderId());
		if (folder == null) {
			log.error("Folder " + folder + " not found");
			throw new Exception("error - folder not found");
		}

		Document doc = document.toDocument(user);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(History.EVENT_STORED);
		transaction.setComment("");
		transaction.setUser(user);

		// Get file to upload inputStream
		InputStream stream = content.getInputStream();

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		doc = documentManager.create(stream, doc, transaction, false);

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
				transaction.setEvent(History.EVENT_CHECKEDIN);
				transaction.setUser(user);
				transaction.setComment(comment);

				// checkin the document; throws an exception if
				// something goes wrong
				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				documentManager.checkin(document.getId(), stream, filename, release, false, transaction);

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

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(History.EVENT_CHECKEDOUT);
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

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(History.EVENT_DELETED);
		transaction.setComment("");
		transaction.setUser(user);
		docDao.delete(docId, transaction);
	}

	private void checkLocked(User user, Document doc) throws Exception {
		if (user.isInGroup("admin"))
			return;

		if (doc.getImmutable() == 1)
			throw new Exception("the document is immutable");

		if (doc.getStatus() != Document.DOC_UNLOCKED && user.getId() != doc.getLockUserId())
			throw new Exception("the document is locked");
	}

	@Override
	public DataHandler getContent(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		checkReadEnable(user, doc.getFolder().getId());
		checkDownloadEnable(user, doc.getFolder().getId());

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = documentManager.getDocumentFile(doc);

		if (!file.exists()) {
			throw new FileNotFoundException(file.getPath());
		}

		log.debug("Attach file " + file.getPath());

		// Now we can append the 'document' attachment to the response
		DataHandler content = new DataHandler(new FileDataSource(file));

		return content;
	}

	@Override
	public WSDocument getDocument(String sid, long docId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		if (doc == null)
			return null;
		checkReadEnable(user, doc.getFolder().getId());

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

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(History.EVENT_LOCKED);
		transaction.setComment("");
		transaction.setUser(user);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		documentManager.lock(docId, Document.DOC_LOCKED, transaction);
	}

	@Override
	public void move(String sid, long docId, long folderId) throws Exception {
		User user = validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);
		checkPermission(Permission.DELETE, user, doc.getFolder().getId());

		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = dao.findById(folderId);
		checkLocked(user, doc);
		checkWriteEnable(user, folder.getId());

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(History.EVENT_MOVED);
		transaction.setComment("");
		transaction.setUser(user);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		documentManager.moveToFolder(doc, folder, transaction);
	}

	@Override
	public void rename(String sid, long docId, String name) throws Exception {
		WSDocument wsDoc = getDocument(sid, docId);
		wsDoc.setTitle(name);
		updateDocument(sid, wsDoc);
	}

	@Override
	public void restore(String sid, long docId, long folderId) throws Exception {
		validateSession(sid);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		docDao.restore(docId, folderId);
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
		transaction.setEvent(History.EVENT_UNLOCKED);
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

		// Initialize the lazy loaded collections
		docDao.initialize(doc);

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(History.EVENT_CHANGED);
		transaction.setComment("");
		transaction.setUser(user);

		manager.update(doc, document.toDocument(user), transaction);
	}

	@Override
	public WSDocument[] list(String sid, long folderId) throws Exception {
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
		WSDocument[] wsDocs = new WSDocument[docs.size()];
		for (int i = 0; i < docs.size(); i++) {
			docDao.initialize(docs.get(i));
			wsDocs[i] = WSDocument.fromDocument(docs.get(i));
		}

		return wsDocs;
	}

	@Override
	public WSDocument[] getDocuments(String sid, Long[] docIds) throws Exception {
		User user = validateSession(sid);
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Long> folderIds = fdao.findFolderIdByUserId(user.getId());

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		List<Document> docs = docDao.findByIds(docIds, null);
		List<WSDocument> wsDocs = new ArrayList<WSDocument>();
		for (int i = 0; i < docs.size(); i++) {
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
				"select distinct(docId) from History where deleted=0 and (docId is not NULL) and userId="
						+ user.getId());
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

			mail.setAccountId(-1);
			mail.setAuthor(user.getUserName());
			mail.setAuthorAddress(user.getEmail());
			mail.parseRecipients(recipients);
			mail.setFolder("outbox");
			mail.setMessageText(message);
			mail.setRead(1);
			mail.setSentDate(new Date());
			mail.setSubject(subject);
			mail.setUserName(user.getUserName());

			/*
			 * Only readable documents can be sent
			 */
			List<Document> docs = new ArrayList<Document>();
			for (long id : docIds) {
				Document doc = docDao.findById(id);
				if (folderDao.isReadEnable(doc.getFolder().getId(), user.getId())) {
					createAttachment(mail, doc);
					docs.add(doc);
				}
			}

			// Send the message
			EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
			sender.send(mail);

			for (Document doc : docs) {
				// Create the document history event
				HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
				History history = new History();
				history.setSessionId(sid);
				history.setDocId(doc.getId());
				history.setEvent(History.EVENT_SENT);
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
		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = manager.getDocumentFile(doc);
		att.setFile(file);
		att.setFileName(doc.getFileName());
		String extension = doc.getFileExtension();
		att.setMimeType(MimeType.get(extension));

		if (att != null) {
			email.addAttachment(2 + email.getAttachments().size(), att);
		}
	}

	@Override
	public WSDocument createAlias(String sid, long docId, long folderId) throws Exception {
		User user = validateSession(sid);
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
		transaction.setEvent(History.EVENT_SHORTCUT_STORED);
		transaction.setComment("");
		transaction.setUser(user);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		Document doc = documentManager.createShortcut(originalDoc, folder, transaction);

		return WSDocument.fromDocument(doc);
	}
}