package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
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

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateMenuDAO
		dao = (MenuDAO) context.getBean("MenuDAO");
	}

	@Test
	public void testStore() {
		Menu menu = new Menu();
		menu.setText("text");
		menu.setSort(1);
		menu.setParentId(1);
		menu.setMenuGroup(new long[] { 1, 2 });
		Assert.assertTrue(dao.store(menu));

		menu = dao.findById(100);
		Assert.assertEquals("menu.admin", menu.getText());
		Assert.assertEquals(1, menu.getSort());
		Assert.assertEquals(2, menu.getMenuGroups().size());

		// Load an existing menu and modify it
		menu = dao.findById(15);
		Assert.assertEquals("search.advanced", menu.getText());

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
		Assert.assertEquals(2, menu.getMenuGroups().size());
		Assert.assertTrue(dao.store(menu));
		menu = dao.findById(Menu.MENUID_HOME);
		Assert.assertEquals(3, menu.getMenuGroups().size());

		menu = dao.findById(101);
		menu.setText("pippo");
		Assert.assertTrue(dao.store(menu));
		menu = dao.findById(102);
		Assert.assertNotNull(menu);

		menu = dao.findById(101);
		menu.setText("pippo2");
		Assert.assertTrue(dao.store(menu));
		menu = dao.findById(102);

		menu = dao.findById(102);
		Assert.assertTrue(dao.store(menu));
	}

	@Test
	public void testDelete() {
		Assert.assertTrue(dao.delete(99));
		Menu menu = dao.findById(99);
		Assert.assertNull(menu);

		DocumentDAO docDao = (DocumentDAO) context.getBean("DocumentDAO");
		docDao.delete(1);

		// Delete a folder with documents
		Assert.assertTrue(dao.delete(103));
		menu = dao.findById(103);
		Assert.assertNull(menu);
	}

	@Test
	public void testFindById() {
		// Try with a menu id
		Menu menu = dao.findById(1);
		Assert.assertNotNull(menu);
		Assert.assertEquals(Menu.MENUID_HOME, menu.getId());
		Assert.assertEquals("menu.home", menu.getText());
		Assert.assertEquals("home.png", menu.getIcon());
		Assert.assertEquals(1, menu.getSort());
		Assert.assertEquals(3, menu.getMenuGroups().size());

		// Try with unexisting id
		menu = dao.findById(99999);
		Assert.assertNull(menu);
	}

	@Test
	public void testFindByText() {
		// Try with existing text
		List<Menu> menus = dao.findByText("menu.admin");
		Assert.assertNotNull(menus);
		Assert.assertEquals(5, menus.size());
		Menu menu = menus.iterator().next();
		Assert.assertEquals("menu.admin", menu.getText());

		menus = (List<Menu>) dao.findByText(null, "menu.admin", new Integer(1));
		Assert.assertNotNull(menus);
		Assert.assertEquals(1, menus.size());

		Menu parent = dao.findById(Menu.MENUID_HOME);
		menus = (List<Menu>) dao.findByText(parent, "menu.admin", 1);
		Assert.assertNotNull(menus);
		Assert.assertEquals(1, menus.size());

		menus = (List<Menu>) dao.findByText(null, "menu.admin", 3);
		Assert.assertNotNull(menus);
		Assert.assertEquals(2, menus.size());

		// Try with unexisting text
		menus = dao.findByText("xxxxx");
		Assert.assertNotNull(menus);
		Assert.assertTrue(menus.isEmpty());
	}

	@Test
	public void testFindByUserNameString() {
		List<Menu> menus = dao.findByUserId(1);
		Assert.assertNotNull(menus);
		Assert.assertEquals(32, menus.size());

		menus = dao.findByUserId(3);
		Assert.assertNotNull(menus);
		Assert.assertEquals(32, menus.size());

		// Try with unexisting user
		menus = dao.findByUserId(99);
		Assert.assertNotNull(menus);
		Assert.assertEquals(0, menus.size());
	}

	@Test
	public void testFindByUserId() {
		List<Menu> menus = dao.findByUserId(1, Menu.MENUID_HOME);
		Assert.assertNotNull(menus);
		Assert.assertEquals(6, menus.size());

		// Try with unexisting user and menus
		menus = dao.findByUserId(1, 70);
		Assert.assertNotNull(menus);
		Assert.assertEquals(0, menus.size());

		menus = dao.findByUserId(99, Menu.MENUID_HOME);
		Assert.assertNotNull(menus);
		Assert.assertEquals(0, menus.size());

		menus = dao.findByUserId(1, Menu.MENUID_HOME, Menu.MENUTYPE_DIRECTORY);
		Assert.assertNotNull(menus);
		Assert.assertEquals(1, menus.size());
	}

	@Test
	public void testCountByUserId() {
		long count = dao.countByUserId(1, Menu.MENUID_HOME, null);
		Assert.assertEquals(7, count);

		// Try with unexisting usernames and menus
		count = dao.countByUserId(1, 70, null);
		Assert.assertEquals(0, count);
	}

	@Test
	public void testFindByParentId() {
		List<Menu> menus = dao.findByParentId(Menu.MENUID_HOME);
		Assert.assertNotNull(menus);
		Assert.assertEquals(33, menus.size());

		// Try with unexisting parent
		menus = dao.findByParentId(999);
		Assert.assertNotNull(menus);
		Assert.assertEquals(0, menus.size());
	}

	@Test
	public void testIsWriteEnable() {
		Assert.assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, 1));
		Assert.assertTrue(dao.isWriteEnable(26, 1));
		Assert.assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, 3));
		Assert.assertFalse(dao.isWriteEnable(Menu.MENUID_HOME, 999));
	}

	@Test
	public void testIsReadEnable() {
		Assert.assertTrue(dao.isReadEnable(Menu.MENUID_HOME, 1));
		Assert.assertTrue(dao.isReadEnable(26, 1));
		Assert.assertFalse(dao.isReadEnable(Menu.MENUID_HOME, 22));
		Assert.assertFalse(dao.isReadEnable(Menu.MENUID_HOME, 999));
	}

	@Test
	public void testIsPermissionEnabled() {
		Assert.assertFalse(dao.isPermissionEnabled(Permission.WRITE, Menu.MENUID_HOME, 1));
		Assert.assertTrue(dao.isPermissionEnabled(Permission.WRITE, 26, 1));
		Assert.assertFalse(dao.isPermissionEnabled(Permission.WRITE, Menu.MENUID_HOME, 3));
		Assert.assertFalse(dao.isPermissionEnabled(Permission.WRITE, Menu.MENUID_HOME, 999));
	}

	@Test
	public void testGetEnabledPermissions() {
		Set<Permission> permissions = dao.getEnabledPermissions(Menu.MENUID_HOME, 1);
		Assert.assertEquals(8, permissions.size());
		Assert.assertTrue(permissions.contains(Permission.READ));
		Assert.assertTrue(permissions.contains(Permission.MANAGE_SECURITY));
		Assert.assertTrue(permissions.contains(Permission.SIGN));
		permissions = dao.getEnabledPermissions(26, 1);
		Assert.assertEquals(7, permissions.size());
		Assert.assertTrue(permissions.contains(Permission.READ));
		Assert.assertTrue(permissions.contains(Permission.WRITE));
		permissions = dao.getEnabledPermissions(999, 1);
		Assert.assertEquals(0, permissions.size());
	}

	@Test
	public void testFindMenuIdByUserId() {
		Collection<Long> ids = dao.findMenuIdByUserId(1);
		Assert.assertNotNull(ids);
		Assert.assertEquals(32, ids.size());

		// Try with unexisting user
		ids = dao.findMenuIdByUserId(99);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testFindMenuIdByUserIdIntInteger() {
		Collection<Long> ids = dao.findIdByUserId(1, 101, null);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertTrue(ids.contains(103L));

		ids = dao.findIdByUserId(1, 101, Menu.MENUTYPE_DIRECTORY);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());

		ids = dao.findIdByUserId(1, 101, 50);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());

		// Try with unexisting user
		ids = dao.findIdByUserId(99, 101, null);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testHasWriteAccess() {
		Menu menu = dao.findById(103);
		Assert.assertTrue(dao.hasWriteAccess(menu, 1));
		Assert.assertTrue(dao.hasWriteAccess(menu, 3));
		Assert.assertFalse(dao.hasWriteAccess(menu, 5));
	}

	@Test
	public void testFindByGroupId() {
		Collection<Menu> menus = dao.findByGroupId(1);
		Assert.assertEquals(32, menus.size());
		menus = dao.findByGroupId(10);
		Assert.assertEquals(0, menus.size());
	}

	@Test
	public void testFindParents() {
		List<Menu> menus = dao.findParents(103);
		Assert.assertEquals(3, menus.size());
		Assert.assertEquals(dao.findById(Menu.MENUID_HOME), menus.get(0));
		Assert.assertEquals(dao.findById(100), menus.get(1));
		Assert.assertEquals(dao.findById(101), menus.get(2));
	}

	@Test
	public void testRestore() {
		Menu menu = dao.findById(1000);
		Assert.assertNull(menu);
		menu = dao.findById(1100);
		Assert.assertNull(menu);

		dao.restore(1100, true);
		menu = dao.findById(1000);
		Assert.assertNotNull(menu);
		menu = dao.findById(1100);
		Assert.assertNotNull(menu);
	}

	@Test
	public void testFindByTextAndParentId() {
		List<Menu> menus = dao.findByTextAndParentId("%menu.admin%", 1);
		Assert.assertEquals(3, menus.size());
		Assert.assertTrue(menus.contains(dao.findById(99)));
		Assert.assertTrue(menus.contains(dao.findById(100)));
		Assert.assertTrue(menus.contains(dao.findById(2)));
		menus = dao.findByTextAndParentId("text", 100);
		Assert.assertEquals(dao.findById(101), menus.get(0));
	}

	@Test
	public void testFindMenuIdByUserIdAndPermission() {
		Set<Long> ids = dao.findMenuIdByUserIdAndPermission(1, Permission.WRITE, null);
		Assert.assertNotNull(ids);
		Assert.assertEquals(10, ids.size());
		ids = dao.findMenuIdByUserIdAndPermission(1, Permission.WRITE, Menu.MENUTYPE_MENU);
		Assert.assertNotNull(ids);
		Assert.assertEquals(4, ids.size());
		ids = dao.findMenuIdByUserIdAndPermission(1, Permission.WRITE, Menu.MENUTYPE_DIRECTORY);
		Assert.assertNotNull(ids);
		Assert.assertEquals(6, ids.size());
	}

	@Test
	public void testComputePathExtended() {
		Assert.assertEquals("/", dao.computePathExtended(5));
		Assert.assertEquals("/test", dao.computePathExtended(1200));
		Assert.assertEquals("/menu.home/menu.admin/text/menu.admin", dao.computePathExtended(103));
		Assert.assertEquals("/menu.home/menu.admin/system/menu.gui", dao.computePathExtended(11));
	}
}