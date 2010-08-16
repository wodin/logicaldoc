package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;

/**
 * The client side stub for the Security Service. This service gives all needed
 * methods to handle user sessions.
 */
@RemoteServiceRelativePath("security")
public interface SecurityService extends RemoteService {
	/**
	 * Logs-in a user
	 * 
	 * @param username
	 * @param password
	 * @param locale
	 * 
	 * @return The newly created session
	 */
	public GUISession login(String username, String password, String locale);

	/**
	 * Changes the password of a user
	 * 
	 * @param userId The user Identifier
	 * @param oldPassword can be null
	 * @param newPassword
	 * @return 0 if all is ok, 1 if the password is incorrect, 2 if the new
	 *         password cannot be notified, otherwise a positive number grater
	 *         than 2
	 */
	public int changePassword(long userId, String oldPassword, String newPassword);

	/**
	 * Logs out the current user
	 */
	public void logout(String sid);

	/**
	 * Deletes a given user
	 */
	public void deleteUser(String sid, long userId) throws InvalidSessionException;

	/**
	 * Creates or updates a user
	 */
	public GUIUser saveUser(String sid, GUIUser user) throws InvalidSessionException;

	/**
	 * Saves the profile data only
	 */
	public GUIUser saveProfile(String sid, GUIUser user) throws InvalidSessionException;

	
	/**
	 * Loads a given user from the database
	 */
	public GUIUser getUser(String sid, long userId) throws InvalidSessionException;

	/**
	 * Loads a given group from the database
	 */
	public GUIGroup getGroup(String sid, long groupId) throws InvalidSessionException;

	/**
	 * Creates or updates a group
	 */
	public GUIGroup saveGroup(String sid, GUIGroup group) throws InvalidSessionException;

	/**
	 * Deletes a given group
	 */
	public void deleteGroup(String sid, long groupId) throws InvalidSessionException;

	/**
	 * Removes users from a group
	 */
	public void removeFromGroup(String sid, long groupId, long[] userIds) throws InvalidSessionException;

	/**
	 * Adds a user to a group
	 */
	public void addUserToGroup(String sid, long groupId, long userId) throws InvalidSessionException;

	/**
	 * Saves security settings
	 */
	public void saveSettings(String sid, GUISecuritySettings settings) throws InvalidSessionException;

	/**
	 * Loads security settings
	 */
	public GUISecuritySettings loadSettings(String sid) throws InvalidSessionException;

	/**
	 * Kill the session with the given sid.
	 */
	public void kill(String sid) throws InvalidSessionException;
}