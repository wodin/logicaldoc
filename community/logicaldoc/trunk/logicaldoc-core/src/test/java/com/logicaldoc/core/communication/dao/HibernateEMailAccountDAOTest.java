package com.logicaldoc.core.communication.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.communication.EMailAccount;

public class HibernateEMailAccountDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private EMailAccountDAO dao;

	public HibernateEMailAccountDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context.
		// Make sure that it is an HibernateEMailAccountDAO
		dao = (EMailAccountDAO) context.getBean("EMailAccountDAO");
	}

	public void testStore() {
		EMailAccount account = new EMailAccount();
		account.setMailAddress("author@logicaldoc.sf.net");
		account.setProvider("Aruba");
		account.setHost("pcalle");
		account.setUser("author@logicaldoc.sf.net");
		account.setPassword("fghfgh");
		account.setUserId(1);
		account.setDeleteFromMailbox(0);

		assertTrue(dao.store(account));

		// Load an existing account and modify it
		EMailAccount account02 = dao.findById(account.getId());
		assertNotNull(account02);
		assertEquals("author@logicaldoc.sf.net", account02.getMailAddress());
		assertEquals("Aruba", account02.getProvider());
		assertEquals("pcalle", account02.getHost());
		assertEquals("author@logicaldoc.sf.net", account02.getUser());
		assertEquals("fghfgh", account02.getPassword());

		account02.setMailAddress("updated@logicaldoc.sf.net");
		assertTrue(dao.store(account02));

		// Verify the stored account
		account = dao.findById(account.getId());
		assertNotNull(account);
		assertEquals("updated@logicaldoc.sf.net", account.getMailAddress());
	}

	public void testDelete() {
		EMailAccount account = dao.findById(2);
		assertNotNull(account);
		assertTrue(dao.delete(2));

		account = dao.findById(2);
		assertNull(account);
	}

	public void testFindById() {
		EMailAccount account = dao.findById(1);
		assertNotNull(account);
		assertEquals(1, account.getId());
		assertTrue(account.isAllowed("pdf"));
		assertFalse(account.isAllowed("xxx"));

		// Try with unexisting article
		account = dao.findById(99);
		assertNull(account);
	}

	public void testDeleteByUserId() {
		assertTrue(dao.deleteByUserId(4));

		Collection<EMailAccount> accounts = dao.findByUserId(4);
		assertTrue(accounts.isEmpty());
	}

	public void testFindAll() {
		Collection<EMailAccount> accounts = dao.findAll();
		assertNotNull(accounts);
		assertEquals(2, accounts.size());
	}

	public void testFindByUserId() {
		Collection<EMailAccount> accounts = dao.findByUserId(1);
		assertNotNull(accounts);
		assertEquals(1, accounts.size());

		accounts = dao.findByUserId(4);
		assertNotNull(accounts);
		assertEquals(1, accounts.size());

		// Try with unexisting user
		accounts = dao.findByUserId(99);
		assertNotNull(accounts);
		assertEquals(0, accounts.size());
	}
}