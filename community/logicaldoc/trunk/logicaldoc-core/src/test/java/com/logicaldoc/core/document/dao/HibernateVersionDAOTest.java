package com.logicaldoc.core.document.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

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

	private DocumentDAO docDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateVersionDAO
		dao = (VersionDAO) context.getBean("VersionDAO");
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
	}

	@Test public void testFindByDocumentId() {
		List<Version> versions = dao.findByDocId(1);
		Assert.assertEquals(2, versions.size());
		Assert.assertTrue(versions.contains(dao.findByVersion(1, "testVersion")));
		Assert.assertTrue(versions.contains(dao.findByVersion(1, "testVersion2")));

		versions = dao.findByDocId(2);
		Assert.assertEquals(0, versions.size());

		versions = dao.findByDocId(99);
		Assert.assertEquals(0, versions.size());
	}

	@Test public void testFindByVersion() {
		Version version = dao.findByVersion(1, "testVersion2");
		Assert.assertNotNull(version);
		Assert.assertEquals("testVersion2", version.getVersion());

		version = dao.findByVersion(1, "xxxx");
		Assert.assertNull(version);
	}

	@Test public void testStore() {
		Version version = new Version();
		version.setId(3);
		version.setDeleted(0);
		version.setLastModified(null);
		version.setComment("pippo");
		version.setUserId(1);
		version.setUsername("matteo");
		version.setDocument(docDao.findById(1));
		Assert.assertTrue(dao.store(version));
		Assert.assertNotNull(dao.findById(2));
		Assert.assertNull(dao.findById(1));
	}
}