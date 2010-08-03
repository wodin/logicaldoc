package com.logicaldoc.ldap.model;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.logicaldoc.util.config.PropertiesBean;

/**
 * All directory-based informations responsible for data will be stored here.
 * 
 * @author Sebastian Wenzky
 * @since 4.5
 */
public class LDAPUserGroupContext {

	private PropertiesBean config;

	/**
	 * Attribute that identifies a user explicitly regarding the user class
	 * definition (e.g: Active Directory: CN)
	 */
	private String userIdentiferAttribute;

	/**
	 * Similar to member variable userIdentiferAttribute logonAttribute defines
	 * a user on its logonAttribute at login-time in LogicalDOC. Those
	 * attributes can be the same but can differ from each other as well (e.g.
	 * Active Directory: CN is userIdentiferAttribute, logonAttribute is
	 * sAMAccountName)
	 */
	private String logonAttribute;

	private String userClass;

	/**
	 * Responsible directory class that identifies a group
	 */
	private String groupClass;

	/**
	 * Attribute that identifies a group explicitly regarding the group class
	 * definition (e.g: Active Directory: CN)
	 */
	private String groupIdentiferAttribute;

	/**
	 * List of DN where users can be stored
	 */
	private ArrayList<String> userBase = new ArrayList<String>();

	/**
	 * List of DB where groups can be stored
	 */
	private ArrayList<String> groupBase = new ArrayList<String>();

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

	public String getUserBaseString() {
		StringBuffer sb = new StringBuffer();
		for (String element : getUserBase()) {
			if (sb.length() > 0)
				sb.append(";");
			sb.append(element);
		}
		return sb.toString();
	}

	public String getGroupBaseString() {
		StringBuffer sb = new StringBuffer();
		for (String element : getGroupBase()) {
			if (sb.length() > 0)
				sb.append(";");
			sb.append(element);
		}
		return sb.toString();
	}

	/**
	 * The user base list, each element separated by a semi-colon(;)
	 */
	public void setUserBaseString(String userBaseString) {
		StringTokenizer st = new StringTokenizer(userBaseString, ";", false);
		userBase.clear();
		while (st.hasMoreTokens())
			userBase.add(st.nextToken());
	}

	/**
	 * The group base list, each element separated by a semi-colon(;)
	 */
	public void setGroupBaseString(String groupBaseString) {
		StringTokenizer st = new StringTokenizer(groupBaseString, ";", false);
		groupBase.clear();
		while (st.hasMoreTokens())
			groupBase.add(st.nextToken());
	}

	public PropertiesBean getConfig() {
		return config;
	}

	public void setConfig(PropertiesBean config) {
		this.config = config;
	}
}