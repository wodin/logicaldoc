package com.logicaldoc.core.communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;

/**
 * Test case for <code>HibernateMessageTemplateDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class HibernateMessageTemplateDAOTest extends AbstractCoreTCase {
	// Instance under test
	private MessageTemplateDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context.
		// Make sure that it is an HibernateMessageTemplateDAO
		dao = (MessageTemplateDAO) context.getBean("MessageTemplateDAO");
	}

	@Test
	public void testFindByLanguage() {
		Collection<MessageTemplate> coll = dao.findByLanguage("en");
		Assert.assertEquals(5, coll.size());
		coll = dao.findByLanguage("it");
		Assert.assertEquals(1, coll.size());
		coll = dao.findByLanguage("de");
		Assert.assertEquals(0, coll.size());
	}

	@Test
	public void testFindByNameAndLanguage() {
		Map<String, String> cntx = new HashMap<String, String>();
		cntx.put("xxx", "label");

		MessageTemplate tmp = dao.findByNameAndLanguage("test1", "en");
		Assert.assertNotNull(tmp);
		Assert.assertEquals("test1", tmp.getName());
		Assert.assertEquals("body Username label", tmp.getFormattedBody(cntx));
		Assert.assertEquals("subject label", tmp.getFormattedSubject(cntx));

		tmp = dao.findByNameAndLanguage("test1", "de");
		Assert.assertNotNull(tmp);
		Assert.assertEquals("test1", tmp.getName());
		Assert.assertEquals("en", tmp.getLanguage());

		tmp = dao.findByNameAndLanguage("test1", "it");
		Assert.assertNotNull(tmp);
		Assert.assertEquals("test1", tmp.getName());
		Assert.assertEquals("corpo Username label", tmp.getFormattedBody(cntx));
		Assert.assertEquals("soggetto label", tmp.getFormattedSubject(cntx));

		tmp = dao.findByNameAndLanguage("xxxxxx", "en");
		Assert.assertNull(tmp);
	}
}
