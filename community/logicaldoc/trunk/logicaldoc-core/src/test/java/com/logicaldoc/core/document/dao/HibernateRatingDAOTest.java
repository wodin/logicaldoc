package com.logicaldoc.core.document.dao;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.Rating;

/**
 * Test case for <code>HibernateRatingDAOTest</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class HibernateRatingDAOTest extends AbstractCoreTCase {

	// Instance under test
	private RatingDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateRatingDAO
		dao = (RatingDAO) context.getBean("RatingDAO");
	}

	@Test
	public void testStore() {
		Rating rat1 = dao.findById(1);
		dao.initialize(rat1);
		rat1.setVote(4);
		rat1.setUserId(3);
		dao.store(rat1);
		Assert.assertNotNull(rat1);

		Rating rat2 = dao.findById(2);
		dao.initialize(rat2);
		rat2.setVote(2);
		rat2.setDocId(4);
		dao.store(rat2);
		Assert.assertNotNull(rat2);

		rat1 = dao.findById(1);
		Assert.assertEquals(4, rat1.getVote());
		Assert.assertEquals(3, rat1.getUserId());
		rat2 = dao.findById(2);
		Assert.assertEquals(2, rat2.getVote());
		Assert.assertEquals(4, rat2.getDocId());
	}

	@Test
	public void testFindVotesByDocId() {
		Rating rat1 = dao.findVotesByDocId(1);
		Assert.assertNotNull(rat1);
		Assert.assertEquals(2, rat1.getCount().intValue());
		Assert.assertEquals(new Float(2.0), rat1.getAverage().floatValue());

		rat1 = dao.findVotesByDocId(2);
		Assert.assertNotNull(rat1);
		Assert.assertEquals(1, rat1.getCount().intValue());
		Assert.assertEquals(new Float(3.0), rat1.getAverage().floatValue());

		// Try with unexisting rating vote
		rat1 = dao.findVotesByDocId(99);
		Assert.assertNull(rat1);
	}

	@Test
	public void testFindByDocIdAndUserId() {
		Assert.assertTrue(dao.findByDocIdAndUserId(1, 1));
		Assert.assertFalse(dao.findByDocIdAndUserId(2, 2));
	}
}
