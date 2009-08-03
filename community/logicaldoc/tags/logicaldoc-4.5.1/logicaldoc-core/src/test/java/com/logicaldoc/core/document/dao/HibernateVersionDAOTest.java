package com.logicaldoc.core.document.dao;

import java.util.List;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.Version;

/**
 * Test case for <code>HibernateVersionDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class HibernateVersionDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private VersionDAO dao;

	public HibernateVersionDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateVersionDAO
		dao = (VersionDAO) context.getBean("VersionDAO");
	}

	public void testFindByDocumentId() {
		List<Version> versions = dao.findByDocId(1);
		assertEquals(2, versions.size());
		assertTrue(versions.contains(dao.findByVersion(1, "testVersion")));
		assertTrue(versions.contains(dao.findByVersion(1, "testVersion2")));

		versions = dao.findByDocId(2);
		assertEquals(0, versions.size());

		versions = dao.findByDocId(99);
		assertEquals(0, versions.size());
	}

	public void testFindByVersion() {
		Version version = dao.findByVersion(1, "testVersion2");
		assertNotNull(version);
		assertEquals("testVersion2", version.getVersion());

		version = dao.findByVersion(1, "xxxx");
		assertNull(version);
	}
}