package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.History;

/**
 * DAO for <code>History</code> handling.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini
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
}