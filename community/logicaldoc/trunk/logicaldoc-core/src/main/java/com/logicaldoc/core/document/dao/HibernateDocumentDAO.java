package com.logicaldoc.core.document.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>DocumentDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateDocumentDAO.java,v 1.1 2007/06/29 06:28:28 marco Exp $
 * @since 3.0
 */
public class HibernateDocumentDAO extends HibernateDaoSupport implements DocumentDAO {

	protected static Log log = LogFactory.getLog(HibernateDocumentDAO.class);

	private ArticleDAO articleDAO;

	private HistoryDAO historyDAO;

	private MenuDAO menuDAO;

	private UserDAO userDAO;
	
	private HibernateDocumentDAO() {
	}

	public MenuDAO getMenuDAO() {
		return menuDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	public ArticleDAO getArticleDAO() {
		return articleDAO;
	}

	public void setArticleDAO(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public HistoryDAO getHistoryDAO() {
		return historyDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#delete(int)
	 */
	public boolean delete(int docId) {
		boolean result = true;

		try {
			Document doc = (Document) getHibernateTemplate().get(Document.class, docId);
			if (doc != null) {
				getHibernateTemplate().deleteAll(articleDAO.findByDocId(docId));
				getHibernateTemplate().deleteAll(historyDAO.findByDocId(docId));
				Menu menu = doc.getMenu();
				doc.getVersions().clear();
				doc.getKeywords().clear();
				doc.setMenu(null);
				getHibernateTemplate().delete(doc);
				menuDAO.delete(menu.getMenuId());
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#deleteByMenuId(int)
	 */
	public boolean deleteByMenuId(int menuId) {
		Document doc = findByMenuId(menuId);
		if (doc != null)
			return delete(doc.getDocId());
		else
			return true;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findAll()
	 */
	@SuppressWarnings("unchecked")
	public Collection<Document> findAll() {
		Collection<Document> coll = new ArrayList<Document>();

		try {
			coll = (Collection<Document>) getHibernateTemplate().find("from com.logicaldoc.core.document.Document");
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findByMenuId(int)
	 */
	@SuppressWarnings("unchecked")
	public Document findByMenuId(int menuId) {
		Document doc = null;

		try {
			Collection<Document> coll = (Collection<Document>) getHibernateTemplate().find(
					"from com.logicaldoc.core.document.Document _doc where _doc.menu.menuId = ?",
					new Object[] { new Integer(menuId) });
			if (!coll.isEmpty())
				doc = coll.iterator().next();
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return doc;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findByPrimaryKey(int)
	 */
	public Document findByPrimaryKey(int docId) {
		Document doc = null;

		try {
			doc = (Document) getHibernateTemplate().get(Document.class, docId);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return doc;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findByUserName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Integer> findByUserName(String username) {
		Collection<Integer> coll = new ArrayList<Integer>();

		try {
			Collection<Menu> menus = menuDAO.findByUserName(username);
			if (menus.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer("select docId from com.logicaldoc.core.document.Document _doc where ");
			query.append("_doc.menu.menuId in (");
			boolean first = true;
			for (Menu menu : menus) {
				if (!first)
					query.append(",");
				query.append("'" + menu.getMenuId() + "'");
				first = false;
			}
			query.append(")");

			coll = (Collection<Integer>) getHibernateTemplate().find(query.toString());

		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findCheckoutByUserName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Document> findCheckoutByUserName(String username) {
		Collection<Document> coll = new ArrayList<Document>();
		try {
			StringBuffer query = new StringBuffer("from com.logicaldoc.core.document.Document _doc where ");
			query.append("_doc.checkoutUser = '" + username + "'");
			coll = (Collection<Document>) getHibernateTemplate().find(query.toString());
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findMenuIdByKeyword(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Integer> findMenuIdByKeyword(String keyword) {
		Collection<Integer> coll = new ArrayList<Integer>();

		try {
			StringBuilder query = new StringBuilder(
					"select menu.menuId from com.logicaldoc.core.document.Document _doc where ");
			query.append("'" + keyword + "'");
			query.append(" in elements(_doc.keywords) ");
			coll = (Collection<Integer>) getHibernateTemplate().find(query.toString());
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#store(com.logicaldoc.core.document.Document)
	 */
	@SuppressWarnings("unchecked")
	public boolean store(final Document doc) {
		boolean result = true;
		try {
			Set<String> src = doc.getKeywords();
			if (src != null && src.size() > 0) {
				// Trim too long keywords
				Set<String> dst = new HashSet<String>();
				for (String str : src) {
					String s = str;
					if (str.length() > 20) {
						s = str.substring(0, 20);
					}
					if (!dst.contains(s))
						dst.add(s);
				}
				doc.setKeywords(dst);
			}
			getHibernateTemplate().saveOrUpdate(doc);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#toKeywords(java.lang.String)
	 */
	public Set<String> toKeywords(String words) {
		Set<String> coll = new HashSet<String>();
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(words);

		int start = boundary.first();

		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
			String word = words.substring(start, end).toLowerCase().trim();

			if (word.length() > 2) {
				if (word.length() > 20)
					coll.add(word.substring(0, 20));
				else
					coll.add(word);
			}
		}

		return coll;
	}

	@SuppressWarnings("unchecked")
	public Collection<String> findKeywords(String firstLetter, String username) {
		Collection<String> coll = new ArrayList<String>();

		try {
			User user = userDAO.findByPrimaryKey(username);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select B.co_keyword from co_document A, co_keywords B, co_menugroup C "
								+ " where A.co_docid = B.co_docid and A.co_menuid=C.co_menuid and C.co_groupname in (");
				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append("'" + ug.getGroupName() + "'");
					first = false;
				}
				query.append(") and lower(B.co_keyword) like '");
				query.append(firstLetter.toLowerCase()).append("%' ");

				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;

				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query.toString());
					while (rs.next()) {
						coll.add(rs.getString(1));
					}
				} finally {
					if (rs != null)
						rs.close();
					if (stmt != null)
						stmt.close();
					if (con != null)
						con.close();
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("unchecked")
	public Collection<Document> findLastModifiedByUserName(String username, int maxElements) {
		Collection<Document> coll = new ArrayList<Document>();

		try {
			StringBuilder query = new StringBuilder(
					"SELECT _history.docId from com.logicaldoc.core.document.History _history");
			query.append(" WHERE _history.username = '" + username + "' ");
			query.append(" ORDER BY _history.date DESC");

			Collection<Integer> results = (Collection<Integer>) getHibernateTemplate().find(query.toString());
			for (Integer docid : results) {
				if (coll.size() >= maxElements)
					break;
				Document document = findByPrimaryKey(docid);
				if (menuDAO.isReadEnable(document.getMenuId(), username))
					coll.add(document);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	public Map<String, Integer> findAllKeywords() {

		Map<String, Integer> map = new HashMap<String, Integer>();

		try {
			StringBuilder query = new StringBuilder("SELECT COUNT(keyword), keyword");
			query.append(" FROM com.logicaldoc.core.document.Document _doc JOIN _doc.keywords keyword GROUP BY keyword");

			List ssss = getHibernateTemplate().find(query.toString());
			for (Iterator iter = ssss.iterator(); iter.hasNext();) {
				Object[] element = (Object[]) iter.next();
				if (element != null && element.length > 1) {
					Long value = (Long) element[0];
					String key = (String) element[1];
					map.put(key, value.intValue());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}
	
}