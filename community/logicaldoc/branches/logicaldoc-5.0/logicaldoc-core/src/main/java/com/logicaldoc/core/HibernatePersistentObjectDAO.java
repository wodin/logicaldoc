package com.logicaldoc.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Hibernate implementation of <code>PersistentObjectDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public abstract class HibernatePersistentObjectDAO<T extends PersistentObject> extends HibernateDaoSupport implements
		PersistentObjectDAO<T> {
	protected Log log = LogFactory.getLog(HibernatePersistentObjectDAO.class);

	protected Class<T> entityClass;

	protected HibernatePersistentObjectDAO(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	public boolean delete(long id) {
		boolean result = true;
		try {
			T entity = findById(id);
			entity.setDeleted(1);
			store(entity);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	public List<T> findAll() {
		return findByWhere("", "");
	}

	public List<Long> findAllIds() {
		return findIdsByWhere("", "");
	}

	@Override
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

	@Override
	public List<T> findByWhere(String where, String order) {
		return findByWhere(where, new Object[0], order);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByWhere(String where, Object[] values, String order) {
		List<T> coll = new ArrayList<T>();
		try {
			String query = "from " + entityClass.getCanonicalName() + " _entity where _entity.deleted=0 "
					+ (StringUtils.isNotEmpty(where) ? " and (" + where + ") " : " ")
					+ (StringUtils.isNotEmpty(order) ? order : " ");
			log.debug("Execute query: " + query);
			coll = (List<T>) getHibernateTemplate().find(query, values);
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@Override
	public List<Long> findIdsByWhere(String where, String order) {
		return findIdsByWhere(where, new Object[0], order);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findIdsByWhere(String where, Object[] values, String order) {
		List<Long> coll = new ArrayList<Long>();
		try {
			String query = "select _entity.id from " + entityClass.getCanonicalName()
					+ " _entity where _entity.deleted = 0 "
					+ (StringUtils.isNotEmpty(where) ? " and (" + where + ") " : " ")
					+ (StringUtils.isNotEmpty(order) ? order : " ");
			log.debug("Execute query: " + query);
			coll = (List<Long>) getHibernateTemplate(1000).find(query, values);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	/**
	 * Useful method that creates a template for returnig a maximum number of
	 * results. If the max results is <1 than the default template is returned.
	 * 
	 * @param maxResults The maximum results number
	 */
	protected HibernateTemplate getHibernateTemplate(Integer maxResults) {
		if (maxResults == null || maxResults < 1)
			return getHibernateTemplate();
		HibernateTemplate template = new HibernateTemplate(getSessionFactory());
		template.setMaxResults(maxResults);
		return template;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> findByQuery(String query, Object[] values) {
		List<Object> coll = new ArrayList<Object>();
		try {
			log.debug("Execute query: " + query);
			coll = (List<Object>) getHibernateTemplate(1000).find(query, values != null ? values : new Object[0]);
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
	 * Doesn't do anything by default
	 */
	public void initialize(T entity) {
		// By default do nothing
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Object> findByJdbcQuery(String query, int returnedColumns, Object[] values) {
		assert (returnedColumns > 0);

		List<Object> coll = new ArrayList<Object>();
		try {
			Connection con = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			try {
				con = getSession().connection();
				stmt = con.prepareStatement(query);
				if (values != null && values.length != 0) {
					for (int i = 0; i < values.length; i++) {
						stmt.setObject(i + 1, values[i]);
					}
				}

				log.debug("Execute query: " + query);
				rs = stmt.executeQuery();
				while (rs.next()) {
					if (returnedColumns == 1) {
						coll.add(rs.getObject(1));
					} else {
						Object[] entry = new Object[returnedColumns];
						for (int i = 1; i <= returnedColumns; i++) {
							entry[i - 1] = rs.getObject(i);
						}
						coll.add(entry);
					}
				}
			} finally {
				if (rs != null)
					rs.close();
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}

		return coll;
	}

	@Override
	public void deleteAll(Collection<T> entities) {
		if (entities == null || entities.isEmpty())
			return;
		StringBuffer ids = new StringBuffer();
		for (T t : entities) {
			if (ids.length() > 0)
				ids.append(",");
			ids.append(Long.toString(t.getId()));
		}
		getHibernateTemplate().bulkUpdate(
				"update " + entityClass.getCanonicalName() + " set deleted=1 where id in(" + ids.toString() + ")");
	}

	@Override
	public int bulkUpdate(String expression, Object[] values) {
		return getHibernateTemplate().bulkUpdate("update " + entityClass.getCanonicalName() + " " + expression, values);
	}
}