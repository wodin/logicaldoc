package com.logicaldoc.core.security.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.UserDoc;

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

	public void testFindByMinDate() {
		UserDoc doc = dao.findByMinDate("admin");
		assertNotNull(doc);
		assertEquals(1, doc.getDocId());
		assertEquals("admin", doc.getUserName());

		// Try with unexisting username
		doc = dao.findByMinDate("xxxx");
		assertNull(doc);
	}

	public void testFindByUserName() {
		Collection<UserDoc> col = dao.findByUserName("admin");
		assertNotNull(col);
		assertEquals(2, col.size());

		// Try with unexisting username
		col = dao.findByUserName("xxxx");
		assertNotNull(col);
		assertTrue(col.isEmpty());
	}

    public void testFindByDocID() {
        Collection<UserDoc> col = dao.findByDocId(1);
        assertNotNull(col);
        assertEquals(1, col.size());

        // Try with unexisting username
        col = dao.findByDocId(9999);
        assertNotNull(col);
        assertTrue(col.isEmpty());
    }

    public void testDeleteLong() {
        Collection<UserDoc> col = dao.findByDocId(1);
        assertNotNull(col);
        assertEquals(1, col.size());

        assertTrue(dao.delete(1));
        
        col = dao.findByDocId(1);
        assertNotNull(col);
        assertEquals(0, col.size());
    }
    
	public void testGetCount() {
		assertEquals(2, dao.getCount("admin"));

		// Try with unexisting username
		assertEquals(0, dao.getCount("xxxx"));
	}

	public void testStore() throws ParseException {
		assertTrue(dao.exists(1, "admin"));
		assertEquals(2, dao.getCount("admin"));
		
		
		
		// store 3 more docs
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		for(int i=4;i<7;i++){
			UserDoc doc=new UserDoc();
			doc.setDocId(i);
			doc.setUserName("admin");
			doc.setDate(df.parse("2007-01-0"+i));
			assertTrue(dao.store(doc));
			assertTrue(dao.exists(i, "admin"));
		}
		
		assertEquals(5, dao.getCount("admin"));
		
		// When the fifth doc is stored, the oldest must be deleted
		UserDoc doc=new UserDoc();
		doc.setDocId(6);
		doc.setUserName("admin");
		doc.setDate(new Date());
		dao.store(doc);
		assertTrue(dao.exists(6, "admin"));
	}
}