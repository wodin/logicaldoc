package com.logicaldoc.web.service;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.web.AbstractWebappTCase;

public class FolderServiceImplTest extends AbstractWebappTCase {

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
		GUIFolder folder = service.getFolder(session.getSid(), 6, false);

		folder = service.save(session.getSid(), folder);
		Assert.assertNotNull(folder);
		Assert.assertEquals("folder6", folder.getName());
		Assert.assertEquals(5, folder.getParentId());

		folder = service.getFolder(session.getSid(), 1200, false);

		folder = service.save(session.getSid(), folder);
		Assert.assertNotNull(folder);
		Assert.assertEquals("test", folder.getName());
		Assert.assertEquals(5, folder.getParentId());
	}

	@Test
	public void testRename() throws InvalidSessionException {
		Folder folder = folderDao.findById(6);
		Assert.assertEquals("folder6", folder.getName());

		service.rename(session.getSid(), 6, "pluto");

		folder = folderDao.findById(6);
		Assert.assertEquals("pluto", folder.getName());
	}

	@Test
	public void testApplyRights() throws InvalidSessionException {
		Folder parentFolder = folderDao.findById(6);
		Assert.assertNotNull(parentFolder);
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.DELETE, 1201, 3));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.RENAME, 1201, 3));
		Folder childFolder1 = folderDao.findById(1202);
		Assert.assertNotNull(childFolder1);
		Assert.assertEquals(1201, childFolder1.getParentId());
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.DELETE, 1202, 3));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.RENAME, 1202, 3));

		GUIFolder folder = service.getFolder(session.getSid(), 6, false);

		service.applyRights(session.getSid(), folder, true);

		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.DELETE, 1202, 1));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.RENAME, 1202, 1));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.DELETE, 1201, 1));
		Assert.assertTrue(folderDao.isPermissionEnabled(Permission.RENAME, 1201, 1));
	}

	@Test
	public void testGetFolder() throws InvalidSessionException {
		GUIFolder folder = service.getFolder(session.getSid(), 6, false);
		Assert.assertNotNull(folder);
		Assert.assertEquals("folder6", folder.getName());
		Assert.assertEquals(5, folder.getParentId());

		folder = service.getFolder(session.getSid(), 1202, true);
		Assert.assertNotNull(folder);
		Assert.assertEquals("xyz", folder.getName());
		Assert.assertEquals(1201, folder.getParentId());
		Assert.assertEquals("/test/ABC/xyz", folder.getPathExtended());

		// Try with unexisting id
		folder = service.getFolder(session.getSid(), 9999, false);
		Assert.assertNull(folder);
	}

	@Test
	public void testMoveFolder_Simple() throws Exception {
		Folder docsFolder = folderDao.findById(Folder.ROOTID);
		Folder folderA = folderDao.create(docsFolder, "folderA", Folder.TYPE_DEFAULT, true, null);
		Folder folderB = folderDao.create(docsFolder, "folderB", Folder.TYPE_DEFAULT, true, null);
		Folder folderC = folderDao.create(folderB, "folderC", Folder.TYPE_DEFAULT, true, null);

		service.move(session.getSid(), folderC.getId(), folderA.getId());

		List<Folder> folderList = folderDao.findChildren(folderA.getId(), null);
		Assert.assertTrue(folderList.size() == 1);

		for (Folder folder : folderList) {
			System.out.println(folder.getId());
		}

		Assert.assertTrue(folderList.contains(folderC));
	}

	@Test
	public void testMoveFolder_Up() throws Exception {
		Folder docsFolder = folderDao.findById(Folder.ROOTID);
		Folder folderA = folderDao.create(docsFolder, "folderA", Folder.TYPE_DEFAULT, true, null);
		Folder folderB = folderDao.create(docsFolder, "folderB", Folder.TYPE_DEFAULT, true, null);
		Folder folderC = folderDao.create(folderB, "folderC", Folder.TYPE_DEFAULT, true, null);
		folderDao.create(folderC, "folderD", Folder.TYPE_DEFAULT, true, null);
		folderDao.create(folderC, "folderE", Folder.TYPE_DEFAULT, true, null);

		service.move(session.getSid(), folderC.getId(), folderA.getId());

		List<Folder> folderList = folderDao.findChildren(folderA.getId(), null);
		Assert.assertTrue(folderList.size() == 1);
		Assert.assertTrue(folderList.contains(folderC));

		folderList = folderDao.findChildren(folderB.getId(), null);
		Assert.assertTrue(folderList.size() == 0);
	}

	@Test
	public void testMoveFolder_Down() throws Exception {
		Folder docsFolder = folderDao.findById(Folder.ROOTID);
		Folder folderB = folderDao.create(docsFolder, "folderB", Folder.TYPE_DEFAULT, true, null);
		Folder folderC = folderDao.create(folderB, "folderC", Folder.TYPE_DEFAULT, true, null);
		Folder folderD = folderDao.create(folderC, "folderD", Folder.TYPE_DEFAULT, true, null);
		Folder folderE = folderDao.create(folderC, "folderE", Folder.TYPE_DEFAULT, true, null);
		folderDao.create(folderE, "folderF", Folder.TYPE_DEFAULT, true, null);

		service.move(session.getSid(), folderE.getId(), folderD.getId());

		List<Folder> folderList = folderDao.findChildren(folderD.getId(), null);
		Assert.assertTrue(folderList.size() == 1);
		Assert.assertTrue(folderList.contains(folderE));

		folderList = folderDao.findChildren(folderC.getId(), null);
		Assert.assertTrue(folderList.size() == 1);
	}
}