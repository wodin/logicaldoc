package com.logicaldoc.web.service;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the FolderService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class FolderServiceImpl extends RemoteServiceServlet implements FolderService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(FolderServiceImpl.class);

	@Override
	public void applyRightsToTree(String sid, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Collection<Menu> submenus = mdao.findByParentId(folderId);
		GUIFolder folder = getFolder(sid, folderId, false);
		for (Menu submenu : submenus) {
			try {
				saveRules(sid, submenu.getId(), SessionUtil.getSessionUser(sid).getId(), folder.getRights());
			} catch (Exception e) {
			}
		}

	}

	@Override
	public void delete(String sid, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		dao.delete(folderId);
	}

	static GUIFolder getFolder(String sid, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu menu = dao.findById(folderId);
		if (menu == null)
			return null;

		try {
			GUIFolder folder = new GUIFolder();
			folder.setId(folderId);
			folder.setName(folderId != Constants.DOCUMENTS_FOLDERID ? menu.getText() : "/");
			folder.setParentId(menu.getParentId());
			folder.setDescription(menu.getDescription());

			List<String> permissionsList = new ArrayList<String>();
			long userId = SessionUtil.getSessionUser(sid).getId();
			if (dao.isPermissionEnabled(Permission.READ, folderId, userId))
				permissionsList.add("read");
			if (dao.isPermissionEnabled(Permission.WRITE, folderId, userId))
				permissionsList.add(Constants.PERMISSION_WRITE);
			if (dao.isPermissionEnabled(Permission.ADD_CHILD, folderId, userId))
				permissionsList.add(Constants.PERMISSION_ADD);
			if (dao.isPermissionEnabled(Permission.MANAGE_SECURITY, folderId, userId))
				permissionsList.add(Constants.PERMISSION_SECURITY);
			if (dao.isPermissionEnabled(Permission.DELETE, folderId, userId))
				permissionsList.add(Constants.PERMISSION_DELETE);
			if (dao.isPermissionEnabled(Permission.RENAME, folderId, userId))
				permissionsList.add(Constants.PERMISSION_RENAME);
			if (dao.isPermissionEnabled(Permission.BULK_IMPORT, folderId, userId))
				permissionsList.add(Constants.PERMISSION_IMPORT);
			if (dao.isPermissionEnabled(Permission.BULK_EXPORT, folderId, userId))
				permissionsList.add(Constants.PERMISSION_EXPORT);
			if (dao.isPermissionEnabled(Permission.SIGN, folderId, userId))
				permissionsList.add(Constants.PERMISSION_SIGN);
			if (dao.isPermissionEnabled(Permission.ARCHIVE, folderId, userId))
				permissionsList.add(Constants.PERMISSION_ARCHIVE);
			if (dao.isPermissionEnabled(Permission.WORKFLOW, folderId, userId))
				permissionsList.add(Constants.PERMISSION_WORKFLOW);
			if (dao.isPermissionEnabled(Permission.MANAGE_IMMUTABILITY, folderId, userId))
				permissionsList.add(Constants.PERMISSION_IMMUTABILITY);

			folder.setPermissions(permissionsList.toArray(new String[permissionsList.size()]));

			int i = 0;
			GUIRight[] rights = new GUIRight[menu.getMenuGroups().size()];
			for (MenuGroup mg : menu.getMenuGroups()) {
				GUIRight right = new GUIRight();
				right.setEntityId(mg.getGroupId());
				right.setAdd(mg.getAddChild() == 1 ? true : false);
				right.setWrite(mg.getWrite() == 1 ? true : false);
				right.setSecurity(mg.getManageSecurity() == 1 ? true : false);
				right.setImmutable(mg.getManageImmutability() == 1 ? true : false);
				right.setDelete(mg.getDelete() == 1 ? true : false);
				right.setRename(mg.getRename() == 1 ? true : false);
				right.setImport(mg.getBulkImport() == 1 ? true : false);
				right.setExport(mg.getBulkExport() == 1 ? true : false);
				right.setSign(mg.getSign() == 1 ? true : false);
				right.setArchive(mg.getArchive() == 1 ? true : false);
				right.setWorkflow(mg.getWorkflow() == 1 ? true : false);

				rights[i] = right;
				i++;
			}

			folder.setRights(rights);

			return folder;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public GUIFolder getFolder(String sid, long folderId, boolean computePath) throws InvalidSessionException {
		GUIFolder folder = getFolder(sid, folderId);

		MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		if (computePath) {
			String pathExtended = dao.computePathExtended(folderId);
			StringTokenizer st = new StringTokenizer(pathExtended, "/", false);
			int elements = st.countTokens();
			GUIFolder[] path = new GUIFolder[elements];
			Menu parent = dao.findById(Menu.MENUID_DOCUMENTS);
			List<Menu> list = new ArrayList<Menu>();
			int j = 0;
			while (st.hasMoreTokens()) {
				String text = st.nextToken();
				list = dao.findByText(parent, text, Menu.MENUTYPE_DIRECTORY, true);
				if (list.isEmpty())
					return null;

				path[j] = getFolder(sid, parent.getId(), false);
				parent = list.get(0);
				j++;
			}

			folder.setPath(path);
		}

		return folder;
	}

	@Override
	public void move(String sid, long folderId, long targetId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			Menu folderToMove = folderDao.findById(folderId);
			// Check destParentId MUST BE <> 0 (initial value)
			if (targetId == 0 || folderDao.isInPath(folderToMove.getId(), targetId)) {
				// TODO Message?
				return;
			}

			User user = SessionUtil.getSessionUser(sid);

			Menu destParentFolder = folderDao.findById(targetId);
			// Check destParentId: Must be different from the current folder
			// parentId
			if (targetId == folderToMove.getParentId())
				throw new SecurityException("No Changes");

			// Check destParentId: Must be different from the current folderId
			// A folder cannot be children of herself
			if (targetId == folderToMove.getId())
				throw new SecurityException("Not Allowed");

			// Check delete permission on the folder parent of folderToMove
			Menu sourceParent = folderDao.findById(folderToMove.getParentId());
			boolean sourceParentDeleteEnabled = folderDao.isPermissionEnabled(Permission.DELETE, sourceParent.getId(),
					user.getId());
			if (!sourceParentDeleteEnabled)
				throw new SecurityException("No rights to delete folder");

			// Check addChild permission on destParentFolder
			boolean addchildEnabled = folderDao.isPermissionEnabled(Permission.ADD_CHILD, destParentFolder.getId(),
					user.getId());
			if (!addchildEnabled)
				throw new SecurityException("AddChild Rights not granted to this user");

			// Add a folder history entry
			History transaction = new History();
			transaction.setSessionId(sid);
			transaction.setUser(user);

			folderDao.move(folderToMove, destParentFolder, transaction);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// TODO Message?
		}
	}

	@Override
	public void rename(String sid, long folderId, String name) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO dao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			List<Menu> folders = dao.findByTextAndParentId(name, dao.findById(folderId).getParentId());
			if (folders.size() > 0 && folders.get(0).getId() != folderId) {
				// TODO Message?
				return;
			}
			// To avoid a 'org.hibernate.StaleObjectStateException', we
			// must retrieve the menu from database.
			Menu menu = dao.findById(folderId);
			menu.setText(name);
			// Add a folder history entry
			History history = new History();
			history.setUser(SessionUtil.getSessionUser(sid));
			history.setEvent(History.EVENT_FOLDER_RENAMED);
			history.setSessionId(sid);

			boolean stored = dao.store(menu, history);
			if (!stored) {
				// TODO Message?
			} else {
				// TODO Message?
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// TODO Message?
		}
	}

	@Override
	public GUIFolder save(String sid, GUIFolder folder) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		Menu menu;
		if (folder.getId() != 0) {
			menu = folderDao.findById(folder.getId());
			folderDao.initialize(menu);
		} else
			menu = new Menu();

		menu.setText(folder.getName());
		menu.setParentId(folder.getParentId());
		menu.setDescription(folder.getDescription());
		menu.setSort(0);
		menu.setIcon("folder.png");
		menu.setType(Menu.MENUTYPE_DIRECTORY);
		GUIFolder parentFolder = getFolder(sid, folder.getParentId());
		for (GUIRight right : parentFolder.getRights()) {
			MenuGroup mg = new MenuGroup();
			mg.setGroupId(right.getEntityId());
			mg.setWrite(right.isWrite() ? 1 : 0);
			mg.setAddChild(right.isAdd() ? 1 : 0);
			mg.setManageSecurity(right.isSecurity() ? 1 : 0);
			mg.setManageImmutability(right.isImmutable() ? 1 : 0);
			mg.setDelete(right.isDelete() ? 1 : 0);
			mg.setRename(right.isRename() ? 1 : 0);
			mg.setBulkImport(right.isImport() ? 1 : 0);
			mg.setBulkExport(right.isExport() ? 1 : 0);
			mg.setSign(right.isSign() ? 1 : 0);
			mg.setArchive(right.isArchive() ? 1 : 0);
			mg.setWorkflow(right.isWorkflow() ? 1 : 0);
			menu.getMenuGroups().add(mg);
		}

		folderDao.store(menu);
		folder.setId(menu.getId());

		return folder;
	}

	private void saveRules(String sid, long id, long userId, GUIRight[] rights) throws Exception {
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		// Rules can be applied only if the user can manage the security
		if (!mdao.isPermissionEnabled(Permission.MANAGE_SECURITY, id, userId))
			return;

		Menu menu = mdao.findById(id);
		boolean sqlerrors = false;
		menu.clearMenuGroups();
		for (GUIRight right : rights) {
			MenuGroup mg = new MenuGroup();
			mg.setGroupId(right.getEntityId());
			mg.setWrite(right.isWrite() ? 1 : 0);
			mg.setAddChild(right.isAdd() ? 1 : 0);
			mg.setManageSecurity(right.isSecurity() ? 1 : 0);
			mg.setManageImmutability(right.isImmutable() ? 1 : 0);
			mg.setDelete(right.isDelete() ? 1 : 0);
			mg.setRename(right.isRename() ? 1 : 0);
			mg.setBulkImport(right.isImport() ? 1 : 0);
			mg.setBulkExport(right.isExport() ? 1 : 0);
			mg.setSign(right.isSign() ? 1 : 0);
			mg.setArchive(right.isArchive() ? 1 : 0);
			mg.setWorkflow(right.isWorkflow() ? 1 : 0);
			menu.getMenuGroups().add(mg);

			boolean stored = mdao.store(menu);
			if (!stored) {
				sqlerrors = true;
			}
		}

		if (sqlerrors) {
			System.out.println("SQL errors saving permissions on folder " + menu.getText());
			log.error("SQL errors saving permissions on folder " + menu.getText());
			throw new Exception("SQL errors saving permissions on folder " + menu.getText());
		} else {
			// Add a folder history entry
			History history = new History();
			history.setUser(SessionUtil.getSessionUser(sid));
			history.setEvent(History.EVENT_FOLDER_PERMISSION);
			history.setSessionId(sid);
			mdao.store(menu, history);
		}
	}

	@Override
	public void paste(String sid, long[] docIds, long folderId, String action) throws InvalidSessionException {
		if (action.equals(Clipboard.CUT))
			cut(sid, docIds, folderId);
		else if (action.equals(Clipboard.COPY))
			copy(sid, docIds, folderId);
	}

	private void cut(String sid, long[] docIds, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Menu selectedMenuFolder = menuDao.findById(folderId);
		try {
			boolean skippedSome = false;
			boolean lockedSome = false;
			for (long id : docIds) {
				Document doc = docDao.findById(id);
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setUser(SessionUtil.getSessionUser(sid));

				// Check if the selected document is a shortcut
				if (doc.getDocRef() != null) {
					long docId = doc.getDocRef();
					doc = docDao.findById(docId);
					if (doc.getFolder().getId() != selectedMenuFolder.getId()) {
						transaction.setEvent(History.EVENT_SHORTCUT_MOVED);
						docManager.moveToFolder(doc, selectedMenuFolder, transaction);
					} else
						// TODO Message?
						continue;
				}

				// The document must be not immutable
				if (doc.getImmutable() == 1 && !transaction.getUser().isInGroup("admin")) {
					skippedSome = true;
					continue;
				}

				// The document must be not locked
				if (doc.getStatus() != Document.DOC_UNLOCKED || doc.getExportStatus() != Document.EXPORT_UNLOCKED) {
					lockedSome = true;
					continue;
				}

				docManager.moveToFolder(doc, selectedMenuFolder, transaction);
			}
			if (skippedSome || lockedSome) {
				// TODO Message?
			}
		} catch (AccessControlException e) {
			// TODO Message?
		} catch (Exception e) {
			// TODO Message?
			log.error("Exception moving documents: " + e.getMessage(), e);
		}
	}

	private void copy(String sid, long[] docIds, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu selectedMenuFolder = menuDao.findById(folderId);
		DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		try {
			for (long id : docIds) {
				Document doc = docDao.findById(id);
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(History.EVENT_STORED);
				transaction.setComment("");
				transaction.setUser(SessionUtil.getSessionUser(sid));

				if (doc.getDocRef() == null) {
					docManager.copyToFolder(doc, selectedMenuFolder, transaction);
				} else {
					long docId = doc.getDocRef();
					doc = docDao.findById(docId);
					if (doc.getFolder().getId() != selectedMenuFolder.getId()) {
						transaction.setEvent(History.EVENT_SHORTCUT_STORED);
						docManager.copyToFolder(doc, selectedMenuFolder, transaction);
					} else {
						// TODO Message?
					}
				}
			}
		} catch (AccessControlException e) {
			// TODO Message?
		} catch (Exception e) {
			// TODO Message?
			log.error("Exception copying documents: " + e.getMessage(), e);
		}
	}

	@Override
	public void pasteAsAlias(String sid, long[] docIds, long folderId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu selectedMenuFolder = menuDao.findById(folderId);
		DocumentManager docManager = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		try {
			for (long id : docIds) {
				Document doc = docDao.findById(id);
				// Create the document history event
				History transaction = new History();
				transaction.setSessionId(sid);
				transaction.setEvent(History.EVENT_SHORTCUT_STORED);
				transaction.setComment("");
				transaction.setUser(SessionUtil.getSessionUser(sid));

				if (doc.getFolder().getId() != selectedMenuFolder.getId()) {
					docManager.createShortcut(doc, selectedMenuFolder, transaction);
				}
			}
		} catch (AccessControlException e) {
			// TODO Message?
		} catch (Exception e) {
			// TODO Message?
			log.error("Exception copying documents alias: " + e.getMessage(), e);
		}
	}
}