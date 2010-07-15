package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

import com.logicaldoc.gui.common.client.Constants;

/**
 * Represents a folder from the GUI view
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIFolder implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private long parentId;

	private String description;

	private String name;

	private String[] permissions = new String[] {};

	private GUIRight[] rights = new GUIRight[] {};

	private GUIFolder[] path = null;

	private String pathExtended;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String[] getPermissions() {
		return permissions;
	}

	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}

	public boolean isWrite() {
		return hasPermission(Constants.PERMISSION_WRITE);
	}

	public boolean hasPermission(String permission) {
		for (String p : permissions) {
			if (p.equals(permission))
				return true;
		}
		return false;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPathExtended() {
		return pathExtended;
	}

	public void setPathExtended(String pathExtended) {
		this.pathExtended = pathExtended;
	}

	public GUIRight[] getRights() {
		return rights;
	}

	public void setRights(GUIRight[] rights) {
		this.rights = rights;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public GUIFolder[] getPath() {
		return path;
	}

	public void setPath(GUIFolder[] path) {
		this.path = path;
		this.pathExtended = "";
		for (GUIFolder folder : path) {
			if (folder.getId() != Constants.DOCUMENTS_FOLDERID)
				pathExtended += "/" + folder.getName();
		}
		pathExtended += "/" + getName();
	}

	public GUIFolder getParent() {
		if (getPath() != null && getPath().length > 0)
			return getPath()[getPath().length - 1];
		else
			return null;
	}
}
