package com.logicaldoc.gui.common.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMenu;
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
	 * Logs-in a user by an existing session ID (session reuse)
	 *
	 */
	public GUISession login(String sid);

	
	/**
	 * Changes the password of a user
	 * 
	 * @param userId The user Identifier
	 * @param oldPassword can be null
	 * @param newPassword
	 * @param notify If the new credentials need to be notified
	 * @return 0 if all is ok, 1 if the password is incorrect, 2 if the new
	 *         password cannot be notified, otherwise a positive number grater
	 *         than 2
	 */
	public int changePassword(long userId, String oldPassword, String newPassword, boolean notify);

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
	public GUIUser saveUser(String sid, GUIUser user, GUIInfo info) throws InvalidSessionException;

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

	/**
	 * Applies all security settings to menu
	 */
	public void applyRights(String sid, GUIMenu menu) throws InvalidSessionException;

	/**
	 * Retrieves the specified menu
	 */
	public GUIMenu getMenu(String sid, long id) throws InvalidSessionException;

	/**
	 * Reset the password for the given email.
	 * 
	 * @param emailAddress the username for which reset password
	 * @param emailAddress the email for which reset password
	 * @param productName the application product name
	 */
	public void resetPassword(String username, String emailAddress, String productName) throws Exception;

	/**
	 * Searches for users
	 * 
	 * @param sid The current session ID
	 * @param username The username used in the like operator (optional)
	 * @param groupId The group ID (optional)
	 * 
	 * @return Array of found users
	 * @throws InvalidSessionException
	 */
	public GUIUser[] searchUsers(String sid, String username, String groupId) throws InvalidSessionException;
}