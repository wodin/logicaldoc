package com.logicaldoc.web.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.document.Article;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.searchengine.Result;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.navigation.MenuItem;
import com.logicaldoc.web.navigation.NavigationBean;
import com.logicaldoc.web.navigation.PageContentBean;
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
 * @author Marco Meschieri
 * @version $Id: DocumentNavigation.java,v 1.14 2006/08/29 16:33:46 marco Exp $
 * @since 3.0
 */
public class DocumentNavigation extends NavigationBean {
	protected static Log log = LogFactory.getLog(DocumentNavigation.class);

	// list for the dynamic menus w/ getter & setter
	protected List<MenuItem> folderItems = new ArrayList<MenuItem>();

	private Directory selectedDir;

	private DirectoryTreeModel directoryModel;

	private boolean showFolderSelector = false;

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

	public boolean isShowFolderSelector() {
		return showFolderSelector;
	}

	public void setShowFolderSelector(boolean showFolderSelector) {
		this.showFolderSelector = showFolderSelector;
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
		String lang = SessionManagement.getLanguage();
		try {
			if (getSelectedDir() != null) {
				Directory currentDir = getSelectedDir();
				while (currentDir.getMenuId() != Menu.MENUID_DOCUMENTS) {
					breadcrumb.add(currentDir);
					currentDir = new Directory(menuDao.findByPrimaryKey(currentDir.getMenu().getParent()));
				}
				Directory rootDir = new Directory(menuDao.findByPrimaryKey(Menu.MENUID_DOCUMENTS));
				rootDir.setDisplayText(Messages.getMessage(rootDir.getMenu().getText()));
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
		selectDirectory(getSelectedDir());
	}

	/**
	 * Finds all sub dirs menus accessible by the current
	 * 
	 * @return
	 */
	protected void createMenuItems() {
		folderItems.clear();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Directory parentDir = new Directory(menuDao.findByPrimaryKey(selectedDir.getMenu().getParent()));
		if (parentDir.getMenuId() == Menu.MENUID_DOCUMENTS)
			parentDir.setDisplayText(Messages.getMessage(parentDir.getMenu().getText()));
		MenuItem item = createMenuItem("<< " + parentDir.getDisplayText(), parentDir);

		if (parentDir.getMenuId() != Menu.MENUID_HOME) {
			// Add parent folder as first menu
			folderItems.add(item);
		}

		String username = SessionManagement.getUsername();
		List<Menu> menues = (List<Menu>) menuDao.findByUserName(username, selectedDir.getMenuId(),
				Menu.MENUTYPE_DIRECTORY);
		Collections.sort(menues, new Comparator<Menu>() {
			@Override
			public int compare(Menu menu1, Menu menu2) {
				return menu1.getText().compareTo(menu2.getText());
			}
		});
		for (Menu menu : menues) {
			item = createMenuItem(menu.getText(), new Directory(menu));
			folderItems.add(item);
		}
	}

	protected MenuItem createMenuItem(String label, Directory dir) {
		MenuItem menuItem = new MenuItem();
		menuItem.setValue(label);
		menuItem.setId("dir-" + dir.getMenuId());
		menuItem.setActionListener(FacesUtil
				.createActionListenerMethodBinding("#{documentNavigation.onSelectDirectory}"));
		menuItem.setIcon(StyleBean.getImagePath("folder.png"));
		menuItem.setUserObject(dir);
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
		setSelectedPanel(new PageContentBean("documents"));
	}

	public void selectDirectory(long directoryId) {
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		selectDirectory(new Directory(menuDao.findByPrimaryKey(directoryId)));
	}

	/**
	 * Opens the directory containing the selected search entry
	 */
	public String openInFolder() {
		Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

		Object entry = (Object) map.get("entry");

		long menuId = Menu.MENUID_DOCUMENTS;

		long docId = 0;

		if (entry instanceof Result) {
			docId = ((Result) entry).getDocId();
		} else if (entry instanceof DocumentRecord) {
			docId = ((DocumentRecord) entry).getDocId();
		}

		DocumentDAO docDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		Document document = docDao.findByPrimaryKey(docId);
		Menu folder = document.getFolder();
		selectDirectory(folder.getId());
		highlightDocument(docId);
		setSelectedPanel(new PageContentBean("documents"));

		// Show the documents browsing panel
		NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log));
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu documentsMenu = menuDao.findByPrimaryKey(Menu.MENUID_DOCUMENTS);

		PageContentBean panel = new PageContentBean("m-" + documentsMenu.getId(), "document/browse");
		panel.setContentTitle(Messages.getMessage(documentsMenu.getText()));
		navigation.setSelectedPanel(panel);

		return null;
	}

