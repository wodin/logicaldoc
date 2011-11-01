package com.logicaldoc.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailAttachment;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Bookmark;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DocumentNote;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.DownloadTicket;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Rating;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.BookmarkDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.document.dao.DocumentNoteDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.DownloadTicketDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.document.dao.RatingDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.SystemQuota;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.core.transfer.InMemoryZipImport;
import com.logicaldoc.core.transfer.ZipExport;
import com.logicaldoc.core.util.UserUtil;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIRating;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.web.UploadServlet;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the DocumentService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class DocumentServiceImpl extends RemoteServiceServlet implements DocumentService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(DocumentServiceImpl.class);

	@Override
	public void addBookmarks(String sid, long[] docIds) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		int added = 0;
		int alreadyAdded = 0;
		for (long id : docIds) {
			try {
				Bookmark bookmark = null;
				if (bookmarkDao.findByUserIdAndDocId(SessionUtil.getSessionUser(sid).getId(), id).size() > 0) {
					// The bookmark already exists
					alreadyAdded++;
				} else {
					Document doc = dao.findById(id);
					bookmark = new Bookmark();
					bookmark.setTitle(doc.getTitle());
					bookmark.setUserId(SessionUtil.getSessionUser(sid).getId());
					bookmark.setDocId(id);
					bookmark.setFileType(doc.getType());
					bookmarkDao.store(bookmark);
					added++;
				}
			} catch (AccessControlException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage(), e);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage(), e);
			}
		}
	}

	@Override
	public void addDocuments(String sid, String encoding, boolean importZip, final GUIDocument metadata)
			throws InvalidSessionException {
		UserSession userSession = SessionUtil.validateSession(sid);

		Map<String, File> uploadedFilesMap = UploadServlet.getReceivedFiles(getThreadLocalRequest(), sid);
		log.debug("Uploading " + uploadedFilesMap.size() + " files");

		Map<String, String> uploadedFileNames = UploadServlet.getReceivedFileNames(getThreadLocalRequest(), sid);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		final Folder parent = folderDao.findById(metadata.getFolder().getId());
		if (uploadedFilesMap.isEmpty())
			throw new RuntimeException("No file uploaded");

		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!fdao.isWriteEnable(metadata.getFolder().getId(), userSession.getUserId())) {
			throw new RuntimeException("The user doesn't have the write permission on the current folder");
		}

		try {
			for (String fileId : uploadedFilesMap.keySet()) {
				final File file = uploadedFilesMap.get(fileId);
				final String filename = uploadedFileNames.get(fileId);

				if (filename.endsWith(".zip") && importZip) {
					log.debug("file = " + file);

					// copy the file into the user folder
					final File destFile = new File(UserUtil.getUserResource(SessionUtil.getSessionUser(sid).getId(),
							"zip"), filename);
					FileUtils.copyFile(file, destFile);

					final long userId = SessionUtil.getSessionUser(sid).getId();
					final String sessionId = sid;
					final String zipEncoding = encoding;
					// Prepare the import thread
					Thread zipImporter = new Thread(new Runnable() {
						public void run() {
							/*
							 * Prepare the Master document used to create the
							 * new one
							 */
							Document doc = toDocument(metadata);
							doc.setCreation(new Date());

							InMemoryZipImport importer = new InMemoryZipImport(doc);
							importer.process(destFile, parent, userId, zipEncoding, sessionId);
							try {
								FileUtils.forceDelete(destFile);
							} catch (IOException e) {
								log.error("Unable to delete " + destFile, e);
							}
						}
					});

					// And launch it
					zipImporter.start();
				} else {
					try {
						// Check if the user can upload another document.
						SystemQuota.checkUserQuota(userSession.getUserId(), file.length());
					} catch (Exception e) {
						return;
					}

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setEvent(History.EVENT_STORED);
					transaction.setUser(SessionUtil.getSessionUser(sid));
					transaction.setComment(metadata.getComment());

					/*
					 * Prepare the Master document used to create the new one
					 */
					Document doc = toDocument(metadata);
					doc.setFileName(filename);
					doc.setTitle(filename.substring(0, filename.lastIndexOf(".")));
					doc.setCreation(new Date());

					// Create the new
					doc = documentManager.create(file, doc, transaction);
				}
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void addDocuments(String sid, String language, long folderId, String encoding, boolean importZip,
			final Long templateId) throws InvalidSessionException {
		GUIDocument metadata = new GUIDocument();
		metadata.setLanguage(language);
		metadata.setFolder(new GUIFolder(folderId));
		metadata.setTemplateId(templateId);
		addDocuments(sid, encoding, importZip, metadata);
	}

	@Override
	public void checkin(String sid, long docId, String comment, boolean major) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		Map<String, File> uploadedFilesMap = UploadServlet.getReceivedFiles(getThreadLocalRequest(), sid);
		File file = uploadedFilesMap.values().iterator().next();
		if (file != null) {
			// check that we have a valid file for storing as new
			// version
			Map<String, String> uploadedFileNames = UploadServlet.getReceivedFileNames(getThreadLocalRequest(), sid);
			String fileName = uploadedFileNames.values().iterator().next();

			log.debug("Checking in file " + fileName);

			try {
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(History.EVENT_CHECKEDIN);
				transaction.setUser(SessionUtil.getSessionUser(sid));
				transaction.setComment(comment);

				// checkin the document; throws an exception if
				// something goes wrong
				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				documentManager.checkin(docId, new FileInputStream(file), fileName, major, transaction);

			} catch (Throwable t) {
				log.error(t.getMessage(), t);
				throw new RuntimeException(t.getMessage(), t);
			}
		}
	}

	@Override
	public void checkout(String sid, long docId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(History.EVENT_CHECKEDOUT);
		transaction.setComment("");
		transaction.setUser(SessionUtil.getSessionUser(sid));

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		try {
			documentManager.checkout(docId, transaction);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException("Unable to checkout the document");
		}
	}

	@Override
	public void delete(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		if (ids.length > 0) {
			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			for (long id : ids) {
				try {
					Document doc = dao.findById(id);
					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setEvent(History.EVENT_DELETED);
					transaction.setComment("");
					transaction.setUser(SessionUtil.getSessionUser(sid));

					// If it is a shortcut, we delete only the shortcut
					if (doc.getDocRef() != null) {
						transaction.setEvent(History.EVENT_SHORTCUT_DELETED);
						dao.delete(doc.getId(), transaction);
						continue;
					}

					// The document of the selected documentRecord must be
					// not immutable
					if (doc.getImmutable() == 1 && !transaction.getUser().isInGroup("admin")) {
						continue;
					}

					// The document must be not locked
					if (doc.getStatus() != Document.DOC_UNLOCKED || doc.getExportStatus() != Document.EXPORT_UNLOCKED) {
						continue;
					}
					// Check if there are some shortcuts associated to the
					// deleting document. All the shortcuts must be deleted.
					if (dao.findShortcutIds(doc.getId()).size() > 0)
						for (Long shortcutId : dao.findShortcutIds(doc.getId())) {
							dao.delete(shortcutId);
						}
					dao.delete(doc.getId(), transaction);
				} catch (AccessControlException t) {
					log.error(t.getMessage(), t);
					throw new RuntimeException(t.getMessage(), t);
				} catch (Exception t) {
					log.error(t.getMessage(), t);
					throw new RuntimeException(t.getMessage(), t);
				}
			}
		}
	}

	@Override
	public void deleteBookmarks(String sid, long[] bookmarkIds) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		BookmarkDAO dao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		for (long id : bookmarkIds) {
			dao.delete(id);
		}

	}

	@Override
	public void deleteLinks(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public GUIExtendedAttribute[] getAttributes(String sid, long templateId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentTemplateDAO templateDao = (DocumentTemplateDAO) Context.getInstance()
				.getBean(DocumentTemplateDAO.class);
		DocumentTemplate template = templateDao.findById(templateId);

		GUIExtendedAttribute[] attributes = prepareGUIAttributes(template, null);

		return attributes;
	}

	private GUIExtendedAttribute[] prepareGUIAttributes(DocumentTemplate template, Document doc) {
		try {
			GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[template.getAttributeNames().size()];
			int i = 0;
			for (String attrName : template.getAttributeNames()) {
				ExtendedAttribute extAttr = template.getAttributes().get(attrName);
				GUIExtendedAttribute att = new GUIExtendedAttribute();
				att.setName(attrName);
				att.setPosition(extAttr.getPosition());
				att.setLabel(extAttr.getLabel());
				att.setMandatory(extAttr.getMandatory() == 1);
				att.setEditor(extAttr.getEditor());

				// If the case, populate the options
				if (att.getEditor() == ExtendedAttribute.EDITOR_LISTBOX) {
					String buf = (String) extAttr.getStringValue();
					List<String> list = new ArrayList<String>();
					StringTokenizer st = new StringTokenizer(buf, ",");
					while (st.hasMoreElements()) {
						String val = (String) st.nextElement();
						if (!list.contains(val))
							list.add(val);
					}
					att.setOptions(list.toArray(new String[0]));
				}

				if (doc != null) {
					if (doc.getValue(attrName) != null)
						att.setValue(doc.getValue(attrName));
				} else
					att.setValue(extAttr.getValue());
				att.setType(extAttr.getType());

				attributes[i] = att;
				i++;
			}
			return attributes;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	@Override
	public GUIDocument getById(String sid, long docId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);

		GUIDocument document = null;
		GUIFolder folder = FolderServiceImpl.getFolder(sid, doc.getFolder().getId());

		if (doc != null) {
			// Check if it is an alias
			if (doc.getDocRef() != null) {
				long id = doc.getDocRef();
				doc = docDao.findById(id);
			}

			document = new GUIDocument();
			try {
				docDao.initialize(doc);
				document.setId(doc.getId());
				document.setTitle(doc.getTitle());
				document.setCustomId(doc.getCustomId());
				if (doc.getTags().size() > 0)
					document.setTags(doc.getTags().toArray(new String[doc.getTags().size()]));
				else
					document.setTags(new String[0]);
				document.setType(doc.getType());
				document.setFileName(doc.getFileName());
				document.setVersion(doc.getVersion());
				document.setCreation(doc.getCreation());
				document.setCreator(doc.getCreator());
				document.setDate(doc.getDate());
				document.setPublisher(doc.getPublisher());
				document.setFileVersion(doc.getFileVersion());
				document.setLanguage(doc.getLanguage());
				document.setSource(doc.getSource());
				document.setRecipient(doc.getRecipient());
				document.setTemplateId(doc.getTemplateId());
				document.setSourceType(doc.getSourceType());
				document.setObject(doc.getObject());
				document.setCoverage(doc.getCoverage());
				document.setSourceAuthor(doc.getSourceAuthor());
				document.setSourceDate(doc.getSourceDate());
				document.setSourceId(doc.getSourceId());
				document.setLastModified(doc.getLastModified());
				document.setLockUserId(doc.getLockUserId());
				document.setComment(doc.getComment());
				document.setStatus(doc.getStatus());
				document.setWorkflowStatus(doc.getWorkflowStatus());
				document.setImmutable(doc.getImmutable());
				document.setFileSize(new Long(doc.getFileSize()).floatValue());

				if (doc.getRating() != null)
					document.setRating(doc.getRating());

				if (doc.getTemplate() != null) {
					document.setTemplate(doc.getTemplate().getName());
					document.setTemplateId(doc.getTemplate().getId());
					GUIExtendedAttribute[] attributes = prepareGUIAttributes(doc.getTemplate(), doc);
					document.setAttributes(attributes);
				}

				if (doc.getCustomId() != null)
					document.setCustomId(doc.getCustomId());
				else
					document.setCustomId("");

				document.setFolder(folder);
				FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
				document.setPathExtended(fdao.computePathExtended(folder.getId()));
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
				throw new RuntimeException(t.getMessage(), t);
			}
		}
		return document;
	}

	@Override
	public GUIVersion[] getVersionsById(String sid, long id1, long id2) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			VersionDAO versDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
			Version docVersion = versDao.findById(id1);

			GUIVersion version1 = null;
			if (docVersion != null) {
				version1 = new GUIVersion();
				version1.setUsername(docVersion.getUsername());
				version1.setComment(docVersion.getComment());
				version1.setId(id1);
				version1.setTitle(docVersion.getTitle());
				version1.setCustomId(docVersion.getCustomId());
				version1.setTags(docVersion.getTags().toArray(new String[docVersion.getTags().size()]));
				version1.setType(docVersion.getType());
				version1.setFileName(docVersion.getFileName());
				version1.setVersion(docVersion.getVersion());
				version1.setCreation(docVersion.getCreation());
				version1.setCreator(docVersion.getCreator());
				version1.setDate(docVersion.getDate());
				version1.setPublisher(docVersion.getPublisher());
				version1.setFileVersion(docVersion.getFileVersion());
				version1.setLanguage(docVersion.getLanguage());
				version1.setTemplateId(docVersion.getTemplateId());
				version1.setFileSize(new Float(docVersion.getFileSize()));
				version1.setSource(docVersion.getSource());
				version1.setCoverage(docVersion.getCoverage());
				version1.setRecipient(docVersion.getRecipient());
				version1.setObject(docVersion.getObject());
				version1.setWorkflowStatus(docVersion.getWorkflowStatus());
				if (docVersion.getRating() != null)
					version1.setRating(docVersion.getRating());
				version1.setSourceType(docVersion.getSourceType());
				version1.setSourceAuthor(docVersion.getSourceAuthor());
				version1.setSourceId(docVersion.getSourceId());
				version1.setSourceDate(docVersion.getSourceDate());

				version1.setTemplate(docVersion.getTemplateName());
				versDao.initialize(docVersion);
				for (String attrName : docVersion.getAttributeNames()) {
					ExtendedAttribute extAttr = docVersion.getAttributes().get(attrName);
					version1.setValue(attrName, extAttr.getValue());
				}
				GUIFolder folder1 = new GUIFolder();
				folder1.setName(docVersion.getFolderName());
				folder1.setId(docVersion.getFolderId());
				version1.setFolder(folder1);
			}

			docVersion = versDao.findById(id2);

			GUIVersion version2 = null;
			if (docVersion != null) {
				version2 = new GUIVersion();
				version2.setUsername(docVersion.getUsername());
				version2.setComment(docVersion.getComment());
				version2.setId(id1);
				version2.setTitle(docVersion.getTitle());
				version2.setCustomId(docVersion.getCustomId());
				version2.setTags(docVersion.getTags().toArray(new String[docVersion.getTags().size()]));
				version2.setType(docVersion.getType());
				version2.setFileName(docVersion.getFileName());
				version2.setVersion(docVersion.getVersion());
				version2.setCreation(docVersion.getCreation());
				version2.setCreator(docVersion.getCreator());
				version2.setDate(docVersion.getDate());
				version2.setPublisher(docVersion.getPublisher());
				version2.setFileVersion(docVersion.getFileVersion());
				version2.setLanguage(docVersion.getLanguage());
				version2.setFileSize(new Float(docVersion.getFileSize()));
				version2.setSource(docVersion.getSource());
				version2.setCoverage(docVersion.getCoverage());
				version2.setRecipient(docVersion.getRecipient());
				version2.setObject(docVersion.getObject());
				if (docVersion.getRating() != null)
					version2.setRating(docVersion.getRating());
				version2.setSourceType(docVersion.getSourceType());
				version2.setSourceAuthor(docVersion.getSourceAuthor());
				version2.setSourceId(docVersion.getSourceId());
				version2.setSourceDate(docVersion.getSourceDate());
				version2.setWorkflowStatus(docVersion.getWorkflowStatus());

				version2.setTemplateId(docVersion.getTemplateId());
				version2.setTemplate(docVersion.getTemplateName());
				versDao.initialize(docVersion);
				for (String attrName : docVersion.getAttributeNames()) {
					ExtendedAttribute extAttr = docVersion.getAttributes().get(attrName);
					version2.setValue(attrName, extAttr.getValue());
				}
				GUIFolder folder2 = new GUIFolder();
				folder2.setName(docVersion.getFolderName());
				folder2.setId(docVersion.getFolderId());
				version2.setFolder(folder2);
			}

			GUIVersion[] versions = null;
			if (version1 != null && version2 != null) {
				versions = new GUIVersion[2];
				versions[0] = version1;
				versions[1] = version2;
			} else if (version1 != null && version2 == null) {
				versions = new GUIVersion[1];
				versions[0] = version1;
			} else if (version1 == null && version2 != null) {
				versions = new GUIVersion[1];
				versions[0] = version2;
			} else
				return null;

			return versions;
		} catch (Throwable t) {
			log.error("Exception linking documents: " + t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void linkDocuments(String sid, long[] inDocIds, long[] outDocIds) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentLinkDAO linkDao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		if (outDocIds.length > 0) {
			try {
				for (int i = 0; i < inDocIds.length; i++) {
					for (int j = 0; j < outDocIds.length; j++) {
						DocumentLink link = linkDao.findByDocIdsAndType(inDocIds[i], outDocIds[j], "default");
						if (link == null) {
							// The link doesn't exist and must be created
							link = new DocumentLink();
							link.setDocument1(docDao.findById(inDocIds[i]));
							link.setDocument2(docDao.findById(outDocIds[j]));
							link.setType("default");
							linkDao.store(link);
						}
					}
				}
			} catch (Throwable t) {
				log.error("Exception linking documents: " + t.getMessage(), t);
				throw new RuntimeException(t.getMessage(), t);
			}
		}
	}

	@Override
	public void lock(String sid, long[] docIds, String comment) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		// Unlock the document; throws an exception if something
		// goes wrong
		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		try {
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setEvent(History.EVENT_LOCKED);
			transaction.setUser(SessionUtil.getSessionUser(sid));
			transaction.setComment(comment);
			for (long id : docIds) {
				documentManager.lock(id, Document.DOC_LOCKED, transaction);
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void makeImmutable(String sid, long[] docIds, String comment) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			for (long id : docIds) {
				Document doc = docDao.findById(id);

				if (doc.getImmutable() == 0) {
					// The document of the selected documentRecord must be
					// not locked
					if (doc.getStatus() != Document.DOC_UNLOCKED || doc.getExportStatus() != Document.EXPORT_UNLOCKED) {
						continue;
					}

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setComment(comment);
					transaction.setUser(SessionUtil.getSessionUser(sid));

					manager.makeImmutable(id, transaction);
				}
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void markHistoryAsRead(String sid, String event) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		for (History history : dao.findByUserIdAndEvent(SessionUtil.getSessionUser(sid).getId(), event)) {
			dao.initialize(history);
			history.setNew(0);
			dao.store(history);
		}
	}

	@Override
	public void markIndexable(String sid, long[] docIds) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			int indexableCount = 0;
			for (long id : docIds) {
				manager.changeIndexingStatus(docDao.findById(id), AbstractDocument.INDEX_TO_INDEX);
				indexableCount++;
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void markUnindexable(String sid, long[] docIds) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			int unindexableCount = 0;
			for (long id : docIds) {
				manager.changeIndexingStatus(docDao.findById(id), AbstractDocument.INDEX_SKIP);
				unindexableCount++;
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void restore(String sid, long docId, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		docDao.restore(docId, folderId);
	}

	@Override
	public GUIDocument save(String sid, GUIDocument document) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = null;
		try {
			if (document.getId() != 0) {
				doc = docDao.findById(document.getId());
				docDao.initialize(doc);
				doc.setCustomId(document.getCustomId());
				doc.setComment(document.getComment());
				try {
					Document docVO = toDocument(document);

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setEvent(History.EVENT_CHANGED);
					transaction.setComment(document.getComment());
					transaction.setUser(SessionUtil.getSessionUser(sid));

					DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(
							DocumentManager.class);
					documentManager.update(doc, docVO, transaction);

					document.setId(doc.getId());
					document.setLastModified(new Date());
					document.setVersion(doc.getVersion());
				} catch (Throwable t) {
					t.printStackTrace();
					log.error(t.getMessage(), t);
					throw new RuntimeException(t.getMessage(), t);
				}
			} else
				return null;

			return document;
		} catch (Throwable e) {
			e.printStackTrace();
			log.warn(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Produces a plain new Document from a GUIDocument
	 */
	protected Document toDocument(GUIDocument document) {
		Document docVO = new Document();
		docVO.setTitle(document.getTitle());
		if (document.getTags() != null && document.getTags().length > 0)
			docVO.setTags(new HashSet<String>(Arrays.asList(document.getTags())));

		docVO.setSourceType(document.getSourceType());
		docVO.setFileName(document.getFileName());
		docVO.setVersion(document.getVersion());
		docVO.setCreation(document.getCreation());
		docVO.setCreator(document.getCreator());
		docVO.setDate(document.getDate());
		docVO.setPublisher(document.getPublisher());
		docVO.setFileVersion(document.getFileVersion());
		docVO.setLanguage(document.getLanguage());
		if (document.getFileSize() != null)
			docVO.setFileSize(document.getFileSize().longValue());
		docVO.setSource(document.getSource());
		docVO.setRecipient(document.getRecipient());
		docVO.setObject(document.getObject());
		docVO.setCoverage(document.getCoverage());
		docVO.setSourceAuthor(document.getSourceAuthor());
		docVO.setSourceDate(document.getSourceDate());
		docVO.setSourceId(document.getSourceId());
		docVO.setRating(document.getRating());
		docVO.setComment(document.getComment());
		docVO.setWorkflowStatus(document.getWorkflowStatus());

		if (document.getTemplateId() != null) {
			docVO.setTemplateId(document.getTemplateId());
			DocumentTemplateDAO templateDao = (DocumentTemplateDAO) Context.getInstance().getBean(
					DocumentTemplateDAO.class);
			DocumentTemplate template = templateDao.findById(document.getTemplateId());
			docVO.setTemplate(template);
			if (document.getAttributes().length > 0) {
				for (GUIExtendedAttribute attr : document.getAttributes()) {
					ExtendedAttribute templateAttribute = template.getAttributes().get(attr.getName());
					// This control is necessary because, changing
					// the template, the values of the old template
					// attributes keys remains on the form value
					// manager,
					// so the GUIDocument contains also the old
					// template attributes keys that must be
					// skipped.
					if (templateAttribute == null)
						continue;

					ExtendedAttribute extAttr = new ExtendedAttribute();
					int templateType = templateAttribute.getType();
					int extAttrType = attr.getType();

					if (templateType != extAttrType) {
						// This check is useful to avoid errors
						// related to the old template
						// attributes keys that remains on the form
						// value manager
						if (attr.getValue().toString().trim().isEmpty() && templateType != 0) {
							if (templateType == ExtendedAttribute.TYPE_INT) {
								extAttr.setIntValue(null);
							} else if (templateType == ExtendedAttribute.TYPE_DOUBLE) {
								extAttr.setDoubleValue(null);
							} else if (templateType == ExtendedAttribute.TYPE_DATE) {
								extAttr.setDateValue(null);
							}
						} else if (templateType == GUIExtendedAttribute.TYPE_DOUBLE) {
							extAttr.setValue(Double.parseDouble(attr.getValue().toString()));
						} else if (templateType == GUIExtendedAttribute.TYPE_INT) {
							extAttr.setValue(Long.parseLong(attr.getValue().toString()));
						}
					} else {
						if (templateType == ExtendedAttribute.TYPE_INT) {
							if (attr.getValue() != null)
								extAttr.setIntValue((Long) attr.getValue());
							else
								extAttr.setIntValue(null);
						} else if (templateType == ExtendedAttribute.TYPE_DOUBLE) {
							if (attr.getValue() != null)
								extAttr.setDoubleValue((Double) attr.getValue());
							else
								extAttr.setDoubleValue(null);
						} else if (templateType == ExtendedAttribute.TYPE_DATE) {
							if (attr.getValue() != null)
								extAttr.setDateValue((Date) attr.getValue());
							else
								extAttr.setDateValue(null);
						} else if (templateType == ExtendedAttribute.TYPE_STRING) {
							if (attr.getValue() != null)
								extAttr.setStringValue((String) attr.getValue());
							else
								extAttr.setStringValue(null);
						}
					}

					extAttr.setLabel(attr.getLabel());
					extAttr.setType(templateType);
					extAttr.setPosition(attr.getPosition());
					extAttr.setMandatory(attr.isMandatory() ? 1 : 0);

					docVO.getAttributes().put(attr.getName(), extAttr);
				}
			}
		}

		docVO.setStatus(document.getStatus());
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (document.getFolder() != null)
			docVO.setFolder(fdao.findById(document.getFolder().getId()));
		return docVO;
	}

	@Override
	public String sendAsEmail(String sid, GUIEmail email) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findById(session.getUserId());

		EMail mail;
		try {
			mail = new EMail();

			mail.setAccountId(-1);
			mail.setAuthor(user.getUserName());
			mail.setAuthorAddress(user.getEmail());
			if (StringUtils.isNotEmpty(email.getRecipients()))
				mail.parseRecipients(email.getRecipients());
			if (StringUtils.isNotEmpty(email.getCc()))
				mail.parseRecipientsCC(email.getCc());
			mail.setFolder("outbox");
			mail.setMessageText(email.getMessage());
			mail.setRead(1);
			mail.setSentDate(new Date());
			mail.setSubject(email.getSubject());
			mail.setUserName(user.getUserName());

			// Needed in case the zip compression was requested by the user
			File zipFile = null;

			if (email.isSendAsTicket()) {
				// Prepare a new download ticket
				String temp = new Date().toString() + user.getId();
				String ticketid = CryptUtil.cryptString(temp);
				DownloadTicket ticket = new DownloadTicket();
				ticket.setTicketId(ticketid);
				ticket.setDocId(email.getDocIds()[0]);
				ticket.setUserId(user.getId());

				// Store the ticket
				DownloadTicketDAO ticketDao = (DownloadTicketDAO) Context.getInstance()
						.getBean(DownloadTicketDAO.class);
				ticketDao.store(ticket);

				// Try to clean the DB from old tickets
				ticketDao.deleteOlder();

				HttpServletRequest request = this.getThreadLocalRequest();
				String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":"
						+ request.getServerPort() + request.getContextPath();
				String address = urlPrefix + "/download-ticket?ticketId=" + ticketid;
				mail.setMessageText(email.getMessage() + "\nURL: " + address);
			} else {
				if (email.isZipCompression()) {
					/*
					 * Create a temporary archive for sending it as unique
					 * attachment
					 */
					zipFile = File.createTempFile("email", "zip");
					OutputStream out = null;

					try {
						out = new FileOutputStream(zipFile);
						ZipExport export = new ZipExport();
						export.process(email.getDocIds(), out);
						createAttachment(mail, zipFile);
					} catch (Throwable t) {
						log.error(t.getMessage(), t);
						try {
							if (out != null)
								out.close();
						} catch (Throwable q) {

						}
					}
				} else {
					for (long id : email.getDocIds())
						createAttachment(mail, id);
				}
			}

			try {
				// Send the message
				EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
				sender.send(mail);

				if (zipFile != null)
					FileUtils.forceDelete(zipFile);

				DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				FolderDAO fDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
				for (long id : email.getDocIds()) {
					Document doc = docDao.findById(id);

					// Create the document history event
					HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
					History history = new History();
					history.setSessionId(sid);
					history.setDocId(id);
					history.setEvent(History.EVENT_SENT);
					history.setUser(SessionUtil.getSessionUser(sid));
					history.setComment(StringUtils.abbreviate(email.getRecipients(), 4000));
					history.setTitle(doc.getTitle());
					history.setVersion(doc.getVersion());
					history.setPath(fDao.computePathExtended(doc.getFolder().getId()));
					dao.store(history);
				}

				return "ok";
			} catch (Exception ex) {
				log.warn(ex.getMessage(), ex);
				return "error";
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			return "error";
		}
	}

	private void createAttachment(EMail email, long docId) throws IOException {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		EMailAttachment att = new EMailAttachment();
		Document doc = docDao.findById(docId);
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

	private void createAttachment(EMail email, File zipFile) throws IOException {
		EMailAttachment att = new EMailAttachment();
		att.setData(FileUtil.toByteArray(zipFile));
		att.setFileName("doc.zip");
		String extension = "zip";
		att.setMimeType(MimeType.get(extension));

		if (att != null) {
			email.addAttachment(2 + email.getAttachments().size(), att);
		}
	}

	@Override
	public void unlock(String sid, long[] docIds) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(SessionUtil.getSessionUser(sid));

			// Unlock the document; throws an exception if something
			// goes wrong
			DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			for (long id : docIds) {
				documentManager.unlock(id, transaction);
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void rename(String sid, long docId, String title) throws Exception {
		SessionUtil.validateSession(sid);
		User user = SessionUtil.getSessionUser(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);

		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!fdao.isWriteEnable(doc.getFolder().getId(), user.getId())) {
			throw new RuntimeException("The user doesn't have the rename permission on the current folder");
		}

		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setUser(user);
		manager.rename(doc, title, true, transaction);
	}

	@Override
	public void updateBookmark(String sid, GUIBookmark bookmark) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		try {
			Bookmark bk;
			if (bookmark.getId() != 0) {
				bk = bookmarkDao.findById(bookmark.getId());
				bookmarkDao.initialize(bk);
			} else
				return;

			bk.setTitle(bookmark.getName());
			bk.setDescription(bookmark.getDescription());

			bookmarkDao.store(bk);
			bookmark.setId(bk.getId());
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public void updateLink(String sid, long id, String type) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		DocumentLink link = dao.findById(id);
		link.setType(type);
		dao.store(link);
	}

	@Override
	public void cleanUploadedFileFolder(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		UploadServlet.cleanReceivedFiles(sid);
	}

	@Override
	public GUIRating getRating(String sid, long docId) throws InvalidSessionException {
		UserSession userSession = SessionUtil.validateSession(sid);

		RatingDAO ratingDao = (RatingDAO) Context.getInstance().getBean(RatingDAO.class);

		try {
			GUIRating rating = new GUIRating();
			Rating rat = ratingDao.findVotesByDocId(docId);
			if (rat != null) {
				ratingDao.initialize(rat);

				rating.setId(rat.getId());
				rating.setDocId(docId);
				// We use the rating userId value to know in the GUI if the user
				// has already vote this document.
				if (ratingDao.findByDocIdAndUserId(docId, userSession.getUserId()))
					rating.setUserId(userSession.getUserId());
				rating.setCount(rat.getCount());
				rating.setAverage(rat.getAverage());
			} else {
				rating.setDocId(docId);
				rating.setCount(0);
				rating.setAverage(0F);
			}

			return rating;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public int saveRating(String sid, GUIRating rating) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		RatingDAO ratingDao = (RatingDAO) Context.getInstance().getBean(RatingDAO.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		try {
			Rating rat = new Rating();
			rat.setDocId(rating.getDocId());
			rat.setUserId(rating.getUserId());
			rat.setVote(rating.getVote());
			ratingDao.store(rat);

			Rating votesDoc = ratingDao.findVotesByDocId(rating.getDocId());
			Document doc = docDao.findById(rating.getDocId());
			docDao.initialize(doc);
			int average = 0;
			if (votesDoc != null && votesDoc.getAverage() != null)
				average = votesDoc.getAverage().intValue();
			doc.setRating(average);
			docDao.store(doc);

			return average;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public long addNote(String sid, long docId, String message) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			DocumentNote note = new DocumentNote();
			note.setDocId(docId);
			note.setUserId(SessionUtil.getSessionUser(sid).getId());
			note.setUsername(SessionUtil.getSessionUser(sid).getFullName());
			note.setDate(new Date());
			note.setMessage(message);

			DocumentNoteDAO dao = (DocumentNoteDAO) Context.getInstance().getBean(DocumentNoteDAO.class);
			dao.store(note);

			return note.getId();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public void deleteNotes(String sid, long[] ids) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentNoteDAO dao = (DocumentNoteDAO) Context.getInstance().getBean(DocumentNoteDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public void bulkUpdate(String sid, long[] ids, GUIDocument vo) throws InvalidSessionException {
		for (long id : ids) {
			GUIDocument buf = getById(sid, id);

			buf.setComment(vo.getComment() != null ? vo.getComment() : "");
			if (StringUtils.isNotEmpty(vo.getLanguage()))
				buf.setLanguage(vo.getLanguage());
			if (vo.getTags() != null && vo.getTags().length > 0)
				buf.setTags(vo.getTags());
			if (StringUtils.isNotEmpty(vo.getCoverage()))
				buf.setCoverage(vo.getCoverage());
			if (StringUtils.isNotEmpty(vo.getObject()))
				buf.setObject(vo.getObject());
			if (StringUtils.isNotEmpty(vo.getRecipient()))
				buf.setRecipient(vo.getRecipient());
			if (StringUtils.isNotEmpty(vo.getSource()))
				buf.setSource(vo.getSource());
			if (StringUtils.isNotEmpty(vo.getSourceAuthor()))
				buf.setSourceAuthor(vo.getSourceAuthor());
			if (StringUtils.isNotEmpty(vo.getSourceId()))
				buf.setSourceId(vo.getSourceId());
			if (StringUtils.isNotEmpty(vo.getSourceType()))
				buf.setSourceType(vo.getSourceType());
			if (vo.getTemplateId() != null)
				buf.setTemplateId(vo.getTemplateId());
			if (vo.getAttributes() != null && vo.getAttributes().length > 0)
				buf.setAttributes(vo.getAttributes());
			if (vo.getSourceDate() != null)
				buf.setSourceDate(vo.getSourceDate());
			if (StringUtils.isNotEmpty(vo.getWorkflowStatus()))
				buf.setSource(vo.getWorkflowStatus());
			save(sid, buf);
		}
	}
}