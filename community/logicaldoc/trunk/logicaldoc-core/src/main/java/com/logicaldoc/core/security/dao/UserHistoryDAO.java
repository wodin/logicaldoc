package com.logicaldoc.core.security.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;

/**
 * DAO for <code>UserHistory</code> handling.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.0
 */
public interface UserHistoryDAO extends PersistentObjectDAO<UserHistory> {

	/**
	 * This method selects all histories of a given user.
	 * 
	 * @param userId
	 * @return list of histories ordered by date
	 */
	public List<UserHistory> findByUserId(long userId);

	/**
	 * Creates an user history entry
	 * 
	 * @param user The user that made the operation
	 * @param eventType The event type
	 * @param comment The comment provided by the user
	 * @param sessionId The user session id
	 */
	public void createUserHistory(User user, String eventType, String comment, String sessionId);

}
