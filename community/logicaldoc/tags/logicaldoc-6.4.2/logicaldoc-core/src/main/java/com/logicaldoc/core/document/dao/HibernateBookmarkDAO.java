package com.logicaldoc.core.document.dao;

import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.Bookmark;

/**
 * Hibernate implementation of <code>BookmarkDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class HibernateBookmarkDAO extends HibernatePersistentObjectDAO<Bookmark> implements BookmarkDAO {

	public HibernateBookmarkDAO() {
		super(Bookmark.class);
		super.log = LogFactory.getLog(HibernateBookmarkDAO.class);
	}

	@Override
	public List<Bookmark> findByUserId(long userId) {
		return findByWhere("_entity.userId =" + userId, null, "order by _entity.position asc", null);
	}

	@Override
	public List<Bookmark> findByUserIdAndDocId(long userId, long docId) {
		return findByWhere("_entity.userId =" + userId + " and _entity.targetId =" + docId + " and _entity.type="
				+ Bookmark.TYPE_DOCUMENT, null, "order by _entity.position asc", null);
	}
}
