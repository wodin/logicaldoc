package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIDocument;

/**
 * The client side stub for the Google Docs Service.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.7
 */
@RemoteServiceRelativePath("gdocs")
public interface GDocsService extends RemoteService {

	/**
	 * Uploads a document to Google Docs.
	 * 
	 * @param docId ID of the document to upload
	 * 
	 * @returns The resourceId of the uploaded document
	 */
	public String upload(String sid, long docId) throws ServerException;

	/**
	 * Deletes a document to Google Docs.
	 * 
	 * @param docId ID of the document to delete
	 */
	public void delete(String sid, String resourceId) throws ServerException;

	/**
	 * Performs the check-in of a Google Docs's document into the LogicalDOC
	 * repository.
	 * 
	 * @param sid The session identifier
	 * @param docId ID of the document to update
	 * @param comment The comment left for the checkin
	 * @param major If this is a major or minor release
	 * 
	 * @return The checked-in document
	 */
	public GUIDocument checkin(String sid, long docId, String comment, boolean major) throws ServerException;

	/**
	 * Performs the creation of a new Google Docs's document.
	 * 
	 * @param sid The session identifier
	 * @param title Title of the new document
	 * @param type The type of the new document ('doc', 'xls' ...)
	 * 
	 * @returns The resource ID of the newly created document
	 */
	public String create(String sid, String title, String type) throws ServerException;

	/**
	 * Imports some Google documents into LogicalDOC
	 * 
	 * @param sid The session identifier
	 * @param resourceIds IDS of the documents to import
	 * @param targetFolderId ID of the import folder
	 * @param format The type of the documents
	 */
	public void importDocuments(String sid, String[] resourceIds, long targetFolderId, String format)
			throws ServerException;

	/**
	 * Exports a selection of documents from LogicalDOC into GoogleDocs
	 * 
	 * @param sid The session identifier
	 * @param ids The ids of the document to be exported
	 * @return The list of the imported documents into Google Docs
	 * @throws ServerException
	 */
	public String[] exportDocuments(String sid, long[] ids) throws ServerException;

	/**
	 * Save the settings used by the Google Docs module
	 * 
	 * @param sid
	 * @param username
	 * @param password
	 * @throws ServerException
	 */
	public void saveSettings(String sid, String username, String password) throws ServerException;

	/**
	 * Retrieve the settings saved for connecting to Google Docs.
	 */
	public String[] loadSettings(String sid) throws ServerException;

	/**
	 * Search in documents into Google Docs
	 * 
	 * @param sid
	 * @param expression
	 * @throws ServerException
	 */
	public GUIDocument[] search(String sid, String expression) throws ServerException;
}