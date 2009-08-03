package com.logicaldoc.core.document.dao;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.DocumentTemplate;

/**
 * Test case for <code>HibernateDocumentTemplateDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class HibernateDocumentTemplateDAOTest extends AbstractCoreTestCase {

	public HibernateDocumentTemplateDAOTest(String name) {
		super(name);
	}

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
		assertTrue(dao.delete(1));
		DocumentTemplate template = dao.findById(1);
		assertNull(template);
	}

	@Test
	public void testFindAll() {
		Collection<DocumentTemplate> templates = dao.findAll();
		assertNotNull(templates);
		assertEquals(2, templates.size());
	}

	@Test
	public void testFindById() {
		DocumentTemplate template = dao.findById(1);
		assertNotNull(template);
		assertEquals(1, template.getId());
		assertEquals("test1", template.getName());
		assertTrue(template.getAttributes().containsKey("attr1"));

		// Try with unexisting template
		template = dao.findById(99);
		assertNull(template);
	}

	@Test
	public void testFindByName() {
		DocumentTemplate template = dao.findByName("test1");
		assertNotNull(template);
		assertEquals(1, template.getId());
		assertEquals("test1", template.getName());

		template = dao.findByName("xxx");
		assertNull(template);
	}

	@Test
	public void testStore() {
		DocumentTemplate template = new DocumentTemplate();
		template.setName("test3");
		template.setValue("a1", "v1");
		template.setValue("a2", "v2");
		assertTrue(dao.store(template));
		assertEquals(3, template.getId());
		template = dao.findById(3);
		assertEquals(3, template.getId());
		assertEquals("test3", template.getName());
		assertTrue(template.getAttributes().containsKey("a1"));
		assertTrue(template.getAttributes().containsKey("a2"));
	}
}