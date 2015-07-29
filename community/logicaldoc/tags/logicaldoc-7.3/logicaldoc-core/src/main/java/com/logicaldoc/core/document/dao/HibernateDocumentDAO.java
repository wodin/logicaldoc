package com.logicaldoc.core.document.dao;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.ibm.icu.util.Calendar;
import com.logicaldoc.core.ExtendedAttribute;
import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.PersistentObject;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentListener;
import com.logicaldoc.core.document.DocumentListenerManager;
import com.logicaldoc.core.document.DocumentNote;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Tag;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.Tenant;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.TenantDAO;
import com.logicaldoc.core.security.dao.UserDAO;
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
@SuppressWarnings("unchecked")
public class HibernateDocumentDAO extends HibernatePersistentObjectDAO<Document> implements DocumentDAO {
	private HistoryDAO historyDAO;

	private VersionDAO versionDAO;

	private TenantDAO tenantDAO;

	private DocumentNoteDAO noteDAO;

	private FolderDAO folderDAO;

	private UserDAO userDAO;

	private DocumentLinkDAO linkDAO;

	private DocumentListenerManager listenerManager;

	private Storer storer;

	private ContextProperties config;

	private HibernateDocumentDAO() {
		super(Document.class);
		super.log = LoggerFactory.getLogger(HibernateDocumentDAO.class);
	}

	public void setListenerManager(DocumentListenerManager listenerManager) {
		this.listenerManager = listenerManager;
	}

	public void setVersionDAO(VersionDAO versionDAO) {
		this.versionDAO = versionDAO;
	}

