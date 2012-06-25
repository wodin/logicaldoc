package com.logicaldoc.core.document;

import java.util.Date;

import com.logicaldoc.core.PersistentObject;

/**
 * Represents a download ticket.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public class DownloadTicket extends PersistentObject {

	public static final int TICKET = 0;

	public static final int PSW_RECOVERY = 1;

	private String ticketId = "";

	private long docId = 0;

	private long userId = -1;

	private int type = TICKET;

	private Date creation = new Date();

	private Date expired = null;

	private int count = 0;

	public DownloadTicket() {

	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	/**
	 * @return Returns the ticketId.
	 */
	public String getTicketId() {
		return ticketId;
	}

	/**
	 * @param ticketId The ticketId to set.
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Date getExpired() {
		return expired;
	}

	public void setExpired(Date expired) {
		this.expired = expired;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isTicketExpired() {
		return new Date().getTime() > expired.getTime();
	}
}