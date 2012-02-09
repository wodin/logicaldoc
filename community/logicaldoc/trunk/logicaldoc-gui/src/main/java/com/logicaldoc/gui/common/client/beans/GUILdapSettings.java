package com.logicaldoc.gui.common.client.beans;

import java.io.Serializable;

/**
 * LDAP Settings bean as used in the GUI
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class GUILdapSettings implements Serializable {

	private static final long serialVersionUID = 1L;

	private String implementation = "";

	private boolean enabled = false;
	
	private boolean anon = false;

	private String url;

	private String username;

	private String pwd;

	private String realm;

	private String userIdentifierAttr;

	private String grpIdentifierAttr;

	private String logonAttr;

	private String userClass;

	private String grpClass;

	private String usersBaseNode;

	private String grpsBaseNode;

	private String language;

	private int pageSize = 100;

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getUserIdentifierAttr() {
		return userIdentifierAttr;
	}

	public void setUserIdentifierAttr(String userIdentifierAttr) {
		this.userIdentifierAttr = userIdentifierAttr;
	}

	public String getGrpIdentifierAttr() {
		return grpIdentifierAttr;
	}

	public void setGrpIdentifierAttr(String grpIdentifierAttr) {
		this.grpIdentifierAttr = grpIdentifierAttr;
	}

	public String getLogonAttr() {
		return logonAttr;
	}

	public void setLogonAttr(String logonAttr) {
		this.logonAttr = logonAttr;
	}

	public String getUserClass() {
		return userClass;
	}

	public void setUserClass(String userClass) {
		this.userClass = userClass;
	}

	public String getGrpClass() {
		return grpClass;
	}

	public void setGrpClass(String grpClass) {
		this.grpClass = grpClass;
	}

	public String getUsersBaseNode() {
		return usersBaseNode;
	}

	public void setUsersBaseNode(String usersBaseNode) {
		this.usersBaseNode = usersBaseNode;
	}

	public String getGrpsBaseNode() {
		return grpsBaseNode;
	}

	public void setGrpsBaseNode(String grpsBaseNode) {
		this.grpsBaseNode = grpsBaseNode;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isAnonymous() {
		return anon;
	}

	public void setAnonymous(boolean anon) {
		this.anon = anon;
	}
}
