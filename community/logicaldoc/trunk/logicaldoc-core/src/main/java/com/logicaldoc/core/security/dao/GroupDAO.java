package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.security.Group;

public interface GroupDAO {
	/**
	 * This method persists a new group object.
	 * 
	 * @param group Group which should be stored in a database.
	 * @param parentGroupId ID of the group this group inherits ACLs from
	 * @return True if successful stored in a database.
	 */
	public boolean insert(Group group, long parentGroupId);

	/**
	 * This method persists a group object.
	 * 
	 * @param group Group which should be stored in a database.
	 * @return True if successful stored in a database.
	 */
	public boolean store(Group group);

	/**
	 * This method deletes a group in database.
	 * 
	 * @param groupId ID of the group to be deleted.
	 * @return True if successful deleted.
	 */
	public boolean delete(long groupId);

	/**
	 * Finds a group by name.
	 * 
	 * @param name name of wanted group.
	 * @return Wanted group or null.
	 */
	public Group findByName(String name);

	/**
	 * Finds a group by its ID
	 * 
	 * @param groupId The group id
	 * @return Wanted group or null
	 */
	public Group findById(long groupId);

	/**
	 * This method selects all groups.
	 */
	public Collection<Group> findAll();

	/**
	 * This method selects all groupnames.
	 */
	public Collection<String> findAllGroupNames();
	

	/**
	 * This method finds a Group by name.
	 * 
	 * @param name
	 *            The name of wanted Group.
	 * @return Collection of selected groups.
	 */
	public Collection<Group> findByLikeName(String name);
}