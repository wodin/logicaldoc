package com.logicaldoc.ldap.model;

/**
 * Value object that can be used as a Spring singleton to store initial
 * configuration issues
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class LDAPContextSourceConfig {
	private String realm;

	private String currentDN;

	private String userAuthenticationPattern;

	private String url;

	private String userGroupContext;

	private String userName;

	private String password;

	private String base;

	private String enabled;
	
	private String authentication;

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getCurrentDN() {
		return currentDN;
	}

	public void setCurrentDN(String currentDN) {
		this.currentDN = currentDN;
	}

	public String getUserAuthenticationPattern() {
		return userAuthenticationPattern;
	}

	public void setUserAuthenticationPattern(String userAuthenticationPattern) {
		this.userAuthenticationPattern = userAuthenticationPattern;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserGroupContext() {
		return userGroupContext;
	}

	public void setUserGroupContext(String userGroupContext) {
		this.userGroupContext = userGroupContext;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
}