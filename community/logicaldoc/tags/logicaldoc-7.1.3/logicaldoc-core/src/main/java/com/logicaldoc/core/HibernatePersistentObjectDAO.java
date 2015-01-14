package com.logicaldoc.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;
import com.sun.source.tree.AssertTree;

/**
 * Hibernate implementation of <code>PersistentObjectDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public abstract class HibernatePersistentObjectDAO<T extends PersistentObject> implements PersistentObjectDAO<T> {
	protected Logger log = LoggerFactory.getLogger(HibernatePersistentObjectDAO.class);

	protected Class<T> entityClass;

	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected HibernatePersistentObjectDAO(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	public boolean delete(long id, int code) {
		assert(code!=0);
		
		boolean result = true;
		try {
			T entity = findById(id);
			if (entity == null)
				return false;
			entity.setDeleted(code);
			store(entity);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	public boolean delete(long id) {
		return delete(id, PersistentObject.DELETED_CODE_DEFAULT);
	}

	public List<T> findAll() {
		return findByWhere("", "", null);
	}

	public List<T> findAll(long tenantId) {
		return findByWhere(" _entity.tenantId=" + tenantId, "", null);
	}

	public List<Long> findAllIds() {
		return findIdsByWhere("", "", null);
	}

	public List<Long> findAllIds(long tenantId) {
		return findIdsByWhere(" _entity.tenantId=" + tenantId, "", null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T findById(long id) {
		T entity = null;
		try {
			entity = (T) sessionFactory.getCurrentSession().get(entityClass, id);
			if (entity != null && entity.getDeleted() == 1)
				return null;
		} catch (Throwable e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return entity;
	}

	@Override
	public List<T> findByWhere(String where, String order, Integer max) {
		return findByWhere(where, null, order, max);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByWhere(String where, Object[] values, String order, Integer max) {
		List<T> coll = new ArrayList<T>();
		try {
			String query = "from " + entityClass.getCanonicalName() + " _entity where _entity.deleted=0 "
					+ (StringUtils.isNotEmpty(where) ? " and (" + where + ") " : " ")
					+ (StringUtils.isNotEmpty(order) ? order : " ");
			log.debug("Execute query: " + query);
			Query queryObject = prepareQuery(query, values, max);
			coll = (List<T>) queryObject.list();
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List findByQuery(String query, Object[] values, Integer max) {
		List<Object> coll = new ArrayList<Object>();
		try {
			log.debug("Execute query: " + query);
			Query queryObject = prepareQuery(query, values, max);
			coll = (List<Object>) queryObject.list();
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@Override
	public List<Long> findIdsByWhere(String where, String order, Integer max) {
		return findIdsByWhere(where, new Object[0], order, max);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Long> findIdsByWhere(String where, Object[] values, String order, Integer max) {
		List<Long> coll = new ArrayList<Long>();
		try {
			String query = "select _entity.id from " + entityClass.getCanonicalName()
					+ " _entity where _entity.deleted=0 "
					+ (StringUtils.isNotEmpty(where) ? " and (" + where + ") " : " ")
					+ (StringUtils.isNotEmpty(order) ? order : " ");
			log.debug("Execute query: " + query);
			Query queryObject = prepareQuery(query, values, max);
			coll = (List<Long>) queryObject.list();
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
			sessionFactory.getCurrentSession().saveOrUpdate(entity);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	protected void saveOrUpdate(Object entity) {
		sessionFactory.getCurrentSession().saveOrUpdate(entity);
	}

	protected void flush() {
		try {
			sessionFactory.getCurrentSession().flush();
		} catch (Throwable t) {

		}
	}

	protected void refresh(Object entity) {
		try {
			sessionFactory.getCurrentSession().refresh(entity);
		} catch (Throwable t) {

		}
	}

	protected Object merge(Object entity) {
		try {
			return sessionFactory.getCurrentSession().merge(entity);
		} catch (Throwable t) {
			return null;
		}
	}

	protected void evict(Object entity) {
		sessionFactory.getCurrentSession().evict(entity);
	}

	/**
	 * Utility method useful for preparing an Hibernate query
	 * 
	 * @param expression The expression for the query
	 * @param values The parameters values to be used (optional, if the query is
	 *        parametric)
	 * @param max Optional maximum number of wanted results
	 * 
	 * @return The Hibernate query
	 */
	protected Query prepareQuery(String expression, Object[] values, Integer max) {
		Query queryObject = sessionFactory.getCurrentSession().createQuery(expression);
		if (values != null)
			for (int i = 0; i < values.length; i++)
				queryObject.setParameter(Integer.toString(i + 1), values[i]);

		if (max != null && max > 0)
			queryObject.setMaxResults(max);
		return queryObject;
	}

	/**
	 * Doesn't do anything by default
	 */
	@Override
	public void initialize(T entity) {
		// By default do nothing
	}

	protected Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List query(String sql, Object[] args, RowMapper rowMapper, Integer maxRows) {
		List list = new ArrayList();
		try {
			DataSource dataSource = (DataSource) Context.getInstance().getBean("DataSource");

			// DataSource dataSource =
			// SessionFactoryUtils.getDataSource(getSessionFactory());
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			if (maxRows != null)
				jdbcTemplate.setMaxRows(maxRows);
			if (args != null)
				list = jdbcTemplate.query(sql, args, rowMapper);
			else
				list = jdbcTemplate.query(sql, rowMapper);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List queryForList(String sql, Object[] args, Class elementType, Integer maxRows) {

		List list = new ArrayList();
		try {
			DataSource dataSource = (DataSource) Context.getInstance().getBean("DataSource");
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			if (maxRows != null)
				jdbcTemplate.setMaxRows(maxRows);
			if (args != null)
				list = jdbcTemplate.queryForList(sql, args, elementType);
			else
				list = jdbcTemplate.queryForList(sql, elementType);
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return list;
	}

	@Override
	public SqlRowSet queryForRowSet(String sql, Object[] args, Integer maxRows) {
		SqlRowSet rs = null;
		try {
			DataSource dataSource = (DataSource) Context.getInstance().getBean("DataSource");
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			if (maxRows != null)
				jdbcTemplate.setMaxRows(maxRows);
			if (args != null)
				rs = jdbcTemplate.queryForRowSet(sql, args);
			else
				rs = jdbcTemplate.queryForRowSet(sql);
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return rs;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List queryForList(String sql, Class elementType) {
		return queryForList(sql, null, elementType, null);
	}

	@Override
	public int queryForInt(String sql) {
		long mytmplong = queryForLong(sql);
		return new Long(mytmplong).intValue();
	}

	@SuppressWarnings("deprecation")
	@Override
	public long queryForLong(String sql) {
		try {
			DataSource dataSource = (DataSource) Context.getInstance().getBean("DataSource");
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			return jdbcTemplate.queryForLong(sql);
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public int jdbcUpdate(String statement) {
		try {
			DataSource dataSource = (DataSource) Context.getInstance().getBean("DataSource");
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			return jdbcTemplate.update(statement);
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public void deleteAll(Collection<T> entities, int code) {
		if (entities == null || entities.isEmpty())
			return;
		StringBuffer ids = new StringBuffer();
		for (T t : entities) {
			if (ids.length() > 0)
				ids.append(",");
			ids.append(Long.toString(t.getId()));
		}

		Query queryObject = sessionFactory.getCurrentSession().createQuery(
				"update " + entityClass.getCanonicalName() + " set deleted=" + code + " where id in(" + ids.toString()
						+ ")");
		queryObject.executeUpdate();
	}

	@Override
	public void deleteAll(Collection<T> entities) {
		deleteAll(entities, PersistentObject.DELETED_CODE_DEFAULT);
	}

	@Override
	public int bulkUpdate(String expression, Object[] values) {
		Query queryObject = prepareQuery("update " + entityClass.getCanonicalName() + " " + expression, values, null);
		return queryObject.executeUpdate();
	}

	@Override
	public int jdbcUpdate(String statement, Object... args) {
		try {
			DataSource dataSource = (DataSource) Context.getInstance().getBean("DataSource");
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			return jdbcTemplate.update(statement, args);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return 0;
	}

	protected Connection getConnection() throws SQLException {
		DataSource dataSource = (DataSource) Context.getInstance().getBean("DataSource");
		return dataSource.getConnection();
	}

	@Override
	public String getDbms() {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		return config.getProperty("jdbc.dbms").toLowerCase();
	}

	protected boolean isHsql() {
		return "hsqldb".equals(getDbms());
	}

	protected boolean isMySQL() {
		return "mysql".equals(getDbms());
	}

	protected boolean isOracle() {
		return "oracle".equals(getDbms());
	}

	protected boolean isSqlServer() {
		return "mssql".equals(getDbms());
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}