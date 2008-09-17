package com.logicaldoc.core.security.dao;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.security.Group;
import com.logicaldoc.core.security.dao.GroupDAO;

/**
 * Test case for <code>HibernateGroupDAOTest</code>
 * 
 * @author Alessandro Gasparini
 * @since 3.0
 */
public class HibernateGroupDAOTest extends AbstractCoreTestCase {
    
    protected static Log log = LogFactory.getLog(HibernateGroupDAOTest.class);
    
	// Instance under test
	private GroupDAO dao;

	public HibernateGroupDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateGroupDAO
		dao = (GroupDAO) context.getBean("GroupDAO");
	}

	public void testDelete() {
		assertTrue(dao.exists("testGroup"));
        
		assertTrue(dao.delete("testGroup"));
		assertFalse(dao.exists("testGroup"));
        
        // Delete a BIG group with associated MenuGroups and UserGroups
        assertTrue(dao.delete("admin"));
        assertFalse(dao.exists("admin"));
	}

	public void testExists() {
		assertTrue(dao.exists("admin"));
		assertFalse(dao.exists("unexistent"));
		assertFalse(dao.exists("xxxxo"));
	}

	public void testFindByPrimaryKey() {
        Group group = dao.findByPrimaryKey("admin");
		assertNotNull(group);
		assertEquals("admin", group.getGroupName());

		// Try with unexisting username
        group = dao.findByPrimaryKey("xxxx");
		assertNull(group);
	}
    
    
    public void testFindAllGroupNames() {
        Collection groupNames = dao.findAllGroupNames();
        assertNotNull(groupNames);
        assertFalse(groupNames.isEmpty());
        assertTrue(groupNames.contains("admin"));
        assertTrue(groupNames.contains("testGroup"));
    }    

	public void testStore() {
        assertFalse(dao.exists("LogicalObjects"));
        
        Group group = new Group();
        group.setGroupName("LogicalObjects");
        group.setGroupDesc("Test group for store method");
        
        boolean result = dao.store(group);        
        assertTrue(dao.exists("LogicalObjects"));
        assertTrue(result);
        
        Group group2 = dao.findByPrimaryKey("LogicalObjects");
        assertEquals(group, group2);
	}
    
    
    public void testInsert() {
        assertFalse(dao.exists("parentNone"));
        
        Group group = new Group();
        group.setGroupName("parentNone");
        group.setGroupDesc("Test group for insert method parent = none");
        
        assertTrue(dao.insert(group,null));
        assertTrue(dao.exists("parentNone"));
        
        // Test with parentGroup Not Empty
        assertFalse(dao.exists("parentNotEmpty"));
        
        group = new Group();
        group.setGroupName("parentNotEmpty");
        group.setGroupDesc("Test group for insertX method parentGroup Not Empty");
        
        assertTrue(dao.insert(group,"parentNone"));
        assertTrue(dao.exists("parentNotEmpty"));
    }    
}