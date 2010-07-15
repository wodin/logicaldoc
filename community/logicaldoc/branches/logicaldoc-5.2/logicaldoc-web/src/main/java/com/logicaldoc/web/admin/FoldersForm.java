package com.logicaldoc.web.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.i18n.Messages;

/**
 * Form for directories editing
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class FoldersForm {
	protected static Log log = LogFactory.getLog(FoldersForm.class);

	private String docDir = "";

	private String indexDir = "";

	private String userDir = "";

	private String importDir = "";

	private String exportDir = "";

	private String pluginDir = "";

	public FoldersForm() {
		reload();
	}

	public String getDocDir() {
		return docDir;
	}

	public void setDocDir(String docDir) {
		this.docDir = docDir;
	}

	public String getIndexDir() {
		return indexDir;
	}

	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}

	public String getUserDir() {
		return userDir;
	}

	public void setUserDir(String userDir) {
		this.userDir = userDir;
	}

	public String getImportDir() {
		return importDir;
	}

	public void setImportDir(String importDir) {
		this.importDir = importDir;
	}

	public String getExportDir() {
		return exportDir;
	}

	public void setExportDir(String exportDir) {
		this.exportDir = exportDir;
	}

	public String getPluginDir() {
		return pluginDir;
	}

	public void setPluginDir(String pluginDir) {
		this.pluginDir = pluginDir;
	}

	private void reload() {
		PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
		docDir = conf.getPropertyWithSubstitutions("conf.docdir");
		indexDir = conf.getPropertyWithSubstitutions("conf.indexdir");
		userDir = conf.getPropertyWithSubstitutions("conf.userdir");
		importDir = conf.getPropertyWithSubstitutions("conf.importdir");
		exportDir = conf.getPropertyWithSubstitutions("conf.exportdir");
		pluginDir = conf.getPropertyWithSubstitutions("conf.plugindir");
	}

	public String save() {
		if (SessionManagement.isValid()) {
			try {
				PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
				conf.setProperty("conf.docdir", docDir);
				conf.setProperty("conf.indexdir", indexDir);
				conf.setProperty("conf.userdir", userDir);
				conf.setProperty("conf.importdir", importDir);
				conf.setProperty("conf.exportdir", exportDir);
				conf.setProperty("conf.plugindir", pluginDir);
				conf.write();

				Messages.addLocalizedInfo("msg.action.savesettings");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				Messages.addLocalizedError("errors.action.savesettings");
			}
		} else {
			return "login";
		}

		reload();
		return null;
	}
}