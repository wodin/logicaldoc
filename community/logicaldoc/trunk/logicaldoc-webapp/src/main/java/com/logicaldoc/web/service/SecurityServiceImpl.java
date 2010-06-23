package com.logicaldoc.web.service;

import java.io.File;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
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
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.PropertiesBean;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityServiceImpl extends RemoteServiceServlet implements SecurityService {

	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(SecurityServiceImpl.class);

	@Override
	public GUISession login(String username, String password, String locale) {
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
			if (StringUtils.isEmpty(locale)) {
				guiUser.setLanguage(user.getLanguage());
			} else {
				guiUser.setLanguage(locale);
			}
			session.setBundle(InfoServiceImpl.getBundle(guiUser.getLanguage()));

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
			userSession.getDictionary().put(SessionUtil.LOCALE, user.getLocale());
			userSession.getDictionary().put(SessionUtil.USER, user);
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
	}

	@Override
	public void logout(String sid) {
		try {
			UserSession session = SessionManager.getInstance().get(sid);
			if (session == null)
				return;
			SessionManager.getInstance().kill(sid);

			PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");
			FileUtils.deleteDirectory(new File(conf.getPropertyWithSubstitutions("conf.userdir") + "/"
					+ session.getUserName() + "/temp"));

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
	public void addUserToGroup(String sid, long groupId, long userId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		manager.assignUserToGroup(userDao.findById(userId), groupDao.findById(groupId));
	}

	@Override
	public void deleteGroup(String sid, long groupId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		groupDao.delete(groupId);
	}

	@Override
	public void deleteUser(String sid, long userId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		// Create the user history event
		UserHistory transaction = new UserHistory();
		transaction.setSessionId(sid);
		transaction.setEvent(UserHistory.EVENT_USER_DELETED);
		transaction.setComment("");
		transaction.setUser(userDao.findById(userId));

		userDao.delete(userId, transaction);
	}

	@Override
	public GUIGroup getGroup(String sid, long groupId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
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
	public GUIUser getUser(String sid, long userId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
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
			usr.setPasswordExpires(user.getPasswordExpires() == 1);

			GUIGroup[] grps = new GUIGroup[user.getGroups().size()];
			int i = 0;
			for (Group group : user.getGroups()) {
				grps[i] = new GUIGroup();
				grps[i].setId(group.getId());
				grps[i].setName(group.getName());
				grps[i].setDescription(group.getDescription());
				i++;
			}
			usr.setGroups(grps);

			return usr;
		}

		return null;
	}

	@Override
	public void removeFromGroup(String sid, long groupId, long[] userIds) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

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
	public GUIGroup saveGroup(String sid, GUIGroup group) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		Group grp;
		if (group.getId() != 0) {
			grp = groupDao.findById(group.getId());
			groupDao.initialize(grp);

			grp.setName(group.getName());
			grp.setDescription(group.getDescription());
			if (group.getInheritGroupId() == null || group.getInheritGroupId().longValue() <= 0) {
				groupDao.store(grp);
			} else {
				groupDao.insert(grp, group.getInheritGroupId().longValue());
			}
		} else {
			grp = new Group();

			grp.setName(group.getName());
			grp.setDescription(group.getDescription());
			groupDao.store(grp);

			if (group.getInheritGroupId() != null && group.getInheritGroupId().longValue() > 0)
				groupDao.inheritACLs(group.getId(), group.getInheritGroupId().longValue());
		}

		group.setId(grp.getId());

		return group;
	}

	@Override
	public GUIUser saveUser(String sid, GUIUser user) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		try {
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
			usr.setEnabled(user.isEnabled() ? 1 : 0);
			usr.setPasswordExpires(user.isPasswordExpires() ? 1 : 0);

			userDao.store(usr);
			user.setId(usr.getId());

			SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
			GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			manager.removeUserFromAllGroups(usr);
			long[] ids = new long[user.getGroups().length];
			for (int i = 0; i < user.getGroups().length; i++) {
				ids[i] = user.getGroups()[i].getId();
			}
			manager.assignUserToGroups(usr, ids);

			Group adminGroup = groupDao.findByName("admin");
			// The admin user must always member of admin group
			if ("admin".equals(user.getUserName()) && !user.isMemberOf("admin")) {
				manager.assignUserToGroup(usr, adminGroup);
			}
			userDao.store(usr);

		} catch (Throwable e) {
		}

		return user;
	}

	@Override
	public void kill(String sid) {
		SessionManager.getInstance().kill(sid);
	}

	@Override
	public GUILdapSettings[] loadExtAuthSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GUILdapSettings[] settings = new GUILdapSettings[2];

		try {

			LDAPContextSourceConfig config = (LDAPContextSourceConfig) Context.getInstance().getBean(
					LDAPContextSourceConfig.class);

			LDAPUserGroupContext userGroupContext = (LDAPUserGroupContext) Context.getInstance().getBean(
					LDAPUserGroupContext.class);

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

				System.out.println("Enabled feature 11!!!!");

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
				// TODO Remove after implementation is completed.
				System.out.println("NO Enabled feature 11!!!!");

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
				BasicLDAPContextSource.class);
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
			if (Feature.enabled(11)) {
				System.out.println("Enabled feature 11!!!!");
				// DO NOTHING
			} else {
				System.out.println("NO Enabled feature 11!!!!");
				// TODO Modify after completed features management.
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
			log.error("Exception writing External Authentication data: " + e.getMessage(), e);
		}
	}

	@Override
	public GUISecuritySettings loadSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GUISecuritySettings securitySettings = new GUISecuritySettings();
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			PropertiesBean pbean = (PropertiesBean) Context.getInstance().getBean("ContextProperties");

			securitySettings.setPwdExpiration(Integer.parseInt(pbean.getProperty("password.ttl")));
			securitySettings.setPwdSize(Integer.parseInt(pbean.getProperty("password.size")));
			StringTokenizer st = new StringTokenizer(pbean.getProperty("audit.user"), ",", false);
			while (st.hasMoreTokens()) {
				String username = st.nextToken();
				User user = userDao.findByUserName(username);
				if (user != null)
					securitySettings.addNotifiedUser(getUser(sid, user.getId()));
			}

			log.info("Security settings data loaded successfully.");
		} catch (Exception e) {
			log.error("Exception loading Security settings data: " + e.getMessage(), e);
		}

		return securitySettings;
	}

	@Override
	public void saveSettings(String sid, GUISecuritySettings settings) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		try {
			PropertiesBean conf = (PropertiesBean) Context.getInstance().getBean("ContextProperties");

			conf.setProperty("password.ttl", Integer.toString(settings.getPwdExpiration()));
			conf.setProperty("password.size", Integer.toString(settings.getPwdSize()));

			String users = "";
			for (GUIUser user : settings.getNotifiedUsers()) {
				users = users + user.getUserName() + ", ";
			}

			conf.setProperty("audit.user", users.trim());
			conf.write();

			log.info("Security settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Security settings data: " + e.getMessage(), e);
		}
	}
}