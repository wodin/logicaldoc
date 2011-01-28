package com.logicaldoc.webservice.folder;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.webservice.AbstractWebServiceTestCase;

/**
 * Test case for <code>FolderServiceImpl</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class FolderServiceImplTest extends AbstractWebServiceTestCase {

	private FolderDAO folderDao;

	// Instance under test
	private FolderServiceImpl folderServiceImpl;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		folderDao = (FolderDAO) context.getBean("FolderDAO");

		// Make sure that this is a AuthServiceImpl instance
		folderServiceImpl = new FolderServiceImpl();
		folderServiceImpl.setValidateSession(false);
	}

	@Test
	public void testMove() throws Exception {
		Folder folderToMove = folderDao.findById(1203);
		Assert.assertNotNull(folderToMove);
		Assert.assertEquals(1201, folderToMove.getParentId());
		Folder parentFolder = folderDao.findById(1200);
		Assert.assertNotNull(parentFolder);

		folderServiceImpl.move("", folderToMove.getId(), parentFolder.getId());
		folderToMove = folderDao.findById(1203);
		Assert.assertEquals(1200, folderToMove.getParentId());
	}

	@Test
	public void testCreate() throws Exception {
		WSFolder wsFolderTest = new WSFolder();
		wsFolderTest.setName("folder test");
		wsFolderTest.setDescription("descr folder test");
		wsFolderTest.setParentId(103);

		WSFolder wsFolder = folderServiceImpl.create("", wsFolderTest);
		Assert.assertNotNull(wsFolder);
		Assert.assertEquals("folder test", wsFolder.getName());
		Assert.assertEquals(103, wsFolder.getParentId());

		Folder createdFolder = folderDao.findByNameAndParentId("folder test", 103).get(0);
		Assert.assertNotNull(createdFolder);
		Assert.assertEquals("folder test", createdFolder.getName());
		Assert.assertEquals("descr folder test", createdFolder.getDescription());
	}

	@Test
	public void testDelete() throws Exception {
		folderServiceImpl.delete("", 1201);
		Folder folder = folderDao.findById(1201);
		Assert.assertNull(folder);
	}

	@Test
	public void testRename() throws Exception {
		Folder folder = folderDao.findById(103);
		Assert.assertNotNull(folder);
		Assert.assertEquals("menu.admin", folder.getName());
		folderDao.initialize(folder);

		folderServiceImpl.rename("", 103, "paperino");

		folder = folderDao.findById(103);
		Assert.assertEquals("paperino", folder.getName());
		Assert.assertEquals(101, folder.getParentId());
		Assert.assertEquals(3, folder.getType());
	}

	@Test
	public void testGetFolder() throws Exception {
		Folder folder = folderDao.findById(103);
		Assert.assertNotNull(folder);

		WSFolder wsFolder = folderServiceImpl.getFolder("", 103);

		Assert.assertEquals(103, wsFolder.getId());
		Assert.assertEquals("menu.admin", wsFolder.getName());
		Assert.assertEquals(101, wsFolder.getParentId());
		Assert.assertEquals("description", wsFolder.getDescription());
	}

	@Test
	public void testIsReadable() throws Exception {
		Assert.assertTrue(folderServiceImpl.isReadable("", 1200));
		Assert.assertTrue(folderServiceImpl.isReadable("", 99));
	}

	@Test
	public void testList() throws Exception {
		WSFolder[] folders = folderServiceImpl.list("", 1201);
		Assert.assertNotNull(folders);
		Assert.assertEquals(2, folders.length);
		List<WSFolder> foldersList = Arrays.asList(folders);
		Assert.assertEquals(1202, foldersList.get(0).getId());
		Assert.assertEquals(1203, foldersList.get(1).getId());

		folders = folderServiceImpl.list("", 1203);
		Assert.assertNotNull(folders);
		Assert.assertEquals(0, folders.length);
	}
}