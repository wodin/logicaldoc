package com.logicaldoc.web.document;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.icesoft.faces.component.tree.IceUserObject;
import com.icesoft.faces.component.tree.Tree;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.web.SessionManagement;
import com.logicaldoc.web.StyleBean;
import com.logicaldoc.web.admin.GuiBean;
import com.logicaldoc.web.i18n.Messages;
import com.logicaldoc.web.util.FacesUtil;

/**
 * A tree model specialized for LogicalDOC's directories and menues
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class DirectoryTreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 1L;

	private static final String FOLDER_VIEW_TREE = "tree";

	protected static Log log = LogFactory.getLog(DirectoryTreeModel.class);

	// Utility map of all directories (key is the menuId)
	private Map<Long, Directory> directories = new HashMap<Long, Directory>();

	private Directory selectedDir;

	private long rootMenuId = Menu.MENUID_DOCUMENTS;

	private int menuType = Menu.MENUTYPE_DIRECTORY;

	private DefaultMutableTreeNode selectedNode;

	private boolean countChildren = false;

	private boolean useMenuIcons = false;

	private boolean running = false;

	public DirectoryTreeModel(long rootMenuId, int menuType) {
		super(new DefaultMutableTreeNode());
		this.rootMenuId = rootMenuId;
		this.menuType = menuType;
	}

	public DirectoryTreeModel() {
		super(new DefaultMutableTreeNode());
		reload();
	}

	public boolean isCountChildren() {
		return countChildren;
	}

	public void setCountChildren(boolean countChildren) {
		this.countChildren = countChildren;
	}

	public boolean isUseMenuIcons() {
		return useMenuIcons;
	}

	public void setUseMenuIcons(boolean useMenuIcons) {
		this.useMenuIcons = useMenuIcons;
	}

	public void reloadAll() {
		init();
		running = true;
	
		Thread reloadTh = new Thread() {
			@Override
			public void run() {
				try {
					reload((DefaultMutableTreeNode) getRoot(), -1);
				} catch (Exception e) {
				}
				running = false;
			}
		};
		reloadTh.start();

		HttpServletRequest request=(HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
		long timeToWait = Long.parseLong(request.getSession().getServletContext().getInitParameter("com.icesoft.faces.connectionTimeout"))-5000;
		int counter = 0;
		while (counter < timeToWait) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

			}
			if (running)
				counter+=1000;
			else
				break;
		}
		reloadTh.interrupt();
	}

	public void reload(DefaultMutableTreeNode node, int depth) {
		Directory dir = ((Directory) node.getUserObject());
		long userId = SessionManagement.getUserId();
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		try {
			List<Menu> menus = (List<Menu>) menuDao.findByUserId(userId, dir.getMenuId(), menuType);

			// Sort by name
			Collections.sort(menus, new Comparator<Menu>() {
				@Override
				public int compare(Menu arg0, Menu arg1) {
					return arg0.getText().compareTo(arg1.getText());
				}
			});

			for (Menu menu : menus) {
				addDir(userId, node, menu, depth);
				dir.setLeaf(false);
			}

			if (countChildren)
				dir.setCount(menuDao.countByUserId(userId, dir.getMenuId(), null));
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
	}

	public void reload(DefaultMutableTreeNode node) {
		reload(node, 1);
	}

	private void init() {
		directories.clear();

		// build root node so that children can be attached
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu rootMenu = menuDao.findById(rootMenuId);
		Directory rootObject = new Directory(rootMenu);
		if (useMenuIcons) {
			StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
			rootObject.setIcon(style.getImagePath(rootMenu.getIcon()));
		} else {
			rootObject.setIcon(StyleBean.XP_BRANCH_CONTRACTED_ICON);
		}

		rootObject.setDisplayText(null);
		rootObject.setContentTitle(null);
		rootObject.setPageContent(true);

		String label = null;
		if (rootMenu.getId() == Menu.MENUID_DOCUMENTS || rootMenu.getText().startsWith("menu."))
			label = Messages.getMessage(rootMenu.getText());
		else
			label = rootMenu.getText();

		rootObject.setDisplayText(label);
		rootObject.setContentTitle(label);
		rootObject.setPathExtended(label);

		DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(rootObject);
		rootObject.setWrapper(rootTreeNode);
		setRoot(rootTreeNode);
		rootTreeNode.setUserObject(rootObject);
		directories.put(rootMenu.getId(), rootObject);
		if (countChildren)
			rootObject.setCount(menuDao.countByUserId(SessionManagement.getUserId(), rootMenuId, null));
	}

	public void reload() {
		init();
		reload((DefaultMutableTreeNode) getRoot());
	}

	public Map<Long, Directory> getDirectories() {
		return directories;
	}

	public Directory getDirectory(long id) {
		return directories.get(id);
	}

	/**
	 * Adds a new directory in the specified place
	 * 
	 * @param dir The new directory's menu
	 * @param parent The parent directory
	 */
	public void addNewDir(Menu dir, Directory parent) {
		Directory parentDir = parent;

		if (parentDir == null) {
			parentDir = selectedDir;
		}

		if (countChildren)
			parentDir.setCount(parentDir.getCount() + 1);
		parentDir.setLeaf(false);

		DefaultMutableTreeNode parentNode = findDirectoryNode(parentDir.getMenuId(), (DefaultMutableTreeNode) getRoot());
		addDir(SessionManagement.getUsername(), parentNode, dir);
	}

	private DefaultMutableTreeNode addDir(String username, DefaultMutableTreeNode parentNode, Menu dir) {
		return addDir(SessionManagement.getUserId(), parentNode, dir, -1);
	}

	/**
	 * Changes the currently selected directory and updates the documents list.
	 * 
	 * @param directoryId
	 */
	public void selectDirectory(Directory directory) {
		selectedDir = directory;
		if (selectedDir != null) {
			selectedDir.setSelected(true);
			setTreeSelectedState((DefaultMutableTreeNode) getRoot());
			expandNodePath(selectedNode);
		}
	}

	/**
	 * Finds the directory node with the specified identifier contained in a
	 * sublevel of the the passed tree node
	 * 
	 * @param direcoryId The directory identifier
	 * @param parent The node in which the directory must be searched
	 * @return The found tree node, null if not found
	 */
	@SuppressWarnings("unchecked")
	private DefaultMutableTreeNode findDirectoryNode(long direcoryId, DefaultMutableTreeNode parent) {
		Directory dir = (Directory) parent.getUserObject();

		if (dir.getMenu().getId() == direcoryId) {
			return parent;
		} else {
			Enumeration<DefaultMutableTreeNode> enumer = (Enumeration<DefaultMutableTreeNode>) parent.children();

			while (enumer.hasMoreElements()) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) enumer.nextElement();
				DefaultMutableTreeNode node = findDirectoryNode(direcoryId, childNode);

				if (node != null) {
					return node;
				}
			}
		}

		return null;
	}

	/**
	 * Adds a directory and all it's childs
	 * 
	 * @param userId
	 * @param parent
	 * @param dir
	 * @param depth the maximum depth
	 * @return
	 */
	private DefaultMutableTreeNode addDir(long userId, DefaultMutableTreeNode parent, Menu dir, int depth) {
		MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		Directory cachedDir = directories.get(dir.getId());

		if ((cachedDir != null) && cachedDir.isLoaded()) {
			DefaultMutableTreeNode node = findDirectoryNode(dir.getId(), parent);

			if (node != null) {
				return node;
			}
		}

		// Component menu item
		Directory branchObject = new Directory(dir);
		String label = null;
		if (dir.getId() == Menu.MENUID_DOCUMENTS || dir.getText().startsWith("menu."))
			label = Messages.getMessage(dir.getText());
		else
			label = dir.getText();

		branchObject.setDisplayText(label);
		branchObject.setContentTitle(label);
		StyleBean style = (StyleBean) Context.getInstance().getBean(StyleBean.class);
		if (useMenuIcons) {
			branchObject.setIcon(style.getImagePath(dir.getIcon()));
		} else {
			branchObject.setIcon(StyleBean.XP_BRANCH_CONTRACTED_ICON);
		}

		DefaultMutableTreeNode branchNode = new DefaultMutableTreeNode(branchObject);
		branchObject.setWrapper(branchNode);
		branchObject.setPageContent(false);
		branchObject.setLeaf(true);
		branchObject.setExpanded(false);
		branchObject.setPathExtended(((Directory) parent.getUserObject()).getPathExtended() + "/"
				+ branchObject.getDisplayText());
		branchNode.setUserObject(branchObject);

		// Iterate over subdirs
		if (depth != 0) {
			List<Menu> children = (List<Menu>) menuDao.findByUserId(userId, dir.getId(), menuType);
			Collections.sort(children, new Comparator<Menu>() {
				@Override
				public int compare(Menu menu1, Menu menu2) {
					return menu1.getText().compareTo(menu2.getText());
				}
			});

			for (Menu child : children) {
				branchObject.setLeaf(false);

				if (depth > 0) {
					addDir(userId, branchNode, child, depth - 1);
				} else if (depth == -1) {
					addDir(userId, branchNode, child, depth);
				}
			}
		} else {
			branchObject.setLeaf(menuDao.countByUserId(userId, dir.getId(), menuType) == 0);
		}

		branchObject.setLoaded(true);
		directories.put(dir.getId(), branchObject);
		parent.add(branchNode);
		if (countChildren)
			branchObject.setCount(menuDao.countByUserId(userId, dir.getId(), null));

		log.debug("added dir " + branchObject.getDisplayText());
		return branchNode;
	}

	public void selectDirectory(long id) {
		selectedDir = directories.get(id);
		if (selectedDir == null) {
			// Perhaps the node was not already visited, so try to load the
			// entire tree
			reloadAll();
			selectedDir = directories.get(id);
		}
		selectDirectory(selectedDir);
	}

	/**
	 * Set the selection state for all directories in the tree
	 */
	@SuppressWarnings("unchecked")
	protected void setTreeSelectedState(DefaultMutableTreeNode node) {
		if ((node.getUserObject() != null) && node.getUserObject() instanceof Directory) {
			Directory dir = (Directory) node.getUserObject();

			if ((selectedDir != null) && (dir.getMenu() != null) && dir.getMenu().equals(selectedDir.getMenu())) {
				dir.setSelected(true);
				if (!useMenuIcons)
					dir.setIcon(dir.getBranchExpandedIcon());
				selectedNode = node;
			} else {
				dir.setSelected(false);
				if (!useMenuIcons)
					dir.setIcon(dir.getBranchContractedIcon());
			}
		}

		Enumeration<DefaultMutableTreeNode> enumer = (Enumeration<DefaultMutableTreeNode>) node.children();

		while (enumer.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) enumer.nextElement();
			setTreeSelectedState(childNode);
		}
	}

	public Directory getSelectedDir() {
		return selectedDir;
	}

	public void cancelSelection() {
		selectedDir = null;
		directories.clear();
		reload();
	}

	public DefaultMutableTreeNode getSelectedNode() {
		return selectedNode;
	}

	/**
	 * Expand all nodes from the passed one back to the root following the
	 * parent/child relation.
	 * 
	 * @param node the leaf node to expand
	 */
	private void expandNodePath(DefaultMutableTreeNode node) {
		if (node == null)
			return;

		IceUserObject obj = (IceUserObject) node.getUserObject();
		obj.setExpanded(true);

		if (!node.equals(getRoot())) {
			expandNodePath((DefaultMutableTreeNode) node.getParent());
		}
	}

	/**
	 * Opens and selects the specified folder. The algorithm is optimised so
	 * that the minimal path is explored and database accesses are reduced.
	 * 
	 * @param folderId The folder folder id
	 */
	public void openFolder(long folderId) {
		GuiBean guiBean = ((GuiBean) FacesUtil.accessBeanFromFacesContext("guiBean", FacesContext.getCurrentInstance(),
				log));
		String folderView = guiBean.getViewModeFolder();
		if (!folderView.equals(FOLDER_VIEW_TREE)) {
			// Reset the tree
			init();
			// Now try to construct the minimal portion of the tree, just to
			// show opened folder
			MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			Menu folderMenu = menuDao.findById(folderId);
			Collection<Menu> parents = menuDao.findParents(folderId);
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) getRoot();
			long userId = SessionManagement.getUserId();
			for (Menu folderParent : parents) {
				if (folderParent.getId() == rootMenuId)
					continue;
				parentNode = addDir(userId, parentNode, folderParent, 0);
			}
			parentNode = addDir(userId, parentNode, folderMenu, 0);
			expandNodePath(parentNode);
		} else {
			selectDirectory(folderId);
		}
	}

	public void onSelectDirectory(ActionEvent event) {
		long directoryId = Long.parseLong((String) FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("directoryId"));
		selectDirectory(directoryId);
		reload(getSelectedNode());
	}

	public void nodeClicked(ActionEvent event) {
		Tree tree = (Tree) event.getSource();
		DefaultMutableTreeNode node = tree.getNavigatedNode();
		IceUserObject userObject = (IceUserObject) node.getUserObject();

		if (userObject.isExpanded()) {
			reload(node);
		}
	}
}