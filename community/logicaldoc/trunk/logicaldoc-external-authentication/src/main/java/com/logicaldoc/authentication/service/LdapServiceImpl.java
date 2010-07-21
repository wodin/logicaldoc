package com.logicaldoc.authentication.service;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.authentication.ldap.BasicLDAPContextSource;
import com.logicaldoc.authentication.ldap.LDAPContextSourceConfig;
import com.logicaldoc.authentication.ldap.LDAPUserGroupContext;
import com.logicaldoc.authentication.ldap.UserAttributeMapper;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.frontend.client.services.LdapService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.web.util.SessionUtil;

public class LdapServiceImpl extends RemoteServiceServlet implements LdapService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(LdapServiceImpl.class);

	@Override
	public GUILdapSettings[] loadExtAuthSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GUILdapSettings[] settings = new GUILdapSettings[2];

		try {
			LDAPContextSourceConfig config = (LDAPContextSourceConfig) Context.getInstance().getBean(
					"LDAPContextSourceConfig");

			LDAPUserGroupContext userGroupContext = (LDAPUserGroupContext) Context.getInstance().getBean(
					"LDAPUserGroupContext");

			UserAttributeMapper userAttributeMapper = (UserAttributeMapper) Context.getInstance().getBean(
					"LDAPBasicUserMapper");

			GUILdapSettings ldapSettings = new GUILdapSettings();
			ldapSettings.setImplementation(config.getAuthentication());
			ldapSettings.setEnabled("true".equals(config.getEnabled()) ? true : false);
			ldapSettings.setUrl(config.getUrl());
			ldapSettings.setUsername(config.getUserName());
			ldapSettings.setPwd(config.getPassword());
			ldapSettings.setRealm(config.getRealm());
			ldapSettings.setDN(config.getCurrentDN());
			ldapSettings.setBase(config.getBase());
			ldapSettings.setUserIdentifierAttr(userGroupContext.getUserIdentiferAttribute());
			ldapSettings.setGrpIdentifierAttr(userGroupContext.getGroupIdentiferAttribute());
			ldapSettings.setLogonAttr(userGroupContext.getLogonAttribute());
			ldapSettings.setAuthPattern(config.getUserAuthenticationPattern());
			ldapSettings.setUserClass(userGroupContext.getUserClass());
			ldapSettings.setGrpClass(userGroupContext.getGroupClass());
			ldapSettings.setUsersBaseNode(userGroupContext.getUserBaseString());
			ldapSettings.setGrpsBaseNode(userGroupContext.getGroupBaseString());
			ldapSettings.setLanguage(userAttributeMapper.getDefaultLanguage());

			settings[0] = ldapSettings;

			GUIADSettings adSettings = new GUIADSettings();
			// Checks if the active directory feature is enabled
			if (Feature.enabled(11)) {
				LDAPContextSourceConfig adConfig = (LDAPContextSourceConfig) Context.getInstance().getBean(
						"ADContextSourceConfig");

				LDAPUserGroupContext adUserGroupContext = (LDAPUserGroupContext) Context.getInstance().getBean(
						"ADUserGroupContext");

				UserAttributeMapper adUserAttributeMapper = (UserAttributeMapper) Context.getInstance().getBean(
						"ADBasicUserMapper");

				adSettings.setImplementation(adConfig.getAuthentication());
				adSettings.setEnabled("true".equals(adConfig.getEnabled()) ? true : false);
				adSettings.setDomain(adConfig.getRealm());
				String url = adConfig.getUrl();
				int lastIndex = url.lastIndexOf(':');
				adSettings.setHost(url.substring("ldap://".length(), lastIndex));
				adSettings.setPort(new Integer(url.substring(lastIndex + 1)));
				adSettings.setUsername(adConfig.getUserName());
				adSettings.setPwd(adConfig.getPassword());
				adSettings.setUsersBaseNode(adUserGroupContext.getUserBaseString());
				adSettings.setGrpsBaseNode(adUserGroupContext.getGroupBaseString());
				adSettings.setLanguage(adUserAttributeMapper.getDefaultLanguage());

				settings[1] = adSettings;
			} else {
				settings[1] = ldapSettings;
			}

			log.info("External Authentication settings data loaded successfully.");
		} catch (Exception e) {
			log.error("Exception loading External Authentication settings data: " + e.getMessage(), e);
		}

		return settings;
	}

	@Override
	public void saveExtAuthSettings(String sid, GUILdapSettings ldapSettings, GUIADSettings adSettings)
			throws InvalidSessionException {
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

			// Checks if the active directory feature is enabled
			if (!Feature.enabled(11)) {
				// DO NOTHING
			} else {
				BasicLDAPContextSource adContextSource = (BasicLDAPContextSource) Context.getInstance().getBean(
						"ADContextSourceConfig");

				// Save Active Directory settings
				conf.setProperty("ad.url", "ldap://" + adSettings.getHost().trim() + ":" + adSettings.getPort());
				adContextSource.setUrl("ldap://" + adSettings.getHost().trim() + ":" + adSettings.getPort());
				conf.setProperty("ad.realm", adSettings.getDomain());
				adContextSource.setRealm(adSettings.getDomain());
				String dn = "";
				StringTokenizer st = new StringTokenizer(adSettings.getDomain(), ".", false);
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (!"".equals(dn))
						dn += ",";
					dn += "DC=" + token;
				}
				conf.setProperty("ad.currentDN", dn);
				adContextSource.setCurrentDN(dn);
				conf.setProperty("ad.authenticationPattern", "{userName}@" + adSettings.getDomain());
				adContextSource.setUserAuthenticationPattern("{userName}@" + adSettings.getDomain());
				conf.setProperty("ad.username", adSettings.getUsername());
				adContextSource.setUserName(adSettings.getUsername());
				conf.setProperty("ad.password", adSettings.getPwd());
				adContextSource.setPassword(adSettings.getPwd());
				conf.setProperty("ad.base", adSettings.getBase());
				adContextSource.setBase(adSettings.getBase());
				conf.setProperty("ad.enabled", adSettings.isEnabled() ? "true" : "false");
				conf.setProperty("ad.authentication", adSettings.getImplementation());

				// Save Active Directory settings
				conf.setProperty("ad.userIdentiferAttribute", adSettings.getUserIdentifierAttr());
				conf.setProperty("ad.logonAttribute", adSettings.getLogonAttr());
				conf.setProperty("ad.userClass", adSettings.getUserClass());
				conf.setProperty("ad.groupClass", adSettings.getGrpClass());
				conf.setProperty("ad.groupIdentiferAttribute", adSettings.getGrpIdentifierAttr());
				conf.setProperty("ad.userBase", adSettings.getUsersBaseNode() + "," + dn);
				conf.setProperty("ad.groupBase", adSettings.getGrpsBaseNode() + "," + dn);

				// Save Active Directory attribute mapper settings
				conf.setProperty("ad.defaultLanguage", adSettings.getLanguage());
			}

			conf.write();

			log.info("External Authentication data written successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception writing External Authentication data: " + e.getMessage(), e);
		}
	}
}