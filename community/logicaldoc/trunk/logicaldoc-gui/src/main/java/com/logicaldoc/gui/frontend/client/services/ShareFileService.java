package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;

/**
 * The client side stub for the ShareFile Service.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.2.1
 */
@RemoteServiceRelativePath("sharefile")
public interface ShareFileService extends RemoteService {
	/**
	 * Exports documents and folders into ShareFille
	 * 
	 * @param sid The session ID
	 * @param targetFolder the target folder in ShareFile
	 * @param folderIds Ids of the folders to be imported (all subfolders and
	 *        docs will be imported as well
	 * @param docIds Ids of the documents to be imported
	 * @return
	 * @throws ServerException
	 */
	public boolean exportDocuments(String sid, String targetFolder, long[] folderIds, long[] docIds)
			throws ServerException;

	public int importDocuments(String sid, long targetFolder, String[] itemIds) throws ServerException;

	/**
	 * Save the settings used by the FileShare module
	 * 
	 * @param sid
	 * @param hostname
	 * @param username
	 * @param password
	 * @throws ServerException
	 */
	public void saveSettings(String sid, String hostname, String username, String password) throws ServerException;

	/**
	 * Retrieve the settings saved for connecting to FileShare.
	 */
	public String[] loadSettings(String sid) throws ServerException;
}