package com.logicaldoc.core.security.dao;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.security.Group;

/**
 * Test case for <code>HibernateGroupDAOTest</code>
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class HibernateGroupDAOTest extends AbstractCoreTestCase {

	protected static Log log = LogFactory.getLog(HibernateGroupDAOTest.class);

	// Instance under test
	private GroupDAO dao;

	private com.logicaldoc.core.security.SecurityManager manager;

	public HibernateGroupDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateGroupDAO
		dao = (GroupDAO) context.getBean("GroupDAO");
		manager = (com.logicaldoc.core.security.SecurityManager) context.getBean("SecurityManager");
	}

	public void testDelete() {
		assertNotNull(dao.findById(10));

		assertTrue(dao.delete(10));
		assertNull(dao.findById(10));

		// Delete a BIG group with associated MenuGroups and UserGroups
		assertTrue(dao.delete(1));
		assertNull(dao.findById(1));
	}

	public void testFindByName() {
		Group group = dao.findByName("admin");
		assertNotNull(group);
		assertEquals("admin", group.getName());

		// Try with unexisting name
		group = dao.findByName("xxxx");
		assertNull(group);
	}

	public void testFindById() {
		Group group = dao.findById(1);
		assertNotNull(group);
		assertEquals("admin", group.getName());

		// Try with unexisting id
		group = dao.findById(999);
		assertNull(group);
	}

	public void testFindAllGroupNames() {
		Collection<String> groupNames = dao.findAllGroupNames();
		assertNotNull(groupNames);
		assertFalse(groupNames.isEmpty());
		assertTrue(groupNames.contains("admin"));
		assertTrue(groupNames.contains("testGroup"));
	}

	public void testStore() {
		assertNull(dao.findByName("LogicalObjects"));

		Group group = new Group();
		group.setName("LogicalObjects");
		group.setDescription("Test group for store method");

		boolean result = dao.store(group);
		assertNotNull(dao.findByName("LogicalObjects"));
		assertTrue(result);

		Group group2 = dao.findByName("LogicalObjects");
		assertEquals(group, group2);
	}

	public void testInsert() {
		assertNull(dao.findByName("parentNone"));

		Group group = new Group();
		group.setName("parentNone");
		group.setDescription("Test group for insert method parent = none");

		assertTrue(dao.insert(group, 90));
		assertNotNull(dao.findByName("parentNone"));

		// Test with parentGroup Not Empty
		assertNull(dao.findByName("parentNotEmpty"));

		group = new Group();
		group.setName("parentNotEmpty");
		group.setDescription("Test group for insertX method parentGroup Not Empty");

		assertTrue(dao.insert(group, 90));
		assertNotNull(dao.findByName("parentNotEmpty"));
	}

	public void testInheritACLs() {
		Group group = new Group();
		group.setName("parentNone");
		group.setDescription("Test group for insert method parent = none");

		assertTrue(dao.insert(group, -1));
		assertFalse(manager.getAllowedGroups(6).contains(group));
		
		dao.inheritACLs(group.getId(), 1);
		assertTrue(manager.getAllowedGroups(6).contains(group));
	}
}