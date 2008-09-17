package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.UserDoc;
import com.logicaldoc.core.security.dao.UserDocDAO;

/**
 * Test case for <code>HibernateUserDocDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class HibernateUserDocDAOTest extends AbstractCoreTestCase {
	// Instance under test
	private UserDocDAO dao;

	public HibernateUserDocDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateUserDocDAO
		dao = (UserDocDAO) context.getBean("UserDocDAO");
	}

	public void testDelete() {
		assertTrue(dao.exists(1, "admin"));
		dao.delete("admin", 1);
		assertFalse(dao.exists(1, "admin"));
	}

	public void testExists() {
		assertTrue(dao.exists(1, "admin"));
		assertFalse(dao.exists(99, "admin"));
		assertFalse(dao.exists(1, "xxxxo"));
	}

	public void testFindByMinTimeStamp() {
		UserDoc doc = dao.findByMinTimeStamp("admin");
		assertNotNull(doc);
		assertEquals(Menu.MENUID_HOME, doc.getMenuId());
		assertEquals("admin", doc.getUserName());
		assertEquals("2006-12-17", doc.getTimeStamp());

		// Try with unexisting username
		doc = dao.findByMinTimeStamp("xxxx");
		assertNull(doc);
	}

	public void testFindByUserName() {
		Collection<UserDoc> col = dao.findByUserName("admin");
		assertNotNull(col);
		assertEquals(3, col.size());

		// Try with unexisting username
		col = dao.findByUserName("xxxx");
		assertNotNull(col);
		assertTrue(col.isEmpty());
	}

    public void testFindByMenuID() {
        Collection<UserDoc> col = dao.findByMenuId(103);
        assertNotNull(col);
        assertEquals(1, col.size());

        // Try with unexisting username
        col = dao.findByMenuId(9999);
        assertNotNull(col);
        assertTrue(col.isEmpty());
    }

    public void testDeleteInteger() {
        Collection<UserDoc> col = dao.findByMenuId(103);
        assertNotNull(col);
        assertEquals(1, col.size());

        assertTrue(dao.delete(103));
        
        col = dao.findByMenuId(103);
        assertNotNull(col);
        assertEquals(0, col.size());
    }
    
	public void testGetCount() {
		assertEquals(3, dao.getCount("admin"));

		// Try with unexisting username
		assertEquals(0, dao.getCount("xxxx"));
	}

	public void testStore() {
		assertTrue(dao.exists(1, "admin"));
		assertEquals(3, dao.getCount("admin"));
		
		// store 3 more docs
		for(int i=4;i<7;i++){
			UserDoc doc=new UserDoc();
			doc.setMenuId(i);
			doc.setUserName("admin");
			doc.setTimeStamp("2007-01-0"+i);
			assertTrue(dao.store(doc));
			assertTrue(dao.exists(i, "admin"));
		}
		
		assertEquals(5, dao.getCount("admin"));
		
		// When the fifth doc is stored, the oldest must be deleted
		UserDoc doc=new UserDoc();
		doc.setMenuId(6);
		doc.setUserName("admin");
		doc.setTimeStamp("2007-01-06");
		dao.store(doc);
		assertTrue(dao.exists(6, "admin"));
		assertFalse(dao.exists(1, "admin"));
	}
}