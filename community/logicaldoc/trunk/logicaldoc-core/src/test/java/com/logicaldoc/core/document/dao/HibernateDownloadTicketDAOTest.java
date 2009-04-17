package com.logicaldoc.core.document.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.DownloadTicket;

/**
 * Test case for <code>HibernateDownloadTicketDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDownloadTicketDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private DownloadTicketDAO dao;

	public HibernateDownloadTicketDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDownaloadTicketoDAO
		dao = (DownloadTicketDAO) context.getBean("DownloadTicketDAO");
	}

	public void testDelete() {
		DownloadTicket ticket = dao.findByTicketId("1");
		assertNotNull(ticket);
		assertTrue(dao.deleteByTicketId("1"));
		ticket = dao.findByTicketId("1");
		assertNull(ticket);
	}

	public void testDeleteByDocId() {
		DownloadTicket ticket = dao.findByTicketId("1");
		assertNotNull(ticket);
		assertEquals(1, ticket.getDocId());
		ticket = dao.findByTicketId("3");
		assertNotNull(ticket);
		assertEquals(1, ticket.getDocId());

		assertTrue(dao.deleteByDocId(1));
		ticket = dao.findByTicketId("1");
		assertNull(ticket);
		ticket = dao.findByTicketId("3");
		assertNull(ticket);
	}

	public void testDeleteOlder() throws ParseException {
		List<DownloadTicket> tickets = dao.findAll();
		assertEquals(3, tickets.size());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		dao.deleteOlder(df.parse("11/12/2008"));
		tickets = dao.findAll();
		assertEquals(1, tickets.size());
	}

	public void testFindByTicketId() {
		DownloadTicket ticket = dao.findByTicketId("1");
		assertNotNull(ticket);
		assertEquals(1, ticket.getUserId());
		assertEquals(1, ticket.getDocId());

		ticket = dao.findByTicketId("99");
		assertNull(ticket);
	}

	public void testFindById() {
		DownloadTicket ticket = dao.findById(1);
		assertNotNull(ticket);
		assertEquals(1, ticket.getUserId());
		assertEquals(1, ticket.getDocId());

		ticket = dao.findById(99);
		assertNull(ticket);
	}

	public void testStore() {
		DownloadTicket ticket = new DownloadTicket();
		ticket.setDocId(1);
		ticket.setUserId(3);
		ticket.setTicketId("5");
		dao.store(ticket);

		DownloadTicket storedTicket = dao.findByTicketId("5");
		assertNotNull(storedTicket);
		assertEquals(ticket, storedTicket);
	}
}