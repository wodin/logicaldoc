package com.logicaldoc.core.document.dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.History;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>HistoryDAO</code>
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class HibernateHistoryDAO extends HibernatePersistentObjectDAO<History> implements HistoryDAO {

	private HibernateHistoryDAO() {
		super(History.class);
		super.log = LogFactory.getLog(HibernateHistoryDAO.class);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByDocId(long)
	 */
	public List<History> findByDocId(long docId) {
		return findByWhere("_entity.docId =" + docId, null, "order by _entity.date asc", null);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByUserId(long)
	 */
	public List<History> findByUserId(long userId) {
		return findByUserIdAndEvent(userId, null);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByFolderId(long)
	 */
	public List<History> findByFolderId(long folderId) {
		return findByWhere("_entity.folderId =" + folderId, null, "order by _entity.date asc", null);
	}

	@Override
	public List<History> findNotNotified() {
		return findByWhere("_entity.notified = 0", null, "order by _entity.date asc", null);
	}

	
	/**
	 * @see com.logicaldoc.core.security.dao.UserHistoryDAO#cleanOldHistories(int)
	 */
	@Override
	public void cleanOldDocumentHistories(int ttl) {
		if (ttl > 0) {
			Date today = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -ttl);
			Date ldDate = cal.getTime();

			log.debug("today: " + today);
			log.debug("ldDate: " + ldDate);

			try {
				int rowsUpdated = jdbcUpdate("UPDATE ld_history SET ld_deleted = 1, ld_lastmodified = ?"
						+ " WHERE ld_deleted = 0 AND ld_docid > 0 AND ld_date < ?", today, ldDate);
				log.info("rows updated: " + rowsUpdated);
			} catch (Exception e) {
				if (log.isErrorEnabled())
					log.error(e.getMessage(), e);
			}

		}
	}


	@Override
	public void cleanOldFolderHistories(int ttl) {
		if (ttl > 0) {
			Date today = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -ttl);
			Date ldDate = cal.getTime();

			log.debug("today: " + today);
			log.debug("ldDate: " + ldDate);

			try {
				int rowsUpdated = jdbcUpdate("UPDATE ld_history SET ld_deleted = 1, ld_lastmodified = ?"
						+ " WHERE ld_deleted = 0 AND ld_docid is null AND ld_date < ?", today, ldDate);
				log.info("rows updated: " + rowsUpdated);
			} catch (Exception e) {
				if (log.isErrorEnabled())
					log.error(e.getMessage(), e);
			}

		}
	}
	
	@Override
	public List<History> findByUserIdAndEvent(long userId, String event) {
		String query = "_entity.userId =" + userId;
		if (event != null && StringUtils.isNotEmpty(event))
			query += " and lower(_entity.event) like '" + SqlUtil.doubleQuotes(event.toLowerCase()) + "'";

		return findByWhere(query, null, "order by _entity.date asc", null);
	}
}