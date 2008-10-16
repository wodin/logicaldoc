package com.logicaldoc.core.communication.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.EMailAccount;

/**
 * Hibernate implementation of <code>EMailAccount</code>
 * 
 * @author Alessandro Gasparini
 * @version $Id: HibernateEMailAccountDAO.java,v 1.1 2007/06/29 06:28:30 marco
 *          Exp $
 * @since 3.0
 */
public class HibernateEMailAccountDAO extends HibernateDaoSupport implements EMailAccountDAO {

	protected static Log log = LogFactory.getLog(HibernateEMailAccountDAO.class);

	private HibernateEMailAccountDAO() {
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.EMailAccountDAO#store(com.logicaldoc.core.communication.EMailAccount)
	 */
	public boolean store(EMailAccount account) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(account);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.EMailAccountDAO#delete(long)
	 */
	public boolean delete(long accountId) {
		boolean result = true;

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(EMail.class);
			dt.add(Property.forName("accountId").eq(new Long(accountId)));
			getHibernateTemplate().deleteAll(getHibernateTemplate().findByCriteria(dt));
			EMailAccount emAccount = (EMailAccount) getHibernateTemplate().get(EMailAccount.class, accountId);
			if (emAccount != null)
				getHibernateTemplate().delete(emAccount);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.EMailAccountDAO#findByPrimaryKey(long)
	 */
	public EMailAccount findByPrimaryKey(long accountId) {
		EMailAccount emAccount = null;

		try {
			emAccount = (EMailAccount) getHibernateTemplate().get(EMailAccount.class, new Long(accountId));
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return emAccount;
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.EMailAccountDAO#findAll()
	 */
	@SuppressWarnings("unchecked")
	public Collection<EMailAccount> findAll() {
		Collection<EMailAccount> result = new ArrayList<EMailAccount>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(EMailAccount.class);
			result = (Collection<EMailAccount>) getHibernateTemplate().findByCriteria(dt);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.EMailAccountDAO#deleteByUsername(java.lang.String)
	 */
	public boolean deleteByUsername(String username) {
		boolean result = true;

		try {
			Collection<EMailAccount> coll = findByUserName(username);
			for (EMailAccount account : coll) {
				delete(account.getId());
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.EMailAccountDAO#findByUserName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Collection<EMailAccount> findByUserName(String username) {
		Collection<EMailAccount> result = new ArrayList<EMailAccount>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(EMailAccount.class);
			dt.add(Property.forName("userName").eq(username));

			result = (Collection<EMailAccount>) getHibernateTemplate().findByCriteria(dt);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return result;
	}
}
