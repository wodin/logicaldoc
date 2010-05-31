package com.logicaldoc.webapp.security;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.webapp.AbstractWebappTestCase;

public class SecurityServiceImplTest extends AbstractWebappTestCase {

	// Instance under test
	private SecurityServiceImpl service = new SecurityServiceImpl();

	private UserDAO userDAO;

	private GroupDAO groupDAO;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		userDAO = (UserDAO) context.getBean("UserDAO");
		groupDAO = (GroupDAO) context.getBean("GroupDAO");
	}

	@Test
	public void testLogin() {
		GUISession session = service.login("admin", "admin");
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
		Assert.assertEquals("admin", session.getUser().getUserName());
		Assert.assertEquals(1, session.getUser().getId());
		Assert.assertEquals(1, SessionManager.getInstance().countOpened());
		SessionManager.getInstance().get(session.getSid()).setClosed();
		Assert.assertEquals(0, SessionManager.getInstance().countOpened());

		session = service.login("admin", "password");
		Assert.assertFalse(session.isLoggedIn());
		session = service.login("unexisting", "admin");
		Assert.assertFalse(session.isLoggedIn());
	}

	@Test
	public void testLogout() {
		GUISession session = service.login("admin", "admin");
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
		Assert.assertEquals("admin", session.getUser().getUserName());
		Assert.assertEquals(1, session.getUser().getId());
		Assert.assertEquals(1, SessionManager.getInstance().countOpened());

		service.logout(session.getSid());

		Assert.assertEquals(0, SessionManager.getInstance().countOpened());
	}

	@Test
	public void testChangePassword() {
		Assert.assertEquals(0, service.changePassword(1, "admin", "test"));
		Assert.assertEquals(0, service.changePassword(1, "test", "admin"));
		Assert.assertNotSame(0, service.changePassword(1, "xxxxx", "test"));
	}

	@Test
	public void testAddUserToGroup() {
		GUISession session = service.login("admin", "admin");
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
		User test = userDAO.findByUserName("test");
		Assert.assertNotNull(test);
		Group group = groupDAO.findByName("author");
		Assert.assertNotNull(group);
		service.addUserToGroup(session.getSid(), group.getId(), test.getId());
		User user = userDAO.findByUserName("test");
		Assert.assertTrue(user.getGroups().contains(group));

		group = groupDAO.findByName("guest");
		Assert.assertNotNull(group);
		service.addUserToGroup(session.getSid(), group.getId(), test.getId());
		user = userDAO.findByUserName("test");
		Assert.assertTrue(user.getGroups().contains(group));
	}

	@Test
	public void testDeleteGroup() {
		GUISession session = service.login("admin", "admin");
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));

		Assert.assertNotNull(groupDAO.findById(10));
		service.deleteGroup(session.getSid(), 10);
		Assert.assertNull(groupDAO.findById(10));

		// Delete a BIG group with associated MenuGroups and UserGroups
		Assert.assertNotNull(groupDAO.findById(1));
		service.deleteGroup(session.getSid(), 1);
		Assert.assertNull(groupDAO.findById(1));
	}

	@Test
	public void testDeleteUser() {
		GUISession session = service.login("admin", "admin");
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));

		User user = userDAO.findByUserName("author");
		Assert.assertEquals(2, user.getGroups().size());
		service.deleteUser(session.getSid(), user.getId());
		user = userDAO.findByUserName("author");
		Assert.assertNull(user);
	}

	@Test
	public void testRemoveFromGroup() {
		GUISession session = service.login("admin", "admin");
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));

		long[] users = new long[2];
		users[0] = 5;
		users[1] = 1;
		Group group = groupDAO.findByName("author");
		service.removeFromGroup(session.getSid(), group.getId(), users);
		User user = userDAO.findByUserName("test");
		Assert.assertFalse(user.getGroups().contains(group));
		user = userDAO.findByUserName("admin");
		Assert.assertFalse(user.getGroups().contains(group));

		group = groupDAO.findByName("guest");
		service.removeFromGroup(session.getSid(), group.getId(), users);
		user = userDAO.findByUserName("test");
		Assert.assertFalse(user.getGroups().contains(group));
		user = userDAO.findByUserName("admin");
		Assert.assertFalse(user.getGroups().contains(group));
	}
}