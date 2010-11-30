package com.logicaldoc.web.service;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.dao.SystemMessageDAO;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMenu;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.CryptUtil;
import com.logicaldoc.util.security.PasswordGenerator;
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

		try {
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

				GUIInfo info = new InfoServiceImpl().getInfo(guiUser.getLanguage());
				session.setInfo(info);

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

				guiUser.setLockedDocs(documentDao.findByLockUserAndStatus(user.getId(), AbstractDocument.DOC_LOCKED)
						.size());
				guiUser.setCheckedOutDocs(documentDao.findByLockUserAndStatus(user.getId(),
						AbstractDocument.DOC_CHECKED_OUT).size());
				guiUser.setUnreadMessages(messageDao.getCount(username, SystemMessage.TYPE_SYSTEM, 0));

				session.setSid(AuthenticationChain.getSessionId());
				session.setUser(guiUser);
				session.setLoggedIn(true);

				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				List<Long> menues = mdao.findMenuIdByUserId(user.getId());
				guiUser.setMenues((Long[]) menues.toArray(new Long[0]));

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

			if (guiUser != null) {
				ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
				guiUser.setPasswordMinLenght(Integer.parseInt(config.getProperty("password.size")));
			}
			return session;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void logout(String sid) {
		try {
			UserSession session = SessionManager.getInstance().get(sid);
			if (session == null)
				return;

			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			FileUtils.deleteDirectory(new File(conf.getPropertyWithSubstitutions("conf.userdir") + "/"
					+ session.getUserName() + "/temp"));

			log.info("User " + session.getUserName() + " logged out.");

			SessionManager.getInstance().kill(sid);
		} catch (Throwable e) {
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
		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		manager.removeAllUsersFromGroup(groupDao.findById(groupId));
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
				grps[i].setType(group.getType());
				i++;
			}
			usr.setGroups(grps);

			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			usr.setPasswordMinLenght(Integer.parseInt(config.getProperty("password.size")));
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
		boolean createNew = false;
		String decodedPassword = "";

		try {
			User usr;
			if (user.getId() != 0) {
				usr = userDao.findById(user.getId());
				userDao.initialize(usr);
			} else {
				usr = new User();
				createNew = true;
			}

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

			if (createNew) {
				// Generate an initial password
				ContextProperties pbean = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
				decodedPassword = new PasswordGenerator().generate(pbean.getInt("password.size"));
				usr.setDecodedPassword(decodedPassword);
				usr.setPasswordChanged(new Date());
			}

			boolean stored = userDao.store(usr);
			if (!stored)
				throw new Exception("User not stored");
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
			stored = userDao.store(usr);
			if (!stored)
				throw new Exception("User not stored");
			else {
				// Notify the user by email
				if (createNew)
					notifyAccount(usr, decodedPassword);
			}

			return user;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Notify the user with it's new account
	 * 
	 * @param user The created user
	 * @param password The decoded password
	 */
	private void notifyAccount(User user, String password) {
		EMail email;
		try {
			email = new EMail();
			Recipient recipient = new Recipient();
			recipient.setAddress(user.getEmail());
			email.addRecipient(recipient);
			email.setFolder("outbox");

			Locale locale = new Locale(user.getLanguage());

			HttpServletRequest request = this.getThreadLocalRequest();
			String address = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath();
			String text = I18N.message("emailnotifyaccount", locale,
					new Object[] { user.getFirstName() + " " + user.getName(), user.getUserName(), password, address });
			email.setMessageText(text);
			email.setRead(1);
			email.setSentDate(new Date());
			email.setSubject(I18N.message("emailnotifyaccountobject", locale));
			email.setUserName(user.getUserName());

			EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
			sender.send(email);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public GUIUser saveProfile(String sid, GUIUser user) throws InvalidSessionException {
		SessionUtil.validateSession(sid);

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		try {
			User usr = userDao.findById(user.getId());
			userDao.initialize(usr);

			usr.setFirstName(user.getFirstName());
			usr.setName(user.getName());
			usr.setEmail(user.getEmail());
			usr.setLanguage(user.getLanguage());
			usr.setStreet(user.getAddress());
			usr.setPostalcode(user.getPostalCode());
			usr.setCity(user.getCity());
			usr.setCountry(user.getCountry());
			usr.setState(user.getState());
			usr.setTelephone(user.getPhone());
			usr.setTelephone2(user.getCell());

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

	private boolean saveRules(String sid, Menu menu, long userId, GUIRight[] rights) throws Exception {
		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);

		boolean sqlerrors = false;
		try {
			menu.setSecurityRef(null);
			menu.getMenuGroups().clear();
			mdao.store(menu);
			sqlerrors = false;
			Set<MenuGroup> grps = new HashSet<MenuGroup>();
			for (GUIRight right : rights) {
				MenuGroup fg = null;
				if (right.isRead()) {
					fg = new MenuGroup();
					fg.setGroupId(right.getEntityId());
				}
				grps.add(fg);
			}

			menu.setMenuGroups(grps);
			boolean stored = mdao.store(menu);
			if (!stored) {
				sqlerrors = true;
			}

			mdao.store(menu);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
		return !sqlerrors;
	}

	@Override
	public void applyRights(String sid, GUIMenu menu) throws InvalidSessionException {
		UserSession session = SessionUtil.validateSession(sid);

		try {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			saveRules(sid, mdao.findById(menu.getId()), session.getUserId(), menu.getRights());
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	@Override
	public GUIMenu getMenu(String sid, long menuId) throws InvalidSessionException {
		SessionUtil.validateSession(sid);
		try {
			MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			Menu menu = dao.findById(menuId);
			if (menu == null)
				return null;

			GUIMenu f = new GUIMenu();
			f.setId(menuId);

			int i = 0;
			GUIRight[] rights = new GUIRight[menu.getMenuGroups().size()];
			for (MenuGroup fg : menu.getMenuGroups()) {
				GUIRight right = new GUIRight();
				right.setEntityId(fg.getGroupId());
				rights[i] = right;
				i++;
			}
			f.setRights(rights);

			return f;
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}

		return null;
	}

}