package com.logicaldoc.core.security;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;

/**
 * Test case for the <code>SessionManager</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.6
 */
public class SessionManagerTest extends AbstractCoreTCase {

	@Test
	public void testNewSession() {
		SessionManager sm = SessionManager.get();
		sm.clear();
		Session session1 = sm.newSession("admin", "admin", null);
		Assert.assertNotNull(session1);
		Session session2 = sm.newSession("admin", "admin", null);
		Assert.assertNotNull(session2);
		Assert.assertFalse(session1.equals(session2));
		Assert.assertEquals(2, sm.getSessions().size());
	}

	@Test
	public void testKill() {
		SessionManager sm = SessionManager.get();
		sm.clear();
		Session session1 = sm.newSession("admin", "admin", null);
		Assert.assertNotNull(session1);
		Session session2 = sm.newSession("admin", "admin", null);
		Assert.assertNotNull(session2);
		Assert.assertFalse(session1.equals(session2));
		Assert.assertEquals(2, sm.getSessions().size());

		sm.kill(session1.getId());
		Assert.assertTrue(sm.isOpen(session2.getId()));
		Assert.assertTrue(!sm.isOpen(session1.getId()));
		Assert.assertEquals(2, sm.getSessions().size());
	}
}
