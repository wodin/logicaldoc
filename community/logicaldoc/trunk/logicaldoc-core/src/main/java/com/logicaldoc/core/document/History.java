package com.logicaldoc.core.document;

import java.util.Date;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini
 */
public class History {
	public final static String STORED = "msg.jsp.docstored";

	public final static String CHANGED = "msg.jsp.docchanged";

	public final static String CHECKIN = "msg.jsp.doccheckedin";

	public final static String CHECKOUT = "msg.jsp.doccheckedout";

	private int historyId;

	private long docId;

	private Date date = null;

	private String username = "";

	private String event = "";

	public History() {
	}

	/**
	 * @return Returns the historyId.
	 */
	public int getHistoryId() {
		return historyId;
	}

	/**
	 * @param historyId The historyId to set.
	 */
	public void setHistoryId(int historyId) {
		this.historyId = historyId;
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

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof History))
			return false;
		History other = (History) arg0;
		return other.getHistoryId() == this.getHistoryId();
	}

	@Override
	public int hashCode() {
		return new Integer(historyId).hashCode();
	}
}
