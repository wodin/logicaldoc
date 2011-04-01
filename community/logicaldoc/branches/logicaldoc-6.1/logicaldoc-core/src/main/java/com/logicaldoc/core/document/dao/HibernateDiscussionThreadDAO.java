package com.logicaldoc.core.document.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

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

	@SuppressWarnings("unchecked")
	@Override
	public List<DiscussionComment> findCommentsByUserId(long userId, Integer maxEntries) {
		String query = "select A.ld_threadid, A.ld_replyto, A.ld_replypath, A.ld_userid, A.ld_username, A.ld_date, A.ld_subject, A.ld_body, A.ld_deleted, A.ld_id, B.ld_docid"
				+ " from ld_dcomment A, ld_dthread B where A.ld_deleted = 0 and B.ld_deleted=0 and A.ld_threadid=B.ld_id and A.ld_userid=" + userId + " order by A.ld_date desc";

		RowMapper discussionMapper = new BeanPropertyRowMapper() {

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

				DiscussionComment comment = new DiscussionComment();
				comment.setThreadId(rs.getLong(1));
				comment.setReplyTo(rs.getInt(2));
				comment.setReplyPath(rs.getString(3));
				comment.setUserId(rs.getLong(4));
				comment.setUserName(rs.getString(5));
				comment.setDate(rs.getDate(6));
				comment.setSubject(rs.getString(7));
				comment.setBody(rs.getString(8));
				comment.setDeleted(rs.getInt(9));
				comment.setDocId(rs.getLong(11));

				return comment;
			}
		};

		List<DiscussionComment> comments = (List<DiscussionComment>) query(query, new Object[] {}, discussionMapper,
				maxEntries);
		return comments;
	}
}
