package com.logicaldoc.webservice.search;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.document.dao.DocumentDAO;
import com.logicaldoc.webservice.AbstractWebServiceTestCase;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;

public class SearchServiceImplTest extends AbstractWebServiceTestCase {

	private DocumentDAO docDao;

	// Instance under test
	private SearchServiceImpl searchServiceImpl;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		docDao = (DocumentDAO) context.getBean("DocumentDAO");

		// Make sure that this is a AuthServiceImpl instance
		searchServiceImpl = new SearchServiceImpl();
		searchServiceImpl.setValidateSession(false);
	}

	@Test
	public void testFindByFilename() throws Exception {
		WSDocument[] documents = searchServiceImpl.findByFilename("", "pluto");
		Assert.assertNotNull(documents);
		Assert.assertEquals(1, documents.length);

		List<WSDocument> docsList = Arrays.asList(documents);
		Assert.assertEquals(2, docsList.get(0).getId());

		documents = searchServiceImpl.findByFilename("", "PLUTO");
		Assert.assertNotNull(documents);
		Assert.assertEquals(1, documents.length);

		docsList = Arrays.asList(documents);
		Assert.assertEquals(2, docsList.get(0).getId());

		documents = searchServiceImpl.findByFilename("", "paperino");
		Assert.assertNotNull(documents);
		Assert.assertEquals(0, documents.length);

		Document doc = docDao.findById(1);
		Assert.assertNotNull(doc);
		Assert.assertEquals("pippo", doc.getFileName());
		docDao.initialize(doc);
		doc.setFileName("pluto");
		docDao.store(doc);
		Assert.assertEquals("pluto", doc.getFileName());

		documents = searchServiceImpl.findByFilename("", "pluto");
		Assert.assertNotNull(documents);
		Assert.assertEquals(2, documents.length);

		docsList = Arrays.asList(documents);
		Assert.assertEquals(1, docsList.get(0).getId());
		Assert.assertEquals(2, docsList.get(1).getId());
	}

	@Test
	public void testFindFolders() throws Exception {
		WSFolder[] folders = searchServiceImpl.findFolders("", "menu.admin");
		Assert.assertNotNull(folders);
		Assert.assertEquals(4, folders.length);
		List<WSFolder> foldersList = Arrays.asList(folders);
		Assert.assertEquals(99, foldersList.get(0).getId());
		Assert.assertEquals(100, foldersList.get(1).getId());

		folders = searchServiceImpl.findFolders("", "menu.adminxx");
		Assert.assertEquals(1, folders.length);
		foldersList = Arrays.asList(folders);
		Assert.assertEquals(100, foldersList.get(0).getId());

		folders = searchServiceImpl.findFolders("", "qqqxxx");
		Assert.assertNotNull(folders);
		Assert.assertEquals(0, folders.length);
	}

	@Test
	public void testFindByTag() throws Exception {
		WSDocument[] documents = searchServiceImpl.findByTag("", "abc");
		Assert.assertNotNull(documents);
		// There is also the shortcut
		Assert.assertEquals(2, documents.length);
		List<WSDocument> docsList = Arrays.asList(documents);
		Assert.assertEquals(1, docsList.get(0).getId());
		Assert.assertEquals(2, docsList.get(1).getId());

		documents = searchServiceImpl.findByTag("", "ask");
		Assert.assertNotNull(documents);
		Assert.assertEquals(1, documents.length);
		docsList = Arrays.asList(documents);
		Assert.assertEquals(2, docsList.get(0).getId());

		documents = searchServiceImpl.findByTag("", "xxx");
		Assert.assertNotNull(documents);
		Assert.assertEquals(0, documents.length);
	}
}