package com.logicaldoc.core.searchengine;

import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;

public class TagSearchTest extends AbstractCoreTCase {

	protected static Log log = LogFactory.getLog(TagSearchTest.class);

	@Test
	public void testSearch() {
		SearchOptions opt = new SearchOptions();
		opt.setType(1);
		Assert.assertEquals(1, opt.getType());
		opt.setUserId(1);
		opt.setExpression("abc");

		TagSearch search = new TagSearch();
		search.setOptions(opt);
		try {
			search.search();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		List<Hit> results = search.getHits();
		Assert.assertEquals(2, results.size());
		Assert.assertEquals(1, results.get(0).getDocId());
		Assert.assertEquals(1, results.get(1).getDocId());
		
		opt = new SearchOptions();
		opt.setType(1);
		Assert.assertEquals(1, opt.getType());
		opt.setUserId(1);
		opt.setExpression("abc");
		opt.setMaxHits(1);

		search = new TagSearch();
		search.setOptions(opt);
		try {
			search.search();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		results = search.getHits();
		Assert.assertEquals(2, results.size());
		Assert.assertEquals(1, results.get(0).getDocId());
	}
}