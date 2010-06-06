package com.logicaldoc.core.document.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.DiscussionComment;
import com.logicaldoc.core.document.DiscussionThread;

/**
 * Hibernate implementation of <code>DiscussionThreadDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class HibernateDiscussionThreadDAO extends HibernatePersistentObjectDAO<DiscussionThread> implements
		DiscussionThreadDAO {

	public HibernateDiscussionThreadDAO() {
		super(DiscussionThread.class);
		super.log = LogFactory.getLog(HibernateDiscussionThreadDAO.class);
	}

	@Override
	public List<DiscussionThread> findByDocId(long docId) {
		return findByWhere("_entity.docId = ?", new Object[] { new Long(docId) }, "order by _entity.lastPost desc",
				null);
	}

	@Override
	public boolean delete(long id) {
		DiscussionThread dt = findById(id);
		for (DiscussionComment comment : dt.getComments()) {
			comment.setDeleted(1);
		}
		store(dt);
		return super.delete(id);
	}

	@Override
	public void initialize(DiscussionThread thread) {
		getHibernateTemplate().refresh(thread);

		for (DiscussionComment comment : thread.getComments()) {
			comment.getSubject();
		}
	}

	@Override
	public List<DiscussionComment> findCommentsByUserId(long userId, Integer maxEntries) {
		String query = "select ld_threadid, ld_replyto, ld_replypath, ld_userid, ld_username, ld_date, ld_subject, ld_body, ld_deleted, ld_id"
				+ " from ld_dcomment where ld_deleted = 0 and  ld_userid=" + userId + " order by ld_date desc";
		List<Object> result = (List<Object>) findByJdbcQuery(query, 10, new Object[] {});

		List<DiscussionComment> comments = new ArrayList<DiscussionComment>();
		int i = 0;
		for (Iterator iterator = result.iterator(); iterator.hasNext()
				&& (maxEntries == null || i < maxEntries.intValue()); i++) {
			Object[] record = (Object[]) iterator.next();
			DiscussionComment comment = new DiscussionComment();
			comment.setThreadId((Long) record[0]);
			comment.setReplyTo((Integer) record[1]);
			comment.setReplyPath((String) record[2]);
			comment.setUserId((Long) record[3]);
			comment.setUserName((String) record[4]);
			comment.setDate((Date) record[5]);
			comment.setSubject((String) record[6]);
			comment.setBody((String) record[7]);
			comment.setDeleted((Integer) record[8]);
			comments.add(comment);
		}
		return comments;
	}
}
