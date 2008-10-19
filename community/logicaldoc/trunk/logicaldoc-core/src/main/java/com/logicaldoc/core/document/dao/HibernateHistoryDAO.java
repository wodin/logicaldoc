package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.document.History;

/**
 * Hibernate implementation of <code>HistoryDAO</code>
 * 
 * @author Alessandro Gasparini
 * @version $Id: HibernateHistoryDAO.java,v 1.1 2007/06/29 06:28:28 marco Exp $
 * @since 3.0
 */
public class HibernateHistoryDAO extends HibernateDaoSupport implements HistoryDAO {

	protected static Log log = LogFactory.getLog(HibernateHistoryDAO.class);

	private HibernateHistoryDAO() {
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#store(com.logicaldoc.core.document.History)
	 */
	public boolean store(History history) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(history);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#delete(long)
	 */
	public boolean delete(long historyId) {
		boolean result = true;

		try {
			History history = (History) getHibernateTemplate().get(History.class, historyId);
			if (history != null) {
				getHibernateTemplate().delete(history);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByDocId(int)
	 */
	@SuppressWarnings("unchecked")
	public List<History> findByDocId(long docId) {
		List<History> coll = new ArrayList<History>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(History.class);
			dt.add(Property.forName("docId").eq(new Long(docId)));
			dt.addOrder(Order.asc("date"));

			coll = (List<History>) getHibernateTemplate().findByCriteria(dt);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.HistoryDAO#findByUsername(long)
	 */
	@SuppressWarnings("unchecked")
	public List<History> findByUserId(long userId) {
		List<History> coll = new ArrayList<History>();

		try {
			DetachedCriteria dt = DetachedCriteria.forClass(History.class);
			dt.add(Property.forName("userId").eq(userId));
			dt.addOrder(Order.asc("date"));

			coll = (List<History>) getHibernateTemplate().findByCriteria(dt);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
		}

		return coll;
	}
}