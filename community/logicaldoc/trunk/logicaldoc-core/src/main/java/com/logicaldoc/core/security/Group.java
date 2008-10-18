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

	private String name = "";

	private String descriprion = "";

	private Set<User> users = new HashSet<User>();

	public Group() {
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