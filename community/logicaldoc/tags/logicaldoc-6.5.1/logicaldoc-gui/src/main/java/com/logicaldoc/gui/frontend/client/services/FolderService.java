package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;

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
	public GUIFolder save(String sid, GUIFolder folder) throws InvalidSessionException;

	/**
	 * Renames the given folder
	 */
	public void rename(String sid, long folderId, String name) throws InvalidSessionException;

	/**
	 * Applies all security settings to folder
	 * 
	 * @param sid The session ID
	 * @param folder The folder that contains the new security settings
	 * @param subfolders If true, the current security settings will be applied
	 *        to the sub-folders
	 */
	public void applyRights(String sid, GUIFolder folder, boolean subfolders) throws InvalidSessionException;

	/**
	 * Applies all extendedAttributes to a sub-tree
	 * 
	 * @param sid The session ID
	 * @param parentId The parent folder containing the metadata
	 */
	public void applyMetadata(String sid, long parentId) throws InvalidSessionException;

	/**
	 * Gets the Folder initializing the permissions.
	 * 
	 * @param folderId The folder identifier
	 * @param boolean True if the complete path must be retrieved
	 * @return The Folder bean
	 */
	public GUIFolder getFolder(String sid, long folderId, boolean computePath) throws InvalidSessionException;

	/**
	 * Deletes the folder and the subtree
	 */
	public void delete(String sid, long folderId) throws InvalidSessionException;

	/**
	 * Moves a folder under a target folder
	 */
	public void move(String sid, long folderId, long targetId) throws InvalidSessionException;

	/**
	 * Pastes documents into the target folder.
	 * 
	 * @param docIds The documents identifiers.
	 * @param folderId The target folder identifier.
	 * @param action The action selectee (Clipboard#COPY or Clipboard#COPY).
	 */
	public void paste(String sid, long[] docIds, long folderId, String action) throws InvalidSessionException;

	/**
	 * Pastes documents alias into the target folder.
	 * 
	 * @param docIds The documents alias identifiers.
	 * @param folderId The target folder identifier.
	 */
	public void pasteAsAlias(String sid, long[] docIds, long folderId) throws InvalidSessionException;

	/**
	 * Loads the folders templates
	 */
	public GUIValuePair[] loadTemplates(String sid) throws InvalidSessionException;

	/**
	 * Saves the passed folder templates
	 */
	public void saveTemplates(String sid, GUIValuePair[] templates) throws InvalidSessionException;

	/**
	 * Applies a template to a folder
	 */
	public void applyTemplate(String sid, long folderId, long templateId) throws InvalidSessionException;
}