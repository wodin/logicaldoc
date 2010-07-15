package com.logicaldoc.core.searchengine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;

public class FulltextSearchOptionsTest extends AbstractCoreTestCase {

	@Test
	public void testWrite() throws FileNotFoundException, IOException, ClassNotFoundException {
		File file = new File(tempDir, "query.ser");

		FulltextSearchOptions opt = new FulltextSearchOptions();

		opt.setLanguage("it");

		opt.setExpression("prova test");
		opt.setExpressionLanguage("italiano");
		opt.setTemplate(1L);
		opt.setSizeMax(3000L);
		opt.setSizeMin(2L);
		opt.setType(SearchOptions.TYPE_FULLTEXT);
		opt.setUserId(1);

		opt.write(file);

		FulltextSearchOptions opt2 = (FulltextSearchOptions) SearchOptions.read(file);

		Assert.assertEquals("prova test", opt2.getExpression());
		Assert.assertEquals("italiano", opt2.getExpressionLanguage());
		Assert.assertEquals(1, opt2.getTemplate().longValue());
		Assert.assertEquals(3000, opt2.getSizeMax().longValue());
		Assert.assertEquals(2, opt2.getSizeMin().longValue());
		Assert.assertEquals(SearchOptions.TYPE_FULLTEXT, opt2.getType());
		Assert.assertEquals(1, opt2.getUserId());
	}
}
