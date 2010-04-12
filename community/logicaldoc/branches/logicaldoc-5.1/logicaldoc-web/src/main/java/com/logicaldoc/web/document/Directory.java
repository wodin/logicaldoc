package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.PluginRegistry;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.MenuItem;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.util.FacesUtil;

/**
 * Instances of this bean represents document directories to be displayed in the
 * navigation tree
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class Directory extends PageContentBean {
	protected static Log log = LogFactory.getLog(Directory.class);

	private boolean selected = false;

	private long count = 0;

	private Boolean writeEnabled = null;

	private Boolean addChildEnabled = null;

	private Boolean manageSecurityEnabled = null;

	private Boolean manageImmutabilityEnabled = null;

	private Boolean deleteEnabled = null;

	private Boolean renameEnabled = null;

	private Boolean bulkImportEnabled = null;

	private Boolean bulkExportEnabled = null;

	private Boolean archiveEnabled = null;

	private Boolean workflowEnabled = null;

	private String pathExtended;

	// list for the dynamic menus w/ getter & setter
	protected List<MenuItem> menuItems = new ArrayList<MenuItem>();

	// True if all childs were loaded from db
	private boolean loaded = false;

	public Directory(Menu menu) {
		super(menu);
		if (menu != null)
			setDisplayText(menu.getText());
		else
			setDisplayText("");
		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
		setIcon(style.getImagePath(menu.getIcon()));
	}

	public String getPathExtended() {
		return pathExtended;
	}

	public void setPathExtended(String pathExtended) {
		this.pathExtended = pathExtended;
	}

	/**
	 * The number of contained documents
	 */
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public void onSelect(ActionEvent event) {
		getDocumentNavigation();

		// refresh menu items
		createMenuItems();
	}

	public String edit() {
		DocumentNavigation navigation = getDocumentNavigation();
		return navigation.edit();
	}

	public String rights() {
		DocumentNavigation navigation = getDocumentNavigation();
		return navigation.rights();
	}

	public String newDirectory() {
		DocumentNavigation navigation = getDocumentNavigation();
		return navigation.newDirectory();
	}

	/**
	 * @return
	 * @since 5.1
	 */
	public String deleteDirectory() {
		DocumentNavigation navigation = getDocumentNavigation();
		return navigation.deleteDirectory();
	}

	/**
	 * @return
	 * @since 5.1
	 */
	public String moveDirectory() {
		DocumentNavigation navigation = getDocumentNavigation();
		return navigation.moveDirectory();
	}

	public String searchInFolder() {
		DocumentNavigation navigation = getDocumentNavigation();
		return navigation.searchInFolder();
	}

	public String delete() {
		DocumentNavigation navigation = getDocumentNavigation();
		return navigation.delete();
	}

	public String history() {
		DocumentNavigation navigation = getDocumentNavigation();
		return navigation.history();
	}

	private DocumentNavigation getDocumentNavigation() {
		// Documents record manager binding
		DocumentNavigation navigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		if (navigation.getSelectedDir().getMenuId() != this.getMenuId()) {
			// The user has selected a new folder
			// Notify the records manager and show the first page of the
			// documents paginator
			DocumentsRecordsManager recordsManager = ((DocumentsRecordsManager) FacesUtil.accessBeanFromFacesContext(
					"documentsRecordsManager", FacesContext.getCurrentInstance(), log));
			if (recordsManager.getTable() != null)
				recordsManager.getTable().setFirst(0);
			else if (recordsManager.getPanels() != null)
				recordsManager.getPanels().setFirst(0);
		}
		navigation.selectDirectory(this);
		return navigation;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public boolean isWriteEnabled() {
		if (writeEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			writeEnabled = new Boolean(mdao.isWriteEnable(getMenuId(), SessionManagement.getUserId()));
		}
		return writeEnabled.booleanValue();
	}

	public boolean isAddChildEnabled() {
		if (addChildEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			addChildEnabled = new Boolean(mdao.isPermissionEnabled(Permission.ADD_CHILD, getMenuId(), SessionManagement
					.getUserId()));
		}
		return addChildEnabled.booleanValue();
	}

	public boolean isManageSecurityEnabled() {
		if (manageSecurityEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			manageSecurityEnabled = new Boolean(mdao.isPermissionEnabled(Permission.MANAGE_SECURITY, getMenuId(),
					SessionManagement.getUserId()));
		}
		return manageSecurityEnabled.booleanValue();
	}

	public boolean isManageImmutabilityEnabled() {
		if (manageImmutabilityEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			manageImmutabilityEnabled = new Boolean(mdao.isPermissionEnabled(Permission.MANAGE_IMMUTABILITY,
					getMenuId(), SessionManagement.getUserId()));
		}
		return manageImmutabilityEnabled.booleanValue();
	}

	public boolean isDeleteEnabled() {
		if (deleteEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			deleteEnabled = new Boolean(mdao.isPermissionEnabled(Permission.DELETE, getMenuId(), SessionManagement
					.getUserId()));
		}
		return deleteEnabled.booleanValue();
	}

	public boolean isRenameEnabled() {
		if (renameEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			renameEnabled = new Boolean(mdao.isPermissionEnabled(Permission.RENAME, getMenuId(), SessionManagement
					.getUserId()));
		}
		return renameEnabled.booleanValue();
	}

	public boolean isBulkImportEnabled() {
		if (bulkImportEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			bulkImportEnabled = new Boolean(mdao.isPermissionEnabled(Permission.BULK_IMPORT, getMenuId(),
					SessionManagement.getUserId()));
		}
		return bulkImportEnabled.booleanValue();
	}

	public boolean isBulkExportEnabled() {
		if (bulkExportEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			bulkExportEnabled = new Boolean(mdao.isPermissionEnabled(Permission.BULK_EXPORT, getMenuId(),
					SessionManagement.getUserId()));
		}
		return bulkExportEnabled.booleanValue();
	}

	public boolean isArchiveEnabled() {
		if (archiveEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			archiveEnabled = new Boolean(mdao.isPermissionEnabled(Permission.ARCHIVE, getMenuId(), SessionManagement
					.getUserId()));
		}
		return archiveEnabled.booleanValue();
	}

	public boolean isWorkflowEnabled() {
		if (workflowEnabled == null) {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			workflowEnabled = new Boolean(mdao.isPermissionEnabled(Permission.WORKFLOW, getMenuId(), SessionManagement
					.getUserId()));
		}
		return workflowEnabled.booleanValue();
	}

	public List<MenuItem> getMenuItems() {
		if (menuItems.isEmpty()) {
			createMenuItems();
		}
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> model) {
		this.menuItems = model;
	}

	/**
	 * Finds all first-level menus accessible by the current user
	 * 
	 * @return
	 */
	protected void createMenuItems() {
		menuItems.clear();

		// Acquire the 'FolderMenu' extensions of the core plug-in
		PluginRegistry registry = PluginRegistry.getInstance();
		Collection<Extension> exts = registry.getSortedExtensions("logicaldoc-core", "FolderMenu", null);

		for (Extension ext : exts) {

			MenuItem item = new MenuItem();
			item.setValue(Messages.getMessage(ext.getParameter("title").valueAsString()));
			if (StringUtils.isNotEmpty(ext.getParameter("target").valueAsString()))
				item.setTarget(ext.getParameter("target").valueAsString());
			if (StringUtils.isNotEmpty(ext.getParameter("action").valueAsString())) {
				item.setAction(FacesUtil.createActionMethodBinding(ext.getParameter("action").valueAsString()));
			}

			if (StringUtils.isNotEmpty(ext.getParameter("link").valueAsString())) {
				ValueBinding linkBinding = FacesUtil.createValueBinding(ext.getParameter("link").valueAsString());
				if (linkBinding != null) {
					String linkValue = (String) linkBinding.getValue(FacesContext.getCurrentInstance());
					item.setLink(linkValue);
				}
			}

			ValueBinding renderedBinding = FacesUtil.createValueBinding(ext.getParameter("rendered").valueAsString());
			if (renderedBinding != null) {
				Boolean rendered = (Boolean) renderedBinding.getValue(FacesContext.getCurrentInstance());
				if (rendered != null)
					item.setRendered(rendered);
			}

			if (StringUtils.isNotEmpty(ext.getParameter("confirm").valueAsString())) {
				String confirmMessage = Messages.getMessage(ext.getParameter("confirm").valueAsString());
				item.setOnclick("if (!confirm('" + confirmMessage + "')) {return false;}");
			}
			item.getAttributes().put("myAttibute", "myValue");

			menuItems.add(item);
		}
	}
}