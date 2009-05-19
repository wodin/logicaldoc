package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.PersistentObjectDAO;
import com.logicaldoc.core.security.Group;

public interface GroupDAO extends PersistentObjectDAO<Group> {
	/**
	 * This method persists a new group object. All permissions and extended
	 * attributes of the parent group will be replicated.
	 * 
	 * @param group Group which should be stored in a database.
	 * @param parentGroupId ID of the group this group inherits ACLs from
	 * @return True if successful stored in a database.
	 */
	public boolean insert(Group group, long parentGroupId);

	/**
	 * This method replicates all ACLs of the parent group to another group.<p>
	 * <b>Attention:</b> The the group(groupId) ACLs will be discarded.
	 *  
	 * @param groupId The group to be altered.
	 * @param groupId The group whose ACLs will be inherited.
	 */
	public void inheritACLs(long groupId, long parentGroupId); 
	
	/**
	 * Finds a group by name.
	 * 
	 * @param name name of wanted group.
	 * @return Wanted group or null.
	 */
	public Group findByName(String name);

	/**
	 * This method selects all groupnames.
	 */
	public Collection<String> findAllGroupNames();

	/**
	 * This method finds a Group by name.
	 * 
	 * @param name The name of wanted Group.
	 * @return Collection of selected groups.
	 */
	public Collection<Group> findByLikeName(String name);
}