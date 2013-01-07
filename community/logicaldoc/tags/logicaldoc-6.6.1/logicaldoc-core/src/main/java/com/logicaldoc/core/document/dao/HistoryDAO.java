package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.History;

/**
 * DAO for <code>History</code> handling.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 */
public interface HistoryDAO extends PersistentObjectDAO<History> {
	/**
	 * This method selects all histories of a given document.
	 * 
	 * @param docId - ID of the document.
	 * @return list of histories ordered by date
	 */
	public List<History> findByDocId(long docId);

	/**
	 * This method selects all histories of a given user.
	 * 
	 * @param userId
	 * @return list of histories ordered by date
	 */
	public List<History> findByUserId(long userId);

	/**
	 * This method selects all histories of a given folder.
	 * 
	 * @param folderId - ID of the document.
	 * @return list of histories ordered by date
	 */
	public List<History> findByFolderId(long folderId);

	/**
	 * This method selects all histories not notified yet.
	 * 
	 * @return max Optional maximum number of records
	 * @return list of histories ordered by date
	 */
	public List<History> findNotNotified(Integer max);

	/**
	 * This method deletes all the document history entries oldest than the
	 * given days from now. If <code>ttl</code> is 0 or -1, the cancellation is
	 * not made.
	 * 
	 * @param ttl The maximum number of days over which the history is
	 *        considered old
	 */
	public void cleanOldHistories(int ttl);

	/**
	 * This method selects all histories of a given user and related to the
	 * given event.
	 * 
	 * @param userId The user identifier
	 * @param event The history event
	 * @return list of histories ordered by date
	 */
	public List<History> findByUserIdAndEvent(long userId, String event);

	/**
	 * Checks if the histories recording is enabled
	 */
	public boolean isEnabled();
}