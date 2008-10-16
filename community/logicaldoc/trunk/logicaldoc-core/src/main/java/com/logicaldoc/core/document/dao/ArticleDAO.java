package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.List;

import com.logicaldoc.core.document.Article;

/**
 * DAO for <code>Article</code> handling.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 */
public interface ArticleDAO {

	/**
	 * This method persists a article object.
	 * 
	 * @param article Article to be stored.
	 * @return True if successfully stored in a database.
	 */
	public boolean store(Article article);

	/**
	 * This method deletes an article.
	 * 
	 * @param articleId ID of the article which should be deleted.
	 */
	public boolean delete(long articleId);

	/**
	 * This method finds an article by its primarykey.
	 * 
	 * @param articleId Primary key of the article.
	 * @return Article with given primarykey.
	 */
	public Article findByPrimaryKey(long articleId);

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
	 * @param username - ID of the user
	 *  @return The list of articles ordered by date
	 */
	public Collection<Article> findByUserName(String username);
}