package com.logicaldoc.core.document.dao;

import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.Bookmark;

/**
 * Test case for <code>HibernateBookmarkDAOTest</code>
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class HibernateBookmarkDAOTest extends AbstractCoreTCase {

	// Instance under test
	private BookmarkDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDiscussionDAO
		dao = (BookmarkDAO) context.getBean("BookmarkDAO");
	}

	@Test
	public void testStore() {
		Bookmark book1 = dao.findById(1);
		dao.initialize(book1);
		book1.setDescription("pippo");
		dao.store(book1);
		Assert.assertNotNull(book1);

		Bookmark book2 = dao.findById(2);
		dao.initialize(book2);
		book2.setDescription("paperino");
		dao.store(book2);
		Assert.assertNotNull(book2);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void testFindByUserId() {
		Collection bookmarks = dao.findByUserId(1);
		Assert.assertNotNull(bookmarks);
		Assert.assertEquals(2, bookmarks.size());

		bookmarks = dao.findByUserId(2);
		Assert.assertNotNull(bookmarks);
		Assert.assertEquals(1, bookmarks.size());

		// Try with unexisting user
		bookmarks = dao.findByUserId(99);
		Assert.assertNotNull(bookmarks);
		Assert.assertEquals(0, bookmarks.size());
	}
	
	@Test
	public void testFindByUserIdAndTargetId() {
		List<Bookmark> bookmarks = dao.findByUserIdAndDocId(1, 1);
		Assert.assertEquals(1, bookmarks.size());
		bookmarks = dao.findByUserIdAndDocId(1, 2);
		Assert.assertEquals(1, bookmarks.size());

		Bookmark book1 = dao.findById(1);
		dao.initialize(book1);
		book1.setTargetId(3);
		dao.store(book1);

		bookmarks = dao.findByUserIdAndDocId(1, 1);
		Assert.assertEquals(0, bookmarks.size());
		bookmarks = dao.findByUserIdAndDocId(1, 2);
		Assert.assertEquals(1, bookmarks.size());
	}
}
