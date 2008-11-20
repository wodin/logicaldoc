package com.logicaldoc.core.generic.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.generic.Generic;

/**
 * Hibernate implementation of <code>GenericDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class HibernateGenericDAO extends HibernateDaoSupport implements GenericDAO {
	protected static Log log = LogFactory.getLog(HibernateGenericDAO.class);

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

	@SuppressWarnings("unchecked")
	@Override
	public Generic findByAlternateKey(String type, String subtype) {
		Generic generic = null;
		try {
			Collection<Generic> coll = (Collection<Generic>) getHibernateTemplate().find(
					"from Generic _generic where _generic.type = '" + type + "' and _generic.subtype='" + subtype
							+ "' and _generic.deleted=0");
			if (coll.size() > 0) {
				generic = coll.iterator().next();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return generic;
	}

	@Override
	public Generic findById(long genericId) {
		Generic generic = null;
		try {
			generic = (Generic) getHibernateTemplate().get(com.logicaldoc.core.generic.Generic.class, genericId);
			if (generic != null && generic.getDeleted() == 1)
				generic = null;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return generic;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Generic> findByTypeAndSubtype(String type, String subtype) {
		List<Generic> coll = new ArrayList<Generic>();
		try {
			String query = "from Generic _generic where _generic.deleted=0 ";
			if (StringUtils.isNotEmpty(type))
				query += " and _generic.type like '" + type + "' ";
			if (StringUtils.isNotEmpty(subtype))
				query += " and _generic.subtype like '" + subtype + "' ";
			coll = (List<Generic>) getHibernateTemplate().find(query);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage(), e);
		}
		return coll;
	}

	@Override
	public boolean store(Generic generic) {
		boolean result = true;
		try {
			getHibernateTemplate().saveOrUpdate(generic);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				log.error(e.getMessage());
			result = false;
		}
		return result;
	}

	@Override
	public void initialize(Generic generic) {
		getHibernateTemplate().refresh(generic);
		for (String attribute : generic.getAttributes().keySet()) {
			generic.getValue(attribute).getBytes();
		}
	}
}
