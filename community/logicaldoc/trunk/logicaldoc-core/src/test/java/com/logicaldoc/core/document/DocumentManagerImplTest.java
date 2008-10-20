package com.logicaldoc.core.document;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.dao.DocumentDAO;

/**
 * Test case for <code>DocumentManagerImpl</code>
 * 
 * @author Marco Meschieri
 * @version $Id: LogicalObjects_Code_Templates.xml,v 1.1 2005/01/21 17:56:30
 *          marco Exp $
 * @since 3.5
 */
public class DocumentManagerImplTest extends AbstractCoreTestCase {
	private DocumentDAO docDao;

	// Instance under test
	private DocumentManager documentManager;

	public DocumentManagerImplTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		docDao = (DocumentDAO) context.getBean("DocumentDAO");

		// Make sure that this is a DocumentManagerImpl instance
		documentManager = (DocumentManager) context.getBean("DocumentManager");
	}

	public void testDelete() throws Exception {
		assertNotNull(docDao.findById(1));
		documentManager.delete(1);
		assertNull(docDao.findById(1));
	}
}