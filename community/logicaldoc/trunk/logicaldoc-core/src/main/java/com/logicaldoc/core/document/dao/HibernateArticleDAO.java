package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.document.Article;

/**
 * Hibernate implementation of <code>ArticleDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateArticleDAO extends HibernateDaoSupport implements ArticleDAO {

	protected static Log log = LogFactory.getLog(HibernateArticleDAO.class);

	private HibernateArticleDAO() {
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#delete(long)
	 */
	public boolean delete(long articleId) {
		boolean result = true;

		try {
			Article article = (Article) getHibernateTemplate().get(Article.class, new Long(articleId));
			if (article != null){
				article.setDeleted(1);
				getHibernateTemplate().saveOrUpdate(article);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#findByDocId(long)
	 */
	@SuppressWarnings("unchecked")
	public List<Article> findByDocId(long docId) {
		List<Article> coll = new ArrayList<Article>();

		try {
			coll = (List<Article>) getHibernateTemplate().find(
					"from Article _article where _article.docId = ? order by _article.date",
					new Object[] { new Long(docId) });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.ArticleDAO#findById(long)
	 */
	public Article findById(long articleid) {
		Article article = null;

		try {
			article = (Article) getHibernateTemplate().get(Article.class, new Long(articleid));
			if(article!=null && article.getDeleted()==1)
				return null;
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
	public List<Article> findByUserName(String username) {
		List<Article> coll = new ArrayList<Article>();

		try {
			coll = (List<Article>) getHibernateTemplate().find(
					"from Article _article where _article.username = ? order by _article.date",
					new Object[] { username });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return coll;
	}
}