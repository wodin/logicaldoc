package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.frontend.client.services.LdapService;

/**
 * Implementation of the LdapService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockLdapServiceImpl extends RemoteServiceServlet implements LdapService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUILdapSettings[] loadExtAuthSettings(String sid) {
		GUILdapSettings[] settings = new GUILdapSettings[2];

		GUILdapSettings ldapSettings = new GUILdapSettings();
		ldapSettings.setImplementation("basic");
		ldapSettings.setEnabled(true);
		ldapSettings.setUrl("ldap://localhost:10389");
		ldapSettings.setUsername("pluto");
		ldapSettings.setPwd("paperino");
		ldapSettings.setRealm("realm");
		ldapSettings.setDN("dn1");
		ldapSettings.setBase("base");
		ldapSettings.setUserIdentifierAttr("attr1");
		ldapSettings.setGrpIdentifierAttr("attr2");
		ldapSettings.setLogonAttr("logon");
		ldapSettings.setAuthPattern("pattern");
		ldapSettings.setUserClass("userclass");
		ldapSettings.setGrpClass("grpClass");
		ldapSettings.setUsersBaseNode("usersBaseNode");
		ldapSettings.setGrpsBaseNode("grpsBaseNode");
		ldapSettings.setLanguage("it");

		GUIADSettings adSettings = new GUIADSettings();
		adSettings.setImplementation("md5");
		adSettings.setEnabled(false);
		adSettings.setDomain("domain");
		adSettings.setHost("host");
		adSettings.setPort(9080);
		adSettings.setUsername("minnie");
		adSettings.setPwd("topolino");
		adSettings.setUsersBaseNode("usersBaseNode2");
		adSettings.setGrpsBaseNode("grpsBaseNode2");
		adSettings.setLanguage("fr");

		settings[0] = ldapSettings;
		settings[1] = adSettings;

		return settings;
	}

	@Override
	public void saveExtAuthSettings(String sid, GUILdapSettings ldapSettings, GUIADSettings adSettings) {

	}
}