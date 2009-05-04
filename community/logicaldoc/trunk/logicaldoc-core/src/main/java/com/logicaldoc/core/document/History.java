package com.logicaldoc.core.document;

import java.util.Date;

import com.logicaldoc.core.PersistentObject;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @author Marco Meschieri - Logical Objects
 */
public class History extends PersistentObject {
	public final static String EVENT_STORED = "event.stored";

	public final static String EVENT_CHANGED = "event.changed";

	public final static String EVENT_CHECKEDIN = "event.checkedin";

	public final static String EVENT_CHECKEDOUT = "event.checkedout";

	public static final String EVENT_IMMUTABLE = "event.makeimmutable";
	
	public static final String EVENT_RENAMED = "event.renamed";

	public static final String EVENT_DOWNLOADED = "event.downloaded";
	
	public final static String EVENT_MOVED = "event.moved";
	
	public final static String EVENT_LOCKED = "event.locked";
	
	public final static String EVENT_UNLOCKED = "event.unlocked";
	
	public final static String EVENT_ARCHIVED = "event.archived";
	
	private long docId;

	private long userId;

	private Date date = null;

	private String userName = "";

	private String event = "";

	private String comment = null;

	public History() {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return Returns the docId.
	 */
	public long getDocId() {
		return docId;
	}

	/**
	 * @param docId The docId to set.
	 */
	public void setDocId(long docId) {
		this.docId = docId;
	}

	/**
	 * @return Returns the event.
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event The event to set.
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return Returns the userName.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName The userName to set.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
