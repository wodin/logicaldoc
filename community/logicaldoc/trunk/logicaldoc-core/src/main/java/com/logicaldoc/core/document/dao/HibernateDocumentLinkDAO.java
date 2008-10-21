package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.document.DocumentLink;

/**
 * Hibernate implementation of <code>DocumentLinkDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @version $Id:$
 * @since 4.0
 * 
 */
public class HibernateDocumentLinkDAO extends HibernateDaoSupport implements DocumentLinkDAO {

	protected static Log log = LogFactory.getLog(HibernateDocumentLinkDAO.class);

	private HibernateDocumentLinkDAO() {
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentLinkDAO#store(com.logicaldoc.core.document.DocumentLink)
	 */
	public boolean store(DocumentLink link) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(link);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentLinkDAO#deleteByLinkId(long)
	 */
	@SuppressWarnings("unchecked")
	public boolean delete(long linkid) {
		boolean result = true;

		try {
			Collection<DocumentLink> coll = (Collection<DocumentLink>) getHibernateTemplate().find(
					"from DocumentLink _link where _link.id = ?", new Object[] { linkid });
			getHibernateTemplate().deleteAll(coll);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentLinkDAO#findById(long)
	 */
	public DocumentLink findById(long linkid) {
		DocumentLink link = null;

		try {
			link = (DocumentLink) getHibernateTemplate().get(DocumentLink.class, linkid);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}
		return link;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentLinkDAO#findByDocId(long)
	 */
	@Override
	public Collection<DocumentLink> findByDocId(long docId) {
		return findByDocId(docId, null);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DocumentLinkDAO#findByDocId(long,
	 *      java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<DocumentLink> findByDocId(long docId, String type) {
		Collection<DocumentLink> coll = new ArrayList<DocumentLink>();
		try {
			StringBuffer query = new StringBuffer("from DocumentLink _link where (_link.document1.id = ? ");
			query.append("or _link.document2.id = ?) ");
			if (StringUtils.isNotEmpty(type)) {
				query.append("and _link.type = '");
				query.append(type);
				query.append("'");
			}

			coll = (Collection<DocumentLink>) getHibernateTemplate().find(query.toString(),
					new Object[] { docId, docId });

		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return coll;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DocumentLink findByDocIdsAndType(long docId1, long docId2, String type) {
		if (type == null)
			 return null;
		DocumentLink link = null;
		try {
			StringBuffer query = new StringBuffer("from DocumentLink _link where "
					+ " _link.document1.id = ? and _link.document2.id = ? ");
				query.append("and _link.type = '");
				query.append(type);
				query.append("'");
				
			Collection<DocumentLink> links = (Collection<DocumentLink>) getHibernateTemplate().find(query.toString(),
					new Object[] { docId1, docId2 });
			if (!links.isEmpty())
				link = links.iterator().next();

		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return link;
	}
}