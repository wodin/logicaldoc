package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUIRating;
import com.logicaldoc.gui.common.client.beans.GUIVersion;

/**
 * The client side stub for the Document Service. This service allows r/w
 * operations on documents.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("document")
public interface DocumentService extends RemoteService {
	/**
	 * Retrieves a specific document by its ID
	 */
	public GUIDocument getById(String sid, long docId) throws ServerException;

	/**
	 * Saves the document in the DB
	 * 
	 * @param document The document to save
	 * @return The saved document
	 */
	public GUIDocument save(String sid, GUIDocument document) throws Exception;

	/**
	 * Retrieves all attributes of the specified template
	 */
	public GUIExtendedAttribute[] getAttributes(String sid, long templateId) throws ServerException;

	/**
	 * Retrieves two specific versions by its ID
	 */
	public GUIVersion[] getVersionsById(String sid, long id1, long id2) throws ServerException;

	/**
	 * Sends a document as email(attachment or download ticket)
	 * 
	 * @return "ok" otherwise an error code
	 */
	public String sendAsEmail(String sid, GUIEmail email) throws ServerException;

	/**
	 * Updates the links type
	 * 
	 * @param sid The session identifier
	 * @param id The link identifier
	 * @param type The new type to be set
	 */
	public void updateLink(String sid, long id, String type) throws ServerException;

	/**
	 * Deletes a selection of links
	 */
	public void deleteLinks(String sid, long[] ids) throws ServerException;

	/**
	 * Deletes a selection of versions
	 */
	public GUIDocument deleteVersions(String sid, long[] ids) throws ServerException;

	/**
	 * Links a set of documents
	 */
	public void linkDocuments(String sid, long[] inDocIds, long[] outDocIds) throws ServerException;

	/**
	 * Deletes a selection of documents
	 */
	public void delete(String sid, long[] ids) throws ServerException;

	/**
	 * Deletes a selection of documents from trash
	 */
	public void deleteFromTrash(String sid, Long[] ids) throws ServerException;

	/**
	 * Clear the user's trash
	 */
	public void emptyTrash(String sid) throws ServerException;

	/**
	 * Makes immutable a set of documents
	 */
	public void makeImmutable(String sid, long[] docIds, String comment) throws ServerException;

	/**
	 * Archives a set of documents
	 */
	public void archiveDocuments(String sid, long[] docIds, String comment) throws ServerException;

	/**
	 * Archives the documents in a folder
	 */
	public long archiveFolder(String sid, long folderId, String comment) throws ServerException;

	/**
	 * Counts the documents in a given status contained the specified folder's
	 * tree
	 */
	public long countDocuments(String sid, long folderId, int status) throws ServerException;

	/**
	 * Unlocks a set of documents
	 */
	public void unlock(String sid, long[] docIds) throws ServerException;

	/**
	 * Locks a set of documents
	 */
	public void lock(String sid, long[] docIds, String comment) throws ServerException;

	/**
	 * Checks out the document
	 */
	public void checkout(String sid, long docId) throws ServerException;

	/**
	 * Adds new documents previously uploaded
	 * 
	 * @param sid The session identifier
	 * @param language The language applied to all documents
	 * @param folderId The destination folder identifier
	 * @param importZip If .zip files have to be unpacked and the contained
	 *        documents imported
	 * @param immediteIndexing If the documents must be immediately indexed
	 * @param templateId The documents template
	 */
	public void addDocuments(String sid, String language, long folderId, boolean importZip, boolean immediateIndexing,
			Long templateId) throws ServerException;

	public void addDocuments(String sid, boolean importZip, boolean immediateIndexing, GUIDocument metadata)
			throws ServerException;

	/**
	 * Indexes the gien set of documents
	 * 
	 * @param sid The session identifier
	 * @param docIds The set of documents to index
	 */
	public void indexDocuments(String sid, Long[] docIds) throws ServerException;

	/**
	 * Checks-in a new document version
	 * 
	 * @param sid The session identifier
	 * @param document The document to update
	 * @param major True if this is a major version
	 * 
	 * @returns The updated document
	 */
	public GUIDocument checkin(String sid, GUIDocument document, boolean major) throws Exception;

	/**
	 * Restores a given document
	 */
	public void restore(String sid, long[] docIds, long folderId) throws ServerException;

	/**
	 * Restores a given set of archived documents
	 */
	public void unarchiveDocuments(String sid, long[] docIds) throws ServerException;

	/**
	 * Adds new bookmarks
	 */
	public void addBookmarks(String sid, long[] targetIds, int type) throws ServerException;

	/**
	 * Deletes a set of bookmarks
	 */
	public void deleteBookmarks(String sid, long[] bookmarkIds) throws ServerException;

	/**
	 * Updates a single bookmark's data
	 */
	public void updateBookmark(String sid, GUIBookmark bookmark) throws ServerException;

	/**
	 * Marks as read the histories related to the current user and the given
	 * event.
	 * 
	 * @param sid The session identifier
	 * @param event The history event to mark as read
	 */
	public void markHistoryAsRead(String sid, String event) throws ServerException;

	/**
	 * Marks a set of documents as unindexable
	 */
	public void markUnindexable(String sid, long[] docIds) throws ServerException;

	/**
	 * Marks a set of documents as indexable
	 */
	public void markIndexable(String sid, long[] docIds) throws ServerException;

	/**
	 * Cleans the uploaded files folder.
	 */
	public void cleanUploadedFileFolder(String sid) throws ServerException;

	/**
	 * Retrieves the rating of the given document.
	 */
	public GUIRating getRating(String sid, long docId) throws ServerException;

	/**
	 * Save a rating vote on a document.
	 * 
	 * @return the new document rating value
	 */
	public int saveRating(String sid, GUIRating rating) throws ServerException;

	/**
	 * Adds a new document note on the given document
	 */
	public long addNote(String sid, long docId, String message) throws ServerException;

	/**
	 * Updates a document note on the given document
	 */
	public void updateNote(String sid, long docId, long noteId, String message) throws ServerException;

	/**
	 * Deletes a selection of document notes
	 */
	public void deleteNotes(String sid, long[] ids) throws ServerException;

	/**
	 * Applies to a selection of documents all the given data.
	 */
	public void bulkUpdate(String sid, long[] ids, GUIDocument vo) throws ServerException;

	/**
	 * Creates a new empty document
	 */
	public GUIDocument createEmpty(String sid, GUIDocument vo) throws ServerException;
}