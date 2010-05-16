package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIEmail;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
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
	public GUIDocument getById(String sid, long docId);

	/**
	 * Saves the document in the DB
	 * 
	 * @param document The document to save
	 * @return The saved document
	 */
	public GUIDocument save(String sid, GUIDocument document);

	/**
	 * Retrieves all attributes of the specified template
	 */
	public GUIExtendedAttribute[] getAttributes(String sid, long templateId);

	/**
	 * Retrieves two specific versions by its ID
	 */
	public GUIVersion[] getVersionsById(String sid, long id1, long id2);

	/**
	 * Sends a document as email(attachment or download ticket)
	 * 
	 * @return "ok" otherwise an error code
	 */
	public String sendAsEmail(String sid, GUIEmail email);

	/**
	 * Updates the links type
	 * 
	 * @param sid The session identifier
	 * @param id The link identifier
	 * @param type The new type to be set
	 */
	public void updateLink(String sid, long id, String type);

	/**
	 * Deletes a selection of links
	 */
	public void deleteLinks(String sid, long[] ids);

	/**
	 * Links a set of documents
	 */
	public void linkDocuments(String sid, long[] inDocIds, long[] outDocIds);

	/**
	 * Deletes a selection of documents
	 */
	public void delete(String sid, long[] ids);

	/**
	 * Deletes a selection of discussions
	 */
	public void deleteDiscussions(String sid, long[] ids);

	/**
	 * Starts a new discussion on the given document
	 */
	public long startDiscussion(String sid, long docId, String title, String message);

	/**
	 * Starts a new discussion on the given document
	 */
	public int replyPost(String sid, long discussionId, int replyTo, String title, String message);

	/**
	 * Deletes a selection of posts
	 */
	public void deletePosts(String sid, long discussionId, int[] postIds);

	/**
	 * Makes immutable a set of documents
	 */
	public void makeImmutable(String sid, long[] docIds, String comment);

	/**
	 * Unlocks a set of documents
	 */
	public void unlock(String sid, long[] docIds);

	/**
	 * Locks a set of documents
	 */
	public void lock(String sid, long[] docIds, String comment);

	/**
	 * Checks out the document
	 */
	public void checkout(String sid, long docId);

	/**
	 * Adds new documents previously uploaded
	 * 
	 * @param sid The session identifier
	 * @param language The language applied to all documents
	 * @param importZip If .zip files have to be unpacked and the contained
	 *        documents imported
	 */
	public void addDocuments(String sid, String language, boolean importZip);

	/**
	 * Checks-in a new document version
	 * 
	 * @param sid The session identifier
	 * @param docId The document to update
	 * @param major True if this is a major version
	 */
	public void checkin(String sid, long docId, boolean major);

	/**
	 * Restores a given document
	 */
	public void restore(String sid, long docId);

	/**
	 * Adds new bookmarks
	 */
	public void addBookmarks(String sid, long[] docIds);

	/**
	 * Deletes a set of bookmarks
	 */
	public void deleteBookmarks(String sid, long[] bookmarkIds);

	/**
	 * Updates a single bookmark's data
	 */
	public void updateBookmark(String sid, GUIBookmark bookmark);

	/**
	 * Marks as read the histories related to the current user and the given
	 * event.
	 * 
	 * @param sid The session identifier
	 * @param event The history event to mark as read
	 */
	public void markHistoryAsRead(String sid, String event);
}