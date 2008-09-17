package com.logicaldoc.core.searchengine;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.AbstractCoreTestCase;
import com.logicaldoc.core.searchengine.Result;
import com.logicaldoc.core.searchengine.Search;
import com.logicaldoc.core.searchengine.SearchOptions;

public class SearchTest extends AbstractCoreTestCase {

	protected static Log log = LogFactory.getLog(SearchTest.class);

	public SearchTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testSearch() {

		String language = "it";
		String searchLanguage = "all".equals(language) ? "it" : "it";

		SearchOptions opt = new SearchOptions();

		ArrayList<String> languages = new ArrayList<String>();

		if ("all".equals(language)) {
			languages.add("en");
			languages.add("it");
			languages.add("fr");
			languages.add("de");
			languages.add("es");
		} else {
			languages.add(language);
		}

		String[] langs = (String[]) languages.toArray(new String[languages.size()]);
		opt.setLanguages(langs);

		opt.setQueryStr("prova test", "", "", "");

		Search search = new Search(opt, searchLanguage);
		List<Result> results = search.search();
		log.info(results);

		for (Result result : results) {
			log.info("result.getName() = " + result.getName());
		}
	}
}