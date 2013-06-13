package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.History;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>HistoryDAO</code>
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
@SuppressWarnings("unchecked")
public class HibernateHistoryDAO extends HibernatePersistentObjectDAO<History> implements HistoryDAO {

	private ContextProperties config;

	private HibernateHistoryDAO() {
		super(History.class);
		super.log = LoggerFactory.getLogger(HibernateHistoryDAO.class);
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
	public List<History> findNotNotified(Integer max) {
		return findByWhere("_entity.notified = 0", null, "order by _entity.date asc", max);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserHistoryDAO#cleanOldHistories(int)
	 */
	@Override
	public void cleanOldHistories(int ttl) {
		if (ttl > 0) {
			Date today = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.add(Calendar.DAY_OF_MONTH, -ttl);
			Date ldDate = cal.getTime();

			log.debug("today: " + today);
			log.debug("ldDate: " + ldDate);

			try {
				int rowsUpdated = jdbcUpdate("UPDATE ld_history SET ld_deleted = 1, ld_lastmodified = ?"
						+ " WHERE ld_deleted = 0 AND ld_date < ?", today, ldDate);

				log.info("cleanOldHistories rows updated: " + rowsUpdated);
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

	@Override
	public boolean store(History entity) {
		// Write only if the history is enabled
		if (isEnabled())
			return super.store(entity);
		else
			return true;
	}

	@Override
	public boolean isEnabled() {
		return "true".equals(config.getProperty("history.enabled"));
	}

	public void setConfig(ContextProperties config) {
		this.config = config;
	}

	@Override
	public List<History> findByPath(String pathExpression, Date olderDate, Collection<String> events, Integer max) {
		StringBuffer query = new StringBuffer("(_entity.path like '" + pathExpression + "' or _entity.pathOld like '"+ pathExpression + "') ");
		List<Object> params = new ArrayList<Object>();
		if (olderDate != null) {
			query.append(" and _entity.date >= ? ");
			params.add(olderDate);
		}
		if (events != null && !events.isEmpty()) {
			StringBuffer eventsStr = new StringBuffer("(");
			for (String event : events) {
				if (eventsStr.length() > 1)
					eventsStr.append(",");
				eventsStr.append("'" + event + "'");
			}
			eventsStr.append(")");
			query.append(" and _entity.event in " + eventsStr);
		}

		return findByWhere(query.toString(), params.toArray(new Object[0]), "order by _entity.date asc", max);
	}
}