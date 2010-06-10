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
	 * Renames the given folder
	 */
	public void rename(String sid, long folderId, String name);

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

	/**
	 * Moves a folder under a target folder
	 */
	public void move(String sid, long folderId, long targetId);

	/**
	 * Pastes documents into the target folder.
	 * 
	 * @param docIds The documents identifiers.
	 * @param folderId The target folder identifier.
	 * @param action The action selectee (Clipboard#COPY or Clipboard#COPY).
	 */
	public void paste(String sid, long[] docIds, long folderId, String action);

	/**
	 * Pastes documents alias into the target folder.
	 * 
	 * @param docIds The documents alias identifiers.
	 * @param folderId The target folder identifier.
	 */
	public void pasteAsAlias(String sid, long[] docIds, long folderId);
}
