package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.Iterator;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.i18n.DateBean;
import com.logicaldoc.core.security.UserHistory;

/**
 * Test case for <code>HibernateUserHistoryDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.0
 */
public class HibernateUserHistoryDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private UserHistoryDAO dao;

	public HibernateUserHistoryDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateHistoryDAO
		dao = (UserHistoryDAO) context.getBean("UserHistoryDAO");
	}

	public void testDelete() {
		Collection<UserHistory> histories = (Collection<UserHistory>) dao.findByUserId(1);
		assertNotNull(histories);
		assertEquals(2, histories.size());

		for (UserHistory history : histories) {
			boolean result = dao.delete(history.getId());
			assertTrue(result);
		}

		histories = (Collection<UserHistory>) dao.findByUserId(4);
		assertNotNull(histories);
		assertEquals(0, histories.size());
	}

	@SuppressWarnings("unchecked")
	public void testFindByUserId() {
		Collection histories = dao.findByUserId(1);
		assertNotNull(histories);
		assertEquals(2, histories.size());

		// Try with unexisting user
		histories = dao.findByUserId(99);
		assertNotNull(histories);
		assertEquals(0, histories.size());
	}

	public void testStore() {
		UserHistory userHistory = new UserHistory();
		userHistory.setDate(DateBean.dateFromCompactString("20061220"));
		userHistory.setUserName("sebastian");
		userHistory.setUserId(3);
		userHistory.setEvent("first test User History store");

		assertTrue(dao.store(userHistory));

		UserHistory newUserHistory = new UserHistory();
		newUserHistory.setDate(DateBean.dateFromCompactString("20061220"));
		newUserHistory.setUserName("sebastian");
		newUserHistory.setUserId(3);
		newUserHistory.setEvent("second test User History store");

		assertTrue(dao.store(newUserHistory));

		// Test the stored history
		Collection<UserHistory> histories = (Collection<UserHistory>) dao.findByUserId(3);
		assertNotNull(histories);
		assertFalse(histories.isEmpty());

		Iterator<UserHistory> itHist = histories.iterator();
		UserHistory hStored = itHist.next();
		assertTrue(hStored.equals(newUserHistory));
		assertEquals(hStored.getDate().getTime(), DateBean.dateFromCompactString("20061220").getTime());
		assertEquals(hStored.getUserName(), "sebastian");
		assertEquals(hStored.getEvent(), "second test User History store");
	}

	@SuppressWarnings("unchecked")
	public void testCleanOldHistories() {
		UserHistory history = dao.findById(1);
		assertNotNull(history);

		Collection histories = dao.findAll();
		assertNotNull(histories);
		assertEquals(3, histories.size());

		dao.cleanOldHistories(5);

		history = dao.findById(1);
		assertNull(history);
		histories = dao.findAll();
		assertEquals(0, histories.size());
	}
}