package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.document.Article;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>ArticleDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateArticleDAO.java,v 1.1 2007/06/29 06:28:28 marco Exp $
 * @since 3.0
 */
public class HibernateArticleDAO extends HibernateDaoSupport implements ArticleDAO {

	protected static Log log = LogFactory.getLog(HibernateArticleDAO.class);

	private HibernateArticleDAO() {
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#delete(int)
	 */
	public boolean delete(int articleid) {
		boolean result = true;

		try {
			Article article = (Article) getHibernateTemplate().get(Article.class, new Integer(articleid));
			if (article != null)
				getHibernateTemplate().delete(article);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#findByDocId(int)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Article> findByDocId(int docId) {
		Collection<Article> coll = new ArrayList<Article>();

		try {
			coll = (Collection<Article>) getHibernateTemplate()
					.find(
							"from com.logicaldoc.core.document.Article _article where _article.docId = ? order by _article.articleDate",
							new Object[] { new Integer(docId) });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#findByPrimaryKey(int)
	 */
	public Article findByPrimaryKey(int articleid) {
		Article article = null;

		try {
			article = (Article) getHibernateTemplate().get(Article.class, new Integer(articleid));
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);

		}

		return article;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#store(com.logicaldoc.core.document.Article)
	 */
	public boolean store(Article article) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(article);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#findByUserName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Article> findByUserName(String username) {
		Collection<Article> coll = new ArrayList<Article>();

		try {
			coll = (Collection<Article>) getHibernateTemplate()
					.find(
							"from com.logicaldoc.core.document.Article _article where _article.username = ? order by _article.articleDate",
							new Object[] { username });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return coll;
	}
}