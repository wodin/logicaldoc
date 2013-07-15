package com.logicaldoc.core.contact;

import java.util.ArrayList;
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
	public List<Contact> findByUser(Long userId, String email) {
		List<Object> params = new ArrayList<Object>();
		if (userId != null)
			params.add(userId);
		if (email != null)
			params.add(email);

		StringBuffer sb = new StringBuffer("");
		if (userId == null)
			sb.append(" _entity.userId is null ");
		else
			sb.append(" _entity.userId=? ");
		if (email != null)
			sb.append(" and _entity.email=? ");
		
		return findByWhere(sb.toString(), params.toArray(new Object[0]),
				"order by _entity.firstName, _entity.lastName", null);
	}
}