	public void setLinkDAO(DocumentLinkDAO linkDAO) {
		this.linkDAO = linkDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setHistoryDAO(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	@Override
	public boolean archive(long docId, History transaction) {
		boolean result = true;
		try {
			Document doc = (Document) findById(docId);
			doc.setStatus(AbstractDocument.DOC_ARCHIVED);
			doc.setLockUserId(transaction.getUserId());
			transaction.setEvent(DocumentEvent.ARCHIVED.toString());
			store(doc, transaction);
			log.debug("Archived document " + docId);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	@Override
	public void unarchive(long docId, History transaction) {
		try {
			Document doc = (Document) findById(docId);
			doc.setStatus(AbstractDocument.DOC_UNLOCKED);
			doc.setLockUserId(null);
			transaction.setEvent(DocumentEvent.RESTORED.toString());
			store(doc, transaction);
			log.debug("Unarchived document " + docId);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public boolean delete(long docId, History transaction) {
		return delete(docId, PersistentObject.DELETED_CODE_DEFAULT, transaction);
	}

	@Override
	public boolean delete(long docId, int delCode, History transaction) {
		assert (delCode != 0);
		assert (transaction != null);
		assert (transaction.getUser() != null);
		boolean result = true;
		try {
			Document doc = (Document) findById(docId);
			if (doc != null && doc.getImmutable() == 0
					|| (doc != null && doc.getImmutable() == 1 && transaction.getUser().isInGroup("admin"))) {
				// Remove versions
				for (Version version : versionDAO.findByDocId(docId)) {
					version.setDeleted(1);
					saveOrUpdate(version);
				}

				// Remove notes
				for (DocumentNote note : noteDAO.findByDocId(docId)) {
					note.setDeleted(1);
					saveOrUpdate(note);
				}

				// Remove links
				for (DocumentLink link : linkDAO.findByDocId(docId)) {
					link.setDeleted(1);
					saveOrUpdate(link);
				}

				doc.setDeleted(delCode);
				doc.setDeleteUserId(transaction.getUserId());
				if (doc.getCustomId() != null)
					doc.setCustomId(doc.getCustomId() + "." + doc.getId());
				store(doc, transaction);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public List<Long> findByUserId(long userId) {
		Collection<Folder> folders = folderDAO.findByUserId(userId);
		if (folders.isEmpty())
			return new ArrayList<Long>();

		StringBuffer query = new StringBuffer();
		query.append("_entity.folder.id in (");
		boolean first = true;
		for (Folder folder : folders) {
			if (!first)
				query.append(",");
			query.append(folder.getId());
			first = false;
		}
		query.append(") and not _entity.status=" + AbstractDocument.DOC_ARCHIVED);
		return findIdsByWhere(query.toString(), null, null);
	}

	@Override
	public List<Document> findByLockUserAndStatus(Long userId, Integer status) {
		StringBuffer sb = new StringBuffer(
				"select ld_id, ld_folderid, ld_version, ld_fileversion, ld_lastmodified, ld_filename, ld_title from ld_document where ld_deleted = 0 ");
		if (userId != null)
			sb.append(" and ld_lockuserid=" + userId);

		if (status != null)
			sb.append(" and ld_status=" + status);

		return (List<Document>) query(sb.toString(), null, new RowMapper<Document>() {

			@Override
			public Document mapRow(ResultSet rs, int col) throws SQLException {
				Document doc = new Document();
				doc.setId(rs.getLong(1));
				Folder folder = new Folder();
				folder.setId(rs.getLong(2));
				doc.setFolder(folder);
				doc.setVersion(rs.getString(3));
				doc.setFileVersion(rs.getString(4));
				doc.setLastModified(rs.getDate(5));
				doc.setFileName(rs.getString(6));
				doc.setTitle(rs.getString(7));
				return doc;
			}

		}, null);
	}

	@Override
	public List<Long> findDocIdByTag(String tag) {
		StringBuilder query = new StringBuilder(
				"select distinct(A.ld_docid) from ld_tag A, ld_document B where A.ld_docid=B.ld_id and not B.ld_status="
						+ AbstractDocument.DOC_ARCHIVED);
		query.append(" and lower(ld_tag)='" + SqlUtil.doubleQuotes(tag).toLowerCase() + "'");
		return (List<Long>) queryForList(query.toString(), Long.class);
	}

	public boolean store(final Document doc) {
		return store(doc, null);
	}

	public boolean store(Document doc, final History transaction) {
		boolean result = true;
		try {
			Tenant tenant = tenantDAO.findById(doc.getTenantId());

			if (transaction != null) {
				transaction.setTenantId(doc.getTenantId());
				transaction.setTenant(tenant.getName());
			}

			// Truncate publishing dates
			if (doc.getStartPublishing() != null)
				doc.setStartPublishing(DateUtils.truncate(doc.getStartPublishing(), Calendar.DATE));
			if (doc.getStopPublishing() != null)
				doc.setStopPublishing(DateUtils.truncate(doc.getStopPublishing(), Calendar.DATE));

			// Check if the document must be indexed
			if (!FileUtil.matches(
					doc.getFileName(),
					config.getProperty(tenant.getName() + ".index.includes") == null ? "" : config.getProperty(tenant
							.getName() + ".index.includes"),
					config.getProperty(tenant.getName() + ".index.excludes") == null ? "" : config.getProperty(tenant
							.getName() + ".index.excludes")))
				doc.setIndexed(Document.INDEX_SKIP);

			// Check if the document must be barcoded
			if (!FileUtil.matches(
					doc.getFileName(),
					config.getProperty(tenant.getName() + ".barcode.includes") == null ? "" : config.getProperty(tenant
							.getName() + ".barcode.includes"),
					config.getProperty(tenant.getName() + ".barcode.excludes") == null ? "" : config.getProperty(tenant
							.getName() + ".barcode.excludes")))
				doc.setBarcoded(Document.BARCODE_SKIP);

			Set<Tag> src = doc.getTags();
			if (src != null && src.size() > 0) {
				// Trim too long tags
				Set<Tag> dst = new HashSet<Tag>();
				for (Tag str : src) {
					str.setTenantId(doc.getTenantId());
					String s = str.getTag();
					if (s != null) {
						if (s.length() > 255) {
							s = s.substring(0, 255);
							str.setTag(s);
						}
						if (!dst.contains(str))
							dst.add(str);
					}
				}
				doc.setTags(dst);
				doc.setTgs(doc.getTagsString());
			}

			/*
			 * Check for attributes defaults
			 */
			if (doc.getFolder().getTemplate() != null) {
				folderDAO.initialize(doc.getFolder());
				if (doc.getTemplate() == null || doc.getTemplate().equals(doc.getFolder().getTemplate())) {
					doc.setTemplate(doc.getFolder().getTemplate());
					try {
						for (String name : doc.getFolder().getAttributeNames()) {
							ExtendedAttribute fAtt = doc.getFolder().getExtendedAttribute(name);
							if (fAtt.getValue() == null || StringUtils.isEmpty(fAtt.getValue().toString()))
								continue;
							ExtendedAttribute dAtt = doc.getExtendedAttribute(name);
							if (dAtt == null) {
								dAtt = new ExtendedAttribute();
								dAtt.setType(fAtt.getType());
								dAtt.setEditor(fAtt.getEditor());
								dAtt.setLabel(fAtt.getLabel());
								dAtt.setMandatory(fAtt.getMandatory());
								dAtt.setPosition(fAtt.getPosition());
								doc.getAttributes().put(name, dAtt);
							}

							if (dAtt.getValue() == null || StringUtils.isEmpty(dAtt.getValue().toString())) {
								dAtt.setStringValue(fAtt.getStringValue());
								dAtt.setDateValue(fAtt.getDateValue());
								dAtt.setDoubleValue(fAtt.getDoubleValue());
								dAtt.setIntValue(fAtt.getIntValue());
							}
						}
					} catch (Throwable t) {
						// From times to times during check-in a lazy-loading
						// exception is thrown
					}
				}
			}

			if ("bulkload".equals(config.getProperty("runlevel")))
				doc.setCustomId(UUID.randomUUID().toString());

			Map<String, Object> dictionary = new HashMap<String, Object>();

			log.debug("Invoke listeners before store");
			for (DocumentListener listener : listenerManager.getListeners())
				listener.beforeStore(doc, transaction, dictionary);

			if (doc.getCustomId() == null)
				doc.setCustomId(UUID.randomUUID().toString());

			// Save the document
			saveOrUpdate(doc);
			try {
				flush();
			} catch (Throwable t) {
			}
			if (doc.getDeleted() == 0 && doc.getId() != 0L)
				refresh(doc);

			log.debug("Invoke listeners after store");
			for (DocumentListener listener : listenerManager.getListeners())
				listener.afterStore(doc, transaction, dictionary);

			if (StringUtils.isEmpty(doc.getCustomId()))
				doc.setCustomId(Long.toString(doc.getId()));

			if (!"bulkload".equals(config.getProperty("runlevel"))) {
				// Perhaps some listeners may have modified the document
				saveOrUpdate(doc);
				saveDocumentHistory(doc, transaction);
			}
		} catch (Throwable e) {
			if (transaction != null && StringUtils.isNotEmpty(transaction.getSessionId())) {
				UserSession session = SessionManager.getInstance().get(transaction.getSessionId());
				session.logError(e.getMessage());
			}
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public void updateDigest(Document doc) {
		String resource = storer.getResourceName(doc, doc.getFileVersion(), null);
		if (storer.exists(doc.getId(), resource)) {
			InputStream in = null;
			try {
				in = storer.getStream(doc.getId(), resource);
				doc.setDigest(FileUtil.computeDigest(in));
			} finally {
				if (in != null)
					try {
						in.close();
					} catch (Throwable t) {
					}
			}

			jdbcUpdate("update ld_document set ld_lastmodified=?, ld_digest=?  where ld_id=?", new Date(),
					doc.getDigest(), doc.getId());

			// Update the versions also
			jdbcUpdate("update ld_version set ld_digest=?  where ld_documentid=? and ld_fileversion=?",
					doc.getDigest(), doc.getId(), doc.getFileVersion());
		}
	}

	public List<Document> findLastModifiedByUserId(long userId, int maxElements) {
		List<Document> coll = new ArrayList<Document>();

		try {
			StringBuilder query = new StringBuilder("SELECT _history.docId from History _history");
			query.append(" WHERE _history.userId = " + Long.toString(userId) + " ");
			query.append(" ORDER BY _history.date DESC");

			List<Long> results = (List<Long>) findByQuery(query.toString(), null, null);
			for (Long docid : results) {
				if (coll.size() >= maxElements)
					break;
				if (docid != null) {
					Document document = findById(docid);
					if (folderDAO.isReadEnable(document.getFolder().getId(), userId))
						coll.add(document);
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Integer> findTags(String firstLetter, Long tenantId) {
		final Map<String, Integer> map = new HashMap<String, Integer>();

		try {
			StringBuilder query = new StringBuilder("SELECT COUNT(ld_tag), ld_tag from ld_tag where 1=1 ");
			if (StringUtils.isNotEmpty(firstLetter))
				query.append(" and lower(ld_tag) like '" + firstLetter.toLowerCase() + "%' ");
			if (tenantId != null)
				query.append(" and ld_tenantid=" + tenantId);
			query.append(" GROUP BY ld_tag");

			query(query.toString(), null, new RowMapper<Object>() {

				@Override
				public Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
					Long value = (Long) rs.getLong(1);
					String key = (String) rs.getString(2);
					map.put(key, value.intValue());
					return null;
				}

			}, null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}

	@Override
	public List<String> findAllTags(String firstLetter, Long tenantId) {
		try {
			StringBuilder sb = new StringBuilder("select distinct(ld_tag) from ld_tag where 1=1 ");
			if (tenantId != null) {
				sb.append(" and ld_tenantid=" + tenantId);
			}

			if (firstLetter != null)
				sb.append(" and lower(ld_tag) like '" + firstLetter.toLowerCase() + "%'");

			return (List<String>) queryForList(sb.toString(), String.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ArrayList<String>();
	}

	@Override
	public List<Document> findByUserIdAndTag(long userId, String tag, Integer max) {

		List<Document> coll = new ArrayList<Document>();

		List<Long> ids = findDocIdByUserIdAndTag(userId, tag);
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
			coll = (List<Document>) findByQuery(query.toString(), null, max);
		}
		return coll;
	}

	@Override
	public List<Long> findDocIdByUserIdAndTag(long userId, String tag) {
		List<Long> ids = new ArrayList<Long>();
		try {
			User user = userDAO.findById(userId);
			if (user == null)
				return ids;

			StringBuffer query = new StringBuffer();

			if (user.isInGroup("admin")) {
				ids = findDocIdByTag(tag);
			} else {

				/*
				 * Search for all accessible folders
				 */
				Collection<Long> precoll = folderDAO.findFolderIdByUserId(userId, null, true);
				String precollString = precoll.toString().replace('[', '(').replace(']', ')');

				query.append("select distinct(C.ld_id) from ld_document C, ld_tag D "
						+ " where C.ld_id=D.ld_docid AND C.ld_deleted=0 and not C.ld_status="
						+ AbstractDocument.DOC_ARCHIVED);
				query.append(" AND C.ld_folderid in ");
				query.append(precollString);
				query.append(" AND D.ld_tag='" + SqlUtil.doubleQuotes(tag.toLowerCase()) + "' ");

				List<Long> docIds = (List<Long>) queryForList(query.toString(), Long.class);
				ids.addAll(docIds);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			System.err.println(e);
		}

		return ids;
	}

	@Override
	public List<Document> findLastDownloadsByUserId(long userId, int maxResults) {
		List<Document> coll = new ArrayList<Document>();

		try {
			StringBuffer query = new StringBuffer("select docId from History ");
			query.append(" where userId = " + userId);
			query.append(" and event = '" + DocumentEvent.DOWNLOADED + "' ");
			query.append(" order by date desc");

			List<Long> results = (List<Long>) findByQuery(query.toString(), null, null);
			ArrayList<Long> tmpal = new ArrayList<Long>(results);
			List<Long> docIds = tmpal;

			System.out.println("results " + results);

			if (docIds.isEmpty())
				return coll;

			if (docIds.size() > maxResults) {
				tmpal.subList(0, maxResults - 1);
			}

			query = new StringBuffer("from Document _entity ");
			query.append(" where not _entity.status=" + AbstractDocument.DOC_ARCHIVED);
			query.append(" and _entity.id in (");

			for (int i = 0; i < docIds.size(); i++) {
				Long docId = docIds.get(i);
				if (i > 0)
					query.append(",");
				query.append(docId);
			}
			query.append(")");

			// execute the query
			List<Document> unorderdColl = (List<Document>) findByQuery(query.toString(), null, null);

			// put all elements in a map
			HashMap<Long, Document> hm = new HashMap<Long, Document>();
			for (Document doc : unorderdColl) {
				hm.put(doc.getId(), doc);
			}

			// Access the map using the folderIds
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
		String sql = "select ld_id from ld_document where ld_deleted=0 and ld_folderid = " + folderId
				+ " and not ld_status=" + AbstractDocument.DOC_ARCHIVED;
		return (List<Long>) queryForList(sql, null, Long.class, max);
	}

	@Override
	public List<Document> findByFolder(long folderId, Integer max) {
		return findByWhere("_entity.folder.id = " + Long.toString(folderId), null, max);
	}

	@Override
	public List<Document> findArchivedByFolder(long folderId) {
		return findByWhere("_entity.folder.id = " + folderId + " and _entity.status=" + AbstractDocument.DOC_ARCHIVED,
				null, null);
	}

	@Override
	public List<Document> findLinkedDocuments(long docId, String linkType, Integer direction) {
		List<Document> coll = new ArrayList<Document>();
		StringBuffer query = null;
		try {
			query = new StringBuffer("select distinct(_entity) from Document _entity, DocumentLink _link where ");
			if (direction == null) {
				query.append(" ((_link.document1 = _entity and _link.document1.id = ?1 )"
						+ "or (_link.document2 = _entity and _link.document2.id = ?1 )) ");
			} else if (direction.intValue() == 1)
				query.append(" _link.document1 = _entity and _link.document1.id = ?1 ");
			else if (direction.intValue() == 2)
				query.append(" _link.document2 = _entity and _link.document2.id = ?1 ");
			if (StringUtils.isNotEmpty(linkType)) {
				query.append(" and _link.type = '");
				query.append(linkType);
				query.append("'");
			}
			query.append(" and not _entity.status=" + AbstractDocument.DOC_ARCHIVED);

			coll = (List<Document>) findByQuery(query.toString(), new Object[] { docId }, null);

		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return coll;
	}

	@Override
	public List<Document> findByFileNameAndParentFolderId(Long folderId, String fileName, Long excludeId,
			Long tenantId, Integer max) {
		String query = "lower(_entity.fileName) like '" + SqlUtil.doubleQuotes(fileName.toLowerCase()) + "'";
		if (tenantId != null) {
			query += " and _entity.tenantId = " + tenantId;
		}
		if (folderId != null) {
			query += " and _entity.folder.id = " + folderId;
		}
		if (excludeId != null)
			query += " and not(_entity.id = " + excludeId + ")";
		query += " and not _entity.status=" + AbstractDocument.DOC_ARCHIVED;

		return findByWhere(query, null, max);
	}

	@Override
	public List<Document> findByTitleAndParentFolderId(long folderId, String title, Long excludeId) {
		String query = "_entity.folder.id = " + folderId + " and lower(_entity.title) like '"
				+ SqlUtil.doubleQuotes(title.toLowerCase()) + "'";
		if (excludeId != null)
			query += " and not(_entity.id = " + excludeId + ")";
		query += " and not _entity.status=" + AbstractDocument.DOC_ARCHIVED;

		return findByWhere(query, null, null);
	}

	@Override
	public void initialize(Document doc) {
		refresh(doc);

		if (doc.getAttributes() != null)
			for (String attribute : doc.getAttributes().keySet()) {
				if (attribute != null)
					attribute.getBytes();
			}

		if (doc.getTags() != null)
			for (Tag tag : doc.getTags()) {
				if (tag.getTag() != null)
					tag.getTag().getBytes();
			}
	}

	@Override
	public List<Long> findDeletedDocIds() {
		String query = "select ld_id from ld_document where ld_deleted=1 order by ld_lastmodified desc";
		return (List<Long>) queryForList(query, Long.class);
	}

	@Override
	public List<Document> findDeletedDocs() {
		List<Document> coll = new ArrayList<Document>();
		try {
			String query = "select ld_id, ld_customid, ld_lastModified, ld_title from ld_document where ld_deleted=1 order by ld_lastmodified desc";

			@SuppressWarnings("rawtypes")
			RowMapper docMapper = new BeanPropertyRowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

					Document doc = new Document();
					doc.setId(rs.getLong(1));
					doc.setCustomId(rs.getString(2));
					doc.setLastModified(rs.getDate(3));
					doc.setTitle(rs.getString(4));

					return doc;
				}
			};

			coll = (List<Document>) query(query, new Object[] {}, docMapper, null);

		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@Override
	public long getTotalSize(boolean computeDeleted) {
		String query = "select sum(ld_filesize) from ld_document ";
		if (!computeDeleted) {
			query += " where ld_deleted=0";
		}
		return queryForLong(query);
	}

	@Override
	public long count(Long tenantId, boolean computeDeleted, boolean computeArchived) {
		String query = "select count(*) from ld_document where 1=1 ";
		if (!computeDeleted)
			query += " and ld_deleted = 0 ";
		if (!computeArchived)
			query += " and not ld_status = " + AbstractDocument.DOC_ARCHIVED;
		if (tenantId != null)
			query += " and ld_tenantid = " + tenantId;
		return queryForLong(query);
	}

	@Override
	public List<Document> findByIndexed(int indexed) {
		return findByWhere("_entity.docRef is null and _entity.indexed=" + indexed,
				"order by _entity.lastModified asc", null);
	}

	@Override
	public void restore(long docId, long folderId, final History transaction) {
		bulkUpdate("set ld_deleted=0, ld_folderid=" + folderId + ", ld_lastmodified=CURRENT_TIMESTAMP where ld_id="
				+ docId, null);

		Document doc = findById(docId);
		if (doc != null && transaction != null) {
			transaction.setDocId(docId);
			transaction.setEvent(DocumentEvent.RESTORED.toString());
			saveDocumentHistory(doc, transaction);
		}
	}

	@Override
	public Document findByCustomId(String customId, long tenantId) {
		Document doc = null;
		if (customId != null)
			try {
				String query = "_entity.customId = '" + SqlUtil.doubleQuotes(customId) + "' "
						+ " and _entity.tenantId=" + tenantId;
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

	public void deleteAll(Collection<Document> documents, History transaction) {
		deleteAll(documents, PersistentObject.DELETED_CODE_DEFAULT, transaction);
	}

	@Override
	public void deleteAll(Collection<Document> documents, int delCode, History transaction) {
		for (Document document : documents) {
			try {
				History deleteHistory = (History) transaction.clone();
				deleteHistory.setEvent(DocumentEvent.DELETED.toString());
				delete(document.getId(), delCode, deleteHistory);
			} catch (CloneNotSupportedException e) {
				if (log.isErrorEnabled())
					log.error(e.getMessage(), e);
			}
		}

	}

	public void setNoteDAO(DocumentNoteDAO noteDAO) {
		this.noteDAO = noteDAO;
	}

	public void setStorer(Storer storer) {
		this.storer = storer;
	}

	@Override
	public void saveDocumentHistory(Document doc, History transaction) {
		if (transaction == null || !historyDAO.isEnabled() || "bulkload".equals(config.getProperty("runlevel")))
			return;

		transaction.setTenantId(doc.getTenantId());
		transaction.setDocId(doc.getId());
		transaction.setFolderId(doc.getFolder().getId());
		transaction.setTitle(doc.getTitle());
		transaction.setVersion(doc.getVersion());
		transaction.setFilename(doc.getFileName());
		transaction.setPath(folderDAO.computePathExtended(doc.getFolder().getId()));
		transaction.setNotified(0);

		historyDAO.store(transaction);
	}

	@Override
	public long countByIndexed(int indexed) {
		String query = "select count(*) from ld_document where ld_deleted=0 and ld_indexed = " + indexed;
		return queryForLong(query);
	}

	@Override
	public List<Long> findAliasIds(long docId) {
		return findIdsByWhere("_entity.docRef = " + Long.toString(docId), null, null);
	}

	@Override
	public List<Document> findDeleted(long userId, Integer maxHits) {
		List<Document> results = new ArrayList<Document>();
		try {
			String query = "select ld_id, ld_title, ld_lastmodified, ld_filename, ld_customid, ld_folderid, ld_tenantid from ld_document where ld_deleted=1 and ld_deleteuserid = "
					+ userId + " order by ld_lastmodified desc";

			@SuppressWarnings("rawtypes")
			RowMapper docMapper = new BeanPropertyRowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					Document doc = new Document();
					doc.setId(rs.getLong(1));
					doc.setTitle(rs.getString(2));
					doc.setLastModified(rs.getDate(3));
					doc.setFileName(rs.getString(4));
					doc.setCustomId(rs.getString(5));
					doc.setTenantId(rs.getLong(6));

					Folder folder = new Folder();
					folder.setId(rs.getLong(6));
					doc.setFolder(folder);

					return doc;
				}
			};

			results = (List<Document>) query(query, null, docMapper, maxHits);

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return results;
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}

	public void setFolderDAO(FolderDAO folderDAO) {
		this.folderDAO = folderDAO;
	}

	@Override
	public List<Document> findByIds(Long[] ids, Integer max) {
		List<Document> docs = new ArrayList<Document>();
		if (ids.length < 1)
			return docs;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {
			if (i > 0)
				sb.append(",");
			sb.append(ids[i]);
		}

		docs = findByWhere("_entity.id in(" + sb.toString() + ")", null, max);
		return docs;
	}

	@Override
	public boolean deleteOrphaned(long deleteUserId) {
		try {
			String dbms = config.getProperty("jdbc.dbms") != null ? config.getProperty("jdbc.dbms").toLowerCase()
					: "mysql";

			String concat = "CONCAT(ld_id,CONCAT('.',ld_customid))";
			if (dbms.contains("postgre"))
				concat = "ld_id || '.' || ld_customid";
			if (dbms.contains("mssql"))
				concat = "CAST(ld_id AS varchar) + '.' + ld_customid";

			jdbcUpdate("update ld_document set ld_deleted=1,ld_customid=" + concat + ", ld_deleteuserid="
					+ deleteUserId
					+ " where ld_deleted=0 and ld_folderid in (select ld_id from ld_folder where ld_deleted  > 0)");
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	@Override
	public Collection<Long> findPublishedIds(Collection<Long> folderIds) {
		StringBuffer query = new StringBuffer("select ld_id from ld_document where ld_deleted=0 and not ld_status="
				+ AbstractDocument.DOC_ARCHIVED);
		if (folderIds != null && !folderIds.isEmpty()) {
			query.append(" and ld_folderid in (");
			query.append(folderIds.toString().replace('[', ' ').replace(']', ' '));
			query.append(" ) ");
		}
		query.append(" and ld_published = 1 ");
		query.append(" and ld_startpublishing <= ? ");
		query.append(" and ( ld_stoppublishing is null or ld_stoppublishing > ? )");

		Date now = new Date();

		Collection<Long> buf = (Collection<Long>) queryForList(query.toString(), new Object[] { now, now }, Long.class,
				null);
		Set<Long> ids = new HashSet<Long>();
		for (Long id : buf) {
			if (!ids.contains(id))
				ids.add(id);
		}
		return buf;
	}

	@Override
	public void cleanExpiredTransactions() {
		jdbcUpdate(
				"update ld_document set ld_transactionid=null where not (ld_transactionid is null) and not exists(select B.ld_id from ld_generic B where B.ld_type='lock' and B.ld_string1=ld_transactionid)",
				new Object[0]);
	}

	public void setTenantDAO(TenantDAO tenantDAO) {
		this.tenantDAO = tenantDAO;
	}
}