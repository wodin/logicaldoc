package com.logicaldoc.web.admin;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.document.Directory;
import com.logicaldoc.web.document.DirectoryTreeModel;
import com.logicaldoc.web.document.RightsRecordsManager;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Form for entry page definition
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class GuiBean {

	protected static Log log = LogFactory.getLog(GuiBean.class);

	private DirectoryTreeModel directoryModel;

	private boolean showFolderSelector = false;

	private String viewModeBrowsing;

	private String viewModeSearch;

	private Integer thumbnailSize = null;

	private Integer thumbnailQuality = null;

	private Integer thumbnailScale = null;

	private String viewModeFolder;

	private String tagcloudMode;

	private Integer pageSize = null;

	public Integer getThumbnailSize() {
		if (thumbnailSize == null)
			init();
		return thumbnailSize;
	}

	public void setThumbnailSize(Integer thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
	}

	public String getViewModeBrowsing() {
		if (viewModeBrowsing == null)
			init();
		return viewModeBrowsing;
	}

	public void setViewModeBrowsing(String viewModeBrowsing) {
		this.viewModeBrowsing = viewModeBrowsing;
	}

	public String getViewModeSearch() {
		if (viewModeSearch == null)
			init();
		return viewModeSearch;
	}

	public void setViewModeSearch(String viewModeSearch) {
		this.viewModeSearch = viewModeSearch;
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
			init();
		}
		return directoryModel;
	}

	void init() {
		directoryModel = new DirectoryTreeModel(Menu.MENUID_HOME, Menu.MENUTYPE_MENU);
		directoryModel.setUseMenuIcons(true);
		directoryModel.reload();
		try {
			PropertiesBean config = new PropertiesBean();
			String entrypage = config.getProperty("gui.entrypage");
			if (StringUtils.isNotEmpty(entrypage))
				directoryModel.selectDirectory(Long.parseLong(entrypage));
			viewModeBrowsing = config.getProperty("gui.viewmode.browsing");
			viewModeSearch = config.getProperty("gui.viewmode.search");
			thumbnailSize = Integer.parseInt(config.getProperty("gui.thumbnail.size"));
			thumbnailScale = Integer.parseInt(config.getProperty("gui.thumbnail.scale"));
			thumbnailQuality = Integer.parseInt(config.getProperty("gui.thumbnail.quality"));
			pageSize = Integer.parseInt(config.getProperty("gui.page.size"));
			viewModeFolder = config.getProperty("gui.viewmode.folder");
			tagcloudMode = config.getProperty("gui.tagcloudmode");
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
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
		RightsRecordsManager manager = ((RightsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"securityRightsRecordsManager", FacesContext.getCurrentInstance(), log));
		manager.cleanSelection();
		showFolderSelector = false;
	}

	public String save() {
		try {
			PropertiesBean config = new PropertiesBean();
			if (directoryModel.getSelectedDir() != null)
				config.setProperty("gui.entrypage", Long.toString(directoryModel.getSelectedDir().getMenuId()));
			config.setProperty("gui.viewmode.browsing", viewModeBrowsing);
			config.setProperty("gui.viewmode.search", viewModeSearch);
			config.setProperty("gui.thumbnail.size", thumbnailSize.toString());
			config.setProperty("gui.thumbnail.quality", thumbnailQuality.toString());
			config.setProperty("gui.thumbnail.scale", thumbnailScale.toString());
			config.setProperty("gui.page.size", pageSize.toString());
			config.setProperty("gui.viewmode.folder", viewModeFolder);
			config.setProperty("gui.tagcloudmode", tagcloudMode);
			config.write();
			Messages.addLocalizedInfo("msg.action.savesettings");
		} catch (IOException e) {
			Messages.addLocalizedError("errors.action.savesettings");
			log.error("Error saving paramaters", e);
		}
		return null;
	}

	public Integer getPageSize() {
		if (pageSize == null)
			init();
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getThumbnailScale() {
		return thumbnailScale;
	}

	public void setThumbnailScale(Integer thumbnailScale) {
		this.thumbnailScale = thumbnailScale;
	}

	public Integer getThumbnailQuality() {
		return thumbnailQuality;
	}

	public void setThumbnailQuality(Integer thumbnailQuality) {
		this.thumbnailQuality = thumbnailQuality;
	}

	public String getViewModeFolder() {
		if (viewModeFolder == null)
			init();
		return viewModeFolder;
	}

	public void setViewModeFolder(String viewModeFolder) {
		this.viewModeFolder = viewModeFolder;
	}

	public String getTagcloudMode() {
		if (tagcloudMode == null)
			init();
		return tagcloudMode;
	}

	public void setTagcloudMode(String tagcloudMode) {
		this.tagcloudMode = tagcloudMode;
	}
}