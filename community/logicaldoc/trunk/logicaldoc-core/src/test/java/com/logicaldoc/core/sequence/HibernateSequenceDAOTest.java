package com.logicaldoc.core.sequence;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.generic.Generic;
import com.logicaldoc.core.security.Tenant;

public class HibernateSequenceDAOTest extends AbstractCoreTCase {

	// Instance under test
	private SequenceDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateSequqnceDAO
		dao = (SequenceDAO) context.getBean("SequenceDAO");
	}

	@Test
	public void testReset() {
		dao.reset("test", Tenant.DEFAULT_ID, 5);
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i + 5, dao.next("test", Tenant.DEFAULT_ID));
		}
		dao.reset("test", Tenant.DEFAULT_ID, 100);
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i + 100, dao.next("test", Tenant.DEFAULT_ID));
		}
		dao.reset("test2", Tenant.DEFAULT_ID, 30);
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i + 30, dao.next("test2", Tenant.DEFAULT_ID));
		}
	}

	@Test
	public void testNext() {
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i, dao.next("test", Tenant.DEFAULT_ID));
		}
		for (int i = 1; i <= 20; i++) {
			Assert.assertEquals(i, dao.next("test2", Tenant.DEFAULT_ID));
		}
	}

	@Test
	public void testFindByName() {
		Collection<Generic> sequences = dao.findByName("customid-", Tenant.DEFAULT_ID);
		Assert.assertNotNull(sequences);
		Assert.assertEquals(2, sequences.size());
	}
}