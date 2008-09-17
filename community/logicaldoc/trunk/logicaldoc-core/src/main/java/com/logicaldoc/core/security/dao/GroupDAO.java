package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.security.Group;

public interface GroupDAO {

    /** 
     * This method persists a new group object.
     * @param group Group which should be stored in a database.
     * @param parentGroup Name of the group this group inherits ACLs from
     * @return True if successful stored in a database.
     */
    public boolean insert(Group group, String parentGroup);

    /** 
     * This method persists a group object.
     * @param group Group which should be stored in a database.
     * @return True if successful stored in a database.
     */
    public boolean store(Group group); 

    /**
     * This method deletes a group in database.
     * @param groupname Groupname of group to be deleted.
     * @return True if successful deleted.
     */
    public boolean delete(String groupname);

    /**
     * Finds a group by primarykey.
     * @param groupname Primarykey of wanted group.
     * @return Wanted group or null.
     */
    public Group findByPrimaryKey(String groupname);

    /**
     * This method selects all groups.
     */
    public Collection<Group> findAll();

    /**
     * This method selects all groupnames.
     */
    public Collection<String> findAllGroupNames();

    /**
     * This method checks the exist of a group.
     * @param groupname Groupname of the group.
     */
    public boolean exists(String groupname);
}