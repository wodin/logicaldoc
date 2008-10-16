package com.logicaldoc.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;

/**
 * This class represents the key concept of security. A Menu not only models
 * menues but also it is used as an element to build hierarchies. With
 * menugroups you can associate groups to a given menu and grant some
 * permissions.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class Menu extends PersistentObject {

	public static final long MENUID_HOME = 1;

	public static final long MENUID_PERSONAL = 4;

	public static final long MENUID_DOCUMENTS = 5;

	public static final long MENUID_MESSAGES = 13;

	public static final long MENUID_EDITME = 19;

	public static final int MENUTYPE_DIRECTORY = 3;

	public static final int MENUTYPE_ACTION = 1;

	public static final int MENUTYPE_HOME = 0;

	private long id = 0;

	private String text = "";

	private long parent = 0;

	private int sort = 0;

	private String icon = "";

	private String path = "";

	private int type = 2;

	private long size = 0;

	private String ref = "";

	protected Set<MenuGroup> menuGroups = new HashSet<MenuGroup>();

	public Menu() {
	}

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public long getParent() {
		return parent;
	}

	public int getSort() {
		return sort;
	}

	public String getIcon() {
		return icon;
	}

	public String getPath() {
		return path;
	}

	public int getType() {
		return type;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public Set<MenuGroup> getMenuGroups() {
		return menuGroups;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setType(int type) {
		this.type = type;
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
		menu.setId(this.getId());
		menu.setText(this.getText());
		menu.setParent(this.getParent());
		menu.setSort(this.getSort());
		menu.setIcon(this.getIcon());
		menu.setPath(this.getPath());
		menu.setType(this.getType());
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
			String[] menus = path.split("/");

			if (menus.length > 0) {
				if (menus[0].length() > 0) {
					MenuDAO menuDao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
					Menu home = menuDao.findByPrimaryKey(Menu.MENUID_HOME);
					coll.add(home);

					for (int i = 1; i < menus.length; i++) {
						try {
							Menu menu = menuDao.findByPrimaryKey(Integer.parseInt(menus[i]));
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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}