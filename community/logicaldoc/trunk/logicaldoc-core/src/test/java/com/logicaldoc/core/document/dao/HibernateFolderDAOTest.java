package com.logicaldoc.core.document.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * Test case for <code>HibernateMenuDAOTest</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class HibernateFolderDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private FolderDAO dao;

	private UserDAO userDao;

	private DocumentDAO docDao;

	private HistoryDAO historyDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateMenuDAO
		dao = (FolderDAO) context.getBean("FolderDAO");
		userDao = (UserDAO) context.getBean("UserDAO");
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		historyDao = (HistoryDAO) context.getBean("HistoryDAO");

	}

	@Test
	public void testCreatePath() throws Exception {
		Menu docsMenu = dao.findById(Menu.MENUID_DOCUMENTS);
		Menu menu = dao.createPath(docsMenu, "/pippo/pluto/paperino", null);
		Assert.assertEquals("paperino", menu.getText());
		menu = dao.findById(menu.getParentId());
		Assert.assertEquals("pluto", menu.getText());
		menu = dao.findById(menu.getParentId());
		Assert.assertEquals("pippo", menu.getText());

		menu = dao.createPath(docsMenu, "/pippo/pluto/paperino", null);
		Assert.assertEquals("paperino", menu.getText());
		menu = dao.findById(menu.getParentId());
		Assert.assertEquals("pluto", menu.getText());
		menu = dao.findById(menu.getParentId());
		Assert.assertEquals("pippo", menu.getText());
	}

	@Test
	public void testFind() {
		Menu folder = dao.find("test", "/");
		Assert.assertNotNull(folder);
		Assert.assertEquals("test", folder.getText());
		Assert.assertEquals(1200, folder.getId());

		folder = dao.find("xyz", "/test/abc");
		Assert.assertNotNull(folder);
		Assert.assertEquals("xyz", folder.getText());
		Assert.assertEquals(1202, folder.getId());

		folder = dao.find("qqq", "/test/abc");
		Assert.assertNull(folder);
	}

	@Test
	public void testDeleteTree() throws Exception {
		Assert.assertNotNull(dao.findById(1200));
		Assert.assertNotNull(dao.findById(1202));
		User user = new User();
		user.setUserName("admin");
		user.setId(1);
		History history = new History();
		history.setUser(user);
		dao.deleteTree(dao.findById(1200), history);
		Assert.assertNull(dao.findById(1200));
		Assert.assertNull(dao.findById(1202));
		Assert.assertFalse(dao.delete(1200));
	}

	@Test
	public void testMoveFolder_Simple() throws Exception {
		Menu docsMenu = dao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuA = dao.create(docsMenu, "folderA", null);
		Menu menuB = dao.create(docsMenu, "folderB", null);
		Menu menuC = dao.create(menuB, "folderC", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");
		transaction.setUser(user);

		dao.move(menuC, menuA, transaction);

		List<Menu> menuList = dao.findChildren(menuA.getId());
		Assert.assertTrue(menuList.size() == 1);

		for (Menu menu : menuList) {
			System.out.println(menu.getId());
		}

		Assert.assertTrue(menuList.contains(menuC));
	}

	@Test
	public void testMoveFolder_Up() throws Exception {
		Menu docsMenu = dao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuA = dao.create(docsMenu, "folderA", null);
		Menu menuB = dao.create(docsMenu, "folderB", null);
		Menu menuC = dao.create(menuB, "folderC", null);
		dao.create(menuC, "folderD", null);
		dao.create(menuC, "folderE", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");
		transaction.setUser(user);

		dao.move(menuC, menuA, transaction);

		List<Menu> menuList = dao.findChildren(menuA.getId());
		Assert.assertTrue(menuList.size() == 1);
		Assert.assertTrue(menuList.contains(menuC));

		menuList = dao.findChildren(menuB.getId());
		Assert.assertTrue(menuList.size() == 0);
	}

	@Test
	public void testMoveFolder_UpWithDocuments() throws Exception {
		Menu docsMenu = dao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuA = dao.create(docsMenu, "folderA", null);
		Menu menuB = dao.create(docsMenu, "folderB", null);
		Menu menuC = dao.create(menuB, "folderC", null);
		Menu menuD = dao.create(menuC, "folderD", null);
		dao.create(menuC, "folderE", null);

		Document doc = docDao.findById(1);
		docDao.initialize(doc);
		doc.setFolder(menuC);
		doc.setIndexed(AbstractDocument.INDEX_INDEXED);
		docDao.store(doc);

		Document doc2 = docDao.findById(2);
		docDao.initialize(doc2);
		doc2.setFolder(menuD);
		doc2.setIndexed(AbstractDocument.INDEX_INDEXED);
		docDao.store(doc2);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");
		transaction.setUser(user);

		dao.move(menuC, menuA, transaction);

		List<Menu> menuList = dao.findChildren(menuA.getId());
		Assert.assertTrue(menuList.size() == 1);

		Assert.assertTrue(menuList.contains(menuC));

		menuList = dao.findChildren(menuB.getId());
		Assert.assertTrue(menuList.size() == 0);

		List<Document> docs = docDao.findByIndexed(0);
		Assert.assertEquals(0, docs.size());

		// Check the history creation
		List<History> folderHistory = historyDao.findByFolderId(menuC.getId());
		Assert.assertTrue(folderHistory.size() > 0);

		boolean eventPresent = false;
		for (History history : folderHistory) {
			if (history.getEvent().equals(History.EVENT_FOLDER_MOVED))
				eventPresent = true;
		}
		Assert.assertTrue(eventPresent);
	}

	@Test
	public void testMoveFolder_Down() throws Exception {
		Menu docsMenu = dao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuB = dao.create(docsMenu, "folderB", null);
		Menu menuC = dao.create(menuB, "folderC", null);
		Menu menuD = dao.create(menuC, "folderD", null);
		Menu menuE = dao.create(menuC, "folderE", null);
		dao.create(menuE, "folderF", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");
		transaction.setUser(user);

		dao.move(menuE, menuD, transaction);

		List<Menu> menuList = dao.findChildren(menuD.getId());
		Assert.assertTrue(menuList.size() == 1);
		Assert.assertTrue(menuList.contains(menuE));

		menuList = dao.findChildren(menuC.getId());
		Assert.assertTrue(menuList.size() == 1);
	}
}