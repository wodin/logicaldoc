package com.logicaldoc.core.document.dao;

import java.util.Date;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.DownloadTicket;

/**
 * DAO for <code>DownloadTicket</code> handling.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public interface DownloadTicketDAO extends PersistentObjectDAO<DownloadTicket> {
	/**
	 * This method deletes a download ticket.
	 * 
	 * @param historyid ID of the ticket which should be delete.
	 */
	public boolean deleteByTicketId(String ticketId);

	/**
	 * This method deletes all tickets of the specified document.
	 * 
	 * @param docId ID of the document
	 */
	public boolean deleteByDocId(long docId);

	/**
	 * Deletes all tickets older than the specified date.
	 */
	public boolean deleteOlder(Date date);

	/**
	 * Deletes all tickets older than the specified time to live.
	 * <p>
	 * Uses the global configuration parameter <code>ticket.ttl</code> that
	 * specifies a number of hours.
	 */
	public void deleteOlder();

	/**
	 * This finds a ticket by its identifier.
	 * 
	 * @param ticketId The ticket id
	 * @return DownloadTicket with given ticket id.
	 */
	public DownloadTicket findByTicketId(String ticketId);
}