package com.logicaldoc.web.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Date;
import java.util.Map;

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
import com.logicaldoc.core.document.DiscussionComment;
import com.logicaldoc.core.document.DiscussionThread;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.BookmarkDAO;
import com.logicaldoc.core.document.dao.DiscussionThreadDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.document.dao.VersionDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.transfer.InMemoryZipImport;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.MimeType;
import com.logicaldoc.util.TagUtil;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.data.UploadServlet;
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
	public void addBookmarks(String sid, long[] docIds) {
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
			} catch (Exception e) {
			}
		}

		if (alreadyAdded != 0) {
			// TODO Message?
		}

		if (added != 0) {
			// TODO Message?
		}

	}

	@Override
	public void addDocuments(String sid, String language, long folderId, String encoding, boolean importZip) {
		SessionUtil.validateSession(sid);

		Map<String, File> uploadedFilesMap = UploadServlet.getReceivedFiles(getThreadLocalRequest());
		log.debug("Uploading " + uploadedFilesMap.size() + " files");

		Map<String, String> uploadedFileNames = UploadServlet.getReceivedFileNames(getThreadLocalRequest());

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		final Menu parent = menuDao.findById(folderId);
		try {
			for (String fileId : uploadedFilesMap.keySet()) {
				File file = uploadedFilesMap.get(fileId);

				if (file.getName().endsWith(".zip") && importZip) {
					log.debug("file = " + file);

					PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
					String path = conf.getPropertyWithSubstitutions("conf.userdir");

					if (!path.endsWith("_")) {
						path += "_";
					}
					path += SessionUtil.getSessionUser(sid).getUserName() + "_" + File.separator;

					FileUtils.forceMkdir(new File(path));

					// copy the file into the user folder
					final File destFile = new File(path, file.getName());
					FileUtils.copyFile(file, destFile);

					final long userId = SessionUtil.getSessionUser(sid).getId();
					final String sessionId = sid;
					final String zipLanguage = language;
					final String zipEncoding = encoding;
					// Prepare the import thread
					Thread zipImporter = new Thread(new Runnable() {
						public void run() {
							InMemoryZipImport importer = new InMemoryZipImport();
							importer.process(destFile, LocaleUtil.toLocale(zipLanguage), parent, userId, null,
									zipEncoding, sessionId);
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
					String filename = uploadedFileNames.get(fileId);
					String title = filename.substring(0, filename.lastIndexOf("."));

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setEvent(History.EVENT_STORED);
					transaction.setUser(SessionUtil.getSessionUser(sid));

					Document doc = new Document();
					doc.setFileName(filename);
					doc.setLocale(LocaleUtil.toLocale(language));
					doc.setTitle(title);
					doc.setFolder(parent);

					doc = documentManager.create(file, doc, transaction, false);
					if (StringUtils.isNotEmpty(doc.getCustomId())) {
						// TODO Message??
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// TODO Message??
		}

	}

	@Override
	public void checkin(String sid, long docId, String comment, boolean major) {
		SessionUtil.validateSession(sid);

		Map<String, File> uploadedFilesMap = UploadServlet.getReceivedFiles(getThreadLocalRequest());
		File file = uploadedFilesMap.values().iterator().next();
		if (file != null) {
			log.debug("Checking in file " + file.getName());
			// check that we have a valid file for storing as new
			// version
			String fileName = file.getName();

			try {
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setUser(SessionUtil.getSessionUser(sid));
				transaction.setComment(comment);

				// checkin the document; throws an exception if
				// something goes wrong
				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				documentManager.checkin(docId, new FileInputStream(file), fileName, major, false, transaction);

				/* create positive log message */
				// TODO Message?
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				// TODO Message?
			}
		} else {
			// TODO Message?
		}
	}

	@Override
	public void checkout(String sid, long docId) {
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
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("Unable to checkout the document");
		}
	}

	@Override
	public void delete(String sid, long[] ids) {
		SessionUtil.validateSession(sid);

		if (ids.length > 0) {
			DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			boolean skippedSome = false;
			boolean deletedSome = false;
			boolean lockedSome = false;
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
						deletedSome = true;
						continue;
					}

					// The document of the selected documentRecord must be
					// not immutable
					if (doc.getImmutable() == 1 && !transaction.getUser().isInGroup("admin")) {
						skippedSome = true;
						continue;
					}

					// The document must be not locked
					if (doc.getStatus() != Document.DOC_UNLOCKED || doc.getExportStatus() != Document.EXPORT_UNLOCKED) {
						lockedSome = true;
						continue;
					}
					// Check if there are some shortcuts associated to the
					// deleting document. All the shortcuts must be deleted.
					if (dao.findShortcutIds(doc.getId()).size() > 0)
						for (Long shortcutId : dao.findShortcutIds(doc.getId())) {
							dao.delete(shortcutId);
						}
					dao.delete(doc.getId(), transaction);
					deletedSome = true;
				} catch (AccessControlException e) {
					// TODO Message?
				} catch (Exception e) {
					e.printStackTrace();
					// TODO Message?
				}
			}
			if (deletedSome) {
				// TODO Message?
			}
			if (skippedSome || lockedSome) {
				// TODO Message?
			}
		} else {
			// TODO Message?
		}
	}

	@Override
	public void deleteBookmarks(String sid, long[] bookmarkIds) {
		SessionUtil.validateSession(sid);

		BookmarkDAO dao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		for (long id : bookmarkIds) {
			dao.delete(id);
		}

	}

	@Override
	public void deleteDiscussions(String sid, long[] ids) {
		SessionUtil.validateSession(sid);

		DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public void deleteLinks(String sid, long[] ids) {
		SessionUtil.validateSession(sid);

		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public void deletePosts(String sid, long discussionId, int[] postIds) {
		SessionUtil.validateSession(sid);

		try {
			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			DiscussionThread thread = dao.findById(discussionId);
			dao.initialize(thread);
			DiscussionComment comment = null;
			for (int postId : postIds) {
				comment = thread.getComments().get(postId);
				comment.setDeleted(1);
			}
			dao.store(thread);
			// TODO Message?
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// TODO Message?
		}
	}

	@Override
	public GUIExtendedAttribute[] getAttributes(String sid, long templateId) {
		SessionUtil.validateSession(sid);

		DocumentTemplateDAO templateDao = (DocumentTemplateDAO) Context.getInstance()
				.getBean(DocumentTemplateDAO.class);
		DocumentTemplate template = templateDao.findById(templateId);
		GUIExtendedAttribute[] attributes = new GUIExtendedAttribute[template.getAttributeNames().size()];
		int i = 0;
		for (String attrName : template.getAttributeNames()) {
			ExtendedAttribute extAttr = template.getAttributes().get(attrName);
			GUIExtendedAttribute att = new GUIExtendedAttribute();
			att.setName(attrName);
			att.setPosition(extAttr.getPosition());
			att.setMandatory(extAttr.getMandatory() == 1 ? true : false);
			att.setType(extAttr.getType());
			if (extAttr.getValue() instanceof String)
				att.setStringValue(extAttr.getStringValue());
			else if (extAttr.getValue() instanceof Long)
				att.setIntValue(extAttr.getIntValue());
			else if (extAttr.getValue() instanceof Double)
				att.setDoubleValue(extAttr.getDoubleValue());
			else if (extAttr.getValue() instanceof Date)
				att.setDateValue(extAttr.getDateValue());

			attributes[i] = att;
			i++;
		}

		return attributes;
	}

	@Override
	public GUIDocument getById(String sid, long docId) {
		SessionUtil.validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);

		if (doc != null) {
			// Check if it is an alias
			if (doc.getDocRef() != null) {
				long id = doc.getDocRef();
				doc = docDao.findById(id);
			}
			GUIDocument document = new GUIDocument();
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
				document.setTemplateId(doc.getTemplateId());
				if (doc.getTemplate() != null)
					document.setTemplate(doc.getTemplate().getName());
				document.setStatus(doc.getStatus());
				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				document.setPathExtended(mdao.computePathExtended(doc.getFolder().getId()));
				document.setFileSize(new Long(doc.getFileSize()).floatValue());
				if (doc.getCustomId() != null)
					document.setCustomId(doc.getCustomId());
				else
					document.setCustomId("" + doc.getId());

				GUIFolder folder = FolderServiceImpl.getFolder(sid, doc.getFolder().getId());
				document.setFolder(folder);
			} catch (Throwable t) {
				t.printStackTrace();
			}

			return document;
		}

		return null;
	}

	@Override
	public GUIVersion[] getVersionsById(String sid, long id1, long id2) {
		SessionUtil.validateSession(sid);

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
			version1.setTemplate(docVersion.getTemplateName());
			versDao.initialize(docVersion);
			for (String attrName : docVersion.getAttributeNames()) {
				ExtendedAttribute extAttr = docVersion.getAttributes().get(attrName);
				version1.setValue(attrName, extAttr);
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
			version2.setTemplateId(docVersion.getTemplateId());
			version2.setFileSize(new Float(docVersion.getFileSize()));
			version2.setTemplate(docVersion.getTemplateName());
			versDao.initialize(docVersion);
			for (String attrName : docVersion.getAttributeNames()) {
				ExtendedAttribute extAttr = docVersion.getAttributes().get(attrName);
				version2.setValue(attrName, extAttr);
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
	}

	@Override
	public void linkDocuments(String sid, long[] inDocIds, long[] outDocIds) {
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
			} catch (Exception e) {
				// TODO Message?
				log.error("Exception linking documents: " + e.getMessage(), e);
			}
		} else {
			// TODO Message?
		}
	}

	@Override
	public void lock(String sid, long[] docIds, String comment) {
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
			for (long id : docIds) {
				documentManager.lock(id, Document.DOC_LOCKED, transaction);
			}

			/* create positive log message */
			// TODO Message?
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// TODO Message?
		}
	}

	@Override
	public void makeImmutable(String sid, long[] docIds, String comment) {
		SessionUtil.validateSession(sid);

		try {
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			boolean lockedSome = false;
			boolean immutableSome = false;
			for (long id : docIds) {
				Document doc = docDao.findById(id);

				if (doc.getImmutable() == 0) {
					// The document of the selected documentRecord must be
					// not locked
					if (doc.getStatus() != Document.DOC_UNLOCKED || doc.getExportStatus() != Document.EXPORT_UNLOCKED) {
						lockedSome = true;
						continue;
					}

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setComment(comment);
					transaction.setUser(SessionUtil.getSessionUser(sid));

					manager.makeImmutable(id, transaction);
					immutableSome = true;
				}
			}
			if (immutableSome) {
				// TODO Message?
			}
			if (lockedSome) {
				// TODO Message?
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			// TODO Message?
		}
	}

	@Override
	public void markHistoryAsRead(String sid, String event) {
		SessionUtil.validateSession(sid);

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		for (History history : dao.findByUserIdAndEvent(SessionUtil.getSessionUser(sid).getId(), event)) {
			dao.initialize(history);
			history.setNew(0);
			dao.store(history);
		}
	}

	@Override
	public void markIndexable(String sid, long[] docIds) {
		SessionUtil.validateSession(sid);

		try {
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			int indexableCount = 0;
			for (long id : docIds) {
				manager.changeIndexingStatus(docDao.findById(id), AbstractDocument.INDEX_TO_INDEX);
				indexableCount++;
			}

			if (indexableCount > 0) {
				// TODO Message???
			}

		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			// TODO Message???
		}
	}

	@Override
	public void markUnindexable(String sid, long[] docIds) {
		SessionUtil.validateSession(sid);

		try {
			DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
			int unindexableCount = 0;
			for (long id : docIds) {
				manager.changeIndexingStatus(docDao.findById(id), AbstractDocument.INDEX_SKIP);
				unindexableCount++;
			}

			if (unindexableCount > 0) {
				// TODO Message???
			}

		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			// TODO Message???
		}
	}

	@Override
	public int replyPost(String sid, long discussionId, int replyTo, String title, String message) {
		SessionUtil.validateSession(sid);

		DiscussionComment comment = new DiscussionComment();
		comment.setReplyTo(replyTo);
		comment.setSubject(title);
		comment.setBody(message);
		comment.setUserId(SessionUtil.getSessionUser(sid).getId());
		comment.setUserName(SessionUtil.getSessionUser(sid).getFullName());
		try {
			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			DiscussionThread thread = dao.findById(discussionId);
			dao.initialize(thread);
			thread.appendComment(comment);
			dao.store(thread);
			// TODO Message?
			return thread.getReplies();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// TODO Message?
			return 0;
		}
	}

	@Override
	public void restore(String sid, long docId) {
		SessionUtil.validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		docDao.restore(docId);
	}

	@Override
	public GUIDocument save(String sid, GUIDocument document) {
		SessionUtil.validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = null;
		if (document.getId() != 0) {
			doc = docDao.findById(document.getId());
			docDao.initialize(doc);
			doc.setCustomId(document.getCustomId());
			try {
				Document docVO = new Document();
				docVO.setTitle(document.getTitle());
				if (document.getTags().length > 0)
					docVO.setTags(TagUtil.extractTags(document.getTags().toString()));
				docVO.setSourceType(document.getSourceType());
				docVO.setFileName(document.getFileName());
				docVO.setVersion(document.getVersion());
				docVO.setCreation(document.getCreation());
				docVO.setCreator(document.getCreator());
				docVO.setDate(document.getDate());
				docVO.setPublisher(document.getPublisher());
				docVO.setFileVersion(document.getFileVersion());
				docVO.setLanguage(document.getLanguage());
				docVO.setFileSize(document.getFileSize().longValue());
				docVO.setSource(document.getSource());
				docVO.setRecipient(document.getRecipient());
				docVO.setObject(document.getObject());
				docVO.setCoverage(document.getCoverage());
				docVO.setSourceAuthor(document.getSourceAuthor());
				docVO.setSourceDate(document.getSourceDate());
				docVO.setSourceId(document.getSourceId());
				if (document.getTemplateId() != null) {
					// DocumentTemplateDAO templateDao = (DocumentTemplateDAO)
					// Context.getInstance().getBean(
					// DocumentTemplateDAO.class);
					// DocumentTemplate template =
					// templateDao.findById(document.getTemplateId());
					docVO.setTemplateId(document.getTemplateId());
					// docVO.setTemplate(template);
				}
				docVO.setStatus(document.getStatus());
				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				docVO.setFolder(mdao.findById(document.getFolder().getId()));

				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(History.EVENT_CHANGED);
				transaction.setComment("");
				transaction.setUser(SessionUtil.getSessionUser(sid));

				DocumentManager documentManager = (DocumentManager) Context.getInstance()
						.getBean(DocumentManager.class);
				documentManager.update(doc, docVO, transaction);

				document.setId(doc.getId());
			} catch (Throwable t) {
				t.printStackTrace();
			}
		} else
			return null;

		return document;

	}

	@Override
	public String sendAsEmail(String sid, GUIEmail email) {
		SessionUtil.validateSession(sid);

		EMail mail;
		try {
			mail = new EMail();

			mail.setAccountId(-1);
			mail.setAuthor(email.getUser().getUserName());
			mail.setAuthorAddress(email.getUser().getEmail());
			mail.parseRecipients(email.getRecipients());
			mail.parseRecipientsCC(email.getCc());
			mail.setFolder("outbox");
			mail.setMessageText(email.getMessage());
			mail.setRead(1);
			mail.setSentDate(new Date());
			mail.setSubject(email.getObject());
			mail.setUserName(email.getUser().getUserName());

			if (!email.isSendAdTicket() && email.getDocId() > 0)
				createAttachment(mail, email.getDocId());

			try {
				EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
				sender.send(mail);
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
		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		File file = manager.getDocumentFile(doc);
		att.setFile(file);
		att.setFileName(doc.getFileName());
		String extension = doc.getFileExtension();
		att.setMimeType(MimeType.get(extension));

		if (att != null) {
			email.addAttachment(2, att);
		}
	}

	@Override
	public long startDiscussion(String sid, long docId, String title, String message) {
		SessionUtil.validateSession(sid);

		try {
			DiscussionThread thread = new DiscussionThread();
			thread.setDocId(docId);
			thread.setCreatorId(SessionUtil.getSessionUser(sid).getId());
			thread.setCreatorName(SessionUtil.getSessionUser(sid).getFullName());
			thread.setLastPost(thread.getCreation());

			DiscussionComment firstComment = new DiscussionComment();
			firstComment.setSubject(title);
			firstComment.setBody(message);
			firstComment.setUserId(thread.getCreatorId());
			firstComment.setUserName(thread.getCreatorName());
			firstComment.setDate(thread.getLastPost());
			thread.getComments().add(firstComment);
			thread.setSubject(title);

			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			dao.store(thread);

			// TODO Message?

			return thread.getId();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			// TODO Message?
			return 0;
		}
	}

	@Override
	public void unlock(String sid, long[] docIds) {
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

			/* create positive log message */
			// TODO Message?
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// TODO Message?
		}
	}

	@Override
	public void updateBookmark(String sid, GUIBookmark bookmark) {
		SessionUtil.validateSession(sid);

		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		Bookmark bk;
		if (bookmark.getId() != 0) {
			bk = bookmarkDao.findById(bookmark.getId());
			bookmarkDao.initialize(bk);
		} else
			bk = new Bookmark();

		bk.setTitle(bookmark.getName());
		bk.setDescription(bookmark.getDescription());

		bookmarkDao.store(bk);
		bookmark.setId(bk.getId());
	}

	@Override
	public void updateLink(String sid, long id, String type) {
		SessionUtil.validateSession(sid);

		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		DocumentLink link = dao.findById(id);
		link.setType(type);
		dao.store(link);
	}
}