package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.security.UserDoc;

/**
 * Hibernate implementation of <code>UserDocDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
@SuppressWarnings("unchecked")
public class HibernateUserDocDAO extends HibernatePersistentObjectDAO<UserDoc> implements UserDocDAO {
	public HibernateUserDocDAO() {
		super(UserDoc.class);
		super.log = LoggerFactory.getLogger(HibernateUserDocDAO.class);
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#delete(long,
	 *      java.lang.String)
	 */
	public boolean delete(long docId, long userId) {
		boolean result = true;

		try {
			List<UserDoc> coll = findByWhere("_entity.docId = ?1 and _entity.userId = ?2",
					new Object[] { docId, userId }, null, null);
			for (UserDoc userDoc : coll) {
				userDoc.setDeleted(1);
				saveOrUpdate(userDoc);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	@Override
	public boolean exists(long docId, long userId) {
		boolean result = false;

		try {
			List<UserDoc> coll = findByWhere("_entity.docId = ?1 and _entity.userId = ?2",
					new Object[] { docId, userId }, null, null);
			return coll != null && coll.size() > 0;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return result;
	}

	@Override
	public UserDoc findByMinDate(long userId) {
		UserDoc userdoc = null;
		Collection<UserDoc> coll = findByUserId(userId);
		Iterator<UserDoc> iter = coll.iterator();

		while (iter.hasNext()) {
			userdoc = iter.next();
		}

		return userdoc;
	}

	@Override
	public List<UserDoc> findByUserId(long userId) {
		return findByWhere("_entity.userId = ?1", new Object[] { userId }, "order by _entity.date desc", null);
	}

	@Override
	public int getCount(long userId) {
		return findByUserId(userId).size();
	}

	@Override
	public boolean store(UserDoc userdoc) {
		boolean result = true;
		int count = 0;

		try {
			count = getCount(userdoc.getUserId());

			boolean exists = exists(userdoc.getDocId(), userdoc.getUserId());

			// if the count of userdocs for the user is greater than 5, delete
			// the oldest userdoc
			if ((count >= 5) && !exists) {
				UserDoc temp = findByMinDate(userdoc.getUserId());
				delete(temp.getDocId(), temp.getUserId());
				flush();
				evict(temp);
				evict(temp.getId());
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		try {
			saveOrUpdate(userdoc);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);

			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#deleteByDocId(long)
	 */
	public boolean deleteByDocId(long docId) {
		boolean result = true;

		try {
			Collection<UserDoc> coll = findByDocId(docId);
			for (UserDoc userDoc : coll) {
				userDoc.setDeleted(1);
				saveOrUpdate(userDoc);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#findByDocId(long)
	 */
	public List<UserDoc> findByDocId(long docId) {
		return findByWhere("_entity.docId = ?1", new Object[] { docId }, "order by _entity.date desc", null);
	}

	@Override
	public boolean deleteByUserId(long userId) {
		boolean result = true;

		try {
			Collection<UserDoc> coll = findByUserId(userId);
			for (UserDoc userDoc : coll) {
				userDoc.setDeleted(1);
				saveOrUpdate(userDoc);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}

		return result;
	}
}