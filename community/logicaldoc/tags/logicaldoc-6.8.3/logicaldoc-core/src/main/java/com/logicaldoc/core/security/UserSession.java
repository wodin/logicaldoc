package com.logicaldoc.core.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.security.dao.UserHistoryDAO;
import com.logicaldoc.util.Context;

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

	private int status = STATUS_OPEN;

	private Object externalSession = null;

	private Object userObject = null;

	private Map<String, Object> dictionary = new HashMap<String, Object>();

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

	UserSession(String userName, Object userObject) {
		super();
		this.id = UUID.randomUUID().toString();
		this.userName = userName;
		this.userObject = userObject;

		// Set the userid
		UserDAO userDAO = (UserDAO) Context.getInstance().getBean(UserDAO.class);
		UserHistoryDAO userHistoryDAO = (UserHistoryDAO) Context.getInstance().getBean(UserHistoryDAO.class);
		User user = userDAO.findByUserName(userName);
		this.userId = user.getId();

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
}