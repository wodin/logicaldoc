package com.logicaldoc.core.document.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.Term;
import com.logicaldoc.core.security.Menu;

/**
 * Test case for <code>HibernateTermDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class HibernateTermDAOTest extends AbstractCoreTestCase {
	// Instance under test
	private TermDAO dao;

	public HibernateTermDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateTermDAO
		dao = (TermDAO) context.getBean("TermDAO");
	}

	public void testDelete() {
		Collection<Term> terms = dao.findByDocId(1);
		assertNotNull(terms);
		assertEquals(1, terms.size());

		assertTrue(dao.delete(1));
		terms = dao.findByDocId(1);
		assertNotNull(terms);
		assertEquals(0, terms.size());
	}

	public void findByPrimaryKey() {
		Term term = dao.findByPrimaryKey(1);
		assertNotNull(term);
		assertEquals("a", term.getStem());
		assertEquals("test", term.getOriginWord());
		term = dao.findByPrimaryKey(100);
		assertNull(term);
	}

	public void testFindByDocId() {
		Collection<Term> terms = dao.findByDocId(1);
		assertNotNull(terms);
		assertEquals(1, terms.size());

		terms = dao.findByDocId(9);
		assertNotNull(terms);
		assertEquals(0, terms.size());

		terms = dao.findByDocId(2);
		assertNotNull(terms);
		assertEquals(4, terms.size());
		assertEquals(0.7, terms.iterator().next().getValue());

		terms = dao.findByDocId(999);
		assertNotNull(terms);
		assertEquals(0, terms.size());
	}

	public void testFindByStem() {
		Collection<Term> terms = dao.findByStem(1, 100);
		assertNotNull(terms);
		assertEquals(3, terms.size());

		terms = dao.findByStem(2, 100);
		assertNotNull(terms);
		assertEquals(1, terms.size());

		terms = dao.findByStem(9, 100);
		assertNotNull(terms);
		assertEquals(5, terms.size());
	}

	public void testStore() {
		Term term = new Term();
		term.setDocId(Menu.MENUID_DOCUMENTS);
		term.setStem("d");
		term.setOriginWord("xxx");
		term.setValue(1.9);
		term.setWordCount(6);
		assertTrue(dao.store(term));

		Term storedTerm = dao.findByDocId(Menu.MENUID_DOCUMENTS).iterator().next();
		assertEquals(term, storedTerm);
		assertEquals("xxx", storedTerm.getOriginWord());
		assertEquals(1.9, storedTerm.getValue());
		assertEquals(6, storedTerm.getWordCount());

		// Load an existing term and modify it
		term = dao.findByDocId(2).iterator().next();
		term.setWordCount(11);
		term.setValue(14.2);
		assertTrue(dao.store(term));
		storedTerm = dao.findByDocId(2).iterator().next();
		assertEquals(term, storedTerm);
		assertEquals(11, storedTerm.getWordCount());
		assertEquals(14.2, storedTerm.getValue());
	}
}