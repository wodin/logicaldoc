package com.logicaldoc.email.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.email.EmailAccount;
import com.logicaldoc.email.EmailCacheManager;

/**
 * Hibernate implementation of <code>EmailAccount</code>
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 4.0
 */
public class HibernateEmailAccountDAO extends HibernatePersistentObjectDAO<EmailAccount> implements EmailAccountDAO {

	protected static Log log = LogFactory.getLog(HibernateEmailAccountDAO.class);

	private EmailCacheManager cacheManager;

	private HibernateEmailAccountDAO() {
		super(EmailAccount.class);
		super.log = LogFactory.getLog(HibernateEmailAccountDAO.class);
	}

	public void setCacheManager(EmailCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

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
}
