package com.logicaldoc.web.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

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
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.rss.dao.FeedMessageDAO;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.MenuGroup;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.authentication.AuthenticationChain;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.sequence.SequenceDAO;
import com.logicaldoc.core.util.UserUtil;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIDashlet;
import com.logicaldoc.gui.common.client.beans.GUIExternalCall;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMenu;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUITenant;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.i18n.I18N;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.crypt.CryptUtil;
import com.logicaldoc.util.security.PasswordGenerator;
import com.logicaldoc.web.SessionFilter;
import com.logicaldoc.web.util.ServiceUtil;

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
	public GUISession login(String username, String password, String locale, String tenant) {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

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
				User user = userDao.findByUserNameIgnoreCase(username);
				userDao.initialize(user);
				session = internalLogin(AuthenticationChain.getSessionId(), user, locale);
				guiUser = session.getUser();
			} else if (userDao.isPasswordExpired(username)) {
				User user = userDao.findByUserName(username);
				guiUser.setId(user.getId());
				guiUser.setPasswordExpired(true);
				guiUser.setLanguage(user.getLanguage());
				guiUser.setName(user.getName());
				guiUser.setFirstName(user.getFirstName());
				session.setUser(guiUser);
				GUIInfo info = new GUIInfo();
				info.setTenant(getTenant(user.getTenantId()));
				session.setInfo(info);
				session.setLoggedIn(false);
				log.info("User " + username + " password expired");
			} else {
				guiUser = null;
				GUIInfo info = new GUIInfo();
				try {
					if (tenant == null)
						info.setTenant(getTenant(Tenant.DEFAULT_NAME));
					else
						info.setTenant(getTenant(tenant));
				} catch (Throwable t) {
					log.warn(t.getMessage());
					GUITenant ten = new GUITenant();
					ten.setId(Tenant.DEFAULT_ID);
					ten.setName(Tenant.DEFAULT_NAME);
					info.setTenant(ten);
				}
				session.setInfo(info);
				session.setLoggedIn(false);
				log.warn("User " + username + " is not valid");
			}

			if (guiUser != null) {
				ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
				guiUser.setPasswordMinLenght(Integer.parseInt(config.getProperty(session.getInfo().getTenant()
						.getName()
						+ ".password.size")));
			}

			return session;
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static GUITenant getTenant(long tenantId) {
		TenantDAO dao = (TenantDAO) Context.getInstance().getBean(TenantDAO.class);
		Tenant tenant = dao.findById(tenantId);
		return fromTenant(tenant);
	}

	public static GUITenant fromTenant(Tenant tenant) {
		if (tenant == null)
			return null;
		GUITenant ten = new GUITenant();
		ten.setId(tenant.getId());
		ten.setTenantId(tenant.getTenantId());
		ten.setCity(tenant.getCity());
		ten.setCountry(tenant.getCountry());
		ten.setDisplayName(tenant.getDisplayName());
		ten.setEmail(tenant.getEmail());
		ten.setName(tenant.getName());
		ten.setPostalCode(tenant.getPostalCode());
		ten.setState(tenant.getState());
		ten.setStreet(tenant.getStreet());
		ten.setTelephone(tenant.getTelephone());
		ten.setMaxRepoDocs(tenant.getMaxRepoDocs());
		ten.setMaxRepoSize(tenant.getMaxRepoSize());
		ten.setMaxSessions(tenant.getMaxSessions());
		ten.setMaxUsers(tenant.getMaxUsers());
		ten.setEnabled(tenant.getEnabled() == 1);
		ten.setExpire(tenant.getExpire());

		return ten;
	}

	public static GUITenant getTenant(String tenantName) {
		TenantDAO dao = (TenantDAO) Context.getInstance().getBean(TenantDAO.class);
		Tenant tenant = dao.findByName(tenantName);
		return fromTenant(tenant);
	}

	/**
	 * Used internally by login procedures, instantiates a new GUISession by a
	 * given authenticated user.
	 */
	protected GUISession internalLogin(String sid, User user, String locale) {
		GUIUser guiUser = new GUIUser();
		GUISession session = new GUISession();
		session.setSid(sid);

		UserSession userSession = SessionManager.getInstance().get(sid);

		DocumentDAO documentDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
		SystemMessageDAO messageDao = (SystemMessageDAO) Context.getInstance().getBean(SystemMessageDAO.class);
		SequenceDAO seqDao = (SequenceDAO) Context.getInstance().getBean(SequenceDAO.class);

		guiUser.setFirstName(user.getFirstName());
		guiUser.setName(user.getName());
		guiUser.setId(user.getId());
		guiUser.setTenantId(user.getTenantId());
		if (StringUtils.isEmpty(locale)) {
			guiUser.setLanguage(user.getLanguage());
		} else {
			guiUser.setLanguage(locale);
		}

		GUIInfo info = new InfoServiceImpl().getInfo(guiUser.getLanguage(), userSession.getTenantName());
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

		guiUser.setUserName(user.getUserName());
		guiUser.setPasswordExpired(false);

		guiUser.setLockedDocs(documentDao.findByLockUserAndStatus(user.getId(), AbstractDocument.DOC_LOCKED).size());
		guiUser.setCheckedOutDocs(documentDao.findByLockUserAndStatus(user.getId(), AbstractDocument.DOC_CHECKED_OUT)
				.size());
		guiUser.setUnreadMessages(messageDao.getCount(user.getUserName(), SystemMessage.TYPE_SYSTEM, 0));

		guiUser.setQuota(user.getQuota());
		guiUser.setQuotaCount(seqDao.getCurrentValue("userquota", user.getId(), user.getTenantId()));
		guiUser.setWelcomeScreen(user.getWelcomeScreen());

		if (StringUtils.isNotEmpty(user.getCertSubject()))
			guiUser.setCertSubject(user.getCertSubject());
		if (StringUtils.isNotEmpty(user.getKeyDigest()))
			guiUser.setKeyDigest(user.getKeyDigest());

		session.setSid(sid);
		session.setUser(guiUser);
		session.setLoggedIn(true);

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		List<Long> menues = mdao.findMenuIdByUserId(user.getId());
		guiUser.setMenues((Long[]) menues.toArray(new Long[0]));

		loadDashlets(guiUser);

		/*
		 * Prepare an incoming message, if any
		 */
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String incomingMessage = config.getProperty(userSession.getTenantName() + ".gui.welcome");
		if (StringUtils.isNotEmpty(incomingMessage)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("user", user.getFullName());
			incomingMessage = StrSubstitutor.replace(incomingMessage, map);
		}

		// In case of news overwrite the incoming message
		if (guiUser.isMemberOf(Constants.GROUP_ADMIN) && info.isEnabled("Feature_27")) {
			// Check if there are incoming messages not already read
			FeedMessageDAO feedMessageDao = (FeedMessageDAO) Context.getInstance().getBean(FeedMessageDAO.class);
			if (feedMessageDao.checkNotRead())
				incomingMessage = I18N.message("productnewsmessage", locale);
		}

		if (StringUtils.isNotEmpty(incomingMessage))
			session.setIncomingMessage(incomingMessage);

		// Define the current locale
		userSession.getDictionary().put(ServiceUtil.LOCALE, user.getLocale());
		userSession.getDictionary().put(ServiceUtil.USER, user);

		guiUser.setPasswordMinLenght(Integer.parseInt(config.getProperty(userSession.getTenantName() + ".password.size")));

		/*
		 * Prepare the external command
		 */
		String tenant = userSession.getTenantName();
		if (info.isEnabled("Feature_31") && "true".equals(config.getProperty(tenant + ".extcall.enabled"))) {
			GUIExternalCall externalCall = new GUIExternalCall();
			externalCall.setName(config.getProperty(tenant + ".extcall.name"));
			externalCall.setBaseUrl(config.getProperty(tenant + ".extcall.baseurl"));
			externalCall.setSuffix(config.getProperty(tenant + ".extcall.suffix"));
			externalCall.setTargetWindow(config.getProperty(tenant + ".extcall.window"));
			externalCall.setParametersStr(config.getProperty(tenant + ".extcall.params"));
			session.setExternalCall(externalCall);
		}

		return session;
	}

	@Override
	public GUISession login(String sid) {
		try {
			UserSession userSession = ServiceUtil.validateSession(sid);
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			User user = userDao.findById(userSession.getUserId());
			userDao.initialize(user);
			GUISession session = internalLogin(sid, user, null);
			return session;
		} catch (Throwable e) {
			log.debug(e.getMessage());
			return null;
		}
	}

	@Override
	public void logout(String sid) {
		try {
			UserSession session = SessionManager.getInstance().get(sid);
			if (session == null)
				return;

			FileUtils.forceDelete(UserUtil.getUserResource(session.getUserId(), "temp"));
			log.info("User " + session.getUserName() + " logged out and closed session " + sid);
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
			user.setPasswordExpired(0);

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
					notifyAccount(user, newPassword, InfoServiceImpl.getInfo(null));
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
	public void addUserToGroup(String sid, long groupId, long userId) throws ServerException {
		ServiceUtil.validateSession(sid);

		SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
		manager.assignUserToGroup(userDao.findById(userId), groupDao.findById(groupId));
	}

	@Override
	public void deleteGroup(String sid, long groupId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			GroupDAO groupDao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			SecurityManager manager = (SecurityManager) Context.getInstance().getBean(SecurityManager.class);
			manager.removeAllUsersFromGroup(groupDao.findById(groupId));
			groupDao.delete(groupId);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public void deleteUser(String sid, long userId) throws ServerException {
		ServiceUtil.validateSession(sid);
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
	public GUIGroup getGroup(String sid, long groupId) throws ServerException {
		ServiceUtil.validateSession(sid);
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
	public GUIUser getUser(String sid, long userId) throws ServerException {
		ServiceUtil.validateSession(sid);
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			TenantDAO tenantDao = (TenantDAO) Context.getInstance().getBean(TenantDAO.class);
			SequenceDAO seqDao = (SequenceDAO) Context.getInstance().getBean(SequenceDAO.class);

			User user = userDao.findById(userId);
			if (user != null) {
				userDao.initialize(user);

				GUIUser usr = new GUIUser();
				usr.setId(userId);
				usr.setTenantId(user.getTenantId());
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
				usr.setPasswordExpired(user.getPasswordExpired() == 1);
				usr.setCertSubject(user.getCertSubject());
				usr.setKeyDigest(user.getKeyDigest());
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

				usr.setQuotaCount(seqDao.getCurrentValue("userquota", user.getId(), user.getTenantId()));

				Tenant tenant = tenantDao.findById(user.getTenantId());

				ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
				usr.setPasswordMinLenght(Integer.parseInt(config.getProperty(tenant.getName() + ".password.size")));

				loadDashlets(usr);

				return usr;
			}
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
		return null;
	}

	/**
	 * Retrieves the dashlets configuration
	 */
	protected void loadDashlets(GUIUser usr) {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		List<GUIDashlet> dashlets = new ArrayList<GUIDashlet>();
		Map<String, Generic> map = userDao.findUserSettings(usr.getId(), "dashlet");
		for (Generic generic : map.values()) {
			dashlets.add(new GUIDashlet(generic.getInteger1().intValue(), generic.getInteger2().intValue(), generic
					.getInteger3().intValue(), generic.getString1() != null ? Integer.parseInt(generic.getString1())
					: 0));
		}

		usr.setDashlets(dashlets.toArray(new GUIDashlet[0]));
	}

	@Override
	public void removeFromGroup(String sid, long groupId, long[] userIds) throws ServerException {
		ServiceUtil.validateSession(sid);

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
	public GUIGroup saveGroup(String sid, GUIGroup group) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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
			grp.setTenantId(session.getTenantId());
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
	public GUIUser saveUser(String sid, GUIUser user, GUIInfo info) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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

			usr.setTenantId(session.getTenantId());
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
			usr.setWelcomeScreen(user.getWelcomeScreen());
			usr.setIpWhiteList(user.getIpWhitelist());
			usr.setIpBlackList(user.getIpBlacklist());

			usr.setQuota(user.getQuota());

			if (createNew) {
				User existingUser = userDao.findByUserName(user.getUserName());
				if (existingUser != null) {
					log.warn("Tried to create duplicate username " + user.getUserName());
					user.setWelcomeScreen(-99);
					return user;
				}

				// Generate an initial password
				ContextProperties pbean = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
				int minsize = 8;
				try {
					minsize = pbean.getInt(session.getTenantName() + ".password.size");
				} catch (Throwable t) {

				}
				decodedPassword = new PasswordGenerator().generate(minsize);
				usr.setDecodedPassword(decodedPassword);

				if (user.isPasswordExpired()) {
					/*
					 * We want the user to change his password at the first
					 * login
					 */
					usr.setPasswordChanged(new Date(10000));
					usr.setPasswordExpired(1);
				} else
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

			Group adminGroup = groupDao.findByName("admin", session.getTenantId());
			groupDao.initialize(adminGroup);

			// The admin user must be always member of admin group
			if ("admin".equals(user.getUserName()) && !user.isMemberOf("admin")) {
				manager.assignUserToGroup(usr, adminGroup);
			}

			// Notify the user by email
			if (createNew && user.isNotifyCredentials())
				try {
					notifyAccount(usr, decodedPassword, info);
				} catch (Throwable e) {
					log.warn(e.getMessage(), e);
				}

			return user;
		} catch (Throwable t) {
			return (GUIUser) ServiceUtil.throwServerException(session, log, t);
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
		email.setHtml(1);
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
		Map<String, Object> dictionary = new HashMap<String, Object>();
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		String address = config.getProperty("server.url");
		dictionary.put("url",address);
		dictionary.put("user", user);
		dictionary.put("password", password);

		EMailSender sender = new EMailSender(user.getTenantId());
		sender.send(email, "psw.rec1", dictionary);
	}

	@Override
	public GUIUser saveProfile(String sid, GUIUser user) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

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
			usr.setWelcomeScreen(user.getWelcomeScreen());

			userDao.store(usr);
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
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
	public GUISecuritySettings loadSettings(String sid) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		GUISecuritySettings securitySettings = new GUISecuritySettings();
		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);
			ContextProperties pbean = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			securitySettings.setPwdExpiration(Integer.parseInt(pbean.getProperty("password.ttl")));
			securitySettings
					.setPwdSize(Integer.parseInt(pbean.getProperty(session.getTenantName() + ".password.size")));
			if (StringUtils.isNotEmpty(pbean.getProperty(session.getTenantName() + ".gui.savelogin")))
				securitySettings.setSaveLogin("true".equals(pbean.getProperty(session.getTenantName()
						+ ".gui.savelogin")));
			securitySettings.setIgnoreLoginCase("true".equals(pbean.getProperty("login.ignorecase")));
			if (StringUtils.isNotEmpty(pbean.getProperty(session.getTenantName() + ".anonymous.enabled")))
				securitySettings.setEnableAnonymousLogin("true".equals(pbean.getProperty(session.getTenantName()
						+ ".anonymous.enabled")));
			if (StringUtils.isNotEmpty(pbean.getProperty(session.getTenantName() + ".anonymous.user"))) {
				User user = userDao.findByUserName(pbean.getProperty(session.getTenantName() + ".anonymous.user"));
				if (user != null)
					securitySettings.setAnonymousUser(getUser(sid, user.getId()));
			}

			log.info("Security settings data loaded successfully.");
		} catch (Exception e) {
			log.error("Exception loading Security settings data: " + e.getMessage(), e);
		}

		return securitySettings;
	}

	@Override
	public void saveSettings(String sid, GUISecuritySettings settings) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			ContextProperties conf = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

			if (session.getTenantId() == Tenant.DEFAULT_ID) {
				conf.setProperty("password.ttl", Integer.toString(settings.getPwdExpiration()));
				conf.setProperty("login.ignorecase", Boolean.toString(settings.isIgnoreLoginCase()));
			}

			conf.setProperty(session.getTenantName() + ".password.size", Integer.toString(settings.getPwdSize()));
			conf.setProperty(session.getTenantName() + ".gui.savelogin", Boolean.toString(settings.isSaveLogin()));
			conf.setProperty(session.getTenantName() + ".anonymous.enabled",
					Boolean.toString(settings.isEnableAnonymousLogin()));

			if (settings.getAnonymousUser() != null)
				conf.setProperty(session.getTenantName() + ".anonymous.user", settings.getAnonymousUser().getUserName());

			conf.write();

			log.info("Security settings data written successfully.");
		} catch (Exception e) {
			log.error("Exception writing Security settings data: " + e.getMessage(), e);
		}
	}

	private boolean saveRules(String sid, Menu menu, long userId, GUIRight[] rights) throws Exception {
		UserSession session = ServiceUtil.validateSession(sid);

		MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
		GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);

		boolean sqlerrors = false;
		try {
			mdao.initialize(menu);
			menu.setSecurityRef(null);

			// Remove all current tenant rights
			Set<MenuGroup> grps = new HashSet<MenuGroup>();
			for (MenuGroup mg : menu.getMenuGroups()) {
				Group group = gdao.findById(mg.getGroupId());
				if (group != null && group.getTenantId() != session.getTenantId())
					grps.add(mg);
			}
			menu.getMenuGroups().clear();

			sqlerrors = false;
			for (GUIRight right : rights) {
				Group group = gdao.findById(right.getEntityId());
				if (group == null || group.getTenantId() != session.getTenantId())
					continue;

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
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
		return !sqlerrors;
	}

	@Override
	public void applyRights(String sid, GUIMenu menu) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);

		try {
			MenuDAO mdao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			saveRules(sid, mdao.findById(menu.getId()), session.getUserId(), menu.getRights());
		} catch (Throwable t) {
			ServiceUtil.throwServerException(session, log, t);
		}
	}

	@Override
	public GUIMenu getMenu(String sid, long menuId) throws ServerException {
		UserSession session = ServiceUtil.validateSession(sid);
		try {
			GroupDAO gdao = (GroupDAO) Context.getInstance().getBean(GroupDAO.class);
			MenuDAO dao = (MenuDAO) Context.getInstance().getBean(MenuDAO.class);
			Menu menu = dao.findById(menuId);
			if (menu == null)
				return null;

			GUIMenu f = new GUIMenu();
			f.setId(menuId);

			int i = 0;
			GUIRight[] rights = new GUIRight[menu.getMenuGroups().size()];
			for (MenuGroup fg : menu.getMenuGroups()) {
				Group group = gdao.findById(fg.getGroupId());
				if (group == null || group.getTenantId() != session.getTenantId())
					continue;

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
			email.setHtml(1);
			email.setTenantId(user.getTenantId());
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
			ticket.setTenantId(user.getTenantId());
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
			email.setUserName(user.getUserName());
			
			HttpServletRequest request = this.getThreadLocalRequest();
			String urlPrefix = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath();
			String address = urlPrefix + "/pswrecovery?ticketId=" + ticketid + "&userId=" + user.getId();

			/*
			 * Prepare the template
			 */
			Map<String, Object> dictionary = new HashMap<String, Object>();
			dictionary.put("product", productName);
			dictionary.put("url", address);
			dictionary.put("user", user);

			EMailSender sender = new EMailSender(user.getTenantId());
			sender.send(email, "psw.rec2", dictionary);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public GUIUser[] searchUsers(String sid, String username, String groupId) throws ServerException {
		ServiceUtil.validateSession(sid);

		try {
			UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

			StringBuffer query = new StringBuffer(
					"select A.ld_id, A.ld_username, A.ld_name, A.ld_firstname from ld_user A ");
			if (StringUtils.isNotEmpty(groupId))
				query.append(", ld_usergroup B");
			query.append(" where A.ld_deleted=0 and A.ld_type=" + User.TYPE_DEFAULT);
			if (StringUtils.isNotEmpty(username))
				query.append(" and A.ld_username like '%" + username + "%'");
			if (StringUtils.isNotEmpty(groupId))
				query.append(" and A.ld_id=B.ld_userid and B.ld_groupid=" + groupId);

			@SuppressWarnings("unchecked")
			List<GUIUser> users = (List<GUIUser>) userDao.query(query.toString(), null, new RowMapper<GUIUser>() {

				@Override
				public GUIUser mapRow(ResultSet rs, int row) throws SQLException {
					GUIUser user = new GUIUser();
					user.setId(rs.getLong(1));
					user.setUserName(rs.getString(2));
					user.setName(rs.getString(3));
					user.setFirstName(rs.getString(4));
					return user;
				}
			}, null);

			return users.toArray(new GUIUser[0]);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			return new GUIUser[0];
		}
	}
}