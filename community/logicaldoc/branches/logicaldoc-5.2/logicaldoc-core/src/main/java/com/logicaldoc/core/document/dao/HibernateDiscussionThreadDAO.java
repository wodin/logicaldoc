package com.logicaldoc.core.document.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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

	@Override
	public List<DiscussionComment> findCommentsByUserId(long userId, Integer maxEntries) {
		String query = "select ld_threadid, ld_replyto, ld_replypath, ld_userid, ld_username, ld_date, ld_subject, ld_body, ld_deleted, ld_id"
				+ " from ld_dcomment where ld_deleted = 0 and  ld_userid=" + userId + " order by ld_date desc";
		
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

				return comment;
			}
		};
		
		List<DiscussionComment> comments = new ArrayList<DiscussionComment>();
		
		List elements = query(query, new Object[]{}, maxEntries, discussionMapper);
		for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
			DiscussionComment comment = (DiscussionComment) iterator.next();	
			comments.add(comment);
		}
		
		return comments;
	}
}
