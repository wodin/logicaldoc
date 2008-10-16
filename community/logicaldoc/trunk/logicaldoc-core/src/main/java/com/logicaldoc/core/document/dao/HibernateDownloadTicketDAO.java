package com.logicaldoc.core.document.dao;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.logicaldoc.core.document.DownloadTicket;

/**
 * Hibernate implementation of <code>DownloadTicketDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id: HibernateDownloadTicketDAO.java,v 1.1 2007/06/29 06:28:28 marco
 *          Exp $
 * @since 3.0
 */
public class HibernateDownloadTicketDAO extends HibernateDaoSupport implements DownloadTicketDAO {

	protected static Log log = LogFactory.getLog(HibernateDownloadTicketDAO.class);

	private HibernateDownloadTicketDAO() {
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DownloadTicketDAO#deleteByTicketId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteByTicketId(String ticketid) {
		boolean result = true;

		try {
			Collection<DownloadTicket> coll = (Collection<DownloadTicket>) getHibernateTemplate().find(
					"from DownloadTicket _ticket where _ticket.ticketId = ?", new Object[] { ticketid });
			getHibernateTemplate().deleteAll(coll);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DownloadTicketDAO#findByTicketId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public DownloadTicket findByTicketId(String ticketid) {
		try {
			Collection<DownloadTicket> coll = (Collection<DownloadTicket>) getHibernateTemplate().find(
					"from DownloadTicket _ticket where _ticket.ticketId = ?", new Object[] { ticketid });
			if (!coll.isEmpty())
				return coll.iterator().next();
			else
				return null;
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DownloadTicketDAO#findByPrimaryKey(long)
	 */
	public DownloadTicket findByPrimaryKey(long ticketId) {
		DownloadTicket ticket = null;

		try {
			ticket = (DownloadTicket) getHibernateTemplate().get(DownloadTicket.class, ticketId);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}
		return ticket;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DownloadTicketDAO#store(com.logicaldoc.core.document.DownloadTicket)
	 */
	public boolean store(DownloadTicket ticket) {
		boolean result = true;

		try {
			getHibernateTemplate().saveOrUpdate(ticket);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DownloadTicketDAO#deleteByDocId(long)
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteByDocId(long docId) {
		boolean result = true;

		try {
			Collection<DownloadTicket> coll = (Collection<DownloadTicket>) getHibernateTemplate().find(
					"from DownloadTicket _ticket where _ticket.docId = ?", new Object[] { new Long(docId) });
			getHibernateTemplate().deleteAll(coll);
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}
}