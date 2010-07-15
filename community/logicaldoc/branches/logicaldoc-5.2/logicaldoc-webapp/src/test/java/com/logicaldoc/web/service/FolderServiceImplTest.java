package com.logicaldoc.web.service;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.web.AbstractWebappTestCase;

public class FolderServiceImplTest extends AbstractWebappTestCase {

	// Instance under test
	private FolderServiceImpl service = new FolderServiceImpl();

	private GUISession session;

	private FolderDAO folderDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		folderDao = (FolderDAO) context.getBean("FolderDAO");

		SecurityServiceImpl securityService = new SecurityServiceImpl();
		session = securityService.login("admin", "admin", null);
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
	}

	@Test
	public void testSave() throws InvalidSessionException {
		GUIFolder folder = service.getFolder(session.getSid(), 100, false);

		folder = service.save(session.getSid(), folder);
		Assert.assertNotNull(folder);
		Assert.assertEquals("menu", folder.getName());
		Assert.assertEquals(5, folder.getParentId());

		folder = service.getFolder(session.getSid(), 103, false);

		folder = service.save(session.getSid(), folder);
		Assert.assertNotNull(folder);
		Assert.assertEquals("menu103", folder.getName());
		Assert.assertEquals(101, folder.getParentId());
	}

	@Test
	public void testRename() throws InvalidSessionException {
		Menu menu = folderDao.findById(100);
		Assert.assertEquals("menu", menu.getText());

		service.rename(session.getSid(), 100, "pluto");

		menu = folderDao.findById(100);
		Assert.assertEquals("pluto", menu.getText());
	}

	@Test
	public void testApplyRights() throws InvalidSessionException {
		Menu parentMenu = folderDao.findById(101);
		Assert.assertNotNull(parentMenu);
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.DELETE, 101, 4));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.RENAME, 101, 4));
		Menu childFolder1 = folderDao.findById(102);
		Assert.assertNotNull(childFolder1);
		Assert.assertEquals(101, childFolder1.getParentId());
		Assert.assertFalse(folderDao.isPermissionEnabled(Permission.DELETE, 102, 4));
		Assert.assertFalse(folderDao.isPermissionEnabled(Permission.RENAME, 102, 4));
		Menu childFolder2 = folderDao.findById(103);
		Assert.assertNotNull(childFolder2);
		Assert.assertEquals(101, childFolder2.getParentId());
		Assert.assertFalse(folderDao.isPermissionEnabled(Permission.DELETE, 103, 4));
		Assert.assertFalse(folderDao.isPermissionEnabled(Permission.RENAME, 103, 4));

		GUIFolder folder = service.getFolder(session.getSid(), 101, false);

		service.applyRights(session.getSid(), folder, true);

		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.DELETE, 102, 1));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.RENAME, 102, 1));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.DELETE, 103, 1));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.RENAME, 103, 1));
	}

	@Test
	public void testGetFolder() throws InvalidSessionException {
		GUIFolder folder = service.getFolder(session.getSid(), 99, false);
		Assert.assertNotNull(folder);
		Assert.assertEquals("menu.admin1", folder.getName());
		Assert.assertEquals(1, folder.getParentId());

		folder = service.getFolder(session.getSid(), 102, true);
		Assert.assertNotNull(folder);
		Assert.assertEquals("menu102", folder.getName());
		Assert.assertEquals(101, folder.getParentId());
		Assert.assertEquals("/menu/text/menu102", folder.getPathExtended());
		Assert.assertEquals(service.getFolder(session.getSid(), 100, false).getName(), folder.getPath()[1].getName());

		// Try with unexisting id
		folder = service.getFolder(session.getSid(), 9999, false);
		Assert.assertNull(folder);
	}

	@Test
	public void testDelete() throws InvalidSessionException {
		service.delete(session.getSid(), 99);
		Menu menu = folderDao.findById(99);
		Assert.assertNull(menu);

		DocumentDAO docDao = (DocumentDAO) context.getBean("DocumentDAO");
		docDao.delete(1);

		// Delete a folder with documents
		service.delete(session.getSid(), 103);
		menu = folderDao.findById(103);
		Assert.assertNull(menu);
	}

	@Test
	public void testMoveFolder_Simple() throws Exception {
		Menu docsMenu = folderDao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuA = folderDao.create(docsMenu, "folderA", null);
		Menu menuB = folderDao.create(docsMenu, "folderB", null);
		Menu menuC = folderDao.create(menuB, "folderC", null);

		service.move(session.getSid(), menuC.getId(), menuA.getId());

		List<Menu> menuList = folderDao.findChildren(menuA.getId(), null);
		Assert.assertTrue(menuList.size() == 1);

		for (Menu menu : menuList) {
			System.out.println(menu.getId());
		}

		Assert.assertTrue(menuList.contains(menuC));
	}

	@Test
	public void testMoveFolder_Up() throws Exception {
		Menu docsMenu = folderDao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuA = folderDao.create(docsMenu, "folderA", null);
		Menu menuB = folderDao.create(docsMenu, "folderB", null);
		Menu menuC = folderDao.create(menuB, "folderC", null);
		folderDao.create(menuC, "folderD", null);
		folderDao.create(menuC, "folderE", null);

		service.move(session.getSid(), menuC.getId(), menuA.getId());

		List<Menu> menuList = folderDao.findChildren(menuA.getId(), null);
		Assert.assertTrue(menuList.size() == 1);
		Assert.assertTrue(menuList.contains(menuC));

		menuList = folderDao.findChildren(menuB.getId(), null);
		Assert.assertTrue(menuList.size() == 0);
	}

	@Test
	public void testMoveFolder_Down() throws Exception {
		Menu docsMenu = folderDao.findById(Menu.MENUID_DOCUMENTS);
		Menu menuB = folderDao.create(docsMenu, "folderB", null);
		Menu menuC = folderDao.create(menuB, "folderC", null);
		Menu menuD = folderDao.create(menuC, "folderD", null);
		Menu menuE = folderDao.create(menuC, "folderE", null);
		folderDao.create(menuE, "folderF", null);

		service.move(session.getSid(), menuE.getId(), menuD.getId());

		List<Menu> menuList = folderDao.findChildren(menuD.getId(), null);
		Assert.assertTrue(menuList.size() == 1);
		Assert.assertTrue(menuList.contains(menuE));

		menuList = folderDao.findChildren(menuC.getId(), null);
		Assert.assertTrue(menuList.size() == 1);
	}
}