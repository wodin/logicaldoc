package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.Date;
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
	public void testFindByFolderId() {
		Collection histories = dao.findByFolderId(5);
		assertNotNull(histories);
		assertEquals(3, histories.size());

		// Try with unexisting folderId
		histories = dao.findByFolderId(99);
		assertNotNull(histories);
		assertEquals(0, histories.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testCreateDocumentHistory() {
		History history = new History();
		history.setDocId(1L);
		history.setFolderId(5);
		history.setTitle("pippo");
		history.setVersion("2.0");

		history.setPath("/" + "paperino");

		history.setDate(new Date());
		history.setUserId(1);
		history.setUserName("mario");
		history.setEvent(History.EVENT_STORED);
		history.setComment("The document has been created.");

		dao.store(history);
		
		Collection histories = dao.findByDocId(1);
		assertNotNull(histories);
		assertEquals(3, histories.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testCreateFolderHistory() {
		History history = new History();
		history.setFolderId(1200);
		history.setTitle("folder_test");

		history.setPath("/" + "minnie");

		Date date = new Date();
		history.setDate(date);
		history.setUserId(1);
		history.setUserName("paolo");
		history.setEvent(History.EVENT_FOLDER_CREATED);
		history.setComment("The folder has been created.");

		dao.store(history);
		
		//Store the history for the parent fodler
		History parentHistory = new History();
		parentHistory.setFolderId(5);
		parentHistory.setTitle("Documenti");

		parentHistory.setPath("/");
		parentHistory.setPath(parentHistory.getPath().replaceAll("//", "/"));
		parentHistory.setPath(parentHistory.getPath().replaceFirst("/menu.documents/", "/"));
		parentHistory.setPath(parentHistory.getPath().replaceFirst("/menu.documents", "/"));

		parentHistory.setDate(date);
		parentHistory.setUserId(1);
		parentHistory.setUserName("alle");
		parentHistory.setEvent(History.EVENT_FOLDER_SUBFOLDER_CREATED);
		parentHistory.setComment("A sub-folder has been created.");
		
		dao.store(parentHistory);
		
		Collection histories = dao.findByFolderId(1200);
		assertNotNull(histories);
		assertEquals(1, histories.size());
		
		histories = dao.findByFolderId(5);
		assertNotNull(histories);
		assertEquals(4, histories.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testStore() {
		History history = new History();
		history.setDocId(1L);
		history.setFolderId(5);
		history.setDate(DateBean.dateFromCompactString("20061220"));
		history.setUserName("sebastian");
		history.setUserId(3);
		history.setEvent("test History store");

		assertTrue(dao.store(history));
		
		History folderHistory = new History();
		folderHistory.setFolderId(5);
		folderHistory.setDate(DateBean.dateFromCompactString("20061220"));
		folderHistory.setUserName("sebastian");
		folderHistory.setUserId(3);
		folderHistory.setEvent("test History store");

		assertTrue(dao.store(folderHistory));

		// Test the stored history
		Collection<History> histories = (Collection<History>) dao.findByUserId(3);
		assertNotNull(histories);
		assertFalse(histories.isEmpty());

		Iterator<History> itHist = histories.iterator();
		History hStored = itHist.next();
		assertTrue(hStored.equals(folderHistory));
		assertEquals(hStored.getFolderId(), 5);
		assertEquals(hStored.getDate().getTime(), DateBean.dateFromCompactString("20061220").getTime());
		assertEquals(hStored.getUserName(), "sebastian");
		assertEquals(hStored.getEvent(), "test History store");
	}
}