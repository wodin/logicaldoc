package com.logicaldoc.core.security.dao;

import java.util.Date;
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
	@SuppressWarnings("unchecked")
	public List<UserHistory> findByUserId(long userId) {
		return findByWhere("_entity.userId =" + userId, null, "order by _entity.date asc");
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserHistoryDAO#createUserHistory(com.logicaldoc.core.security.User,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createUserHistory(User user, String eventType, String comment, String sessionId) {
		UserHistory history = new UserHistory();

		history.setDate(new Date());
		history.setUserId(user.getId());
		history.setUserName(user.getFullName());
		history.setEvent(eventType);
		history.setComment(comment);
		if (sessionId != null)
			history.setSessionId(sessionId);

		store(history);
	}

}
