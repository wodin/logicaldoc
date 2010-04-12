package com.logicaldoc.core.document;

import com.logicaldoc.core.PersistentObject;

/**
 * Represents a download ticket.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public class DownloadTicket extends PersistentObject {

	private String ticketId = "";

	private long docId = 0;

	private long userId = -1;

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
}