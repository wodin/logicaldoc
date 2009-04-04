package com.logicaldoc.core.document.dao;

import java.util.Collection;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.DownloadTicket;

/**
 * Hibernate implementation of <code>DownloadTicketDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDownloadTicketDAO extends HibernatePersistentObjectDAO<DownloadTicket> implements
		DownloadTicketDAO {

	public HibernateDownloadTicketDAO() {
		super(DownloadTicket.class);
		super.log = LogFactory.getLog(HibernateDownloadTicketDAO.class);
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DownloadTicketDAO#deleteByTicketId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean deleteByTicketId(String ticketid) {
		boolean result = true;
		try {
			DownloadTicket ticket = findByTicketId(ticketid);
			if (ticket != null) {
				ticket.setDeleted(1);
				getHibernateTemplate().saveOrUpdate(ticket);
			}
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
			DownloadTicket ticket = null;
			if (!coll.isEmpty()) {
				ticket = coll.iterator().next();
				if (ticket.getDeleted() == 0)
					return ticket;
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}
		return null;
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
			for (DownloadTicket downloadTicket : coll) {
				downloadTicket.setDeleted(1);
				getHibernateTemplate().saveOrUpdate(downloadTicket);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}
}