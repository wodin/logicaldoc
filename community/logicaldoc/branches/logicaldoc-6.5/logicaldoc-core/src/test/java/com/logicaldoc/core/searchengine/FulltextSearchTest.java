package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.core.AbstractCoreTCase;
import com.logicaldoc.core.document.Document;
import com.logicaldoc.core.security.Folder;

public class FulltextSearchTest extends AbstractCoreTCase {

	protected static Logger log = LoggerFactory.getLogger(FulltextSearchTest.class);

	protected SearchEngine engine;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		engine = (SearchEngine) context.getBean("SearchEngine");
		engine.init();
		addHits();
	}

	@Test
	public void testWrite() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File(tempDir, "query.ser");

		FulltextSearchOptions opt = new FulltextSearchOptions();

		opt.setLanguage("it");
		opt.setExpression("prova test");
		opt.setExpressionLanguage("it");
		opt.setTemplate(1L);
		opt.setSizeMax(3000L);
		opt.setSizeMin(2L);
		opt.setType(SearchOptions.TYPE_FULLTEXT);
		opt.setUserId(1);

		opt.write(file);

		FulltextSearchOptions opt2 = (FulltextSearchOptions) SearchOptions.read(file);

		Assert.assertEquals("prova test", opt2.getExpression());
		Assert.assertEquals("it", opt2.getExpressionLanguage());
		Assert.assertEquals(1, opt2.getTemplate().longValue());
		Assert.assertEquals(3000, opt2.getSizeMax().longValue());
		Assert.assertEquals(2, opt2.getSizeMin().longValue());
		Assert.assertEquals(SearchOptions.TYPE_FULLTEXT, opt2.getType());
		Assert.assertEquals(1, opt2.getUserId());
	}

	private void addHits() throws Exception {
		Document document = new Document();
		document.setId(1L);
		document.setTitle("Document test 1");
		document.setLanguage("it");
		document.setDate(new Date());
		Folder fold = new Folder();
		fold.setId(Folder.DEFAULTWORKSPACE);
		fold.setName("test");
		document.setFolder(fold);

		engine.addHit(document, "Questo è un documento di prova. Per fortuna che esistono i test. document");

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
	
	
		document = new Document();
		document.setId(2L);
		document.setTitle("Document test 2");
		document.setLanguage("en");
		document.setDate(new Date());
		fold = new Folder();
		fold.setId(Folder.DEFAULTWORKSPACE);
		fold.setName("test");
		document.setFolder(fold);
		engine.addHit(document, "Another document");
	}

	@Test
	public void testSearch() throws Exception {
		Assert.assertEquals(3, engine.getCount());
		
		FulltextSearchOptions opt = new FulltextSearchOptions();
		opt.setLanguage("en");
		opt.setExpression("document");
		opt.setFields(new String[] { "content", "title" });
		opt.setExpressionLanguage("en");
		opt.setType(SearchOptions.TYPE_FULLTEXT);
		opt.setUserId(1);

		Search search = new FulltextSearch();
		search.setOptions(opt);

		List<Hit> hits = search.search();
		Assert.assertEquals(2, hits.size());
		
		opt.setMaxHits(1);
		hits = search.search();
		Assert.assertEquals(1, hits.size());
		Assert.assertTrue(search.isMoreHitsPresent());
	}
}