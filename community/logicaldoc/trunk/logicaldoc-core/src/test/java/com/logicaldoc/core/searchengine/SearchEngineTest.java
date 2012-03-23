package com.logicaldoc.core.searchengine;

import java.util.Date;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.security.Folder;

public class SearchEngineTest extends AbstractCoreTCase {

	protected static Log log = LogFactory.getLog(SearchEngineTest.class);

	protected SearchEngine engine;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		engine = (SearchEngine) context.getBean("SearchEngine");
		engine.init();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		engine.unlock();
		engine.close();
	}

	@Test
	public void testAddHit() throws Exception {
		Document document = new Document();
		document.setId(1L);
		document.setTitle("Document test 1");
		document.setLanguage("it");
		document.setDate(new Date());
		Folder fold = new Folder();
		fold.setId(Folder.DEFAULTWORKSPACE);
		fold.setName("test");
		document.setFolder(fold);

		engine.addHit(document, "Questo � un documento di prova. Per fortuna che esistono i test. document");

		document = new Document();
		document.setId(111L);
		document.setTitle("Document test 111");
		document.setTemplateId(0L);
		document.setLanguage("en");
		document.setDate(new Date());
		document.setFolder(fold);
		engine.addHit(
				document,
				"This is another test documents just for test insertion.Solr is an enterprise-ready, Lucene-based search server that supports faceted ... This is useful for retrieving and highlighting the documents contents for display but is not .... hl, When hl=true , highlight snippets in the query response.");

		Hit hit = engine.getHit(1L);
		Assert.assertEquals(1L, hit.getDocId());
		Assert.assertEquals("Document test 1", hit.getTitle());
		Assert.assertEquals("it", hit.getLanguage());

		hit = engine.getHit(111L);
		Assert.assertEquals(111L, hit.getDocId());
		Assert.assertEquals("Document test 111", hit.getTitle());
		Assert.assertEquals("en", hit.getLanguage());

		Assert.assertEquals(2, engine.getCount());

		hit = engine.getHit(112L);
		Assert.assertNull(hit);
	}

	@Test
	public void testDeleteHit() throws Exception {
		testAddHit();
		Hit hit = engine.getHit(1L);
		Assert.assertEquals(1L, hit.getDocId());
		Assert.assertEquals("Document test 1", hit.getTitle());
		Assert.assertEquals("it", hit.getLanguage());

		engine.deleteHit(1L);

		hit = engine.getHit(1L);
		Assert.assertNull(hit);

		engine.deleteHit(99L);

		Assert.assertEquals(1, engine.getCount());
	}

	@Test
	public void testSearch() throws Exception {
		testAddHit();
		Hits hits = engine.search("content:document", null, "en", 50);
		Assert.assertEquals(2, hits.getCount());

		hits = engine.search("content:document", null, "en", 1);
		Assert.assertEquals(1, hits.getCount());
		Assert.assertEquals(2, hits.getEstimatedCount());

		hits = engine.search("content:document", new String[] { "templateId:0", "folderId:4",
				"date:[2012-01-01T00:00:00Z TO *]" }, "en", 50);
		Assert.assertEquals(1, hits.getCount());
		Assert.assertEquals(111L, hits.next().getDocId());

		hits = engine.search("content:document", new String[] { "templateId:1" }, "en", 50);
		Assert.assertEquals(0, hits.getCount());

		hits = engine.search("content:house", null, "en", 50);
		Assert.assertEquals(0, hits.getCount());
	}

	@Test
	public void testClose() throws Exception {
		Document document = new Document();
		document.setId(1L);
		document.setTitle("Document test 1");
		document.setLanguage("en");

		engine.addHit(document, "This is a test content just for test insertion");

		Hit hit = engine.getHit(1L);
		Assert.assertEquals(1L, hit.getDocId());
		Assert.assertEquals("Document test 1", hit.getTitle());
		Assert.assertEquals("en", hit.getLanguage());

		engine.close();

		hit = engine.getHit(1L);
		Assert.assertNull(hit);
	}

	@Test
	public void testDropIndex() throws Exception {
		testAddHit();
		engine.dropIndexes();
		Hit hit = engine.getHit(1L);
		Assert.assertNull(hit);
	}
}