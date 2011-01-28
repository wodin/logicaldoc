package com.logicaldoc.webservice.auth;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Auth Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
@WebService
public interface AuthService {
	/**
	 * Starts a new user session.
	 * 
	 * @param username The username
	 * @param password The password
	 * @return The newly created session identifier(sid)
	 */
	public String login(@WebParam(name = "username") String username, @WebParam(name = "password") String password)
			throws Exception;

	/**
	 * Closes a user session.
	 * 
	 * @param sid The session identifier
	 */
	public void logout(@WebParam(name = "sid") String sid);

	/**
	 * Retrieves the list of users.
	 * 
	 * @param sid The session identifier
	 * @return 'error' if error occurred, the ids collection of users.
	 * @throws Exception
	 */
	public long[] getUsers(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Retrieves the list of groups.
	 * 
	 * @param sid The session identifier
	 * @return 'error' if error occurred, the ids collection of groups.
	 * @throws Exception
	 */
	public long[] getGroups(@WebParam(name = "sid") String sid) throws Exception;

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
	public void grantUser(String sid, long folderId, long userId, int permissions, boolean recursive) throws Exception;

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
	public void grantGroup(String sid, long folderId, long groupId, int permissions, boolean recursive)
			throws Exception;

	/**
	 * Retrieves the list of granted users for the given folder.
	 * 
	 * @param sid Session identifier
	 * @param folderId Folder id
	 * @return 'error' if error occurred, the right objects collection.
	 * @throws Exception
	 */
	public Right[] getGrantedUsers(String sid, long folderId) throws Exception;

	/**
	 * Retrieves the list of granted groups for the given folder.
	 * 
	 * @param sid Session identifier
	 * @param folderId Folder id
	 * @return 'error' if error occurred, the right objects collection.
	 * @throws Exception
	 */
	public Right[] getGrantedGroups(String sid, long folderId) throws Exception;
}
