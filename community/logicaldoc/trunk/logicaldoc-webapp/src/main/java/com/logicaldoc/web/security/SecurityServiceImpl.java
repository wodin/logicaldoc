package com.logicaldoc.web.security;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.authentication.ldap.BasicLDAPContextSource;
import com.logicaldoc.authentication.ldap.LDAPContextSourceConfig;
import com.logicaldoc.authentication.ldap.LDAPUserGroupContext;
import com.logicaldoc.authentication.ldap.UserAttributeMapper;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.web.AbstractService;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityServiceImpl extends AbstractService implements SecurityService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SecurityServiceImpl.class);

	@Override
	public GUISession login(String username, String password) {
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			AuthenticationChain authenticationChain = (AuthenticationChain) Context.getInstance().getBean(
					AuthenticationChain.class);
			
			GUISession session = new GUISession();
			GUIUser guiUser = new GUIUser();
			if (authenticationChain.authenticate(username, password,
					getThreadLocalRequest() != null ? getThreadLocalRequest().getRemoteAddr() : "")) {

				User user = userDao.findByUserName(username);
				userDao.initialize(user);

				guiUser.setFirstName(user.getFirstName());
				guiUser.setId(user.getId());
				guiUser.setLanguage(user.getLanguage());
				guiUser.setName(user.getName());

				GUIGroup[] groups = new GUIGroup[user.getGroups().size()];
				int i = 0;
				for (Group g : user.getGroups()) {
					groups[i] = new GUIGroup();
					groups[i].setId(g.getId());
					groups[i].setName(g.getName());
					groups[i].setDescription(g.getDescription());
					i++;
				}
				guiUser.setGroups(groups);

				guiUser.setUserName(username);
				guiUser.setExpired(false);
				session.setSid(AuthenticationChain.getSessionId());
				session.setUser(guiUser);
				session.setLoggedIn(true);
				
				// Define the current locale
				UserSession userSession = SessionManager.getInstance().get(session.getSid());
				userSession.getDictionary().put(LOCALE, user.getLocale());
			} else if (userDao.isPasswordExpired(username)) {
				User user = userDao.findByUserName(username);
				guiUser.setId(user.getId());
				guiUser.setExpired(true);
				guiUser.setLanguage(user.getLanguage());
				session.setUser(guiUser);
				session.setLoggedIn(false);
				log.info("User " + username + " password expired");
			} else {
				guiUser = null;
				session.setLoggedIn(false);
				log.warn("User " + username + " is not valid");
			}
			return session;
		} catch (Throwable e) {
			e.printStackTrace();
		}
