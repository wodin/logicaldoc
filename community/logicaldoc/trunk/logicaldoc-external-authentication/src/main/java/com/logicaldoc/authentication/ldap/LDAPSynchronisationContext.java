package com.logicaldoc.authentication.ldap;

import java.util.Map;

/**
 * 
 * @author Sebastian Wenzky
 *
 */
public class LDAPSynchronisationContext {

	private String synchronisationTime;
	
	private LDAPUserGroupContext ldapUserGroupContext;
	
	private Map<String, String> userGroupMapping;

	public String getSynchronisationTime() {
		return synchronisationTime;
	}

	public void setSynchronisationTime(String synchronisationTime) {
		this.synchronisationTime = synchronisationTime;
	}

	public LDAPUserGroupContext getLdapUserContext() {
		return ldapUserGroupContext;
	}

	public void setLdapUserGroupContext(LDAPUserGroupContext ldapUserGroupContext) {
		this.ldapUserGroupContext = ldapUserGroupContext;
	}

	public Map<String, String> getUserGroupMapping() {
		return userGroupMapping;
	}

	public void setUserGroupMapping(Map<String, String> userGroupMapping) {
		this.userGroupMapping = userGroupMapping;
	}
	
	
}
