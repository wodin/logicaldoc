package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.Date;
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
	 * @return max Optional maximum number of records
	 * @return list of histories ordered by date
	 */
	public List<FolderHistory> findNotNotified(Integer max);

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
	 * This method finds all histories about a path (you can use expression)
	 * 
	 * @param pathExpression The path expression (like /Default/acme%)
	 * @param oldestDate The older date for the retrieved histories
	 * @events events Optional list of event codes to be used as filter
	 * @param max Optional maximum number of records
	 * @return
	 */
	public List<FolderHistory> findByPath(String pathExpression, Date oldestDate, Collection<String> events, Integer max);

	/**
	 * This method deletes all the user history entries oldest than the given
	 * days from now. If <code>ttl</code> is 0 or -1, the cancellation is not
	 * made.
	 * 
	 * @param ttl The maximum number of days over which the history is
	 *        considered old
	 */
	public void cleanOldHistories(int ttl);

	/**
	 * Checks if the histories recording is enabled
	 */
	public boolean isEnabled();
}