package com.logicaldoc.webapp.security;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.webapp.AbstractWebappTestCase;

public class SecurityServiceImplTest extends AbstractWebappTestCase {

	// Instance under test
	private SecurityServiceImpl service = new SecurityServiceImpl();

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
		Assert.assertNull(session);
		session = service.login("unexisting", "admin");
		Assert.assertNull(session);
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
}