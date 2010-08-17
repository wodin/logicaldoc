package com.logicaldoc.core.document.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.DiscussionThread;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentListener;
import com.logicaldoc.core.document.DocumentListenerManager;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.core.security.dao.UserDocDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.io.FileUtil;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>DocumentDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDocumentDAO extends HibernatePersistentObjectDAO<Document> implements DocumentDAO {
	private HistoryDAO historyDAO;

	private VersionDAO versionDAO;

	private DiscussionThreadDAO discussionDAO;

	private MenuDAO menuDAO;

	private UserDAO userDAO;

	private UserDocDAO userDocDAO;

	private DocumentLinkDAO linkDAO;

	private DocumentListenerManager listenerManager;

	private Storer storer;

	private ContextProperties config;

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

	public void setLinkDAO(DocumentLinkDAO linkDAO) {
		this.linkDAO = linkDAO;
	}

	public MenuDAO getMenuDAO() {
		return menuDAO;
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	public boolean delete(long docId, History transaction) {
		assert (transaction != null);
		assert (transaction.getUser() != null);
		boolean result = true;
		try {
			Document doc = (Document) getHibernateTemplate().get(Document.class, docId);
			if (doc != null && doc.getImmutable() == 0
					|| (doc != null && doc.getImmutable() == 1 && transaction.getUser().isInGroup("admin"))) {
				// Remove versions
				for (Version version : versionDAO.findByDocId(docId)) {
					version.setDeleted(1);
					getHibernateTemplate().saveOrUpdate(version);
				}

				// Remove discussions
				for (DiscussionThread discussion : discussionDAO.findByDocId(docId)) {
					discussion.setDeleted(1);
					getHibernateTemplate().saveOrUpdate(discussion);
				}

				// Remove links
				for (DocumentLink link : linkDAO.findByDocId(docId)) {
					link.setDeleted(1);
					getHibernateTemplate().saveOrUpdate(link);
				}

				userDocDAO.deleteByDocId(docId);

				doc.setDeleted(1);
				doc.setDeleteUserId(transaction.getUserId());
				if (doc.getCustomId() != null)
					doc.setCustomId(doc.getCustomId() + "." + doc.getId());
				store(doc, transaction);
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
		return findIdsByWhere(query.toString(), null, null);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findLockedByUserId(java.lang.String)
	 */
	public List<Document> findLockedByUserId(long userId) {
		return findByWhere("_entity.lockUserId = " + userId + " and not(_entity.status=" + Document.DOC_UNLOCKED + ")",
				null, null);
	}

	@Override
	public List<Document> findByLockUserAndStatus(Long userId, Integer status) {
		StringBuffer sb = new StringBuffer();
		if (userId != null)
			sb.append(" _entity.lockUserId =" + userId);
		else
			sb.append(" 1=1 ");
		if (status != null)
			sb.append(" and _entity.status=" + status);
		return findByWhere(sb.toString(), null, null);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentDAO#findDocIdByTag(java.lang.String)
	 */
	public List<Long> findDocIdByTag(String tag) {
		StringBuilder query = new StringBuilder();
		query.append("'" + SqlUtil.doubleQuotes(tag) + "'");
		query.append(" in elements(_entity.tags) ");
		return findIdsByWhere(query.toString(), null, null);
	}

	public boolean store(final Document doc) {
		return store(doc, null);
	}

	public boolean store(final Document doc, final History transaction) {
		boolean result = true;
		try {
			Set<String> src = doc.getTags();
			if (src != null && src.size() > 0) {
				// Trim too long tags
				Set<String> dst = new HashSet<String>();
				for (String str : src) {
					String s = str;
					if (str.length() > 255) {
						s = str.substring(0, 255);
					}
					if (!dst.contains(s))
						dst.add(s);
				}
				doc.setTags(dst);
			}

			Map<String, Object> dictionary = new HashMap<String, Object>();

			log.debug("Invoke listeners before store");
			for (DocumentListener listener : listenerManager.getListeners()) {
				listener.beforeStore(doc, transaction, dictionary);
			}

			if (doc.getId() == 0) {
				// This is the first creation so check if it is indexable
				if (!FileUtil.matches(doc.getFileName(), config.getProperty("index.includes") == null ? "" : config
						.getProperty("index.includes"), config.getProperty("index.excludes") == null ? "" : config
						.getProperty("index.excludes")))
					doc.setIndexed(Document.INDEX_SKIP);
			}

			// Save the document
			getHibernateTemplate().saveOrUpdate(doc);

			// Update size and digest
			File docFile = storer.getFile(doc, doc.getFileVersion(), null);
			if (docFile.exists()) {
				long size = docFile.length();
				doc.setFileSize(size);
				doc.setDigest(FileUtil.computeDigest(docFile));
				getHibernateTemplate().saveOrUpdate(doc);
			}

			log.debug("Invoke listeners after store");
			for (DocumentListener listener : listenerManager.getListeners()) {
				listener.afterStore(doc, transaction, dictionary);
			}

			if (StringUtils.isEmpty(doc.getCustomId()))
				doc.setCustomId(Long.toString(doc.getId()));

			// Perhaps some listener may have modified the document
			getHibernateTemplate().saveOrUpdate(doc);

			saveDocumentHistory(doc, transaction);
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
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
	@Override
	public Map<String, Integer> findTags(String firstLetter) {
		Map<String, Integer> map = new HashMap<String, Integer>();

		try {
			StringBuilder query = new StringBuilder("SELECT COUNT(tag), tag");
			query.append(" FROM Document _entity JOIN _entity.tags tag ");
			if (StringUtils.isNotEmpty(firstLetter))
				query.append(" where lower(tag) like '" + firstLetter.toLowerCase() + "%'");
			query.append(" GROUP BY tag");

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

	@Override
	public List<Object> findAllTags(String firstLetter) {
		try {
			StringBuilder query = new StringBuilder("select distinct(A.ld_tag) from ld_tag A ");
			if (StringUtils.isNotEmpty(firstLetter))
				query.append("  where lower(ld_tag) like '" + firstLetter.toLowerCase() + "%'");
			return super.findByJdbcQuery(query.toString(), 1, null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ArrayList<Object>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Document> findByUserIdAndTag(long userId, String tag, Integer max) {
		List<Document> coll = new ArrayList<Document>();
		Set<Long> ids = findDocIdByUserIdAndTag(userId, tag);
		StringBuffer buf = new StringBuffer();
		if (!ids.isEmpty()) {
			boolean first = true;
			for (Long id : ids) {
				if (!first)
					buf.append(",");
				buf.append(id);
				first = false;
			}

			StringBuffer query = new StringBuffer("select A from Document A where A.id in (");
			query.append(buf);
			query.append(")");
			coll = (List<Document>) getHibernateTemplate(max).find(query.toString());
		}
		return coll;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Set<Long> findDocIdByUserIdAndTag(long userId, String tag) {
		Set<Long> ids = new HashSet<Long>();
		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return ids;

			StringBuffer query = new StringBuffer();

			if (user.isInGroup("admin")) {
				query.append("select distinct(C.ld_id) from ld_document C, ld_tag D "
						+ " where (C.ld_id=D.ld_docid OR C.ld_docref=D.ld_docid) AND C.ld_deleted=0");
				query.append(" AND lower(D.ld_tag)='" + SqlUtil.doubleQuotes(tag.toLowerCase()) + "'");
			} else {

				/*
				 * Search for all accessible folders
				 */
				List<Long> precoll = menuDAO.findMenuIdByUserId(userId);
				StringBuffer buf = new StringBuffer();
				boolean first = true;
				for (Long id : precoll) {
					if (!first)
						buf.append(",");
					buf.append(id);
					first = false;
				}

				query
						.append("select distinct(C.ld_id) from ld_document C, ld_tag D "
								+ " where (C.ld_id=D.ld_docid OR C.ld_docref=D.ld_docid) AND C.ld_deleted=0 AND C.ld_folderid in (");
				query.append(buf.toString());
				query.append(") ");
				query.append(" AND lower(D.ld_tag)='" + SqlUtil.doubleQuotes(tag.toLowerCase()) + "' ");
			}

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

	@Override
	public List<Long> findDocIdByFolder(long folderId, Integer max) {
		return findIdsByWhere("_entity.folder.id = " + Long.toString(folderId), null, max);
	}

	@Override
	public List<Document> findByFolder(long folderId, Integer max) {
		return findByWhere("_entity.folder.id = " + Long.toString(folderId), null, max);
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

	@Override
	public List<Document> findByFileNameAndParentFolderId(Long folderId, String fileName, Long excludeId, Integer max) {
		String query = "lower(_entity.fileName) like '" + SqlUtil.doubleQuotes(fileName.toLowerCase()) + "'";
		if (folderId != null) {
			query += "and _entity.folder.id = " + folderId;
		}
		if (excludeId != null)
			query += " and not(_entity.id = " + excludeId + ")";
		return findByWhere(query, null, max);
	}

	@Override
	public List<Document> findByTitleAndParentFolderId(long folderId, String title, Long excludeId) {
		String query = "_entity.folder.id = " + folderId + " and lower(_entity.title) like '"
				+ SqlUtil.doubleQuotes(title.toLowerCase()) + "'";
		if (excludeId != null)
			query += " and not(_entity.id = " + excludeId + ")";
		return findByWhere(query, null, null);
	}

	@Override
	public void initialize(Document doc) {
		getHibernateTemplate().refresh(doc);

		for (String attribute : doc.getAttributes().keySet()) {
			attribute.getBytes();
		}
		for (String tag : doc.getTags()) {
			tag.getBytes();
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
	public long count(boolean computeDeleted) {
		long count = 0;
		try {
			String query = "select count(*) from ld_document A  where ";
			// For performance issues on InnoDB tables, we always use the where
			// clause
			if (!computeDeleted) {
				query += " A.ld_deleted=0";
			} else {
				query += " A.ld_deleted>=0";
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
		return findByWhere("_entity.indexed=" + indexed, "order by _entity.lastModified asc", null);
	}

	@Override
	public void restore(long docId) {
		super.bulkUpdate("set ld_deleted=0 where ld_id=" + docId, null);
		List<Object> folders = super.findByJdbcQuery("select ld_folderid from ld_document where ld_id=" + docId, 1,
				null);
		for (Object id : folders) {
			menuDAO.restore((Long) id, true);
		}
	}

	@Override
	public Document findByCustomId(String customId) {
		Document doc = null;
		try {
			String query = "_entity.customId = '" + SqlUtil.doubleQuotes(customId) + "'";
			List<Document> coll = findByWhere(query, null, null);
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
	public void makeImmutable(long docId, History transaction) {
		Document doc = null;
		try {
			doc = findById(docId);
			initialize(doc);
			doc.setImmutable(1);
			doc.setStatus(Document.DOC_UNLOCKED);
			store(doc, transaction);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

	}

	@Override
	public void deleteAll(Collection<Document> documents, History transaction) {
		for (Document document : documents) {
			try {
				History deleteHistory = (History) transaction.clone();
				deleteHistory.setEvent(History.EVENT_DELETED);
				delete(document.getId(), deleteHistory);
			} catch (CloneNotSupportedException e) {
				if (log.isErrorEnabled())
					log.error(e.getMessage(), e);
			}
		}

	}

	public void setDiscussionDAO(DiscussionThreadDAO discussionDAO) {
		this.discussionDAO = discussionDAO;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}

	private void saveDocumentHistory(Document doc, History transaction) {
		if (transaction == null)
			return;
		transaction.setDocId(doc.getId());
		transaction.setFolderId(doc.getFolder().getId());
		transaction.setTitle(doc.getTitle());
		transaction.setVersion(doc.getVersion());
		transaction.setFilename(doc.getFileName());
		transaction.setPath(menuDAO.computePathExtended(doc.getFolder().getId()));
		transaction.setNotified(0);

		historyDAO.store(transaction);
	}

	@Override
	public long countByIndexed(int indexed) {
		long count = 0;
		try {
			String query = "select count(*) from ld_document A where A.ld_deleted=0 ";
			query += " and A.ld_indexed=" + indexed;

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
	public List<Long> findShortcutIds(long docId) {
		return findIdsByWhere("_entity.docRef = " + Long.toString(docId), null, null);
	}

	@Override
	public List<Document> findDeleted(long userId, Integer maxHits) {
		List<Document> results = new ArrayList<Document>();
		try {
			List<Object> result = findByJdbcQuery(
					"select A.ld_id,A.ld_title,A.ld_lastmodified,A.ld_filename,A.ld_folderid from ld_document as A, ld_menu as B where A.ld_folderid=B.ld_id and B.ld_deleted=0 and A.ld_deleted=1 and A.ld_deleteuserid = "
							+ userId + " order by A.ld_lastmodified desc", 5, null);

			int i = 0;
			for (Object object : result) {
				if (i >= maxHits.intValue())
					break;
				Object[] record = (Object[]) object;

				Document docDeleted = new Document();
				// Id
				docDeleted.setId((Long) record[0]);
				// Title
				docDeleted.setTitle((String) record[1]);
				// Last modified
				docDeleted.setLastModified(new Date(((Timestamp) record[2]).getTime()));
				// File name
				docDeleted.setFileName((String) record[3]);

				Menu folder = new Menu();
				folder.setId((Long) record[4]);
				docDeleted.setFolder(folder);

				// Add the document to the List
				results.add(docDeleted);
				i++;
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return results;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}
}