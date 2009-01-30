package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.Article;

/**
 * DAO for <code>Article</code> handling.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public interface ArticleDAO extends PersistentObjectDAO<Article> {

	/**
	 * This method selects all articles of for a given document
	 * 
	 * @param docId - ID of the document.
	 * @return The list of articles ordered by date
	 */
	public List<Article> findByDocId(long docId);

	/**
	 * This method selects all articles of the given user
	 * 
	 * @param userId - ID of the user
	 * @return The list of articles ordered by date
	 */
	public Collection<Article> findByUserId(long userId);
}