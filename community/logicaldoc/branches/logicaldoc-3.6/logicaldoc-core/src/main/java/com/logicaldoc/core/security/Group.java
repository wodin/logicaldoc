package com.logicaldoc.core.security;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * This class represents groups.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 2L;

	private String groupName = "";

	private String groupDesc = "";

	private Set<User> users = new HashSet<User>();

	public Group() {
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getGroupDesc() {
		return groupDesc;
	}

	public void setGroupName(String name) {
		groupName = name;
	}

	public void setGroupDesc(String desc) {
		groupDesc = desc;
	}

	public void reset() {
		groupName = "";
		groupDesc = "";
		users = new HashSet<User>();
	}

	public String toString() {
		// return ReflectionToStringBuilder.toString(this);
		return (new ReflectionToStringBuilder(this) {
			protected boolean accept(java.lang.reflect.Field f) {
				return super.accept(f);
			}
		}).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Group))
			return false;
		Group other = (Group) obj;
		return this.getGroupName().equals(other.getGroupName());
	}

	@Override
	public int hashCode() {
		return groupName.hashCode();
	}
}