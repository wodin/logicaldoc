package com.logicaldoc.web.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import com.logicaldoc.util.TagUtil;
import com.logicaldoc.util.config.MimeTypeConfig;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.AbstractService;

/**
 * Implementation of the DocumentService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class DocumentServiceImpl extends AbstractService implements DocumentService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(DocumentServiceImpl.class);

	@Override
	public void addBookmarks(String sid, long[] docIds) {
		validateSession(sid);

		BookmarkDAO bookmarkDao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		DocumentDAO dao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		int added = 0;
		int alreadyAdded = 0;
		for (long id : docIds) {
			try {
				Bookmark bookmark = null;
				if (bookmarkDao.findByUserIdAndDocId(getSessionUser(sid).getId(), id).size() > 0) {
					// The bookmark already exists
					alreadyAdded++;
				} else {
					Document doc = dao.findById(id);
					bookmark = new Bookmark();
					bookmark.setTitle(doc.getTitle());
					bookmark.setUserId(getSessionUser(sid).getId());
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
		validateSession(sid);

		File[] uploadedFiles = listUploadedFiles();
		log.debug("Uploading " + uploadedFiles.length + "files");

		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		final Menu parent = menuDao.findById(folderId);
		try {
			for (File file : uploadedFiles) {
				if (file.getName().endsWith(".zip") && importZip) {
					log.debug("file = " + file);

					PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
					String path = conf.getProperty("conf.userdir");

					if (!path.endsWith("_")) {
						path += "_";
					}
					path += getSessionUser(sid).getUserName() + "_" + File.separator;

					FileUtils.forceMkdir(new File(path));

					// copy the file into the user folder
					final File destFile = new File(path, file.getName());
					FileUtils.copyFile(file, destFile);

					final long userId = getSessionUser(sid).getId();
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

					String title = file.getName().substring(0, file.getName().lastIndexOf("."));

					// Create the document history event
					History transaction = new History();
					transaction.setSessionId(sid);
					transaction.setEvent(History.EVENT_STORED);
					transaction.setUser(getSessionUser(sid));

					Document doc = new Document();
					doc.setFileName(file.getName());
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
		validateSession(sid);

		File file = listUploadedFiles()[0];
		log.debug("Checking in file " + file.getName());

		if (file != null) {
			// check that we have a valid file for storing as new
			// version
			String fileName = file.getName();

			try {
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setUser(getSessionUser(sid));
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

	private File[] listUploadedFiles() {
		String path = getServletContext().getRealPath("/upload/" + getThreadLocalRequest().getSession().getId());
		File uploadFolder = new File(path);

		return uploadFolder.listFiles();
	}

	@Override
	public void checkout(String sid, long docId) {
		validateSession(sid);

		// Create the document history event
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setEvent(History.EVENT_CHECKEDOUT);
		transaction.setComment("");
		transaction.setUser(getSessionUser(sid));

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
		validateSession(sid);

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
					transaction.setUser(getSessionUser(sid));

					// If it is a shortcut, we delete only the shortcut
					if (doc.getDocRef() != null) {
						transaction.setEvent(History.EVENT_SHORTCUT_DELETED);
						doc = dao.findById(doc.getDocRef());
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
					// The document of the selected documentRecord must be
					// not locked
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
		validateSession(sid);

		BookmarkDAO dao = (BookmarkDAO) Context.getInstance().getBean(BookmarkDAO.class);
		for (long id : bookmarkIds) {
			dao.delete(id);
		}

	}

	@Override
	public void deleteDiscussions(String sid, long[] ids) {
		validateSession(sid);

		DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public void deleteLinks(String sid, long[] ids) {
		validateSession(sid);

		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		for (long id : ids) {
			dao.delete(id);
		}
	}

	@Override
	public void deletePosts(String sid, long discussionId, int[] postIds) {
		validateSession(sid);

		try {
			// TODO the 'postid' is the position of each comment into the
			// thread???
			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			DiscussionThread thread = dao.findById(discussionId);
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
		validateSession(sid);

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
			attributes[i] = att;
			i++;
		}

		return attributes;
	}

	@Override
	public GUIDocument getById(String sid, long docId) {
		validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc = docDao.findById(docId);

		if (doc != null) {
			GUIDocument document = new GUIDocument();
			document.setId(docId);
			document.setTitle(doc.getTitle());
			document.setCustomId(doc.getCustomId());
			document.setTags(doc.getTags().toArray(new String[doc.getTags().size()]));
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
			document.setTemplate(doc.getTemplate().getName());
			document.setStatus(doc.getStatus());
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			document.setPathExtended(mdao.computePathExtended(doc.getFolder().getId()));
			GUIFolder folder = new GUIFolder();
			Menu docFolder = doc.getFolder();
			folder.setName(docFolder.getText());
			folder.setId(docFolder.getId());

			// TODO Fixed after complete implementation
			if (docId % 2 == 0)
				folder.setPermissions(new String[] { "read", "write", "addChild", "manageSecurity", "delete", "rename",
						"bulkImport", "bulkExport", "sign", "archive", "workflow", "manageImmutability" });
			else
				folder.setPermissions(new String[] { "read" });

			document.setFolder(folder);

			return document;
		}

		return null;
	}

	@Override
	public GUIVersion[] getVersionsById(String sid, long id1, long id2) {
		validateSession(sid);

		GUIVersion[] versions = new GUIVersion[2];

		VersionDAO versDao = (VersionDAO) Context.getInstance().getBean(VersionDAO.class);
		Version docVersion = versDao.findById(id1);

		GUIVersion version = new GUIVersion();
		version.setUsername(docVersion.getUsername());
		version.setComment(docVersion.getComment());
		version.setId(id1);
		version.setTitle(docVersion.getTitle());
		version.setCustomId(docVersion.getCustomId());
		version.setTags(docVersion.getTags().toArray(new String[docVersion.getTags().size()]));
		version.setType(docVersion.getType());
		version.setFileName(docVersion.getFileName());
		version.setVersion(docVersion.getVersion());
		version.setCreation(docVersion.getCreation());
		version.setCreator(docVersion.getCreator());
		version.setDate(docVersion.getDate());
		version.setPublisher(docVersion.getPublisher());
		version.setFileVersion(docVersion.getFileVersion());
		version.setLanguage(docVersion.getLanguage());
		version.setTemplateId(docVersion.getTemplateId());
		version.setFileSize(new Float(docVersion.getFileSize()));
		version.setTemplate(docVersion.getTemplateName());
		for (String attrName : docVersion.getAttributeNames()) {
			ExtendedAttribute extAttr = docVersion.getAttributes().get(attrName);
			version.setValue(attrName, extAttr);
		}
		GUIFolder folder = new GUIFolder();
		folder.setName(docVersion.getFolderName());
		folder.setId(docVersion.getFolderId());
		version.setFolder(folder);
		versions[0] = version;

		docVersion = versDao.findById(id2);

		version = new GUIVersion();
		version.setUsername(docVersion.getUsername());
		version.setComment(docVersion.getComment());
		version.setId(id1);
		version.setTitle(docVersion.getTitle());
		version.setCustomId(docVersion.getCustomId());
		version.setTags(docVersion.getTags().toArray(new String[docVersion.getTags().size()]));
		version.setType(docVersion.getType());
		version.setFileName(docVersion.getFileName());
		version.setVersion(docVersion.getVersion());
		version.setCreation(docVersion.getCreation());
		version.setCreator(docVersion.getCreator());
		version.setDate(docVersion.getDate());
		version.setPublisher(docVersion.getPublisher());
		version.setFileVersion(docVersion.getFileVersion());
		version.setLanguage(docVersion.getLanguage());
		version.setTemplateId(docVersion.getTemplateId());
		version.setFileSize(new Float(docVersion.getFileSize()));
		version.setTemplate(docVersion.getTemplateName());
		for (String attrName : docVersion.getAttributeNames()) {
			ExtendedAttribute extAttr = docVersion.getAttributes().get(attrName);
			version.setValue(attrName, extAttr);
		}
		folder = new GUIFolder();
		folder.setName(docVersion.getFolderName());
		folder.setId(docVersion.getFolderId());
		version.setFolder(folder);
		versions[1] = version;

		return versions;
	}

	@Override
	public void linkDocuments(String sid, long[] inDocIds, long[] outDocIds) {
		validateSession(sid);

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
		validateSession(sid);

		// Unlock the document; throws an exception if something
		// goes wrong
		DocumentManager documentManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		try {
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setEvent(History.EVENT_LOCKED);
			transaction.setUser(getSessionUser(sid));
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
		validateSession(sid);

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
					transaction.setUser(getSessionUser(sid));

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
		validateSession(sid);

		HistoryDAO dao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		for (History history : dao.findByUserIdAndEvent(getSessionUser(sid).getId(), event)) {
			dao.initialize(history);
			history.setNew(0);
			dao.store(history);
		}
	}

	@Override
	public void markIndexable(String sid, long[] docIds) {
		validateSession(sid);

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
		validateSession(sid);

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
		validateSession(sid);

		DiscussionComment comment = new DiscussionComment();
		comment.setReplyTo(replyTo);
		comment.setSubject(title);
		comment.setBody(message);
		comment.setUserId(getSessionUser(sid).getId());
		comment.setUserName(getSessionUser(sid).getFullName());
		try {
			DiscussionThreadDAO dao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
			DiscussionThread thread = dao.findById(discussionId);
			thread.appendComment(comment);
			dao.store(thread);
			// TODO Message?
			return replyTo;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// TODO Message?
			return 0;
		}
	}

	@Override
	public void restore(String sid, long docId) {
		validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		docDao.restore(docId);
	}

	@Override
	public GUIDocument save(String sid, GUIDocument document) {
		validateSession(sid);

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document doc;
		if (document.getId() != 0) {
			doc = docDao.findById(document.getId());
			docDao.initialize(doc);
		} else
			doc = new Document();

		doc.setTitle(document.getTitle());
		doc.setCustomId(document.getCustomId());
		// TODO It works???
		doc.setTags(TagUtil.extractTags(document.getTags().toString()));
		doc.setType(document.getType());
		doc.setFileName(document.getFileName());
		doc.setVersion(document.getVersion());
		doc.setCreation(document.getCreation());
		doc.setCreator(document.getCreator());
		doc.setDate(document.getDate());
		doc.setPublisher(document.getPublisher());
		doc.setFileVersion(document.getFileVersion());
		doc.setLanguage(document.getLanguage());
		DocumentTemplateDAO templateDao = (DocumentTemplateDAO) Context.getInstance()
				.getBean(DocumentTemplateDAO.class);
		DocumentTemplate template = templateDao.findById(document.getTemplateId());
		doc.setTemplateId(template.getId());
		doc.setTemplate(template);
		doc.setStatus(document.getStatus());
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		doc.setFolder(mdao.findById(document.getFolder().getId()));

		docDao.store(doc);
		doc.setId(document.getId());

		return document;
	}

	@Override
	public String sendAsEmail(String sid, GUIEmail email) {
		validateSession(sid);

		EMail mail;
		try {
			mail = new EMail();

			mail.setAccountId(-1);
			mail.setAuthor(email.getUser().getUserName());
			// TODO It is correct???
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
				// TODO Why do not return the email sent message???
				// I18N.message("email.sent");
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
		MimeTypeConfig mtc = (MimeTypeConfig) Context.getInstance().getBean(MimeTypeConfig.class);
		String mimetype = mtc.getMimeApp(extension);

		if ((mimetype == null) || mimetype.equals("")) {
			mimetype = "application/octet-stream";
		}

		att.setMimeType(mimetype);

		if (att != null) {
			email.addAttachment(2, att);
		}
	}

	@Override
	public long startDiscussion(String sid, long docId, String title, String message) {
		validateSession(sid);

		try {
			DiscussionThread thread = new DiscussionThread();
			thread.setDocId(docId);
			thread.setCreatorId(getSessionUser(sid).getId());
			thread.setCreatorName(getSessionUser(sid).getFullName());
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
		validateSession(sid);

		try {
			// Create the document history event
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(getSessionUser(sid));

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
		validateSession(sid);

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
		validateSession(sid);

		DocumentLinkDAO dao = (DocumentLinkDAO) Context.getInstance().getBean(DocumentLinkDAO.class);
		DocumentLink link = dao.findById(id);
		link.setType(type);
		dao.store(link);
	}
}