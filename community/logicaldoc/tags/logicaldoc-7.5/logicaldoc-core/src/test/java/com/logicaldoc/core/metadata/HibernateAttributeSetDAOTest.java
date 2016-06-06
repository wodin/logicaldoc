package com.logicaldoc.core.metadata;

import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.security.Tenant;

/**
 * Test case for <code>HibernateAttributeDAO</code>
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 4.0
 */
public class HibernateAttributeSetDAOTest extends AbstractCoreTCase {

	private AttributeSetDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateAttributeSetDAO
		dao = (AttributeSetDAO) context.getBean("AttributeSetDAO");
	}

	@Test
	public void testDelete() {
		Assert.assertTrue(dao.delete(1));
		AttributeSet set = dao.findById(1);
		Assert.assertNull(set);
	}

	@Test
	public void testFindAll() {
		Collection<AttributeSet> sets = dao.findAll();
		Assert.assertNotNull(sets);
		Assert.assertEquals(1, sets.size());
	}

	@Test
	public void testFindById() {
		AttributeSet set = dao.findById(-1);
		Assert.assertNotNull(set);
		Assert.assertEquals(-1, set.getId());
		Assert.assertEquals("default", set.getName());
		Assert.assertTrue(set.getAttributes().containsKey("object"));

		// Try with unexisting set
		set = dao.findById(99);
		Assert.assertNull(set);
	}

	@Test
	public void testFindByName() {
		AttributeSet set = dao.findByName("default", Tenant.DEFAULT_ID);
		Assert.assertNotNull(set);
		Assert.assertEquals(-1, set.getId());
		Assert.assertEquals("default", set.getName());

		set = dao.findByName("xxx", Tenant.DEFAULT_ID);
		Assert.assertNull(set);

		set = dao.findByName("default", 99L);
		Assert.assertNull(set);
	}

	@Test
	public void testStore() {
		AttributeSet set = new AttributeSet();
		set.setName("test3");
		set.setValue("a1", "v1");
		set.setValue("a2", "v2");
		Assert.assertTrue(dao.store(set));
		set = dao.findById(set.getId());
		Assert.assertEquals("test3", set.getName());
		Assert.assertTrue(set.getAttributes().containsKey("a1"));
		Assert.assertTrue(set.getAttributes().containsKey("a2"));
	}
}