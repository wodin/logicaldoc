package com.logicaldoc.core.security.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.security.UserDoc;

/**
 * Test case for <code>HibernateUserDocDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
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

	public void testDeleteLongLong() {
		assertTrue(dao.exists(1, 1));
		dao.delete(1, 1);
		assertFalse(dao.exists(1, 1));
	}

	public void testExists() {
		assertTrue(dao.exists(1, 1));
		assertFalse(dao.exists(99, 1));
		assertFalse(dao.exists(1, 99));
	}

	public void testFindByMinDate() {
		UserDoc doc = dao.findByMinDate(1);
		assertNotNull(doc);
		assertEquals(1, doc.getDocId());
		assertEquals(1, doc.getUserId());

		// Try with unexisting username
		doc = dao.findByMinDate(99);
		assertNull(doc);
	}

	public void testFindByUserId() {
		Collection<UserDoc> col = dao.findByUserId(1);
		assertNotNull(col);
		assertEquals(2, col.size());

		// Try with unexisting user
		col = dao.findByUserId(99);
		assertNotNull(col);
		assertTrue(col.isEmpty());
	}

	public void testFindByDocID() {
		Collection<UserDoc> col = dao.findByDocId(1);
		assertNotNull(col);
		assertEquals(1, col.size());

		// Try with unexisting document
		col = dao.findByDocId(9999);
		assertNotNull(col);
		assertTrue(col.isEmpty());
	}

	public void testDeleteLong() {
		Collection<UserDoc> col = dao.findByDocId(1);
		assertNotNull(col);
		assertEquals(1, col.size());

		assertTrue(dao.deleteByDocId(1));

		col = dao.findByDocId(1);
		assertNotNull(col);
		assertEquals(0, col.size());
	}

	public void testGetCount() {
		assertEquals(2, dao.getCount(1));

		// Try with unexisting user
		assertEquals(0, dao.getCount(99));
	}

	public void testStore() throws ParseException {
		assertTrue(dao.exists(1, 1));
		assertEquals(2, dao.getCount(1));

		// store 3 more docs
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 4; i < 7; i++) {
			UserDoc doc = new UserDoc();
			doc.setDocId(1);
			doc.setUserId(1);
			doc.setDate(df.parse("2007-01-0" + i));
			assertTrue(dao.store(doc));
			assertTrue(dao.exists(1, 1));
		}

		assertEquals(5, dao.getCount(1));

		// When the fifth doc is stored, the oldest must be deleted
		UserDoc doc = new UserDoc();
		doc.setDocId(2);
		doc.setUserId(4);
		doc.setDate(new Date());
		dao.store(doc);
		assertTrue(dao.exists(2, 4));
	}
}