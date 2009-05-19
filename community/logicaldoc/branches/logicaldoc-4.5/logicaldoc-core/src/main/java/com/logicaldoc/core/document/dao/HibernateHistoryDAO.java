package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.History;

/**
 * Hibernate implementation of <code>HistoryDAO</code>
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class HibernateHistoryDAO extends HibernatePersistentObjectDAO<History> implements HistoryDAO {

	private HibernateHistoryDAO() {
		super(History.class);
		super.log = LogFactory.getLog(HibernateHistoryDAO.class);
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