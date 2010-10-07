package com.logicaldoc.core.security.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Permission;
import com.logicaldoc.core.security.User;

/**
 * Test case for <code>HibernateFolderDAOTest</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class HibernateFolderDAOTest extends AbstractCoreTCase {

	// Instance under test
	private FolderDAO dao;

	private UserDAO userDao;

	private DocumentDAO docDao;

	private HistoryDAO historyDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateFolderDAO
		dao = (FolderDAO) context.getBean("FolderDAO");
		userDao = (UserDAO) context.getBean("UserDAO");
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		historyDao = (HistoryDAO) context.getBean("HistoryDAO");
	}

	@Test
	public void testCreatePath() throws Exception {
		Folder docsFolder = dao.findById(6);
		Folder folder = dao.createPath(docsFolder, "/pippo/pluto/paperino", null);
		Assert.assertEquals("paperino", folder.getName());
		folder = dao.findById(folder.getParentId());
		Assert.assertEquals("pluto", folder.getName());
		folder = dao.findById(folder.getParentId());
		Assert.assertEquals("pippo", folder.getName());

		folder = dao.createPath(docsFolder, "/pippo/pluto/paperino", null);
		Assert.assertEquals("paperino", folder.getName());
		folder = dao.findById(folder.getParentId());
		Assert.assertEquals("pluto", folder.getName());
		folder = dao.findById(folder.getParentId());
		Assert.assertEquals("pippo", folder.getName());
	}

	@Test
	public void testFind() {
		Folder folder = dao.find("test", "/");
		Assert.assertNotNull(folder);
		Assert.assertEquals("test", folder.getName());
		Assert.assertEquals(1200, folder.getId());

		folder = dao.find("xyz", "/test/ABC");
		Assert.assertNotNull(folder);
		Assert.assertEquals("xyz", folder.getName());
		Assert.assertEquals(1202, folder.getId());

		folder = dao.find("qqq", "/test/ABC");
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
		Folder docsFolder = dao.findById(6);
		Folder folderA = dao.create(docsFolder, "folderA", null);
		Folder folderB = dao.create(docsFolder, "folderB", null);
		Folder folderC = dao.create(folderB, "folderC", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");
		transaction.setUser(user);

		dao.move(folderC, folderA, transaction);

		List<Folder> folderList = dao.findChildren(folderA.getId(), null);
		Assert.assertTrue(folderList.size() == 1);

		for (Folder folder : folderList) {
			System.out.println(folder.getId());
		}

		Assert.assertTrue(folderList.contains(folderC));
	}

	@Test
	public void testMoveFolder_Up() throws Exception {
		Folder docsFolder = dao.findById(6);
		Folder folderA = dao.create(docsFolder, "folderA", null);
		Folder folderB = dao.create(docsFolder, "folderB", null);
		Folder folderC = dao.create(folderB, "folderC", null);
		dao.create(folderC, "folderD", null);
		dao.create(folderC, "folderE", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");
		transaction.setUser(user);

		dao.move(folderC, folderA, transaction);

		List<Folder> folderList = dao.findChildren(folderA.getId(), null);
		Assert.assertTrue(folderList.size() == 1);
		Assert.assertTrue(folderList.contains(folderC));

		folderList = dao.findChildren(folderB.getId(), null);
		Assert.assertTrue(folderList.size() == 0);
	}

	@Test
	public void testMoveFolder_UpWithDocuments() throws Exception {
		Folder docsFolder = dao.findById(6);
		Folder folderA = dao.create(docsFolder, "folderA", null);
		Folder folderB = dao.create(docsFolder, "folderB", null);
		Folder folderC = dao.create(folderB, "folderC", null);
		Folder folderD = dao.create(folderC, "folderD", null);
		dao.create(folderC, "folderE", null);

		Document doc = docDao.findById(1);
		docDao.initialize(doc);
		doc.setFolder(folderC);
		doc.setIndexed(AbstractDocument.INDEX_INDEXED);
		docDao.store(doc);

		Document doc2 = docDao.findById(2);
		docDao.initialize(doc2);
		doc2.setFolder(folderD);
		doc2.setIndexed(AbstractDocument.INDEX_INDEXED);
		docDao.store(doc2);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");
		transaction.setUser(user);

		dao.move(folderC, folderA, transaction);

		List<Folder> folderList = dao.findChildren(folderA.getId(), null);
		Assert.assertTrue(folderList.size() == 1);

		Assert.assertTrue(folderList.contains(folderC));

		folderList = dao.findChildren(folderB.getId(), null);
		Assert.assertTrue(folderList.size() == 0);

		List<Document> docs = docDao.findByIndexed(0);
		Assert.assertEquals(0, docs.size());

		// Check the history creation
		List<History> folderHistory = historyDao.findByFolderId(folderC.getId());
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
		Folder docsFolder = dao.findById(6);
		Folder folderB = dao.create(docsFolder, "folderB", null);
		Folder folderC = dao.create(folderB, "folderC", null);
		Folder folderD = dao.create(folderC, "folderD", null);
		Folder folderE = dao.create(folderC, "folderE", null);
		dao.create(folderE, "folderF", null);

		User user = userDao.findByUserName("admin");

		History transaction = new History();
		transaction.setNotified(0);
		transaction.setComment("");
		transaction.setUser(user);

		dao.move(folderE, folderD, transaction);

		List<Folder> folderList = dao.findChildren(folderD.getId(), null);
		Assert.assertTrue(folderList.size() == 1);
		Assert.assertTrue(folderList.contains(folderE));

		folderList = dao.findChildren(folderC.getId(), null);
		Assert.assertTrue(folderList.size() == 1);
	}

	@Test
	public void testIsInPath() throws Exception {
		Assert.assertTrue(dao.isInPath(1200, 1201));
		Assert.assertTrue(dao.isInPath(1200, 1202));
		Assert.assertFalse(dao.isInPath(99, 1202));
	}

	@Test
	public void testFind2() {
		List<Folder> folders = dao.find("folder");
		Assert.assertNotNull(folders);
		Assert.assertEquals(2, folders.size());
		Folder folder = dao.findById(6);
		Folder folder2 = dao.findById(7);
		Assert.assertTrue(folders.contains(folder));
		Assert.assertTrue(folders.contains(folder2));

		folders = dao.find("folder.adminxx");
		Assert.assertEquals(0, folders.size());
	}

	@Test
	public void testStore() {
		Folder folder = new Folder();
		folder.setName("text");
		folder.setParentId(5);
		folder.setFolderGroup(new long[] { 1, 2 });
		Assert.assertTrue(dao.store(folder));

		folder = dao.findById(6);
		Assert.assertEquals("folder6", folder.getName());
		Assert.assertEquals(3, folder.getFolderGroups().size());

		// Load an existing folder and modify it
		folder = dao.findById(6);
		Assert.assertEquals("folder6", folder.getName());

		History transaction = new History();
		transaction.setFolderId(folder.getId());
		transaction.setEvent(History.EVENT_FOLDER_RENAMED);
		transaction.setUser(userDao.findById(1));
		transaction.setNotified(0);
		dao.store(folder, transaction);

		folder = dao.findById(7);
		dao.store(folder);

		folder = dao.findById(7);
		folder.setName("xxxx");
		transaction = new History();
		transaction.setFolderId(folder.getId());
		transaction.setEvent(History.EVENT_FOLDER_RENAMED);
		transaction.setUser(userDao.findById(1));
		transaction.setNotified(0);
		dao.store(folder, transaction);

		folder = dao.findById(6);
		folder.getFolderGroups().remove(folder.getFolderGroup(2));
		Assert.assertEquals(2, folder.getFolderGroups().size());
		Assert.assertTrue(dao.store(folder));
		folder = dao.findById(Folder.ROOTID);
		Assert.assertEquals(2, folder.getFolderGroups().size());

		folder = dao.findById(1200);
		folder.setName("pippo");
		Assert.assertTrue(dao.store(folder));
		folder = dao.findById(1202);
		Assert.assertNotNull(folder);

		folder = dao.findById(1201);
		folder.setName("pippo2");
		Assert.assertTrue(dao.store(folder));

		folder = dao.findById(1202);
		Assert.assertTrue(dao.store(folder));
	}

	@Test
	public void testDelete() {
		Assert.assertTrue(dao.delete(1202));
		Folder folder = dao.findById(12012);
		Assert.assertNull(folder);

		DocumentDAO docDao = (DocumentDAO) context.getBean("DocumentDAO");
		docDao.delete(1202);

		// Delete a folder with documents
		Assert.assertTrue(dao.delete(1201));
		folder = dao.findById(1201);
		Assert.assertNull(folder);
	}

	@Test
	public void testFindById() {
		// Try with a folder id
		Folder folder = dao.findById(Folder.ROOTID);
		Assert.assertNotNull(folder);
		Assert.assertEquals(Folder.ROOTID, folder.getId());
		Assert.assertEquals("/", folder.getName());
		Assert.assertEquals(2, folder.getFolderGroups().size());

		// Try with unexisting id
		folder = dao.findById(99999);
		Assert.assertNull(folder);
	}

	@Test
	public void testFindByName() {
		// Try with existing text
		List<Folder> folders = dao.findByName("%folder%");
		Assert.assertNotNull(folders);
		Assert.assertEquals(2, folders.size());

		folders = (List<Folder>) dao.findByName(null, "test", true);
		Assert.assertNotNull(folders);
		Assert.assertEquals(1, folders.size());

		Folder parent = dao.findById(Folder.ROOTID);
		folders = (List<Folder>) dao.findByName(parent, "test", true);
		Assert.assertNotNull(folders);
		Assert.assertEquals(1, folders.size());

		// Try with unexisting text
		folders = dao.findByName("xxxxx");
		Assert.assertNotNull(folders);
		Assert.assertTrue(folders.isEmpty());
	}

	@Test
	public void testFindByUserId() {
		List<Folder> folders = dao.findByUserId(1, Folder.ROOTID);
		Assert.assertNotNull(folders);
		Assert.assertEquals(3, folders.size());

		// Try with unexisting user and folders
		folders = dao.findByUserId(1, 70);
		Assert.assertNotNull(folders);
		Assert.assertEquals(0, folders.size());

		folders = dao.findByUserId(99, Folder.ROOTID);
		Assert.assertNotNull(folders);
		Assert.assertEquals(0, folders.size());

		folders = dao.findByUserId(4, Folder.ROOTID);
		Assert.assertNotNull(folders);
		Assert.assertEquals(1, folders.size());
	}

	@Test
	public void testFindByParentId() {
		List<Folder> folders = dao.findByParentId(Folder.ROOTID);
		Assert.assertNotNull(folders);
		Assert.assertEquals(5, folders.size());

		// Try with unexisting parent
		folders = dao.findByParentId(999);
		Assert.assertNotNull(folders);
		Assert.assertEquals(0, folders.size());
	}

	@Test
	public void testIsWriteEnable() {
		Assert.assertTrue(dao.isWriteEnable(Folder.ROOTID, 1));
		Assert.assertTrue(dao.isWriteEnable(6, 1));
		Assert.assertTrue(dao.isWriteEnable(1200, 3));
		Assert.assertTrue(dao.isWriteEnable(Folder.ROOTID, 3));
		Assert.assertFalse(dao.isWriteEnable(Folder.ROOTID, 999));
	}

	@Test
	public void testIsReadEnable() {
		Assert.assertTrue(dao.isReadEnable(Folder.ROOTID, 1));
		Assert.assertTrue(dao.isReadEnable(5, 1));
		Assert.assertFalse(dao.isReadEnable(Folder.ROOTID, 22));
		Assert.assertFalse(dao.isReadEnable(Folder.ROOTID, 999));
		Assert.assertTrue(dao.isReadEnable(1200, 3));
	}

	@Test
	public void testIsPermissionEnabled() {
		Assert.assertTrue(dao.isPermissionEnabled(Permission.WRITE, Folder.ROOTID, 1));
		Assert.assertTrue(dao.isPermissionEnabled(Permission.WRITE, 6, 1));
		Assert.assertFalse(dao.isPermissionEnabled(Permission.WRITE, Folder.ROOTID, 5));
		Assert.assertFalse(dao.isPermissionEnabled(Permission.WRITE, Folder.ROOTID, 999));
		Assert.assertTrue(dao.isPermissionEnabled(Permission.WRITE, 6, 4));
	}

	@Test
	public void testGetEnabledPermissions() {
		Set<Permission> permissions = dao.getEnabledPermissions(Folder.ROOTID, 1);
		Assert.assertEquals(12, permissions.size());
		Assert.assertTrue(permissions.contains(Permission.READ));
		Assert.assertTrue(permissions.contains(Permission.SECURITY));
		Assert.assertTrue(permissions.contains(Permission.SIGN));
		permissions = dao.getEnabledPermissions(6, 4);
		Assert.assertEquals(5, permissions.size());
		Assert.assertTrue(permissions.contains(Permission.READ));
		Assert.assertTrue(permissions.contains(Permission.WRITE));
		permissions = dao.getEnabledPermissions(999, 1);
		Assert.assertEquals(12, permissions.size());
	}

	@Test
	public void testFindFolderIdByUserId() {
		Collection<Long> ids = dao.findFolderIdByUserId(3);
		Assert.assertNotNull(ids);
		Assert.assertEquals(6, ids.size());

		// Try with unexisting user
		ids = dao.findFolderIdByUserId(99);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testFindIdByUserId() {
		Collection<Long> ids = dao.findIdByUserId(1, 1201);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertTrue(ids.contains(1202L));

		ids = dao.findIdByUserId(3, 1200);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());

		ids = dao.findIdByUserId(1, 1201);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());

		// Try with unexisting user
		ids = dao.findIdByUserId(99, 1201);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testHasWriteAccess() {
		Folder folder = dao.findById(1200);
		Assert.assertTrue(dao.hasWriteAccess(folder, 3));
		Assert.assertFalse(dao.hasWriteAccess(folder, 5));
		folder = dao.findById(6);
		Assert.assertTrue(dao.hasWriteAccess(folder, 3));
	}

	@Test
	public void testFindByGroupId() {
		Collection<Folder> folders = dao.findByGroupId(1);
		Assert.assertEquals(6, folders.size());
		folders = dao.findByGroupId(10);
		Assert.assertEquals(0, folders.size());
		folders = dao.findByGroupId(2);
	}

	@Test
	public void testFindParents() {
		List<Folder> folders = dao.findParents(1202);
		Assert.assertEquals(3, folders.size());
		Assert.assertEquals(dao.findById(Folder.ROOTID), folders.get(0));
		Assert.assertEquals(dao.findById(1200), folders.get(1));
		Assert.assertEquals(dao.findById(1201), folders.get(2));
	}

	@Test
	public void testRestore() {
		Folder folder = dao.findById(1204);
		Assert.assertNull(folder);

		dao.restore(1204, true);
		folder = dao.findById(1204);
		Assert.assertNotNull(folder);
	}

	@Test
	public void testFindByNameAndParentId() {
		List<Folder> folders = dao.findByNameAndParentId("%folder%", 5);
		Assert.assertEquals(2, folders.size());
		Assert.assertFalse(folders.contains(dao.findById(99)));
		Assert.assertTrue(folders.contains(dao.findById(7)));
		folders = dao.findByNameAndParentId("ABC", 1200);
		Assert.assertEquals(dao.findById(1201), folders.get(0));
	}

	@Test
	public void testFindFolderIdByUserIdAndPermission() {
		List<Long> ids = dao.findFolderIdByUserIdAndPermission(5, Permission.WRITE);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());

		ids = dao.findFolderIdByUserIdAndPermission(3, Permission.WRITE);
		Assert.assertNotNull(ids);
		Assert.assertEquals(6, ids.size());
	}

	@Test
	public void testComputePathExtended() {
		Assert.assertEquals("/", dao.computePathExtended(5));
		Assert.assertEquals("/test/ABC", dao.computePathExtended(1201));
	}

	@Test
	public void testFindChildren() {
		List<Folder> dirs = dao.findChildren(1200L, 1L);
		Assert.assertNotNull(dirs);
		Assert.assertEquals(1, dirs.size());

		dirs = dao.findChildren(1201L, 3L);
		Assert.assertNotNull(dirs);
		Assert.assertEquals(1, dirs.size());
	}

	@Test
	public void testApplyRightsToTree() {
		History transaction = new History();
		User user = new User();
		user.setId(4);
		transaction.setUser(user);
		transaction.setNotified(0);

		Folder folder = dao.findById(1200);
		Assert.assertNull(folder.getSecurityRef());
		folder.setSecurityRef(5L);
		dao.store(folder);

		Assert.assertTrue(dao.applyRithtToTree(1200, transaction));
		folder = dao.findById(1201);
		Assert.assertEquals(5L, folder.getSecurityRef().longValue());
		folder = dao.findById(1202);
		Assert.assertEquals(5L, folder.getSecurityRef().longValue());
	}
}