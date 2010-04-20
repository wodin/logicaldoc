package com.logicaldoc.webapp.security;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.webapp.AbstractWebappTestCase;

public class SecurityServiceImplTest extends AbstractWebappTestCase {

	// Instance under test
	private SecurityServiceImpl service = new SecurityServiceImpl();

	@Test
	public void testLogin() {
		GUIUser user = service.login("admin", "admin");
		Assert.assertNotNull(user);
		Assert.assertNotNull(SessionManager.getInstance().get(user.getSid()));
		Assert.assertEquals("admin", user.getUserName());
		Assert.assertEquals(1, user.getId());
		Assert.assertEquals(1, SessionManager.getInstance().countOpened());
		SessionManager.getInstance().get(user.getSid()).setClosed();
		Assert.assertEquals(0, SessionManager.getInstance().countOpened());

		user = service.login("admin", "password");
		Assert.assertNull(user);
		user = service.login("unexisting", "admin");
		Assert.assertNull(user);
	}

	@Test
	public void testLogout() {
		GUIUser user = service.login("admin", "admin");
		Assert.assertNotNull(user);
		Assert.assertNotNull(SessionManager.getInstance().get(user.getSid()));
		Assert.assertEquals("admin", user.getUserName());
		Assert.assertEquals(1, user.getId());
		Assert.assertEquals(1, SessionManager.getInstance().countOpened());

		service.logout(user.getSid());

		Assert.assertEquals(0, SessionManager.getInstance().countOpened());
	}

	@Test
	public void testChangePassword() {
	  Assert.assertEquals(0, service.changePassword(1, "admin", "test"));
	  Assert.assertEquals(0, service.changePassword(1, "test", "admin"));
	  Assert.assertNotSame(0, service.changePassword(1, "xxxxx", "test"));
	}
}