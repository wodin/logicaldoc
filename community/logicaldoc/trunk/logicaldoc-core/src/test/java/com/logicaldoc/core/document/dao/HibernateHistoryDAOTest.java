package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.Iterator;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.i18n.DateBean;

/**
 * Test case for <code>HibernateHistoryDAO</code>
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0
 */
public class HibernateHistoryDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private HistoryDAO dao;

	public HibernateHistoryDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateHistoryDAO
		dao = (HistoryDAO) context.getBean("HistoryDAO");
	}

	@SuppressWarnings("unchecked")
	public void testDelete() {
		Collection<History> histories = (Collection<History>) dao.findByUserId(1);
		assertNotNull(histories);
		assertEquals(2, histories.size());

		for (History history : histories) {
			boolean result = dao.delete(history.getId());
			assertTrue(result);
		}

		histories = (Collection<History>) dao.findByUserId(4);
		assertNotNull(histories);
		assertEquals(0, histories.size());
	}

	@SuppressWarnings("unchecked")
	public void testFindByDocId() {
		Collection histories = dao.findByDocId(1);
		assertNotNull(histories);
		assertEquals(2, histories.size());

		// Try with unexisting docId
		histories = dao.findByDocId(99);
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

	@SuppressWarnings("unchecked")
	public void testStore() {
		History history = new History();
		history.setDocId(1);
		history.setDate(DateBean.dateFromCompactString("20061220"));
		history.setUserName("sebastian");
		history.setUserId(3);
		history.setEvent("test History store");

		assertTrue(dao.store(history));

		// Test the stored history
		Collection<History> histories = (Collection<History>) dao.findByUserId(3);
		assertNotNull(histories);
		assertFalse(histories.isEmpty());

		Iterator<History> itHist = histories.iterator();
		History hStored = itHist.next();
		assertTrue(hStored.equals(history));
		assertEquals(hStored.getDocId(), 1);
		assertEquals(hStored.getDate().getTime(), DateBean.dateFromCompactString("20061220").getTime());
		assertEquals(hStored.getUserName(), "sebastian");
		assertEquals(hStored.getEvent(), "test History store");
	}
}