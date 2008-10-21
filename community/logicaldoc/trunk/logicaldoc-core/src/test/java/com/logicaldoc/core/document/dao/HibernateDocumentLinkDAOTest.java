package com.logicaldoc.core.document.dao;

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
		link.setType("xxx");
		dao.store(link);
		link = dao.findById(link.getId());
		assertEquals(1, link.getDocument1().getId());
		assertEquals(2, link.getDocument2().getId());
		assertEquals("xxx", link.getType());
	}

	public void testDelete() {
		fail("Not yet implemented");
	}

	public void testFindById() {
		fail("Not yet implemented");
	}

	public void testFindByDocIdLong() {
		fail("Not yet implemented");
	}

	public void testFindByDocIdLongString() {
		fail("Not yet implemented");
	}
}