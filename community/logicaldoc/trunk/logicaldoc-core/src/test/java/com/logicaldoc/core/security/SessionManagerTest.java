package com.logicaldoc.core.security;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test case for the <code>SessionManager</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class SessionManagerTest {

	@Test
	public void testNewSession() {
		SessionManager sm = SessionManager.getInstance();
		sm.clear();
		String session1 = sm.newSession("admin");
		Assert.assertNotNull(session1);
		String session2 = sm.newSession("admin");
		Assert.assertNotNull(session2);
		Assert.assertFalse(session1.equals(session2));
		Assert.assertEquals(2, sm.getSessions().size());
	}

	@Test
	public void testKill() {
		SessionManager sm = SessionManager.getInstance();
		sm.clear();
		String session1 = sm.newSession("admin");
		Assert.assertNotNull(session1);
		String session2 = sm.newSession("admin");
		Assert.assertNotNull(session2);
		Assert.assertFalse(session1.equals(session2));
		Assert.assertEquals(2, sm.getSessions().size());

		sm.kill(session1);
		Assert.assertTrue(sm.isValid(session2));
		Assert.assertTrue(!sm.isValid(session1));
		Assert.assertEquals(2, sm.getSessions().size());
	}
}
