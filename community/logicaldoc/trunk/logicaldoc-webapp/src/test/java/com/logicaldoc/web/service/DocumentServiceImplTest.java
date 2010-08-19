package com.logicaldoc.web.service;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Bookmark;
import com.logicaldoc.core.document.DiscussionThread;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.BookmarkDAO;
import com.logicaldoc.core.document.dao.DiscussionThreadDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIExtendedAttribute;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
import com.logicaldoc.web.AbstractWebappTCase;

public class DocumentServiceImplTest extends AbstractWebappTCase {

	// Instance under test
	private DocumentServiceImpl service = new DocumentServiceImpl();

	private GUISession session;

	private DocumentDAO docDao;

	private DocumentLinkDAO linkDao;

	private DocumentTemplateDAO templateDao;

	private DiscussionThreadDAO dthreadDao;

	private BookmarkDAO bookDao;

	private HistoryDAO historyDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		templateDao = (DocumentTemplateDAO) context.getBean("DocumentTemplateDAO");
		linkDao = (DocumentLinkDAO) context.getBean("DocumentLinkDAO");
		dthreadDao = (DiscussionThreadDAO) context.getBean("DiscussionThreadDAO");
		bookDao = (BookmarkDAO) context.getBean("BookmarkDAO");
		historyDao = (HistoryDAO) context.getBean("HistoryDAO");

