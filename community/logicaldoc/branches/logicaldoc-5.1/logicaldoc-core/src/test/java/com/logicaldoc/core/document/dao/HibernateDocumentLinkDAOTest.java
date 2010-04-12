package com.logicaldoc.core.document.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;

/**
 * Test case for <code>HibernateDocumentLinkDAO</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 4.0
 */
public class HibernateDocumentLinkDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private DocumentLinkDAO dao;

	private DocumentDAO docDao;

	public HibernateDocumentLinkDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDocumentLinkDAO
		dao = (DocumentLinkDAO) context.getBean("DocumentLinkDAO");

		docDao = (DocumentDAO) context.getBean("DocumentDAO");
	}

	public void testStore() {
		DocumentLink link = new DocumentLink();
		Document doc1 = docDao.findById(1);
		Document doc2 = docDao.findById(2);
		link.setDocument1(doc1);
		link.setDocument2(doc2);
		link.setType("zzz");
		dao.store(link);
		link = dao.findById(link.getId());
		assertEquals(1, link.getDocument1().getId());
		assertEquals(2, link.getDocument2().getId());
		assertEquals("zzz", link.getType());
	}

	public void testDelete() {
		DocumentLink link = dao.findById(1);
		assertNotNull(link);
		assertTrue(dao.delete(1));
		link = dao.findById(1);
		assertNull(link);

		// Try with unexisting link
		dao.delete(99);
		link = dao.findById(1);
		assertNull(link);
		link = dao.findById(2);
		assertNotNull(link);
	}

	public void testFindById() {
		DocumentLink link1 = dao.findById(1);
		assertNotNull(link1);
		assertEquals(1, link1.getDocument1().getId());
		assertEquals(2, link1.getDocument2().getId());
		assertEquals("test", link1.getType());

		DocumentLink link2 = dao.findById(2);
		assertNotNull(link2);
		assertEquals(2, link2.getDocument1().getId());
		assertEquals(1, link2.getDocument2().getId());

		link2 = dao.findById(99);
		assertNull(link2);
	}

	public void testFindByDocId() {
		Collection<DocumentLink> links = dao.findByDocId(1, null);
		assertNotNull(links);
		assertEquals(4, links.size());

		Collection<DocumentLink> links2 = dao.findByDocId(2, null);
		assertNotNull(links2);
		assertEquals(4, links.size());

		Collection<DocumentLink> links3 = dao.findByDocId(99, null);
		assertNotNull(links3);
		assertEquals(0, links3.size());

		links = dao.findByDocId(1, "test");
		assertNotNull(links);
		assertEquals(1, links.size());

		links2 = dao.findByDocId(99, "pippo");
		assertNotNull(links2);
		assertEquals(0, links2.size());
		
		links2 = dao.findByDocId(1, "pippo");
		assertNotNull(links2);
		assertEquals(0, links2.size());
	}
	
	public void testFindByDocIdsAndType() {
		DocumentLink link = dao.findByDocIdsAndType(1, 2, "test");
		assertNotNull(link);
		assertEquals(1, link.getId());

		link = dao.findByDocIdsAndType(2, 1, "xyz");
		assertNotNull(link);
		assertEquals(2, link.getId());
		
		link = dao.findByDocIdsAndType(1, 2, "xxx");
		assertNotNull(link);
		assertEquals(3, link.getId());
		
		link = dao.findByDocIdsAndType(2, 1, "");
		assertNotNull(link);
		assertEquals(4, link.getId());
		
		link = dao.findByDocIdsAndType(2, 1, null);
		assertNull(link);
		
		link = dao.findByDocIdsAndType(1, 2, "zzz");
		assertNull(link);
		
	}
}