package com.logicaldoc.core.security.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.security.UserDoc;

/**
 * Hibernate implementation of <code>UserDocDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateUserDocDAO.java,v 1.1 2007/06/29 06:28:25 marco Exp $
 * @since 3.0
 */
public class HibernateUserDocDAO extends HibernateDaoSupport implements UserDocDAO {

	protected static Log log = LogFactory.getLog(HibernateUserDocDAO.class);

	private HibernateUserDocDAO() {
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#delete(long,
	 *      java.lang.StringO)
	 */
	@SuppressWarnings("unchecked")
	public boolean delete(long docId, long userId) {
		boolean result = true;

		try {
			Collection<UserDoc> coll = (Collection<UserDoc>) getHibernateTemplate().find(
					"from UserDoc _userdoc where _userdoc.docId = ? and _userdoc.userId = ?",
					new Object[] { docId, userId });
			getHibernateTemplate().deleteAll(coll);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#exists(int,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean exists(long docId, long userId) {
		boolean result = false;

		try {
			Collection<UserDoc> coll = (Collection<UserDoc>) getHibernateTemplate().find(
					"from UserDoc _userdoc where _userdoc.docId = ? and _userdoc.userId = ?",
					new Object[] { docId, userId });
			return coll != null && coll.size() > 0;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#findByMinDate(long)
	 */
	public UserDoc findByMinDate(long userId) {
		UserDoc userdoc = null;
		Collection<UserDoc> coll = findByUserId(userId);
		Iterator<UserDoc> iter = coll.iterator();

		while (iter.hasNext()) {
			userdoc = iter.next();
		}

		return userdoc;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#findByUserName(long)
	 */
	@SuppressWarnings("unchecked")
	public List<UserDoc> findByUserId(long userId) {
		List<UserDoc> coll = new ArrayList<UserDoc>();

		try {
			coll = (List<UserDoc>) getHibernateTemplate().find(
					"from UserDoc _userdoc where _userdoc.userId = ? order by _userdoc.date desc",
					new Object[] { userId });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#getCount(longo)
	 */
	@SuppressWarnings("unchecked")
	public int getCount(long userId) {
		return findByUserId(userId).size();
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#store(com.logicaldoc.core.security.UserDoc)
	 */
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
				getHibernateTemplate().flush();
				getHibernateTemplate().evict(temp);
				getHibernateTemplate().evict(temp.getId());
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		try {
			getHibernateTemplate().saveOrUpdate(userdoc);
		} catch (Exception e) {

			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);

			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#delete(long)
	 */
	public boolean delete(long docId) {
		boolean result = true;

		try {
			getHibernateTemplate().deleteAll(findByDocId(docId));
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
	@SuppressWarnings("unchecked")
	public List<UserDoc> findByDocId(long docId) {
		List<UserDoc> coll = new ArrayList<UserDoc>();

		try {
			coll = (List<UserDoc>) getHibernateTemplate().find(
					"from UserDoc _userdoc where _userdoc.docId = ? order by _userdoc.date desc",
					new Object[] { docId });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return coll;
	}
}