package com.logicaldoc.core.document.dao;

import java.io.File;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.document.Article;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.util.config.SettingsConfig;

/**
 * Hibernate implementation of <code>DocumentDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDocumentDAO extends HibernateDaoSupport implements DocumentDAO {

	protected static Log log = LogFactory.getLog(HibernateDocumentDAO.class);

	private ArticleDAO articleDAO;

	private HistoryDAO historyDAO;

	private MenuDAO menuDAO;

	private UserDAO userDAO;

	private UserDocDAO userDocDAO;

	private DocumentLinkDAO linkDAO;

	private SettingsConfig settings;

	private HibernateDocumentDAO() {
	}

	public void setUserDocDAO(UserDocDAO userDocDAO) {
		this.userDocDAO = userDocDAO;
	}

	public void setSettings(SettingsConfig settings) {
		this.settings = settings;
	}

	public void setLinkDAO(DocumentLinkDAO linkDAO) {
		this.linkDAO = linkDAO;
	}

	public MenuDAO getMenuDAO() {
		return menuDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	public void setArticleDAO(ArticleDAO articleDAO) {
		this.articleDAO = articleDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#delete(int)
	 */
	public boolean delete(long docId) {
		boolean result = true;
		try {
			Document doc = (Document) getHibernateTemplate().get(Document.class, docId);
			if (doc != null) {
				// Remove articles
				for (Article article : articleDAO.findByDocId(docId)) {
					article.setDeleted(1);
					getHibernateTemplate().saveOrUpdate(article);
				}

				// Remove history
				for (History history : historyDAO.findByDocId(docId)) {
					history.setDeleted(1);
					getHibernateTemplate().saveOrUpdate(history);
				}

				// Remove links
				for (DocumentLink link : linkDAO.findByDocId(docId)) {
					link.setDeleted(1);
					getHibernateTemplate().saveOrUpdate(link);
				}

				userDocDAO.deleteByDocId(docId);

				doc.setDeleted(1);
				getHibernateTemplate().saveOrUpdate(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findAll()
	 */
	@SuppressWarnings("unchecked")
	public Collection<Document> findAll() {
		Collection<Document> coll = new ArrayList<Document>();

		try {
			coll = (Collection<Document>) getHibernateTemplate().find("from Document");
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findById(int)
	 */
	public Document findById(long docId) {
		Document doc = null;

		try {
			doc = (Document) getHibernateTemplate().get(Document.class, docId);
			if(doc!=null && doc.getDeleted()==1)
				return null;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return doc;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findByUserId(long)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Long> findByUserId(long userId) {
		Collection<Long> coll = new ArrayList<Long>();

		try {
			Collection<Menu> menus = menuDAO.findByUserId(userId);
			if (menus.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer("select _doc.id from Document _doc where ");
			query.append("_doc.folder.id in (");
			boolean first = true;
			for (Menu menu : menus) {
				if (!first)
					query.append(",");
				query.append("'" + menu.getId() + "'");
				first = false;
			}
			query.append(")");

			coll = (Collection<Long>) getHibernateTemplate().find(query.toString());

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
			StringBuffer query = new StringBuffer("from Document _doc where ");
			query.append("_doc.checkoutUser = '" + username + "'");
			coll = (Collection<Document>) getHibernateTemplate().find(query.toString());
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findDocIdByKeyword(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Long> findDocIdByKeyword(String keyword) {
		Collection<Long> coll = new ArrayList<Long>();

		try {
			StringBuilder query = new StringBuilder("select _doc.id from Document _doc where ");
			query.append("'" + keyword + "'");
			query.append(" in elements(_doc.keywords) ");
			coll = (Collection<Long>) getHibernateTemplate().find(query.toString());
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
					if (str.length() > 255) {
						s = str.substring(0, 255);
					}
					if (!dst.contains(s))
						dst.add(s);
				}
				doc.setKeywords(dst);
			}

			File docFile = new File(
					(settings.getValue("docdir") + "/" + doc.getPath() + "/doc_" + doc.getId() + "/" + doc
							.getFileName()));
			if (docFile.exists()) {
				long size = docFile.length();
				doc.setFileSize(size);
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

	@SuppressWarnings( { "unchecked", "deprecation" })
	public Collection<String> findKeywords(String firstLetter, long userId) {
		Collection<String> coll = new ArrayList<String>();

		try {
			User user = userDAO.findById(userId);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select B.ld_keyword from ld_document A, ld_keyword B, ld_menugroup C "
								+ " where A.ld_deleted=0 and A.ld_id = B.ld_docid and A.ld_folderid=C.ld_menuid and C.ld_groupid in (");
				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append(Long.toString(ug.getId()));
					first = false;
				}
				query.append(") and lower(B.ld_keyword) like '");
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
	public List<Document> findLastModifiedByUserId(long userId, int maxElements) {
		List<Document> coll = new ArrayList<Document>();

		try {
			StringBuilder query = new StringBuilder("SELECT _history.docId from History _history");
			query.append(" WHERE _history.userId = " + Long.toString(userId) + " ");
			query.append(" ORDER BY _history.date DESC");

			Collection<Long> results = (Collection<Long>) getHibernateTemplate().find(query.toString());
			for (Long docid : results) {
				if (coll.size() >= maxElements)
					break;
				Document document = findById(docid);
				if (menuDAO.isReadEnable(document.getFolder().getId(), userId))
					coll.add(document);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Integer> findAllKeywords() {
		Map<String, Integer> map = new HashMap<String, Integer>();

		try {
			StringBuilder query = new StringBuilder("SELECT COUNT(keyword), keyword");
			query.append(" FROM Document _doc JOIN _doc.keywords keyword GROUP BY keyword");

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

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Document> findByUserIdAndKeyword(long userId, String keyword) {
		Collection<Document> coll = new ArrayList<Document>();
		try {
			User user = userDAO.findById(userId);
			Collection<Group> precoll = user.getGroups();
			Iterator<Group> iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer("select distinct(_doc) from Document _doc  ");
			query.append(" left outer join _menu.menuGroups as _group ");
			query.append(" where _group.groupId in (");

			boolean first = true;
			while (iter.hasNext()) {
				if (!first)
					query.append(",");
				Group ug = (Group) iter.next();
				query.append(Long.toString(ug.getId()));
				first = false;
			}
			query.append(")");

			Collection<Long> ids = findDocIdByUserIdAndKeyword(userId, keyword);
			Iterator<Long> iter2 = ids.iterator();
			if (ids.isEmpty())
				return coll;
			query.append("and _doc.id in (");
			first = true;
			while (iter2.hasNext()) {
				if (!first)
					query.append(",");
				query.append("'" + iter.next() + "'");
				first = false;
			}
			query.append(")");
			coll = (Collection<Document>) getHibernateTemplate().find(query.toString());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Set<Long> findDocIdByUserIdAndKeyword(long userId, String keyword) {
		Set<Long> ids = new HashSet<Long>();
		try {
			User user = userDAO.findById(userId);
			Collection<Group> precoll = user.getGroups();
			Iterator<Group> iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select distinct(C.ld_id) from ld_menugroup A, ld_document C, ld_keyword D "
								+ " where A.ld_menuid=C.ld_folderid AND C.ld_id=D.ld_docid" + " AND A.ld_groupid in (");
				boolean first = true;
				while (iter.hasNext()) {
					if (!first)
						query.append(",");
					Group ug = (Group) iter.next();
					query.append(Long.toString(ug.getId()));
					first = false;
				}
				query.append(")");
				query.append(" AND lower(D.ld_keyword)='" + keyword + "'");

				Connection con = null;
				Statement stmt = null;
				ResultSet rs = null;

				try {
					con = getSession().connection();
					stmt = con.createStatement();
					rs = stmt.executeQuery(query.toString());
					while (rs.next()) {
						ids.add(new Long(rs.getLong(1)));
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
			log.error(e.getMessage(), e);
		}
		return ids;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Document> findLastDownloadsByUserId(long userId, int maxResults) {
		List<Document> coll = new ArrayList<Document>();

		try {
			StringBuffer query = new StringBuffer("select _userdoc.id.docId from UserDoc _userdoc");
			query.append(" where _userdoc.id.userId = ?");
			query.append(" order by _userdoc.date desc");

			Collection<Long> results = (Collection<Long>) getHibernateTemplate().find(query.toString(), userId);
			ArrayList<Long> tmpal = new ArrayList<Long>(results);
			List<Long> docIds = tmpal;

			if (docIds.isEmpty())
				return coll;

			if (docIds.size() > maxResults) {
				tmpal.subList(0, maxResults - 1);
			}

			query = new StringBuffer("from Document _doc  ");
			query.append(" where _doc.id in (");

			for (int i = 0; i < docIds.size(); i++) {
				Long docId = docIds.get(i);
				if (i > 0)
					query.append(",");
				query.append(docId);
			}
			query.append(")");

			// execute the query
			Collection<Document> unorderdColl = (Collection<Document>) getHibernateTemplate().find(query.toString());

			// put all elements in a map
			HashMap<Long, Document> hm = new HashMap<Long, Document>();
			for (Document doc : unorderdColl) {
				hm.put(doc.getId(), doc);
			}

			// Access the map using the menuIds
			// if a match is found, put it in the original list
			for (Long docId : docIds) {
				Document myDoc = hm.get(docId);
				if (myDoc != null)
					coll.add(myDoc);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Long> findDocIdByFolder(long folderId) {
		Collection<Long> coll = new ArrayList<Long>();

		try {
			StringBuffer query = new StringBuffer("select _doc.id from Document _doc where ");
			query.append("_doc.folder.id = ");
			query.append(Long.toString(folderId));

			coll = (Collection<Long>) getHibernateTemplate().find(query.toString());
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Document> findByFolder(long folderId) {
		Collection<Document> coll = new ArrayList<Document>();

		try {
			StringBuffer query = new StringBuffer("select _doc from Document _doc where ");
			query.append("_doc.folder.id = ");
			query.append(Long.toString(folderId));

			coll = (Collection<Document>) getHibernateTemplate().find(query.toString());
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Document> findLinkedDocuments(long docId, String linkType, Integer direction) {
		Collection<Document> coll = new ArrayList<Document>();
		StringBuffer query = null;
		try {
			query = new StringBuffer("select distinct(_doc) from Document _doc, DocumentLink _link where ");
			if (direction == null) {
				query.append(" ((_link.document1 = _doc and _link.document1.id = ? )"
						+ "or (_link.document2 = _doc and _link.document2.id = ? )) ");
			} else if (direction.intValue() == 1)
				query.append(" _link.document1 = _doc and _link.document1.id = ? ");
			else if (direction.intValue() == 2)
				query.append(" _link.document2 = _doc and _link.document2.id = ? ");
			if (StringUtils.isNotEmpty(linkType)) {
				query.append(" and _link.type = '");
				query.append(linkType);
				query.append("'");
			}

			if (direction == null)
				coll = (Collection<Document>) getHibernateTemplate().find(query.toString(),
						new Object[] { docId, docId });
			else
				coll = (Collection<Document>) getHibernateTemplate().find(query.toString(), new Object[] { docId });

		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return coll;
	}

	@Override
	public Document findDocumentByNameAndParentFolderId(long folderId, String documentName) {
		Collection<Document> coll = null;
		try {
			StringBuffer query = new StringBuffer("select _doc.id from Document _doc where ");
			query.append("_doc.folder.id = ");
			query.append(Long.toString(folderId));

			coll = (Collection<Document>) getHibernateTemplate().find(
					"from Document _doc where _doc.folder.id = " + folderId + " and _doc.fileName='" + documentName
							+ "'");

			if (coll != null && coll.size() > 0)
				return coll.iterator().next();
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public void initialize(Document doc) {
		getHibernateTemplate().refresh(doc);
		for (Version version : doc.getVersions()) {
			version.getVersion();
		}
		for (String attribute : doc.getAttributes().keySet()) {
			attribute.getBytes();
		}
		for (String keyword : doc.getKeywords()) {
			keyword.getBytes();
		}
	}
}