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
	public abstract void assignUsersToGroup(Collection<User> users, Group group);

	/**
	 * Assign a user to a collection of groups.
	 * 
	 * @param user The user to be assigned
	 * @param groups The groups collection
	 */
	public abstract void assignUserToGroups(User user, Collection<Group> groups);

	/**
	 * Assign a user to a collection of groups.
	 * 
	 * @param user The user to be assigned
	 * @param groupNames Array of group names
	 */
	public abstract void assignUserToGroups(User user, String[] groupNames);
	
	/**
	 * Assign a user to a group.
	 * 
	 * @param user The user to be assigned
	 * @param group The group
	 */
	public abstract void assignUserToGroup(User user, Group group);

	/**
	 * Removes the specified users from a group
	 * 
	 * @param users The users to be removed
	 * @param group The group to be updated
	 */
	public abstract void removeUsersFromGroup(Collection<User> users, Group group);

	/**
	 * Removes the specified user from a group
	 * 
	 * @param users The user to be removed
	 * @param group The group to be updated
	 */
	public abstract void removeUserFromGroup(User user, Group group);

	/**
	 * Removes the specified user from a all groups
	 * 
	 * @param user The user to be removed
	 */
	public abstract void removeUserFromAllGroups(User user);

	/**
	 * Removes all users from group
	 * 
	 * @param group The group from wich all users must be removed
	 */
	public abstract void removeAllUsersFromGroup(Group group);

}