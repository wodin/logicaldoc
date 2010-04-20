package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;

/**
 * Test case for <code>HibernateMenuDAOTest</code>
 * 
 * @author Marco Meschieri - Logical Objects
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
		menu.setSort(1);
		menu.setParentId(1);
		menu.setMenuGroup(new long[] { 1, 2 });
		assertTrue(dao.store(menu));

		menu = dao.findById(100);
		assertEquals("menu.admin", menu.getText());
		assertEquals(1, menu.getSort());
		assertEquals(2, menu.getMenuGroups().size());

		// Load an existing menu and modify it
		menu = dao.findById(15);
		assertEquals("search.advanced", menu.getText());

		History transaction = new History();
		transaction.setFolderId(menu.getId());
		transaction.setEvent(History.EVENT_FOLDER_RENAMED);
		transaction.setUserId(1);
		transaction.setNotified(0);
		dao.store(menu, transaction);

		menu = dao.findById(23);
		dao.store(menu);

		menu = dao.findById(18);
		menu.setText("xxxx");
		transaction = new History();
		transaction.setFolderId(menu.getId());
		transaction.setEvent(History.EVENT_FOLDER_RENAMED);
		transaction.setUserId(1);
		transaction.setNotified(0);
		dao.store(menu, transaction);
		menu = dao.findById(15);

		menu.getMenuGroups().remove(menu.getMenuGroup(3));
		assertEquals(2, menu.getMenuGroups().size());
		assertTrue(dao.store(menu));
		menu = dao.findById(Menu.MENUID_HOME);
		assertEquals(3, menu.getMenuGroups().size());

		menu = dao.findById(101);
		menu.setText("pippo");
		assertTrue(dao.store(menu));
		menu = dao.findById(102);
		assertNotNull(menu);

		menu = dao.findById(101);
		menu.setText("pippo2");
		assertTrue(dao.store(menu));
		menu = dao.findById(102);

		menu = dao.findById(102);
		assertTrue(dao.store(menu));
	}

	public void testDelete() {
		assertTrue(dao.delete(99));
		Menu menu = dao.findById(99);
		assertNull(menu);

		DocumentDAO docDao = (DocumentDAO) context.getBean("DocumentDAO");
		docDao.delete(1);

		// Delete a folder with documents
		assertTrue(dao.delete(103));
		menu = dao.findById(103);
		assertNull(menu);
	}

	public void testFindById() {
		// Try with a menu id
		Menu menu = dao.findById(1);
		assertNotNull(menu);
		assertEquals(Menu.MENUID_HOME, menu.getId());
		assertEquals("menu.home", menu.getText());
		assertEquals("home.png", menu.getIcon());
		assertEquals(1, menu.getSort());
		assertEquals(3, menu.getMenuGroups().size());

		// Try with unexisting id
		menu = dao.findById(99999);
		assertNull(menu);
	}

	public void testFindByText() {
		// Try with existing text
		List<Menu> menus = dao.findByText("menu.admin");
		assertNotNull(menus);
		assertEquals(5, menus.size());
		Menu menu = menus.iterator().next();
		assertEquals("menu.admin", menu.getText());

		menus = (List<Menu>) dao.findByText(null, "menu.admin", new Integer(1));
		assertNotNull(menus);
		assertEquals(1, menus.size());

		Menu parent = dao.findById(Menu.MENUID_HOME);
		menus = (List<Menu>) dao.findByText(parent, "menu.admin", 1);
		assertNotNull(menus);
		assertEquals(1, menus.size());

		menus = (List<Menu>) dao.findByText(null, "menu.admin", 3);
		assertNotNull(menus);
		assertEquals(2, menus.size());

		// Try with unexisting text
		menus = dao.findByText("xxxxx");
		assertNotNull(menus);
		assertTrue(menus.isEmpty());
	}

	public void testFindByUserNameString() {
		List<Menu> menus = dao.findByUserId(1);
		assertNotNull(menus);
		assertEquals(27, menus.size());

		menus = dao.findByUserId(3);
		assertNotNull(menus);
		assertEquals(27, menus.size());

		// Try with unexisting user
		menus = dao.findByUserId(99);
		assertNotNull(menus);
		assertEquals(0, menus.size());
	}

	public void testFindByUserId() {
		List<Menu> menus = dao.findByUserId(1, Menu.MENUID_HOME);
		assertNotNull(menus);
		assertEquals(6, menus.size());

		// Try with unexisting user and menus
		menus = dao.findByUserId(1, 70);
		assertNotNull(menus);
		assertEquals(0, menus.size());

		menus = dao.findByUserId(99, Menu.MENUID_HOME);
		assertNotNull(menus);
		assertEquals(0, menus.size());

		menus = dao.findByUserId(1, Menu.MENUID_HOME, Menu.MENUTYPE_DIRECTORY);
		assertNotNull(menus);
		assertEquals(1, menus.size());
	}

	public void testCountByUserId() {
		long count = dao.countByUserId(1, Menu.MENUID_HOME, null);
		assertEquals(7, count);

		// Try with unexisting usernames and menus
		count = dao.countByUserId(1, 70, null);
		assertEquals(0, count);
	}

	public void testFindByParentId() {
		List<Menu> menus = dao.findByParentId(Menu.MENUID_HOME);
		assertNotNull(menus);
		assertEquals(32, menus.size());

		// Try with unexisting parent
		menus = dao.findByParentId(999);
		assertNotNull(menus);
		assertEquals(0, menus.size());
	}

	public void testIsWriteEnable() {
		assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, 1));
		assertTrue(dao.isWriteEnable(26, 1));
		assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, 3));
		assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, 999));
	}

	public void testIsReadEnable() {
		assertTrue(dao.isReadEnable(Menu.MENUID_HOME, 1));
		assertTrue(dao.isReadEnable(26, 1));
		assertFalse(dao.isReadEnable(Menu.MENUID_HOME, 22));
		assertFalse(dao.isReadEnable(Menu.MENUID_HOME, 999));
	}

	public void testIsPermissionEnabled() {
		assertFalse(dao.isPermissionEnabled(Permission.WRITE, Menu.MENUID_HOME, 1));
		assertTrue(dao.isPermissionEnabled(Permission.WRITE, 26, 1));
		assertFalse(dao.isPermissionEnabled(Permission.WRITE, Menu.MENUID_HOME, 3));
		assertFalse(dao.isPermissionEnabled(Permission.WRITE, Menu.MENUID_HOME, 999));
	}

	public void testGetEnabledPermissions() {
		Set<Permission> permissions = dao.getEnabledPermissions(Menu.MENUID_HOME, 1);
		assertEquals(8, permissions.size());
		assertTrue(permissions.contains(Permission.READ));
		assertTrue(permissions.contains(Permission.MANAGE_SECURITY));
		assertTrue(permissions.contains(Permission.SIGN));
		permissions = dao.getEnabledPermissions(26, 1);
		assertEquals(7, permissions.size());
		assertTrue(permissions.contains(Permission.READ));
		assertTrue(permissions.contains(Permission.WRITE));
		permissions = dao.getEnabledPermissions(999, 1);
		assertEquals(0, permissions.size());
	}

	public void testFindMenuIdByUserId() {
		Collection<Long> ids = dao.findMenuIdByUserId(1);
		assertNotNull(ids);
		assertEquals(27, ids.size());

		// Try with unexisting user
		ids = dao.findMenuIdByUserId(99);
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testFindMenuIdByUserIdIntInteger() {
		Collection<Long> ids = dao.findIdByUserId(1, 101, null);
		assertNotNull(ids);
		assertEquals(1, ids.size());
		assertTrue(ids.contains(103L));

		ids = dao.findIdByUserId(1, 101, Menu.MENUTYPE_DIRECTORY);
		assertNotNull(ids);
		assertEquals(1, ids.size());

		ids = dao.findIdByUserId(1, 101, 50);
		assertNotNull(ids);
		assertEquals(0, ids.size());

		// Try with unexisting user
		ids = dao.findIdByUserId(99, 101, null);
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testHasWriteAccess() {
		Menu menu = dao.findById(103);
		assertTrue(dao.hasWriteAccess(menu, 1));
		assertTrue(dao.hasWriteAccess(menu, 3));
		assertFalse(dao.hasWriteAccess(menu, 5));
	}

	public void testFindByGroupId() {
		Collection<Menu> menus = dao.findByGroupId(1);
		assertEquals(27, menus.size());
		menus = dao.findByGroupId(10);
		assertEquals(0, menus.size());
	}

	public void testFindParents() {
		List<Menu> menus = dao.findParents(103);
		assertEquals(3, menus.size());
		assertEquals(dao.findById(Menu.MENUID_HOME), menus.get(0));
		assertEquals(dao.findById(100), menus.get(1));
		assertEquals(dao.findById(101), menus.get(2));
	}

	public void testRestore() {
		Menu menu = dao.findById(1000);
		assertNull(menu);
		menu = dao.findById(1100);
		assertNull(menu);

		dao.restore(1100, true);
		menu = dao.findById(1000);
		assertNotNull(menu);
		menu = dao.findById(1100);
		assertNotNull(menu);
	}

	public void testFindByTextAndParentId() {
		List<Menu> menus = dao.findByTextAndParentId("%menu.admin%", 1);
		assertEquals(3, menus.size());
		assertTrue(menus.contains(dao.findById(99)));
		assertTrue(menus.contains(dao.findById(100)));
		assertTrue(menus.contains(dao.findById(2)));
		menus = dao.findByTextAndParentId("text", 100);
		assertEquals(dao.findById(101), menus.get(0));
	}

	@Test
	public void testFindMenuIdByUserIdAndPermission() {
		Set<Long> ids = dao.findMenuIdByUserIdAndPermission(1, Permission.WRITE, null);
		assertNotNull(ids);
		assertEquals(6, ids.size());
		ids = dao.findMenuIdByUserIdAndPermission(1, Permission.WRITE, Menu.MENUTYPE_MENU);
		assertNotNull(ids);
		assertEquals(4, ids.size());
		ids = dao.findMenuIdByUserIdAndPermission(1, Permission.WRITE, Menu.MENUTYPE_DIRECTORY);
		assertNotNull(ids);
		assertEquals(2, ids.size());
	}

	@Test
	public void testComputePathExtended() {
		assertEquals("/test", dao.computePathExtended(1200));
		assertEquals("/menu.home/menu.admin/text/menu.admin", dao.computePathExtended(103));
		assertEquals("/menu.home/menu.admin/system/menu.gui", dao.computePathExtended(11));
	}
}