package com.logicaldoc.core.document.dao;

import com.logicaldoc.core.document.DownloadTicket;

/**
 * DAO for <code>DownloadTicket</code> handling.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public interface DownloadTicketDAO {

	/**
	 * This method persists a download ticket object.
	 * 
	 * @param ticket DownloadTicket to be stored.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(DownloadTicket ticket);

	/**
	 * This method deletes a download ticket.
	 * 
	 * @param historyid ID of the ticket which should be delete.
	 */
	public boolean delete(String ticketid);

	/**
	 * This method deletes all tickets of the specified menu.
	 * 
	 * @param menuId ID of the menu
	 */
	public boolean deleteByMenuId(int menuId);

	/**
	 * This method finds a download ticket by its primarykey.
	 * 
	 * @param ticketid Primarykey of the download ticket.
	 * @return DownloadTicket with given primarykey.
	 */
	public DownloadTicket findByPrimaryKey(String ticketid);
}