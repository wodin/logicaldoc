package com.logicaldoc.core.document.dao;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.DocumentTemplate;

/**
 * Test case for <code>HibernateDocumentTemplateDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class HibernateDocumentTemplateDAOTest extends AbstractCoreTCase {

	private DocumentTemplateDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDocumentDAO
		dao = (DocumentTemplateDAO) context.getBean("DocumentTemplateDAO");
	}

	@Test
	public void testDelete() {
		Assert.assertTrue(dao.delete(1));
		DocumentTemplate template = dao.findById(1);
		Assert.assertNull(template);
	}

	@Test
	public void testFindAll() {
		Collection<DocumentTemplate> templates = dao.findAll();
		Assert.assertNotNull(templates);
		Assert.assertEquals(2, templates.size());
	}

	@Test
	public void testFindById() {
		DocumentTemplate template = dao.findById(1);
		Assert.assertNotNull(template);
		Assert.assertEquals(1, template.getId());
		Assert.assertEquals("test1", template.getName());
		Assert.assertTrue(template.getAttributes().containsKey("attr1"));

		// Try with unexisting template
		template = dao.findById(99);
		Assert.assertNull(template);
	}

	@Test
	public void testFindByName() {
		DocumentTemplate template = dao.findByName("test1");
		Assert.assertNotNull(template);
		Assert.assertEquals(1, template.getId());
		Assert.assertEquals("test1", template.getName());

		template = dao.findByName("xxx");
		Assert.assertNull(template);
	}

	@Test
	public void testStore() {
		DocumentTemplate template = new DocumentTemplate();
		template.setName("test3");
		template.setValue("a1", "v1");
		template.setValue("a2", "v2");
		Assert.assertTrue(dao.store(template));
		template = dao.findById(template.getId());
		Assert.assertEquals("test3", template.getName());
		Assert.assertTrue(template.getAttributes().containsKey("a1"));
		Assert.assertTrue(template.getAttributes().containsKey("a2"));
	}
}