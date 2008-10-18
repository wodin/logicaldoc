package com.logicaldoc.core.security.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.security.User;
import com.logicaldoc.util.io.CryptUtil;

/**
 * Hibernate implementation of <code>MenuDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateUserDAO.java,v 1.1 2007/06/29 06:28:25 marco Exp $
 * @since 3.0
 */
public class HibernateUserDAO extends HibernateDaoSupport implements UserDAO {

	protected static Log log = LogFactory.getLog(HibernateUserDAO.class);

	private UserDocDAO userDocDAO;

	private HibernateUserDAO() {
	}

	public UserDocDAO getUserDocDAO() {
		return userDocDAO;
	}

	public void setUserDocDAO(UserDocDAO userDocDAO) {
		this.userDocDAO = userDocDAO;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#delete(long)
	 */
	public boolean delete(long userId) {
		boolean result = true;

		try {
			User user = (User) getHibernateTemplate().get(User.class, userId);
			if (user != null) {
				getHibernateTemplate().deleteAll(userDocDAO.findByUserName(user.getUserName()));
				getHibernateTemplate().delete(user);
			}
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = true;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findAll()
	 */
	@SuppressWarnings("unchecked")
	public Collection<User> findAll() {
		Collection<User> coll = new ArrayList<User>();

		try {
			coll = (Collection<User>) getHibernateTemplate().find("from User");
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection findByName(String name) {
		Collection<User> coll = new ArrayList<User>();

		try {
			coll = (Collection<User>) getHibernateTemplate().find("from User _user where _user.name like ?",
					new Object[] { name });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findByUserName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public User findByUserName(String username) {
		User user = null;

		try {
			Collection<User> coll = (Collection<User>) getHibernateTemplate().find(
					"from User _user where _user.userName = ?", new Object[] { username });
			if (coll.size() > 0) {
				user = coll.iterator().next();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return user;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findByUserName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<User> findByLikeUserName(String username) {
		Collection<User> coll = new ArrayList<User>();

		try {
			coll = (Collection<User>) getHibernateTemplate().find("from User _user where _user.userName like ?",
					new Object[] { username });

		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#findByUserNameAndName(java.lang.String,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<User> findByUserNameAndName(String username, String name) {
		Collection<User> coll = new ArrayList<User>();

		try {
			coll = (Collection<User>) getHibernateTemplate().find(
					"from User _user where _user.name like ? and _user.userName like ?",
					new Object[] { name, username });
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#store(com.logicaldoc.core.security.User)
	 */
	public boolean store(User user) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(user);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.security.dao.UserDAO#validateUser(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean validateUser(String username, String password) {
		boolean result = true;

		try {
			User user = findByUserName(username);

			if ((user == null) || !user.getPassword().equals(CryptUtil.cryptString(password))) {
				result = false;
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public User findByPrimaryKey(long userId) {
		User user = null;

		try {
			user = (User) getHibernateTemplate().get(User.class, userId);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return user;
	}
}