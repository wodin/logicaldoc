package com.logicaldoc.core.searchengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.AbstractCoreTestCase;

public class SearchTest extends AbstractCoreTestCase {

	protected static Log log = LogFactory.getLog(SearchTest.class);

	public SearchTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testSearch() {
		Locale searchLanguage = Locale.ITALIAN;

		SearchOptions opt = new SearchOptions();

		ArrayList<String> languages = new ArrayList<String>();

		String[] langs = (String[]) languages.toArray(new String[languages.size()]);
		opt.setLanguages(langs);

		opt.setQueryStr("prova test", "", "", "");

		Search search = new Search(opt, searchLanguage);
		List<Result> results = search.search();
		log.info(results);

		for (Result result : results) {
			log.info("result.getName() = " + result.getTitle());
		}
	}
}