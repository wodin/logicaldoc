package com.logicaldoc.webservice.folder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.FolderEvent;
import com.logicaldoc.core.security.FolderGroup;
import com.logicaldoc.core.security.FolderHistory;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.AbstractService;
import com.logicaldoc.webservice.auth.Right;

/**
 * Folder Web Service Implementation
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class FolderServiceImpl extends AbstractService implements FolderService {

	protected static Logger log = LoggerFactory.getLogger(FolderServiceImpl.class);

	@Override
	public WSFolder create(String sid, WSFolder folder) throws Exception {
		User user = validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder parentFolder = folderDao.findById(folder.getParentId());
		if (parentFolder == null)
			throw new Exception("A parent folder with id " + folder.getParentId() + " was not found.");
		checkPermission(Permission.ADD, user, folder.getParentId());

		// Add a folder history entry
		FolderHistory transaction = new FolderHistory();
		transaction.setUser(user);
		transaction.setSessionId(sid);

		Folder folderVO = new Folder();
		folderVO.setName(folder.getName());
		folderVO.setDescription(folder.getDescription());
		folderVO.setType(folder.getType());
		folder.updateExtendedAttributes(folderVO);
		Folder f = folderDao.create(parentFolder, folderVO, true, transaction);

		if (f == null) {
			log.error("Folder " + folderVO.getName() + " not created");
			throw new Exception("error");
		} else {
			log.info("Created folder " + f.getName());
		}

		return WSFolder.fromFolder(f);
	}

	@Override
	public void delete(String sid, long folderId) throws Exception {
		User user = validateSession(sid);

		if (folderId == Folder.ROOTID || folderId == Folder.DEFAULTWORKSPACE)
			throw new Exception("Cannot delete root folder or Default workspace");

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = folderDao.findById(folderId);
		Folder parentFolder = folderDao.findById(folder.getParentId());
		checkPermission(Permission.DELETE, user, parentFolder.getParentId());
		// Add a folder history entry
		FolderHistory transaction = new FolderHistory();
		transaction.setUser(user);
		transaction.setEvent(FolderEvent.DELETED.toString());
		transaction.setSessionId(sid);
		List<Folder> notDeletedFolder = folderDao.deleteTree(folder, transaction);
		if (notDeletedFolder.contains(folder)) {
			throw new Exception("User " + user.getUserName() + " cannot delete folder " + folderId);
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
	public WSFolder findByPath(String sid, String path) throws Exception {
		User user = validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder folder = folderDao.findByPath(path);
		if (folder == null)
			return null;

		folderDao.initialize(folder);
		checkReadEnable(user, folder.getId());
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
	public WSFolder[] listChildren(String sid, long folderId) throws Exception {
		User user = validateSession(sid);
		checkReadEnable(user, folderId);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Folder> folders = folderDao.findChildren(folderId, user.getId());
		WSFolder[] wsFolders = new WSFolder[folders.size()];
		for (int i = 0; i < folders.size(); i++) {
			folderDao.initialize(folders.get(i));
			wsFolders[i] = WSFolder.fromFolder(folders.get(i));
		}

		return wsFolders;
	}

	@Override
	public void move(String sid, long folderId, long parentId) throws Exception {
		if (parentId == Folder.ROOTID) {
			log.error("Cannot move folders in the root");
			throw new Exception("Cannot move folders in the root");
		}

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
		FolderHistory transaction = new FolderHistory();
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

		if (folderId == Folder.DEFAULTWORKSPACE)
			throw new Exception("cannot rename the Default workspace");

		Folder folder = folderDao.findById(folderId);
		if (folder == null)
			throw new Exception("cannot find folder " + folderId);

		folderDao.initialize(folder);

		List<Folder> folders = folderDao.findByNameAndParentId(name, folder.getParentId());
		if (folders.size() > 0 && folders.get(0).getId() != folder.getId()) {
			throw new Exception("duplicate folder name " + name);
		} else {
			// Add a folder history entry
			FolderHistory transaction = new FolderHistory();
			transaction.setUser(user);
			transaction.setEvent(FolderEvent.RENAMED.toString());
			transaction.setSessionId(sid);
			transaction.setTitleOld(folder.getName());

			folder.setName(name);
			folderDao.store(folder, transaction);
		}
	}

	@Override
	public WSFolder getRootFolder(String sid) throws Exception {
		return getFolder(sid, Folder.ROOTID);
	}

	@Override
	public WSFolder getDefaultWorkspace(String sid) throws Exception {
		return getFolder(sid, Folder.DEFAULTWORKSPACE);
	}

	@Override
	public boolean isWriteable(String sid, long folderId) throws Exception {
		User user = validateSession(sid);
		try {
			checkWriteEnable(user, folderId);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isGranted(String sid, long folderId, int permission) throws Exception {
		User user = validateSession(sid);
		try {
			checkPermission(Permission.valueOf(permission), user, folderId);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public WSFolder[] getPath(String sid, long folderId) throws Exception {
		User user = validateSession(sid);

		checkReadEnable(user, folderId);

		List<WSFolder> path = new ArrayList<WSFolder>();

		if (folderId == Folder.ROOTID)
			path.add(getRootFolder(sid));
		else {
			// Iterate on the parents and populate the path
			FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
			List<Folder> folders = folderDao.findParents(folderId);
			for (Folder folder : folders) {
				folderDao.initialize(folder);
				WSFolder wsFolder = WSFolder.fromFolder(folder);
				path.add(wsFolder);
			}
			// Insert the target folder itself
			Folder f = folderDao.findById(folderId);
			folderDao.initialize(f);
			WSFolder wsFolder = WSFolder.fromFolder(f);
			path.add(wsFolder);
		}

		return path.toArray(new WSFolder[0]);
	}

	@Override
	public void grantUser(String sid, long folderId, long userId, int permissions, boolean recursive) throws Exception {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		User user = userDao.findById(userId);
		grantGroup(sid, folderId, user.getUserGroup().getId(), permissions, recursive);
	}

	@Override
	public void grantGroup(String sid, long folderId, long groupId, int permissions, boolean recursive)
			throws Exception {
		User sessionUser = validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		// Check if the session user has the Security Permission of this folder
		if (!folderDao.isPermissionEnabled(Permission.SECURITY, folderId, sessionUser.getId()))
			throw new Exception("Security Rights not granted to the user on folder id " + folderId);
		try {
			Folder folder = folderDao.findById(folderId);
			folderDao.initialize(folder);
			addFolderGroup(folder, groupId, permissions);

			if (recursive) {
				folderDao.initialize(folder);
				FolderHistory transaction = new FolderHistory();
				transaction.setUser(sessionUser);
				transaction.setSessionId(sid);
				folderDao.applyRithtToTree(folder.getId(), transaction);
			}
		} catch (Exception e) {
			log.error("Some errors occurred", e);
			throw new Exception("error", e);
		}
	}

	private FolderGroup addFolderGroup(Folder folder, long groupId, int permissions) {
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);

		Set<FolderGroup> groups = new HashSet<FolderGroup>();
		for (FolderGroup folderGroup : folder.getFolderGroups()) {
			if (folderGroup.getGroupId() != groupId)
				groups.add(folderGroup);
		}
		folder.setSecurityRef(null);
		folder.getFolderGroups().clear();
		folderDao.store(folder);

		FolderGroup mg = new FolderGroup();
		mg.setGroupId(groupId);
		mg.setPermissions(permissions);
		if (mg.getRead() != 0)
			groups.add(mg);
		folder.setFolderGroups(groups);
		folderDao.store(folder);
		return mg;
	}

	@Override
	public Right[] getGrantedUsers(String sid, long folderId) throws Exception {
		return getGranted(sid, folderId, true);
	}

	private Right[] getGranted(String sid, long folderId, boolean users) throws Exception {
		validateSession(sid);

		List<Right> rightsList = new ArrayList<Right>();
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		try {
			Folder folder = folderDao.findById(folderId);
			if (folder.getSecurityRef() != null)
				folder = folderDao.findById(folder.getSecurityRef());
			folderDao.initialize(folder);
			for (FolderGroup mg : folder.getFolderGroups()) {
				Group group = groupDao.findById(mg.getGroupId());
				if (group.getName().startsWith("_user_") && users) {
					rightsList.add(new Right(Long.parseLong(group.getName().substring(
							group.getName().lastIndexOf('_') + 1)), mg.getPermissions()));
				} else if (!group.getName().startsWith("_user_") && !users)
					rightsList.add(new Right(group.getId(), mg.getPermissions()));
			}
		} catch (Exception e) {
			log.error("Some errors occurred", e);
			throw new Exception("error", e);
		}

		return (Right[]) rightsList.toArray(new Right[rightsList.size()]);
	}

	@Override
	public Right[] getGrantedGroups(String sid, long folderId) throws Exception {
		return getGranted(sid, folderId, false);
	}

	@Override
	public void update(String sid, WSFolder folder) throws Exception {
		User user = validateSession(sid);

		long folderId = folder.getId();
		String name = folder.getName();

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		if (!folderDao.isPermissionEnabled(Permission.RENAME, folderId, user.getId())) {
			throw new Exception("user does't have rename permission");
		}

		if (folderId == Folder.ROOTID)
			throw new Exception("cannot update the root folder");

		Folder fld = folderDao.findById(folderId);
		if (fld == null)
			throw new Exception("cannot find folder " + folderId);

		List<Folder> folders = folderDao.findByNameAndParentId(name, fld.getParentId());
		if (folders.size() > 0 && folders.get(0).getId() != fld.getId()) {
			throw new Exception("duplicate folder name " + name);
		} else {
			folderDao.initialize(fld);

			fld.setName(name);
			fld.setDescription(folder.getDescription());

			folder.updateExtendedAttributes(fld);

			// Add a folder history entry
			FolderHistory transaction = new FolderHistory();
			transaction.setUser(user);
			transaction.setEvent(FolderEvent.RENAMED.toString());
			transaction.setSessionId(sid);
			folderDao.store(fld, transaction);
		}
	}

	@Override
	public WSFolder createPath(String sid, long parentId, String path) throws Exception {
		User user = validateSession(sid);
		checkPermission(Permission.ADD, user, parentId);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Folder parent = folderDao.findById(parentId);

		FolderHistory transaction = new FolderHistory();
		transaction.setUser(user);
		transaction.setEvent(FolderEvent.CREATED.toString());
		transaction.setSessionId(sid);

		if (path.startsWith("/"))
			path.substring(1);

		/*
		 * Cannot write in the root so if the parent is the root, we have to
		 * guarantee that the first element in the path is a workspace. If not
		 * the Default one will be used.
		 */
		if (parentId == Folder.ROOTID) {
			Folder workspace = null;

			/*
			 * Check if the path contains the workspace specification
			 */
			for (WSFolder w : listWorkspaces(sid)) {
				if (path.startsWith(w.getName())) {
					workspace = folderDao.findById(w.getId());
					break;
				}
			}

			if (workspace == null) {
				log.debug("Path " + path + " will be created in the Default workspace");
				parent = folderDao.findById(Folder.DEFAULTWORKSPACE);
			}
		}

		Folder folder = folderDao.createPath(parent, path, true, transaction);

		return WSFolder.fromFolder(folder);
	}

	@Override
	public WSFolder[] listWorkspaces(String sid) throws Exception {
		User user = validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		List<Folder> folders = folderDao.findByUserId(user.getId(), Folder.ROOTID);
		List<WSFolder> wsFolders = new ArrayList<WSFolder>();
		for (Folder folder : folders) {
			if (folder.getType() == Folder.TYPE_WORKSPACE) {
				folderDao.initialize(folder);
				wsFolders.add(WSFolder.fromFolder(folder));
			}
		}
		return wsFolders.toArray(new WSFolder[0]);
	}
}