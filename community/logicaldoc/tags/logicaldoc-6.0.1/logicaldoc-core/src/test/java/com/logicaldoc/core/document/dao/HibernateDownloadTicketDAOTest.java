package com.logicaldoc.core.document.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.DownloadTicket;

/**
 * Test case for <code>HibernateDownloadTicketDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class HibernateDownloadTicketDAOTest extends AbstractCoreTCase {

	// Instance under test
	private DownloadTicketDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDownaloadTicketoDAO
		dao = (DownloadTicketDAO) context.getBean("DownloadTicketDAO");
	}

	@Test
	public void testDelete() {
		DownloadTicket ticket = dao.findByTicketId("1");
		Assert.assertNotNull(ticket);
		Assert.assertTrue(dao.deleteByTicketId("1"));
		ticket = dao.findByTicketId("1");
		Assert.assertNull(ticket);
	}

	@Test
	public void testDeleteByDocId() {
		DownloadTicket ticket = dao.findByTicketId("1");
		Assert.assertNotNull(ticket);
		Assert.assertEquals(1, ticket.getDocId());
		ticket = dao.findByTicketId("3");
		Assert.assertNotNull(ticket);
		Assert.assertEquals(1, ticket.getDocId());

		Assert.assertTrue(dao.deleteByDocId(1));
		ticket = dao.findByTicketId("1");
		Assert.assertNull(ticket);
		ticket = dao.findByTicketId("3");
		Assert.assertNull(ticket);
	}

	@Test
	public void testDeleteOlder() throws ParseException {
		List<DownloadTicket> tickets = dao.findAll();
		Assert.assertEquals(3, tickets.size());
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		dao.deleteOlder(df.parse("11/12/2008"));
		tickets = dao.findAll();
		Assert.assertEquals(1, tickets.size());
	}

	@Test
	public void testFindByTicketId() {
		DownloadTicket ticket = dao.findByTicketId("1");
		Assert.assertNotNull(ticket);
		Assert.assertEquals(1, ticket.getUserId());
		Assert.assertEquals(1, ticket.getDocId());

		ticket = dao.findByTicketId("99");
		Assert.assertNull(ticket);
	}

	@Test
	public void testFindById() {
		DownloadTicket ticket = dao.findById(1);
		Assert.assertNotNull(ticket);
		Assert.assertEquals(1, ticket.getUserId());
		Assert.assertEquals(1, ticket.getDocId());

		ticket = dao.findById(99);
		Assert.assertNull(ticket);
	}

	@Test
	public void testStore() {
		DownloadTicket ticket = new DownloadTicket();
		ticket.setDocId(1);
		ticket.setUserId(3);
		ticket.setTicketId("5");
		dao.store(ticket);

		DownloadTicket storedTicket = dao.findByTicketId("5");
		Assert.assertNotNull(storedTicket);
		Assert.assertEquals(ticket, storedTicket);
	}
}