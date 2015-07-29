package com.logicaldoc.webservice.document;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.Tenant;
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

		// Make sure that this is a DocumentServiceImpl instance
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
		wsDoc.setCustomId("xxxxxxxx");
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
		// NOTA: attenzione errore optimistic lock
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
		WSDocument wsDoc = new WSDocument();
		wsDoc.setId(0L);
		wsDoc.setFolderId(4L);
		wsDoc.setTitle("document test");
		wsDoc.setFileName("document test.txt");
		wsDoc.setCoverage("coverage");
		wsDoc.setCustomId("yyyyyyyy");
		File file = new File("pom.xml");
		wsDoc.setComment("comment");
		docServiceImpl.create("xxxx", wsDoc, new DataHandler(new FileDataSource(file)));

		Document doc = docDao.findByTitleAndParentFolderId(wsDoc.getFolderId(), wsDoc.getTitle(), null).get(0);
		Assert.assertNotNull(doc);
		docDao.initialize(doc);

		Assert.assertEquals("document test", doc.getTitle());
		Assert.assertEquals("coverage", doc.getCoverage());
	}

	@Test
	public void testUpload() throws Exception {
		File file = new File("pom.xml");
		long docId = docServiceImpl.upload("xxxx", null, 4L, true, "document test.txt", new DataHandler(
				new FileDataSource(file)));

		Assert.assertTrue(docId > 0L);
		
		long docId2 = docServiceImpl.upload("xxxx", docId, null, true, "document test.txt", new DataHandler(
				new FileDataSource(file)));
	
		Assert.assertEquals(docId, docId2);
		Assert.assertEquals("2.0", docDao.findById(docId2).getVersion());
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

		// Assert.assertEquals(AbstractDocument.INDEX_TO_INDEX,
		// doc.getIndexed());
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
	public void testRenameFile() throws Exception {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname", doc.getTitle());
		docDao.initialize(doc);
		docServiceImpl.renameFile("", 1, "pippo.doc");
		docDao.initialize(doc);
		Assert.assertEquals("pippo.doc", doc.getFileName());
		Assert.assertEquals("doc", doc.getType());
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
	public void testGetDocumentByCustomId() throws Exception {
		Document doc = docDao.findByCustomId("a", Tenant.DEFAULT_ID);
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
		Assert.assertEquals("testVer02", versionsList.get(0).getVersion());
		Assert.assertEquals("testVer01", versionsList.get(1).getVersion());

		versions = docServiceImpl.getVersions("", 2);
		Assert.assertEquals(0, versions.length);
	}

	@Test
	public void testListDocuments() throws Exception {
		WSDocument[] docs = docServiceImpl.listDocuments("", 103, null);
		Assert.assertNotNull(docs);
		Assert.assertEquals(2, docs.length);
		List<WSDocument> docsList = Arrays.asList(docs);
		Assert.assertEquals(1, docsList.get(0).getId());
		Assert.assertEquals(2, docsList.get(1).getId());

		docs = docServiceImpl.listDocuments("", 103, "plo");
		Assert.assertNotNull(docs);
		Assert.assertEquals(0, docs.length);

		docs = docServiceImpl.listDocuments("", 103, "*ut*");
		Assert.assertNotNull(docs);
		Assert.assertEquals(1, docs.length);
		Assert.assertEquals("pluto", docs[0].getFileName());

		docs = docServiceImpl.listDocuments("", 103, "pippo");
		Assert.assertNotNull(docs);
		Assert.assertEquals(1, docs.length);
		Assert.assertEquals("pippo", docs[0].getFileName());

		docs = docServiceImpl.list("", 99);
		Assert.assertNotNull(docs);
		Assert.assertEquals(0, docs.length);
	}

	@Test
	public void testGetDocuments() throws Exception {
		WSDocument[] docs = docServiceImpl.getDocuments("", new Long[] { 1L, 2L, 3L });
		Assert.assertNotNull(docs);
		Assert.assertEquals(2, docs.length);
	}

	@Test
	public void testGetAliases() throws Exception {
		WSDocument[] docs = docServiceImpl.getAliases("", 1L);
		Assert.assertNotNull(docs);
		Assert.assertEquals(1, docs.length);

		docs = docServiceImpl.getAliases("", 2L);
		Assert.assertNotNull(docs);
		Assert.assertEquals(0, docs.length);
	}
}