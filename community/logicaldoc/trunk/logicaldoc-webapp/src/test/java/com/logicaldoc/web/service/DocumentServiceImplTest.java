package com.logicaldoc.web.service;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.document.AbstractDocument;
import com.logicaldoc.core.document.Bookmark;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.DocumentLink;
import com.logicaldoc.core.document.DocumentNote;
import com.logicaldoc.core.document.DocumentTemplate;
import com.logicaldoc.core.document.History;
import com.logicaldoc.core.document.dao.BookmarkDAO;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.core.document.dao.DocumentLinkDAO;
import com.logicaldoc.core.document.dao.DocumentNoteDAO;
import com.logicaldoc.core.document.dao.DocumentTemplateDAO;
import com.logicaldoc.core.document.dao.HistoryDAO;
import com.logicaldoc.core.security.SessionManager;
import com.logicaldoc.gui.common.client.ServerException;
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

	private DocumentNoteDAO noteDao;

	private BookmarkDAO bookDao;

	private HistoryDAO historyDao;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		docDao = (DocumentDAO) context.getBean("DocumentDAO");
		templateDao = (DocumentTemplateDAO) context.getBean("DocumentTemplateDAO");
		linkDao = (DocumentLinkDAO) context.getBean("DocumentLinkDAO");
		noteDao = (DocumentNoteDAO) context.getBean("DocumentNoteDAO");
		bookDao = (BookmarkDAO) context.getBean("BookmarkDAO");
		historyDao = (HistoryDAO) context.getBean("HistoryDAO");

		SecurityServiceImpl securityService = new SecurityServiceImpl();
		session = securityService.login("admin", "admin", null, null);
		Assert.assertNotNull(session);
		Assert.assertNotNull(SessionManager.getInstance().get(session.getSid()));
	}

	@Test
	public void testGetVersionsById() throws ServerException {
		GUIVersion[] versions = service.getVersionsById(session.getSid(), 1, 2);
		Assert.assertNotNull(versions);
		Assert.assertEquals(2, versions.length);
	}

	@Test
	public void testGetAttributes() throws ServerException {
		DocumentTemplate template = new DocumentTemplate();
		template.setName("test3");
		template.setValue("a1", "v1");
		template.setValue("a2", 23L);
		Assert.assertTrue(templateDao.store(template));

		GUIExtendedAttribute[] extAttr = service.getAttributes(session.getSid(), template.getId());
		Assert.assertEquals(GUIExtendedAttribute.TYPE_STRING, extAttr[0].getType());
		Assert.assertEquals("a1", extAttr[0].getName());
		Assert.assertEquals("v1", extAttr[0].getValue());
		Assert.assertEquals("a2", extAttr[1].getName());
		Assert.assertEquals(23L, extAttr[1].getValue());
	}

	@Test
	public void testGetById() throws ServerException {
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
	public void testSave() throws Exception {
		GUIDocument doc = service.getById(session.getSid(), 1);

		doc.setCoverage("xxxx");
		doc = service.save(session.getSid(), doc);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname", doc.getTitle());
		Assert.assertEquals("myself", doc.getPublisher());
		Assert.assertEquals("xxxx", doc.getCoverage());

		doc = service.getById(session.getSid(), 3);
		Assert.assertEquals("testDocname3", doc.getTitle());
		Assert.assertEquals("pippo", doc.getFileName());

		doc = service.save(session.getSid(), doc);
		Assert.assertNotNull(doc);
		Assert.assertEquals("testDocname3", doc.getTitle());
	}

	@Test
	public void testUpdateLink() throws ServerException {
		DocumentLink link = linkDao.findById(1);
		Assert.assertNotNull(link);
		Assert.assertEquals("test", link.getType());

		service.updateLink(session.getSid(), 1, "pippo");

		link = linkDao.findById(1);
		Assert.assertNotNull(link);
		Assert.assertEquals("pippo", link.getType());
	}

	@Test
	public void testDeleteLinks() throws ServerException {
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
	public void testDelete() throws ServerException {
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
	public void testDeleteNotes() throws ServerException {
		List<DocumentNote> notes = noteDao.findByDocId(1);
		Assert.assertNotNull(notes);
		Assert.assertEquals(2, notes.size());
		Assert.assertEquals("message for note 1", notes.get(0).getMessage());

		service.deleteNotes(session.getSid(), new long[] { 1 });

		notes = noteDao.findByDocId(1);
		Assert.assertNotNull(notes);
		Assert.assertEquals(1, notes.size());
	}

	@Test
	public void testAddNote() throws ServerException {
		List<DocumentNote> notes = noteDao.findByDocId(1L);
		Assert.assertNotNull(notes);
		Assert.assertEquals(2, notes.size());

		long noteId = service.addNote(session.getSid(), 1L, "pippo");

		DocumentNote note = noteDao.findById(noteId);
		Assert.assertNotNull(note);
		Assert.assertEquals("pippo", note.getMessage());

		notes = noteDao.findByDocId(1L);
		Assert.assertNotNull(notes);
		Assert.assertEquals(3, notes.size());
	}

	@Test
	public void testLock() throws ServerException {
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
	public void testLinkDocuments() throws ServerException {
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
	public void testRestore() throws ServerException {
		docDao.delete(4);
		Assert.assertNull(docDao.findById(4));
		service.restore(session.getSid(), new long[] { 4 }, 5);
		Assert.assertNotNull(docDao.findById(4));
		Assert.assertNotNull(docDao.findById(4));
		Assert.assertEquals(5L, docDao.findById(4).getFolder().getId());
	}

	@Test
	public void testBookmarks() throws ServerException {
		service.addBookmarks(session.getSid(), new long[] { 1, 2 }, 0);

		Bookmark book = bookDao.findByUserIdAndDocId(1, 1).get(0);
		Assert.assertNotNull(book);
		book = bookDao.findByUserIdAndDocId(1, 2).get(0);
		Assert.assertNotNull(book);

		GUIBookmark bookmark = new GUIBookmark();
		bookmark.setId(book.getId());
		bookmark.setName("bookmarkTest");
		bookmark.setDescription("bookDescr");

		service.updateBookmark(session.getSid(), bookmark);
		book = bookDao.findById(bookmark.getId());
		Assert.assertNotNull(book);
		Assert.assertEquals("bookmarkTest", book.getTitle());
		Assert.assertEquals("bookDescr", book.getDescription());

		service.deleteBookmarks(session.getSid(), new long[] { 1, bookmark.getId() });

		book = bookDao.findById(1);
		Assert.assertNull(book);
		book = bookDao.findById(2);
		Assert.assertNull(book);
	}

	@Test
	public void testMarkHistoryAsRead() throws ServerException {
		List<History> histories = historyDao.findByUserIdAndEvent(1, "data test 01", null);
		Assert.assertEquals(2, histories.size());
		Assert.assertEquals(1, histories.get(0).getNew());
		Assert.assertEquals(1, histories.get(1).getNew());

		service.markHistoryAsRead(session.getSid(), "data test 01");

		histories = historyDao.findByUserIdAndEvent(1, "data test 01", null);
		Assert.assertEquals(2, histories.size());
		Assert.assertEquals(0, histories.get(0).getNew());
		Assert.assertEquals(0, histories.get(1).getNew());
	}

	@Test
	public void testIndexable() throws ServerException {
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

	@Test
	public void testCountDocuments() throws ServerException {
		Assert.assertEquals(4, service.countDocuments(session.getSid(), new long[] { 5 }, 0));
		Assert.assertEquals(0, service.countDocuments(session.getSid(), new long[] { 5 }, 3));
	}
}