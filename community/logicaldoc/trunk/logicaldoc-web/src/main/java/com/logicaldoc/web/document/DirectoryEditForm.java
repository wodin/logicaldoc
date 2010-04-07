package com.logicaldoc.web.document;

import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

/**
 * Base form for directory editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class DirectoryEditForm {
	protected static Log log = LogFactory.getLog(DirectoryEditForm.class);

	private String folderName = "";

	private Directory directory;

	private long[] menuGroup;

	private DocumentNavigation documentNavigation;
	
	private boolean showFolderSelector = false;
	
	private DirectoryTreeModel directoryModel;

	private long destParentId;

	private boolean treeChanged;

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
		this.folderName = directory.getMenu().getText();
	}

	public long[] getMenuGroup() {
		return menuGroup;
	}

	public void setMenuGroup(long[] menuGroup) {
		this.menuGroup = menuGroup;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	

	public void openFolderSelector(ActionEvent e) {
		showFolderSelector = true;
	}

	public void closeFolderSelector(ActionEvent e) {
		showFolderSelector = false;
	}
	
	public void cancelFolderSelector(ActionEvent e) {
		directoryModel.cancelSelection();
		destParentId = 0;
		showFolderSelector = false;
	}
	
	public void folderSelected(ActionEvent e) {
		showFolderSelector = false;
		Directory dir = directoryModel.getSelectedDir();
		destParentId = dir.getMenuId();
	}

	public boolean isShowFolderSelector() {
		return showFolderSelector;
	}

	public void setShowFolderSelector(boolean showFolderSelector) {
		this.showFolderSelector = showFolderSelector;
	}

	public void setDirectoryModel(DirectoryTreeModel directoryModel) {
		this.directoryModel = directoryModel;
	}

	public DirectoryTreeModel getDirectoryModel() {
		if (directoryModel == null || treeChanged) {
			loadTree();
			treeChanged = false;
		}
		return directoryModel;
	}

	void loadTree() {
		directoryModel = new DirectoryTreeModel();
	}
	
	/**
	 * 
	 * @return
	 * @since 5.1
	 */
	public String move() {
		Menu folderToMove = documentNavigation.getSelectedDir().getMenu();

		if (SessionManagement.isValid()) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

			// Check destParentId MUST BE <> 0 (initial value)
			if (destParentId == 0) {
				Messages.addLocalizedError("folder.error.notupdated");
				return null;
			}
			
			try {
				DocumentManager docman = (DocumentManager) Context.getInstance().getBean(DocumentManager.class);
				User user = SessionManagement.getUser();
				
				Menu destParentFolder = mdao.findById(destParentId);
				
				// Check destParentId: Must be different from the current folder parentId
				if (destParentId == folderToMove.getParentId())
					throw new SecurityException("No Changes");
				
				// Check destParentId: Must be different from the current folder Id
				// A folder cannot be children of herself
				if (destParentId == folderToMove.getId())
					throw new SecurityException("Not Allowed");
				
				// Check delete permission on the folder parent of folderToMove
				Menu sourceParent = mdao.findById(folderToMove.getParentId());
				boolean sourceParentDeleteEnabled = mdao.isPermissionEnabled(Permission.DELETE, sourceParent.getId(), user.getId());
				if (!sourceParentDeleteEnabled)
					throw new SecurityException("No rights to delete folder");	
				
				// Check addChild permission on destParentFolder
				boolean addchildEnabled = mdao.isPermissionEnabled(Permission.ADD_CHILD, destParentFolder.getId(), user.getId());
				if (!addchildEnabled)
					throw new SecurityException("AddChild Rights not granted to this user");
				

				// Add a folder history entry
				History transaction = new History();
				transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
				
				docman.moveFolder(folderToMove, destParentFolder, user, transaction);
				
				// ricarico l'albero delle cartelle
				documentNavigation.getDirectoryModel().reloadAll();
				documentNavigation.selectDirectory(new Directory(destParentFolder));
				
				// reset destParentId 
				destParentId = 0;
				
				// set the directoryModel to be reloaded
				treeChanged = true;
				
				Messages.addLocalizedInfo("msg.action.updatefolder");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("folder.error.notupdated");
			}

			return null;
		} else {
			return "login";
		}
	}

	public String update() {
		if (SessionManagement.isValid()) {
			MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			try {
				if (dao.findByMenuTextAndParentId(folderName, directory.getMenu().getParentId()).size() > 0) {
					Messages.addLocalizedWarn("errors.folder.duplicate");
					documentNavigation.refresh();
				} else {
					directory.setDisplayText(folderName);
					directory.getMenu().setText(folderName);
					// To avoid a 'org.hibernate.StaleObjectStateException', we
					// must retrieve the menu from database.
					Menu menu = dao.findById(directory.getMenuId());
					menu.setText(folderName);
					// Add a folder history entry
					History history = new History();
					history.setUserId(SessionManagement.getUserId());
					history.setUserName(SessionManagement.getUser().getFullName());
					history.setEvent(History.EVENT_FOLDER_RENAMED);
					history.setSessionId(SessionManagement.getCurrentUserSessionId());

					boolean stored = dao.store(menu, history);
					if (!stored) {
						Messages.addLocalizedError("folder.error.notupdated");
					} else {
						Messages.addLocalizedInfo("msg.action.updatefolder");
					}

					documentNavigation.refresh();
					documentNavigation.selectDirectory(directory);

					if (DocumentNavigation.FOLDER_VIEW_TREE.equals(documentNavigation.getFolderView())) {
						Directory dir = (Directory) documentNavigation.getDirectoryModel().getSelectedNode()
								.getUserObject();
						dir.setDisplayText(folderName);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.error");
			}

			return null;
		} else {
			return "login";
		}
	}


	public String insert() {
		Menu parent = documentNavigation.getSelectedDir().getMenu();

		if (SessionManagement.isValid()) {
			MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

			try {
				// Add a folder history entry
				History transaction = new History();
				transaction.setUserId(SessionManagement.getUserId());
				transaction.setUserName(SessionManagement.getUser().getFullName());
				transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
				Menu menu = dao.createFolder(parent, getFolderName(), transaction);
				menu.getMenuGroups().clear();
				for (MenuGroup mg : parent.getMenuGroups()) {
					MenuGroup clone = mg.clone();
					menu.getMenuGroups().add(clone);
					if (clone.getGroupId() == SessionManagement.getUser().getUserGroup().getId()) {
						clone.setManageSecurity(1);
					}
				}

				MenuGroup mg = new MenuGroup(SessionManagement.getUser().getUserGroup().getId());
				mg.setManageSecurity(1);

				if (!menu.getMenuGroups().contains(mg)) {
					menu.getMenuGroups().add(mg);
				}

				boolean stored = dao.store(menu);

				if (!stored) {
					Messages.addLocalizedError("folder.error.notstored");
				} else {
					Messages.addLocalizedInfo("msg.action.savefolder");
				}

				documentNavigation.refresh();
				documentNavigation.selectDirectory(new Directory(menu));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("folder.error.notstored");
			}

			return null;
		} else {
			return "login";
		}
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}

	/**
	 * This method registers the keyboard 'Enter' button during the folder
	 * creation .
	 */
	public void insert(ActionEvent event) {
		insert();
	}

	/**
	 * This method registers the keyboard 'Enter' button during the folder
	 * editing .
	 */
	public void update(ActionEvent event) {
		update();
	}

	public long getDestParentId() {
		return destParentId;
	}

	public void setDestParentId(long destParentId) {
		this.destParentId = destParentId;
	}

}