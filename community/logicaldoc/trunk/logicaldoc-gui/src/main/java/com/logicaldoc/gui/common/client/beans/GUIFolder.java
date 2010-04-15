package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * Represents a folder from the GUI view
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIFolder implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id;

	private String description;

	private String name;

	private String pathExtended;

	private String[] permissions = new String[] {};

	private GUIRight[] rights = new GUIRight[] {};

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
}
