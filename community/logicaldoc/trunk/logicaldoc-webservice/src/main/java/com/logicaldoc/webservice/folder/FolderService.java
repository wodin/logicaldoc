package com.logicaldoc.webservice.folder;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Folder Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
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
	public WSFolder create(@WebParam(name = "sid") String sid, @WebParam(name = "folder") WSFolder folder)
			throws Exception;;

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
	 * Gets folder metadata of an existing folder with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @return A value object containing the folder's metadata.
	 * @throws Exception
	 */
	public WSFolder getFolder(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Test if a folder identifier is readable.
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @return True if the identifier denotes a folder, otherwise false.
	 * @throws Exception
	 */
	public boolean isReadable(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;

	/**
	 * Lists the folders inside a folder
	 * 
	 * @param sid Session identifier
	 * @param folderId The folder id
	 * @return Array of folders contained in the folder
	 * @throws Exception
	 */
	public WSFolder[] list(@WebParam(name = "sid") String sid, @WebParam(name = "folderId") long folderId)
			throws Exception;
}
