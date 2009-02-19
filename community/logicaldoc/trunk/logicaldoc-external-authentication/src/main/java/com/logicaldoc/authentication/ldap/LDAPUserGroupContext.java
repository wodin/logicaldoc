package com.logicaldoc.authentication.ldap;

import java.util.ArrayList;

/**
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class LDAPUserGroupContext {
	private String userIdentiferAttribute;

	private String logonAttribute;

	private String userClass;

	private String groupClass;

	private String groupIdentiferAttribute;

	private ArrayList<String> userBase;

	private ArrayList<String> groupBase;

	public String getUserIdentiferAttribute() {
		return userIdentiferAttribute;
	}

	public void setUserIdentiferAttribute(String userIdentiferAttribute) {
		this.userIdentiferAttribute = userIdentiferAttribute;
	}

	public String getUserClass() {
		return userClass;
	}

	public void setUserClass(String userClass) {
		this.userClass = userClass;
	}

	public void setGroupClass(String groupClass) {
		this.groupClass = groupClass;
	}

	public void setGroupIdentiferAttribute(String groupIdentiferAttribute) {
		this.groupIdentiferAttribute = groupIdentiferAttribute;
	}

	public String getGroupClass() {
		return groupClass;
	}

	public String getGroupIdentiferAttribute() {
		return groupIdentiferAttribute;
	}

	public void setGroupBase(ArrayList<String> groupBase) {
		this.groupBase = groupBase;
	}

	public ArrayList<String> getGroupBase() {
		return groupBase;
	}

	public void setUserBase(ArrayList<String> userBase) {
		this.userBase = userBase;
	}

	public ArrayList<String> getUserBase() {
		return userBase;
	}

	public void setLogonAttribute(String logonAttribute) {
		this.logonAttribute = logonAttribute;
	}

	public String getLogonAttribute() {
		return logonAttribute;
	}
}
