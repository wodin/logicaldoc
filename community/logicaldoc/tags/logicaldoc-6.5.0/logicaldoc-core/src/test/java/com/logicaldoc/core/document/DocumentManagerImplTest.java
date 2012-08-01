package com.logicaldoc.core.document;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.Folder;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.FolderDAO;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * Test case for <code>DocumentManagerImpl</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5
 */
public class DocumentManagerImplTest extends AbstractCoreTCase {

	private DocumentDAO docDao;

	private UserDAO userDao;

	private FolderDAO folderDao;

	// Instance under test
	private DocumentManager documentManager;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		userDao = (UserDAO) context.getBean("UserDAO");
		folderDao = (FolderDAO) context.getBean("FolderDAO");

		// Make sure that this is a DocumentManagerImpl instance
		documentManager = (DocumentManager) context.getBean("DocumentManager");
		documentManager.setStorer(new MockStorer());
	}

	@Test
	public void testUpdate() throws Exception {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Document newDoc = docDao.findById(2);
		Assert.assertNotNull(newDoc);
		docDao.initialize(doc);
		docDao.initialize(newDoc);

		User user = userDao.findByUserName("admin");
		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setUser(user);
		transaction.setDocId(doc.getId());
		transaction.setUserId(1);
		transaction.setNotified(0);
		transaction.setComment("pippo_reason");

		documentManager.update(doc, newDoc, transaction);

		Assert.assertEquals("testDocname2(1)", doc.getTitle());
		Assert.assertEquals("sourceauthor2", doc.getSourceAuthor());
		Assert.assertEquals("sourcetype2", doc.getSourceType());
		Assert.assertEquals("coverage2", doc.getCoverage());
	}

	@Test
	public void testCopyToFolder() throws Exception {
		User user = userDao.findByUserName("admin");
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Folder folder = doc.getFolder();
		Assert.assertEquals(6, folder.getId());

		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setUser(user);
		transaction.setDocId(doc.getId());
		transaction.setUserId(1);
		transaction.setNotified(0);
		transaction.setComment("pippo_reason");
		transaction.setFilename(doc.getFileName());

		Folder newFolder = folderDao.findById(6);
		docDao.initialize(doc);
		Document newDoc = documentManager.copyToFolder(doc, newFolder, transaction);
		Assert.assertNotSame(doc.getId(), newDoc.getId());
		Assert.assertEquals(newFolder, newDoc.getFolder());
	}

	@Test
	public void testMoveToFolder() throws Exception {
		User user = userDao.findByUserName("admin");
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Folder folder = doc.getFolder();
		Assert.assertEquals(6, folder.getId());

		History transaction = new History();
		transaction.setFolderId(6);
		transaction.setUser(user);
		transaction.setDocId(doc.getId());
		transaction.setUserId(1);
		transaction.setNotified(0);
		transaction.setComment("pippo_reason");

		Folder newFolder = folderDao.findById(6);
		docDao.initialize(doc);
		documentManager.moveToFolder(doc, newFolder, transaction);
		Assert.assertSame(1L, doc.getId());
		Assert.assertEquals(newFolder, doc.getFolder());
	}

	@Test
	public void testMakeImmutable() throws Exception {
		User user = userDao.findByUserName("admin");
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setUser(user);
		transaction.setDocId(doc.getId());
		transaction.setUserId(1);
		transaction.setNotified(0);
		transaction.setComment("pippo_reason");
		documentManager.makeImmutable(doc.getId(), transaction);
		doc = docDao.findById(1);
		Assert.assertEquals(1, doc.getImmutable());
		doc.setFileName("ciccio");
		docDao.initialize(doc);
		docDao.store(doc);
	}

	@Test
	public void testLock() throws Exception {
		User user = userDao.findByUserName("admin");
		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setUser(user);
		transaction.setDocId(1L);
		transaction.setUserId(1);
		transaction.setNotified(0);
		documentManager.unlock(1L, transaction);
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		transaction.setComment("pippo_reason");
		documentManager.lock(doc.getId(), 2, transaction);
		doc = docDao.findById(1);
		Assert.assertEquals(2, doc.getStatus());
		Assert.assertEquals(1L, doc.getLockUserId().longValue());
	}

	@Test
	public void testCreate() throws Exception {
		User user = userDao.findByUserName("admin");
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		docDao.initialize(doc);
		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setUser(user);
		transaction.setDocId(doc.getId());
		transaction.setUserId(1);
		transaction.setNotified(0);
		transaction.setComment("pippo_reason");

		Document newDoc = documentManager.create(new FileInputStream("pom.xml"), doc, transaction);

		newDoc = docDao.findById(newDoc.getId());
		Assert.assertEquals(newDoc.getTitle(), doc.getTitle());
		Assert.assertEquals(newDoc.getSourceAuthor(), doc.getSourceAuthor());
		Assert.assertEquals(newDoc.getSourceType(), doc.getSourceType());
		Assert.assertEquals(newDoc.getCoverage(), doc.getCoverage());
	}

	@Test
	public void testCreateShortcut() throws Exception {
		User user = userDao.findByUserName("admin");
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		docDao.initialize(doc);
		History transaction = new History();
		transaction.setFolderId(6);
		transaction.setUser(user);
		transaction.setDocId(doc.getId());
		transaction.setUserId(1);
		transaction.setNotified(0);
		transaction.setComment("pippo_reason");
		Folder newFolder = folderDao.findById(6);

		Document newDoc = documentManager.createShortcut(doc, newFolder, transaction);

		Assert.assertNotSame(doc.getId(), newDoc.getId());
		Assert.assertEquals(newFolder, newDoc.getFolder());
		Assert.assertEquals(newDoc.getTitle(), "testDocname(1)");
		Assert.assertEquals(newDoc.getSourceAuthor(), doc.getSourceAuthor());
		Assert.assertEquals(newDoc.getSourceType(), doc.getSourceType());
		Assert.assertEquals(newDoc.getCoverage(), doc.getCoverage());
	}

	@Test
	public void testCheckin() throws Exception {
		User user = userDao.findByUserName("admin");
		History transaction = new History();
		transaction.setFolderId(103);
		transaction.setUser(user);
		transaction.setDocId(1L);
		transaction.setUserId(1);
		transaction.setNotified(0);
		transaction.setComment("pippo_reason");

		documentManager.checkout(1L, transaction);

		File file = new File("pom.xml");

		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals(AbstractDocument.INDEX_INDEXED, doc.getIndexed());
		docDao.initialize(doc);

		Assert.assertEquals(Document.DOC_CHECKED_OUT, doc.getStatus());

		documentManager.checkin(1L, file, "pippo", true, transaction);

		doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		docDao.initialize(doc);

		Assert.assertEquals(AbstractDocument.INDEX_TO_INDEX, doc.getIndexed());
		Assert.assertEquals(0, doc.getSigned());
		Assert.assertEquals(Document.DOC_UNLOCKED, doc.getStatus());
	}

	@Test
	public void testChangeIndexingStatus() {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals(AbstractDocument.INDEX_INDEXED, doc.getIndexed());
		documentManager.changeIndexingStatus(doc, AbstractDocument.INDEX_SKIP);
		Assert.assertEquals(AbstractDocument.INDEX_SKIP, doc.getIndexed());

		doc = docDao.findById(2);
		Assert.assertNotNull(doc);
		Assert.assertEquals(AbstractDocument.INDEX_TO_INDEX, doc.getIndexed());
		documentManager.changeIndexingStatus(doc, AbstractDocument.INDEX_SKIP);
		Assert.assertEquals(AbstractDocument.INDEX_SKIP, doc.getIndexed());
	}
}