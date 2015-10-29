package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIValue;

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
	public GUIFolder save(String sid, GUIFolder folder) throws ServerException;

	/**
	 * Creates a new folder
	 * 
	 * @param newFolder The folder to be created
	 * @param inheritSecurity True if the new folder has to inherit the security
	 *        policies from the parent
	 * @return The saved folder
	 */
	public GUIFolder create(String sid, GUIFolder newFolder, boolean inheritSecurity) throws ServerException;

	/**
	 * Renames the given folder
	 */
	public void rename(String sid, long folderId, String name) throws ServerException;

	/**
	 * Applies all security settings to folder
	 * 
	 * @param sid The session ID
	 * @param folder The folder that contains the new security settings
	 * @param subfolders If true, the current security settings will be applied
	 *        to the sub-folders
	 */
	public void applyRights(String sid, GUIFolder folder, boolean subfolders) throws ServerException;

	/**
	 * Inherits the rights of another folder
	 * 
	 * @param sid The session ID
	 * @param folderId The folder that has to be updated
	 * @param rightsFolderId the folder that defines the rights
	 * 
	 * @return The updated Folder
	 */
	public GUIFolder inheritRights(String sid, long folderId, long rightsFolderId) throws ServerException;

	/**
	 * Applies all extendedAttributes to a sub-tree
	 * 
	 * @param sid The session ID
	 * @param parentId The parent folder containing the metadata
	 */
	public void applyMetadata(String sid, long parentId) throws ServerException;

	/**
	 * Gets the Folder initializing the permissions.
	 * 
	 * @param folderId The folder identifier
	 * @param boolean True if the complete path must be retrieved
	 * @return The Folder bean
	 */
	public GUIFolder getFolder(String sid, long folderId, boolean computePath) throws ServerException;

	/**
	 * Deletes the folder and the subtree
	 */
	public void delete(String sid, long folderId) throws ServerException;

	/**
	 * Deletes a selection of folders from trash
	 */
	public void deleteFromTrash(String sid, Long[] ids) throws ServerException;
	
	/**
	 * Restores a given folder
	 */
	public void restore(String sid, long folderId, long parentId) throws ServerException;

	/**
	 * Moves a folder under a target folder
	 */
	public void move(String sid, long folderId, long targetId) throws ServerException;

	/**
	 * Copies a folder under a target folder
	 */
	public void copyFolder(String sid, long folderId, long targetId, boolean foldersOnly, boolean inheritSecurity) throws ServerException;

	/**
	 * Pastes documents into the target folder.
	 * 
	 * @param docIds The documents identifiers.
	 * @param folderId The target folder identifier.
	 * @param action The action selectee (Clipboard#COPY or Clipboard#COPY).
	 */
	public void paste(String sid, long[] docIds, long folderId, String action) throws ServerException;

	void pasteAsAlias(String sid, long[] docIds, long folderId, String type) throws ServerException;

	/**
	 * Loads the folders templates
	 */
	public GUIValue[] loadTemplates(String sid) throws ServerException;

	/**
	 * Saves the passed folder templates
	 */
	public void saveTemplates(String sid, GUIValue[] templates) throws ServerException;

	/**
	 * Applies a template to a folder
	 */
	public void applyTemplate(String sid, long folderId, long templateId, boolean inheritSecurity) throws ServerException;
}