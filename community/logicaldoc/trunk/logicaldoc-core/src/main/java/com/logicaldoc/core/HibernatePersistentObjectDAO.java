package com.logicaldoc.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Hibernate implementation of <code>PersistentObjectDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public abstract class HibernatePersistentObjectDAO<T extends PersistentObject> extends HibernateDaoSupport implements
		PersistentObjectDAO<T> {
	protected Logger log = LoggerFactory.getLogger(HibernatePersistentObjectDAO.class);

	protected Class<T> entityClass;

	protected HibernatePersistentObjectDAO(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	public boolean delete(long id) {
		boolean result = true;
		try {
			T entity = findById(id);
			if (entity == null)
				return false;
			entity.setDeleted(1);
			store(entity);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	public List<T> findAll() {
		return findByWhere("", "", null);
	}

	public List<Long> findAllIds() {
		return findIdsByWhere("", "", null);
	}

	@Override
	public T findById(long id) {
		T entity = null;
		try {
			entity = (T) getHibernateTemplate().get(entityClass, id);
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
		return findByWhere(where, new Object[0], order, max);
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

			coll = (List<T>) getHibernateTemplate(max).find(query, values);
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
			coll = (List<Long>) getHibernateTemplate(max).find(query, values);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@SuppressWarnings("unchecked")
	public List<Object> findByQuery(String query, Object[] values, Integer max) {
		List<Object> coll = new ArrayList<Object>();
		try {
			log.debug("Execute query: " + query);
			coll = (List<Object>) getHibernateTemplate(max).find(query, values != null ? values : new Object[0]);
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

	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
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
}