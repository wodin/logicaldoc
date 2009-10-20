package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;

/**
 * DAO for <code>History</code> handling.
 * 
 * @author Michael Scholz
 * @author Alessandro Gasparini - Logical Objects
 */
public interface HistoryDAO extends PersistentObjectDAO<History> {
	/**
	 * This method selects all histories of a given document.
	 * 
	 * @param docId - ID of the document.
	 * @return list of histories ordered by date
	 */
	public List<History> findByDocId(long docId);

	/**
	 * This method selects all histories of a given user.
	 * 
	 * @param userId
	 * @return list of histories ordered by date
	 */
	public List<History> findByUserId(long userId);
	
	/**
	 * This method selects all histories of a given folder.
	 * 
	 * @param folderId - ID of the document.
	 * @return list of histories ordered by date
	 */
	public List<History> findByFolderId(long folderId);
	
	/**
	 * Creates history entry saying username has checked in document (id)
	 * 
	 * @param doc The document for which enter the history
	 * @param user The user that made the operation
	 * @param eventType The event type
	 * @param comment The comment provided by the user
	 */
	public void createDocumentHistory(Document doc, User user, String eventType, String comment);

	/**
	 * Creates folder history entry
	 * 
	 * @param folder The folder for which enter the history
	 * @param user The user that made the operation
	 * @param eventType The event type
	 * @param comment The comment provided by the user
	 */
	public void createFolderHistory(Menu folder, User user, String eventType, String comment);
}