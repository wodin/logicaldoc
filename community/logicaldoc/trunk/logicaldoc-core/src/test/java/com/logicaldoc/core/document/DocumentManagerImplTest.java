package com.logicaldoc.core.document;

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

	public DocumentManagerImplTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		userDao = (UserDAO) context.getBean("UserDAO");

		// Make sure that this is a DocumentManagerImpl instance
		documentManager = (DocumentManager) context.getBean("DocumentManager");
	}

	public void testDelete() throws Exception {
		assertNotNull(docDao.findById(1));
		documentManager.delete(1);
		assertNull(docDao.findById(1));
	}

	public void testMakeImmutable() throws Exception {
		User user = userDao.findByUserName("admin");
		Document doc = docDao.findById(1);
		String reason = "pippo_reason";
		assertNotNull(doc);
		documentManager.makeImmutable(doc.getId(), user, reason);
		doc = docDao.findById(1);
		assertEquals(1, doc.getImmutable());
		doc.setFileName("ciccio");
		docDao.initialize(doc);
		docDao.store(doc);
		assertEquals("pippo", doc.getFileName());		
		doc.setImmutable(0);
		docDao.store(doc);
		docDao.findById(doc.getId());
		assertEquals(1, doc.getImmutable());
	}
}