package com.logicaldoc.core.document;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.security.User;
import com.logicaldoc.core.security.dao.UserDAO;

/**
 * Test case for <code>DocumentManagerImpl</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.5
 */
public class DocumentManagerImplTest extends AbstractCoreTestCase {

	private DocumentDAO docDao;

	private UserDAO userDao;

	// Instance under test
	private DocumentManager documentManager;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		userDao = (UserDAO) context.getBean("UserDAO");

		// Make sure that this is a DocumentManagerImpl instance
		documentManager = (DocumentManager) context.getBean("DocumentManager");
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
		Assert.assertEquals("pippo", doc.getFileName());
		doc.setImmutable(0);
		docDao.store(doc);
		docDao.findById(doc.getId());
		Assert.assertEquals(1, doc.getImmutable());
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
}