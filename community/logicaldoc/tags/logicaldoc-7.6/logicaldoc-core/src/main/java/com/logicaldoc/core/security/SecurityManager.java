package com.logicaldoc.core.security;

import java.util.Collection;
import java.util.Set;

/**
 * Manager for security objects like users and groups
 * 
 * @author Marco Meschieri - Logical Objects
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
	 * Removes the specified users from a group. Users will not removed from
	 * their user's groups.
	 * 
	 * @param users The users to be removed
	 * @param group The group to be updated
	 */
	public void removeUsersFromGroup(Collection<User> users, Group group);

	/**
	 * Removes the specified user from a group. If the specified group is the
	 * user's group no changes are committed.
	 * 
	 * @param users The user to be removed
	 * @param group The group to be updated
	 */
	public void removeUserFromGroup(User user, Group group);

	/**
	 * Removes the specified user from a all groups except it's user's group
	 * 
	 * @param user The user to be removed
	 */
	public void removeUserFromAllGroups(User user);

	/**
	 * Removes all users from group
	 * 
	 * @param group The group from which all users must be removed
	 */
	public void removeAllUsersFromGroup(Group group);

	/**
	 * Retrieves the collection of groups that can access the given menu
	 * 
	 * @param menuId The menu to consider
	 * @return The collection of allowed groups
	 */
	public Set<Group> getAllowedGroups(long menuId);

	/**
	 * Checks if a given user is member of a particular group
	 * 
	 * @param userId The user identifier
	 * @param groupId The group identifier
	 * @return true only if the user belongs to the group
	 */
	public boolean isMemberOf(long userId, long groupId);

	/**
	 * Checks if a given user is member of a particular group
	 * 
	 * @param userId The user identifier
	 * @param groupName The group name
	 * @return true only if the user belongs to the group
	 */
	public boolean isMemberOf(long userId, String groupName);

	/**
	 * This method is looking up for writing rights for a folder and an user.
	 * 
	 * @param docId ID of the document
	 * @param userId ID of the user
	 */
	public boolean isWriteEnabled(long docId, long userId);

	public boolean isReadEnabled(long docId, long userId);
	
	public boolean isPrintEnabled(long docId, long userId);

	public boolean isDownloadEnabled(long docId, long userId);

	/**
	 * This method checks if the given permission is enabled for a document and
	 * an user.
	 * 
	 * @param permission the permission to check
	 * @param docId ID of the document
	 * @param userId ID of the user
	 */
	public boolean isPermissionEnabled(Permission permission, long docId, long userId);

	/**
	 * Finds all permissions of a user enabled on the specified document
	 * 
	 * @param docId ID of the document
	 * @param userId ID of the user
	 * @return Collection of enabled permissions
	 */
	public Set<Permission> getEnabledPermissions(long docId, long userId);
}