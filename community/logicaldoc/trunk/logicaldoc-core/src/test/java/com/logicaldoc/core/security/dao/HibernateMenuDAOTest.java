package com.logicaldoc.core.security.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;

/**
 * Test case for <code>HibernateMenuDAOTest</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class HibernateMenuDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private MenuDAO dao;

	public HibernateMenuDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateMenuDAO
		dao = (MenuDAO) context.getBean("MenuDAO");
	}

	public void testStore() {
		Menu menu = new Menu();
		menu.setMenuText("text");
		menu.setMenuPath("path");
		menu.setMenuSort(1);
		menu.setMenuGroup(new String[] { "admin", "author" });
		assertTrue(dao.store(menu));
		menu = dao.findByPrimaryKey(100);
		assertEquals("db.admin", menu.getMenuText());
		assertEquals("ROOT", menu.getMenuPath());
		assertEquals(1, menu.getMenuSort());
		assertEquals(2, menu.getMenuGroups().size());

		// Load an existing menu and modify it
		menu = dao.findByPrimaryKey(Menu.MENUID_HOME);
		assertEquals("db.home", menu.getMenuText());
		menu.setMenuText("xxxx");
		assertTrue(dao.store(menu));
		menu = dao.findByPrimaryKey(Menu.MENUID_HOME);
		assertEquals("xxxx", menu.getMenuText());
	}

	public void testDelete() {
		assertTrue(dao.delete(99));
		Menu menu = dao.findByPrimaryKey(99);
		assertNull(menu);

		DocumentDAO docDao = (DocumentDAO) context.getBean("DocumentDAO");
		docDao.delete(1);
		try {
			assertTrue(dao.delete(103));
			fail();
		} catch (Exception e) {
			// All OK, we are trying to delete a folder with documents
		}
		menu = dao.findByPrimaryKey(103);
		assertNotNull(menu);
	}

	public void testFindByPrimaryKey() {
		// Try with a menu id
		Menu menu = dao.findByPrimaryKey(1);
		assertNotNull(menu);
		assertEquals(Menu.MENUID_HOME, menu.getMenuId());
		assertEquals("db.home", menu.getMenuText());
		assertEquals("home.png", menu.getMenuIcon());
		assertEquals(1, menu.getMenuSort());
		assertEquals(3, menu.getMenuGroups().size());

		// Try with unexisting id
		menu = dao.findByPrimaryKey(99999);
		assertNull(menu);
	}

	@SuppressWarnings("unchecked")
	public void testFindByMenuText() {
		// Try with existing text
		Collection<Menu> menues = (Collection<Menu>) dao.findByMenuText("db.admin");
		assertNotNull(menues);
		assertEquals(5, menues.size());
		Menu menu = menues.iterator().next();
		assertEquals("db.admin", menu.getMenuText());

		menues = (Collection<Menu>) dao.findByMenuText(null, "db.admin", new Integer(1));
		assertNotNull(menues);
		assertEquals(1, menues.size());

		Menu parent = dao.findByPrimaryKey(Menu.MENUID_HOME);
		menues = (Collection<Menu>) dao.findByMenuText(parent, "db.admin", 1);
		assertNotNull(menues);
		assertEquals(1, menues.size());

		menues = (Collection<Menu>) dao.findByMenuText(null, "db.admin", 3);
		assertNotNull(menues);
		assertEquals(2, menues.size());

		// Try with unexisting text
		menues = dao.findByMenuText("xxxxx");
		assertNotNull(menues);
		assertTrue(menues.isEmpty());
	}

	public void testFindByUserNameString() {
		Collection<Menu> menues = dao.findByUserName("admin");
		assertNotNull(menues);
		assertEquals(21, menues.size());

		menues = dao.findByUserName("sebastian");
		assertNotNull(menues);
		assertEquals(21, menues.size());

		// Try with unexisting username
		menues = dao.findByUserName("xxx");
		assertNotNull(menues);
		assertEquals(0, menues.size());
	}

	public void testFindByUserNameStringIntInt() {
		Collection<Menu> menues = dao.findByUserName("admin", Menu.MENUID_HOME);
		assertNotNull(menues);
		assertEquals(6, menues.size());

		// Try with unexisting usernames and menues
		menues = dao.findByUserName("admin", 70);
		assertNotNull(menues);
		assertEquals(0, menues.size());

		menues = dao.findByUserName("xxxx", Menu.MENUID_HOME);
		assertNotNull(menues);
		assertEquals(0, menues.size());

		menues = dao.findByUserName("admin", Menu.MENUID_HOME, Menu.MENUTYPE_DIRECTORY);
		assertNotNull(menues);
		assertEquals(1, menues.size());
	}

	public void testCountByUserNameStringIntInt() {
		long count = dao.countByUserName("admin", Menu.MENUID_HOME, null);
		assertEquals(6, count);

		// Try with unexisting usernames and menues
		count = dao.countByUserName("admin", 70, null);
		assertEquals(0, count);
	}

	public void testFindByParentId() {
		Collection<Menu> menues = dao.findByParentId(Menu.MENUID_HOME);
		assertNotNull(menues);
		assertEquals(22, menues.size());

		// Try with unexisting parent
		menues = dao.findByParentId(999);
		assertNotNull(menues);
		assertEquals(0, menues.size());
	}

	public void testIsWriteEnable() {
		assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, "admin"));
		assertTrue(dao.isWriteEnable(26, "admin"));
		assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, "guest"));
		assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, "xxxx"));
	}

	public void testIsReadEnable() {
		assertTrue(dao.isReadEnable(Menu.MENUID_HOME, "admin"));
		assertTrue(dao.isReadEnable(26, "admin"));
		assertFalse(dao.isReadEnable(Menu.MENUID_HOME, "guest"));
		assertFalse(dao.isReadEnable(Menu.MENUID_HOME, "xxxx"));
	}

	public void testFindMenuIdByUserName() {
		Collection<Integer> ids = dao.findMenuIdByUserName("admin");
		assertNotNull(ids);
		assertEquals(21, ids.size());

		// Try with unexisting username
		ids = dao.findMenuIdByUserName("xxxx");
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testFindMenuIdByUserNameIntInteger() {
		Collection<Integer> ids = dao.findMenuIdByUserName("admin", 101, null);
		assertNotNull(ids);
		assertEquals(1, ids.size());
		assertTrue(ids.contains(103));

		ids = dao.findMenuIdByUserName("admin", 101, Menu.MENUTYPE_DIRECTORY);
		assertNotNull(ids);
		assertEquals(1, ids.size());

		ids = dao.findMenuIdByUserName("admin", 101, 50);
		assertNotNull(ids);
		assertEquals(0, ids.size());

		// Try with unexisting username
		ids = dao.findMenuIdByUserName("xxxx", 101, null);
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testGetContainedMenus() {
		Collection<Menu> coll = dao.getContainedMenus(2, "admin");
		assertEquals(8, coll.size());
		coll = dao.getContainedMenus(2, "sebastian");
		assertEquals(8, coll.size());
	}

	public void testHasWriteAccess() {
		Menu menu = dao.findByPrimaryKey(103);
		assertTrue(dao.hasWriteAccess(menu, "admin"));
		assertTrue(dao.hasWriteAccess(menu, "sebastian"));
		assertFalse(dao.hasWriteAccess(menu, "test"));
	}

	public void testFindByGroupName() {
		Collection<Menu> menues = dao.findByGroupName("admin");
		assertEquals(21, menues.size());
		menues = dao.findByGroupName("testGroup");
		assertEquals(0, menues.size());
	}

	public void testCreateDirectories() throws Exception {
		Menu docsMenu = dao.findByPrimaryKey(Menu.MENUID_DOCUMENTS);
		Menu menu = dao.createFolders(docsMenu, "/pippo/pluto/paperino");
		assertEquals("paperino", menu.getMenuText());
		menu = dao.findByPrimaryKey(menu.getMenuParent());
		assertEquals("pluto", menu.getMenuText());
		menu = dao.findByPrimaryKey(menu.getMenuParent());
		assertEquals("pippo", menu.getMenuText());

		menu = dao.createFolders(docsMenu, "/pippo/pluto/paperino");
		assertEquals("paperino", menu.getMenuText());
		menu = dao.findByPrimaryKey(menu.getMenuParent());
		assertEquals("pluto", menu.getMenuText());
		menu = dao.findByPrimaryKey(menu.getMenuParent());
		assertEquals("pippo", menu.getMenuText());
	}
}