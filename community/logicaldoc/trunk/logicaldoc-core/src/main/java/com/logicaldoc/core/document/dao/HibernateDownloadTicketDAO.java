package com.logicaldoc.core.document.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.DownloadTicket;
import com.logicaldoc.util.config.ContextProperties;

/**
 * Hibernate implementation of <code>DownloadTicketDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
@SuppressWarnings("unchecked")
public class HibernateDownloadTicketDAO extends HibernatePersistentObjectDAO<DownloadTicket> implements
		DownloadTicketDAO {

	private ContextProperties contextProperties;

	public HibernateDownloadTicketDAO() {
		super(DownloadTicket.class);
		super.log = LoggerFactory.getLogger(HibernateDownloadTicketDAO.class);
	}

	@Override
	public boolean store(DownloadTicket entity) {
		if (entity.getExpired() == null) {
			// Retrieve the time to live
			int ttl = contextProperties.getInt("ticket.ttl");
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, +ttl);
			entity.setExpired(cal.getTime());
		}

		boolean ret = super.store(entity);
		return ret;
	}

	/**
	 * @see com.logicaldoc.core.document.dao.DownloadTicketDAO#deleteByTicketId(java.lang.String)
	 */
	public boolean deleteByTicketId(String ticketid) {
		boolean result = true;
		try {
			DownloadTicket ticket = findByTicketId(ticketid);
			if (ticket != null) {
				ticket.setDeleted(1);
				saveOrUpdate(ticket);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}

		return result;
	}

	@Override
	public DownloadTicket findByTicketId(String ticketid) {
		try {
			Collection<DownloadTicket> coll = (Collection<DownloadTicket>) findByQuery(
					"from DownloadTicket _ticket where _ticket.ticketId = ?1", new Object[] { ticketid }, null);
			DownloadTicket ticket = null;
			if (!coll.isEmpty()) {
				ticket = coll.iterator().next();
				if (ticket.getDeleted() == 0)
					return ticket;
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean deleteByDocId(long docId) {
		boolean result = true;

		try {
			Collection<DownloadTicket> coll = (Collection<DownloadTicket>) findByQuery(
					"from DownloadTicket _ticket where _ticket.docId = ?1", new Object[] { new Long(docId) }, null);
			for (DownloadTicket downloadTicket : coll) {
				downloadTicket.setDeleted(1);
				saveOrUpdate(downloadTicket);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public void deleteOlder() {
		// Retrieve the time to live
		int ttl = contextProperties.getInt("ticket.ttl");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -ttl);
		deleteOlder(cal.getTime());
	}

	@Override
	public boolean deleteOlder(Date date) {
		log.debug("delete all tickets before " + date);
		boolean result = true;
		try {
			Collection<DownloadTicket> coll = (Collection<DownloadTicket>) findByQuery(
					"from DownloadTicket _ticket where _ticket.deleted=0 and _ticket.lastModified < ?1",
					new Object[] { date }, null);
			for (DownloadTicket downloadTicket : coll) {
				initialize(downloadTicket);
				downloadTicket.setDeleted(1);
				saveOrUpdate(downloadTicket);
			}
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
			result = false;
		}
		return result;
	}

	public void setContextProperties(ContextProperties contextProperties) {
		this.contextProperties = contextProperties;
	}
}