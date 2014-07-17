package com.logicaldoc.core.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * Basic implementation of <code>SecurityManager</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class SecurityManagerImpl implements SecurityManager {

	protected static Logger log = LoggerFactory.getLogger(SecurityManagerImpl.class);

	protected UserDAO userDAO;

	protected GroupDAO groupDAO;

	protected MenuDAO menuDAO;

	private SecurityManagerImpl() {
	}

	public void setMenuDAO(MenuDAO menuDAO) {
		this.menuDAO = menuDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#assignUsersToGroup(java.util.Collection,
	 *      com.logicaldoc.core.security.Group)
	 */
	public void assignUsersToGroup(Collection<User> users, Group group) {
		groupDAO.initialize(group);
		
		for (Iterator<User> iter = users.iterator(); iter.hasNext();) {
			User user = iter.next();
			if (!group.getUsers().contains(user)) {
				group.getUsers().add(user);
			}

			if (!user.getGroups().contains(group)) {
				user.getGroups().add(group);
			}
		}

		groupDAO.store(group);

		if (log.isDebugEnabled())
			log.debug("Assigned users " + users + " to group " + group);
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#assignUserToGroups(com.logicaldoc.core.security.User,
	 *      java.util.Collection)
	 */
	public void assignUserToGroups(User user, Collection<Group> groups) {
		for (Group group : groups)
			assignUserToGroup(user, group);
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#assignUserToGroup(com.logicaldoc.core.security.User,
	 *      com.logicaldoc.core.security.Group)
	 */
	public void assignUserToGroup(User user, Group group) {
		Collection<User> users = new ArrayList<User>();
		users.add(user);
		assignUsersToGroup(users, group);
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#removeUsersFromGroup(java.util.Collection,
	 *      com.logicaldoc.core.security.Group)
	 */
	public void removeUsersFromGroup(Collection<User> users, Group group) {
		try {
			groupDAO.initialize(group);
			Set<User> oldUsers = group.getUsers();
			for (User user : users) {
				if (oldUsers.contains(user)
						&& !group.getName().equals(user.getUserGroupName())) {
					user.getGroups().remove(group);
				}
			}

			Set<User> newUsers = new HashSet<User>();
			for (User user : oldUsers) {
				if (!users.contains(user))
					newUsers.add(user);
			}
			group.setUsers(newUsers);

			groupDAO.store(group);

			if (log.isDebugEnabled())
				log.debug("Removed users " + users + " from group " + group);
		} catch (Throwable t) {
			log.warn(t.getMessage(), t);
		}
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#removeUserFromGroup(com.logicaldoc.core.security.User,
	 *      com.logicaldoc.core.security.Group)
	 */
	public void removeUserFromGroup(User user, Group group) {
		// Avoid the unassignment to the user's group
		if (user.getUserGroupName().equals(group.getName()))
			return;
		Collection<User> users = new ArrayList<User>();
		users.add(user);
		removeUsersFromGroup(users, group);
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#removeUserFromAllGroups(com.logicaldoc.core.security.User)
	 */
	public void removeUserFromAllGroups(User user) {
		Collection<Group> groups = groupDAO.findAll();
		for (Iterator<Group> iter = groups.iterator(); iter.hasNext();) {
			Group group = iter.next();
			if (group.getUsers().contains(user))
				removeUserFromGroup(user, group);
		}
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#removeAllUsersFromGroup(com.logicaldoc.core.security.Group)
	 */
	public void removeAllUsersFromGroup(Group group) {
		groupDAO.initialize(group);
		removeUsersFromGroup(group.getUsers(), group);
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#assignUserToGroups(com.logicaldoc.core.security.User,
	 *      long[])
	 */
	public void assignUserToGroups(User user, long[] groupIds) {
		if (groupIds == null)
			return;
		ArrayList<Group> groups = new ArrayList<Group>();
		for (int i = 0; i < groupIds.length; i++)
			groups.add(groupDAO.findById(groupIds[i]));

		// Always add the user's group
		Group userGroup = groupDAO.findByName(user.getUserGroupName(), user.getTenantId());
		if (userGroup != null && !groups.contains(userGroup))
			groups.add(userGroup);
		assignUserToGroups(user, groups);
	}

	/**
	 * @see com.logicaldoc.core.security.SecurityManager#getAllowedGroups(com.logicaldoc.core.security.Menu)
	 */
	@Override
	public Set<Group> getAllowedGroups(long menuId) {
		Menu menu = menuDAO.findById(menuId);
		if (menu.getSecurityRef() != null)
			menu = menuDAO.findById(menu.getSecurityRef());
		Set<Group> groups = new HashSet<Group>();
		for (MenuGroup mg : menu.getMenuGroups()) {
			Group group = groupDAO.findById(mg.getGroupId());
			if (!groups.contains(group))
				groups.add(groupDAO.findById(mg.getGroupId()));
		}
		Group admin = groupDAO.findById(Group.GROUPID_ADMIN);
		if (!groups.contains(admin))
			groups.add(admin);
		return groups;
	}

	@Override
	public boolean isMemberOf(long userId, long groupId) {
		return userDAO.queryForInt("select count(*) from ld_usergroup where ld_userid="+userId+ " and ld_groupid="+groupId) > 0;
	}

	@Override
	public boolean isMemberOf(long userId, String groupName) {
		User user = userDAO.findById(userId);
		if (user == null)
			return false;
		for (Group group : user.getGroups())
			if (group.getName().equals(groupName))
				return true;
		return false;
	}
}