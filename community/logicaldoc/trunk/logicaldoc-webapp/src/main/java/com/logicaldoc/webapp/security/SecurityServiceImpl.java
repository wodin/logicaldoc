package com.logicaldoc.webapp.security;

import java.io.File;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.webapp.AbstractService;

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

		GUIGroup grp = new GUIGroup();
		grp.setId(groupId);
		grp.setDescription(group.getDescription());
		grp.setName(group.getName());
		return grp;
	}

	@Override
	public GUIUser getUser(String sid, long userId) {
		validateSession(sid);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findById(userId);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GUISecuritySettings loadSettings(String sid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveExtAuthSettings(String sid, GUILdapSettings ldapSettings, GUIADSettings adSettings) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveSettings(String sid, GUISecuritySettings settings) {
		// TODO Auto-generated method stub

	}

	@Override
	public GUIRight[] getSecurityEntities(String sid) {
		// TODO Auto-generated method stub
		return null;
	}
}