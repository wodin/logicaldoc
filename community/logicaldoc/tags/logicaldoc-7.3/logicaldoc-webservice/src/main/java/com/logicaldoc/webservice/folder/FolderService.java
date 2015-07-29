package com.logicaldoc.webservice.folder;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.logicaldoc.webservice.auth.Right;

/**
 * Folder Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 * 
 */
@WebService
public interface FolderService {
	/**
	 * Create a new folder. The user can completely customize the folder through
	 * a value object containing the folder's metadata.
	 * 
	 * @param sid Session identifier
	 * @param folder value object containing the folder's metadata
	 * @return The value object containing the folder's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "folder")
	public WSFolder create(@WebParam(name = "sid") String sid, @WebParam(name = "folder") WSFolder folder)
			throws Exception;

	/**
	 * Create a new folder.
	 * 
	 * @param sid Session identifier
	 * @param parentId The parent's ID
	 * @param name The new folder's name
	 * @return The newly created folder ID
	 * @throws Exception
	 */
	@WebResult(name = "folderId")
	public long createFolder(@WebParam(name = "sid") String sid, @WebParam(name = "parentId") long parentId,
			@WebParam(name = "name") String name) throws Exception;

	/**
	 * Deletes an existing folder with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @throws Exception
	 */
	public void delete(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId) throws Exception;

	/**
	 * Renames an existing folder with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @param name The new folder name
	 * @throws Exception
	 */
	public void rename(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId,
			@WebParam(name = "name") String name) throws Exception;

	/**
	 * Updates an existing folder. To perform this you need the RENAME
	 * permission.
	 * 
	 * @param sid Session identifier
	 * @param folder The folders metadata(please compile the ID)
	 * @throws Exception
	 */
	public void update(@WebParam(name = "sid") String sid, @WebParam(name = "folder") WSFolder folder) throws Exception;

	/**
	 * Moves an existing folder with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @param parentId The folder id of the new parent folder
	 * @throws Exception
	 */
	public void move(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId,
			@WebParam(name = "parentId") long parentId) throws Exception;

	/**
	 * Copies an existing folder with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @param targetId The folder id of the target folder
	 * @param foldersOnly If 1, only the folders will be copied and not the
	 *        documents
	 * @param inheritSecurity If 1, the new folders will inherit the target's
	 *        security policies
	 * @throws Exception
	 */
	public void copy(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId,
			@WebParam(name = "targetId") long targetId, @WebParam(name = "foldersOnly") int foldersOnly,
			@WebParam(name = "inheritSecurity") int inheritSecurity) throws Exception;

	/**
	 * Gets folder metadata of an existing folder with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @return A value object containing the folder's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "folder")
	public WSFolder getFolder(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Gets root metadata
	 * 
	 * @param sid Session identifier
	 * @return A value object containing the folder's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "folder")
	public WSFolder getRootFolder(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Gets the Default workspace
	 * 
	 * @param sid Session identifier
	 * @return A value object containing the workspace's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "workspace")
	public WSFolder getDefaultWorkspace(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Test if a folder identifier is readable.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @return True if the identifier denotes a readable folder, otherwise
	 *         false.
	 * @throws Exception
	 */
	public boolean isReadable(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Test if a folder identifier is writeable.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @return True if the identifier denotes a writeable folder, otherwise
	 *         false.
	 * @throws Exception
	 */
	public boolean isWriteable(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Test if the current user has a specific permission.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @param permission The permission representation
	 * @return True if the identifier denotes a granted permission, otherwise
	 *         false.
	 * @throws Exception
	 */
	public boolean isGranted(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId,
			@WebParam(name = "permission") int permission) throws Exception;

	/**
	 * Lists all direct folders of a parent folder.<br>
	 * Attention: readable only sub-folders are returned.
	 * 
	 * @param sid Session identifier
	 * @param folderId
	 * @return Array of folders contained in the folder
	 * @throws Exception
	 */
	@WebResult(name = "folder")
	public WSFolder[] listChildren(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Computes the path from the root to the target folder.
	 * 
	 * @param sid Session identifier
	 * @param folderId The target folder id
	 * @return The list of folder, the first is the root, the last is the target
	 *         folder
	 * @throws Exception
	 */
	@WebResult(name = "folders")
	public WSFolder[] getPath(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Grant user permission to the folder.
	 * 
	 * @param sid Session identifier
	 * @param folderId Folder id
	 * @param userId User Id
	 * @param permissions the permission integer representation. If '0', the
	 *        user will be not granted to access the folder.
	 * @param recursive recursion option. If true, the grant operation is
	 *        applied also to the subfolders.
	 * @throws Exception
	 */
	public void grantUser(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId,
			@WebParam(name = "userId") long userId, @WebParam(name = "permissions") int permissions,
			@WebParam(name = "recursive") boolean recursive) throws Exception;

	/**
	 * Grant group permission to the folder.
	 * 
	 * @param sid Session identifier
	 * @param folderId Folder id
	 * @param groupId Group Id
	 * @param permissions the permission integer representation. If '0', the
	 *        group will be not granted to access the folder.
	 * @param recursive recursion option. If true, the grant operation is
	 *        applied also to the subfolders.
	 * @throws Exception
	 */
	public void grantGroup(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId,
			@WebParam(name = "groupId") long groupId, @WebParam(name = "permissions") int permissions,
			@WebParam(name = "recursive") boolean recursive) throws Exception;

	/**
	 * Retrieves the list of granted users for the given folder.
	 * 
	 * @param sid Session identifier
	 * @param folderId Folder id
	 * @return 'error' if error occurred, the right objects collection.
	 * @throws Exception
	 */
	public Right[] getGrantedUsers(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Retrieves the list of granted groups for the given folder.
	 * 
	 * @param sid Session identifier
	 * @param folderId Folder id
	 * @return 'error' if error occurred, the right objects collection.
	 * @throws Exception
	 */
	public Right[] getGrantedGroups(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Creates the folder for the specified path. All unexisting nodes specified
	 * in the path will be created.
	 * 
	 * @param sid Session identifier
	 * @param parentId The parent folder
	 * @param path The folder path(for example /Default/dog/cat/mouse)
	 * 
	 * @return The created folder
	 */
	@WebResult(name = "folder")
	public WSFolder createPath(@WebParam(name = "sid") String sid, @WebParam(name = "parentId") long parentId,
			@WebParam(name = "path") String path) throws Exception;

	/**
	 * Finds the folder at the specified path
	 * 
	 * @param sid Session identifier
	 * @param path The folder path(for example /Default/dog/cat/mouse)
	 * 
	 * @return The created folder
	 */
	@WebResult(name = "folder")
	public WSFolder findByPath(@WebParam(name = "sid") String sid, @WebParam(name = "path") String path)
			throws Exception;

	/**
	 * Retrieves the list of all workspaces.
	 * 
	 * @param sid Session identifier
	 * 
	 * @return the list of all workspaces
	 */
	@WebResult(name = "workspaces")
	public WSFolder[] listWorkspaces(@WebParam(name = "sid") String sid) throws Exception;
}