package com.logicaldoc.core.contact;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.generic.HibernateGenericDAO;

/**
 * Hibernate implementation of <code>ContactDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
@SuppressWarnings("unchecked")
public class HibernateContactDAO extends HibernatePersistentObjectDAO<Contact> implements ContactDAO {

	public HibernateContactDAO() {
		super(Contact.class);
		super.log = LoggerFactory.getLogger(HibernateGenericDAO.class);
	}

	@Override
	public List<Contact> findByUser(Long userId) {
		if (userId != null)
			return findByWhere(" _entity.userId=? ", new Object[] { userId },
					"order by _entity.firstName, _entity.lastName", null);
		else
			return findByWhere(" _entity.userId is null ", new Object[0],
					"order by _entity.firstName, _entity.lastName", null);
	}
}