package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.List;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;

/**
 * Test case for <code>HibernateMenuDAOTest</code>
 * 
 * @author Marco Meschieri - Logical Objects
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
		menu.setText("text");
		menu.setPath("path");
		menu.setSort(1);
		menu.setMenuGroup(new long[] { 1, 2 });
		assertTrue(dao.store(menu));
		menu = dao.findByPrimaryKey(100);
		assertEquals("db.admin", menu.getText());
		assertEquals("/", menu.getPath());
		assertEquals(1, menu.getSort());
		assertEquals(2, menu.getMenuGroups().size());

		// Load an existing menu and modify it
		menu = dao.findByPrimaryKey(Menu.MENUID_HOME);
		assertEquals("db.home", menu.getText());
		menu.setText("xxxx");
		menu.getMenuGroups().remove(menu.getMenuGroup(3));
		assertEquals(2, menu.getMenuGroups().size());
		assertTrue(dao.store(menu));
		menu = dao.findByPrimaryKey(Menu.MENUID_HOME);
		assertEquals("xxxx", menu.getText());
		assertEquals(2, menu.getMenuGroups().size());
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
		assertEquals(Menu.MENUID_HOME, menu.getId());
		assertEquals("db.home", menu.getText());
		assertEquals("home.png", menu.getIcon());
		assertEquals(1, menu.getSort());
		assertEquals(3, menu.getMenuGroups().size());

		// Try with unexisting id
		menu = dao.findByPrimaryKey(99999);
		assertNull(menu);
	}

	@SuppressWarnings("unchecked")
	public void testFindByText() {
		// Try with existing text
		Collection<Menu> menus = (Collection<Menu>) dao.findByText("db.admin");
		assertNotNull(menus);
		assertEquals(5, menus.size());
		Menu menu = menus.iterator().next();
		assertEquals("db.admin", menu.getText());

		menus = (Collection<Menu>) dao.findByText(null, "db.admin", new Integer(1));
		assertNotNull(menus);
		assertEquals(1, menus.size());

		Menu parent = dao.findByPrimaryKey(Menu.MENUID_HOME);
		menus = (Collection<Menu>) dao.findByText(parent, "db.admin", 1);
		assertNotNull(menus);
		assertEquals(1, menus.size());

		menus = (Collection<Menu>) dao.findByText(null, "db.admin", 3);
		assertNotNull(menus);
		assertEquals(2, menus.size());

		// Try with unexisting text
		menus = dao.findByText("xxxxx");
		assertNotNull(menus);
		assertTrue(menus.isEmpty());
	}

	public void testFindByUserNameString() {
		Collection<Menu> menus = dao.findByUserName("admin");
		assertNotNull(menus);
		assertEquals(21, menus.size());

		menus = dao.findByUserName("sebastian");
		assertNotNull(menus);
		assertEquals(21, menus.size());

		// Try with unexisting username
		menus = dao.findByUserName("xxx");
		assertNotNull(menus);
		assertEquals(0, menus.size());
	}

	public void testFindByUserNameStringIntInt() {
		Collection<Menu> menus = dao.findByUserName("admin", Menu.MENUID_HOME);
		assertNotNull(menus);
		assertEquals(6, menus.size());

		// Try with unexisting usernames and menus
		menus = dao.findByUserName("admin", 70);
		assertNotNull(menus);
		assertEquals(0, menus.size());

		menus = dao.findByUserName("xxxx", Menu.MENUID_HOME);
		assertNotNull(menus);
		assertEquals(0, menus.size());

		menus = dao.findByUserName("admin", Menu.MENUID_HOME, Menu.MENUTYPE_DIRECTORY);
		assertNotNull(menus);
		assertEquals(1, menus.size());
	}

	public void testCountByUserNameStringIntInt() {
		long count = dao.countByUserName("admin", Menu.MENUID_HOME, null);
		assertEquals(6, count);

		// Try with unexisting usernames and menus
		count = dao.countByUserName("admin", 70, null);
		assertEquals(0, count);
	}

	public void testFindByParentId() {
		Collection<Menu> menus = dao.findByParentId(Menu.MENUID_HOME);
		assertNotNull(menus);
		assertEquals(22, menus.size());

		// Try with unexisting parent
		menus = dao.findByParentId(999);
		assertNotNull(menus);
		assertEquals(0, menus.size());
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
		Collection<Long> ids = dao.findMenuIdByUserName("admin");
		assertNotNull(ids);
		assertEquals(21, ids.size());

		// Try with unexisting username
		ids = dao.findMenuIdByUserName("xxxx");
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testFindMenuIdByUserNameIntInteger() {
		Collection<Long> ids = dao.findMenuIdByUserName("admin", 101, null);
		assertNotNull(ids);
		assertEquals(1, ids.size());
		assertTrue(ids.contains(103L));

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

	public void testHasWriteAccess() {
		Menu menu = dao.findByPrimaryKey(103);
		assertTrue(dao.hasWriteAccess(menu, "admin"));
		assertTrue(dao.hasWriteAccess(menu, "sebastian"));
		assertFalse(dao.hasWriteAccess(menu, "test"));
	}

	public void testFindByGroupId() {
		Collection<Menu> menus = dao.findByGroupId(1);
		assertEquals(21, menus.size());
		menus = dao.findByGroupId(10);
		assertEquals(0, menus.size());
	}

	public void testCreateDirectories() throws Exception {
		Menu docsMenu = dao.findByPrimaryKey(Menu.MENUID_DOCUMENTS);
		Menu menu = dao.createFolders(docsMenu, "/pippo/pluto/paperino");
		assertEquals("paperino", menu.getText());
		menu = dao.findByPrimaryKey(menu.getParentId());
		assertEquals("pluto", menu.getText());
		menu = dao.findByPrimaryKey(menu.getParentId());
		assertEquals("pippo", menu.getText());

		menu = dao.createFolders(docsMenu, "/pippo/pluto/paperino");
		assertEquals("paperino", menu.getText());
		menu = dao.findByPrimaryKey(menu.getParentId());
		assertEquals("pluto", menu.getText());
		menu = dao.findByPrimaryKey(menu.getParentId());
		assertEquals("pippo", menu.getText());
	}

	public void testFindParents() {
		List<Menu> menus = dao.findParents(103);
		System.out.println(menus);
		assertEquals(3, menus.size());
		assertEquals(dao.findByPrimaryKey(Menu.MENUID_HOME), menus.get(0));
		assertEquals(dao.findByPrimaryKey(100), menus.get(1));
		assertEquals(dao.findByPrimaryKey(101), menus.get(2));
	}
}