package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.beans.GUIFolder;

/**
 * The client side stub for the Folder Service. This service allows r/w
 * operations on folders.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
@RemoteServiceRelativePath("folder")
public interface FolderService extends RemoteService {
	/**
	 * Saves the folder in the DB
	 * 
	 * @param folder The folder to save
	 * @return The saved folder
	 */
	public GUIFolder save(String sid, GUIFolder folder);

	/**
	 * Applies all security settings to a tree of folders
	 * 
	 * @param sid The session ID
	 * @param folderId The ID of the root folder
	 */
	public void applyRightsToTree(String sid, long folderId);

	/**
	 * Gets the Folder initializing the permissions.
	 * 
	 * @param folderId The folder identifier
	 * @param boolean True if the complete path must be retrieved
	 * @return The Folder bean
	 */
	public GUIFolder getFolder(String sid, long folderId, boolean computePath);

	/**
	 * Deletes the folder and the subtree
	 */
	public void delete(String sid, long folderId);
}
