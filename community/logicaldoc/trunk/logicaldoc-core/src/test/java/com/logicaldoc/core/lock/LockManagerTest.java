package com.logicaldoc.core.lock;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.generic.GenericDAO;
import com.logicaldoc.core.security.Tenant;

/**
 * Test case for <code>LockManager</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class LockManagerTest extends AbstractCoreTCase {
	private LockManager manager;

	private GenericDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		manager = (LockManager) context.getBean("LockManager");
		dao = (GenericDAO) context.getBean("GenericDAO");
	}

	@Test
	public void testGet() {
		Assert.assertTrue(manager.get("test", "t1"));
		Assert.assertTrue(manager.get("test", "t1"));
		Assert.assertFalse(manager.get("test", "t2"));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		Assert.assertTrue(manager.get("test", "t2"));
		manager.release("test", "t2");

		Generic lock = dao.findByAlternateKey("lock", "test", null, Tenant.DEFAULT_ID);
		Assert.assertNotNull(lock);
		Assert.assertNull(lock.getString1());
		Assert.assertNull(lock.getDate1());
	}
}
