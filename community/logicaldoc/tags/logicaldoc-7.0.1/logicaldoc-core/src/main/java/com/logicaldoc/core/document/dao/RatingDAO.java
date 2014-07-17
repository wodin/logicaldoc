package com.logicaldoc.core.document.dao;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.Rating;

/**
 * DAO service for ratings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public interface RatingDAO extends PersistentObjectDAO<Rating> {

	/**
	 * Returns a rating that contains count and average of vote on the given
	 * document.
	 * 
	 * @param docId ID of the document.
	 * @return Number of ratings on the given document.
	 */
	public Rating findVotesByDocId(long docId);

	/**
	 * Find the rating for the given user id and the given document id.
	 * 
	 * @param docId ID of the document.
	 * @param userId ID of the user.
	 * @return true if the specified user has already voted on the given document.
	 */
	public boolean findByDocIdAndUserId(long docId, long userId);
}
