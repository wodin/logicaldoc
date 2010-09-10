package com.logicaldoc.web.service;

import java.io.File;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
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
		DocumentDAO documentDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		SystemMessageDAO messageDao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);

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

			guiUser.setLockedDocs(documentDao.findByLockUserAndStatus(user.getId(), AbstractDocument.DOC_LOCKED).size());
			guiUser.setCheckedOutDocs(documentDao.findByLockUserAndStatus(user.getId(),
					AbstractDocument.DOC_CHECKED_OUT).size());
			guiUser.setUnreadMessages(messageDao.getCount(username, SystemMessage.TYPE_SYSTEM, 0));

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

			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
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

			if (oldPassword != null && !CryptUtil.cryptString(oldPassword).equals(user.getPassword())) {
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
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}

		return user;
	}

	@Override
	public GUIUser saveProfile(String sid, GUIUser user) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		try {
			User usr = userDao.findById(user.getId());
			userDao.initialize(usr);

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
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}

		return user;
	}

	@Override
	public void kill(String sid) {
		SessionManager.getInstance().kill(sid);
	}

	@Override
	public GUISecuritySettings loadSettings(String sid) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		GUISecuritySettings securitySettings = new GUISecuritySettings();
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			ContextProperties pbean = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

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
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

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