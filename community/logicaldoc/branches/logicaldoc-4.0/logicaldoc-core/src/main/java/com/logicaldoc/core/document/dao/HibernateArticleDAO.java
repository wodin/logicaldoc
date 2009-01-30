package com.logicaldoc.core.document.dao;

import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.Article;

/**
 * Hibernate implementation of <code>ArticleDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateArticleDAO extends HibernatePersistentObjectDAO<Article> implements ArticleDAO {
	public HibernateArticleDAO() {
		super(Article.class);
		super.log = LogFactory.getLog(HibernateArticleDAO.class);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#findByDocId(long)
	 */
	@SuppressWarnings("unchecked")
	public List<Article> findByDocId(long docId) {
		return findByWhere("_entity.docId = ? order by _entity.date", new Object[] { new Long(docId) });
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#findByUserId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Article> findByUserId(long userId) {
		return findByWhere("_entity.userId = ? order by _entity.date", new Object[] { new Long(userId) });
	}
}