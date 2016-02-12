package com.logicaldoc.core.document.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.HibernatePersistentObjectDAO;
import com.logicaldoc.core.document.DocumentEvent;
import com.logicaldoc.core.document.DocumentNote;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.util.Context;

/**
 * Hibernate implementation of <code>DocumentNoteDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
@SuppressWarnings("unchecked")
public class HibernateDocumentNoteDAO extends HibernatePersistentObjectDAO<DocumentNote> implements DocumentNoteDAO {

	public HibernateDocumentNoteDAO() {
		super(DocumentNote.class);
		super.log = LoggerFactory.getLogger(HibernateDocumentNoteDAO.class);
	}

	@Override
	public boolean store(DocumentNote note, History transaction) {
		boolean result = super.store(note);
		if (!result)
			return false;

		try {
			if (transaction != null) {
				transaction.setEvent(DocumentEvent.NEW_NOTE.toString());

				DocumentDAO documentDao = (DocumentDAO) Context.getInstance().getBean(DocumentDAO.class);
				documentDao.saveDocumentHistory(documentDao.findById(note.getDocId()), transaction);
			}
		} catch (Throwable e) {
			if (transaction != null && StringUtils.isNotEmpty(transaction.getSessionId())) {
				UserSession session = SessionManager.getInstance().get(transaction.getSessionId());
				session.logError(e.getMessage());
			}
			log.error(e.getMessage(), e);
			result = false;
		}

		return result;
	}

	@Override
	public List<DocumentNote> findByDocId(long docId) {
		return findByWhere("_entity.docId =" + docId, null, null);
	}

	@Override
	public List<DocumentNote> findByUserId(long userId) {
		return findByWhere("_entity.userId =" + userId, "order by _entity.date desc", null);
	}

	@Override
	public void deleteContentAnnotations(long docId) {
		List<DocumentNote> notes = findByDocId(docId);
		for (DocumentNote note : notes)
			if (note.getPage() != 0)
				delete(note.getId());
	}
}