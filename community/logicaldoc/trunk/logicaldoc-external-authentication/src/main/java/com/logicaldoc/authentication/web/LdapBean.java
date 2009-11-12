package com.logicaldoc.authentication.web;

import java.io.IOException;

import com.logicaldoc.authentication.ldap.BasicLDAPContextSource;
import com.logicaldoc.authentication.ldap.LDAPContextSourceConfig;
import com.logicaldoc.authentication.ldap.LDAPUserGroupContext;
import com.logicaldoc.authentication.ldap.UserAttributeMapper;
import com.logicaldoc.util.config.PropertiesBean;

/**
 * This bean allows the configuration of the LDAP connection parameters
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class LdapBean {
	private String propertyPrefix = "ldap";

	private LDAPContextSourceConfig contextSourceConfig;

	private LDAPUserGroupContext userGroupContext;

	private BasicLDAPContextSource contextSource;

	private UserAttributeMapper userAttributeMapper;
	
	protected String password;

	public boolean isPasswordEmpty() {
		return password == null || "".equals(password);
	}

	public LDAPContextSourceConfig getContextSourceConfig() {
		return contextSourceConfig;
	}

	public LDAPUserGroupContext getUserGroupContext() {
		return userGroupContext;
	}

	public void setUserGroupContext(LDAPUserGroupContext userGroupContext) {
		this.userGroupContext = userGroupContext;
	}

	public String getPassword() {
		if (password == null) {
			password = getContextSourceConfig().getPassword();
		}
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String changePassword() {
		password = null;
		getContextSourceConfig().setPassword(null);
		return null;
	}

	public String save() {
		try {
			PropertiesBean pbean = new PropertiesBean();

			// Save source settings
			pbean.setProperty(propertyPrefix + ".url", contextSourceConfig.getUrl());
			contextSource.setUrl(contextSourceConfig.getUrl());
			pbean.setProperty(propertyPrefix + ".realm", contextSourceConfig.getRealm());
			contextSource.setRealm(contextSourceConfig.getRealm());
			pbean.setProperty(propertyPrefix + ".currentDN", contextSourceConfig.getCurrentDN());
			contextSource.setCurrentDN(contextSourceConfig.getCurrentDN());
			pbean.setProperty(propertyPrefix + ".authenticationPattern", contextSourceConfig
					.getUserAuthenticationPattern());
			contextSource.setUserAuthenticationPattern(contextSourceConfig.getUserAuthenticationPattern());
			pbean.setProperty(propertyPrefix + ".username", contextSourceConfig.getUserName());
			contextSource.setUserName(contextSourceConfig.getUserName());
			if (contextSourceConfig.getPassword() == null)
				contextSourceConfig.setPassword("");
			else
				contextSourceConfig.setPassword(password);
			pbean.setProperty(propertyPrefix + ".password", contextSourceConfig.getPassword());
			contextSource.setPassword(contextSourceConfig.getPassword());
			pbean.setProperty(propertyPrefix + ".base", contextSourceConfig.getBase());
			contextSource.setBase(contextSourceConfig.getBase());
			pbean.setProperty(propertyPrefix + ".enabled", contextSourceConfig.getEnabled());
			pbean.setProperty(propertyPrefix + ".authentication", contextSourceConfig.getAuthentication());

			// Save user group settings
			pbean.setProperty(propertyPrefix + ".userIdentiferAttribute", userGroupContext.getUserIdentiferAttribute());
			pbean.setProperty(propertyPrefix + ".logonAttribute", userGroupContext.getLogonAttribute());
			pbean.setProperty(propertyPrefix + ".userClass", userGroupContext.getUserClass());
			pbean.setProperty(propertyPrefix + ".groupClass", userGroupContext.getGroupClass());
			pbean.setProperty(propertyPrefix + ".groupIdentiferAttribute", userGroupContext
					.getGroupIdentiferAttribute());
			pbean.setProperty(propertyPrefix + ".userBase", userGroupContext.getUserBaseString());
			pbean.setProperty(propertyPrefix + ".groupBase", userGroupContext.getGroupBaseString());

			// Save attribute mapper settings
			pbean.setProperty(propertyPrefix + ".defaultLanguage", userAttributeMapper.getDefaultLanguage());
			
			pbean.write();

			LdapMessages.addLocalizedInfo("msg.action.savesettings");
		} catch (IOException e) {
			LdapMessages.addLocalizedError("errors.action.savesettings");
		}

		return null;
	}

	public void setContextSourceConfig(LDAPContextSourceConfig contextSourceConfig) {
		this.contextSourceConfig = contextSourceConfig;
		this.password=contextSourceConfig.getPassword();
	}

	public String getPropertyPrefix() {
		return propertyPrefix;
	}

	public void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
	}

	public void setContextSource(BasicLDAPContextSource contextSource) {
		this.contextSource = contextSource;
	}

	public UserAttributeMapper getUserAttributeMapper() {
		return userAttributeMapper;
	}

	public void setUserAttributeMapper(UserAttributeMapper userAttributeMapper) {
		this.userAttributeMapper = userAttributeMapper;
	}
}