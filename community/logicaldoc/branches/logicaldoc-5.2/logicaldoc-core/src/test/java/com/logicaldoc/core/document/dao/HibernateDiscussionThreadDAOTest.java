package com.logicaldoc.core.document.dao;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.document.DiscussionComment;
import com.logicaldoc.core.document.DiscussionThread;

/**
 * Test case for discussion thread DAO
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 4.5
 */
public class HibernateDiscussionThreadDAOTest extends AbstractCoreTestCase {

	private DiscussionThreadDAO dao;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// Retrieve the instance under test from spring context. Make sure that
		// it is an HibernateDiscussionDAO
		dao = (DiscussionThreadDAO) context.getBean("DiscussionThreadDAO");
	}

	@Test
	public void testFindByDocumentId() {
		List<DiscussionThread> threads = dao.findByDocId(1L);
		Assert.assertNotNull(threads);
		Assert.assertEquals(2, threads.size());
		DiscussionThread thread = threads.get(0);
		dao.initialize(thread);
		Assert.assertEquals("body2", thread.getComments().get(1).getBody());

		DiscussionComment comment = new DiscussionComment();
		comment.setBody("body3");
		comment.setSubject("RE: RE: subject");
		comment.setReplyTo(2);
		comment.setUserId(1);
		comment.setUserName("Admin");
	}

	@Test
	public void testFindCommentsByUserId() {
		List<DiscussionComment> comments = dao.findCommentsByUserId(1L, 1);
		Assert.assertNotNull(comments);
		Assert.assertEquals(1, comments.size());
		Assert.assertEquals(1L, comments.get(0).getThreadId().longValue());
		Assert.assertEquals(1L, comments.get(0).getUserId());

		comments = dao.findCommentsByUserId(1L, null);
		Assert.assertNotNull(comments);
		Assert.assertEquals(2, comments.size());

		comments = dao.findCommentsByUserId(2L, null);
		Assert.assertNotNull(comments);
		Assert.assertEquals(0, comments.size());
	}
}