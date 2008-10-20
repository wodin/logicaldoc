package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.security.User;

/**
 * This class is a DAO-service for User-objects.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public interface UserDAO {

	/**
	 * This method persists the user object.
	 * 
	 * @param user
	 *            User, which should be stored in a database.
	 * @return True if successful stored in a database.
	 */
	public boolean store(User user);

	/**
	 * This method deletes a given user.
	 * 
	 * @param userId
	 *            ID of the user to be deleted.
	 * @return True if successful deleted in a database.
	 */
	public boolean delete(long userId);

	/**
	 * This method deletes by its ID
	 * 
	 * @param userId
	 *            ID of the user to be searched.
	 * @return Wanted User or null if user doesn't exist.
	 */
	public User findById(long userId);

	/**
	 * This method finds an User by its username.
	 * 
	 * @param username
	 *            username of wanted User.
	 * @return Wanted User or null if user doesn't exist.
	 */
	public User findByUserName(String username);

	/**
	 * This method finds an User by username.
	 * 
	 * @param username
	 *            The username of wanted User.
	 * @return Collection of selected users.
	 */
	public Collection<User> findByLikeUserName(String username);

	/**
	 * This method finds an User by name.
	 * 
	 * @param name
	 *            The name of wanted User.
	 * @return Collection of selected users.
	 */
	public Collection<User> findByName(String name);

	/**
	 * This method finds an User by username and name.
	 * 
	 * @param username
	 *            The username of wanted user.
	 * @param name
	 *            The name of wanted user.
	 * @return Collection of selected users.
	 */
	public Collection<User> findByUserNameAndName(String username, String name);

	/**
	 * This method finds all user.
	 * 
	 * @return All users.
	 */
	public Collection<User> findAll();

	/**
	 * Validates an username and a password.
	 * 
	 * @param username
	 *            Username of the User to be validate.
	 * @param password
	 *            Password of the User to be validate.
	 * @return True if User is authenticated.
	 */
	public boolean validateUser(String username, String password);

}
