package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.DocumentNote;

/**
 * DAO for <code>DocumentNote</code> handling.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public interface DocumentNoteDAO extends PersistentObjectDAO<DocumentNote> {

	/**
	 * This method finds the list of document note regarding a document with the
	 * given ID.
	 * 
	 * @param docId ID of the document.
	 * @return The list of document note.
	 */
	public List<DocumentNote> findByDocId(long docId);

	/**
	 * This method finds the list of document notes regarding posted by a
	 * specific user.
	 * 
	 * @param userId ID of the user
	 * @return The list of document notes ordered by descending date
	 */
	public List<DocumentNote> findByUserId(long userId);
}