package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * User bean as used in the GUI
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUIUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userName;

	private String sid;

	private long id = 0;

	private String[] features = new String[0];

	private String[] groups = new String[0];

	private String firstName;

	private String name;

	private boolean expired = true;

	private int passwordMinLenght = 0;

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String[] getFeatures() {
		return features;
	}

	public void setFeatures(String[] features) {
		this.features = features;
	}

	public String[] getGroups() {
		return groups;
	}

	public void setGroups(String[] groups) {
		this.groups = groups;
	}

	public boolean isMemberOf(String group) {
		for (String g : groups) {
			if (group.equals(g))
				return true;
		}
		return false;
	}

	public String getFullName() {
		String fullName = getFirstName();
		if (fullName != null && getName() != null)
			fullName += " " + getName();
		if (fullName == null && getName() != null)
			fullName = getName();
		if (fullName == null)
			fullName = getUserName();
		return fullName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public int getPasswordMinLenght() {
		return passwordMinLenght;
	}

	public void setPasswordMinLenght(int passwordMinLenght) {
		this.passwordMinLenght = passwordMinLenght;
	}
}