return null;
	}

	@Override
	public void logout(String sid) {
		try {
			UserSession session = SessionManager.getInstance().get(sid);
			if (session == null)
				return;
			SessionManager.getInstance().kill(sid);

			SettingsConfig conf = (SettingsConfig) Context.getInstance().getBean(SettingsConfig.class);
			FileUtils.deleteDirectory(new File(conf.getValue("userdir") + "/" + session.getUserName() + "/temp"));

			log.info("User " + session.getUserName() + " logged out.");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public int changePassword(long userId, String oldPassword, String newPassword) {
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User user = userDao.findById(userId);
			if (user == null)
				throw new Exception("User " + userId + " not found");

			if (!CryptUtil.cryptString(oldPassword).equals(user.getPassword())) {
				return 1;
			}

			UserHistory history = null;
			// The password was changed
			user.setDecodedPassword(newPassword);
			user.setPasswordChanged(new Date());
			// Add a user history entry
			history = new UserHistory();
			history.setUser(user);
			history.setEvent(UserHistory.EVENT_USER_PASSWORDCHANGED);
			history.setComment("");
			user.setRepass("");

			UserDAO dao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

			boolean stored = dao.store(user, history);

			if (!stored)
				throw new Exception("User not stored");
			return 0;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return 1;
		}
	}

	@Override
	public void addUserToGroup(String sid, long groupId, long userId) {
		validateSession(sid);

		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		manager.assignUserToGroup(userDao.findById(userId), groupDao.findById(groupId));
	}

	@Override
	public void deleteGroup(String sid, long groupId) {
		validateSession(sid);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		groupDao.delete(groupId);
	}

	@Override
	public void deleteUser(String sid, long userId) {
		validateSession(sid);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		userDao.delete(userId);
	}

	@Override
	public GUIGroup getGroup(String sid, long groupId) {
		validateSession(sid);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Group group = groupDao.findById(groupId);

		if (group != null) {
			GUIGroup grp = new GUIGroup();
			grp.setId(groupId);
			grp.setDescription(group.getDescription());
			grp.setName(group.getName());
			return grp;
		}

		return null;
	}

	@Override
	public GUIUser getUser(String sid, long userId) {
		validateSession(sid);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findById(userId);
		if (user != null) {
			userDao.initialize(user);

			GUIUser usr = new GUIUser();
			usr.setId(userId);
			usr.setAddress(user.getStreet());
			usr.setCell(user.getTelephone2());
			usr.setPhone(user.getTelephone());
			usr.setCity(user.getCity());
			usr.setCountry(user.getCountry());
			usr.setEmail(user.getEmail());
			usr.setEnabled(user.getEnabled() == 1);
			usr.setFirstName(user.getFirstName());
			usr.setLanguage(user.getLanguage());
			usr.setName(user.getName());
			usr.setPostalCode(user.getPostalcode());
			usr.setState(user.getState());
			usr.setUserName(user.getUserName());

			GUIGroup[] grps = new GUIGroup[user.getGroups().size()];

			int i = 0;
			for (Group group : user.getGroups()) {
				grps[i] = new GUIGroup();
				grps[i].setId(group.getId());
				grps[i].setName(group.getName());
				grps[i].setDescription(group.getDescription());
				grps[i].setName(group.getName());
				i++;
			}

			return usr;
		}

		return null;
	}

	@Override
	public void removeFromGroup(String sid, long groupId, long[] userIds) {
		validateSession(sid);

		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Group group = groupDao.findById(groupId);
		for (long id : userIds) {
			User user = userDao.findById(id);
			manager.removeUserFromGroup(user, group);
		}
	}

	@Override
	public GUIGroup saveGroup(String sid, GUIGroup group) {
		validateSession(sid);

		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Group grp;
		if (group.getId() != 0) {
			grp = groupDao.findById(group.getId());
			groupDao.initialize(grp);
		} else
			grp = new Group();

		grp.setName(group.getName());
		grp.setDescription(group.getDescription());
		groupDao.store(grp);

		group.setId(grp.getId());
		return group;
	}

	@Override
	public GUIUser saveUser(String sid, GUIUser user) {
		validateSession(sid);

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User usr;
		if (user.getId() != 0) {
			usr = userDao.findById(user.getId());
			userDao.initialize(usr);
		} else
			usr = new User();

		usr.setCity(user.getCity());
		usr.setCountry(user.getCountry());
		usr.setEmail(user.getEmail());
		usr.setFirstName(user.getFirstName());
		usr.setName(user.getName());
		usr.setLanguage(user.getLanguage());
		usr.setPostalcode(user.getPostalCode());
		usr.setState(user.getState());
		usr.setStreet(user.getAddress());
		usr.setTelephone(user.getPhone());
		usr.setTelephone2(user.getCell());
		usr.setUserName(user.getUserName());

		userDao.store(usr);
		user.setId(usr.getId());

		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		manager.removeUserFromAllGroups(usr);
		for (GUIGroup grp : user.getGroups()) {
			Group g = groupDao.findById(grp.getId());
			manager.assignUserToGroup(usr, g);
		}

		return user;
	}

	@Override
	public void kill(String sid) {
		SessionManager.getInstance().kill(sid);
	}

	@Override
	public GUILdapSettings[] loadExtAuthSettings(String sid) {
		validateSession(sid);

		LDAPContextSourceConfig config = (LDAPContextSourceConfig) Context.getInstance().getBean(
				LDAPContextSourceConfig.class);

		LDAPUserGroupContext userGroupContext = (LDAPUserGroupContext) Context.getInstance().getBean(
				LDAPUserGroupContext.class);

		UserAttributeMapper userAttributeMapper = (UserAttributeMapper) Context.getInstance().getBean(
				UserAttributeMapper.class);

		GUILdapSettings[] settings = new GUILdapSettings[2];

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

		GUIADSettings adSettings = new GUIADSettings();
		adSettings.setImplementation(config.getAuthentication());
		adSettings.setEnabled("true".equals(config.getEnabled()) ? true : false);
		adSettings.setDomain(config.getRealm());
		String url = config.getUrl();
		int lastIndex = url.lastIndexOf(':');
		adSettings.setHost(url.substring("ldap://".length(), lastIndex));
		adSettings.setPort(new Integer(url.substring(lastIndex + 1)));
		adSettings.setUsername(config.getUserName());
		adSettings.setPwd(config.getPassword());
		adSettings.setUsersBaseNode(userGroupContext.getUserBaseString());
		adSettings.setGrpsBaseNode(userGroupContext.getGroupBaseString());
		adSettings.setLanguage(userAttributeMapper.getDefaultLanguage());

		settings[0] = ldapSettings;
		settings[1] = adSettings;

		return settings;
	}

	@Override
	public void saveExtAuthSettings(String sid, GUILdapSettings ldapSettings, GUIADSettings adSettings) {
		BasicLDAPContextSource ldapContextSource = (BasicLDAPContextSource) Context.getInstance().getBean(
				BasicLDAPContextSource.class);
		BasicLDAPContextSource adContextSource = (BasicLDAPContextSource) Context.getInstance().getBean(
				"ADContextSourceConfig");
		try {
			PropertiesBean pbean = new PropertiesBean();

			// Save LDAP settings
			pbean.setProperty("ldap.url", ldapSettings.getUrl());
			ldapContextSource.setUrl(ldapSettings.getUrl());
			pbean.setProperty("ldap.realm", ldapSettings.getRealm());
			ldapContextSource.setRealm(ldapSettings.getRealm());
			pbean.setProperty("ldap.currentDN", ldapSettings.getDN());
			ldapContextSource.setCurrentDN(ldapSettings.getDN());
			pbean.setProperty("ldap.authenticationPattern", ldapSettings.getAuthPattern());
			ldapContextSource.setUserAuthenticationPattern(ldapSettings.getAuthPattern());
			pbean.setProperty("ldap.username", ldapSettings.getUsername());
			ldapContextSource.setUserName(ldapSettings.getUsername());
			pbean.setProperty("ldap.password", ldapSettings.getPwd());
			ldapContextSource.setPassword(ldapSettings.getPwd());
			pbean.setProperty("ldap.base", ldapSettings.getBase());
			ldapContextSource.setBase(ldapSettings.getBase());
			pbean.setProperty("ldap.enabled", ldapSettings.isEnabled() ? "true" : "false");
			pbean.setProperty("ldap.authentication", ldapSettings.getImplementation());

			// Save LDAP user group settings
			pbean.setProperty("ldap.userIdentiferAttribute", ldapSettings.getUserIdentifierAttr());
			pbean.setProperty("ldap.logonAttribute", ldapSettings.getLogonAttr());
			pbean.setProperty("ldap.userClass", ldapSettings.getUserClass());
			pbean.setProperty("ldap.groupClass", ldapSettings.getGrpClass());
			pbean.setProperty("ldap.groupIdentiferAttribute", ldapSettings.getGrpIdentifierAttr());
			pbean.setProperty("ldap.userBase", ldapSettings.getUsersBaseNode());
			pbean.setProperty("ldap.groupBase", ldapSettings.getGrpsBaseNode());

			// Save LDAP attribute mapper settings
			pbean.setProperty("ldap.defaultLanguage", ldapSettings.getLanguage());

			// Save Active Directory settings
			pbean.setProperty("ad.url", "ldap://" + adSettings.getHost().trim() + ":" + adSettings.getPort());
			adContextSource.setUrl("ldap://" + adSettings.getHost().trim() + ":" + adSettings.getPort());
			pbean.setProperty("ad.realm", adSettings.getDomain());
			adContextSource.setRealm(adSettings.getDomain());
			String dn = "";
			StringTokenizer st = new StringTokenizer(adSettings.getDomain(), ".", false);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (!"".equals(dn))
					dn += ",";
				dn += "DC=" + token;
			}
			pbean.setProperty("ad.currentDN", dn);
			adContextSource.setCurrentDN(dn);
			pbean.setProperty("ad.authenticationPattern", "{userName}@" + adSettings.getDomain());
			adContextSource.setUserAuthenticationPattern("{userName}@" + adSettings.getDomain());
			pbean.setProperty("ad.username", adSettings.getUsername());
			adContextSource.setUserName(adSettings.getUsername());
			pbean.setProperty("ad.password", adSettings.getPwd());
			adContextSource.setPassword(adSettings.getPwd());
			pbean.setProperty("ad.base", adSettings.getBase());
			adContextSource.setBase(adSettings.getBase());
			pbean.setProperty("ad.enabled", adSettings.isEnabled() ? "true" : "false");
			pbean.setProperty("ad.authentication", adSettings.getImplementation());

			// Save Active Directory settings
			pbean.setProperty("ad.userIdentiferAttribute", adSettings.getUserIdentifierAttr());
			pbean.setProperty("ad.logonAttribute", adSettings.getLogonAttr());
			pbean.setProperty("ad.userClass", adSettings.getUserClass());
			pbean.setProperty("ad.groupClass", adSettings.getGrpClass());
			pbean.setProperty("ad.groupIdentiferAttribute", adSettings.getGrpIdentifierAttr());
			pbean.setProperty("ad.userBase", adSettings.getUsersBaseNode() + "," + dn);
			pbean.setProperty("ad.groupBase", adSettings.getGrpsBaseNode() + "," + dn);

			// Save Active Directory attribute mapper settings
			pbean.setProperty("ad.defaultLanguage", adSettings.getLanguage());

			pbean.write();

			log.info("External Authentication data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing External Authentication data: " + e.getMessage(), e);
		}
	}

	@Override
	public GUISecuritySettings loadSettings(String sid) {
		validateSession(sid);

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		GUISecuritySettings securitySettings = new GUISecuritySettings();
		try {
			PropertiesBean pbean = new PropertiesBean();

			securitySettings.setPwdExpiration(Integer.parseInt(pbean.getProperty("password.ttl")));
			securitySettings.setPwdSize(Integer.parseInt(pbean.getProperty("password.size")));
			StringTokenizer st = new StringTokenizer(pbean.getProperty("audit.user"), ",", false);
			while (st.hasMoreTokens()) {
				String username = st.nextToken();
				User user = userDao.findByUserName(username);
				securitySettings.addNotifiedUser(getUser(sid, user.getId()));
			}
		} catch (IOException e) {
		}

		return securitySettings;
	}

	@Override
	public void saveSettings(String sid, GUISecuritySettings settings) {
		validateSession(sid);

		try {
			PropertiesBean pbean = new PropertiesBean();

			pbean.setProperty("password.ttl", Integer.toString(settings.getPwdExpiration()));
			pbean.setProperty("password.size", Integer.toString(settings.getPwdSize()));

			String users = "";
			for (GUIUser user : settings.getNotifiedUsers()) {
				users = users + user.getUserName() + ", ";
			}

			pbean.setProperty("audit.user", users.trim());
			pbean.write();
			log.info("Security settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Security settings data: " + e.getMessage(), e);
		}
	}
}