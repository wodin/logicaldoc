package com.logicaldoc.core.document.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.Version;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.TermDAO;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;

/**
 * Test case for <code>HibernateDocumentDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
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
		Document doc=dao.findByPrimaryKey(100);
		assertNull(doc);
	}

	public void testDeleteByMenuId() {
		TermDAO termDAO=(TermDAO)context.getBean("TermDAO");
		termDAO.delete(103);
        assertEquals(0,termDAO.findByMenuId(103).size());
        termDAO.delete(1);
		assertTrue(dao.deleteByMenuId(103));
		Document doc=dao.findByPrimaryKey(1);
		assertNull(doc);
	}

	public void testFindAll() {
		Collection<Document> documents = dao.findAll();
		assertNotNull(documents);
		assertEquals(2, documents.size());
	}

	public void testFindByMenuId() {
		Document doc=dao.findByMenuId(103);
		assertNotNull(doc);
		assertEquals(1,doc.getDocId());
		assertEquals(103,doc.getMenuId());
		
		//Try with unexisting menu
		doc=dao.findByMenuId(1000);
		assertNull(doc);
	}

	public void testFindByPrimaryKey() {
		Document doc = dao.findByPrimaryKey(1);
		assertNotNull(doc);
		assertEquals(1, doc.getDocId());
		assertEquals("testDocname", doc.getDocName());
		assertEquals(2, doc.getVersions().size());
		assertNotNull(doc.getMenu());
		assertEquals(103, doc.getMenu().getMenuId());

		// Try with unexisting document
		doc = dao.findByPrimaryKey(99);
		assertNull(doc);
	}

	public void testFindByUserName() {
		Collection<Integer> ids = dao.findByUserName("sebastian");
		assertNotNull(ids);
		assertEquals(2, ids.size());
		assertTrue(ids.contains(new Integer(2)));

		// Try with a user without documents
		ids = dao.findByUserName("test");
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}
	
	
	public void testFindLastModifiedByUserName() {
		Collection<Document> coll = dao.findLastModifiedByUserName("author", 10);
		assertNotNull(coll);
		assertEquals(2, coll.size());
		
		coll = dao.findLastModifiedByUserName("sebastian", 10);
		assertNotNull(coll);
		assertEquals(0, coll.size());
	}

	public void testFindMenuIdByKeyword() {
		Collection<Integer> ids=dao.findMenuIdByKeyword("abc");
		assertNotNull(ids);
		assertEquals(1, ids.size());
		assertEquals(new Integer(103), ids.iterator().next());
		
		ids=dao.findMenuIdByKeyword("xxx");
		assertNotNull(ids);
		assertEquals(0, ids.size());
	}

	public void testStore() {
		Document doc=new Document();
		Menu menu = menuDao.findByPrimaryKey(Menu.MENUID_HOME);
		doc.setMenu(menu);
		doc.setDocPublisher("admin");
		doc.setDocName("test");
		doc.addKeyword("pippo");
		doc.addKeyword("pluto");
		//Try a long keyword
		doc.addKeyword("123456789123456789123456789");
		Version version = new Version();
		version.setVersion("1.0");
		version.setVersionComment("comment");
		doc.addVersion(version);
		assertTrue(dao.store(doc));
		assertEquals(3, doc.getDocId());
		doc = dao.findByPrimaryKey(3);
		assertNotNull(doc);
		assertEquals(3, doc.getDocId());
		assertEquals(3, doc.getKeywords().size());
		assertTrue(doc.getKeywords().contains("pluto"));
		assertFalse(doc.getKeywords().contains("123456789123456789123456789"));
		assertTrue(doc.getKeywords().contains("12345678912345678912"));
		assertEquals(1, doc.getVersions().size());
		assertEquals(version, doc.getVersion("1.0"));
		
        //Try to change the version comment
        doc = dao.findByPrimaryKey(3);
        version=doc.getVersion("1.0");
        version.setVersionComment("xxxx");
        dao.store(doc);
        doc = dao.findByPrimaryKey(3);
        version=doc.getVersion("1.0");
        assertEquals("xxxx", version.getVersionComment());
        
		// Load an existing document and modify it
		doc = dao.findByPrimaryKey(1);
		assertNotNull(doc);
		assertEquals("testDocname", doc.getDocName());
		assertEquals(2, doc.getVersions().size());
		assertEquals(3, doc.getKeywords().size());
		doc.setDocName("xxxx");
		assertTrue(dao.store(doc));
		doc = dao.findByPrimaryKey(1);
		assertNotNull(doc);
		assertEquals(1, doc.getDocId());
		assertEquals("xxxx", doc.getDocName());
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
	
	public void testFindKeywords(){
		Collection<String> keywords = dao.findKeywords("a", "admin");
		assertNotNull(keywords);
		assertEquals(1, keywords.size());
		assertEquals("abc", keywords.iterator().next());
	}
}