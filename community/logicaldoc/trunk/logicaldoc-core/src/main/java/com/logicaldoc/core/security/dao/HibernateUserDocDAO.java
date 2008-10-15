package com.logicaldoc.core.security.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.UserDocID;

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
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#delete(java.lang.String, int)
	 */
	public boolean delete(String username, long docId) {
		boolean result = true;

		try {
			UserDocID id = new UserDocID(docId, username);
			UserDoc ud = (UserDoc) getHibernateTemplate().get(UserDoc.class, id);
			if (ud != null)
				getHibernateTemplate().delete(ud);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#exists(int, java.lang.String)
	 */
	public boolean exists(long docId, String username) {
		boolean result = false;

		try {
			UserDocID id = new UserDocID(docId, username);
			UserDoc ud = (UserDoc) getHibernateTemplate().get(UserDoc.class, id);
			result = ud != null;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#findByMinDate(java.lang.String)
	 */
	public UserDoc findByMinDate(String username) {
		UserDoc userdoc = null;
		Collection<UserDoc> coll = findByUserName(username);
		Iterator<UserDoc> iter = coll.iterator();

		while (iter.hasNext()) {
			userdoc = iter.next();
		}

		return userdoc;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#findByUserName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<UserDoc> findByUserName(String username) {
		List<UserDoc> coll = new ArrayList<UserDoc>();

		try {
			coll = (List<UserDoc>) getHibernateTemplate()
					.find(
							"from com.logicaldoc.core.security.UserDoc _userdoc where _userdoc.id.userName = ? order by _userdoc.date desc",
							new Object[] { username });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#getCount(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public int getCount(String username) {
		return findByUserName(username).size();
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDocDAO#store(com.logicaldoc.core.security.UserDoc)
	 */
	public boolean store(UserDoc userdoc) {
		boolean result = true;
		int count = 0;

		try {
			count = getCount(userdoc.getUserName());

			boolean exists = exists(userdoc.getDocId(), userdoc.getUserName());

			// if the count of userdocs for the user is greater than 5, delete
			// the oldest userdoc
			if ((count >= 5) && !exists) {
				UserDoc temp = findByMinDate(userdoc.getUserName());
				delete(temp.getUserName(), temp.getDocId());
                getHibernateTemplate().flush();
                getHibernateTemplate().evict(temp);
                getHibernateTemplate().evict(temp.getId());
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(),e);
		}

		try {
			getHibernateTemplate().saveOrUpdate(userdoc);
		} catch (Exception e) {

			if (log.isErrorEnabled())
				log.error(e.getMessage(),e);

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
            coll = (List<UserDoc>) getHibernateTemplate()
                    .find(
                            "from com.logicaldoc.core.security.UserDoc _userdoc where _userdoc.id.docId = ? order by _userdoc.date desc",
                            new Object[] { docId });
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage());
        }

        return coll;
    }
}