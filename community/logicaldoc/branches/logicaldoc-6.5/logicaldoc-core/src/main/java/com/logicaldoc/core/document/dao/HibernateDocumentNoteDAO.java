package com.logicaldoc.core.document.dao;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.DocumentNote;

/**
 * Hibernate implementation of <code>DocumentNoteDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public class HibernateDocumentNoteDAO extends HibernatePersistentObjectDAO<DocumentNote> implements DocumentNoteDAO {

	public HibernateDocumentNoteDAO() {
		super(DocumentNote.class);
		super.log = LoggerFactory.getLogger(HibernateDocumentNoteDAO.class);
	}

	@Override
	public List<DocumentNote> findByDocId(long docId) {
		return findByWhere("_entity.docId =" + docId, null, null, null);
	}

	@Override
	public List<DocumentNote> findByUserId(long userId) {
		return findByWhere("_entity.userId =" + userId, null, "order by _entity.date desc", null);
	}
}