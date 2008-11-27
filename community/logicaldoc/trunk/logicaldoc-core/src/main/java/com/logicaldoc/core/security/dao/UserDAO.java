package com.logicaldoc.core.security.dao;

import java.util.List;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.security.User;

/**
 * This class is a DAO-service for User-objects.
 * 
 * @author Michael Scholz
 * @author Marco Meschieri
 * @version 1.0
 */
public interface UserDAO extends PersistentObjectDAO<User> {

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
	 * @param username Username of the User to be validate.
	 * @param password Password of the User to be validate.
	 * @return True if User is authenticated.
	 */
	public boolean validateUser(String username, String password);

}
