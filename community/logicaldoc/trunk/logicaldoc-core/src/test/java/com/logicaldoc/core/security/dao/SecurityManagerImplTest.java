package com.logicaldoc.core.security.dao;

import java.util.ArrayList;
import java.util.Set;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SecurityManager;
import com.logicaldoc.core.security.User;

/**
 * Test case for the manager <code>SecurityManager<code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @version $Id:$
 * @since 3.0
 *
 */
public class SecurityManagerImplTest extends AbstractCoreTestCase {
	// Instance under test
	private SecurityManager manager;

	private UserDAO userDAO;

	private GroupDAO groupDAO;

	public SecurityManagerImplTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		manager = (SecurityManager) context.getBean("SecurityManager");
		userDAO = (UserDAO) context.getBean("UserDAO");
		groupDAO = (GroupDAO) context.getBean("GroupDAO");
	}

	public void testAssignUsersToGroup() {
		ArrayList<User> users = new ArrayList<User>();
		users.add(userDAO.findByUserName("test"));
		users.add(userDAO.findByUserName("admin"));
		Group group = groupDAO.findByName("author");
		manager.assignUsersToGroup(users, group);
		User user = userDAO.findByUserName("test");
		assertTrue(user.getGroups().contains(group));
		user = userDAO.findByUserName("admin");
		assertTrue(user.getGroups().contains(group));

		group = groupDAO.findByName("guest");
		manager.assignUsersToGroup(users, group);
		user = userDAO.findByUserName("test");
		assertTrue(user.getGroups().contains(group));
		user = userDAO.findByUserName("admin");
		assertTrue(user.getGroups().contains(group));
	}

	public void testRemoveUsersFromGroup() {
		ArrayList<User> users = new ArrayList<User>();
		users.add(userDAO.findByUserName("test"));
		users.add(userDAO.findByUserName("admin"));
		Group group = groupDAO.findByName("author");
		manager.removeUsersFromGroup(users, group);
		User user = userDAO.findByUserName("test");
		assertFalse(user.getGroups().contains(group));
		user = userDAO.findByUserName("admin");
		assertFalse(user.getGroups().contains(group));

		group = groupDAO.findByName("guest");
		manager.removeUsersFromGroup(users, group);
		user = userDAO.findByUserName("test");
		assertFalse(user.getGroups().contains(group));
		user = userDAO.findByUserName("admin");
		assertFalse(user.getGroups().contains(group));
	}

	public void testAssignUserToGroups() {
		User user = new User();
		user.setUserName("zzz");
		user.setDecodedPassword("xxxpwd");
		userDAO.store(user);
	}
	
	public void testGetAllowedGroups() {
		Set<Group> groups=manager.getAllowedGroups(5);
		assertNotNull(groups);
		assertTrue(groups.contains(groupDAO.findByName("admin")));
		assertTrue(groups.contains(groupDAO.findByName("author")));
		assertTrue(groups.contains(groupDAO.findByName("guest")));
	}
}