package com.logicaldoc.web.document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;

import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

/**
 * Base form for directory editing
 * 
 * @author Marco Meschieri
 * @version $Id: DirectoryEditForm.java,v 1.2 2006/09/03 16:24:37 marco Exp $
 * @since 3.0
 */
public class DirectoryEditForm {
	protected static Log log = LogFactory.getLog(DirectoryEditForm.class);

	private String folderName;

	private Directory directory;

	private String[] menuGroup;

	private DocumentNavigation documentNavigation;

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
		this.folderName = directory.getMenu().getMenuText();
	}

	public String[] getMenuGroup() {
		return menuGroup;
	}

	public void setMenuGroup(String[] menuGroup) {
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
				directory.setDisplayText(folderName);
				directory.getMenu().setMenuText(folderName);
				dao.store(directory.getMenu());
				documentNavigation.refresh();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.savefolder.notstored");
			}

			return null;
		} else {
			return "login";
		}
	}

	public String insert() {
		int menuparent = documentNavigation.getSelectedDir().getMenuId();

		if (SessionManagement.isValid()) {
			MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

			try {
				Menu menu = new Menu();
				menu.setMenuText(getFolderName());
				menu.setMenuParent(menuparent);
				menu.setMenuSort(1);
				menu.setMenuIcon("folder.png");
				menu.setMenuPath(documentNavigation.getSelectedDir().getMenu().getMenuPath() + "/" + menuparent);
				menu.setMenuType(Menu.MENUTYPE_DIRECTORY);
				menu.setMenuHier(menu.getMenuHier());
				menu.setMenuRef("");
				menu.setMenuGroup(menuGroup);

				boolean stored = dao.store(menu);

				if (!stored) {
					Messages.addLocalizedError("errors.action.savefolder.notstored");
				} else {
					Messages.addLocalizedInfo("msg.action.savefolder");
				}

				documentNavigation.refresh();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.savefolder.notstored");
			}

			return null;
		} else {
			return "login";
		}
	}

	public void setDocumentNavigation(DocumentNavigation documentNavigation) {
		this.documentNavigation = documentNavigation;
	}
}
