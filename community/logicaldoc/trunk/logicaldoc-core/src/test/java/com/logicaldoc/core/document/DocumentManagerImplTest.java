package com.logicaldoc.core.document;

import java.util.List;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * Test case for <code>DocumentManagerImpl</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5
 */
public class DocumentManagerImplTest extends AbstractCoreTestCase {

	private DocumentDAO docDao;

	private UserDAO userDao;

	private MenuDAO menuDao;

	private HistoryDAO historyDao;

	// Instance under test
	private DocumentManager documentManager;

	public DocumentManagerImplTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		userDao = (UserDAO) context.getBean("UserDAO");
		menuDao = (MenuDAO) context.getBean("MenuDAO");
		historyDao = (HistoryDAO) context.getBean("HistoryDAO");

		// Make sure that this is a DocumentManagerImpl instance
		documentManager = (DocumentManager) context.getBean("DocumentManager");
	}

	public void testMakeImmutable() throws Exception {
		User user = userDao.findByUserName("admin");
		Document doc = docDao.findById(1);
		assertNotNull(doc);
		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setDocId(doc.getId());
		transaction.setUserId(1);
		transaction.setNotified(0);
		transaction.setComment("pippo_reason");
		documentManager.makeImmutable(doc.getId(), user, transaction);
		doc = docDao.findById(1);
		assertEquals(1, doc.getImmutable());
		doc.setFileName("ciccio");
		docDao.initialize(doc);
		docDao.store(doc);
		assertEquals("pippo", doc.getFileName());
		doc.setImmutable(0);
		docDao.store(doc);
		docDao.findById(doc.getId());
		assertEquals(1, doc.getImmutable());
	}

	public void testLock() throws Exception {
		User user = userDao.findByUserName("admin");
		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setDocId(1L);
		transaction.setUserId(1);
		transaction.setNotified(0);
		documentManager.unlock(1L, user, transaction);
		Document doc = docDao.findById(1);
		assertNotNull(doc);
		transaction.setComment("pippo_reason");
		documentManager.lock(doc.getId(), 2, user, transaction);
		doc = docDao.findById(1);
		assertEquals(2, doc.getStatus());
		assertEquals(1L, doc.getLockUserId().longValue());
	}

	public void testMoveFolder_Simple() throws Exception {
		// first create a folder a
		// create folder b
		// create folder c children of b
		// move folder c to parent a

		Menu docsMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuA = menuDao.createFolder(docsMenu, "folderA", null);
		Menu menuB = menuDao.createFolder(docsMenu, "folderB", null);
		Menu menuC = menuDao.createFolder(menuB, "folderC", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");

		documentManager.moveFolder(menuC, menuA, user, transaction);

		List<Menu> menuList = menuDao.findChildren(menuA.getId());
		assertTrue(menuList.size() == 1);

		for (Menu menu : menuList) {
			System.out.println(menu.getId());
		}

		assertTrue(menuList.contains(menuC));
	}

	public void testMoveFolder_Up() throws Exception {
		Menu docsMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuA = menuDao.createFolder(docsMenu, "folderA", null);
		Menu menuB = menuDao.createFolder(docsMenu, "folderB", null);
		Menu menuC = menuDao.createFolder(menuB, "folderC", null);
		menuDao.createFolder(menuC, "folderD", null);
		menuDao.createFolder(menuC, "folderE", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");

		documentManager.moveFolder(menuC, menuA, user, transaction);

		List<Menu> menuList = menuDao.findChildren(menuA.getId());
		assertTrue(menuList.size() == 1);

		// for (Menu menu : menuList) {
		// System.out.println(menu.getId());
		// System.out.println(menu.getText());
		// System.out.println(menu.getPath());
		// System.out.println(menu.getPathExtended());
		// }

		assertTrue(menuList.contains(menuC));

		menuList = menuDao.findChildren(menuB.getId());
		assertTrue(menuList.size() == 0);
	}

	public void testMoveFolder_UpWithDocuments() throws Exception {
		Menu docsMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuA = menuDao.createFolder(docsMenu, "folderA", null);
		Menu menuB = menuDao.createFolder(docsMenu, "folderB", null);
		Menu menuC = menuDao.createFolder(menuB, "folderC", null);
		Menu menuD = menuDao.createFolder(menuC, "folderD", null);
		menuDao.createFolder(menuC, "folderE", null);

		Document doc = docDao.findById(1);
		docDao.initialize(doc);
		doc.setFolder(menuC);
		doc.setIndexed(1);
		docDao.store(doc);

		Document doc2 = docDao.findById(2);
		docDao.initialize(doc2);
		doc2.setFolder(menuD);
		doc2.setIndexed(1);
		docDao.store(doc2);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");

		documentManager.moveFolder(menuC, menuA, user, transaction);

		List<Menu> menuList = menuDao.findChildren(menuA.getId());
		assertTrue(menuList.size() == 1);

		assertTrue(menuList.contains(menuC));

		menuList = menuDao.findChildren(menuB.getId());
		assertTrue(menuList.size() == 0);

		List<Document> docs = docDao.findByIndexed(0);
		assertEquals(0, docs.size());

		// Check the history creation
		List<History> folderHistory = historyDao.findByFolderId(menuC.getId());
		assertTrue(folderHistory.size() > 0);

		boolean eventPresent = false;
		for (History history : folderHistory) {
			if (history.getEvent().equals(History.EVENT_FOLDER_MOVED))
				eventPresent = true;
		}
		assertTrue(eventPresent);
	}

	public void testMoveFolder_Down() throws Exception {
		Menu docsMenu = menuDao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuB = menuDao.createFolder(docsMenu, "folderB", null);
		Menu menuC = menuDao.createFolder(menuB, "folderC", null);
		Menu menuD = menuDao.createFolder(menuC, "folderD", null);
		Menu menuE = menuDao.createFolder(menuC, "folderE", null);
		menuDao.createFolder(menuE, "folderF", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");

		documentManager.moveFolder(menuE, menuD, user, transaction);

		List<Menu> menuList = menuDao.findChildren(menuD.getId());
		assertTrue(menuList.size() == 1);
		assertTrue(menuList.contains(menuE));

		menuList = menuDao.findChildren(menuC.getId());
		assertTrue(menuList.size() == 1);
	}

}