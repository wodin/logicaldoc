package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.DiscussionComment;
import com.logicaldoc.core.document.DiscussionThread;

/**
 * DAO service for discussion threads
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public interface DiscussionThreadDAO extends PersistentObjectDAO<DiscussionThread> {

	/**
	 * Finds the list of threads opened on a specified document. The result is
	 * ordered by creationDate asc.
	 */
	public List<DiscussionThread> findByDocId(long docId);

	/**
	 * Initializes lazy loaded collections
	 */
	public void initialize(DiscussionThread thread);

	/**
	 * Finds comments of a user, ordered by date desc
	 * 
	 * @param userId User's identifier
	 * @param maxEntries the max number of entries (optional)
	 */
	public List<DiscussionComment> findCommentsByUserId(long userId, Integer maxEntries);
}