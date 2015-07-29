package com.logicaldoc.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailAttachment;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.contact.Contact;
import com.logicaldoc.core.contact.ContactDAO;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Bookmark;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
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
import com.logicaldoc.core.document.pdf.PdfConverterManager;
import com.logicaldoc.core.document.thumbnail.ThumbnailManager;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.core.transfer.InMemoryZipImport;
import com.logicaldoc.core.transfer.ZipExport;
import com.logicaldoc.core.util.UserUtil;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIRating;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.util.crypt.CryptUtil;
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.web.UploadServlet;
import com.logicaldoc.web.util.ServiceUtil;

/**
 * Implementation of the DocumentService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class DocumentServiceImpl extends RemoteServiceServlet implements DocumentService {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

	@Override
	public void addBookmarks(String sid, long[] ids, int type) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		int added = 0;
		int alreadyAdded = 0;
		for (long id : ids) {
			try {
				Bookmark bookmark = null;
				if (bookmarkDao.findByUserIdAndDocId(ServiceUtil.getSessionUser(sid).getId(), id).size() > 0) {
					// The bookmark already exists
					alreadyAdded++;
				} else {
					bookmark = new Bookmark();
					bookmark.setTenantId(session.getTenantId());
					bookmark.setType(type);
					bookmark.setTargetId(id);
					bookmark.setUserId(ServiceUtil.getSessionUser(sid).getId());

					if (type == Bookmark.TYPE_DOCUMENT) {
						Document doc = dao.findById(id);
						bookmark.setTitle(doc.getTitle());
						bookmark.setFileType(doc.getType());
					} else {
						Folder f = fdao.findById(id);
						bookmark.setTitle(f.getName());
					}

					bookmarkDao.store(bookmark);

					added++;
				}
			} catch (AccessControlException e) {
				ServiceUtil.throwServerException(session, log, e);
			} catch (Exception e) {
				ServiceUtil.throwServerException(session, log, e);
			}
		}
	}

	@Override
	public void indexDocuments(String sid, Long[] docIds) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		for (Long id : docIds) {
			if (id != null)
				try {
					documentManager.reindex(id, null);
				} catch (Throwable e) {
					ServiceUtil.throwServerException(session, log, e);
				}
		}
	}

	@Override
	public void addDocuments(String sid, boolean importZip, boolean immediateIndexing, final GUIDocument metadata)
			throws ServerException {
		final UserSession session = ServiceUtil.validateSession(sid);

		Map<String, File> uploadedFilesMap = UploadServlet.getReceivedFiles(getThreadLocalRequest(), sid);
		log.debug("Uploading " + uploadedFilesMap.size() + " files");

		Map<String, String> uploadedFileNames = UploadServlet.getReceivedFileNames(getThreadLocalRequest(), sid);

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		final Folder parent = folderDao.findById(metadata.getFolder().getId());
		if (uploadedFilesMap.isEmpty())
			ServiceUtil.throwServerException(session, log, new Exception("No file uploaded"));

		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!fdao.isWriteEnable(metadata.getFolder().getId(), session.getUserId()))
			ServiceUtil.throwServerException(session, log, new Exception(
					"The user doesn't have the write permission on the current folder"));

		List<Long> docsToIndex = new ArrayList<Long>();

		try {
			for (String fileId : uploadedFilesMap.keySet()) {
				final File file = uploadedFilesMap.get(fileId);
				final String filename = uploadedFileNames.get(fileId);

				if (filename.endsWith(".zip") && importZip) {
					log.debug("file = " + file);

					// copy the file into the user folder
					final File destFile = new File(UserUtil.getUserResource(ServiceUtil.getSessionUser(sid).getId(),
							"zip"), filename);
					FileUtils.copyFile(file, destFile);

					final long userId = ServiceUtil.getSessionUser(sid).getId();
					final String sessionId = sid;
					// Prepare the import thread
					Thread zipImporter = new Thread(new Runnable() {
						public void run() {
							/*
							 * Prepare the Master document used to create the
							 * new one
							 */
							Document doc = toDocument(metadata);
							doc.setTenantId(session.getTenantId());
							doc.setCreation(new Date());

							InMemoryZipImport importer = new InMemoryZipImport(doc);
							importer.process(destFile, parent, userId, sessionId);
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
					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setEvent(DocumentEvent.STORED.toString());
					transaction.setUser(ServiceUtil.getSessionUser(sid));
					transaction.setComment(metadata.getComment());

					/*
					 * Prepare the Master document used to create the new one
					 */
					Document doc = toDocument(metadata);
					doc.setTenantId(session.getTenantId());
					doc.setCreation(new Date());

					doc.setFileName(filename);
					doc.setTitle(FilenameUtils.getBaseName(filename));

					// Create the new document
					doc = documentManager.create(file, doc, transaction);

					if (immediateIndexing && doc.getIndexed() == Document.INDEX_TO_INDEX)
						docsToIndex.add(doc.getId());
				}
			}
			UploadServlet.cleanReceivedFiles(getThreadLocalRequest().getSession());

			if (!docsToIndex.isEmpty())
				indexDocuments(sid, docsToIndex.toArray(new Long[0]));
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void addDocuments(String sid, String language, long folderId, boolean importZip, boolean immediateIndexing,
			final Long templateId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (folderId == fdao.findRoot(session.getTenantId()).getId())
			throw new RuntimeException("Cannot add documents in the root");

		GUIDocument metadata = new GUIDocument();
		metadata.setLanguage(language);
		metadata.setFolder(new GUIFolder(folderId));
		metadata.setTemplateId(templateId);
		addDocuments(sid, importZip, immediateIndexing, metadata);
	}

	@Override
	public GUIDocument checkin(String sid, GUIDocument document, boolean major) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		Map<String, File> uploadedFilesMap = UploadServlet.getReceivedFiles(getThreadLocalRequest(), sid);
		File file = uploadedFilesMap.values().iterator().next();
		if (file != null) {
			// check that we have a valid file for storing as new version
			Map<String, String> uploadedFileNames = UploadServlet.getReceivedFileNames(getThreadLocalRequest(), sid);
			String fileName = uploadedFileNames.values().iterator().next();

			log.debug("Checking in file " + fileName);

			try {
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(DocumentEvent.CHECKEDIN.toString());
				transaction.setUser(ServiceUtil.getSessionUser(sid));
				transaction.setComment(document.getComment());

				DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				Document doc = dao.findById(document.getId());
				if (doc.getDocRef() != null)
					doc = dao.findById(doc.getDocRef().longValue());

				// checkin the document; throws an exception if
				// something goes wrong
				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				documentManager.checkin(doc.getId(), new FileInputStream(file), fileName, major, toDocument(document),
						transaction);
				UploadServlet.cleanReceivedFiles(getThreadLocalRequest().getSession());
				return getById(sid, doc.getId());
			} catch (Throwable t) {
				return (GUIDocument) ServiceUtil.throwServerException(session, log, t);
			}
		} else
			return null;
	}

	@Override
	public void checkout(String sid, long docId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(DocumentEvent.CHECKEDOUT.toString());
		transaction.setComment("");
		transaction.setUser(ServiceUtil.getSessionUser(sid));

		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = dao.findById(docId);
		if (doc.getDocRef() != null)
			docId = doc.getDocRef().longValue();

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		try {
			documentManager.checkout(docId, transaction);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void delete(String sid, long[] ids) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		if (ids.length > 0) {
			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			for (long id : ids) {
				try {
					Document doc = dao.findById(id);

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setEvent(DocumentEvent.DELETED.toString());
					transaction.setComment("");
					transaction.setUser(ServiceUtil.getSessionUser(sid));

					// If it is a shortcut, we delete only the shortcut
					if (doc.getDocRef() != null) {
						transaction.setEvent(DocumentEvent.SHORTCUT_DELETED.toString());
						dao.delete(doc.getId(), transaction);
						continue;
					}

					// The document of the selected documentRecord must be
					// not immutable
					if (doc.getImmutable() == 1 && !transaction.getUser().isInGroup("admin")) {
						log.debug("Document " + id + " was not deleted because immutable");
						continue;
					}

					// The document must be not locked
					if (doc.getStatus() == Document.DOC_LOCKED) {
						log.debug("Document " + id + " was not deleted because locked");
						continue;
					}

					// Check if there are some shortcuts associated to the
					// deleting document. All the shortcuts must be deleted.
					if (dao.findAliasIds(doc.getId()).size() > 0)
						for (Long shortcutId : dao.findAliasIds(doc.getId())) {
							dao.delete(shortcutId);
						}
					dao.delete(doc.getId(), transaction);
				} catch (Throwable t) {
					t.printStackTrace();
					ServiceUtil.throwServerException(session, log, t);
				}
			}
		}
	}

	@Override
	public void deleteBookmarks(String sid, long[] bookmarkIds) throws ServerException {
		ServiceUtil.validateSession(sid);

		BookmarkDAO dao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		for (long id : bookmarkIds) {
			dao.delete(id);
		}

	}

	@Override
	public void deleteLinks(String sid, long[] ids) throws ServerException {
		ServiceUtil.validateSession(sid);

		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public GUIExtendedAttribute[] getAttributes(String sid, long templateId) throws ServerException {
		ServiceUtil.validateSession(sid);

		DocumentTemplateDAO templateDao = (DocumentTemplateDAO) Context.getInstance()
				.getBean(DocumentTemplateDAO.class);
		DocumentTemplate template = templateDao.findById(templateId);

		GUIExtendedAttribute[] attributes = prepareGUIAttributes(template, null);

		return attributes;
	}

	private static GUIExtendedAttribute[] prepareGUIAttributes(DocumentTemplate template, Document doc) {
		try {
			if (template != null) {
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
					att.setStringValue(extAttr.getStringValue());

					att.setOptions(new String[] { extAttr.getStringValue() });

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
			} else {
				List<GUIExtendedAttribute> list = new ArrayList<GUIExtendedAttribute>();
				for (String name : doc.getAttributeNames()) {
					ExtendedAttribute e = doc.getAttributes().get(name);
					GUIExtendedAttribute ext = new GUIExtendedAttribute();
					ext.setName(name);
					ext.setDateValue(e.getDateValue());
					ext.setStringValue(e.getStringValue());
					ext.setIntValue(e.getIntValue());
					ext.setDoubleValue(e.getDoubleValue());
					ext.setBooleanValue(e.getBooleanValue());
					ext.setType(e.getType());
					list.add(ext);
				}
				return list.toArray(new GUIExtendedAttribute[0]);
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return null;
		}
	}

	@Override
	public GUIDocument getById(String sid, long docId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);

		GUIDocument document = null;
		GUIFolder folder = null;

		if (doc != null) {
			folder = FolderServiceImpl.getFolder(sid, doc.getFolder().getId());

			Long aliasId = null;

			// Check if it is an alias
			if (doc.getDocRef() != null) {
				long id = doc.getDocRef();
				doc = docDao.findById(id);
				aliasId = docId;
			}

			try {
				User user = ServiceUtil.getSessionUser(sid);
				checkPublished(user, doc);
				docDao.initialize(doc);
				document = fromDocument(doc, folder);
				FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
				document.setPathExtended(fdao.computePathExtended(folder.getId()));
				if (aliasId != null)
					document.setDocRef(aliasId);
			} catch (Throwable t) {
				ServiceUtil.throwServerException(session, log, t);
			}
		}
		return document;
	}

	public static GUIDocument fromDocument(Document doc, GUIFolder folder) {
		GUIDocument document = new GUIDocument();
		document.setId(doc.getId());
		if (doc.getDocRef() != null && doc.getDocRef().longValue() != 0) {
			document.setDocRef(doc.getDocRef());
			document.setDocRefType(doc.getDocRefType());
		}
		document.setTitle(doc.getTitle());
		document.setCustomId(doc.getCustomId());
		if (doc.getTags().size() > 0)
			document.setTags(doc.getTagsAsWords().toArray(new String[doc.getTags().size()]));
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
		document.setStartPublishing(doc.getStartPublishing());
		document.setStopPublishing(doc.getStopPublishing());
		document.setPublished(doc.getPublished());
		document.setSigned(doc.getSigned());
		document.setStamped(doc.getStamped());
		document.setBarcoded(doc.getBarcoded());
		document.setIndexed(doc.getIndexed());
		document.setExtResId(doc.getExtResId());
		document.setPages(doc.getPages());
		document.setNature(doc.getNature());
		document.setFormId(doc.getFormId());

		if (doc.getRating() != null)
			document.setRating(doc.getRating());

		if (doc.getCustomId() != null)
			document.setCustomId(doc.getCustomId());
		else
			document.setCustomId("");

		if (doc.getTemplate() != null) {
			document.setTemplate(doc.getTemplate().getName());
			document.setTemplateId(doc.getTemplate().getId());
		}

		if (doc.getAttributes() != null && !doc.getAttributes().isEmpty()) {
			GUIExtendedAttribute[] attributes = prepareGUIAttributes(doc.getTemplate(), doc);
			document.setAttributes(attributes);
		}

		if (folder != null) {
			document.setFolder(folder);
		} else {
			GUIFolder f = new GUIFolder(doc.getFolder().getId());
			f.setName(doc.getFolder().getName());
			document.setFolder(f);
		}

		return document;
	}

	@Override
	public GUIVersion[] getVersionsById(String sid, long id1, long id2) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			VersionDAO versDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
			Version docVersion = versDao.findById(id1);
			versDao.initialize(docVersion);

			GUIVersion version1 = null;
			if (docVersion != null) {
				version1 = new GUIVersion();
				version1.setDocId(docVersion.getDocId());
				version1.setUsername(docVersion.getUsername());
				version1.setComment(docVersion.getComment());
				version1.setId(id1);
				version1.setTitle(docVersion.getTitle());
				version1.setCustomId(docVersion.getCustomId());
				version1.setTagsString(docVersion.getTgs());
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
				version1.setStartPublishing(docVersion.getStartPublishing());
				version1.setStopPublishing(docVersion.getStopPublishing());
				version1.setPublished(docVersion.getPublished());
				version1.setPages(docVersion.getPages());

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
			versDao.initialize(docVersion);

			GUIVersion version2 = null;
			if (docVersion != null) {
				version2 = new GUIVersion();
				version2.setDocId(docVersion.getDocId());
				version2.setUsername(docVersion.getUsername());
				version2.setComment(docVersion.getComment());
				version2.setId(id1);
				version2.setTitle(docVersion.getTitle());
				version2.setCustomId(docVersion.getCustomId());
				version2.setTagsString(docVersion.getTgs());
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
				version2.setStartPublishing(docVersion.getStartPublishing());
				version2.setStopPublishing(docVersion.getStopPublishing());
				version2.setPublished(docVersion.getPublished());
				version2.setPages(docVersion.getPages());

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
			return (GUIVersion[]) ServiceUtil.throwServerException(session, null, t);
		}
	}

	@Override
	public void linkDocuments(String sid, long[] inDocIds, long[] outDocIds) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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
							link.setTenantId(session.getTenantId());
							link.setDocument1(docDao.findById(inDocIds[i]));
							link.setDocument2(docDao.findById(outDocIds[j]));
							link.setType("default");
							linkDao.store(link);
						}
					}
				}
			} catch (Throwable t) {
				ServiceUtil.throwServerException(session, log, t);
			}
		}
	}

	@Override
	public void lock(String sid, long[] docIds, String comment) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		// Unlock the document; throws an exception if something
		// goes wrong
		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		try {
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setEvent(DocumentEvent.LOCKED.toString());
			transaction.setUser(ServiceUtil.getSessionUser(sid));
			transaction.setComment(comment);
			for (long id : docIds) {
				documentManager.lock(id, Document.DOC_LOCKED, transaction);
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void makeImmutable(String sid, long[] docIds, String comment) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			for (long id : docIds) {
				Document doc = docDao.findById(id);

				if (doc.getImmutable() == 0) {
					// The document of the selected documentRecord must be
					// not locked
					if (doc.getStatus() != Document.DOC_UNLOCKED) {
						continue;
					}

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setComment(comment);
					transaction.setUser(ServiceUtil.getSessionUser(sid));

					manager.makeImmutable(id, transaction);
				}
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void markHistoryAsRead(String sid, String event) throws ServerException {
		ServiceUtil.validateSession(sid);

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		for (History history : dao.findByUserIdAndEvent(ServiceUtil.getSessionUser(sid).getId(), event, null)) {
			dao.initialize(history);
			history.setNew(0);
			dao.store(history);
		}
	}

	@Override
	public void markIndexable(String sid, long[] docIds) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			for (long id : docIds) {
				manager.changeIndexingStatus(docDao.findById(id), AbstractDocument.INDEX_TO_INDEX);
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void markUnindexable(String sid, long[] docIds) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			for (long id : docIds) {
				manager.changeIndexingStatus(docDao.findById(id), AbstractDocument.INDEX_SKIP);
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void restore(String sid, long[] docIds, long folderId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

		for (long docId : docIds) {
			History transaction = new History();
			transaction.setUser(ServiceUtil.getSessionUser(session.getId()));
			transaction.setSessionId(session.getId());
			docDao.restore(docId, folderId, transaction);
		}
	}

	@Override
	public GUIDocument save(String sid, GUIDocument document) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			if (document.getId() != 0) {
				Document doc = docDao.findById(document.getId());
				docDao.initialize(doc);

				doc.setCustomId(document.getCustomId());
				doc.setComment(document.getComment());
				try {
					Document docVO = toDocument(document);
					docVO.setBarcoded(doc.getBarcoded());
					docVO.setTenantId(session.getTenantId());

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setEvent(DocumentEvent.CHANGED.toString());
					transaction.setComment(document.getComment());
					transaction.setUser(ServiceUtil.getSessionUser(sid));

					DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(
							DocumentManager.class);
					documentManager.update(doc, docVO, transaction);

					return getById(sid, doc.getId());
				} catch (Throwable t) {
					log.error(t.getMessage(), t);
					throw new RuntimeException(t.getMessage(), t);
				}
			} else
				return null;
		} catch (Throwable t) {
			return (GUIDocument) ServiceUtil.throwServerException(session, log, t);
		}
	}

	/**
	 * Produces a plain new Document from a GUIDocument
	 */
	protected Document toDocument(GUIDocument document) {
		Document docVO = new Document();
		if (document.getTags() != null && document.getTags().length > 0)
			docVO.setTagsFromWords(new HashSet<String>(Arrays.asList(document.getTags())));

		docVO.setTitle(document.getTitle());
		docVO.setCustomId(document.getCustomId());
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
		docVO.setStartPublishing(document.getStartPublishing());
		docVO.setStopPublishing(document.getStopPublishing());
		docVO.setPublished(document.getPublished());
		docVO.setBarcoded(document.getBarcoded());
		docVO.setExtResId(document.getExtResId());
		docVO.setPages(document.getPages());
		docVO.setNature(document.getNature());
		docVO.setFormId(document.getFormId());

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
							if (templateType == ExtendedAttribute.TYPE_INT
									|| templateType == ExtendedAttribute.TYPE_BOOLEAN) {
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
						} else if (templateType == GUIExtendedAttribute.TYPE_BOOLEAN) {
							extAttr.setValue(attr.getBooleanValue());
							extAttr.setType(ExtendedAttribute.TYPE_BOOLEAN);
						} else if (templateType == GUIExtendedAttribute.TYPE_USER) {
							extAttr.setIntValue(attr.getIntValue());
							extAttr.setStringValue(attr.getStringValue());
							extAttr.setType(templateType);
						}
					} else {
						if (templateType == ExtendedAttribute.TYPE_INT) {
							if (attr.getValue() != null)
								extAttr.setIntValue((Long) attr.getValue());
							else
								extAttr.setIntValue(null);
						} else if (templateType == ExtendedAttribute.TYPE_BOOLEAN) {
							if (attr.getBooleanValue() != null)
								extAttr.setValue(attr.getBooleanValue());
							else
								extAttr.setBooleanValue(null);
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
						} else if (templateType == ExtendedAttribute.TYPE_USER) {
							if (attr.getValue() != null) {
								extAttr.setIntValue(attr.getIntValue());
								extAttr.setStringValue(attr.getStringValue());
							} else {
								extAttr.setIntValue(null);
								extAttr.setStringValue(null);
							}
							extAttr.setType(templateType);
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
	public String sendAsEmail(String sid, GUIEmail email) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		DocumentDAO documentDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		User user = userDao.findById(session.getUserId());

		EMail mail;
		try {
			mail = new EMail();
			mail.setHtml(1);
			mail.setTenantId(session.getTenantId());

			mail.setAccountId(-1);
			mail.setAuthor(user.getUserName());
			mail.setAuthorAddress(user.getEmail());

			if (StringUtils.isNotEmpty(email.getRecipients()))
				mail.parseRecipients(email.getRecipients());
			if (StringUtils.isNotEmpty(email.getCc()))
				mail.parseRecipientsCC(email.getCc());
			mail.setFolder("outbox");

			String message = email.getMessage();

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

				Document doc = documentDao.findById(ticket.getDocId());

				HttpServletRequest request = this.getThreadLocalRequest();
				String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":"
						+ request.getServerPort() + request.getContextPath();
				String address = urlPrefix + "/download-ticket?ticketId=" + ticketid;
				message = email.getMessage()
						+ "<div style='margin-top:10px; border-top:1px solid black; background-color:#CCCCCC;'><b>&nbsp;"
						+ I18N.message("clicktodownload") + ": <a href='" + address + "'>" + doc.getFileName()
						+ "</a></b></div>";

				if (doc.getDocRef() != null)
					doc = documentDao.findById(doc.getDocRef());
				String thumb = createPreview(doc, user.getId());
				if (thumb != null) {
					mail.getImages().add(thumb);
					message += "<p><img src='cid:image_1'/></p>";
				}

				mail.setMessageText("<html>" + message + "</html>");
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

						// Create the document history event
						History transaction = new History();
						transaction.setSessionId(sid);
						transaction.setEvent(DocumentEvent.DOWNLOADED.toString());
						transaction.setUser(ServiceUtil.getSessionUser(sid));

						ZipExport export = new ZipExport();
						export.process(email.getDocIds(), out, email.isPdfConversion(), transaction);
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
						createAttachment(mail, id, email.isPdfConversion());
				}
			}

			try {
				message = "<html>" + message + "</html>";
				mail.setMessageText(message);

				// Send the message
				EMailSender sender = new EMailSender(session.getTenantName());
				sender.send(mail);

				if (zipFile != null)
					FileUtils.forceDelete(zipFile);

				DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				FolderDAO fDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
				for (long id : email.getDocIds()) {
					Document doc = docDao.findById(id);
					if (doc.getDocRef() != null)
						doc = documentDao.findById(doc.getDocRef());

					// Create the document history event
					HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
					History history = new History();
					history.setSessionId(sid);
					history.setDocId(id);
					history.setEvent(DocumentEvent.SENT.toString());
					history.setUser(ServiceUtil.getSessionUser(sid));
					history.setComment(StringUtils.abbreviate(email.getRecipients(), 4000));
					history.setTitle(doc.getTitle());
					history.setVersion(doc.getVersion());
					history.setPath(fDao.computePathExtended(doc.getFolder().getId()));
					dao.store(history);
				}

				/*
				 * Save the recipients in the user's contacts
				 */
				ContactDAO cdao = (ContactDAO) Context.getInstance().getBean(ContactDAO.class);
				for (Recipient recipient : mail.getRecipients()) {
					List<Contact> contacts = cdao.findByUser(user.getId(), recipient.getAddress());
					if (contacts.isEmpty()) {
						Contact cont = new Contact();
						cont.setUserId(user.getId());
						cont.setEmail(recipient.getAddress().trim());
						cdao.store(cont);
					}
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

	private String createPreview(Document doc, long userId) {
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
		String thumbResource = storer.getResourceName(doc, doc.getFileVersion(), "thumb.jpg");

		// In any case try to produce the thumbnail
		if (!storer.exists(doc.getId(), thumbResource)) {
			ThumbnailManager thumbManager = (ThumbnailManager) Context.getInstance().getBean(ThumbnailManager.class);
			try {
				thumbManager.createTumbnail(doc, doc.getFileVersion());
			} catch (Throwable t) {
				log.error(t.getMessage(), t);
			}
		}

		if (storer.exists(doc.getId(), thumbResource)) {
			File file = UserUtil.getUserResource(userId, "/tmp/thumb.jpg");
			file.getParentFile().mkdirs();
			storer.writeToFile(doc.getId(), thumbResource, file);
			try {
				return file.toURI().toURL().toString();
			} catch (MalformedURLException e) {

			}
		}

		return null;
	}

	private void createAttachment(EMail email, long docId, boolean pdfConversion) throws IOException {
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Storer storer = (Storer) Context.getInstance().getBean(Storer.class);
		Document doc = docDao.findById(docId);
		String resource = storer.getResourceName(doc, null, null);

		boolean convertToPdf = pdfConversion;
		if (doc.getDocRef() != null) {
			// this is an alias
			if ("pdf".equals(doc.getDocRefType())) {
				doc = docDao.findById(doc.getDocRef());
				convertToPdf = true;
			}
		}

		EMailAttachment att = new EMailAttachment();
		att.setIcon(doc.getIcon());
		att.setFileName(doc.getFileName());
		String extension = doc.getFileExtension();
		att.setMimeType(MimeType.get(extension));

		if (convertToPdf) {
			if (!"pdf".equals(FilenameUtils.getExtension(doc.getFileName().toLowerCase()))) {
				PdfConverterManager manager = (PdfConverterManager) Context.getInstance().getBean(
						PdfConverterManager.class);
				manager.createPdf(doc);
				resource = storer.getResourceName(doc, null, "conversion.pdf");
			}
			att.setMimeType(MimeType.get("pdf"));
			att.setFileName(FilenameUtils.getBaseName(doc.getFileName()) + ".pdf");
		}

		att.setData(storer.getBytes(doc.getId(), resource));

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
	public void unlock(String sid, long[] docIds) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(ServiceUtil.getSessionUser(sid));

			// Unlock the document; throws an exception if something
			// goes wrong
			DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			for (long id : docIds) {
				documentManager.unlock(id, transaction);
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void updateBookmark(String sid, GUIBookmark bookmark) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void updateLink(String sid, long id, String type) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
			DocumentLink link = dao.findById(id);
			dao.initialize(link);
			link.setType(type);
			dao.store(link);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void cleanUploadedFileFolder(String sid) throws ServerException {
		ServiceUtil.validateSession(sid);
		UploadServlet.cleanReceivedFiles(sid);
	}

	@Override
	public GUIRating getRating(String sid, long docId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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
				if (ratingDao.findByDocIdAndUserId(docId, session.getUserId()))
					rating.setUserId(session.getUserId());
				rating.setCount(rat.getCount());
				rating.setAverage(rat.getAverage());
			} else {
				rating.setDocId(docId);
				rating.setCount(0);
				rating.setAverage(0F);
			}

			return rating;
		} catch (Throwable t) {
			return (GUIRating) ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public int saveRating(String sid, GUIRating rating) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		RatingDAO ratingDao = (RatingDAO) Context.getInstance().getBean(RatingDAO.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		try {
			Rating rat = new Rating();
			rat.setTenantId(session.getTenantId());
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
			return (Integer) ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public long addNote(String sid, long docId, String message) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			User sessionUser = ServiceUtil.getSessionUser(sid);

			DocumentNote note = new DocumentNote();
			note.setTenantId(session.getTenantId());
			note.setDocId(docId);
			note.setUserId(sessionUser.getId());
			note.setUsername(sessionUser.getFullName());
			note.setDate(new Date());
			note.setMessage(message);

			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(sessionUser);

			DocumentNoteDAO dao = (DocumentNoteDAO) Context.getInstance().getBean(DocumentNoteDAO.class);
			dao.store(note, transaction);

			return note.getId();
		} catch (Throwable t) {
			return (Integer) ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void deleteNotes(String sid, long[] ids) throws ServerException {
		ServiceUtil.validateSession(sid);
		DocumentNoteDAO dao = (DocumentNoteDAO) Context.getInstance().getBean(DocumentNoteDAO.class);
		for (long id : ids)
			dao.delete(id);
	}

	@Override
	public void bulkUpdate(String sid, long[] ids, GUIDocument vo) throws ServerException {
		for (long id : ids) {
			try {
				GUIDocument buf = getById(sid, id);
				if (buf.getImmutable() == 1 || buf.getStatus() != Document.DOC_UNLOCKED)
					continue;

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
				if (vo.getPublished() > -1)
					buf.setPublished(vo.getPublished());
				if (vo.getStartPublishing() != null)
					buf.setStartPublishing(vo.getStartPublishing());
				if (vo.getStopPublishing() != null)
					buf.setStopPublishing(vo.getStopPublishing());

				save(sid, buf);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	protected void checkPublished(User user, Document doc) throws Exception {
		if (!user.isInGroup("admin") && !user.isInGroup("publisher") && !doc.isPublishing())
			throw new FileNotFoundException("Document not published");
	}

	@Override
	public void updateNote(String sid, long docId, long noteId, String message) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			User user = ServiceUtil.getSessionUser(session.getId());
			DocumentNoteDAO dao = (DocumentNoteDAO) Context.getInstance().getBean(DocumentNoteDAO.class);
			DocumentNote note = dao.findById(noteId);
			note.setUserId(user.getId());
			note.setUsername(user.getFullName());
			note.setMessage(message);
			dao.store(note);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public GUIDocument deleteVersions(String sid, long[] ids) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			User user = ServiceUtil.getSessionUser(session.getId());
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			for (long id : ids) {
				History transaction = new History();
				transaction.setUser(user);
				Version version = manager.deleteVersion(id, transaction);
				return getById(sid, version.getDocId());
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
		return null;
	}

	@Override
	public GUIDocument createEmpty(String sid, GUIDocument vo) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			User user = ServiceUtil.getSessionUser(session.getId());
			DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

			if (!fdao.isWriteEnable(vo.getFolder().getId(), session.getUserId())) {
				throw new RuntimeException("The user doesn't have the write permission on the current folder");
			}

			Document doc = toDocument(vo);
			doc.setId(0L);

			History transaction = new History();
			transaction.setUser(user);
			transaction.setEvent(DocumentEvent.STORED.toString());
			Document document = documentManager.create(IOUtils.toInputStream(""), doc, transaction);

			// If that VO is in checkout, perform a checkout also
			if (vo.getStatus() == Document.DOC_CHECKED_OUT) {
				transaction = new History();
				transaction.setUser(user);
				documentManager.checkout(document.getId(), transaction);
			}

			return fromDocument(document, vo.getFolder());
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
		return null;
	}

	@Override
	public void deleteFromTrash(String sid, Long[] ids) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		if (ids == null || ids.length < 1)
			return;

		try {
			String idsStr = Arrays.asList(ids).toString().replace('[', '(').replace(']', ')');
			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			dao.bulkUpdate("set ld_deleted=2 where ld_id in " + idsStr, null);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void emptyTrash(String sid) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			dao.bulkUpdate("set ld_deleted=2 where ld_deleted=1 and  ld_deleteuserid=" + session.getUserId(), null);

			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			fdao.bulkUpdate("set ld_deleted=2 where ld_deleted=1 and  ld_deleteuserid=" + session.getUserId(), null);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void archiveDocuments(String sid, long[] docIds, String comment) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(ServiceUtil.getSessionUser(sid));
			transaction.setComment(comment);
			manager.archiveDocuments(docIds, transaction);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public long archiveFolder(String sid, long folderId, String comment) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(ServiceUtil.getSessionUser(sid));
			transaction.setComment(comment);
			return manager.archiveFolder(folderId, transaction);
		} catch (Throwable t) {
			return (Long) ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void unarchiveDocuments(String sid, long[] docIds) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			User user = ServiceUtil.getSessionUser(sid);

			for (long id : docIds) {
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setUser(user);

				dao.unarchive(id, transaction);
			}
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public long countDocuments(String sid, long folderId, int status) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		User user = ServiceUtil.getSessionUser(sid);
		try {
			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

			List<Long> accessibleIds = fdao.findIdByUserId(user.getId(), folderId);

			StringBuffer query = new StringBuffer(
					"select count(ld_id) from ld_document where ld_deleted=0 and ld_status=" + status);
			if (!user.isInGroup("admin"))
				query.append(" and ld_folderid in " + accessibleIds.toString().replace("[", "(").replace("]", ")"));
			return dao.queryForLong(query.toString());
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
			return 0L;
		}
	}
}