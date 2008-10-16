package com.logicaldoc.core.document;

import java.util.Date;

import com.logicaldoc.core.PersistentObject;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini
 * @author Marco Meschieri
 */
public class History extends PersistentObject{
	public final static String STORED = "msg.jsp.docstored";

	public final static String CHANGED = "msg.jsp.docchanged";

	public final static String CHECKIN = "msg.jsp.doccheckedin";

	public final static String CHECKOUT = "msg.jsp.doccheckedout";

	private long docId;

	private Date date = null;

	private String username = "";

	private String event = "";

	public History() {
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
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
}
