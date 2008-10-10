package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.security.UserDoc;

/**
 * This class is a DAO-service for userdocs.
 * 
 * @author Michael Scholz
 * @version 1.0
 */
public interface UserDocDAO {

	/**
	 * This method persist an userdoc. The maximum of userdoc for an user is 5.
	 */
	public boolean store(UserDoc userdoc);

	/**
	 * Select the count of userdocs for an user.
	 */
	public int getCount(String username);

	/**
	 * This method deletes an userdoc by the primary key.
	 */
	public boolean delete(String username, int menuId);

    /**
     * This method deletes all userdocs by the menu id.
     */
    public boolean delete(int menuId);
    
	/**
	 * Selects all userdocs for a given user.
	 */
	public Collection<UserDoc> findByUserName(String username);
    
    /**
     * Selects all userdocs for a given menu.
     */
    public Collection<UserDoc> findByMenuId(int menuId);
    

	/**
	 * Selects the oldest userdoc for a given user.
	 */
	public UserDoc findByMinTimeStamp(String username);

	/**
	 * Check if an userdoc exists.
	 * 
	 * @param menuId MenuId of the userdoc
	 * @param username
	 */
	public boolean exists(int menuId, String username);
}