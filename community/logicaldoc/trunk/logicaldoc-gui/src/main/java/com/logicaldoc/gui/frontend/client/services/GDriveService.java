package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIDocument;

/**
 * The client side stub for the Google Drive Service.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3
 */
@RemoteServiceRelativePath("gdrive")
public interface GDriveService extends RemoteService {

	/**
	 * Uploads a document to Google Drive.
	 * 
	 * @param docId ID of the document to upload
	 * 
	 * @returns The resourceId of the uploaded document
	 */
	public String upload(String sid, long docId) throws ServerException;

	/**
	 * Deletes a document in Google Drive.
	 * 
	 * @param docId ID of the document to delete
	 */
	public void delete(String sid, String resourceId) throws ServerException;

	/**
	 * Performs the check-in of a Google Drives's document into the LogicalDOC
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
	 * @return The list of the imported documents into Google Drive
	 * @throws ServerException
	 */
	public String[] exportDocuments(String sid, long[] ids) throws ServerException;

	/**
	 * Save the settings used by the Google Drive module
	 * 
	 * @param sid
	 * @return The URL of the consent page
	 * @throws ServerException
	 */
	public String saveSettings(String sid, String clientId, String clientSecret) throws ServerException;

	/**
	 * Save the settings used by the Google Drive module
	 * 
	 * @param sid
	 * @return clientId, clientSecret
	 * @throws ServerException
	 */
	public String[] loadSettings(String sid) throws ServerException;

	/**
	 * Creates a new Google Document
	 * 
	 * @param sid
	 * @return The newly created document's ID in Google Drive
	 * @throws ServerException
	 */
	public String create(String sid, String fileName) throws ServerException;

	/**
	 * Search in documents into Google Drive
	 * 
	 * @param sid
	 * @param expression
	 * @throws ServerException
	 */
	public GUIDocument[] search(String sid, String expression) throws ServerException;
}