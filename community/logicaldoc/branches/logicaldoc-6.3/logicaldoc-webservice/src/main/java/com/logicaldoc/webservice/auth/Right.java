package com.logicaldoc.webservice.auth;

/**
 * Useful class to associate a user or a group to a permission integer
 * representation.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class Right {
	private long id;

	private int permissions;

	public Right() {
	}

	public Right(long id, int permissions) {
		this.id = id;
		this.permissions = permissions;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getPermissions() {
		return permissions;
	}

	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}
}
