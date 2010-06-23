package com.logicaldoc.gui.frontend.server;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.frontend.client.services.SecurityService;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockSecurityServiceImpl extends RemoteServiceServlet implements SecurityService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUISession login(String username, String password, String locale) {
		String loc = locale;
		if (StringUtils.isEmpty(loc))
			loc = "en";

		GUISession session = new GUISession();
		session.setBundle(getBundle(loc));

		session.setLoggedIn(false);
		if ("admin".equals(username)) {
			GUIUser user = new GUIUser();
			user.setLanguage(loc);
			session.setUser(user);
			user.setUserName(username);
			session.setSid("sid" + new Date().getTime());

			GUIGroup group = new GUIGroup();
			group.setId(1);
			group.setName("admin");
			group.setDescription("Administrators");
			user.setGroups(new GUIGroup[] { group });
			user.setFirstName("Marco");
			user.setName("Meschieri");
			user.setExpired(false);
			user.setPasswordMinLenght(8);
			user.setLockedDocs(5);
			user.setCheckedOutDocs(1);
			session.setLoggedIn(true);
			return session;
		} else if ("author".equals(username)) {
			GUIUser user = new GUIUser();
			user.setId(100);
			user.setExpired(true);
			user.setPasswordMinLenght(8);
			user.setLanguage(loc);
			session.setUser(user);
			session.setLoggedIn(false);
			return session;
		} else {
			session.setLoggedIn(false);
			return session;
		}
	}

	public GUIValuePair[] getBundle(String locale) {
		System.out.println("** locale=" + locale);

		// In production, use our LocaleUtil to instantiate the locale
		Locale l = new Locale(locale);
		ResourceBundle rb = ResourceBundle.getBundle("i18n.messages", l);
		GUIValuePair[] buf = new GUIValuePair[rb.keySet().size()];
		int i = 0;
		for (String key : rb.keySet()) {
			GUIValuePair entry = new GUIValuePair();
			entry.setCode(key);
			entry.setValue(rb.getString(key));
			buf[i++] = entry;
		}
		return buf;
	}

	@Override
	public void logout(String sid) {

	}

	@Override
	public int changePassword(long userId, String oldPassword, String newPassword) {
		return 0;
	}

	@Override
	public void deleteUser(String sid, long userId) {

	}

	@Override
	public GUIUser saveUser(String sid, GUIUser user) {
		if (user.getId() == 0)
			user.setId(9999);
		return user;
	}

	@Override
	public GUIUser getUser(String sid, long userId) {
		GUIUser user = new GUIUser();
		user.setId(userId);
		user.setUserName("admin");
		user.setName("Meschieri");
		user.setFirstName("Marco");
		user.setEmail("m.meschieri@logicalobjects.it");
		GUIGroup group = new GUIGroup();
		group.setId(1);
		group.setName("admin");
		group.setDescription("Administrators");
		user.setGroups(new GUIGroup[] { group });
		return user;
	}

	@Override
	public void deleteGroup(String sid, long groupId) {

	}

	@Override
	public GUIGroup getGroup(String sid, long groupId) {
		GUIGroup group = new GUIGroup();
		group.setId(groupId);
		group.setName("Group " + groupId);
		group.setDescription("Description " + groupId);
		return group;
	}

	@Override
	public GUIGroup saveGroup(String sid, GUIGroup group) {
		if (group.getId() == 0)
			group.setId(9999);
		return group;
	}

	@Override
	public void addUserToGroup(String sid, long groupId, long userId) {

	}

	@Override
	public void removeFromGroup(String sid, long groupId, long[] docIds) {

	}

	@Override
	public GUISecuritySettings loadSettings(String sid) {
		GUISecuritySettings settings = new GUISecuritySettings();
		settings.setPwdExpiration(60);
		settings.setPwdSize(8);
		GUIUser user = new GUIUser();
		user.setId(1);
		user.setUserName("admin");
		user.setName("Caruso");
		user.setFirstName("Matteo");
		settings.setNotifiedUsers(new GUIUser[] { user });
		return settings;
	}

	@Override
	public void saveSettings(String sid, GUISecuritySettings settings) {

	}

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

	@Override
	public void kill(String sid) {

	}
}