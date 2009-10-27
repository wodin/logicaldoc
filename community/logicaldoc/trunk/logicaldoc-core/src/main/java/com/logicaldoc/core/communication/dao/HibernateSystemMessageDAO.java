package com.logicaldoc.core.communication.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.jdbc.object.SqlUpdate;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.communication.SystemMessage;
import com.logicaldoc.util.StringUtil;
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
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#findByRecipient(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<SystemMessage> findByRecipient(String recipient) {
		return findByWhere("_entity.recipient = '" + SqlUtil.doubleQuotes(recipient)+"'", null, "order by _entity.sentDate asc");
	}

	/**
	 * @see com.logicaldoc.core.communication.dao.SystemMessageDAO#getCount(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public int getCount(String recipient) {
		int count = 0;
     	count = findIdsByWhere("_entity.recipient='" + SqlUtil.doubleQuotes(recipient) + "' and _entity.read =" + new Integer(0), null).size();
		return count;
	}

	public void deleteExpiredMessages(String recipient) {
		collectGarbage(findByRecipient(recipient), true);
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
}