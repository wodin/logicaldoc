package com.logicaldoc.core.document;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.DocumentManager;
import com.logicaldoc.core.security.Menu;
import com.logicaldoc.core.security.dao.MenuDAO;

/**
 * Test case for <code>DocumentManagerImpl</code>
 * 
 * @author Marco Meschieri
 * @version $Id: LogicalObjects_Code_Templates.xml,v 1.1 2005/01/21 17:56:30
 *          marco Exp $
 * @since 3.5
 */
public class DocumentManagerImplTest extends AbstractCoreTestCase {
	private MenuDAO menuDAO;

	// Instance under test
	private DocumentManager documentManager;

	public DocumentManagerImplTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		menuDAO = (MenuDAO) context.getBean("MenuDAO");

		// Make sure that this is a DocumentManagerImpl instance
		documentManager = (DocumentManager) context.getBean("DocumentManager");
	}

	public void testDeleteFileMenu() throws Exception {
		assertNotNull(menuDAO.findByPrimaryKey(99));
		documentManager.delete(99, "admin");
		assertNull(menuDAO.findByPrimaryKey(99));
	}

	public void testDeleteDirectoryMenu() throws Exception {
		assertNotNull(menuDAO.findByPrimaryKey(100));
		assertNotNull(menuDAO.findByPrimaryKey(101));
		assertNotNull(menuDAO.findByPrimaryKey(102));
		documentManager.delete(100, "admin");
		assertNull(menuDAO.findByPrimaryKey(100));
		assertNull(menuDAO.findByPrimaryKey(101));
		assertNull(menuDAO.findByPrimaryKey(102));
	}

	public void testCreateDirectories() throws Exception {
		Menu docsMenu = menuDAO.findByPrimaryKey(Menu.MENUID_DOCUMENTS);
		Menu menu=documentManager.createFolders(docsMenu, "/pippo/pluto/paperino");
		assertEquals("paperino",menu.getMenuText());
		menu=menuDAO.findByPrimaryKey(menu.getMenuParent());
		assertEquals("pluto",menu.getMenuText());
		menu=menuDAO.findByPrimaryKey(menu.getMenuParent());
		assertEquals("pippo",menu.getMenuText());
		
		menu=documentManager.createFolders(docsMenu, "/pippo/pluto/paperino");
		assertEquals("paperino",menu.getMenuText());
		menu=menuDAO.findByPrimaryKey(menu.getMenuParent());
		assertEquals("pluto",menu.getMenuText());
		menu=menuDAO.findByPrimaryKey(menu.getMenuParent());
		assertEquals("pippo",menu.getMenuText());
	}
}