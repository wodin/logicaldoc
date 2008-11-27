package com.logicaldoc.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.document.dao.HibernateDocumentDAO;

/**
 * Hibernate implementation of <code>PersistentObjectDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public abstract class HibernatePersistentObjectDAO<T extends PersistentObject> extends HibernateDaoSupport implements
		PersistentObjectDAO<T> {
	protected Log log = LogFactory.getLog(HibernateDocumentDAO.class);

	protected Class<T> entityClass;

	protected HibernatePersistentObjectDAO(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	public boolean delete(long id) {
		boolean result = true;
		try {
			T doc = findById(id);
			doc.setDeleted(1);
			store(doc);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	public List<T> findAll() {
		return findByWhere("");
	}

	public List<Long> findAllIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public T findById(long id) {
		T entity = null;
		try {
			entity = (T) getHibernateTemplate().get(entityClass, id);
			if (entity != null && entity.getDeleted() == 1)
				return null;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return entity;
	}

	public List<T> findByWhere(String where) {
		return findByWhere(where, new Object[0]);
	}

	@SuppressWarnings("unchecked")
	public List<T> findByWhere(String where, Object[] values) {
		List<T> coll = new ArrayList<T>();
		try {
			coll = (List<T>) getHibernateTemplate().find(
					"from " + entityClass.getCanonicalName() + " _entity where _entity.deleted=0"
							+ (StringUtils.isNotEmpty(where) ? " and " + where : ""), values);
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	public List<Long> findIdsByWhere(String where) {
		return findIdsByWhere(where, new Object[0]);
	}

	@SuppressWarnings("unchecked")
	public List<Long> findIdsByWhere(String where, Object[] values) {
		List<Long> coll = new ArrayList<Long>();
		try {
			coll = (List<Long>) getHibernateTemplate().find(
					"select _entity.id from " + entityClass.getCanonicalName() + " _entity where _entity.deleted=0"
							+ (StringUtils.isNotEmpty(where) ? " and " + where : ""), values);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	public boolean store(T entity) {
		boolean result = true;
		try {
			// Save the entity
			getHibernateTemplate().saveOrUpdate(entity);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	/**
	 * Doesn't do anything
	 */
	public void initialize(T entity) {
		//By default do nothing
	}
}
