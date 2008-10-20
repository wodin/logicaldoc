package com.logicaldoc.core.document.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;

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

	public HibernateDocumentDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDocumentDAO
		dao = (DocumentDAO) context.getBean("DocumentDAO");
		menuDao = (MenuDAO) context.getBean("MenuDAO");
	}

	public void testDelete() {
		assertTrue(dao.delete(100));
		Document doc = dao.findById(100);
		assertNull(doc);
	}

	public void testFindAll() {
		Collection<Document> documents = dao.findAll();
		assertNotNull(documents);
		assertEquals(2, documents.size());
	}

	public void testFindById() {
		Document doc = dao.findById(1);
		assertNotNull(doc);
		assertEquals(1, doc.getId());
		assertEquals("testDocname", doc.getTitle());
		assertEquals(2, doc.getVersions().size());
		assertNotNull(doc.getFolder());
		assertEquals(103, doc.getFolder().getId());

		// Try with unexisting document
		doc = dao.findById(99);
		assertNull(doc);
	}

	public void testFindByUserId() {
		Collection<Long> ids = dao.findByUserId(3);
		assertNotNull(ids);
		assertEquals(2, ids.size());
		assertTrue(ids.contains(new Long(2)));

		// Try with a user without documents
		ids = dao.findByUserId(5);
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testFindDocIdByFolder() {
		Collection<Long> ids = dao.findDocIdByFolder(103);
		assertNotNull(ids);
		assertEquals(2, ids.size());
		assertTrue(ids.contains(new Long(2)));

		ids = dao.findDocIdByFolder(1111);
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testFindByFolder() {
		Collection<Document> docs = dao.findByFolder(103);
		assertNotNull(docs);
		assertEquals(2, docs.size());
		assertTrue(docs.contains(dao.findById(2)));

		docs = dao.findByFolder(1111);
		assertNotNull(docs);
		assertEquals(0, docs.size());
	}

	public void testFindLastModifiedByUserId() {
		Collection<Document> coll = dao.findLastModifiedByUserId(4, 10);
		assertNotNull(coll);
		assertEquals(2, coll.size());

		coll = dao.findLastModifiedByUserId(3, 10);
		assertNotNull(coll);
		assertEquals(1, coll.size());
	}

	public void testFindDocIdByKeyword() {
		Collection<Long> ids = dao.findDocIdByKeyword("abc");
		assertNotNull(ids);
		assertEquals(1, ids.size());
		assertEquals(new Long(1), ids.iterator().next());

		ids = dao.findDocIdByKeyword("xxx");
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testStore() {
		Document doc = new Document();
		Menu menu = menuDao.findById(Menu.MENUID_HOME);
		doc.setFolder(menu);
		doc.setPublisher("admin");
		doc.setTitle("test");
		doc.addKeyword("pippo");
		doc.addKeyword("pluto");
		// Try a long keyword
		doc.addKeyword("123456789123456789123456789");
		Version version = new Version();
		version.setVersion("1.0");
		version.setComment("comment");
		doc.addVersion(version);
		assertTrue(dao.store(doc));
		assertEquals(3, doc.getId());
		doc = dao.findById(3);
		assertNotNull(doc);
		assertEquals(3, doc.getId());
		assertEquals(3, doc.getKeywords().size());
		assertTrue(doc.getKeywords().contains("pluto"));
		assertTrue(doc.getKeywords().contains("123456789123456789123456789"));
		assertEquals(1, doc.getVersions().size());
		assertEquals(version, doc.getVersion("1.0"));

		// Try to change the version comment
		doc = dao.findById(3);
		version = doc.getVersion("1.0");
		version.setComment("xxxx");
		version.setVersion("1.0");
		doc.clearVersions();
		doc.addVersion(version);
		dao.store(doc);
		doc = dao.findById(3);
		version = doc.getVersion("1.0");
		assertEquals("xxxx", version.getComment());

		// Load an existing document and modify it
		doc = dao.findById(1);
		assertNotNull(doc);
		assertEquals("testDocname", doc.getTitle());
		assertEquals(2, doc.getVersions().size());
		assertEquals(3, doc.getKeywords().size());
		doc.setTitle("xxxx");
		assertTrue(dao.store(doc));
		doc = dao.findById(1);
		assertNotNull(doc);
		assertEquals(1, doc.getId());
		assertEquals("xxxx", doc.getTitle());
		assertEquals(2, doc.getVersions().size());
		assertEquals(3, doc.getKeywords().size());
	}

	public void testToKeywords() {
		Collection<String> coll = dao.toKeywords("my name is tom");
		assertNotNull(coll);
		assertEquals(2, coll.size());

		coll = dao.toKeywords("il mio nome e' tom");
		assertNotNull(coll);
		assertEquals(3, coll.size());

		coll = dao.toKeywords("il mio nome e' 123456789123456789123456789");
		assertNotNull(coll);
		System.out.println(coll);
		assertEquals(3, coll.size());
		assertFalse(coll.contains("123456789123456789123456789"));
		assertTrue(coll.contains("12345678912345678912"));
	}

	public void testFindKeywords() {
		Collection<String> keywords = dao.findKeywords("a", 1);
		assertNotNull(keywords);
		assertEquals(1, keywords.size());
		assertEquals("abc", keywords.iterator().next());
	}

	public void testFindDocIdByUserIdAndKeyword() {
		Collection<Long> ids = dao.findDocIdByUserIdAndKeyword(1, "abc");
		assertNotNull(ids);
		assertEquals(1, ids.size());
		assertEquals(new Long(1), ids.iterator().next());

		ids = dao.findDocIdByUserIdAndKeyword(1, "xxx");
		assertNotNull(ids);
		assertEquals(0, ids.size());

		ids = dao.findDocIdByUserIdAndKeyword(99, "abc");
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testFindLastDownloadsByUserId() {
		Collection<Document> documents = dao.findLastDownloadsByUserId(1, 10);
		assertNotNull(documents);
		assertEquals(2, documents.size());
	}
}