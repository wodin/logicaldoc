package com.logicaldoc.core.document.dao;

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
	 * This method deletes all tickets of the specified menu.
	 * 
	 * @param ticketId The ticket id
	 * @return DownloadTicket with given ticket id.
	 */
	public DownloadTicket findByTicketId(String ticketId);
}