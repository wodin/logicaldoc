package com.logicaldoc.core.sequence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;

public class HibernateSequenceDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private SequenceDAO dao;

	public HibernateSequenceDAOTest(String name) {
		super(name);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateSequqnceDAO
		dao = (SequenceDAO) context.getBean("SequenceDAO");
	}

	@Test
	public void testReset() {
		dao.reset("test", 5);
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i + 5, dao.next("test"));
		}
		dao.reset("test", 100);
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i + 100, dao.next("test"));
		}
		dao.reset("test2", 30);
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i + 30, dao.next("test2"));
		}
	}

	@Test
	public void testNext() {
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i, dao.next("test"));
		}
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i, dao.next("test2"));
		}
	}
}