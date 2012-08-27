package com.logicaldoc.core.generic.dao;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>GenericDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class HibernateGenericDAO extends HibernatePersistentObjectDAO<Generic> implements GenericDAO {
	public HibernateGenericDAO() {
		super(Generic.class);
		super.log = LoggerFactory.getLogger(HibernateGenericDAO.class);
	}

	@Override
	public boolean delete(long genericId) {
		boolean result = true;
		try {
			Generic generic = findById(genericId);
			if (generic != null) {
				generic.setType(generic.getType() + "." + generic.getId());
				generic.setSubtype(generic.getSubtype() + "." + generic.getId());
				generic.setDeleted(1);
				getHibernateTemplate().saveOrUpdate(generic);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	@Override
	public Generic findByAlternateKey(String type, String subtype) {
		Generic generic = null;
		Collection<Generic> coll = findByWhere(" _entity.type = '" + SqlUtil.doubleQuotes(type)
				+ "' and _entity.subtype='" + SqlUtil.doubleQuotes(subtype) + "'", null, null);
		if (coll.size() > 0) {
			generic = coll.iterator().next();
		}
		return generic;
	}

	@Override
	public List<Generic> findByTypeAndSubtype(String type, String subtype) {
		String query = " 1=1 ";
		if (StringUtils.isNotEmpty(type))
			query += " and _entity.type like '" + SqlUtil.doubleQuotes(type) + "' ";
		if (StringUtils.isNotEmpty(subtype))
			query += " and _entity.subtype like '" + SqlUtil.doubleQuotes(subtype) + "' ";
		return findByWhere(query, null, null);
	}

	@Override
	public void initialize(Generic generic) {
		getHibernateTemplate().refresh(generic);
		for (String attribute : generic.getAttributes().keySet()) {
			if (generic.getValue(attribute) != null)
				generic.getValue(attribute).toString();
		}
	}
}
