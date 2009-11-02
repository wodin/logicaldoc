package com.logicaldoc.core.communication.dao;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.communication.Recipient;
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
		SystemMessage message = dao.findById(1);
		assertNull(message);
	}

	public void testFindById() {
		SystemMessage message = dao.findById(1);
		assertNotNull(message);
		assertEquals(1, message.getId());
		assertEquals("message text1", message.getMessageText());

		// Try with unexisting message
		message = dao.findById(99);
		assertNull(message);
	}

	public void testFindByRecipient() {
		Collection<SystemMessage> coll = dao.findByRecipient("sebastian", SystemMessage.TYPE_SYSTEM);
		assertEquals(2, coll.size());
		coll = dao.findByRecipient("marco", 1);
		assertEquals(2, coll.size());
		coll = dao.findByRecipient("paperino", 2);
		assertEquals(1, coll.size());
		coll = dao.findByRecipient("xxxx", SystemMessage.TYPE_SYSTEM);
		assertEquals(0, coll.size());
	}

	public void testDeleteExpiredMessages() {
		dao.deleteExpiredMessages("sebastian");
		assertNotNull(dao.findById(1));

		dao.deleteExpiredMessages(SystemMessage.TYPE_SYSTEM);
		assertNotNull(dao.findById(1));
	}

	public void testGetCount() {
		assertEquals(2, dao.getCount("sebastian", SystemMessage.TYPE_SYSTEM));
		assertEquals(2, dao.getCount("marco", 1));
		assertEquals(0, dao.getCount("admin", SystemMessage.TYPE_SYSTEM));
	}

	public void testFindByType() {
		Collection<SystemMessage> coll = dao.findByType(0);
		assertEquals(2, coll.size());
		coll = dao.findByType(1);
		assertEquals(2, coll.size());
		coll = dao.findByType(2);
		assertEquals(1, coll.size());
		coll = dao.findByType(3);
		assertEquals(0, coll.size());
	}

	public void testFindByMode() {
		Collection<SystemMessage> coll = dao.findByMode("CC");
		assertEquals(1, coll.size());
		coll = dao.findByMode("sms");
		assertEquals(2, coll.size());
		coll = dao.findByMode("socket");
		assertEquals(0, coll.size());
		coll = dao.findByMode("xxxx");
		assertEquals(0, coll.size());
	}

	public void testStore() {
		Set<Recipient> recipients = new HashSet<Recipient>();
		Recipient recipient = new Recipient();
		recipient.setName("pippo");
		recipient.setAddress("pippo");
		recipient.setType(Recipient.TYPE_SYSTEM);
		recipient.setMode("test1");
		recipients.add(recipient);
		recipient = new Recipient();
		recipient.setName("paperino");
		recipient.setAddress("paperino");
		recipient.setType(Recipient.TYPE_EMAIL);
		recipient.setMode("test2");
		recipients.add(recipient);

		SystemMessage message = new SystemMessage();
		message.setAuthor("admin");
		message.setRead(0);
		message.setMessageText("text");
		message.setLastNotified(new Date());
		message.setType(SystemMessage.TYPE_SYSTEM);
		message.setStatus(SystemMessage.STATUS_NEW);
		message.setRecipients(recipients);
		assertTrue(dao.store(message));
		assertEquals(4, message.getId());
		message = dao.findById(4);
		assertNotNull(message);
		assertEquals(2, message.getRecipients().size());

		// Update an already existing message
		message = dao.findById(1);
		assertNotNull(message);
		assertEquals("message text1", message.getMessageText());
		message.setMessageText("xxxx");
		message.setRecipients(recipients);
		dao.store(message);
		message = dao.findById(1);
		assertNotNull(message);
		assertEquals("xxxx", message.getMessageText());
		assertEquals(2, message.getRecipients().size());
	}

	public void testFindMessagesToBeSent() {
		Collection<SystemMessage> coll = dao.findMessagesToBeSent(0, 5);
		assertEquals(1, coll.size());
		coll = dao.findMessagesToBeSent(1, 5);
		assertEquals(2, coll.size());
		coll = dao.findMessagesToBeSent(2, 5);
		assertEquals(0, coll.size());
		coll = dao.findMessagesToBeSent(3, 5);
		assertEquals(0, coll.size());

		// Update an already existing message
		SystemMessage message = dao.findById(1);
		assertNotNull(message);
		assertEquals("message text1", message.getMessageText());
		message.setTrials(5);
		dao.store(message);
		message = dao.findById(1);
		assertNotNull(message);
		coll = dao.findMessagesToBeSent(1, 5);
		assertEquals(1, coll.size());

		message = dao.findById(2);
		assertNotNull(message);
		assertEquals("message text2", message.getMessageText());
		message.setType(0);
		dao.store(message);
		message = dao.findById(2);
		assertNotNull(message);
		coll = dao.findMessagesToBeSent(0, 5);
		assertEquals(2, coll.size());
	}
}
