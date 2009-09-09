package com.logicaldoc.core.generic.dao;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.generic.Generic;

/**
 * Test case for <code>HibernateGenericDAO</code>
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.0
 */
public class HibernateGenericDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private GenericDAO dao;

	public HibernateGenericDAOTest(String name) {
		super(name);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateGenericDAO
		dao = (GenericDAO) context.getBean("GenericDAO");
	}

	@Test
	public void testDelete() {
		assertNotNull(dao.findById(1L));
		dao.delete(1L);
		assertNull(dao.findById(1L));
	}

	@Test
	public void testFindByAlternateKey() {
		Generic generic = dao.findByAlternateKey("a", "a1");
		assertNotNull(generic);
		assertEquals(new Integer(0), generic.getInteger1());
		generic = dao.findByAlternateKey("a", "xxx");
		assertNull(generic);
	}

	@Test
	public void testFindById() {
		Generic generic = dao.findById(1L);
		assertNotNull(generic);
		assertEquals(new Integer(0), generic.getInteger1());
	}

	@Test
	public void testFindByTypeAndSubtype() {
		List<Generic> generics=dao.findByTypeAndSubtype("a", "a%");
		assertEquals(2,generics.size());
	}

	@Test
	public void testStore() {
		Generic generic = new Generic();
		generic.setType("xx");
		generic.setSubtype("xxx");
		generic.setInteger1(22);
		generic.setString1("aaa");
		assertTrue(dao.store(generic));
		generic = dao.findById(generic.getId());
		assertEquals("xx", generic.getType());
		assertEquals("xxx", generic.getSubtype());
		assertEquals(new Integer(22), generic.getInteger1());
		assertEquals("aaa", generic.getString1());
	}

	@Test
	public void testInitialize() {
		Generic generic = dao.findById(1);
		assertNotNull(generic);
		assertEquals(new Integer(0), generic.getInteger1());
		dao.initialize(generic);
		assertEquals(1, generic.getAttributes().size());
		assertEquals("val1", generic.getValue("att1"));
	}
}