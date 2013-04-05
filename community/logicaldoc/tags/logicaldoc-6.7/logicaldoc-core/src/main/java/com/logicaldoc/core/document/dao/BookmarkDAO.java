package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.Bookmark;

/**
 * DAO service for bookmarks
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public interface BookmarkDAO extends PersistentObjectDAO<Bookmark> {

	/**
	 * Finds all bookmarks for the given user id.
	 * 
	 * @param userId ID of the user.
	 * @return Collection of all bookmarks for the specified user ordered by
	 *         position.
	 */
	public List<Bookmark> findByUserId(long userId);

	/**
	 * Finds all bookmarks for the given user id and the given document id.
	 * 
	 * @param userId ID of the user.
	 * @param docId ID of the document.
	 * @return Collection of all bookmarks for the specified user ordered by
	 *         position.
	 */
	public List<Bookmark> findByUserIdAndDocId(long userId, long docId);
}
