package com.logicaldoc.core.document.dao;

import java.util.Collection;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.Article;
import com.logicaldoc.core.document.dao.ArticleDAO;

/**
 * Test case for <code>HibernateArticleDAO</code>
 * 
 * @author Marco Meschieri
 * @version $Id:$
 * @since 3.0
 */
public class HibernateArticleDAOTest extends AbstractCoreTestCase {

	// Instance under test
	private ArticleDAO dao;

	public HibernateArticleDAOTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDownaloadTicketDAO
		dao = (ArticleDAO) context.getBean("ArticleDAO");
	}

	public void testDelete() {
		Article article = dao.findById(3);
		assertNotNull(article);
		assertTrue(dao.delete(3));
		article = dao.findById(3);
		assertNull(article);
	}

	public void testFindByDocId() {
		Collection<Article> articles = dao.findByDocId(1);
		assertNotNull(articles);
		assertEquals(3, articles.size());

		// Try with unexisting document
		articles = dao.findByDocId(99);
		assertNotNull(articles);
		assertEquals(0, articles.size());
	}

	public void testFindByUserName() {
		Collection<Article> articles = dao.findByUserName("admin");
		assertNotNull(articles);
		assertEquals(2, articles.size());
		articles = dao.findByUserName("sebastian");
		assertNotNull(articles);
		assertEquals(1, articles.size());

		// Try with unexisting user
		articles = dao.findByUserName("xxx");
		assertNotNull(articles);
		assertEquals(0, articles.size());
	}
	
	public void testFindById() {
		Article article = dao.findById(1);
		assertNotNull(article);
		assertEquals(1L, article.getId());
		assertEquals("subject", article.getSubject());

		// Try with unexisting article
		article = dao.findById(99);
		assertNull(article);
	}

	public void testStore() {
		Article article = new Article();
		article.setUsername("admin");
		article.setDocId(1);
		article.setSubject("xx");
		article.setMessage("xxxxx");
		assertTrue(dao.store(article));

		assertEquals(4L, article.getId());

		// Load an existing article and modify it
		article = dao.findById(3);
		assertNotNull(article);
		assertEquals("subject3", article.getSubject());
		article.setSubject("yyyy");
		assertTrue(dao.store(article));
		article = dao.findById(3);
		assertNotNull(article);
		assertEquals("yyyy", article.getSubject());
	}

}
