package com.logicaldoc.email.dao;

import java.util.Collection;

import com.logicaldoc.core.security.dao.MenuDAO;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.email.AbstractEmailTestCase;
import com.logicaldoc.email.EmailAccount;

public class HibernateEmailAccountDAOTest extends AbstractEmailTestCase {

	// Instance under test
	private EmailAccountDAO dao;

	private MenuDAO menuDao;

	private UserDAO userDao;

	public HibernateEmailAccountDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context.
		// Make sure that it is an HibernateEmailAccountDAO
		dao = (EmailAccountDAO) context.getBean("EmailAccountDAO");
		menuDao = (MenuDAO) context.getBean("MenuDAO");
		userDao = (UserDAO) context.getBean("UserDAO");
	}

	public void testStore() {
		EmailAccount account = new EmailAccount();
		account.setMailAddress("author@logicaldoc.sf.net");
		account.setAllowedTypes("pippo");
		account.setProvider("Aruba");
		account.setHost("pcalle");
		account.setUserName("author@logicaldoc.sf.net");
		account.setPassword("fghfgh");
		account.setEnabled(1);
		account.setTargetFolder(menuDao.findById(24));
		account.setDeleteFromMailbox(0);
		account.setLanguage("it");
		account.setDeleted(0);
		account.setSslModel(0);
		account.setExtractTags(0);

		assertTrue(dao.store(account));

		// Load an existing account and modify it
		EmailAccount account02 = dao.findById(account.getId());
		assertNotNull(account02);
		assertEquals("author@logicaldoc.sf.net", account02.getMailAddress());
		assertEquals("Aruba", account02.getProvider());
		assertEquals("pcalle", account02.getHost());
		assertEquals("author@logicaldoc.sf.net", account02.getUserName());
		assertEquals("fghfgh", account02.getPassword());

		account02.setMailAddress("updated@logicaldoc.sf.net");
		assertTrue(dao.store(account02));

		// Verify the stored account
		account = dao.findById(account.getId());
		assertNotNull(account);
		assertEquals("updated@logicaldoc.sf.net", account.getMailAddress());
	}

	public void testDelete() {
		EmailAccount account = dao.findById(2);
		assertNotNull(account);
		assertTrue(dao.delete(2));
		
		account = dao.findById(2);
		assertNull(account);
	}

	public void testFindById() {
		EmailAccount account = dao.findById(1);
		assertNotNull(account);
		assertEquals(1, account.getId());
		assertTrue(account.isAllowed("pdf"));
		assertFalse(account.isAllowed("xxx"));

		// Try with unexisting article
		account = dao.findById(99);
		assertNull(account);
	}

	public void testFindAll() {
		Collection<EmailAccount> accounts = dao.findAll();
		assertNotNull(accounts);
		assertEquals(2, accounts.size());
	}
}