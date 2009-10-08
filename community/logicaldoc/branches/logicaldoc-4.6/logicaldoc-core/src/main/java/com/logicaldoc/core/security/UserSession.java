package com.logicaldoc.core.security;

import java.util.Date;
import java.util.UUID;

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

	private int status = STATUS_OPEN;

	private Object externalSession = null;

	private Object userObject = null;

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
	}

	public void setClosed() {
		this.status = STATUS_CLOSED;
		externalSession = null;
	}

	UserSession(String userName) {
		super();
		this.id = UUID.randomUUID().toString();
		this.userName = userName;
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
	 * A generic object that is stored within the session
	 */
	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
}