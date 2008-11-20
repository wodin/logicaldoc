package com.logicaldoc.email.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.email.EmailAccount;
import com.logicaldoc.email.EmailCacheManager;

/**
 * Hibernate implementation of <code>EmailAccount</code>
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.0
 */
public class HibernateEmailAccountDAO extends HibernateDaoSupport implements EmailAccountDAO {

	protected static Log log = LogFactory.getLog(HibernateEmailAccountDAO.class);

	private EmailCacheManager cacheManager;

	private HibernateEmailAccountDAO() {
	}

	public void setCacheManager(EmailCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * @see com.logicaldoc.email.dao.EmailAccountDAO#store(com.logicaldoc.email.EmailAccount)
	 */
	public boolean store(EmailAccount account) {
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
	 * @see com.logicaldoc.email.dao.EmailAccountDAO#delete(long)
	 */
	public boolean delete(long accountId) {
		boolean result = true;
		try {
			EmailAccount emAccount = (EmailAccount) getHibernateTemplate().get(EmailAccount.class, accountId);
			if (emAccount != null) {
				emAccount.setDeleted(1);
				getHibernateTemplate().saveOrUpdate(emAccount);
				cacheManager.deleteCache(emAccount);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.email.dao.EmailAccountDAO#findById(long)
	 */
	public EmailAccount findById(long accountId) {
		EmailAccount emAccount = null;

		try {
			emAccount = (EmailAccount) getHibernateTemplate().get(EmailAccount.class, new Long(accountId));
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		if (emAccount != null && emAccount.getDeleted() == 1)
			return null;

		return emAccount;
	}

	/**
	 * @see com.logicaldoc.email.dao.EmailAccountDAO#findAll()
	 */
	@SuppressWarnings("unchecked")
	public Collection<EmailAccount> findAll() {
		Collection<EmailAccount> result = new ArrayList<EmailAccount>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(EmailAccount.class);
			result = (Collection<EmailAccount>) getHibernateTemplate().findByCriteria(dt);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return result;
	}
}
