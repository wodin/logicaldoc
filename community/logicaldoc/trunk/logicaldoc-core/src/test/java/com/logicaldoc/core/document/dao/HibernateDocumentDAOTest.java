package com.logicaldoc.core.document.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.store.Storer;
import com.logicaldoc.util.io.FileUtil;

/**
 * Test case for <code>HibernateDocumentDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDocumentDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private DocumentDAO dao;

	private MenuDAO menuDao;

	private Storer storer;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDocumentDAO
		dao = (DocumentDAO) context.getBean("DocumentDAO");
		menuDao = (MenuDAO) context.getBean("MenuDAO");
		storer = (Storer) context.getBean("Storer");
	}

	@Test
	public void testDelete() {
		Assert.assertTrue(dao.delete(1));
		Document doc = dao.findById(1);
		Assert.assertNull(doc);
	}

	@Test
	public void testFindAll() {
		Collection<Document> documents = dao.findAll();
		Assert.assertNotNull(documents);
		Assert.assertEquals(2, documents.size());
		
		Assert.assertEquals(2, dao.findByWhere("1=1", null, null).size());
		Assert.assertEquals(1, dao.findByWhere("1=1", null, 1).size());
	}

	@Test
	public void testFindById() {
		Document doc = dao.findById(1);
		Assert.assertNotNull(doc);
		dao.initialize(doc);
		Assert.assertEquals(1, doc.getId());
		Assert.assertEquals("testDocname", doc.getTitle());
		Assert.assertNotNull(doc.getFolder());
		Assert.assertEquals(103, doc.getFolder().getId());

		// Try with unexisting document
		doc = dao.findById(99);
		Assert.assertNull(doc);
	}

	@Test
	public void testFindByCustomId() {
		Document doc = dao.findByCustomId("a");
		Assert.assertNotNull(doc);
		dao.initialize(doc);
		Assert.assertEquals(1, doc.getId());
		Assert.assertEquals("testDocname", doc.getTitle());
		Assert.assertNotNull(doc.getFolder());
		Assert.assertEquals(103, doc.getFolder().getId());

		// Try with unexisting document
		doc = dao.findByCustomId("xx");
		Assert.assertNull(doc);
	}

	@Test
	public void testFindByUserId() {
		Collection<Long> ids = dao.findByUserId(3);
		Assert.assertNotNull(ids);
		Assert.assertEquals(2, ids.size());
		Assert.assertTrue(ids.contains(new Long(2)));

		// Try with a user without documents
		ids = dao.findByUserId(5);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testFindDocIdByFolder() {
		Collection<Long> ids = dao.findDocIdByFolder(103);
		Assert.assertNotNull(ids);
		Assert.assertEquals(2, ids.size());
		Assert.assertTrue(ids.contains(new Long(2)));

		ids = dao.findDocIdByFolder(1111);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testFindByFolder() {
		Collection<Document> docs = dao.findByFolder(103);
		Assert.assertNotNull(docs);
		Assert.assertEquals(2, docs.size());
		Assert.assertTrue(docs.contains(dao.findById(2)));

		docs = dao.findByFolder(1111);
		Assert.assertNotNull(docs);
		Assert.assertEquals(0, docs.size());
	}

	@Test
	public void testFindIndexed() {
		List<Document> docs = dao.findByIndexed(1);
		Assert.assertNotNull(docs);
		Assert.assertEquals(1, docs.size());
		Assert.assertEquals(1, docs.get(0).getId());

		docs = dao.findByIndexed(0);
		Assert.assertNotNull(docs);
		Assert.assertEquals(1, docs.size());
		Assert.assertEquals(2, docs.get(0).getId());
	}

	@Test
	public void testFindLastModifiedByUserId() {
		Collection<Document> coll = dao.findLastModifiedByUserId(1, 10);
		Assert.assertNotNull(coll);
		Assert.assertEquals(2, coll.size());

		coll = dao.findLastModifiedByUserId(3, 10);
		Assert.assertNotNull(coll);
		Assert.assertEquals(0, coll.size());
	}

	@Test
	public void testFindDocIdByTag() {
		Collection<Long> ids = dao.findDocIdByTag("abc");
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertEquals(new Long(1), ids.iterator().next());

		ids = dao.findDocIdByTag("xxx");
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testStore() throws IOException {
		Document doc = new Document();
		Menu menu = menuDao.findById(Menu.MENUID_HOME);
		doc.setFolder(menu);
		doc.setPublisher("admin");
		doc.setPublisherId(1);
		doc.setTitle("test");
		doc.addTag("pippo");
		doc.addTag("pluto");
		doc.setValue("att_1", "val 1");
		doc.setFileName("test.txt");
		doc.setFileVersion("1.0");
		doc.setVersion("1.0");

		// Prepare the document file for digest computation
		File docFile = storer.getFile(7L, doc.getFileVersion());
		FileUtils.forceMkdir(docFile.getParentFile());
		Writer out = new FileWriter(docFile);
		out.write("Questo file serve per fare il test del digest su un documento");
		out.flush();
		out.close();
		Assert.assertTrue(docFile.exists());
		String digest = FileUtil.computeDigest(docFile);
		System.out.println("Saved file " + docFile.getPath());

		Assert.assertEquals("1.0", doc.getFileVersion());
		
		// Try a long tag
		doc.addTag("123456789123456789123456789");
		User user = new User();
		user.setId(1);
		Version version = Version.create(doc, user, "comment", Version.EVENT_CHECKIN, true);

		History transaction = new History();
		transaction.setFolderId(menu.getId());
		transaction.setDocId(doc.getId());
		transaction.setEvent(History.EVENT_STORED);
		transaction.setUserId(1);
		transaction.setNotified(0);

		Assert.assertTrue(docFile.exists());
		Assert.assertEquals("2.0", doc.getFileVersion());
		Assert.assertTrue(dao.store(doc, transaction));

		Assert.assertTrue(docFile.exists());
		Assert.assertEquals(7, doc.getId());
		doc = dao.findById(7);
		Assert.assertNotNull(doc);
		dao.initialize(doc);

		transaction = new History();
		transaction.setFolderId(menu.getId());
		transaction.setDocId(doc.getId());
		transaction.setEvent(History.EVENT_CHANGED);
		transaction.setUserId(1);
		transaction.setNotified(0);

		docFile = storer.getFile(doc, doc.getFileVersion(), null);
		FileUtils.forceMkdir(docFile.getParentFile());
		out = new FileWriter(docFile);
		out.write("Questo file serve per fare il test del digest su un documento");
		out.flush();
		out.close();
		Assert.assertTrue(docFile.exists());
		dao.store(doc, transaction);

		Assert.assertEquals(7, doc.getId());
		Assert.assertEquals(3, doc.getTags().size());
		Assert.assertTrue(doc.getTags().contains("pluto"));
		Assert.assertTrue(doc.getTags().contains("123456789123456789123456789"));
		Assert.assertEquals("val 1", doc.getValue("att_1"));
		Assert.assertTrue(docFile.exists());
		Assert.assertNotNull(doc.getDigest());
		Assert.assertEquals(doc.getDigest(), digest);

		// Try to change the version comment
		doc = dao.findById(7);
		dao.initialize(doc);
		version.setComment("xxxx");
		version.setVersion("1.0");
		version.setUserId(1);
		dao.store(doc);
		doc = dao.findById(7);
		dao.initialize(doc);

		// Load an existing document and modify it
		doc = dao.findById(1);
		Assert.assertNotNull(doc);
		dao.initialize(doc);
		Assert.assertEquals("testDocname", doc.getTitle());
		Assert.assertEquals(3, doc.getTags().size());
		doc.setTitle("xxxx");
		Assert.assertTrue(dao.store(doc));
		doc = dao.findById(1);
		Assert.assertNotNull(doc);
		dao.initialize(doc);
		Assert.assertEquals(1, doc.getId());
		Assert.assertEquals("xxxx", doc.getTitle());
		Assert.assertEquals(3, doc.getTags().size());
	}

	@Test
	public void testFindTags() {
		Collection<String> tags = dao.findTags("a", 1);
		Assert.assertNotNull(tags);
		// There is also the shortcut
		Assert.assertEquals(3, tags.size());
		Assert.assertTrue(tags.contains("abc"));
	}

	@Test
	public void testFindDocIdByUserIdAndTag() {
		Collection<Long> ids = dao.findDocIdByUserIdAndTag(1, "abc");
		Assert.assertNotNull(ids);
		// There is also the shortcut
		Assert.assertEquals(2, ids.size());
		Assert.assertEquals(new Long(1), ids.iterator().next());

		ids = dao.findDocIdByUserIdAndTag(1, "xxx");
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());

		ids = dao.findDocIdByUserIdAndTag(99, "abc");
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}
	
	@Test
	public void testFindByUserIdAndTag() {
		List<Document> ids = dao.findByUserIdAndTag(1, "abc");
		Assert.assertNotNull(ids);
		// There is also the shortcut
		Assert.assertEquals(2, ids.size());
		Assert.assertEquals(1L, ids.get(0).getId());

		ids = dao.findByUserIdAndTag(1, "xxx");
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
		
		ids = dao.findByUserIdAndTag(1, "ask");
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());

		ids = dao.findByUserIdAndTag(99, "abc");
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testFindLastDownloadsByUserId() {
		Collection<Document> documents = dao.findLastDownloadsByUserId(1, 10);
		Assert.assertNotNull(documents);
		Assert.assertEquals(2, documents.size());
	}

	@Test
	public void testFindByTitleAndParentFolderId() {
		Collection<Document> documents = dao.findByTitleAndParentFolderId(103, "testDocname", null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(1, documents.size());
	}

	@Test
	public void testFindByFileNameAndParentFolderId() {
		Collection<Document> documents = dao.findByFileNameAndParentFolderId(103L, "pluto", null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(1, documents.size());

		documents = dao.findByFileNameAndParentFolderId(103L, "PLUTO", null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(1, documents.size());

		documents = dao.findByFileNameAndParentFolderId(103L, "paperino", null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(0, documents.size());

		Document doc = dao.findById(1);
		Assert.assertNotNull(doc);
		dao.initialize(doc);
		doc.setFileName("pluto");
		doc.setFolder(menuDao.findById(99));
		dao.store(doc);
		Assert.assertEquals("pluto", doc.getFileName());
		Assert.assertEquals(99, doc.getFolder().getId());

		documents = dao.findByFileNameAndParentFolderId(103L, "pluto", null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(1, documents.size());

		documents = dao.findByFileNameAndParentFolderId(null, "pluto", null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(2, documents.size());
	}

	@Test
	public void testFindLinkedDocuments() {
		Collection<Document> docs = dao.findLinkedDocuments(1, null, null);
		Assert.assertNotNull(docs);
		Assert.assertEquals(1, docs.size());
		Assert.assertEquals(1, docs.iterator().next().getId());

		docs = dao.findLinkedDocuments(2, "xyz", null);
		Assert.assertNotNull(docs);
		Assert.assertEquals(1, docs.size());
		Assert.assertEquals(2, docs.iterator().next().getId());

		docs = dao.findLinkedDocuments(2, "xyz", 1);
		Assert.assertNotNull(docs);
		Assert.assertEquals(1, docs.size());
		Assert.assertEquals(2, docs.iterator().next().getId());

		docs = dao.findLinkedDocuments(2, "xyz", 2);
		Assert.assertNotNull(docs);
		Assert.assertEquals(0, docs.size());
	}

	@Test
	public void testFindDeletedDocIds() {
		List<Long> coll = dao.findDeletedDocIds();
		Assert.assertNotNull(coll);
		Assert.assertEquals(4, coll.size());
		Assert.assertTrue(coll.contains(new Long(3)));
		Assert.assertTrue(coll.contains(new Long(4)));
	}

	@Test
	public void testFindDeletedDocs() {
		List<Document> coll = dao.findDeletedDocs();
		Assert.assertNotNull(coll);
		Assert.assertEquals(4, coll.size());
	}

	@Test
	public void testGetTotalSize() {
		Assert.assertEquals(613081L, dao.getTotalSize(true));
		Assert.assertEquals(123701L, dao.getTotalSize(false));
	}

	@Test
	public void testCount() {
		Assert.assertEquals(6L, dao.count(true));
		Assert.assertEquals(2L, dao.count(false));
	}

	@Test
	public void testCountByIndexed() {
		Assert.assertEquals(1L, dao.countByIndexed(0));
		Assert.assertEquals(1L, dao.countByIndexed(1));
	}

	@Test
	public void testRestore() {
		Assert.assertNull(dao.findById(4));
		dao.restore(4);
		Assert.assertNotNull(dao.findById(4));
		Assert.assertNotNull(menuDao.findById(1100));
		Assert.assertNotNull(menuDao.findById(1000));
	}

	@Test
	public void testMakeImmutable() {
		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setDocId(2L);
		transaction.setUserId(1);
		transaction.setNotified(0);
		dao.makeImmutable(2, transaction);
		Assert.assertEquals(1, dao.findById(2).getImmutable());
	}

	@Test
	public void testFindLockedByUserId() {
		Assert.assertEquals(1, dao.findLockedByUserId(3).size());
		Assert.assertEquals(0, dao.findLockedByUserId(1).size());
		Assert.assertEquals(0, dao.findLockedByUserId(987541).size());
	}

	@Test
	public void testFindByLockUserAndStatus() {
		Assert.assertEquals(2, dao.findByLockUserAndStatus(3L, null).size());
		Assert.assertEquals(1, dao.findByLockUserAndStatus(3L, Document.DOC_CHECKED_OUT).size());
		Assert.assertEquals(1, dao.findByLockUserAndStatus(null, Document.DOC_CHECKED_OUT).size());
		Assert.assertEquals(0, dao.findByLockUserAndStatus(1L, null).size());
		Assert.assertEquals(0, dao.findByLockUserAndStatus(1L, Document.DOC_CHECKED_OUT).size());
		Assert.assertEquals(0, dao.findByLockUserAndStatus(987541L, null).size());
	}

	@Test
	public void testFindShortcutIds() {
		Collection<Long> ids = dao.findShortcutIds(1);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());
		Assert.assertTrue(ids.contains(new Long(2)));

		ids = dao.findShortcutIds(3);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testFindDeleted() {
		List<Document> deletedDocs = dao.findDeleted(1, 5);
		Assert.assertNotNull(deletedDocs);
		Assert.assertEquals(2, deletedDocs.size());
		Assert.assertEquals("pluto", deletedDocs.get(0).getFileName());
		Assert.assertEquals("DELETED 1", deletedDocs.get(0).getTitle());
		Assert.assertEquals("paperino", deletedDocs.get(1).getFileName());
		Assert.assertEquals("DELETED 3", deletedDocs.get(1).getTitle());

		deletedDocs = dao.findDeleted(2, 4);
		Assert.assertNotNull(deletedDocs);
		// The size is 0 because the document folderid is deleted
		Assert.assertEquals(0, deletedDocs.size());

		deletedDocs = dao.findDeleted(1, 1);
		Assert.assertNotNull(deletedDocs);
		Assert.assertEquals(1, deletedDocs.size());
		Assert.assertEquals("pluto", deletedDocs.get(0).getFileName());
		Assert.assertEquals("DELETED 1", deletedDocs.get(0).getTitle());
	}
}