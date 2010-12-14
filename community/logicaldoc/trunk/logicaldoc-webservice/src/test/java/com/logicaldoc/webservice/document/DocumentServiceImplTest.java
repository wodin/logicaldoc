package com.logicaldoc.webservice.document;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.webservice.AbstractWebServiceTestCase;

/**
 * Test case for <code>DocumentServiceImpl</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class DocumentServiceImplTest extends AbstractWebServiceTestCase {

	private DocumentDAO docDao;

	private FolderDAO folderDao;

	// Instance under test
	private DocumentServiceImpl docServiceImpl;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		folderDao = (FolderDAO) context.getBean("FolderDAO");

		// Make sure that this is a AuthServiceImpl instance
		docServiceImpl = new DocumentServiceImpl();
		docServiceImpl.setValidateSession(false);
	}

	@Test
	public void testUpdate() throws Exception {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals(103, doc.getFolder().getId());
		Document newDoc = docDao.findById(2);
		Assert.assertNotNull(newDoc);
		Assert.assertEquals(103, newDoc.getFolder().getId());
		docDao.initialize(doc);
		docDao.initialize(newDoc);

		WSDocument wsDoc = WSDocument.fromDocument(newDoc);
		Assert.assertEquals(2, wsDoc.getId());
		wsDoc.setId(1);
		Assert.assertEquals(1, wsDoc.getId());
		Assert.assertEquals("testDocname2", wsDoc.getTitle());
		Assert.assertEquals("sourceauthor2", wsDoc.getSourceAuthor());
		Assert.assertEquals("sourcetype2", wsDoc.getSourceType());
		Assert.assertEquals("coverage2", wsDoc.getCoverage());

		docServiceImpl.update("", wsDoc);

		docDao.initialize(doc);
		Assert.assertEquals("testDocname2(1)", doc.getTitle());
		Assert.assertEquals("sourceauthor2", doc.getSourceAuthor());
		Assert.assertEquals("sourcetype2", doc.getSourceType());
		Assert.assertEquals("coverage2", doc.getCoverage());
	}

	@Test
	public void testMove() throws Exception {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Folder folder = doc.getFolder();
		Assert.assertEquals(103, folder.getId());

		Folder newFolder = folderDao.findById(100);
		docDao.initialize(doc);
		doc.setIndexed(0);
		docDao.store(doc);
		docServiceImpl.move("", doc.getId(), newFolder.getId());
		Assert.assertSame(1L, doc.getId());
		docDao.initialize(doc);
		Assert.assertEquals(newFolder, doc.getFolder());
	}

	@Test
	public void testLock() throws Exception {
		docServiceImpl.unlock("", 1);

		docServiceImpl.lock("", 1);

		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		docDao.initialize(doc);
		Assert.assertEquals(2, doc.getStatus());
		Assert.assertEquals(1L, doc.getLockUserId().longValue());
	}

	@Test
	public void testCreate() throws Exception {
		Document newDoc = docDao.findById(2);
		Assert.assertNotNull(newDoc);
		docDao.initialize(newDoc);

		Assert.assertNull(docDao.findById(50));
		WSDocument wsDoc = WSDocument.fromDocument(newDoc);
		Assert.assertEquals(2, wsDoc.getId());
		wsDoc.setId(50);
		wsDoc.setTitle("document test");
		File file = new File("pom.xml");
		docServiceImpl.create("", wsDoc, new DataHandler(new FileDataSource(file)));

		Document doc = docDao.findByTitleAndParentFolderId(wsDoc.getFolderId(), wsDoc.getTitle(), null).get(0);
		Assert.assertNotNull(doc);
		docDao.initialize(doc);

		Assert.assertEquals("document test", doc.getTitle());
		Assert.assertEquals("sourceauthor2", doc.getSourceAuthor());
		Assert.assertEquals("sourcetype2", doc.getSourceType());
		Assert.assertEquals("coverage2", doc.getCoverage());
	}

	@Test
	public void testCheckin() throws Exception {
		docServiceImpl.checkout("", 1);

		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		docDao.initialize(doc);
		Assert.assertEquals(Document.DOC_CHECKED_OUT, doc.getStatus());

		File file = new File("pom.xml");
		docServiceImpl.checkin("", 1, "comment", "pom.xml", true, new DataHandler(new FileDataSource(file)));

		doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		docDao.initialize(doc);

		Assert.assertEquals(AbstractDocument.INDEX_TO_INDEX, doc.getIndexed());
		Assert.assertEquals(0, doc.getSigned());
		Assert.assertEquals(Document.DOC_UNLOCKED, doc.getStatus());
	}

	@Test
	public void testDelete() throws Exception {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		docServiceImpl.delete("", doc.getId());
		doc = docDao.findById(1);
		Assert.assertNull(doc);
	}

	@Test
	public void testRename() throws Exception {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname", doc.getTitle());
		docDao.initialize(doc);

		docServiceImpl.rename("", 1, "pippo");

		docDao.initialize(doc);
		Assert.assertEquals("pippo", doc.getTitle());
		Assert.assertEquals("sourceauthor1", doc.getSourceAuthor());
		Assert.assertEquals("sourcetype1", doc.getSourceType());
		Assert.assertEquals("coverage1", doc.getCoverage());
	}

	@Test
	public void testGetDocument() throws Exception {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);

		WSDocument wsDoc = docServiceImpl.getDocument("", 1);

		Assert.assertEquals(1, wsDoc.getId());
		Assert.assertEquals("testDocname", wsDoc.getTitle());
		Assert.assertEquals(103, wsDoc.getFolderId().longValue());
	}

	@Test
	public void testIsReadable() throws Exception {
		Assert.assertTrue(docServiceImpl.isReadable("", 1));
		Assert.assertFalse(docServiceImpl.isReadable("", 99));
	}

	@Test
	public void testRestore() throws Exception {
		Assert.assertNull(docDao.findById(4));
		docServiceImpl.restore("", 4, 5);
		Assert.assertNotNull(docDao.findById(4));
	}

	@Test
	public void testGetVersions() throws Exception {
		WSDocument[] versions = docServiceImpl.getVersions("", 1);
		Assert.assertEquals(2, versions.length);
		List<WSDocument> versionsList = Arrays.asList(versions);
		Assert.assertEquals("testVersion2", versionsList.get(0).getVersion());
		Assert.assertEquals("testVersion", versionsList.get(1).getVersion());

		versions = docServiceImpl.getVersions("", 2);
		Assert.assertEquals(0, versions.length);
	}

	@Test
	public void testList() throws Exception {
		WSDocument[] docs = docServiceImpl.list("", 103);
		Assert.assertNotNull(docs);
		Assert.assertEquals(2, docs.length);
		List<WSDocument> docsList = Arrays.asList(docs);
		Assert.assertEquals(1, docsList.get(0).getId());
		Assert.assertEquals(2, docsList.get(1).getId());

		docs = docServiceImpl.list("", 99);
		Assert.assertNotNull(docs);
		Assert.assertEquals(0, docs.length);
	}
	
	@Test
	public void testGetDocuments() throws Exception {
		WSDocument[] docs = docServiceImpl.getDocuments("", new Long[]{1L,2L,3L});
		Assert.assertNotNull(docs);
		Assert.assertEquals(2, docs.length);
	}
}