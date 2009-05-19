package com.logicaldoc.authentication.web;

import java.io.IOException;

import com.logicaldoc.authentication.ldap.BasicLDAPContextSource;
import com.logicaldoc.authentication.ldap.LDAPUserGroupContext;
import com.logicaldoc.util.config.PropertiesBean;

/**
 * This bean allows the configuration of the LDAP connection parameters
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class LdapBean {
	private BasicLDAPContextSource contextSource;

	private LDAPUserGroupContext userGroupContext;

	public BasicLDAPContextSource getContextSource() {
		return contextSource;
	}

	public void setContextSource(BasicLDAPContextSource contextSource) {
		this.contextSource = contextSource;
	}

	public LDAPUserGroupContext getUserGroupContext() {
		return userGroupContext;
	}

	public void setUserGroupContext(LDAPUserGroupContext userGroupContext) {
		this.userGroupContext = userGroupContext;
	}

	public String save() {
		try {
			PropertiesBean pbean = new PropertiesBean();

			// Save source settings
			pbean.setProperty("ldap.url", contextSource.getUrl());
			pbean.setProperty("ldap.realm", contextSource.getRealm());
			pbean.setProperty("ldap.currentDN", contextSource.getCurrentDN());
			pbean.setProperty("ldap.authenticationPattern", contextSource.getUserAuthenticationPatern());
			pbean.setProperty("ldap.username", contextSource.getUserName());
			pbean.setProperty("ldap.password", contextSource.getPassword());
			pbean.setProperty("ldap.realm", contextSource.getRealm());

			// Save user group settings
			pbean.setProperty("ldap.userIdentiferAttribute", userGroupContext.getUserIdentiferAttribute());
			pbean.setProperty("ldap.logonAttribute", userGroupContext.getLogonAttribute());
			pbean.setProperty("ldap.userClass", userGroupContext.getUserClass());
			pbean.setProperty("ldap.groupClass", userGroupContext.getGroupClass());
			pbean.setProperty("ldap.groupIdentiferAttribute", userGroupContext.getGroupIdentiferAttribute());
			pbean.setProperty("ldap.userBase", userGroupContext.getUserBaseString());
			pbean.setProperty("ldap.groupBase", userGroupContext.getGroupBaseString());

			pbean.write();

			LdapMessages.addLocalizedInfo("msg.action.savesettings");
		} catch (IOException e) {
			LdapMessages.addLocalizedError("errors.action.savesettings");
		}

		return null;
	}
}