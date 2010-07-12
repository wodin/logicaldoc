package com.logicaldoc.webservice.folder;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
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
		Menu parentMenu = folderDao.findById(folder.getParentId());
		checkPermission(Permission.ADD_CHILD, user, folder.getParentId());

		Menu menu = new Menu();
		menu.setText(folder.getText());
		menu.setDescription(folder.getDescription());
		menu.setParentId(folder.getParentId());
		menu.setSort(1);
		menu.setIcon("folder.png");
		menu.setType(Menu.MENUTYPE_DIRECTORY);
		menu.setRef("");
		menu.setMenuGroup(parentMenu.getMenuGroupIds());

		boolean stored = folderDao.store(menu);
		// Add a folder history entry
		History transaction = new History();
		transaction.setUser(user);
		transaction.setEvent(History.EVENT_FOLDER_CREATED);
		transaction.setSessionId(sid);
		stored = folderDao.store(menu, transaction);

		if (!stored) {
			log.error("Folder " + menu.getText() + " not created");
			throw new Exception("error");
		} else {
			log.info("Created folder " + menu.getText());
		}

		return WSFolder.fromFolder(menu);
	}

	@Override
	public void delete(String sid, long folderId) throws Exception {
		User user = validateSession(sid);
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Menu folder = folderDao.findById(folderId);
		Menu parentMenu = folderDao.findById(folder.getParentId());
		checkPermission(Permission.DELETE, user, parentMenu.getParentId());
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
		Menu folder = folderDao.findById(folderId);
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
		List<Menu> folders = folderDao.findByParentId(folderId);
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
		Menu destParentFolder = folderDao.findById(parentId);
		Menu folderToMove = folderDao.findById(folderId);

		// Check destParentId: Must be different from the current folder
		// parentId
		if (parentId == folderToMove.getParentId())
			throw new SecurityException("No Changes");

		// Check destParentId: Must be different from the current folder Id
		// A folder cannot be children of herself
		if (parentId == folderToMove.getId())
			throw new SecurityException("Not Allowed");

		// Check delete permission on the folder parent of folderToMove
		Menu sourceParent = folderDao.findById(folderToMove.getParentId());
		boolean sourceParentDeleteEnabled = folderDao.isPermissionEnabled(Permission.DELETE, sourceParent.getId(), user
				.getId());
		if (!sourceParentDeleteEnabled)
			throw new SecurityException("No rights to delete folder");

		// Check addChild permission on destParentFolder
		boolean addchildEnabled = folderDao.isPermissionEnabled(Permission.ADD_CHILD, destParentFolder.getId(), user
				.getId());
		if (!addchildEnabled)
			throw new SecurityException("AddChild Rights not granted to this user");

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

		if (folderId == Menu.MENUID_DOCUMENTS)
			throw new Exception("cannot rename the root folder");

		Menu menu = folderDao.findById(folderId);
		if (menu == null)
			throw new Exception("cannot find folder " + folderId);

		List<Menu> folders = folderDao.findByTextAndParentId(name, menu.getParentId());
		if (folders.size() > 0 && folders.get(0).getId() != menu.getId()) {
			throw new Exception("duplicate folder name " + name);
		} else {
			menu.setText(name);
			// Add a folder history entry
			History transaction = new History();
			transaction.setUser(user);
			transaction.setEvent(History.EVENT_FOLDER_RENAMED);
			transaction.setSessionId(sid);
			folderDao.store(menu, transaction);
		}
	}
}