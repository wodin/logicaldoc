package com.logicaldoc.core.document.dao;

import com.logicaldoc.core.AbstractCoreTestCase;
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

	
	public HibernateDocumentLinkDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDocumentLinkDAO
		dao = (DocumentLinkDAO) context.getBean("DocumentLinkDAO");
	}

	public void testStore() {
		DocumentLink link = new DocumentLink();
		link.setDocId1(1);
		link.setDocId2(2);
		link.setType("xxx");
		dao.store(link);
		link = dao.findById(link.getId());
		assertEquals(1, link.getDocId1());
		assertEquals(2, link.getDocId2());
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
