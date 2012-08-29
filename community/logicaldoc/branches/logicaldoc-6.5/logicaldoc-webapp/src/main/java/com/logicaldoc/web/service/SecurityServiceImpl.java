package com.logicaldoc.web.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.core.SystemInfo;
import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailSender;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.core.communication.SystemMessageDAO;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.DownloadTicket;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DownloadTicketDAO;
import com.logicaldoc.core.rss.dao.FeedMessageDAO;
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
import com.logicaldoc.core.util.UserUtil;
import com.logicaldoc.gui.common.client.Constants;
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
import com.logicaldoc.web.SessionFilter;
import com.logicaldoc.web.util.SessionUtil;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityServiceImpl extends RemoteServiceServlet implements SecurityService {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(SecurityServiceImpl.class);

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
			String[] remoteAddress = new String[] { null, null };
			if (getThreadLocalRequest() != null) {
				remoteAddress = new String[] { getThreadLocalRequest().getRemoteAddr(),
						getThreadLocalRequest().getRemoteHost() };
			}

			if (authenticationChain.authenticate(username, password, remoteAddress)) {
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

//				guiUser.setLockedDocs(documentDao.findByLockUserAndStatus(user.getId(), AbstractDocument.DOC_LOCKED)
//						.size());
//				guiUser.setCheckedOutDocs(documentDao.findByLockUserAndStatus(user.getId(),
//						AbstractDocument.DOC_CHECKED_OUT).size());
//				guiUser.setUnreadMessages(messageDao.getCount(username, SystemMessage.TYPE_SYSTEM, 0));

				guiUser.setQuota(user.getQuota());
				guiUser.setQuotaCount(user.getQuotaCount());
				guiUser.setWelcomeScreen(user.getWelcomeScreen());

				if (StringUtils.isNotEmpty(user.getSignatureId()))
					guiUser.setSignatureId(user.getSignatureId());
				if (StringUtils.isNotEmpty(user.getSignatureInfo()))
					guiUser.setSignatureInfo(user.getSignatureInfo());

				session.setSid(AuthenticationChain.getSessionId());
				session.setUser(guiUser);
				session.setLoggedIn(true);

				MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
				List<Long> menues = mdao.findMenuIdByUserId(user.getId());
				guiUser.setMenues((Long[]) menues.toArray(new Long[0]));

				/*
				 * Prepare an incoming message, if any
				 */
				ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
				String incomingMessage = config.getProperty("gui.welcome");
				if (StringUtils.isNotEmpty(incomingMessage)) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("user", user.getFullName());
					incomingMessage = StrSubstitutor.replace(incomingMessage, map);
				}

				// In case of news overwrite the incoming message
				if (guiUser.isMemberOf(Constants.GROUP_ADMIN) && info.isEnabled("Feature_27")) {
					// Check if there are incoming messages not already read
					FeedMessageDAO feedMessageDao = (FeedMessageDAO) Context.getInstance()
							.getBean(FeedMessageDAO.class);
					if (feedMessageDao.checkNotRead())
						incomingMessage = I18N.message("productnewsmessage", locale);
				}

				if (StringUtils.isNotEmpty(incomingMessage))
					session.setIncomingMessage(incomingMessage);

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

			FileUtils.forceDelete(UserUtil.getUserResource(session.getUserId(), "temp"));
			log.info("User " + session.getUserName() + " logged out.");
			kill(sid);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public int changePassword(long userId, String oldPassword, String newPassword, boolean notify) {

		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User user = userDao.findById(userId);
			if (user == null)
				throw new Exception("User " + userId + " not found");
			userDao.initialize(user);

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

			boolean stored = userDao.store(user, history);

			if (!stored)
				throw new Exception("User not stored");

			if (notify)
				try {
					notifyAccount(user, newPassword, InfoServiceImpl.getInfo());
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
					return 2;
				}
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
		try {
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
				usr.setSignatureId(user.getSignatureId());
				usr.setSignatureInfo(user.getSignatureInfo());
				usr.setWelcomeScreen(user.getWelcomeScreen());
				usr.setIpWhitelist(user.getIpWhiteList());
				usr.setIpBlacklist(user.getIpBlackList());

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

				usr.setQuota(user.getQuota());
				usr.setQuotaCount(user.getQuotaCount());

				ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
				usr.setPasswordMinLenght(Integer.parseInt(config.getProperty("password.size")));

				return usr;
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
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
			if (group.getInheritGroupId() == null || group.getInheritGroupId().longValue() == 0) {
				groupDao.store(grp);
			} else {
				groupDao.insert(grp, group.getInheritGroupId().longValue());
			}
		} else {
			grp = new Group();

			grp.setName(group.getName());
			grp.setDescription(group.getDescription());
			groupDao.store(grp);

			if (group.getInheritGroupId() != null && group.getInheritGroupId().longValue() != 0)
				groupDao.inheritACLs(grp.getId(), group.getInheritGroupId().longValue());
		}

		group.setId(grp.getId());

		return group;
	}

	@Override
	public GUIUser saveUser(String sid, GUIUser user, GUIInfo info) throws InvalidSessionException {
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
			usr.setSignatureId(user.getSignatureId());
			usr.setSignatureInfo(user.getSignatureInfo());
			usr.setWelcomeScreen(user.getWelcomeScreen());
			usr.setIpWhiteList(user.getIpWhitelist());
			usr.setIpBlackList(user.getIpBlacklist());

			usr.setQuota(user.getQuota());

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
			groupDao.initialize(adminGroup);

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
					try {
						notifyAccount(usr, decodedPassword, info);
					} catch (Throwable e) {
						log.warn(e.getMessage(), e);
					}
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
	 * @throws Exception
	 */
	private void notifyAccount(User user, String password, GUIInfo info) throws Exception {
		EMail email;
		email = new EMail();
		Recipient recipient = new Recipient();
		recipient.setAddress(user.getEmail());
		recipient.setRead(1);
		email.addRecipient(recipient);
		email.setFolder("outbox");
		email.setUserName(user.getUserName());
		email.setSentDate(new Date());

		Locale locale = new Locale(user.getLanguage());
		email.setLocale(locale);

		/*
		 * Prepare the template
		 */
		Map<String, String> args = new HashMap<String, String>();
		HttpServletRequest request = this.getThreadLocalRequest();
		String address = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
		args.put("_url", address);
		args.put("_product", SystemInfo.get().getProduct());
		args.put(
				"_message",
				I18N.message("emailnotifyaccount", locale, new Object[] { user.getFirstName() + " " + user.getName(),
						"", user.getUserName(), password, address }));

		EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
		sender.send(email, "psw.rec1", args);
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
			usr.setSignatureId(user.getSignatureId());
			usr.setSignatureInfo(user.getSignatureInfo());
			usr.setWelcomeScreen(user.getWelcomeScreen());

			userDao.store(usr);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}

		return user;
	}

	@Override
	public void kill(String sid) {
		// Kill the LogicalDOC session
		SessionManager.getInstance().kill(sid);

		// Also kill the servlet container session, if any
		HttpSession httpSession = SessionFilter.getServletSession(sid);
		if (httpSession != null) {
			httpSession.invalidate();
		}
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
			if (StringUtils.isNotEmpty(pbean.getProperty("gui.savelogin")))
				securitySettings.setSaveLogin("true".equals(pbean.getProperty("gui.savelogin")));

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
			conf.setProperty("gui.savelogin", Boolean.toString(settings.isSaveLogin()));

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

	@Override
	public void resetPassword(String username, String emailAddress, String productName) throws Exception {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		User user = userDao.findByUserName(username);

		EMail email;
		try {
			if (user == null)
				throw new Exception("User " + username + " not found");
			else if (!user.getEmail().trim().equals(emailAddress.trim()))
				throw new Exception("User with email " + emailAddress + " not found");

			email = new EMail();
			Recipient recipient = new Recipient();
			recipient.setAddress(user.getEmail());
			recipient.setRead(1);
			email.addRecipient(recipient);
			email.setFolder("outbox");

			// Prepare a new download ticket
			String temp = new Date().toString() + user.getId();
			String ticketid = CryptUtil.cryptString(temp);
			DownloadTicket ticket = new DownloadTicket();
			ticket.setTicketId(ticketid);
			ticket.setDocId(0L);
			ticket.setUserId(user.getId());
			ticket.setType(DownloadTicket.PSW_RECOVERY);
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, +5);
			ticket.setExpired(cal.getTime());

			// Store the ticket
			DownloadTicketDAO ticketDao = (DownloadTicketDAO) Context.getInstance().getBean(DownloadTicketDAO.class);
			ticketDao.store(ticket);

			// Try to clean the DB from old tickets
			ticketDao.deleteOlder();

			Locale locale = new Locale(user.getLanguage());

			email.setLocale(locale);
			email.setSentDate(new Date());
			email.setSubject(productName + " " + I18N.message("passwordrequest", locale));
			email.setUserName(user.getUserName());

			HttpServletRequest request = this.getThreadLocalRequest();
			String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath();
			String address = urlPrefix + "/pswrecovery?ticketId=" + ticketid + "&userId=" + user.getId();

			/*
			 * Prepare the template
			 */
			Map<String, String> args = new HashMap<String, String>();
			args.put("_product", productName);
			args.put("_url", address);

			EMailSender sender = (EMailSender) Context.getInstance().getBean(EMailSender.class);
			sender.send(email, "psw.rec2", args);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}
}