		SecurityServiceImpl securityService = new SecurityServiceImpl();
		session = securityService.login("admin", "admin", null);
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
	}

	@Test
	public void testGetVersionsById() throws InvalidSessionException {
		GUIVersion[] versions = service.getVersionsById(session.getSid(), 1, 2);
		Assert.assertNotNull(versions);
		Assert.assertEquals(2, versions.length);

		versions = service.getVersionsById(session.getSid(), 1, 4);
		Assert.assertNotNull(versions);
		Assert.assertEquals(1, versions.length);

		versions = service.getVersionsById(session.getSid(), 5, 2);
		Assert.assertNotNull(versions);
		Assert.assertEquals(1, versions.length);

		versions = service.getVersionsById(session.getSid(), 8, 9);
		Assert.assertNull(versions);
	}

	@Test
	public void testGetAttributes() throws InvalidSessionException {
		DocumentTemplate template = new DocumentTemplate();
		template.setName("test3");
		template.setValue("a1", "v1");
		template.setValue("a2", 23L);
		Assert.assertTrue(templateDao.store(template));
		Assert.assertEquals(3, template.getId());

		GUIExtendedAttribute[] extAttr = service.getAttributes(session.getSid(), 3);
		Assert.assertEquals("a1", extAttr[0].getName());
		Assert.assertEquals("v1", extAttr[0].getValue());
		Assert.assertEquals("a2", extAttr[1].getName());
		Assert.assertEquals(23L, extAttr[1].getValue());
	}

	@Test
	public void testGetById() throws InvalidSessionException {
		GUIDocument doc = service.getById(session.getSid(), 1);
		Assert.assertEquals(1, doc.getId());
		Assert.assertEquals("testDocname", doc.getTitle());
		Assert.assertNotNull(doc.getFolder());
		Assert.assertEquals(5, doc.getFolder().getId());
		Assert.assertEquals("/", doc.getFolder().getName());

		doc = service.getById(session.getSid(), 3);
		Assert.assertEquals(3, doc.getId());
		Assert.assertEquals("testDocname3", doc.getTitle());

		// Try with unexisting document
		doc = service.getById(session.getSid(), 99);
		Assert.assertNull(doc);
	}

	@Test
	public void testSave() throws InvalidSessionException {
		GUIDocument doc = service.getById(session.getSid(), 1);

		doc = service.save(session.getSid(), doc);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname", doc.getTitle());
		Assert.assertEquals("myself", doc.getPublisher());

		doc = service.getById(session.getSid(), 3);
		Assert.assertEquals("testDocname3", doc.getTitle());

		doc = service.save(session.getSid(), doc);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname3", doc.getTitle());
		Assert.assertEquals("pippo", doc.getFileName());
	}

	@Test
	public void testUpdateLink() throws InvalidSessionException {
		DocumentLink link = linkDao.findById(1);
		Assert.assertNotNull(link);
		Assert.assertEquals("test", link.getType());

		service.updateLink(session.getSid(), 1, "pippo");

		link = linkDao.findById(1);
		Assert.assertNotNull(link);
		Assert.assertEquals("pippo", link.getType());
	}

	@Test
	public void testDeleteLinks() throws InvalidSessionException {
		DocumentLink link = linkDao.findById(1);
		Assert.assertNotNull(link);
		Assert.assertEquals("test", link.getType());
		link = linkDao.findById(2);
		Assert.assertNotNull(link);
		Assert.assertEquals("xyz", link.getType());

		service.deleteLinks(session.getSid(), new long[] { 1, 2 });

		link = linkDao.findById(1);
		Assert.assertNull(link);
		link = linkDao.findById(2);
		Assert.assertNull(link);
	}

	@Test
	public void testDelete() throws InvalidSessionException {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname", doc.getTitle());
		doc = docDao.findById(2);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname2", doc.getTitle());
		Assert.assertEquals(1, doc.getDocRef().longValue());
		doc = docDao.findById(3);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname3", doc.getTitle());

		service.delete(session.getSid(), new long[] { 2, 3 });

		doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		doc = docDao.findById(2);
		Assert.assertNull(doc);
		doc = docDao.findById(3);
		Assert.assertNull(doc);
	}

	@Test
	public void testDeleteDiscussions() throws InvalidSessionException {
		DiscussionThread dthread = dthreadDao.findById(1);
		Assert.assertNotNull(dthread);
		Assert.assertEquals("subject", dthread.getSubject());

		service.deleteDiscussions(session.getSid(), new long[] { 1 });

		dthread = dthreadDao.findById(1);
		Assert.assertNull(dthread);
	}

	@Test
	public void testStartDiscussion() throws InvalidSessionException {
		List<DiscussionThread> threads = dthreadDao.findByDocId(1L);
		Assert.assertNotNull(threads);
		Assert.assertEquals(2, threads.size());

		long dThreadId = service.startDiscussion(session.getSid(), 1L, "pippo", "logicaldoc");

		Assert.assertEquals(4, dThreadId);
		DiscussionThread dthread = dthreadDao.findById(dThreadId);
		Assert.assertNotNull(dthread);
		Assert.assertEquals("pippo", dthread.getSubject());

		threads = dthreadDao.findByDocId(1L);
		Assert.assertNotNull(threads);
		Assert.assertEquals(3, threads.size());
	}

	@Test
	public void testReplyPost() throws InvalidSessionException {
		int commentPosition = service.replyPost(session.getSid(), 1L, 1, "pippo", "logicaldoc");
		Assert.assertEquals(3, commentPosition);
	}

	@Test
	public void testDeletePosts() throws InvalidSessionException {
		DiscussionThread dthread = dthreadDao.findById(1);
		Assert.assertNotNull(dthread);
		dthreadDao.initialize(dthread);
		Assert.assertEquals(2, dthread.getComments().size());
		Assert.assertEquals("body1", dthread.getComments().get(0).getBody());
		Assert.assertEquals("body2", dthread.getComments().get(1).getBody());

		service.deletePosts(session.getSid(), 1L, new int[] { 0, 1 });

		dthread = dthreadDao.findById(1);
		Assert.assertNotNull(dthread);
		dthreadDao.initialize(dthread);
		Assert.assertEquals(1, dthread.getComments().get(0).getDeleted());
		Assert.assertEquals(1, dthread.getComments().get(1).getDeleted());
	}

	@Test
	public void testMakeImmutable() throws InvalidSessionException {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals(0, doc.getImmutable());
		doc = docDao.findById(2);
		Assert.assertNotNull(doc);
		Assert.assertEquals(0, doc.getImmutable());

		service.makeImmutable(session.getSid(), new long[] { 1, 2 }, "comment");

		doc = docDao.findById(1);
		Assert.assertEquals(1, doc.getImmutable());
		doc = docDao.findById(2);
		Assert.assertEquals(1, doc.getImmutable());
	}

	@Test
	public void testLock() throws InvalidSessionException {
		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals(3L, doc.getLockUserId().longValue());
		doc = docDao.findById(2);
		Assert.assertNotNull(doc);
		Assert.assertEquals(3L, doc.getLockUserId().longValue());

		service.unlock(session.getSid(), new long[] { 1, 2 });

		doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertNull(doc.getLockUserId());
		doc = docDao.findById(2);
		Assert.assertNotNull(doc);
		Assert.assertNull(doc.getLockUserId());

		service.lock(session.getSid(), new long[] { 1, 2 }, "comment");

		doc = docDao.findById(1);
		Assert.assertEquals(1L, doc.getLockUserId().longValue());
		doc = docDao.findById(2);
		Assert.assertEquals(1L, doc.getLockUserId().longValue());
	}

	@Test
	public void testLinkDocuments() throws InvalidSessionException {
		service.linkDocuments(session.getSid(), new long[] { 1, 2 }, new long[] { 3, 4 });

		DocumentLink link = linkDao.findByDocIdsAndType(1, 3, "default");
		Assert.assertNotNull(link);
		link = linkDao.findByDocIdsAndType(1, 4, "default");
		Assert.assertNotNull(link);
		link = linkDao.findByDocIdsAndType(2, 3, "default");
		Assert.assertNotNull(link);
		link = linkDao.findByDocIdsAndType(2, 4, "default");
		Assert.assertNotNull(link);
		link = linkDao.findByDocIdsAndType(3, 4, "default");
		Assert.assertNull(link);
	}

	@Test
	public void testRestore() throws InvalidSessionException {
		docDao.delete(4);
		Assert.assertNull(docDao.findById(4));
		service.restore(session.getSid(), 4);
		Assert.assertNotNull(docDao.findById(4));
	}

	@Test
	public void testBookmarks() throws InvalidSessionException {
		service.addBookmarks(session.getSid(), new long[] { 1, 2 });

		Bookmark book = bookDao.findByUserIdAndDocId(1, 1).get(0);
		Assert.assertNotNull(book);
		Assert.assertEquals(1, book.getId());
		book = bookDao.findByUserIdAndDocId(1, 2).get(0);
		Assert.assertNotNull(book);
		Assert.assertEquals(2, book.getId());

		GUIBookmark bookmark = new GUIBookmark();
		bookmark.setId(1);
		bookmark.setName("bookmarkTest");
		bookmark.setDescription("bookDescr");

		service.updateBookmark(session.getSid(), bookmark);
		book = bookDao.findById(bookmark.getId());
		Assert.assertNotNull(book);
		Assert.assertEquals(1, book.getId());
		Assert.assertEquals("bookmarkTest", book.getTitle());
		Assert.assertEquals("bookDescr", book.getDescription());

		service.deleteBookmarks(session.getSid(), new long[] { 1, 2 });

		book = bookDao.findById(1);
		Assert.assertNull(book);
		book = bookDao.findById(2);
		Assert.assertNull(book);
	}

	@Test
	public void testMarkHistoryAsRead() throws InvalidSessionException {
		List<History> histories = historyDao.findByUserIdAndEvent(1, "data test 01");
		Assert.assertEquals(2, histories.size());
		Assert.assertEquals(1, histories.get(0).getNew());
		Assert.assertEquals(1, histories.get(1).getNew());

		service.markHistoryAsRead(session.getSid(), "data test 01");

		histories = historyDao.findByUserIdAndEvent(1, "data test 01");
		Assert.assertEquals(2, histories.size());
		Assert.assertEquals(0, histories.get(0).getNew());
		Assert.assertEquals(0, histories.get(1).getNew());
	}

	@Test
	public void testIndexable() throws InvalidSessionException {
		Document doc1 = docDao.findById(1);
		Assert.assertNotNull(doc1);
		Assert.assertEquals(AbstractDocument.INDEX_INDEXED, doc1.getIndexed());
		Document doc2 = docDao.findById(2);
		Assert.assertNotNull(doc2);
		Assert.assertEquals(AbstractDocument.INDEX_TO_INDEX, doc2.getIndexed());
		Document doc3 = docDao.findById(3);
		Assert.assertNotNull(doc3);
		Assert.assertEquals(AbstractDocument.INDEX_INDEXED, doc3.getIndexed());
		service.markUnindexable(session.getSid(), new long[] { 1, 2, 3 });

		doc1 = docDao.findById(1);
		Assert.assertNotNull(doc1);
		Assert.assertEquals(AbstractDocument.INDEX_SKIP, doc1.getIndexed());
		doc2 = docDao.findById(2);
		Assert.assertNotNull(doc2);
		Assert.assertEquals(AbstractDocument.INDEX_SKIP, doc2.getIndexed());
		doc3 = docDao.findById(3);
		Assert.assertNotNull(doc3);
		Assert.assertEquals(AbstractDocument.INDEX_SKIP, doc3.getIndexed());

		service.markIndexable(session.getSid(), new long[] { 1, 3 });

		doc1 = docDao.findById(1);
		Assert.assertNotNull(doc1);
		Assert.assertEquals(AbstractDocument.INDEX_TO_INDEX, doc1.getIndexed());
		doc3 = docDao.findById(3);
		Assert.assertNotNull(doc3);
		Assert.assertEquals(AbstractDocument.INDEX_TO_INDEX, doc3.getIndexed());
	}
}