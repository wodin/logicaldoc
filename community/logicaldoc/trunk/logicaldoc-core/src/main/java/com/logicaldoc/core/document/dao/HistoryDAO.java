package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.document.History;

/**
 * DAO for <code>History</code> handling.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini
 */
public interface HistoryDAO {

	/**
	 * This method persists a history object.
	 * 
	 * @param history History to be stored.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(History history);

	/**
	 * This method deletes a history.
	 * 
	 * @param historyId ID of the history which should be delete.
	 */
	public boolean delete(long historyId);

	/**
	 * This method selects all histories of a given document.
	 * 
	 * @param docId - ID of the document.
	 * @return list of histories ordered by date
	 */
	public List<History> findByDocId(long docId);

	/**
	 * This method selects all histories of a given username.
	 * 
	 * @param username
	 * @return list of histories ordered by date
	 */
	public List<History> findByUsername(String username);
}