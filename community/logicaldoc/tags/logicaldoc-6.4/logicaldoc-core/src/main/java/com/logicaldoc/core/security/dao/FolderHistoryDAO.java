package com.logicaldoc.core.security.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.security.FolderHistory;

/**
 * DAO for <code>FolderHistory</code> handling.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 5.0
 */
public interface FolderHistoryDAO extends PersistentObjectDAO<FolderHistory> {

	/**
	 * This method selects all histories of a given user.
	 * 
	 * @param userId
	 * @return list of histories ordered by date
	 */
	public List<FolderHistory> findByUserId(long userId);

	/**
	 * This method selects all histories of a given folder.
	 * 
	 * @param folderId - ID of the document.
	 * @return list of histories ordered by date
	 */
	public List<FolderHistory> findByFolderId(long folderId);

	/**
	 * This method selects all histories not notified yet.
	 * 
	 * @return list of histories ordered by date
	 */
	public List<FolderHistory> findNotNotified();

	/**
	 * This method selects all histories of a given user and related to the
	 * given event.
	 * 
	 * @param userId The user identifier
	 * @param event The history event
	 * @return list of histories ordered by date
	 */
	public List<FolderHistory> findByUserIdAndEvent(long userId, String event);

	/**
	 * This method deletes all the user history entries oldest than the given
	 * days from now. If <code>ttl</code> is 0 or -1, the cancellation is not
	 * made.
	 * 
	 * @param ttl The maximum number of days over which the history is
	 *        considered old
	 */
	public void cleanOldHistories(int ttl);
}