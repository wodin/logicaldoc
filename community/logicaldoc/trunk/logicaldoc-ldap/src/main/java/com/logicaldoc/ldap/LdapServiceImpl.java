package com.logicaldoc.ldap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.frontend.client.services.LdapService;
import com.logicaldoc.ldap.model.BasicLDAPContextSource;
import com.logicaldoc.ldap.model.LDAPContextSourceConfig;
import com.logicaldoc.ldap.model.LDAPUserGroupContext;
import com.logicaldoc.ldap.model.UserAttributeMapper;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.util.SessionUtil;

public class LdapServiceImpl extends RemoteServiceServlet implements LdapService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(LdapServiceImpl.class);

	@Override
	public GUILdapSettings loadSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GUILdapSettings settings = new GUILdapSettings();

		try {
			LDAPContextSourceConfig config = (LDAPContextSourceConfig) Context.getInstance().getBean(
					"LDAPContextSourceConfig");

			LDAPUserGroupContext userGroupContext = (LDAPUserGroupContext) Context.getInstance().getBean(
					"LDAPUserGroupContext");

			UserAttributeMapper userAttributeMapper = (UserAttributeMapper) Context.getInstance().getBean(
					"LDAPBasicUserMapper");

			settings.setImplementation(config.getAuthentication());
			settings.setEnabled("true".equals(config.getEnabled()) ? true : false);
			settings.setUrl(config.getUrl());
			settings.setUsername(config.getUserName());
			settings.setPwd(config.getPassword());
			settings.setRealm(config.getRealm());
			settings.setDN(config.getCurrentDN());
			settings.setBase(config.getBase());
			settings.setUserIdentifierAttr(userGroupContext.getUserIdentiferAttribute());
			settings.setGrpIdentifierAttr(userGroupContext.getGroupIdentiferAttribute());
			settings.setLogonAttr(userGroupContext.getLogonAttribute());
			settings.setAuthPattern(config.getUserAuthenticationPattern());
			settings.setUserClass(userGroupContext.getUserClass());
			settings.setGrpClass(userGroupContext.getGroupClass());
			settings.setUsersBaseNode(userGroupContext.getUserBaseString());
			settings.setGrpsBaseNode(userGroupContext.getGroupBaseString());
			settings.setLanguage(userAttributeMapper.getDefaultLanguage());
			log.info("External Authentication settings data loaded successfully.");
		} catch (Exception e) {
			log.error("Exception loading External Authentication settings data: " + e.getMessage(), e);
		}

		return settings;
	}

	@Override
	public void saveSettings(String sid, GUILdapSettings ldapSettings) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		BasicLDAPContextSource ldapContextSource = (BasicLDAPContextSource) Context.getInstance().getBean(
				"LDAPContextSource");
		try {
			PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");

			// Save LDAP settings
			conf.setProperty("ldap.url", ldapSettings.getUrl());
			ldapContextSource.setUrl(ldapSettings.getUrl());
			conf.setProperty("ldap.realm", ldapSettings.getRealm());
			ldapContextSource.setRealm(ldapSettings.getRealm());
			conf.setProperty("ldap.currentDN", ldapSettings.getDN());
			ldapContextSource.setCurrentDN(ldapSettings.getDN());
			conf.setProperty("ldap.authenticationPattern", ldapSettings.getAuthPattern());
			ldapContextSource.setUserAuthenticationPattern(ldapSettings.getAuthPattern());
			conf.setProperty("ldap.username", ldapSettings.getUsername());
			ldapContextSource.setUserName(ldapSettings.getUsername());
			conf.setProperty("ldap.password", ldapSettings.getPwd());
			ldapContextSource.setPassword(ldapSettings.getPwd());
			conf.setProperty("ldap.base", ldapSettings.getBase());
			ldapContextSource.setBase(ldapSettings.getBase());
			conf.setProperty("ldap.enabled", ldapSettings.isEnabled() ? "true" : "false");
			conf.setProperty("ldap.authentication", ldapSettings.getImplementation());

			// Save LDAP user group settings
			conf.setProperty("ldap.userIdentiferAttribute", ldapSettings.getUserIdentifierAttr());
			conf.setProperty("ldap.logonAttribute", ldapSettings.getLogonAttr());
			conf.setProperty("ldap.userClass", ldapSettings.getUserClass());
			conf.setProperty("ldap.groupClass", ldapSettings.getGrpClass());
			conf.setProperty("ldap.groupIdentiferAttribute", ldapSettings.getGrpIdentifierAttr());
			conf.setProperty("ldap.userBase", ldapSettings.getUsersBaseNode());
			conf.setProperty("ldap.groupBase", ldapSettings.getGrpsBaseNode());

			// Save LDAP attribute mapper settings
			conf.setProperty("ldap.defaultLanguage", ldapSettings.getLanguage());

			conf.write();

			log.info("External Authentication data written successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception writing External Authentication data: " + e.getMessage(), e);
		}
	}
}