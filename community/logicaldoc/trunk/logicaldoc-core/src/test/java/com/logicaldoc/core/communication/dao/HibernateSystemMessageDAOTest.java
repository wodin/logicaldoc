package com.logicaldoc.core.communication.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.communication.SystemMessage;

/**
 * Test case for <code>HibernateSystemMessageDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class HibernateSystemMessageDAOTest extends AbstractCoreTestCase {
	// Instance under test
	private SystemMessageDAO dao;

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context.
		// Make sure that it is an HibernateSystemMessageDAOo
		dao = (SystemMessageDAO) context.getBean("SystemMessageDAO");
	}

	public HibernateSystemMessageDAOTest(String name) {
		super(name);
	}

	public void testDelete() {
		assertTrue(dao.delete(1));
		SystemMessage message = dao.findByPrimaryKey(1);
		assertNull(message);
	}

	public void testFindByPrimaryKey() {
		SystemMessage message = dao.findByPrimaryKey(1);
		assertNotNull(message);
		assertEquals(1, message.getId());
		assertEquals("message text1", message.getMessageText());

		// Try with unexisting message
		message = dao.findByPrimaryKey(99);
		assertNull(message);
	}

	public void testFindByRecipient() {
		Collection<SystemMessage> coll = dao.findByRecipient("sebastian");
		assertEquals(2, coll.size());
		coll = dao.findByRecipient("admin");
		assertEquals(1, coll.size());
		coll = dao.findByRecipient("test");
		assertEquals(0, coll.size());
		coll = dao.findByRecipient("xxxx");
		assertEquals(0, coll.size());
	}

	public void testDeleteExpiredMessages() {
		dao.deleteExpiredMessages("sebastian");
		assertNotNull(dao.findByPrimaryKey(2));
	}

	public void testGetCount() {
		assertEquals(1, dao.getCount("sebastian"));
		assertEquals(0, dao.getCount("admin"));
	}

	public void testStore() {
		SystemMessage message = new SystemMessage();
		message.setAuthor("admin");
		message.setRead(0);
		message.setRecipient("test");
		message.setMessageText("text");
		assertTrue(dao.store(message));
		assertEquals(4, message.getId());
		message = dao.findByPrimaryKey(4);
		assertNotNull(message);

		// Update an already existing message
		message = dao.findByPrimaryKey(1);
		assertNotNull(message);
		assertEquals("message text1", message.getMessageText());
		message.setMessageText("xxxx");
		dao.store(message);
		message = dao.findByPrimaryKey(1);
		assertNotNull(message);
		assertEquals("xxxx", message.getMessageText());
	}

}
