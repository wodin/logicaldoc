package com.logicaldoc.core.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.security.dao.UserHistoryDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * A single user session with it's unique identifier and the reference to the
 * user
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6.0
 */
public class UserSession implements Comparable<UserSession> {
	public final static int STATUS_OPEN = 0;

	public final static int STATUS_EXPIRED = 1;

	public final static int STATUS_CLOSED = 2;

	private Date creation = new Date();

	private Date lastRenew = creation;

	private String id;

	private String userName;

	private long userId;

	private long tenantId;

	private String tenantName;

	// The password given by the user at login time
	private String password;

	private int status = STATUS_OPEN;

	private Object externalSession = null;

	private Object userObject = null;

	private Map<String, Object> dictionary = new HashMap<String, Object>();

	public final static String ERROR = "ERROR";

	public final static String WARN = "WARN";

	public final static String INFO = "INFO";

	private List<Log> logs = new ArrayList<Log>();

	public Map<String, Object> getDictionary() {
		return dictionary;
	}

	/**
	 * Map that collects session-bound values
	 */
	public void setDictionary(Map<String, Object> dictionary) {
		this.dictionary = dictionary;
	}

	public String getId() {
		return id;
	}

	public Date getCreation() {
		return creation;
	}

	public Date getLastRenew() {
		return lastRenew;
	}

	public void renew() {
		if (status == STATUS_OPEN)
			lastRenew = new Date();
	}

	public int getStatus() {
		return status;
	}

	public void setExpired() {
		this.status = STATUS_EXPIRED;
		externalSession = null;
		// Add a user history entry
		UserDAO userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		UserHistoryDAO userHistoryDAO = (UserHistoryDAO) Context.getInstance().getBean(UserHistoryDAO.class);
		userHistoryDAO.createUserHistory(userDAO.findById(userId), UserHistory.EVENT_USER_TIMEOUT, "", id);
	}

	public void setClosed() {
		this.status = STATUS_CLOSED;
		externalSession = null;
		// Add a user history entry
		UserDAO userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		UserHistoryDAO userHistoryDAO = (UserHistoryDAO) Context.getInstance().getBean(UserHistoryDAO.class);
		userHistoryDAO.createUserHistory(userDAO.findById(userId), UserHistory.EVENT_USER_LOGOUT, "", id);
	}

	UserSession(String userName, String password, Object userObject) {
		super();
		this.id = UUID.randomUUID().toString();
		this.userName = userName;
		this.password = password;
		this.userObject = userObject;

		// Set the user's id
		UserDAO userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		UserHistoryDAO userHistoryDAO = (UserHistoryDAO) Context.getInstance().getBean(UserHistoryDAO.class);
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);

		User user = null;
		if ("true".equals(config.getProperty("login.ignorecase")))
			user = userDAO.findByUserNameIgnoreCase(userName);
		else
			user = userDAO.findByUserName(userName);

		this.userId = user.getId();
		this.userName = user.getUserName();

		// Set the tenant's id and name
		this.tenantId = user.getTenantId();
		TenantDAO tenantDAO = (TenantDAO) Context.getInstance().getBean(TenantDAO.class);
		Tenant tenant = tenantDAO.findById(this.tenantId);
		if (tenant != null)
			tenantName = tenant.getName();

		/*
		 * Store in the history comment the remote host and IP
		 */
		String comment = "";
		if (userObject != null && "[Ljava.lang.String;".equals(userObject.getClass().getName())) {
			String addr = ((String[]) userObject)[0];
			String host = ((String[]) userObject)[1];
			if (StringUtils.isNotEmpty(host) && !host.equals(addr))
				comment = host + " (" + addr + ") ";
			else
				comment = addr;
		}

		// Add a user history entry
		userHistoryDAO.createUserHistory(user, UserHistory.EVENT_USER_LOGIN, comment, id);
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * Representation of the container session eventually associated to this
	 * user session
	 */
	public Object getExternalSession() {
		return externalSession;
	}

	public void setExternalSession(Object externalSession) {
		this.externalSession = externalSession;
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	protected void finalize() throws Throwable {
		externalSession = null;
	}

	@Override
	public int compareTo(UserSession o) {
		int compare = new Integer(status).compareTo(new Integer(o.getStatus()));
		if (compare == 0)
			compare = o.getCreation().compareTo(creation);
		return compare;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final UserSession other = (UserSession) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * A generic object that is stored within the session and is thought to be
	 * used as alternative session identifier
	 */
	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTenantId() {
		return tenantId;
	}

	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public String getPassword() {
		return password;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public void logError(String message) {
		logs.add(0, new Log(ERROR, message));
	}

	public void logWarn(String message) {
		logs.add(0, new Log(WARN, message));
	}

	public void logInfo(String message) {
		logs.add(0, new Log(INFO, message));
	}

	public List<Log> getLogs() {
		return logs;
	}

	public Log getLastError() {
		if (logs == null || logs.isEmpty())
			return null;

		for (Log log : logs)
			if (ERROR.equals(log.getLevel()))
				return log;
		return null;
	}

	public boolean isEmpty() {
		return logs.isEmpty();
	}

	public class Log {
		private Date date = new Date();

		private String level;

		private String message;

		public Log(String level, String message) {
			super();
			this.level = level;
			this.message = message;
		}

		@Override
		public String toString() {
			return message;
		}

		public String getLevel() {
			return level;
		}

		public Date getDate() {
			return date;
		}

		public String getMessage() {
			return message;
		}
	}
}