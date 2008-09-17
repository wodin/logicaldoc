package com.logicaldoc.core.communication.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.communication.Attachment;
import com.logicaldoc.core.communication.EMail;
import com.logicaldoc.core.communication.Recipient;
import com.logicaldoc.core.communication.dao.EMailDAO;

public class HibernateEMailDAOTest extends AbstractCoreTestCase {

    // Instance under test
    private EMailDAO dao;

    public HibernateEMailDAOTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        // Retrieve the instance under test from spring context.
        // Make sure that it is an HibernateEMailDAO
        dao = (EMailDAO) context.getBean("EMailDAO");
    }

    public void testStore() {
        EMail message = new EMail();
        message.setMessageText("text");
        message.setAuthor("myself");
        message.setSubject("[LogicalDOC-devel] Join LogicalDOC developer community");
        message.setSentDate("Tue, 28 Nov 2006 15:44:02 +0100");
        message.setRead(1);
        message.setAuthorAddress("a.gasparini@logicalobjects.it");
        message.setUserName("author");
        message.setFolder("LogicalDOC-devel");
        message.setAccountId(1);
        message.setEmailId("xxx");

        // Add some recipients
        Recipient recipient = new Recipient();
        recipient.setAddress("m.meschieri@logicalobjects.it");
        recipient.setName("Marco Meschieri");
        Recipient recipient2 = new Recipient();
        recipient2.setAddress("author@logicaldoc.sf.net");
        recipient2.setName("logicaldoc Author");
        message.addRecipient(recipient);
        message.addRecipient(recipient2);

        // Add some attachments
        Attachment attachment = new Attachment();
        attachment.setFilename("magnolia30_log4j.xml");
        attachment.setIcon("icon");
        attachment.setMimeType("text/xml");
        message.addAttachment(4, attachment);

        assertTrue(dao.store(message));
        assertEquals(20, message.getMessageId());

        message = dao.findByPrimaryKey(20);
        assertNotNull(message);
        assertFalse(message.getRecipients().isEmpty());
        assertEquals(2, message.getRecipients().size());
        assertEquals(1, message.getAttachments().size());

        // Update an already existing message
        message = dao.findByPrimaryKey(18);
        assertNotNull(message);
        assertEquals("messageText", message.getMessageText());
        message.setMessageText("xxxx");

        dao.store(message);
        message = dao.findByPrimaryKey(18);
        assertNotNull(message);
        assertEquals("xxxx", message.getMessageText());

        Attachment xxx = message.getAttachments().values().iterator().next();
        assertEquals("hibernate.log", xxx.getFilename());
    }

    public void testDelete() {
        assertTrue(dao.delete(18));
        EMail message = dao.findByPrimaryKey(18);
        assertNull(message);
    }

    public void testFindByPrimaryKey() {
        EMail message = dao.findByPrimaryKey(17);
        assertNotNull(message);

        assertEquals("messageText", message.getMessageText());
        assertEquals("Morven Macauley", message.getAuthor());
        assertEquals("Re: maintenanc", message.getSubject());
        assertEquals("12/14/2006 04:49 AM", message.getSentDate());
        assertEquals("sprou@l2r9f8.varberg.net", message.getAuthorAddress());

        assertNotNull(message.getAttachments());
        assertEquals(1, message.getAttachmentCount());
        assertNotNull(message.getAttachment(54));

        // Try with unexisting document
        message = dao.findByPrimaryKey(99);
        assertNull(message);
    }

    public void testFindByUserNameString() {
        Collection<EMail> emails = dao.findByUserName("admin");
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertEquals(17, emails.iterator().next().getMessageId());

        // Try with a user without documents
        emails = dao.findByUserName("test");
        assertNotNull(emails);
        assertEquals(0, emails.size());
    }

    public void testFindByUserNameStringString() {
        Collection<EMail> emails = dao.findByUserName("author", "Junk");
        assertNotNull(emails);
        assertEquals(2, emails.size());
        assertEquals(18, emails.iterator().next().getMessageId());

        // Try with a user without documents
        emails = dao.findByUserName("author", "Trash");
        assertNotNull(emails);
        assertEquals(0, emails.size());
    }

    public void testFindByAccountId() {
        Collection<EMail> emails = dao.findByAccountId(1);
        assertNotNull(emails);
        assertEquals(2, emails.size());
        assertEquals(17, emails.iterator().next().getMessageId());

        dao.findByAccountId(2);
        assertNotNull(emails);
        assertEquals(2, emails.size());
        assertEquals(17, emails.iterator().next().getMessageId());
    }

    public void testCollectEmailIds() {
        Collection<String> emails = dao.collectEmailIds(1);
        assertNotNull(emails);
        assertEquals(2, emails.size());
        assertTrue(emails.contains("id1"));
        assertTrue(emails.contains("id2"));

        emails = dao.collectEmailIds(2);
        assertNotNull(emails);
        assertEquals(1, emails.size());
        assertTrue(emails.contains("id1"));
    }
}
