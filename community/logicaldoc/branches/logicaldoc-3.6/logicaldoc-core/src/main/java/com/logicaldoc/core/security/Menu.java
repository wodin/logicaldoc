package com.logicaldoc.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;

/**
 * This class represents menus.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class Menu {

	public static final int MENUID_HOME = 1;

	public static final int MENUID_PERSONAL = 4;

	public static final int MENUID_DOCUMENTS = 5;

	public static final int MENUID_MESSAGES = 13;

	public static final int MENUID_EDITME = 19;

	public static final int MENUTYPE_DIRECTORY = 3;

	public static final int MENUTYPE_FILE = 5;

	public static final int MENUTYPE_ACTION = 1;

	public static final int MENUTYPE_HOME = 0;

	// Special menu for browsing the document archive
	public static final String MENU_BROWSE = "Browse.do";

	protected int menuId = 0;

	protected String menuText = "";

	protected int menuParent = 0;

	protected int menuSort = 0;

	protected String menuIcon = "";

	protected String menuPath = "";

	protected int menuType = 2;

	protected int menuHier = 0;

	protected String menuRef = "";

	protected long menuSize = 0;

	protected Set<MenuGroup> menuGroups = new HashSet<MenuGroup>();

	// Not persistent
	protected boolean writeable = false;

	// Not pesistent
	protected Date date;

	public Menu() {
	}

	public int getMenuId() {
		return menuId;
	}

	public String getMenuText() {
		return menuText;
	}

	public int getMenuParent() {
		return menuParent;
	}

	public int getMenuSort() {
		return menuSort;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public String getMenuPath() {
		return menuPath;
	}

	public int getMenuType() {
		return menuType;
	}

	public int getMenuHier() {
		return menuHier;
	}

	public String getMenuRef() {
		return menuRef;
	}

	public Set<MenuGroup> getMenuGroups() {
		return menuGroups;
	}

	protected void setMenuId(int id) {
		menuId = id;
	}

	public void setMenuText(String text) {
		menuText = text;
	}

	public void setMenuParent(int parent) {
		menuParent = parent;
	}

	public void setMenuSort(int sort) {
		menuSort = sort;
	}

	public void setMenuIcon(String icon) {
		menuIcon = icon;
	}

	public void setMenuPath(String path) {
		menuPath = path;
	}

	public void setMenuType(int type) {
		menuType = type;
	}

	public void setMenuHier(int hier) {
		menuHier = hier;
	}

	public void setMenuRef(String ref) {
		menuRef = ref;
	}

	public void setMenuGroups(Set<MenuGroup> mgroup) {
		menuGroups = mgroup;
	}

	public String[] getMenuGroupNames() {
		ArrayList<String> ids = new ArrayList<String>();
		for (MenuGroup mg : menuGroups) {
			ids.add(mg.getGroupName());
		}
		return (String[]) ids.toArray(new String[] {});
	}

	/**
	 * Adds MenuGroup object given in a String array to the ArrayList of
	 * MenuGroups.
	 * 
	 * @param groups
	 */
	public void setMenuGroup(String[] groups) {
		menuGroups.clear();
		for (int i = 0; i < groups.length; i++) {
			MenuGroup mg = new MenuGroup();
			mg.setGroupName(groups[i]);
			mg.setWriteEnable(1);
			menuGroups.add(mg);
		}
	}

	public Menu copy() {
		Menu menu = new Menu();
		menu.setMenuId(this.getMenuId());
		menu.setMenuText(this.getMenuText());
		menu.setMenuParent(this.getMenuParent());
		menu.setMenuSort(this.getMenuSort());
		menu.setMenuIcon(this.getMenuIcon());
		menu.setMenuPath(this.getMenuPath());
		menu.setMenuType(this.getMenuType());
		menu.setMenuHier(this.getMenuHier());
		menu.setMenuRef(this.getMenuRef());
		menu.setMenuGroups(this.getMenuGroups());
		return menu;
	}

	/**
	 * Returns a Collection of menus being a parent of the given menu. The
	 * collection is sorted by the hierarchy level (menuHier) of the menus.
	 * 
	 * @return
	 */
	public Collection<Menu> getParents() {
		Collection<Menu> coll = new ArrayList<Menu>();

		try {
			String[] menus = menuPath.split("/");

			if (menus.length > 0) {
				if (menus[0].length() > 0) {
					MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(
							MenuDAO.class);
					Menu home = menuDao.findByPrimaryKey(Menu.MENUID_HOME);
					coll.add(home);

					for (int i = 1; i < menus.length; i++) {
						try {
							Menu menu = menuDao.findByPrimaryKey(Integer
									.parseInt(menus[i]));
							coll.add(menu);
						} catch (NumberFormatException nfe) {
							;
						}
					}
				}
			}
		} catch (Exception e) {
			;
		}

		return coll;
	}

	public MenuGroup getMenuGroup(String groupName) {
		for (MenuGroup mg : menuGroups) {
			if (mg.getGroupName().equals(groupName))
				return mg;
		}
		return null;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Menu))
			return false;
		Menu other = (Menu) arg0;
		return other.getMenuId() == this.getMenuId();
	}

	@Override
	public int hashCode() {
		return new Integer(menuId).hashCode();
	}

	/**
	 * The size of the file associated to this menu expressed in bytes
	 */
	public long getMenuSize() {
		return menuSize;
	}

	public void setMenuSize(long size) {
		this.menuSize = size;
	}

	public boolean isWriteable() {
		return writeable;
	}

	public void setWriteable(boolean writeable) {
		this.writeable = writeable;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}