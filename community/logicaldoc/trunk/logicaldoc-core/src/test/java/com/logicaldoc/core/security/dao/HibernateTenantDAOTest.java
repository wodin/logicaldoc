package com.logicaldoc.core.security.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.security.Tenant;

/**
 * Test case for <code>HibernateTenantDAOTest</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class HibernateTenantDAOTest extends AbstractCoreTCase {

	protected static Logger log = LoggerFactory.getLogger(HibernateTenantDAOTest.class);

	// Instance under test
	private TenantDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateTenantDAO
		dao = (TenantDAO) context.getBean("TenantDAO");
	}

	@Test
	public void testDelete() {
		Assert.assertNotNull(dao.findById(2));

		Assert.assertTrue(dao.delete(2));
		Assert.assertNull(dao.findById(2));
	}

	@Test
	public void testFindByName() {
		Tenant tenant = dao.findByName(Tenant.DEFAULT_NAME);
		Assert.assertNotNull(tenant);
		Assert.assertEquals("default", tenant.getName());

		tenant = dao.findByName("tenant2");
		Assert.assertNotNull(tenant);
		Assert.assertEquals("tenant2", tenant.getName());

		// Try with unexisting name
		tenant = dao.findByName("xxxx");
		Assert.assertNull(tenant);
	}
}