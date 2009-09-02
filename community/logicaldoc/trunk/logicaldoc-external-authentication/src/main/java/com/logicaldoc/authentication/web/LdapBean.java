package com.logicaldoc.authentication.web;

import java.io.IOException;

import com.logicaldoc.authentication.ldap.LDAPContextSourceConfig;
import com.logicaldoc.authentication.ldap.LDAPUserGroupContext;
import com.logicaldoc.util.config.PropertiesBean;

/**
 * This bean allows the configuration of the LDAP connection parameters
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class LdapBean {
	private LDAPContextSourceConfig contextSourceConfig;

	private LDAPUserGroupContext userGroupContext;

	public LDAPContextSourceConfig getContextSourceConfig() {
		return contextSourceConfig;
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
			pbean.setProperty("ldap.url", contextSourceConfig.getUrl());
			pbean.setProperty("ldap.realm", contextSourceConfig.getRealm());
			pbean.setProperty("ldap.currentDN", contextSourceConfig.getCurrentDN());
			pbean.setProperty("ldap.authenticationPattern", contextSourceConfig.getUserAuthenticationPattern());
			pbean.setProperty("ldap.username", contextSourceConfig.getUserName());
			pbean.setProperty("ldap.password", contextSourceConfig.getPassword());
			pbean.setProperty("ldap.realm", contextSourceConfig.getRealm());
			pbean.setProperty("ldap.base", contextSourceConfig.getBase());

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

	public void setContextSourceConfig(LDAPContextSourceConfig contextSourceConfig) {
		this.contextSourceConfig = contextSourceConfig;
	}
}