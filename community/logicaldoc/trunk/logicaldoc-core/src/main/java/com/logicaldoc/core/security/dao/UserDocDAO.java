package com.logicaldoc.core.security.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.security.UserDoc;

/**
 * This class is a DAO-service for userdocs.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public interface UserDocDAO extends PersistentObjectDAO<UserDoc> {

	/**
	 * Select the count of userdocs for an user.
	 */
	public int getCount(long userId);

	/**
	 * This method deletes an userdoc by the alternate key.
	 */
	public boolean delete(long docId, long userId);

	/**
	 * This method deletes all userdocs by the document id.
	 */
	public boolean deleteByDocId(long docId);

	/**
	 * This method deletes all userdocs by the user id.
	 */
	public boolean deleteByUserId(long userId);

	/**
	 * Selects all userdocs for a given user.
	 */
	public List<UserDoc> findByUserId(long userId);

	/**
	 * Selects all userdocs for a given doc.
	 */
	public List<UserDoc> findByDocId(long docId);

	/**
	 * Selects the oldest userdoc for a given user.
	 */
	public UserDoc findByMinDate(long userId);

	/**
	 * Check if an userdoc exists.
	 * 
	 * @param docId ID of the document
	 * @param userId
	 */
	public boolean exists(long docId, long userId);
}