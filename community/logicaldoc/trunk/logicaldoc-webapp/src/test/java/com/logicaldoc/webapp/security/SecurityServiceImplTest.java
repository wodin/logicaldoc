package com.logicaldoc.webapp.security;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.GroupDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.web.security.SecurityServiceImpl;
import com.logicaldoc.webapp.AbstractWebappTestCase;

public class SecurityServiceImplTest extends AbstractWebappTestCase {

	// Instance under test
	private SecurityServiceImpl service = new SecurityServiceImpl();

	private UserDAO userDAO;

	private GroupDAO groupDAO;

	private GUISession session;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		userDAO = (UserDAO) context.getBean("UserDAO");
		groupDAO = (GroupDAO) context.getBean("GroupDAO");

		session = service.login("admin", "admin");
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
	}

	@Test
	public void testLogin() {
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
		User user = userDAO.findByUserName("author");
		Assert.assertEquals(2, user.getGroups().size());
		service.deleteUser(session.getSid(), user.getId());
		user = userDAO.findByUserName("author");
		Assert.assertNull(user);
	}

	@Test
	public void testRemoveFromGroup() {
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

	@Test
	public void testGetGroup() {
		GUIGroup group = service.getGroup(session.getSid(), 10);
		Assert.assertNotNull(group);
		Assert.assertEquals("testGroup", group.getName());
		Assert.assertEquals("Group for tests", group.getDescription());

		// Try with unexisting id
		group = service.getGroup(session.getSid(), 999);
		Assert.assertNull(group);
	}

	@Test
	public void testGetUser() {
		GUIUser user = service.getUser(session.getSid(), 1);
		Assert.assertNotNull(user);
		Assert.assertEquals("admin", user.getUserName());
		Assert.assertEquals("admin@admin.net", user.getEmail());

		user = service.getUser(session.getSid(), 3);
		Assert.assertNotNull(user);
		Assert.assertEquals("sebastian", user.getUserName());
		Assert.assertEquals("seb_stein@gmx.de", user.getEmail());
		Assert.assertEquals("de", user.getLanguage());

		// Try with unexisting id
		user = service.getUser(session.getSid(), 9999);
		Assert.assertNull(user);
	}

	@Test
	public void testSaveGroup() {
		GUIGroup group = service.getGroup(session.getSid(), 10);

		group = service.saveGroup(session.getSid(), group);
		Assert.assertNotNull(group);
		Assert.assertEquals("testGroup", group.getName());
		Assert.assertEquals("Group for tests", group.getDescription());
	}

	@Test
	public void testSaveUser() {
		GUIUser user = service.getUser(session.getSid(), 1);

		user = service.saveUser(session.getSid(), user);
		Assert.assertNotNull(user);
		Assert.assertEquals("admin", user.getUserName());
		Assert.assertEquals("admin@admin.net", user.getEmail());

		user = service.getUser(session.getSid(), 3);

		user = service.saveUser(session.getSid(), user);
		Assert.assertNotNull(user);
		Assert.assertEquals("sebastian", user.getUserName());
		Assert.assertEquals("seb_stein@gmx.de", user.getEmail());
		Assert.assertEquals("de", user.getLanguage());
	}

	@Test
	public void testKill() {
		SessionManager sm = SessionManager.getInstance();
		sm.clear();
		String session1 = sm.newSession("admin", null);
		Assert.assertNotNull(session1);
		String session2 = sm.newSession("admin", null);
		Assert.assertNotNull(session2);
		Assert.assertFalse(session1.equals(session2));
		Assert.assertEquals(2, sm.getSessions().size());

		service.kill(session1);
		Assert.assertTrue(sm.isValid(session2));
		Assert.assertTrue(!sm.isValid(session1));
		Assert.assertEquals(2, sm.getSessions().size());
	}

	@Test
	public void testSaveSettings() {
		GUISecuritySettings securitySettings = new GUISecuritySettings();
		securitySettings.setPwdExpiration(30);
		securitySettings.setPwdSize(6);

		GUIUser user1 = service.getUser(session.getSid(), 1);
		securitySettings.addNotifiedUser(user1);
		GUIUser user2 = service.getUser(session.getSid(), 3);
		securitySettings.addNotifiedUser(user2);

		service.saveSettings(session.getSid(), securitySettings);
	}
}