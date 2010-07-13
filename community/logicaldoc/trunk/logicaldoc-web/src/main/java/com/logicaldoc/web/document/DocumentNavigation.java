package com.logicaldoc.web.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.panelseries.PanelSeries;
import com.icesoft.faces.component.tree.IceUserObject;
import com.icesoft.faces.component.tree.Tree;
import com.logicaldoc.core.document.DiscussionComment;
import com.logicaldoc.core.document.DiscussionThread;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DiscussionThreadDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.searchengine.Hit;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.admin.GuiBean;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.MenuBarBean;
import com.logicaldoc.web.navigation.MenuItem;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
import com.logicaldoc.web.search.DocumentResult;
import com.logicaldoc.web.search.SearchForm;
import com.logicaldoc.web.util.FacesUtil;

/**
 * <p>
 * The TreeNavigation class is the backing bean for the documents navigation
 * tree on the left hand side of the application. Each node in the tree is made
 * up of a PageContent which is responsible for the navigation action when a
 * tree node is selected.
 * </p>
 * <p>
 * When the Tree component binding takes place the tree nodes are initialised
 * and the tree is built. Any addition to the tree navigation must be made to
 * this class.
 * </p>
 * <p>
 * This bean also controls which panel is shown on the right side of the
 * documents view
 * </p>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class DocumentNavigation extends NavigationBean {

	public static final String FOLDER_VIEW_TREE = "tree";

	protected static Log log = LogFactory.getLog(DocumentNavigation.class);

	// list for the dynamic menus w/ getter & setter
	protected List<MenuItem> folderItems = new ArrayList<MenuItem>();

	private Directory selectedDir;

	private DirectoryTreeModel directoryModel;

	private boolean showFolderSelector = false;

	private String viewMode = null;

	private String folderView;

	// binding to component
	private Tree treeComponent;

	// the last documents list page visualized by the user
	private int lastPageNumber = 0;

	/**
	 * Default constructor of the tree. The root node of the tree is created at
	 * this point.
	 */
	public DocumentNavigation() {
		selectDirectory(Menu.MENUID_DOCUMENTS);
	}

	public DirectoryTreeModel getDirectoryModel() {
		if (directoryModel == null) {
			loadTree();
		}
		return directoryModel;
	}

	/**
	 * Sets the tree component binding.
	 * 
	 * @param treeComponent tree component to bind to
	 */
	public void setTreeComponent(Tree treeComponent) {
		this.treeComponent = treeComponent;
	}

	/**
	 * Gets the tree component binding.
	 * 
	 * @return tree component binding
	 */
	public Tree getTreeComponent() {
		return treeComponent;
	}

	public boolean isShowFolderSelector() {
		return showFolderSelector;
	}

	public void setShowFolderSelector(boolean showFolderSelector) {
		this.showFolderSelector = showFolderSelector;
	}

	public void setViewMode(String viewModeP) {
		if (!this.viewMode.equals(viewModeP)) {
			this.viewMode = viewModeP;
			// Notify the records manager
			DocumentsRecordsManager recordsManager = ((DocumentsRecordsManager) FacesUtil.accessBeanFromFacesContext(
					"documentsRecordsManager", FacesContext.getCurrentInstance(), log));
			if (recordsManager.getTable() != null)
				// Get the current documents page displayed as 'simple' or
				// 'details'
				lastPageNumber = recordsManager.getTable().getFirst();
			else if (recordsManager.getPanels() != null)
				// Get the current documents page displayed as 'icons' or
				// 'iconslarge'
				lastPageNumber = recordsManager.getPanels().getFirst();

			if (viewMode.contains("icons")) {
				recordsManager.setPanels(new PanelSeries());
				recordsManager.getPanels().setFirst(lastPageNumber);
			}

			selectDirectory(getSelectedDir());
			refresh();

			// Set the correct page to be displayed
			if (!viewMode.contains("icons")) {
				recordsManager.setTable(new HtmlDataTable());
				recordsManager.getTable().setFirst(lastPageNumber);
			} else {
				recordsManager.setPanels(new PanelSeries());
				recordsManager.getPanels().setFirst(lastPageNumber);
			}
		}
	}

	public String getViewMode() {
		if (viewMode == null)
			try {
				PropertiesBean config = new PropertiesBean();
				viewMode = config.getProperty("gui.viewmode.browsing");
			} catch (IOException e) {
			}
		if (StringUtils.isEmpty(viewMode))
			viewMode = "simple";
		return viewMode;
	}

	public String getFolderView() {
		if (folderView == null) {
			GuiBean guiBean = ((GuiBean) FacesUtil.accessBeanFromFacesContext("guiBean", FacesContext
					.getCurrentInstance(), log));
			folderView = guiBean.getViewModeFolder();
		}
		return folderView;
	}

	public List<MenuItem> getFolderItems() {
		if (folderItems.isEmpty())
			createMenuItems();
		return folderItems;
	}

	public void setFolderItems(List<MenuItem> folderItems) {
		this.folderItems = folderItems;
	}

	public Directory getSelectedDir() {
		return selectedDir;
	}

	public List<Directory> getBreadcrumb() {
		List<Directory> breadcrumb = new ArrayList<Directory>();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		try {
			if (getSelectedDir() != null) {
				Directory currentDir = getSelectedDir();
				int counter = 0;
				while (currentDir.getMenuId() != Menu.MENUID_DOCUMENTS) {
					breadcrumb.add(currentDir);
					currentDir = new Directory(menuDao.findById(currentDir.getMenu().getParentId()));
					counter++;
				}
				Directory rootDir = new Directory(menuDao.findById(Menu.MENUID_DOCUMENTS));
				rootDir.setDisplayText(Messages.getMessage(rootDir.getMenu().getText()));
				if (counter == 0)
					rootDir.setSelected(true);
				breadcrumb.add(rootDir);
			}
		} catch (RuntimeException e) {
			log.error("getBreadcrumb() Eccezione: " + e.getMessage(), e);
		}

		// revert the list
		Collections.reverse(breadcrumb);

		return breadcrumb;
	}

	public void refresh() {
		if (FOLDER_VIEW_TREE.equals(getFolderView())) {
			if (getDirectoryModel().getSelectedNode() != null) {
				DefaultMutableTreeNode node = getDirectoryModel().getSelectedNode();
				directoryModel.reload(node, -1);
			} else {
				directoryModel.reload();
			}
		} else {
			selectDirectory(getSelectedDir());
			if (directoryModel != null)
				directoryModel.reload();
		}
	}

	/**
	 * Finds all sub dirs menus accessible by the current
	 * 
	 * @return
	 */
	protected void createMenuItems() {
		if (selectedDir == null || selectedDir.getMenu() == null)
			return;

		folderItems.clear();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Directory parentDir = new Directory(menuDao.findById(selectedDir.getMenu().getParentId()));
		if (parentDir.getMenuId() == Menu.MENUID_DOCUMENTS)
			parentDir.setDisplayText(Messages.getMessage(parentDir.getMenu().getText()));

		MenuItem item = createMenuItem(parentDir.getDisplayText(), parentDir, "folder_up.png", "folderParent");

		if (parentDir.getMenuId() != Menu.MENUID_HOME) {
			// Add parent folder as first menu
			folderItems.add(item);
		}

		long userId = SessionManagement.getUserId();
		List<Menu> menus = (List<Menu>) menuDao.findByUserId(userId, selectedDir.getMenuId(), Menu.MENUTYPE_DIRECTORY);
		Collections.sort(menus, new Comparator<Menu>() {
			@Override
			public int compare(Menu menu1, Menu menu2) {
				return menu1.getText().compareTo(menu2.getText());
			}
		});
		for (Menu menu : menus) {
			item = createMenuItem(menu.getText(), new Directory(menu));
			folderItems.add(item);
		}
	}

	protected MenuItem createMenuItem(String label, Directory dir) {
		return createMenuItem(label, dir, "folder.png", null);
	}

	protected MenuItem createMenuItem(String label, Directory dir, String imageName, String styleClass) {

		MenuItem menuItem = new MenuItem();
		menuItem.setValue(label);
		menuItem.setId("dir-" + dir.getMenuId());
		menuItem.setActionListener(FacesUtil
				.createActionListenerMethodBinding("#{documentNavigation.onSelectDirectory}"));
		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
		menuItem.setIcon(style.getImagePath(imageName));
		menuItem.setUserObject(dir);
		if (styleClass != null)
			menuItem.setStyleClass(styleClass);

		return menuItem;
	}

	public void onSelectDirectory(ActionEvent event) {
		Directory dir = null;
		if (event.getSource() instanceof MenuItem) {
			dir = (Directory) ((MenuItem) event.getSource()).getUserObject();
			selectDirectory(dir);
		} else {
			int directoryId = Integer.parseInt((String) FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap().get("directoryId"));
			selectDirectory(directoryId);
		}
	}

	public void selectDirectory(Directory directory) {
		selectedDir = directory;
		createMenuItems();

		// Notify the records manager
		DocumentsRecordsManager recordsManager = ((DocumentsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"documentsRecordsManager", FacesContext.getCurrentInstance(), log));
		recordsManager.selectDirectory(directory.getMenu().getId());
		setSelectedPanel(new PageContentBean(getViewMode()));

		if (directoryModel != null) {
			directoryModel.selectDirectory(directory);
			nodeClicked();
		}
	}

	public void selectDirectory(long directoryId) {
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		selectDirectory(new Directory(menuDao.findById(directoryId)));
	}

	/**
	 * Opens the directory containing the selected search entry
	 */
	public String openInFolder() {
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

		Object entry = (Object) map.get("entry");
		if (entry == null)
			entry = (Object) map.get("documentRecord");

		long docId = 0;

		if (entry instanceof Hit) {
			if (((DocumentResult) entry).getShortcut() != null)
				docId = ((DocumentResult) entry).getShortcut().getId();
			else
				docId = ((DocumentResult) entry).getDocId();
		} else if (entry instanceof DocumentRecord) {
			if (((DocumentRecord) entry).getShortcut() != null)
				docId = ((DocumentRecord) entry).getShortcut().getId();
			else
				docId = ((DocumentRecord) entry).getDocId();
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = docDao.findById(docId);
		Menu folder = document.getFolder();

		if (FOLDER_VIEW_TREE.equals(getFolderView()) && getDirectoryModel() != null) {
			getDirectoryModel().openFolder(folder.getId());
			getDirectoryModel().selectDirectory(folder.getId());
		}

		selectDirectory(folder.getId());
		highlightDocument(docId);
		setSelectedPanel(new PageContentBean(getViewMode()));

		// Show the documents browsing panel
		NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log));
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu documentsMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);

		PageContentBean panel = new PageContentBean(documentsMenu.getId(), "document/browse");
		panel.setContentTitle(Messages.getMessage(documentsMenu.getText()));
		navigation.setSelectedPanel(panel);

		return null;
	}

	public void highlightDocument(long docId) {
		// Notify the records manager
		DocumentsRecordsManager recordsManager = ((DocumentsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"documentsRecordsManager", FacesContext.getCurrentInstance(), log));
		recordsManager.selectHighlightedDocument(docId);
	}

	public String delete() {
		FolderDAO folderDao = (FolderDAO) Context.getInstance().getBean(FolderDAO.class);
		try {
			// Create the folder history event. It will be used by the
			// 'deleteAll' methods of MenuDAO and DocumentDAO. The event will be
			// set by DAOs.
			History transaction = new History();
			transaction.setSessionId(SessionManagement.getCurrentUserSessionId());
			transaction.setComment("");
			transaction.setUser(SessionManagement.getUser());

			List<Menu> notDeletableFolders = folderDao.deleteTree(selectedDir.getMenu(), transaction);
			if (notDeletableFolders.size() > 0)
				Messages.addLocalizedWarn("errors.action.deletefolder");
			else
				Messages.addLocalizedInfo("msg.action.deleteitem");
		} catch (Exception e) {
			Messages.addLocalizedError("errors.action.deleteitem");
			log.error(e.getMessage(), e);
		}

		if (folderDao.findById(getSelectedDir().getMenuId()) == null) {
			Directory parent = new Directory(folderDao.findById(getSelectedDir().getMenu().getParentId()));

			if (FOLDER_VIEW_TREE.equals(getFolderView())) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) directoryModel.getSelectedNode()
						.getParent();
				directoryModel.removeNodeFromParent(directoryModel.getSelectedNode());
				if (parentNode.getChildCount() == 0) {
					Directory dir = ((Directory) parentNode.getUserObject());
					dir.setExpanded(false);
					directoryModel.reloadAll();
				}
			}

			selectDirectory(parent);
		}
		
		setSelectedPanel(new PageContentBean(getViewMode()));
		refresh();
		return null;
	}

	public String edit() {
		setSelectedPanel(new PageContentBean("updateDir"));
		DirectoryEditForm form = ((DirectoryEditForm) FacesUtil.accessBeanFromFacesContext("directoryForm",
				FacesContext.getCurrentInstance(), log));
		form.setDirectory(getSelectedDir());

		return null;
	}

	public String rights() {
		setSelectedPanel(new PageContentBean("rights"));
		RightsRecordsManager form = ((RightsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"rightsRecordsManager", FacesContext.getCurrentInstance(), log));
		form.selectDirectory(getSelectedDir());
		return null;
	}

	public String searchInFolder() {
		NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log));

		PageContentBean page = new PageContentBean("advancedSearch", "search/advancedSearch");
		page.setContentTitle(Messages.getMessage("search.advanced"));
		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
		page.setIcon(style.getImagePath("search.png"));

		SearchForm form = ((SearchForm) FacesUtil.accessBeanFromFacesContext("searchForm", FacesContext
				.getCurrentInstance(), log));

		Menu currMenu = getSelectedDir().getMenu();

		form.setParentPathDescr(currMenu.getText());

		navigation.setSelectedPanel(page);

		// It is necessary to visualise the correct breadcrumb
		MenuBarBean menuBar = ((MenuBarBean) FacesUtil.accessBeanFromFacesContext("menuBar", FacesContext
				.getCurrentInstance(), log));
		menuBar.selectItem("m-15", page);

		return null;
	}

	public String newDirectory() {
		setSelectedPanel(new PageContentBean("newDir"));
		DirectoryEditForm form = ((DirectoryEditForm) FacesUtil.accessBeanFromFacesContext("directoryForm",
				FacesContext.getCurrentInstance(), log));
		GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Group adminGroup = gdao.findByName("admin");

		long[] groups = SessionManagement.getUser().getGroupIds();
		// Add the admin group if not specified
		boolean found = false;
		for (int i = 0; i < groups.length; i++) {
			if (groups[i] == adminGroup.getId())
				found = true;
		}
		if (!found) {
			long[] tmp = new long[groups.length + 1];
			for (int i = 0; i < groups.length; i++)
				tmp[i] = groups[i];
			tmp[groups.length] = adminGroup.getId();
			groups = tmp;
		}

		form.setMenuGroup(groups);
		form.setFolderName("");
		form.setFolderDescription("");
		form.refresh();

		return null;
	}

	public String deleteDirectory() {

		setSelectedPanel(new PageContentBean("deleteDir"));
		DirectoryEditForm form = ((DirectoryEditForm) FacesUtil.accessBeanFromFacesContext("directoryForm",
				FacesContext.getCurrentInstance(), log));
		form.setDirectory(getSelectedDir());

		return null;
	}

	void loadTree() {
		directoryModel = new DirectoryTreeModel();
		selectDirectory(Menu.MENUID_DOCUMENTS);
	}

	public void openFolderSelector(ActionEvent e) {
		showFolderSelector = true;
	}

	public void closeFolderSelector(ActionEvent e) {
		showFolderSelector = false;
	}

	public void folderSelected(ActionEvent e) {
		Directory dir = getDirectoryModel().getSelectedDir();
		if (dir != null)
			selectDirectory(dir);
		showFolderSelector = false;
	}

	public void cancelFolderSelector(ActionEvent e) {
		directoryModel.cancelSelection();
		showFolderSelector = false;
	}

	public String showDocuments() {
		this.setSelectedPanel(new PageContentBean(getViewMode()));
		return null;
	}

	/**
	 * Opens the discussion containing the selected article
	 */
	public String showDiscussion() {

		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

		Object entry = (Object) map.get("comment");

		DiscussionComment article = (DiscussionComment) entry;
		DiscussionThreadDAO ddao = (DiscussionThreadDAO) Context.getInstance().getBean(DiscussionThreadDAO.class);
		DocumentDAO docdao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		DiscussionThread thread = ddao.findById(article.getThreadId());
		ddao.initialize(thread);
		Document document = docdao.findById(thread.getDocId());

		long folderId = Menu.MENUID_DOCUMENTS;
		Menu folder = document.getFolder();
		folderId = folder.getId();
		selectDirectory(folderId);

		setSelectedPanel(new PageContentBean(getViewMode()));

		// Show the documents browsing panel
		NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log));

		Menu documentsMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);

		PageContentBean panel = new PageContentBean(documentsMenu.getId(), "document/browse");
		panel.setContentTitle(Messages.getMessage(documentsMenu.getText()));
		navigation.setSelectedPanel(panel);

		// Show the discussion panel
		DiscussionsManager discussionsManager = ((DiscussionsManager) FacesUtil.accessBeanFromFacesContext(
				"discussionsManager", FacesContext.getCurrentInstance(), log));
		discussionsManager.selectDocument(document);
		discussionsManager.setSelectedThread(thread);
		discussionsManager.showComments();

		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));
		documentNavigation.setSelectedPanel(new PageContentBean("discussions"));

		return null;
	}

	public void refresh(long docId) {
		// Notify the records manager
		DocumentsRecordsManager recordsManager = ((DocumentsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"documentsRecordsManager", FacesContext.getCurrentInstance(), log));
		recordsManager.refresh(docId);
		setSelectedPanel(new PageContentBean(getViewMode()));
	}

	public String getFolderExportLink() {
		return "zip-export?menuId=" + selectedDir.getMenuId();
	}

	public String getFolderRssLink() {
		return "folder_rss?folderId=" + selectedDir.getMenuId();
	}

	public String history() {
		setSelectedPanel(new PageContentBean("historyDir"));
		HistoryRecordsManager historyRecordsManager = ((HistoryRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"historyRecordsManager", FacesContext.getCurrentInstance(), log));
		historyRecordsManager.selectFolder(getSelectedDir().getMenu());
		return null;
	}

	public void nodeClicked(ActionEvent event) {
		nodeClicked();
	}

	public void nodeClicked() {
		if (FOLDER_VIEW_TREE.equals(getFolderView()) && treeComponent != null) {
			try {
				DefaultMutableTreeNode node = treeComponent.getNavigatedNode();
				if (treeComponent.getCurrentNode() != null
						&& treeComponent.getCurrentNode().equals(directoryModel.getSelectedNode()))
					node = directoryModel.getSelectedNode();
				if (node != null) {
					IceUserObject userObject = (IceUserObject) node.getUserObject();
					if (userObject.isExpanded()) {
						directoryModel.reload(node);
					}
				}
			} catch (Throwable t) {
				log.warn(t.getMessage());
			}
		}
	}

	public String moveDirectory() {
		setSelectedPanel(new PageContentBean("moveDir"));
		DirectoryEditForm form = ((DirectoryEditForm) FacesUtil.accessBeanFromFacesContext("directoryForm",
				FacesContext.getCurrentInstance(), log));
		form.setDirectory(getSelectedDir());

		return null;
	}
}