package com.logicaldoc.core.document.dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.History;

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
		return findByWhere("_entity.docId =" + docId, null, "order by _entity.date asc");
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByUserId(long)
	 */
	public List<History> findByUserId(long userId) {
		return findByWhere("_entity.userId =" + userId, null, "order by _entity.date asc");
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByFolderId(long)
	 */
	public List<History> findByFolderId(long folderId) {
		return findByWhere("_entity.folderId =" + folderId, null, "order by _entity.date asc");
	}

	@Override
	public List<History> findNotNotified() {
		return findByWhere("_entity.notified = 0", null, "order by _entity.date asc");
	}

	@Override
	public void cleanOldDocumentHistories(int ttl) {
		if (ttl > 0) {
			Date date = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, -ttl);
			date = cal.getTime();
			// Retrieve all old user histories
			List<Object> histories = super.findByJdbcQuery(
					"select ld_id from ld_history where ld_deleted = 0 and ld_docid > 0 and ld_date < '"
							+ new Timestamp(date.getTime()) + "'", 1, null);
			for (Object id : histories) {
				Long historyId = (Long) id;
				super.bulkUpdate("set ld_deleted = 1 where ld_id = " + historyId, null);
			}
		}
	}

	@Override
	public void cleanOldFolderHistories(int ttl) {
		if (ttl > 0) {
			Date date = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, -ttl);
			date = cal.getTime();
			// Retrieve all old user histories
			List<Object> histories = super.findByJdbcQuery(
					"select ld_id from ld_history where ld_deleted = 0 and ld_docid is null and ld_date < '"
							+ new Timestamp(date.getTime()) + "'", 1, null);
			for (Object id : histories) {
				Long historyId = (Long) id;
				super.bulkUpdate("set ld_deleted = 1 where ld_id = " + historyId, null);
			}
		}
	}
}