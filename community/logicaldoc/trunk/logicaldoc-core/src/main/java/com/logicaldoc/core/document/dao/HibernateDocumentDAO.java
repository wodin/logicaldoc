package com.logicaldoc.core.document.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.Article;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentListener;
import com.logicaldoc.core.document.DocumentListenerManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.util.config.SettingsConfig;
import com.logicaldoc.util.io.FileUtil;

/**
 * Hibernate implementation of <code>DocumentDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDocumentDAO extends HibernatePersistentObjectDAO<Document> implements DocumentDAO {

	private ArticleDAO articleDAO;

	private HistoryDAO historyDAO;

	private VersionDAO versionDAO;

	private MenuDAO menuDAO;

	private UserDAO userDAO;

	private UserDocDAO userDocDAO;

	private DocumentLinkDAO linkDAO;

	private SettingsConfig settings;

	private DocumentListenerManager listenerManager;

	private HibernateDocumentDAO() {
		super(Document.class);
		super.log = LogFactory.getLog(HibernateDocumentDAO.class);
	}

	public void setListenerManager(DocumentListenerManager listenerManager) {
		this.listenerManager = listenerManager;
	}

	public void setUserDocDAO(UserDocDAO userDocDAO) {
		this.userDocDAO = userDocDAO;
	}

	public void setVersionDAO(VersionDAO versionDAO) {
		this.versionDAO = versionDAO;
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

	public boolean delete(long docId) {
		boolean result = true;
		try {
			Document doc = (Document) getHibernateTemplate().get(Document.class, docId);
			if (doc != null && doc.getImmutable() == 0) {
				// Remove versions
				for (Version version : versionDAO.findByDocId(docId)) {
					version.setDeleted(1);
					getHibernateTemplate().saveOrUpdate(version);
				}
				
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
				store(doc);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findByUserId(long)
	 */
	@SuppressWarnings("unchecked")
	public List<Long> findByUserId(long userId) {
		Collection<Menu> menus = menuDAO.findByUserId(userId);
		if (menus.isEmpty())
			return new ArrayList<Long>();

		StringBuffer query = new StringBuffer();
		query.append("_entity.folder.id in (");
		boolean first = true;
		for (Menu menu : menus) {
			if (!first)
				query.append(",");
			query.append("'" + menu.getId() + "'");
			first = false;
		}
		query.append(")");
		return findIdsByWhere(query.toString());
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findCheckoutByUserId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Document> findCheckoutByUserId(long userId) {
		return findByWhere("_entity.checkoutUserId = " + userId);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findDocIdByKeyword(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Long> findDocIdByKeyword(String keyword) {
		StringBuilder query = new StringBuilder();
		query.append("'" + keyword + "'");
		query.append(" in elements(_entity.keywords) ");
		return findIdsByWhere(query.toString());
	}

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

			Map<String, Object> dictionary = new HashMap<String, Object>();

			log.debug("Invoke listeners before store");
			for (DocumentListener listener : listenerManager.getListeners()) {
				listener.beforeStore(doc, dictionary);
			}

			// Save the document
			getHibernateTemplate().saveOrUpdate(doc);

			// Update size and digest
			File docFile = new File(
					(settings.getValue("docdir") + "/" + doc.getPath() + "/doc_" + doc.getId() + "/" + doc.getVersion()));
			if (docFile.exists()) {
				long size = docFile.length();
				doc.setFileSize(size);
				doc.setDigest(FileUtil.computeDigest(docFile));
				getHibernateTemplate().saveOrUpdate(doc);
			}

			log.debug("Invoke listeners after store");
			for (DocumentListener listener : listenerManager.getListeners()) {
				listener.afterStore(doc, dictionary);
			}

			// Perhaps some listener may have modified the document
			getHibernateTemplate().saveOrUpdate(doc);
		} catch (Throwable e) {
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
	public List<String> findKeywords(String firstLetter, long userId) {
		List<String> coll = new ArrayList<String>();

		try {
			User user = userDAO.findById(userId);
			Collection<Group> precoll = user.getGroups();
			Iterator iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select distinct B.ld_keyword, A.ld_id from ld_document A, ld_keyword B, ld_menugroup C "
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
			e.printStackTrace();
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

			List<Long> results = (List<Long>) getHibernateTemplate().find(query.toString());
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
			query.append(" FROM Document _entity JOIN _entity.keywords keyword GROUP BY keyword");

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
	public List<Document> findByUserIdAndKeyword(long userId, String keyword) {
		List<Document> coll = new ArrayList<Document>();
		try {
			User user = userDAO.findById(userId);
			Collection<Group> precoll = user.getGroups();
			Iterator<Group> iter = precoll.iterator();
			if (precoll.isEmpty())
				return coll;

			StringBuffer query = new StringBuffer("select distinct(_entity) from Document _entity  ");
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

			Set<Long> ids = findDocIdByUserIdAndKeyword(userId, keyword);
			Iterator<Long> iter2 = ids.iterator();
			if (ids.isEmpty())
				return coll;
			query.append("and _entity.id in (");
			first = true;
			while (iter2.hasNext()) {
				if (!first)
					query.append(",");
				query.append("'" + iter.next() + "'");
				first = false;
			}
			query.append(")");
			coll = (List<Document>) getHibernateTemplate().find(query.toString());
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
			if (user == null)
				return ids;
			Collection<Group> precoll = user.getGroups();
			Iterator<Group> iter = precoll.iterator();

			if (!precoll.isEmpty()) {
				StringBuffer query = new StringBuffer(
						"select distinct(C.ld_id) from ld_menugroup A, ld_document C, ld_keyword D "
								+ " where A.ld_menuid=C.ld_folderid AND C.ld_id=D.ld_docid AND C.ld_deleted=0 AND A.ld_groupid in (");
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

			List<Long> results = (List<Long>) getHibernateTemplate().find(query.toString(), userId);
			ArrayList<Long> tmpal = new ArrayList<Long>(results);
			List<Long> docIds = tmpal;

			if (docIds.isEmpty())
				return coll;

			if (docIds.size() > maxResults) {
				tmpal.subList(0, maxResults - 1);
			}

			query = new StringBuffer("from Document _entity  ");
			query.append(" where _entity.id in (");

			for (int i = 0; i < docIds.size(); i++) {
				Long docId = docIds.get(i);
				if (i > 0)
					query.append(",");
				query.append(docId);
			}
			query.append(")");

			// execute the query
			List<Document> unorderdColl = (List<Document>) getHibernateTemplate().find(query.toString());

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
	public List<Long> findDocIdByFolder(long folderId) {
		return findIdsByWhere("_entity.folder.id = " + Long.toString(folderId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Document> findByFolder(long folderId) {
		return findByWhere("_entity.folder.id = " + Long.toString(folderId));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Document> findLinkedDocuments(long docId, String linkType, Integer direction) {
		List<Document> coll = new ArrayList<Document>();
		StringBuffer query = null;
		try {
			query = new StringBuffer("select distinct(_entity) from Document _entity, DocumentLink _link where ");
			if (direction == null) {
				query.append(" ((_link.document1 = _entity and _link.document1.id = ? )"
						+ "or (_link.document2 = _entity and _link.document2.id = ? )) ");
			} else if (direction.intValue() == 1)
				query.append(" _link.document1 = _entity and _link.document1.id = ? ");
			else if (direction.intValue() == 2)
				query.append(" _link.document2 = _entity and _link.document2.id = ? ");
			if (StringUtils.isNotEmpty(linkType)) {
				query.append(" and _link.type = '");
				query.append(linkType);
				query.append("'");
			}

			if (direction == null)
				coll = (List<Document>) getHibernateTemplate().find(query.toString(), new Object[] { docId, docId });
			else
				coll = (List<Document>) getHibernateTemplate().find(query.toString(), new Object[] { docId });

		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Document> findByFileNameAndParentFolderId(long folderId, String fileName, Long excludeId) {
		String query = "_entity.folder.id = " + folderId + " and lower(_entity.fileName) like '"
				+ fileName.toLowerCase() + "'";
		if (excludeId != null)
			query += " and not(_entity.id = " + excludeId + ")";
		return findByWhere(query);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Document> findByTitleAndParentFolderId(long folderId, String title, Long excludeId) {
		String query = "_entity.folder.id = " + folderId + " and lower(_entity.title) like '" + title.toLowerCase()
				+ "'";
		if (excludeId != null)
			query += " and not(_entity.id = " + excludeId + ")";
		return findByWhere(query);
	}

	@Override
	public void initialize(Document doc) {
		getHibernateTemplate().refresh(doc);

		for (String attribute : doc.getAttributes().keySet()) {
			attribute.getBytes();
		}
		for (String keyword : doc.getKeywords()) {
			keyword.getBytes();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Long> findDeletedDocIds() {
		List<Long> coll = new ArrayList<Long>();
		try {
			String query = "select A.ld_id from ld_document A where A.ld_deleted=1 order by A.ld_lastmodified desc";
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = getSession().connection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(query.toString());
				while (rs.next()) {
					coll.add(rs.getLong(1));
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Document> findDeletedDocs() {
		List<Document> coll = new ArrayList<Document>();
		try {
			String query = "select A.ld_id, A.ld_customid, A.ld_lastModified, A.ld_title from ld_document A where A.ld_deleted=1 order by A.ld_lastmodified desc";
			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				con = getSession().connection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(query.toString());
				while (rs.next()) {
					Document doc = new Document();
					doc.setId(rs.getLong(1));
					doc.setCustomId(rs.getString(2));
					doc.setLastModified(rs.getDate(3));
					doc.setTitle(rs.getString(4));
					coll.add(doc);
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@SuppressWarnings("deprecation")
	@Override
	public long getTotalSize(boolean computeDeleted) {
		long size = 0;
		try {
			String query = "select sum(A.ld_filesize) from ld_document A ";
			if (!computeDeleted) {
				query += " where A.ld_deleted=0";
			}

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = getSession().connection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(query.toString());
				while (rs.next()) {
					size = rs.getLong(1);
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return size;
	}

	@SuppressWarnings("deprecation")
	@Override
	public long getDocumentCount(boolean computeDeleted) {
		long count = 0;
		try {
			String query = "select count(*) from ld_document A ";
			if (!computeDeleted) {
				query += " where A.ld_deleted=0";
			}

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = getSession().connection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(query.toString());
				while (rs.next()) {
					count = rs.getLong(1);
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return count;
	}

	@Override
	public List<Document> findByIndexed(int indexed) {
		StringBuffer query = new StringBuffer();
		query.append("_entity.indexed=" + indexed);
		query.append(" order by _entity.lastModified asc ");
		return findByWhere(query.toString());
	}

	@SuppressWarnings("deprecation")
	@Override
	public void restore(long docId) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = getSession().connection();
			StringBuffer query = new StringBuffer("update ld_document _entity  ");
			query.append(" set _entity.ld_deleted=0 ");
			query.append(" where _entity.ld_id = ?");
			stmt = con.prepareStatement(query.toString());

			// Restore the document
			stmt.setLong(1, docId);
			stmt.execute();
			stmt.close();

			ResultSet rs = con.createStatement().executeQuery(
					"select ld_folderid from ld_document where ld_id=" + docId);
			rs.next();
			menuDAO.restore(rs.getLong(1), true);
			rs.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public Document findByCustomId(String customId) {
		Document doc = null;
		try {
			String query = "_entity.customId = '" + customId + "'";
			List<Document> coll = findByWhere(query);
			if (!coll.isEmpty()) {
				doc = coll.get(0);
				if (doc.getDeleted() == 1)
					doc = null;
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return doc;
	}

	@Override
	public void makeImmutable(long docId) {
		Document doc = null;
		try {
			doc = findById(docId);
			initialize(doc);
			doc.setImmutable(1);
			doc.setStatus(Document.DOC_CHECKED_IN);
			store(doc);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

	}
}