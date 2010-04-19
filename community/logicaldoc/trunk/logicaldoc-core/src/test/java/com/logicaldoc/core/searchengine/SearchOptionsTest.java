package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.AbstractCoreTestCase;

public class SearchOptionsTest extends AbstractCoreTestCase {

	protected static Log log = LogFactory.getLog(SearchOptionsTest.class);

	public SearchOptionsTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWrite() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File(tempDir, "query.ser");

		SearchOptions opt = new SearchOptions();

		String[] langs = new String[] { "it", "en", "de" };
		opt.setLanguages(langs);

		opt.setQueryStr("prova test");
		opt.setQueryLanguage("italiano");
		opt.setTemplate(1L);
		opt.setSizeMax(3000L);
		opt.setSizeMin(2L);
		opt.setType(SearchOptions.TYPE_FULLTEXT);
		opt.setUserId(1);

		opt.write(file);

		SearchOptions opt2 = SearchOptions.read(file);

		assertEquals("prova test", opt2.getQueryStr());
		assertEquals("italiano", opt2.getQueryLanguage());
		assertEquals(1, opt2.getTemplate().longValue());
		assertEquals(3000, opt2.getSizeMax().longValue());
		assertEquals(2, opt2.getSizeMin().longValue());
		assertEquals(SearchOptions.TYPE_FULLTEXT, opt2.getType());
		assertEquals(1, opt2.getUserId());
		for (int i = 0; i < langs.length; i++) {
			assertEquals(opt.getLanguages()[i], opt2.getLanguages()[i]);
		}
		assertEquals("it", opt2.getLanguages()[0]);
	}
}
