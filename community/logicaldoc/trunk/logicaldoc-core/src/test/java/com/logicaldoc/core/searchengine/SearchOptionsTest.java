package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;

public class SearchOptionsTest extends AbstractCoreTestCase {

	protected static Log log = LogFactory.getLog(SearchOptionsTest.class);

	@Test
	public void testWrite() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File(tempDir, "query.ser");

		SearchOptions opt = new SearchOptions();

		opt.setLanguage("it");

		opt.setQueryStr("prova test");
		opt.setQueryLanguage("italiano");
		opt.setTemplate(1L);
		opt.setSizeMax(3000L);
		opt.setSizeMin(2L);
		opt.setType(SearchOptions.TYPE_FULLTEXT);
		opt.setUserId(1);

		opt.write(file);

		SearchOptions opt2 = SearchOptions.read(file);

		Assert.assertEquals("prova test", opt2.getQueryStr());
		Assert.assertEquals("italiano", opt2.getQueryLanguage());
		Assert.assertEquals(1, opt2.getTemplate().longValue());
		Assert.assertEquals(3000, opt2.getSizeMax().longValue());
		Assert.assertEquals(2, opt2.getSizeMin().longValue());
		Assert.assertEquals(SearchOptions.TYPE_FULLTEXT, opt2.getType());
		Assert.assertEquals(1, opt2.getUserId());
	}
}
