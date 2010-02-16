package com.logicaldoc.web.document;

import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
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
}