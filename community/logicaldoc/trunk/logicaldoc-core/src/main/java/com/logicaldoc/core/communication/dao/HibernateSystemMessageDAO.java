package com.logicaldoc.core.communication.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>SystemMessageDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateSystemMessageDAO extends HibernatePersistentObjectDAO<SystemMessage> implements SystemMessageDAO {
	public HibernateSystemMessageDAO() {
		super(SystemMessage.class);
		super.log = LogFactory.getLog(HibernateSystemMessageDAO.class);
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#findByRecipient(java.lang.String,
	 *      int)
	 */
	@SuppressWarnings("unchecked")
	public List<SystemMessage> findByRecipient(String recipient, int type, Integer read) {
		String query = "select ld_lastmodified, ld_deleted, ld_author, ld_messagetext, ld_subject, ld_sentdate, ld_datescope, ld_prio, ld_confirmation, ld_red, ld_lastnotified, ld_status, ld_trials, ld_type, ld_id"
				+ " from ld_systemmessage where ld_deleted = 0 and ld_id IN (select ld_messageid from ld_recipient where ld_name = '"
				+ SqlUtil.doubleQuotes(recipient) + "' and ld_type = " + type + ")";
		if (read != null)
			query = query + " and ld_red=" + read;
		query = query + " order by ld_sentdate desc";

		List<Object> result = (List<Object>) findByJdbcQuery(query, 15, new Object[] {});

		List<SystemMessage> messages = new ArrayList<SystemMessage>();
		int i = 0;
		for (Iterator iterator = result.iterator(); iterator.hasNext(); i++) {
			Object[] record = (Object[]) iterator.next();
			SystemMessage message = new SystemMessage();
			message.setLastModified((Date) record[0]);
			message.setDeleted((Integer) record[1]);
			message.setAuthor((String) record[2]);
			message.setMessageText((String) record[3]);
			message.setSubject((String) record[4]);
			message.setSentDate((Date) record[5]);
			message.setDateScope((Integer) record[6]);
			message.setPrio((Integer) record[7]);
			message.setConfirmation((Integer) record[8]);
			message.setRead((Integer) record[9]);
			message.setLastNotified((Date) record[10]);
			message.setStatus((Integer) record[11]);
			message.setTrials((Integer) record[12]);
			message.setType((Integer) record[13]);
			message.setId((Long) record[14]);
			messages.add(message);
		}

		return messages;
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#getCount(java.lang.String,
	 *      int)
	 */
	public int getCount(String recipient, int type, Integer read) {
		return findByRecipient(recipient, type, read).size();
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#deleteExpiredMessages(java.lang.String)
	 */
	public void deleteExpiredMessages(String recipient) {
		collectGarbage(findByRecipient(recipient, SystemMessage.TYPE_SYSTEM, null), true);
	}

	/**
	 * Cleans from the passed collection all expired messages
	 * 
	 * @param coll The input messages
	 * @param removeExpired True if expired messages must be deleted
	 * @return The cleaned messages collection
	 */
	protected List<SystemMessage> collectGarbage(Collection<SystemMessage> coll, boolean removeExpired) {
		List<SystemMessage> out = new ArrayList<SystemMessage>();
		try {
			Iterator<SystemMessage> iter = coll.iterator();
			Date date = new Date();
			long time = date.getTime();

			while (iter.hasNext()) {
				SystemMessage sm = (SystemMessage) iter.next();
				long sentdate = new Date().getTime();
				long timespan = sm.getDateScope();
				timespan = timespan * 86400000;
				sentdate += timespan;

				if (time >= sentdate) {
					if (removeExpired)
						delete(sm.getId());
				} else {
					out.add(sm);
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}

		return out;
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#deleteExpiredMessages(int)
	 */
	public void deleteExpiredMessages(int type) {
		collectGarbage(findByType(type), true);
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#findByMode(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<SystemMessage> findByMode(String mode) {
		String query = "select ld_lastmodified, ld_deleted, ld_author, ld_messagetext, ld_subject, ld_sentdate, ld_datescope, ld_prio, ld_confirmation, ld_red, ld_lastnotified, ld_status, ld_trials, ld_type, ld_id"
				+ " from ld_systemmessage where ld_deleted = 0 and ld_id IN (select ld_messageid from ld_recipient where ld_mode = '"
				+ SqlUtil.doubleQuotes(mode) + "') order by ld_sentdate desc";

		List<Object> result = (List<Object>) findByJdbcQuery(query, 15, new Object[] {});

		List<SystemMessage> messages = new ArrayList<SystemMessage>();
		int i = 0;
		for (Iterator iterator = result.iterator(); iterator.hasNext(); i++) {
			Object[] record = (Object[]) iterator.next();
			SystemMessage message = new SystemMessage();
			message.setLastModified((Date) record[0]);
			message.setDeleted((Integer) record[1]);
			message.setAuthor((String) record[2]);
			message.setMessageText((String) record[3]);
			message.setSubject((String) record[4]);
			message.setSentDate((Date) record[5]);
			message.setDateScope((Integer) record[6]);
			message.setPrio((Integer) record[7]);
			message.setConfirmation((Integer) record[8]);
			message.setRead((Integer) record[9]);
			message.setLastNotified((Date) record[10]);
			message.setStatus((Integer) record[11]);
			message.setTrials((Integer) record[12]);
			message.setType((Integer) record[13]);
			message.setId((Long) record[14]);
			messages.add(message);
		}

		return messages;
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#findByType(int)
	 */
	@SuppressWarnings("unchecked")
	public List<SystemMessage> findByType(int type) {
		String query = "select ld_lastmodified, ld_deleted, ld_author, ld_messagetext, ld_subject, ld_sentdate, ld_datescope, ld_prio, ld_confirmation, ld_red, ld_lastnotified, ld_status, ld_trials, ld_type, ld_id"
				+ " from ld_systemmessage where ld_deleted = 0 and ld_id IN (select ld_messageid from ld_recipient where ld_type = "
				+ type + ") order by ld_sentdate desc";

		List<Object> result = (List<Object>) findByJdbcQuery(query, 15, new Object[] {});

		List<SystemMessage> messages = new ArrayList<SystemMessage>();
		int i = 0;
		for (Iterator iterator = result.iterator(); iterator.hasNext(); i++) {
			Object[] record = (Object[]) iterator.next();
			SystemMessage message = new SystemMessage();
			message.setLastModified((Date) record[0]);
			message.setDeleted((Integer) record[1]);
			message.setAuthor((String) record[2]);
			message.setMessageText((String) record[3]);
			message.setSubject((String) record[4]);
			message.setSentDate((Date) record[5]);
			message.setDateScope((Integer) record[6]);
			message.setPrio((Integer) record[7]);
			message.setConfirmation((Integer) record[8]);
			message.setRead((Integer) record[9]);
			message.setLastNotified((Date) record[10]);
			message.setStatus((Integer) record[11]);
			message.setTrials((Integer) record[12]);
			message.setType((Integer) record[13]);
			message.setId((Long) record[14]);
			messages.add(message);
		}

		return messages;
	}

	@Override
	public void initialize(SystemMessage message) {
		getHibernateTemplate().refresh(message);

		for (Recipient recipient : message.getRecipients()) {
			recipient.getName();
		}
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#findMessagesToBeSent(int)
	 */
	@SuppressWarnings("unchecked")
	public List<SystemMessage> findMessagesToBeSent(int type, int maxTrial) {
		String query = "select ld_lastmodified, ld_deleted, ld_author, ld_messagetext, ld_subject, ld_sentdate, ld_datescope, ld_prio, ld_confirmation, ld_red, ld_lastnotified, ld_status, ld_trials, ld_type, ld_id"
				+ " from ld_systemmessage where ld_deleted = 0 and ld_status <> "
				+ SystemMessage.STATUS_DELIVERED
				+ " and ld_type = " + type + " and ld_trials < " + maxTrial + " order by ld_sentdate desc";

		List<Object> result = (List<Object>) findByJdbcQuery(query, 15, new Object[] {});

		List<SystemMessage> messages = new ArrayList<SystemMessage>();
		int i = 0;
		for (Iterator iterator = result.iterator(); iterator.hasNext(); i++) {
			Object[] record = (Object[]) iterator.next();
			SystemMessage message = new SystemMessage();
			message.setLastModified((Date) record[0]);
			message.setDeleted((Integer) record[1]);
			message.setAuthor((String) record[2]);
			message.setMessageText((String) record[3]);
			message.setSubject((String) record[4]);
			message.setSentDate((Date) record[5]);
			message.setDateScope((Integer) record[6]);
			message.setPrio((Integer) record[7]);
			message.setConfirmation((Integer) record[8]);
			message.setRead((Integer) record[9]);
			message.setLastNotified((Date) record[10]);
			message.setStatus((Integer) record[11]);
			message.setTrials((Integer) record[12]);
			message.setType((Integer) record[13]);
			message.setId((Long) record[14]);
			messages.add(message);
		}

		return messages;
	}
}