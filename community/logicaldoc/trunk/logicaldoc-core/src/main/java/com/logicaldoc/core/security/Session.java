package com.logicaldoc.core.security;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.dao.TenantDAO;
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
public class Session implements Comparable<Session> {

	private static Logger log = LoggerFactory.getLogger(Session.class);

	public final static int STATUS_OPEN = 0;

	public final static int STATUS_EXPIRED = 1;

	public final static int STATUS_CLOSED = 2;

	private final static String ERROR = "ERROR";

	private final static String WARN = "WARN";

	private final static String INFO = "INFO";

	// Map docId - Password used to unprotect it
	private Map<Long, String> unprotectedDocs = Collections.synchronizedMap(new HashMap<Long, String>());

	private Date creation = new Date();

	private Date lastRenew = creation;

	/**
	 * Represents the auto generated identifier of the session
	 */
	private String id;

	/**
	 * The password given by the user at login time
	 */
	private String password;

	/**
	 * A third parameter(other than the username and password) given by the
	 * client at login time
	 */
	private String key;

	private String tenantName;

	private long tenantId;

	private int status = STATUS_OPEN;

	private Client client = null;

	private User user = null;

	/**
	 * Represents a dictionary of custom informations a client may save in the
	 * session
	 */
	private Map<String, Object> dictionary = new ConcurrentHashMap<String, Object>();

	private List<Log> logs = new ArrayList<Log>();

	public Map<String, Object> getDictionary() {
		return dictionary;
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
			if (isTimedOut()) {
				setExpired();
				return;
			} else
				lastRenew = new Date();
		else
			return;
	}

	/**
	 * Retrieves the timeout in minutes
	 */
	protected int getTimeout() {
		int timeout = 30;
		ContextProperties config = Context.get().getProperties();
		if (config.getInt(getTenantName() + ".session.timeout") > 0)
			timeout = config.getInt(getTenantName() + ".session.timeout");

		return timeout;
	}

	public boolean isOpen() {
		return status == STATUS_OPEN;
	}

	protected boolean isTimedOut() {
		if (status != STATUS_OPEN)
			return true;

		int timeout = getTimeout();
		if (timeout <= 0)
			return false;

		/**
		 * In order to compare the actual date and the last renewal date, we
		 * prefer to drop milliseconds and seconds.
		 */
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		Date now = calendar.getTime();

		calendar.setTime(lastRenew);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		Date lastRen = calendar.getTime();

		long diff = now.getTime() - lastRen.getTime();
		long diffMinutes = Math.abs(diff / 1000 / 60);
		return diffMinutes >= timeout;
	}

	public int getStatus() {
		if (status == STATUS_OPEN && isTimedOut())
			setExpired();
		return status;
	}

	public void setExpired() {
		log.warn("Session " + getId() + " expired");
		logWarn("Session expired");

		this.status = STATUS_EXPIRED;
		// Add a user history entry
		UserHistoryDAO userHistoryDAO = (UserHistoryDAO) Context.get().getBean(UserHistoryDAO.class);
		userHistoryDAO.createUserHistory(user, UserHistory.EVENT_USER_TIMEOUT, "", id);
	}

	public void setClosed() {
		log.info("Session " + getId() + " was closed");
		logInfo("Session closed");

		this.status = STATUS_CLOSED;
		// Add a user history entry
		UserHistoryDAO userHistoryDAO = (UserHistoryDAO) Context.get().getBean(UserHistoryDAO.class);
		userHistoryDAO.createUserHistory(user, UserHistory.EVENT_USER_LOGOUT, "", id);
	}

	Session(User user, String password, String key, Client client) {
		super();
		assert (user != null);
		this.id = UUID.randomUUID().toString();
		this.tenantId = user.getTenantId();
		this.user = user;
		this.password = password;
		this.key = key;
		this.client = client;

		// Set the user's id
		UserHistoryDAO userHistoryDAO = (UserHistoryDAO) Context.get().getBean(UserHistoryDAO.class);

		TenantDAO tenantDAO = (TenantDAO) Context.get().getBean(TenantDAO.class);
		Tenant tenant = tenantDAO.findById(tenantId);
		if (tenant != null)
			tenantName = tenant.getName();

		/*
		 * Store in the history comment the remote host and IP
		 */
		String comment = "";
		if (client != null) {
			String addr = client.getAddress();
			String host = client.getHost();
			if (StringUtils.isNotEmpty(host) && !host.equals(addr))
				comment = host + " (" + addr + ") ";
			else
				comment = addr;
		}

		// Add a user history entry
		userHistoryDAO.createUserHistory(user, UserHistory.EVENT_USER_LOGIN, comment, id);

		log.info("Session " + getId() + " has been started");
		logInfo("Session started");

		renew();
	}

	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public String toString() {
		return getId();
	}

	@Override
	public int compareTo(Session o) {
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
		final Session other = (Session) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public long getUserId() {
		return user.getId();
	}

	public long getTenantId() {
		return tenantId;
	}

	public String getTenantName() {
		return tenantName;
	}

	public String getPassword() {
		return password;
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Client getClient() {
		return client;
	}

	void setClient(Client client) {
		this.client = client;
	}

	public User getUser() {
		return user;
	}

	public void setTenantId(long tenantId) {
		this.tenantId = tenantId;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public Map<Long, String> getUnprotectedDocs() {
		return unprotectedDocs;
	}
}