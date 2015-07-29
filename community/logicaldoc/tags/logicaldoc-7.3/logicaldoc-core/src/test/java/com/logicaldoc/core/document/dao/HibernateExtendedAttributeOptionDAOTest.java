package com.logicaldoc.core.document.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.ExtendedAttributeOption;

/**
 * Test case for <code>HibernateExtendedAttributeOptionDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
public class HibernateExtendedAttributeOptionDAOTest extends AbstractCoreTCase {

	// Instance under test
	private ExtendedAttributeOptionDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateExtendedAttributeOptionDAO
		dao = (ExtendedAttributeOptionDAO) context.getBean("ExtendedAttributeOptionDAO");
	}

	@Test
	public void testDeleteByTemplateIdAndAttribute() {
		List<ExtendedAttributeOption> options = dao.findByTemplateAndAttribute(1L, null);
		Assert.assertEquals(3, options.size());

		dao.deleteByTemplateIdAndAttribute(1L, "att1");
		options = dao.findByTemplateAndAttribute(1L, null);
		Assert.assertEquals(1, options.size());

		dao.deleteByTemplateIdAndAttribute(1L, null);
		options = dao.findByTemplateAndAttribute(1L, null);
		Assert.assertEquals(0, options.size());
	}

	@Test
	public void testFindByTemplateAndAttribute() {
		List<ExtendedAttributeOption> options = dao.findByTemplateAndAttribute(1L, "att1");
		Assert.assertEquals(2, options.size());
		options = dao.findByTemplateAndAttribute(1L, null);
		Assert.assertEquals(3, options.size());

		options = dao.findByTemplateAndAttribute(999L, null);
		Assert.assertEquals(0, options.size());

		options = dao.findByTemplateAndAttribute(1L, "att2");
		Assert.assertEquals(1, options.size());
		options = dao.findByTemplateAndAttribute(2L, "att1");
		Assert.assertEquals(1, options.size());
		options = dao.findByTemplateAndAttribute(2L, null);
		Assert.assertEquals(1, options.size());
	}
}