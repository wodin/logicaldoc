package com.logicaldoc.core.rss.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.rss.FeedMessage;
import com.logicaldoc.util.sql.SqlUtil;

/**
 * Hibernate implementation of <code>FeedMessageDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class HibernateFeedMessageDAO extends HibernatePersistentObjectDAO<FeedMessage> implements FeedMessageDAO {
	public HibernateFeedMessageDAO() {
		super(FeedMessage.class);
		super.log = LogFactory.getLog(HibernateFeedMessageDAO.class);
	}

	@Override
	public List<FeedMessage> findAll() {
		return findByWhere(" 1=1", "order by _entity.pubDate desc", null);
	}

	@Override
	public FeedMessage findByGuid(String guid) {
		FeedMessage feedMessage = null;
		List<FeedMessage> coll = findByWhere("_entity.guid = '" + SqlUtil.doubleQuotes(guid) + "'", null, null);
		if (coll.size() > 0) {
			feedMessage = coll.iterator().next();
		}
		if (feedMessage != null && feedMessage.getDeleted() == 1)
			feedMessage = null;
		return feedMessage;
	}

	@Override
	public boolean checkNotRead() {
		String query = "select count(ld_id) from ld_feedmessage where ld_deleted=0 and ld_read <> 0";
		return queryForInt(query) > 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteOld() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);
		log.debug("delete all feed message before " + cal.getTime());
		try {
			Collection<FeedMessage> coll = (Collection<FeedMessage>) getHibernateTemplate().find(
					"from FeedMessage _feedmessage where _feedmessage.deleted=0 and _feedmessage.pubDate < ?",
					cal.getTime());
			for (FeedMessage feedMessage : coll) {
				getHibernateTemplate().initialize(feedMessage);
				feedMessage.setDeleted(1);
				getHibernateTemplate().saveOrUpdate(feedMessage);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled())
				logger.error(e.getMessage(), e);
		}
	}
}
