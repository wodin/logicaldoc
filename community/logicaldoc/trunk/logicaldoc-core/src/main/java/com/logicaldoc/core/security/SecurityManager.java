package com.logicaldoc.core.security;

import java.util.Collection;

/**
 * Manager on security issues such as users and groups
 * 
 * @author Marco Meschieri
 * @version $Id: SecurityManager.java,v 1.1 2007/06/29 06:28:30 marco Exp $
 * @since 3.0
 */
public interface SecurityManager {

	/**
	 * Assign a collection of users to a group.
	 * 
	 * @param users The users collection
	 * @param group The group
	 */
	public void assignUsersToGroup(Collection<User> users, Group group);

	/**
	 * Assign a user to a collection of groups.
	 * 
	 * @param user The user to be assigned
	 * @param groups The groups collection
	 */
	public void assignUserToGroups(User user, Collection<Group> groups);

	/**
	 * Assign a user to a collection of groups.
	 * 
	 * @param user The user to be assigned
	 * @param groupIds Array of group ids
	 */
	public void assignUserToGroups(User user, long[] groupIds);

	/**
	 * Assign a user to a group.
	 * 
	 * @param user The user to be assigned
	 * @param group The group
	 */
	public void assignUserToGroup(User user, Group group);

	/**
	 * Removes the specified users from a group
	 * 
	 * @param users The users to be removed
	 * @param group The group to be updated
	 */
	public void removeUsersFromGroup(Collection<User> users, Group group);

	/**
	 * Removes the specified user from a group
	 * 
	 * @param users The user to be removed
	 * @param group The group to be updated
	 */
	public void removeUserFromGroup(User user, Group group);

	/**
	 * Removes the specified user from a all groups
	 * 
	 * @param user The user to be removed
	 */
	public void removeUserFromAllGroups(User user);

	/**
	 * Removes all users from group
	 * 
	 * @param group The group from wich all users must be removed
	 */
	public void removeAllUsersFromGroup(Group group);

	/**
	 * Retrieves the collection of groups that can access the given menu
	 * 
	 * @param menu The menu to consider
	 * @return The collection of allowed groups
	 */
	public Collection<Group> getAllowedGroups(Menu menu);
}