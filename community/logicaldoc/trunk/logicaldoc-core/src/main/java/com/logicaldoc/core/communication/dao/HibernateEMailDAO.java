package com.logicaldoc.core.communication.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.communication.EMail;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>EMailDAO</code>
 * 
 * @author Alessandro Gasparini
 * @version $Id: HibernateEMailDAO.java,v 1.1 2007/06/29 06:28:30 marco Exp $
 * @since 3.0
 */
public class HibernateEMailDAO extends HibernateDaoSupport implements EMailDAO {

    protected static Log log = LogFactory.getLog(HibernateEMailDAO.class);

    private HibernateEMailDAO() {
    }

    /**
     * @see com.logicaldoc.core.communication.dao.EMailDAO#store(com.logicaldoc.core.communication.EMail)
     */
    public boolean store(EMail email) {
        boolean result = true;

        try {
            getHibernateTemplate().saveOrUpdate(email);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                logger.error(e.getMessage(), e);
            result = false;
        }

        return result;
    }

    /**
     * @see com.logicaldoc.core.communication.dao.EMailDAO#delete(int)
     */
    public boolean delete(int messageId) {
        boolean result = true;

        try {
            EMail message = findByPrimaryKey(messageId);
            if (message != null)
                getHibernateTemplate().delete(message);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                logger.error(e.getMessage(), e);
            result = false;
        }

        return result;
    }

    /**
     * @see com.logicaldoc.core.communication.dao.EMailDAO#findByPrimaryKey(int)
     */
    public EMail findByPrimaryKey(int messageId) {
        EMail email = null;

        try {
            email = (EMail) getHibernateTemplate().get(EMail.class, new Integer(messageId));
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error(e.getMessage(), e);
        }

        return email;
    }

    /**
     * @see com.logicaldoc.core.communication.dao.EMailDAO#findByUserName(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Collection<EMail> findByUserName(String username) {
        Collection<EMail> coll = new ArrayList<EMail>();

        try {
            DetachedCriteria dt = DetachedCriteria.forClass(EMail.class);
            dt.add(Property.forName("userName").eq(username));

            coll = (Collection<EMail>) getHibernateTemplate().findByCriteria(dt);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                logger.error(e.getMessage(), e);
        }

        return coll;
    }

    /**
     * @see com.logicaldoc.core.communication.dao.EMailDAO#findByUserName(java.lang.String,
     *      java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Collection<EMail> findByUserName(String username, String folder) {
        Collection<EMail> coll = new ArrayList<EMail>();

        try {
            DetachedCriteria dt = DetachedCriteria.forClass(EMail.class);
            dt.add(Property.forName("userName").eq(username));
            dt.add(Property.forName("folder").eq(folder));
            dt.addOrder(Order.asc("sentDate"));

            coll = (Collection<EMail>) getHibernateTemplate().findByCriteria(dt);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                logger.error(e.getMessage(), e);
        }

        return coll;
    }

    /**
     * @see com.logicaldoc.core.communication.dao.EMailDAO#findByAccountId(int)
     */
    @SuppressWarnings("unchecked")
    public Collection<EMail> findByAccountId(int accountId) {
        Collection<EMail> coll = new ArrayList<EMail>();

        try {
            DetachedCriteria dt = DetachedCriteria.forClass(EMail.class);
            dt.add(Property.forName("accountId").eq(new Integer(accountId)));
            dt.addOrder(Order.asc("sentDate"));

            coll = (Collection<EMail>) getHibernateTemplate().findByCriteria(dt);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                logger.error(e.getMessage(), e);
        }

        return coll;
    }

    /**
     * @see com.logicaldoc.core.communication.dao.EMailDAO#collectEmailIds(int)
     */
    public Collection<String> collectEmailIds(int accountId) {
        Collection<String> ids = new HashSet<String>();
        Collection<EMail> coll = findByAccountId(accountId);
        for (EMail mail : coll) {
            if (!ids.contains(mail.getEmailId()))
                ids.add(mail.getEmailId());
        }
        return ids;
    }
}