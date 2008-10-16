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

	private String username = "";

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