package com.logicaldoc.core.document.dao;

import java.util.Collection;

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
	 * @param articleid ArticleID of the article which should be delete.
	 */
	public boolean delete(int articleid);

	/**
	 * This method finds an article by its primarykey.
	 * 
	 * @param articleid Primarykey of the article.
	 * @return Article with given primarykey.
	 */
	public Article findByPrimaryKey(int articleid);

	/**
	 * This method selects all articles of for a given document.
	 * 
	 * @param docId - ID of the document.
	 */
	public Collection<Article> findByDocId(long docId);
	
	/**
	 * This method selects all articles of the given user.
	 * 
	 * @param username - ID of the user.
	 */
	public Collection<Article> findByUserName(String username);
}
