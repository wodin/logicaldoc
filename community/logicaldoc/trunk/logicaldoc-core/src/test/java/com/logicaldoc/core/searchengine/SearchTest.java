package com.logicaldoc.core.searchengine;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTestCase;

public class SearchTest extends AbstractCoreTestCase {

	protected static Log log = LogFactory.getLog(SearchTest.class);

	@Test
	public void testSearch() {
		SearchOptions opt = new SearchOptions();
		opt.setLanguage(null);
		opt.setQueryStr("prova test", "", "", "");

		Search search = new Search(opt);
		List<Result> results = search.search();
		log.info(results);

		for (Result result : results) {
			log.info("result.getName() = " + result.getTitle());
		}
	}
}