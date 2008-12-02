package com.logicaldoc.core.document;

import java.util.Date;

import com.logicaldoc.core.PersistentObject;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 * @author Marco Meschieri - Logical Objects
 */
public class History extends PersistentObject {
	public final static String STORED = "msg.jsp.docstored";

	public final static String CHANGED = "msg.jsp.docchanged";

	public final static String CHECKIN = "msg.jsp.doccheckedin";

	public final static String CHECKOUT = "msg.jsp.doccheckedout";

	public static final String UNCHECKOUT = "msg.jsp.docuncheckedout";

	private long docId;

	private long userId;

	private Date date = null;

	private String userName = "";

	private String event = "";

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
}
