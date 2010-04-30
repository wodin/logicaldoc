package com.logicaldoc.webservice.folder;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.webservice.document.WSDocument;

public class WSFolder {

	protected static Log log = LogFactory.getLog(WSDocument.class);

	public static final long MENUID_HOME = 1;

	public static final long MENUID_PERSONAL = 4;

	public static final long MENUID_DOCUMENTS = 5;

	public static final long MENUID_MESSAGES = 13;

	public static final long MENUID_EDITME = 19;

	public static final int MENUTYPE_DIRECTORY = 3;

	public static final int MENUTYPE_MENU = 1;

	private long id = 0;

	private String text = "";

	private long parentId = 0;

	private int sort = 0;

	private String icon = "";

	private int type = 2;

	private long size = 0;

	private String ref = "";

	private String description = "";
	
	protected Set<MenuGroup> menuGroups = new HashSet<MenuGroup>();

	public WSFolder fromFolder(Menu folder) {

		WSFolder wsFolder = new WSFolder();
		wsFolder.setId(folder.getId());
		wsFolder.setText(folder.getText());
		wsFolder.setParentId(folder.getParentId());
		wsFolder.setSort(folder.getSort());
		wsFolder.setIcon(folder.getIcon());
		wsFolder.setType(folder.getType());
		wsFolder.setSize(folder.getSize());
		wsFolder.setRef(folder.getRef());
		wsFolder.setDescription(folder.getDescription());

		return wsFolder;
	}

	public Menu toFolder(User user) throws Exception {
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		Menu folder = mdao.findById(id);
		if (folder == null) {
			log.error("Folder " + folder + " not found");
			throw new Exception("error - folder not found");
		}

		Menu menu = new Menu();
		menu.setText(text);
		menu.setParentId(parentId);
		menu.setSort(sort);
		menu.setIcon(icon);
		menu.setType(type);
		menu.setSize(size);
		menu.setRef(ref);
		menu.setDescription(description);

		return menu;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<MenuGroup> getMenuGroups() {
		return menuGroups;
	}

	public void setMenuGroups(Set<MenuGroup> menuGroups) {
		this.menuGroups = menuGroups;
	}
}