package com.logicaldoc.core.security.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.security.Folder;
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

	private GroupDAO groupDao;

	private UserDAO userDao;

	private FolderDAO folderDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateTenantDAO
		dao = (TenantDAO) context.getBean("TenantDAO");
		folderDao = (FolderDAO) context.getBean("FolderDAO");
		userDao = (UserDAO) context.getBean("UserDAO");
		groupDao = (GroupDAO) context.getBean("GroupDAO");
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

	@Test
	public void testStore() {
		Tenant tenant = new Tenant();
		tenant.setName("test2");
		Assert.assertTrue(dao.store(tenant));

		List<Folder> folders = folderDao.findByName("/", tenant.getId());
		Assert.assertEquals(1, folders.size());

		folders = folderDao.findByName("Default", tenant.getId());
		Assert.assertEquals(1, folders.size());

		Assert.assertNotNull(userDao.findByName("adminTest2"));

		Assert.assertNotNull(groupDao.findByName("admin", tenant.getId()));
	}
}