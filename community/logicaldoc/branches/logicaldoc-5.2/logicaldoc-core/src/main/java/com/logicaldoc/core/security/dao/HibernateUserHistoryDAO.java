package com.logicaldoc.core.security.dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;

public class HibernateUserHistoryDAO extends HibernatePersistentObjectDAO<UserHistory> implements UserHistoryDAO {

	private HibernateUserHistoryDAO() {
		super(UserHistory.class);
		super.log = LogFactory.getLog(HibernateUserHistoryDAO.class);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserHistoryDAO#findByUserId(long)
	 */
	public List<UserHistory> findByUserId(long userId) {
		return findByWhere("_entity.userId =" + userId, null, "order by _entity.date asc", null);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserHistoryDAO#createUserHistory(com.logicaldoc.core.security.User,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createUserHistory(User user, String eventType, String comment, String sessionId) {
		UserHistory history = new UserHistory();

		history.setUser(user);
		history.setEvent(eventType);
		history.setComment(comment);
		if (sessionId != null)
			history.setSessionId(sessionId);

		store(history);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserHistoryDAO#cleanOldHistories(int)
	 */
	@Override
	public void cleanOldHistories(int ttl) {
		if (ttl > 0) {
			Date date = new Date();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, -ttl);
			date = cal.getTime();
			// Retrieve all old user histories
			String query = "select ld_id from ld_user_history where ld_deleted = 0 and ld_date < '" + new Timestamp(date.getTime()) + "'";
			
			List histories = queryForList(query, Long.class);
			
			for (Iterator iterator = histories.iterator(); iterator.hasNext();) {
				Long historyId = (Long) iterator.next();
				super.bulkUpdate("set ld_deleted = 1 where ld_id = " + historyId, null);
			}
		}
	}
}
