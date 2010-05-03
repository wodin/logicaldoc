package com.logicaldoc.webservice.folder;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.document.dao.FolderDAO;
import com.logicaldoc.core.security.Menu;
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
		Menu folderToMove = folderDao.findById(1203);
		Assert.assertNotNull(folderToMove);
		Assert.assertEquals(1201, folderToMove.getParentId());
		Menu parentFolder = folderDao.findById(1200);
		Assert.assertNotNull(parentFolder);

		folderServiceImpl.move("", folderToMove.getId(), parentFolder.getId());
		folderToMove = folderDao.findById(1203);
		Assert.assertEquals(1200, folderToMove.getParentId());
	}

	@Test
	public void testCreate() throws Exception {
		WSFolder wsFolderTest = new WSFolder();
		wsFolderTest.setText("folder test");
		wsFolderTest.setDescription("descr folder test");
		wsFolderTest.setParentId(103);

		WSFolder wsFolder = folderServiceImpl.create("", wsFolderTest);
		Assert.assertNotNull(wsFolder);
		Assert.assertEquals("folder test", wsFolder.getText());
		Assert.assertEquals(103, wsFolder.getParentId());

		Menu createdMenu = folderDao.findByTextAndParentId("folder test", 103).get(0);
		Assert.assertNotNull(createdMenu);
		Assert.assertEquals("folder test", createdMenu.getText());
		Assert.assertEquals("descr folder test", createdMenu.getDescription());
	}

	@Test
	public void testDelete() throws Exception {
		folderServiceImpl.delete("", 1201);
		Menu menu = folderDao.findById(1201);
		Assert.assertNull(menu);
	}

	@Test
	public void testRename() throws Exception {
		Menu menu = folderDao.findById(103);
		Assert.assertNotNull(menu);
		Assert.assertEquals("menu.admin", menu.getText());
		folderDao.initialize(menu);

		folderServiceImpl.rename("", 103, "paperino");

		menu = folderDao.findById(103);
		Assert.assertEquals("paperino", menu.getText());
		Assert.assertEquals(101, menu.getParentId());
		Assert.assertEquals(3, menu.getType());
	}

	@Test
	public void testGetFolder() throws Exception {
		Menu menu = folderDao.findById(103);
		Assert.assertNotNull(menu);

		WSFolder wsFolder = folderServiceImpl.getFolder("", 103);

		Assert.assertEquals(103, wsFolder.getId());
		Assert.assertEquals("menu.admin", wsFolder.getText());
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