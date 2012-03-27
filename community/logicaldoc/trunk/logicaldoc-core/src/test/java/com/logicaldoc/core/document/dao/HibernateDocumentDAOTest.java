package com.logicaldoc.core.document.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;
import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;

/**
 * Test case for <code>HibernateDocumentDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDocumentDAOTest extends AbstractCoreTCase {

	// Instance under test
	private DocumentDAO dao;

	private FolderDAO folderDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDocumentDAO
		dao = (DocumentDAO) context.getBean("DocumentDAO");
		folderDao = (FolderDAO) context.getBean("FolderDAO");
	}

	@Test
	public void testDelete() {
		// Create the document history event
		History transaction = new History();
		transaction.setSessionId("123");
		transaction.setEvent(History.EVENT_DELETED);
		transaction.setComment("");
		transaction.setUser(new User());

		Assert.assertTrue(dao.delete(1, transaction));
		Document doc = dao.findById(1);
		Assert.assertNull(doc);
	}

	@Test
	public void testFindAll() {
		Collection<Document> documents = dao.findAll();
		Assert.assertNotNull(documents);
		Assert.assertEquals(3, documents.size());

		Assert.assertEquals(3, dao.findByWhere("1=1", null, null).size());
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
		Assert.assertEquals(6, doc.getFolder().getId());

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
		Assert.assertEquals(6, doc.getFolder().getId());

		// Try with unexisting document
		doc = dao.findByCustomId("xx");
		Assert.assertNull(doc);
	}

	@Test
	public void testFindByUserId() {
		Collection<Long> ids = dao.findByUserId(3);
		Assert.assertNotNull(ids);
		Assert.assertEquals(3, ids.size());
		Assert.assertTrue(ids.contains(new Long(2)));

		// Try with a user without documents
		ids = dao.findByUserId(2);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testFindDocIdByFolder() {
		Collection<Long> ids = dao.findDocIdByFolder(6, null);
		Assert.assertNotNull(ids);
		Assert.assertEquals(3, ids.size());
		Assert.assertTrue(ids.contains(new Long(2)));

		ids = dao.findDocIdByFolder(1111, null);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());
	}

	@Test
	public void testFindByFolder() {
		Collection<Document> docs = dao.findByFolder(6, null);
		Assert.assertNotNull(docs);
		Assert.assertEquals(3, docs.size());
		Assert.assertTrue(docs.contains(dao.findById(2)));

		docs = dao.findByFolder(1111, null);
		Assert.assertNotNull(docs);
		Assert.assertEquals(0, docs.size());
	}

	@Test
	public void testFindIndexed() {
		List<Document> docs = dao.findByIndexed(1);
		Assert.assertNotNull(docs);
		Assert.assertEquals(2, docs.size());
		Assert.assertEquals(1, docs.get(0).getId());

		docs = dao.findByIndexed(0);
		Assert.assertNotNull(docs);
		// The document with is 2 has a docRef not null
		Assert.assertEquals(0, docs.size());
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
	public void testDeleteOrphaned() {
		folderDao.delete(6);
		Assert.assertNull(folderDao.findById(6));
		dao.deleteOrphaned(1);
		Document doc = dao.findById(1);
		Assert.assertTrue(doc == null || doc.getDeleted() == 1);
	}

	@Test
	public void testFindPublishedIds() throws IOException {
		GregorianCalendar cal = new GregorianCalendar();
		
		Document doc = new Document();
		Folder folder = folderDao.findById(Folder.ROOTID);
		doc.setFolder(folder);
		doc.setFileVersion("1.0");
		doc.setVersion("1.0");
		doc.setPublisher("admin");
		doc.setPublisherId(1);
		doc.setTitle("test");
		doc.setFileName("test.txt");
		
		// Prepare the document file for digest computation
		File docFile = new File("target");
		docFile = new File(docFile, "store");
		docFile = new File(docFile, doc.getFileVersion());
		FileUtils.forceMkdir(docFile.getParentFile());
		Writer out = new FileWriter(docFile);
		out.write("Questo file serve per fare il test del digest su un documento");
		out.flush();
		out.close();
		Assert.assertTrue(docFile.exists());
		System.out.println("Saved file " + docFile.getPath());
		
		dao.store(doc);

		Set<Long> fids = new HashSet<Long>();
		fids.add(Folder.ROOTID);

		Collection<Long> ids = dao.findPublishedIds(fids);
		System.out.println("*ids="+ids);
		Assert.assertTrue(ids.contains(doc.getId()));
		
		doc.setPublished(0);
		dao.store(doc);
		ids = dao.findPublishedIds(fids);
		Assert.assertFalse(ids.contains(doc.getId()));
		
		cal.add(Calendar.DATE, 1);
		Date pick=cal.getTime();
		doc.setPublished(1);
		doc.setStartPublishing(pick);
		dao.store(doc);
		ids = dao.findPublishedIds(fids);
		Assert.assertFalse(ids.contains(doc.getId()));
		
		cal.add(Calendar.DATE, -3);
		pick=cal.getTime();
		doc.setStartPublishing(pick);
		dao.store(doc);
		ids = dao.findPublishedIds(fids);
		Assert.assertTrue(ids.contains(doc.getId()));
		
		cal.add(Calendar.DATE, 1);
		pick=cal.getTime();
		doc.setStopPublishing(pick);
		dao.store(doc);
		ids = dao.findPublishedIds(fids);
		Assert.assertFalse(ids.contains(doc.getId()));
	}

	@Test
	public void testStore() throws IOException {
		Document doc = new Document();
		Folder folder = folderDao.findById(Folder.ROOTID);
//		doc.setFolder(folder);
//		doc.setPublisher("admin");
//		doc.setPublisherId(1);
//		doc.setTitle("test");
//		doc.addTag("pippo");
//		doc.addTag("pluto");
//		doc.setValue("att_1", "val 1");
//		doc.setFileName("test.txt");
//		doc.setFileVersion("1.0");
//		doc.setVersion("1.0");
//
//		// Prepare the document file for digest computation
//		File docFile = new File("target");
//		docFile = new File(docFile, "store");
//		docFile = new File(docFile, doc.getFileVersion());
//		FileUtils.forceMkdir(docFile.getParentFile());
//		Writer out = new FileWriter(docFile);
//		out.write("Questo file serve per fare il test del digest su un documento");
//		out.flush();
//		out.close();
//		Assert.assertTrue(docFile.exists());
//		System.out.println("Saved file " + docFile.getPath());
//
//		Assert.assertEquals("1.0", doc.getFileVersion());
//
//		// Try a long tag
//		doc.addTag("123456789123456789123456789");
//		User user = new User();
//		user.setId(1);
//		Version version = Version.create(doc, user, "comment", Version.EVENT_CHECKIN, true);
//
//		History transaction = new History();
//		transaction.setFolderId(folder.getId());
//		transaction.setDocId(doc.getId());
//		transaction.setEvent(History.EVENT_STORED);
//		transaction.setUserId(1);
//		transaction.setNotified(0);
//
//		Assert.assertTrue(docFile.exists());
//		Assert.assertEquals("2.0", doc.getFileVersion());
//		Assert.assertTrue(dao.store(doc, transaction));
//
//		Assert.assertTrue(docFile.exists());
//		Assert.assertEquals(7, doc.getId());
//		doc = dao.findById(7);
//		Assert.assertNotNull(doc);
//		dao.initialize(doc);
//
//		transaction = new History();
//		transaction.setFolderId(folder.getId());
//		transaction.setDocId(doc.getId());
//		transaction.setEvent(History.EVENT_CHANGED);
//		transaction.setUserId(1);
//		transaction.setNotified(0);
//
//		// Try to change the version comment
//		doc = dao.findById(7);
//		dao.initialize(doc);
//		version.setComment("xxxx");
//		version.setVersion("1.0");
//		version.setUserId(1);
//		dao.store(doc);
//		doc = dao.findById(7);
//		dao.initialize(doc);


		
		
		
		
		doc = dao.findById(1);
		Assert.assertNotNull(doc);
		dao.initialize(doc);
		
		// Try to store it inside a folder with extended attributes
		folder=folderDao.findById(1202);
		doc.setFolder(folder);
		dao.store(doc);
		
		//Check if the defaults were applied
		doc=dao.findById(1);
		dao.initialize(doc);
		Assert.assertEquals(1, doc.getTemplate().getId());
		Assert.assertEquals("test_val_1", doc.getValue("val1"));
	}

	@Test
	public void testFindTags() {
		Collection<String> tags = dao.findTags("a").keySet();
		Assert.assertNotNull(tags);
		Assert.assertEquals(2, tags.size());
		Assert.assertTrue(tags.contains("abc"));
	}

	@Test
	public void testFindAllTags() {
		Collection<String> tags = dao.findAllTags("a");
		Assert.assertNotNull(tags);
		Assert.assertEquals(2, tags.size());
		Assert.assertTrue(tags.contains("abc"));
		tags = dao.findAllTags(null);
		Assert.assertNotNull(tags);
		Assert.assertEquals(5, tags.size());
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
		List<Document> ids = dao.findByUserIdAndTag(1, "abc", null);
		Assert.assertNotNull(ids);
		// There is also the shortcut
		Assert.assertEquals(2, ids.size());
		Assert.assertEquals(1L, ids.get(0).getId());

		ids = dao.findByUserIdAndTag(4, "zzz", null);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());

		ids = dao.findByUserIdAndTag(1, "xxx", null);
		Assert.assertNotNull(ids);
		Assert.assertEquals(0, ids.size());

		ids = dao.findByUserIdAndTag(1, "ask", null);
		Assert.assertNotNull(ids);
		Assert.assertEquals(1, ids.size());

		ids = dao.findByUserIdAndTag(99, "abc", null);
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
		Collection<Document> documents = dao.findByTitleAndParentFolderId(6, "testDocname", null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(1, documents.size());
	}

	@Test
	public void testFindByFileNameAndParentFolderId() {
		Collection<Document> documents = dao.findByFileNameAndParentFolderId(6L, "pluto", null, null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(2, documents.size());

		documents = dao.findByFileNameAndParentFolderId(6L, "PLUTO", null, null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(2, documents.size());

		documents = dao.findByFileNameAndParentFolderId(6L, "paperino", null, null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(0, documents.size());

		Document doc = dao.findById(1);
		Assert.assertNotNull(doc);
		dao.initialize(doc);
		doc.setFileName("pluto");
		doc.setFolder(folderDao.findById(7));
		dao.store(doc);
		Assert.assertEquals("pluto", doc.getFileName());
		Assert.assertEquals(7, doc.getFolder().getId());

		documents = dao.findByFileNameAndParentFolderId(6L, "pluto", null, null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(2, documents.size());

		documents = dao.findByFileNameAndParentFolderId(null, "pluto", null, null);
		Assert.assertNotNull(documents);
		Assert.assertEquals(3, documents.size());
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
		Assert.assertEquals(3, coll.size());
		Assert.assertTrue(coll.contains(new Long(4)));
	}

	@Test
	public void testFindDeletedDocs() {
		List<Document> coll = dao.findDeletedDocs();
		Assert.assertNotNull(coll);
		Assert.assertEquals(3, coll.size());
	}

	@Test
	public void testCount() {
		dao.initialize(null);
		Assert.assertEquals(6L, dao.count(true));
		Assert.assertEquals(3L, dao.count(false));
	}

	@Test
	public void testCountByIndexed() {
		Assert.assertEquals(1L, dao.countByIndexed(0));
		Assert.assertEquals(2L, dao.countByIndexed(1));
	}

	@Test
	public void testRestore() {
		Assert.assertNull(dao.findById(4));
		dao.restore(4, 5);
		Assert.assertNotNull(dao.findById(4));
		Assert.assertEquals(5L, dao.findById(4).getFolder().getId());
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
		Assert.assertEquals(2, dao.findLockedByUserId(3).size());
		Assert.assertEquals(0, dao.findLockedByUserId(1).size());
		Assert.assertEquals(0, dao.findLockedByUserId(987541).size());
	}

	@Test
	public void testFindByLockUserAndStatus() {
		Assert.assertEquals(3, dao.findByLockUserAndStatus(3L, null).size());
		Assert.assertEquals(2, dao.findByLockUserAndStatus(3L, Document.DOC_CHECKED_OUT).size());
		Assert.assertEquals(2, dao.findByLockUserAndStatus(null, Document.DOC_CHECKED_OUT).size());
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
		Assert.assertEquals(1, deletedDocs.size());
		Assert.assertEquals("pippo", deletedDocs.get(0).getFileName());
		Assert.assertEquals("DELETED 2", deletedDocs.get(0).getTitle());

		deletedDocs = dao.findDeleted(2, 4);
		Assert.assertNotNull(deletedDocs);
		Assert.assertEquals(2, deletedDocs.size());

		deletedDocs = dao.findDeleted(1, 1);
		Assert.assertNotNull(deletedDocs);
		Assert.assertEquals(1, deletedDocs.size());
		Assert.assertEquals("pippo", deletedDocs.get(0).getFileName());
	}

	@Test
	public void testFindByIds() {
		List<Document> docs = dao.findByIds(new Long[0], 5);
		Assert.assertNotNull(docs);
		Assert.assertTrue(docs.isEmpty());

		docs = dao.findByIds(new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L }, null);
		Assert.assertNotNull(docs);
		Assert.assertEquals(3, docs.size());
	}
}