	/**
	 * Opens the discussion containing the selected article
	 */
	public String showDiscussion() {

		Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

		Object entry = (Object) map.get("article");

		Article article = (Article) entry;
		DocumentDAO docdao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Document document = docdao.findByPrimaryKey(article.getDocId());

		long folderId = Menu.MENUID_DOCUMENTS;
		Menu folder = document.getFolder();
		folderId = folder.getId();
		selectDirectory(folderId);

		setSelectedPanel(new PageContentBean("documents"));

		// Show the documents browsing panel
		NavigationBean navigation = ((NavigationBean) FacesUtil.accessBeanFromFacesContext("navigation", FacesContext
				.getCurrentInstance(), log));

		Menu documentsMenu = menuDao.findByPrimaryKey(Menu.MENUID_DOCUMENTS);

		PageContentBean panel = new PageContentBean("m-" + documentsMenu.getId(), "document/browse");
		panel.setContentTitle(Messages.getMessage(documentsMenu.getText()));
		navigation.setSelectedPanel(panel);

		// Show the discussion panel
		ArticlesRecordsManager articlesManager = ((ArticlesRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"articlesRecordsManager", FacesContext.getCurrentInstance(), log));

		articlesManager.selectDocument(document);

		DocumentNavigation documentNavigation = ((DocumentNavigation) FacesUtil.accessBeanFromFacesContext(
				"documentNavigation", FacesContext.getCurrentInstance(), log));

		documentNavigation.setSelectedPanel(new PageContentBean("articles"));

		return null;
	}

	private void highlightDocument(long docId) {
		// Notify the records manager
		DocumentsRecordsManager recordsManager = ((DocumentsRecordsManager) FacesUtil.accessBeanFromFacesContext(
				"documentsRecordsManager", FacesContext.getCurrentInstance(), log));
		recordsManager.selectHighlightedDocument(docId);
	}

	public String delete() {
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		try {
			menuDao.delete(selectedDir.getMenuId());
			Messages.addLocalizedInfo("msg.action.deleteitem");
		} catch (Exception e) {
			Messages.addLocalizedError("errors.action.deleteitem");
		}

		Directory parent = new Directory(menuDao.findByPrimaryKey(getSelectedDir().getMenu().getParent()));
		selectDirectory(parent);
		setSelectedPanel(new PageContentBean("documents"));

		loadTree();
		return null;
	}

	public String edit() {
		setSelectedPanel(new PageContentBean("updateDir"));
		DirectoryEditForm form = ((DirectoryEditForm) FacesUtil.accessBeanFromFacesContext("directoryForm",
				FacesContext.getCurrentInstance(), log));
		form.setDirectory(getSelectedDir());

		loadTree();
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
		page.setIcon(StyleBean.getImagePath("extsearch.gif"));

		SearchForm form = ((SearchForm) FacesUtil.accessBeanFromFacesContext("searchForm", FacesContext
				.getCurrentInstance(), log));

		Menu currMenu = getSelectedDir().getMenu();

		String menuPath = currMenu.getPath() + "/" + currMenu.getId();
		form.setPath(menuPath);
		form.setParentPathDescr(currMenu.getText());

		navigation.setSelectedPanel(page);
		return null;
	}

	public String newDirectory() {
		setSelectedPanel(new PageContentBean("newDir"));

		DirectoryEditForm form = ((DirectoryEditForm) FacesUtil.accessBeanFromFacesContext("directoryForm",
				FacesContext.getCurrentInstance(), log));

		String[] groups = SessionManagement.getUser().getGroupNames();
		// Add the admin group if not specified
		boolean found = false;
		for (int i = 0; i < groups.length; i++) {
			if (groups[i].equals("admin"))
				found = true;
		}
		if (!found) {
			String[] tmp = new String[groups.length + 1];
			for (int i = 0; i < groups.length; i++)
				tmp[i] = groups[i];
			tmp[groups.length] = "admin";
			groups = tmp;
		}

		form.setMenuGroup(groups);
		form.setFolderName("");

		loadTree();
		return null;
	}

	void loadTree() {
		directoryModel = new DirectoryTreeModel();
	}

	public void openFolderSelector(ActionEvent e) {
		showFolderSelector = true;
	}

	public void closeFolderSelector(ActionEvent e) {
		showFolderSelector = false;
	}

	public void folderSelected(ActionEvent e) {
		Directory dir = getDirectoryModel().getSelectedDir();
		selectDirectory(dir);
	}
}
