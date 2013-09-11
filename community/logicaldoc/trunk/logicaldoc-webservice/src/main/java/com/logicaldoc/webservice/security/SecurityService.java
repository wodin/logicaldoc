package com.logicaldoc.webservice.security;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

/**
 * Security Web Service definition interface
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
@WebService
public interface SecurityService {
	/**
	 * Gets metadata of all existing users.
	 * 
	 * @param sid Session identifier. Must be an administrator.
	 * @return A value object containing the users metadata.
	 * @throws Exception
	 */
	@WebResult(name = "users")
	public WSUser[] listUsers(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Gets group metadata of all existing groups.
	 * 
	 * @param sid Session identifier. Must be an administrator.
	 * @return A value object containing the groups metadata.
	 * @throws Exception
	 */
	@WebResult(name = "groups")
	public WSGroup[] listGroups(@WebParam(name = "sid") String sid) throws Exception;

	/**
	 * Create/Update a user. You can completely customize the user through a
	 * value object containing the user's metadata. The current user must be an
	 * administrator.
	 * 
	 * @param sid Session identifier. Must be an administrator.
	 * @param user Web service value object containing the user's metadata
	 * @return id of the created/updated user.
	 * @throws Exception
	 */
	@WebResult(name = "userId")
	public long storeUser(@WebParam(name = "sid") String sid, @WebParam(name = "user") WSUser user) throws Exception;

	/**
	 * Create/Update a group. You can completely customize the group through a
	 * value object containing the group's metadata.
	 * 
	 * @param sid Session identifier. Must be an administrator.
	 * @param group Web service value object containing the group's metadata
	 * @return id of the created/updated group.
	 * @throws Exception
	 */
	@WebResult(name = "groupId")
	public long storeGroup(@WebParam(name = "sid") String sid, @WebParam(name = "group") WSGroup group)
			throws Exception;

	/**
	 * Deletes an existing user with the given identifier.
	 * 
	 * @param sid Session identifier. Must be an administrator.
	 * @param userId The user id
	 * @throws Exception
	 */
	public void deleteUser(@WebParam(name = "sid") String sid, @WebParam(name = "userId") long userId) throws Exception;

	/**
	 * Deletes an existing group with the given identifier.
	 * 
	 * @param sid Session identifier. Must be an administrator.
	 * @param groupId The group id
	 * @throws Exception
	 */
	public void deleteGroup(@WebParam(name = "sid") String sid, @WebParam(name = "groupId") long groupId)
			throws Exception;

	/**
	 * Changes the password of a user
	 * 
	 * @param userId The user Identifier. Must be an administrator.
	 * @param oldPassword can be null
	 * @param newPassword
	 * @return 0 if all is ok, 1 if the password is incorrect, 2 if the new
	 *         password cannot be notified, otherwise a positive number grater
	 *         than 2
	 */
	@WebResult(name = "changeResult")
	public int changePassword(@WebParam(name = "sid") String sid, @WebParam(name = "userId") long userId,
			@WebParam(name = "oldPassword") String oldPassword, @WebParam(name = "newPassword") String newPassword)
			throws Exception;

	/**
	 * Gets user metadata of an existing user with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param userId The user id
	 * @return A value object containing the user's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "user")
	public WSUser getUser(@WebParam(name = "sid") String sid, @WebParam(name = "userId") long userId) throws Exception;

	/**
	 * Gets user metadata of an existing user with the given username.
	 * 
	 * @param sid Session identifier
	 * @param username The user name
	 * @return A value object containing the user's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "user")
	public WSUser getUserByUsername(@WebParam(name = "sid") String sid, @WebParam(name = "username") String username) throws Exception;
	
	/**
	 * Gets group metadata of an existing group with the given identifier.
	 * 
	 * @param sid Session identifier
	 * @param groupId The group id
	 * @return A value object containing the group's metadata.
	 * @throws Exception
	 */
	@WebResult(name = "group")
	public WSGroup getGroup(@WebParam(name = "sid") String sid, @WebParam(name = "groupId") long groupId)
			throws Exception;
}