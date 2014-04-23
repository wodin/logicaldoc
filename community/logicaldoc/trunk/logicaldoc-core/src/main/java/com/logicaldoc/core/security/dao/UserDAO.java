package com.logicaldoc.core.security.dao;

import java.util.List;
import java.util.Map;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.UserHistory;

/**
 * This class is a DAO-service for User-objects.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public interface UserDAO extends PersistentObjectDAO<User> {

	/**
	 * Counts the total number of users
	 */
	public int count();

	/**
	 * This method finds an User by its username.
	 * 
	 * @param username username of wanted User.
	 * @return Wanted User or null if user doesn't exist.
	 */
	public User findByUserName(String username);

	/**
	 * This method finds an User by username.
	 * 
	 * @param username The username of wanted User.
	 * @return Collection of selected users.
	 */
	public List<User> findByLikeUserName(String username);

	/**
	 * This method finds an User by name.
	 * 
	 * @param name The name of wanted User.
	 * @return Collection of selected users.
	 */
	public List<User> findByName(String name);

	/**
	 * This method finds an User by username and name.
	 * 
	 * @param username The username of wanted user.
	 * @param name The name of wanted user.
	 * @return Collection of selected users.
	 */
	public List<User> findByUserNameAndName(String username, String name);

	/**
	 * Validates an username and a password.
	 * 
	 * @param username Username of the User to be validated.
	 * @param password Password of the User to be validated.
	 * @return True if User is valid and authenticated.
	 */
	public boolean validateUser(String username, String password);

	/**
	 * Validates an username only (the password content is not inspectged.
	 * 
	 * @param username Username of the User to be validated.
	 * @return True if User is valid.
	 */
	public boolean validateUser(String username);

	/**
	 * Is password expired.
	 * 
	 * @param username Username of the User to be validate.
	 * @return True if the password is expired
	 */
	public boolean isPasswordExpired(String username);

	public int getPasswordTtl();

	public void setPasswordTtl(int passwordTtl);

	/**
	 * This method deletes the user object and insert a new user history entry.
	 * 
	 * @param userId The id of the user to delete
	 * @param transaction entry to log the event
	 * @return True if successfully deleted from the database.
	 */
	public boolean delete(long userId, UserHistory transaction);

	/**
	 * This method persists the user object and insert a new user history entry.
	 * 
	 * @param user
	 * @param transaction entry to log the event
	 * @return True if successfully stored in a database.
	 */
	public boolean store(final User user, final UserHistory transaction);

	/**
	 * Retrieves the settings for a user. The settings are stored as Generics of
	 * type usersetting.
	 * 
	 * @param userId Identifier of the user
	 * @param namePrefix Name prefix of the property (optional)
	 * 
	 * @return The map setting_name-generic
	 */
	public Map<String, Generic> findUserSettings(long userId, String namePrefix);

	/**
	 * Retrieve the administrator for the given tenant. The general rule is that
	 * the administrator's username is:
	 * <ol>
	 * <li>admin if the tenant is default</li>
	 * <li>admin<b>Tenantname</b> in all other cases</li>
	 * </ol>
	 */
	public User findAdminUser(String tenantName);
}
