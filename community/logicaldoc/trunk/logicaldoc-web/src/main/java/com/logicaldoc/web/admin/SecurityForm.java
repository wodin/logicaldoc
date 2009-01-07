package com.logicaldoc.web.admin;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.web.document.Directory;
import com.logicaldoc.web.document.DirectoryTreeModel;
import com.logicaldoc.web.document.RightsRecordsManager;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Form for menu security
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class SecurityForm {

	protected static Log log = LogFactory.getLog(SecurityForm.class);

	private DirectoryTreeModel directoryModel;

	private String path = "";

	private boolean showFolderSelector = false;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void openFolderSelector(ActionEvent e) {
		showFolderSelector = true;
	}

	public void closeFolderSelector(ActionEvent e) {
		showFolderSelector = false;
		path = null;
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
		if (directoryModel == null) {
			loadTree();
		}
		return directoryModel;
	}

	void loadTree() {
		directoryModel = new DirectoryTreeModel(Menu.MENUID_HOME, Menu.MENUTYPE_MENU);
		directoryModel.setUseMenuIcons(true);
		directoryModel.reload();
	}

	public void folderSelected(ActionEvent e) {
		showFolderSelector = false;

		Directory dir = directoryModel.getSelectedDir();
		Menu menu = dir.getMenu();
		String dirPath = menu.getPath() + "/" + menu.getId();
		setPath(dirPath);
		RightsRecordsManager manager = ((RightsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"securityRightsRecordsManager", FacesContext.getCurrentInstance(), log));
		manager.selectDirectory(dir);
	}

	public void cancelFolderSelector(ActionEvent e) {
		directoryModel.cancelSelection();
		RightsRecordsManager manager = ((RightsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"securityRightsRecordsManager", FacesContext.getCurrentInstance(), log));
		manager.cleanSelection();
		showFolderSelector = false;
	}
}