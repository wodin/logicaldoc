package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.i18n.DateBean;
import com.logicaldoc.core.security.UserHistory;

/**
 * Test case for <code>HibernateUserHistoryDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.0
 */
public class HibernateUserHistoryDAOTest extends AbstractCoreTCase {

	// Instance under test
	private UserHistoryDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateHistoryDAO
		dao = (UserHistoryDAO) context.getBean("UserHistoryDAO");
	}

	@Test
	public void testDelete() {
		Collection<UserHistory> histories = (Collection<UserHistory>) dao.findByUserId(1);
		Assert.assertNotNull(histories);
		Assert.assertEquals(2, histories.size());

		for (UserHistory history : histories) {
			boolean result = dao.delete(history.getId());
			Assert.assertTrue(result);
		}

		histories = (Collection<UserHistory>) dao.findByUserId(4);
		Assert.assertNotNull(histories);
		Assert.assertEquals(0, histories.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindByUserId() {
		Collection histories = dao.findByUserId(1);
		Assert.assertNotNull(histories);
		Assert.assertEquals(2, histories.size());

		// Try with unexisting user
		histories = dao.findByUserId(99);
		Assert.assertNotNull(histories);
		Assert.assertEquals(0, histories.size());
	}

	@Test
	public void testStore() {
		UserHistory userHistory = new UserHistory();
		userHistory.setDate(DateBean.dateFromCompactString("20061220"));
		userHistory.setUserName("sebastian");
		userHistory.setUserId(3);
		userHistory.setEvent("first test User History store");

		Assert.assertTrue(dao.store(userHistory));

		UserHistory newUserHistory = new UserHistory();
		newUserHistory.setDate(DateBean.dateFromCompactString("20061220"));
		newUserHistory.setUserName("sebastian");
		newUserHistory.setUserId(3);
		newUserHistory.setEvent("second test User History store");

		Assert.assertTrue(dao.store(newUserHistory));

		// Test the stored history
		Collection<UserHistory> histories = (Collection<UserHistory>) dao.findByUserId(3);
		Assert.assertNotNull(histories);
		Assert.assertFalse(histories.isEmpty());

		Iterator<UserHistory> itHist = histories.iterator();
		UserHistory hStored = itHist.next();
		Assert.assertTrue(hStored.equals(newUserHistory));
		Assert.assertEquals(hStored.getDate().getTime(), DateBean.dateFromCompactString("20061220").getTime());
		Assert.assertEquals(hStored.getUserName(), "sebastian");
		Assert.assertEquals(hStored.getEvent(), "second test User History store");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCleanOldHistories() {
		UserHistory history = dao.findById(1);
		Assert.assertNotNull(history);

		Collection histories = dao.findAll();
		Assert.assertNotNull(histories);
		Assert.assertEquals(3, histories.size());

		dao.cleanOldHistories(5);

		history = dao.findById(1);
		Assert.assertNull(history);
		histories = dao.findAll();
		Assert.assertEquals(0, histories.size());
	}
}