package com.logicaldoc.dropbox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxEntry;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderHistory;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.frontend.client.services.DropboxService;
import com.logicaldoc.util.Context;

/**
 * Implementation of the DropboxService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.0
 */
public class DropboxServiceImpl extends RemoteServiceServlet implements DropboxService {
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(DropboxServiceImpl.class);

	@Override
	public boolean isConnected(String sid) throws ServerException {
		SessionUtil.validateSession(sid);

		try {
			User user = SessionUtil.getSessionUser(sid);
			Dropbox dbox = new Dropbox();
			String accessToken = loadAccessToken(user);
			if (accessToken == null)
				return false;
			return dbox.login(accessToken, user.getLocale());
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public String startAuthorization(String sid) throws ServerException {
		SessionUtil.validateSession(sid);

		try {
			User user = SessionUtil.getSessionUser(sid);
			Dropbox dbox = new Dropbox();
			return dbox.startAuthorization(user.getLocale());
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public String finishAuthorization(String sid, String authorizationCode) throws ServerException {
		SessionUtil.validateSession(sid);

		try {
			User user = SessionUtil.getSessionUser(sid);
			Dropbox dbox = new Dropbox();
			String token = dbox.finishAuthorization(authorizationCode, user.getLocale());
			if (token == null)
				return null;
			dbox.login(token, user.getLocale());
			String account = dbox.getAccountName();
			saveAccessToken(user, token, account);
			return account;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	/**
	 * Loads the access token saved for the given user.
	 */
	static String loadAccessToken(User user) {
		GenericDAO dao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);
		Generic generic = dao.findByAlternateKey("dropbox", "token", user.getId(), user.getTenantId());
		if (generic == null)
			return null;
		else
			return generic.getString1();
	}

	/**
	 * Saves the access token saved for the given user. The token is saved in a
	 * Generic(type: dropbox, subtype: token)
	 */
	protected void saveAccessToken(User user, String token, String account) {
		GenericDAO dao = (GenericDAO) Context.getInstance().getBean(GenericDAO.class);
		Generic generic = dao.findByAlternateKey("dropbox", "token", user.getId(), user.getTenantId());
		if (generic == null)
			generic = new Generic("dropbox", "token", user.getId(), user.getTenantId());
		generic.setString1(token);
		generic.setString2(account);
		dao.store(generic);
	}

	@Override
	public boolean exportDocuments(String sid, String targetPath, long[] folderIds, long[] docIds)
			throws ServerException {
		SessionUtil.validateSession(sid);

		try {
			User user = SessionUtil.getSessionUser(sid);
			Dropbox dbox = new Dropbox();
			String token = loadAccessToken(user);
			if (token == null)
				return false;
			dbox.login(token, user.getLocale());

			DbxEntry entry = dbox.get(targetPath);
			if (entry == null || entry instanceof DbxEntry.File)
				return false;

			if (!targetPath.endsWith("/"))
				targetPath += "/";

			FolderDAO fDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

			// Prepare a map docId-path
			Map<Long, String> documents = new HashMap<Long, String>();

			// First of all put all single selected documents
			List<Long> dIds = new ArrayList<Long>();
			for (int i = 0; i < docIds.length; i++)
				dIds.add(docIds[i]);

			for (Document document : docDao.findByIds(dIds.toArray(new Long[0]), null))
				documents.put(document.getId(), document.getFileName());

			/*
			 * Now browse all the tree adding the documents
			 */

			// Prepare a map folderId-basepath
			Map<Long, String> folders = new HashMap<Long, String>();
			for (long folderId : folderIds) {
				Folder folder = fDao.findById(folderId);
				if (folder == null || !fDao.isPermissionEnabled(Permission.DOWNLOAD, folderId, user.getId()))
					continue;

				loadFoldersTree(folderId, folder.getName() + "/", user.getId(), folders);
			}
			for (Long folderId : folders.keySet()) {
				List<Document> folderDocs = docDao.findByFolder(folderId, null);
				for (Document doc : folderDocs)
					documents.put(doc.getId(), folders.get(folderId) + doc.getFileName());
			}

			for (Long docId : documents.keySet()) {
				uploadDocument(docId, targetPath + documents.get(docId), dbox, user, sid);
			}

			return true;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return false;
		}
	}

	private void uploadDocument(Long docId, String path, Dropbox dropbox, User user, String sid) throws IOException {
		Storer store = (Storer) Context.getInstance().getBean(Storer.class);
		HistoryDAO hdao = (HistoryDAO) Context.getInstance().getBean(HistoryDAO.class);
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);

		File temp = null;
		try {
			temp = File.createTempFile("dboxupload", ".tmp");
			store.writeToFile(docId, store.getResourceName(docId, null, null), temp);
			dropbox.uploadFile(temp, path, true);

			Document doc = ddao.findById(docId);

			// Add an history entry to track the download of the document
			History history = new History();
			history.setDocId(doc.getId());
			history.setTitle(doc.getTitle());
			history.setVersion(doc.getVersion());
			history.setFilename(doc.getFileName());
			history.setFolderId(doc.getFolder().getId());
			history.setComment("Exported into Dropbox");
			history.setUser(user);
			history.setSessionId(sid);

			FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			history.setPath(fdao.computePathExtended(doc.getFolder().getId()));
			history.setEvent(DocumentEvent.DOWNLOADED.toString());
			hdao.store(history);
		} finally {
			FileUtils.deleteQuietly(temp);
		}
	}

	private void loadFoldersTree(long parentId, String parentPath, long userId, Map<Long, String> folders) {
		FolderDAO fDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		folders.put(parentId, parentPath);
		for (Folder folder : fDao.findChildren(parentId, userId)) {
			if (parentId == folder.getId())
				continue;
			else
				loadFoldersTree(folder.getId(), parentPath + folder.getName() + "/", userId, folders);
		}
	}

	@Override
	public int importDocuments(String sid, long targetFolder, String[] paths) throws ServerException {
		UserSession session = SessionUtil.validateSession(sid);
		FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		if (!fdao.isPermissionEnabled(Permission.IMPORT, targetFolder, session.getUserId()))
			return 0;

		int count = 0;
		try {
			User user = SessionUtil.getSessionUser(sid);
			Dropbox dbox = new Dropbox();
			String token = loadAccessToken(user);
			if (token == null)
				return 0;
			dbox.login(token, user.getLocale());

			Folder root = fdao.findById(targetFolder);

			Set<String> imported = new HashSet<String>();
			for (String path : paths) {
				if (imported.contains(path))
					continue;

				DbxEntry entry = dbox.get(path);
				if (entry instanceof DbxEntry.File) {
					importDocument(root, (DbxEntry.File) entry, dbox, user, sid);
					imported.add(entry.path);
				} else {
					String rootPath = entry.path;
					if (!rootPath.endsWith("/"))
						rootPath += "/";

					List<DbxEntry> files = dbox.listFilesInTree(rootPath);
					for (DbxEntry file : files) {
						if (imported.contains(file.path))
							continue;

						FolderHistory transaction = new FolderHistory();
						transaction.setSessionId(sid);
						transaction.setUser(user);

						String folderPath = file.path.substring(rootPath.length());
						folderPath = FilenameUtils.getPath(file.path);
						folderPath = folderPath.replaceAll("\\\\", "/");

						Folder folder = fdao.createPath(root, folderPath, true, transaction);

						importDocument(folder, file, dbox, user, sid);
						imported.add(file.path);
					}
				}
			}

			count = imported.size();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}

		return count;
	}

	private void importDocument(Folder root, DbxEntry src, Dropbox dbox, User user, String sid) throws Exception {
		DocumentDAO ddao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		DocumentManager manager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);

		File temp = null;
		try {
			temp = File.createTempFile("dboxdownload", ".tmp");
			dbox.downloadFile(src.path, temp);

			List<Document> docs = ddao.findByFileNameAndParentFolderId(root.getId(), src.name, null,
					root.getTenantId(), null);
			if (docs.size() == 1) {
				/*
				 * Checkout and checkin an existing document
				 */
				Document doc = docs.get(0);

				History history = new History();
				history.setFolderId(root.getId());
				history.setUser(user);
				history.setSessionId(sid);

				FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
				String pathExtended = fdao.computePathExtended(root.getId());
				history.setPath(pathExtended);

				manager.checkout(doc.getId(), history);

				history = new History();
				history.setFolderId(root.getId());
				history.setUser(user);
				history.setSessionId(sid);
				history.setPath(pathExtended);
				history.setComment("Updated from Dropbox");

				manager.checkin(doc.getId(), temp, doc.getFileName(), false, null, history);
			} else {
				/*
				 * Create a new document
				 */

				Document docVO = new Document();
				docVO.setTitle(FilenameUtils.getBaseName(src.name));
				docVO.setFileName(src.name);
				docVO.setFolder(root);
				docVO.setLanguage(user.getLanguage());

				History history = new History();
				history.setFolderId(root.getId());
				history.setComment("Imported from Dropbox");
				history.setUser(user);
				history.setSessionId(sid);

				FolderDAO fdao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
				history.setPath(fdao.computePathExtended(root.getId()));
				history.setEvent(DocumentEvent.STORED.toString());

				manager.create(temp, docVO, history);
			}
		} finally {
			FileUtils.deleteQuietly(temp);
		}
	}
}