package com.logicaldoc.core.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.core.PersistentObject;

/**
 * This class represents groups.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class Group extends PersistentObject implements Serializable {

	private static final long serialVersionUID = 2L;

	public static int TYPE_DEFAULT = 0;

	public static int TYPE_USER = 1;

	private String name = "";

	private String descriprion = "";

	private int type = TYPE_DEFAULT;

	private Set<User> users = new HashSet<User>();

	public Group() {
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return descriprion;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		descriprion = description;
	}

	public void reset() {
		name = "";
		descriprion = "";
		users = new HashSet<User>();
	}

	public void clearUsers() {
		users.clear();
		users = new HashSet<User>();
	}

	public String toString() {
		return getName();
	}
}