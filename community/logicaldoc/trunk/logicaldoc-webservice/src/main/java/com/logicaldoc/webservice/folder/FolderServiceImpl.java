package com.logicaldoc.webservice.folder;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.AbstractService;

/**
 * Folder Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class FolderServiceImpl extends AbstractService implements FolderService {

	public static Log log = LogFactory.getLog(FolderServiceImpl.class);

	@Override
	public WSFolder create(String sid, WSFolder folder) throws Exception {
		User user = validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder parentFolder = folderDao.findById(folder.getParentId());
		if (parentFolder == null)
			throw new Exception("A parent folder with id " + folder.getParentId() + " was not found.");
		checkPermission(Permission.ADD, user, folder.getParentId());

		// Add a folder history entry
		History transaction = new History();
		transaction.setUser(user);
		transaction.setSessionId(sid);

		Folder f = folderDao.create(parentFolder, folder.getName(), transaction);
		f.setDescription(folder.getDescription());
		boolean stored = folderDao.store(f);

		if (!stored) {
			log.error("Folder " + f.getName() + " not created");
			throw new Exception("error");
		} else {
			log.info("Created folder " + f.getName());
		}

		return WSFolder.fromFolder(f);
	}

	@Override
	public void delete(String sid, long folderId) throws Exception {
		User user = validateSession(sid);
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = folderDao.findById(folderId);
		Folder parentFolder = folderDao.findById(folder.getParentId());
		checkPermission(Permission.DELETE, user, parentFolder.getParentId());
		try {
			// Add a folder history entry
			History transaction = new History();
			transaction.setUser(user);
			transaction.setEvent(History.EVENT_FOLDER_DELETED);
			transaction.setSessionId(sid);
			folderDao.deleteTree(folderId, transaction);
		} catch (Exception e) {
			log.error("Some elements were not deleted");
			throw new Exception("error");
		}
	}

	@Override
	public WSFolder getFolder(String sid, long folderId) throws Exception {
		User user = validateSession(sid);
		checkReadEnable(user, folderId);
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = folderDao.findById(folderId);
		folderDao.initialize(folder);

		return WSFolder.fromFolder(folder);
	}

	@Override
	public boolean isReadable(String sid, long folderId) throws Exception {
		User user = validateSession(sid);
		try {
			checkReadEnable(user, folderId);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public WSFolder[] list(String sid, long folderId) throws Exception {
		User user = validateSession(sid);
		checkReadEnable(user, folderId);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Folder> folders = folderDao.findByParentId(folderId);
		WSFolder[] wsFolders = new WSFolder[folders.size()];
		for (int i = 0; i < folders.size(); i++) {
			folderDao.initialize(folders.get(i));
			wsFolders[i] = WSFolder.fromFolder(folders.get(i));
		}

		return wsFolders;
	}

	@Override
	public void move(String sid, long folderId, long parentId) throws Exception {
		User user = validateSession(sid);
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder destParentFolder = folderDao.findById(parentId);
		Folder folderToMove = folderDao.findById(folderId);

		// Check destParentId: Must be different from the current folder
		// parentId
		if (parentId == folderToMove.getParentId())
			throw new SecurityException("No Changes");

		// Check destParentId: Must be different from the current folder Id
		// A folder cannot be children of herself
		if (parentId == folderToMove.getId())
			throw new SecurityException("Not Allowed");

		// Check delete permission on the folder parent of folderToMove
		Folder sourceParent = folderDao.findById(folderToMove.getParentId());
		boolean sourceParentDeleteEnabled = folderDao.isPermissionEnabled(Permission.DELETE, sourceParent.getId(),
				user.getId());
		if (!sourceParentDeleteEnabled)
			throw new SecurityException("No rights to delete folder");

		// Check add permission on destParentFolder
		boolean addEnabled = folderDao.isPermissionEnabled(Permission.ADD, destParentFolder.getId(), user.getId());
		if (!addEnabled)
			throw new SecurityException("Add Rights not granted to this user");

		// Add a folder history entry
		History transaction = new History();
		transaction.setSessionId(sid);
		transaction.setUser(user);

		folderDao.move(folderToMove, destParentFolder, transaction);
	}

	@Override
	public void rename(String sid, long folderId, String name) throws Exception {
		User user = validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!folderDao.isPermissionEnabled(Permission.RENAME, folderId, user.getId())) {
			throw new Exception("user does't have rename permission");
		}

		if (folderId == Folder.ROOTID)
			throw new Exception("cannot rename the root folder");

		Folder folder = folderDao.findById(folderId);
		if (folder == null)
			throw new Exception("cannot find folder " + folderId);

		List<Folder> folders = folderDao.findByNameAndParentId(name, folder.getParentId());
		if (folders.size() > 0 && folders.get(0).getId() != folder.getId()) {
			throw new Exception("duplicate folder name " + name);
		} else {
			folder.setName(name);
			// Add a folder history entry
			History transaction = new History();
			transaction.setUser(user);
			transaction.setEvent(History.EVENT_FOLDER_RENAMED);
			transaction.setSessionId(sid);
			folderDao.store(folder, transaction);
		}
	}
}