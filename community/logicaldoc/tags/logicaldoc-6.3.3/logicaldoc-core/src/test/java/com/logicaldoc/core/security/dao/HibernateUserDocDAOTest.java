package com.logicaldoc.core.security.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.security.UserDoc;

/**
 * Test case for <code>HibernateUserDocDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateUserDocDAOTest extends AbstractCoreTCase {
	// Instance under test
	private UserDocDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateUserDocDAO
		dao = (UserDocDAO) context.getBean("UserDocDAO");
	}

	@Test
	public void testDeleteLongLong() {
		Assert.assertTrue(dao.exists(1, 1));
		dao.delete(1, 1);
		Assert.assertFalse(dao.exists(1, 1));
	}

	@Test
	public void testExists() {
		Assert.assertTrue(dao.exists(1, 1));
		Assert.assertFalse(dao.exists(99, 1));
		Assert.assertFalse(dao.exists(1, 99));
	}

	@Test
	public void testFindByMinDate() {
		UserDoc doc = dao.findByMinDate(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals(1, doc.getDocId());
		Assert.assertEquals(1, doc.getUserId());

		// Try with unexisting username
		doc = dao.findByMinDate(99);
		Assert.assertNull(doc);
	}

	@Test
	public void testFindByUserId() {
		Collection<UserDoc> col = dao.findByUserId(1);
		Assert.assertNotNull(col);
		Assert.assertEquals(2, col.size());

		// Try with unexisting user
		col = dao.findByUserId(99);
		Assert.assertNotNull(col);
		Assert.assertTrue(col.isEmpty());
	}

	@Test
	public void testFindByDocID() {
		Collection<UserDoc> col = dao.findByDocId(1);
		Assert.assertNotNull(col);
		Assert.assertEquals(1, col.size());

		// Try with unexisting document
		col = dao.findByDocId(9999);
		Assert.assertNotNull(col);
		Assert.assertTrue(col.isEmpty());
	}

	@Test
	public void testDeleteLong() {
		Collection<UserDoc> col = dao.findByDocId(1);
		Assert.assertNotNull(col);
		Assert.assertEquals(1, col.size());

		Assert.assertTrue(dao.deleteByDocId(1));

		col = dao.findByDocId(1);
		Assert.assertNotNull(col);
		Assert.assertEquals(0, col.size());
	}

	@Test
	public void testGetCount() {
		Assert.assertEquals(2, dao.getCount(1));

		// Try with unexisting user
		Assert.assertEquals(0, dao.getCount(99));
	}

	@Test
	public void testStore() throws ParseException {
		Assert.assertTrue(dao.exists(1, 1));
		Assert.assertEquals(2, dao.getCount(1));

		// store 3 more docs
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 4; i < 7; i++) {
			UserDoc doc = new UserDoc();
			doc.setDocId(1);
			doc.setUserId(1);
			doc.setDate(df.parse("2007-01-0" + i));
			Assert.assertTrue(dao.store(doc));
			Assert.assertTrue(dao.exists(1, 1));
		}

		Assert.assertEquals(5, dao.getCount(1));

		// When the fifth doc is stored, the oldest must be deleted
		UserDoc doc = new UserDoc();
		doc.setDocId(2);
		doc.setUserId(4);
		doc.setDate(new Date());
		dao.store(doc);
		Assert.assertTrue(dao.exists(2, 4));
	}
}