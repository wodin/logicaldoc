package com.logicaldoc.web.admin;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.document.Directory;
import com.logicaldoc.web.document.DirectoryTreeModel;
import com.logicaldoc.web.document.RightsRecordsManager;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Form for menu security and other security issues.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class SecurityForm {

	protected static Log log = LogFactory.getLog(SecurityForm.class);

	private DirectoryTreeModel directoryModel;

	private String path = "";

	private boolean showFolderSelector = false;

	private int passwordSize;

	private int passwordTtl;

	private String auditUser;

	private String auditUserString = "";

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
		RightsRecordsManager manager = ((RightsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"securityRightsRecordsManager", FacesContext.getCurrentInstance(), log));
		manager.selectDirectory(dir);
	}

	public void cancelFolderSelector(ActionEvent e) {
		directoryModel.cancelSelection();
		path = "";
		RightsRecordsManager manager = ((RightsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"securityRightsRecordsManager", FacesContext.getCurrentInstance(), log));
		manager.cleanSelection();
		showFolderSelector = false;
	}

	public int getPasswordSize() {
		return getConfig().getInt("password.size");
	}

	public int getPasswordTtl() {
		return getConfig().getInt("password.ttl");
	}

	public void setPasswordSize(int passwordSize) {
		this.passwordSize = passwordSize;
	}

	public void setPasswordTtl(int passwordTtl) {
		this.passwordTtl = passwordTtl;
	}

	public PropertiesBean getConfig() {
		return (PropertiesBean) Context.getInstance().getBean("ContextProperties");
	}

	public void save() {
		try {
			PropertiesBean context = getConfig();
			context.setProperty("password.size", passwordSize > 4 ? Integer.toString(passwordSize) : "4");
			context.setProperty("password.ttl", passwordTtl > 0 ? Integer.toString(passwordTtl) : "0");
			context.write();

			UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			dao.setPasswordTtl(passwordTtl > 0 ? passwordTtl : 0);
			Messages.addLocalizedInfo("msg.action.passwordsettings");
		} catch (IOException e) {
			Messages.addLocalizedError("errors.error");
		}
	}

	public String getAuditUser() {
		return getConfig().getProperty("audit.user");
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	/**
	 * Store the usernames given by the user to the parameter 'audit.user'
	 */
	public void saveNotificationsSettings() {
		try {
			auditUser = "";
			StringTokenizer st = new StringTokenizer(auditUserString.trim().toLowerCase(), ", ;", false);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				auditUser = auditUser + token + ", ";
			}
			if (auditUser.trim().endsWith(","))
				auditUser = auditUser.substring(0, auditUser.lastIndexOf(","));

			PropertiesBean context = getConfig();
			context.setProperty("audit.user", auditUser.trim());
			context.write();

			Messages.addLocalizedInfo("notifications.settings");
		} catch (IOException e) {
			Messages.addLocalizedError("errors.error");
		}
	}

	public String getAuditUserString() {
		if (auditUserString == null || auditUserString.trim().isEmpty()) {
			return getAuditUser();
		} else
			return auditUserString;

	}

	public void setAuditUserString(String auditUserString) {
		this.auditUserString = auditUserString;
	}
}