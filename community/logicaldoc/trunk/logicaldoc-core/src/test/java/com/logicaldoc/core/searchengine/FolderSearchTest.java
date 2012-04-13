package com.logicaldoc.core.searchengine;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.logicaldoc.core.AbstractCoreTCase;

public class FolderSearchTest extends AbstractCoreTCase {

	protected static Log log = LogFactory.getLog(FolderSearchTest.class);

	@Test
	public void testSearch() {
		FolderSearchOptions opt = new FolderSearchOptions();
		opt.setUserId(1);
		opt.setFolderName("AbC");

		FolderSearch search = new FolderSearch();
		search.setOptions(opt);
		try {
			search.search();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		List<Hit> results = search.getHits();
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(1201, results.get(0).getId());
		Assert.assertEquals(1200, results.get(0).getFolder().getId());

		opt.setUserId(5);
		search = new FolderSearch();
		search.setOptions(opt);
		try {
			search.search();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		results = search.getHits();
		Assert.assertEquals(0, results.size());

		opt.setUserId(1);
		opt.setFolderId(8L);
		opt.setSearchInSubPath(true);
		opt.setDepth(5);
		opt.setFolderName("AbC");
		search = new FolderSearch();
		search.setOptions(opt);
		search.search();
		results = search.getHits();
		Assert.assertEquals(0, results.size());

		opt.setFolderId(5L);
		search.search();
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(1201, results.get(0).getId());
		Assert.assertEquals(1200, results.get(0).getFolder().getId());

		opt.setCreationFrom(new Date());
		search.search();
		Assert.assertEquals(0, results.size());

		opt.setCreationFrom(null);
		opt.setCreationTo(new Date());
		search.search();
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(1201, results.get(0).getId());
		Assert.assertEquals(1200, results.get(0).getFolder().getId());

		opt.setFolderDescription("cocco");
		search.search();
		Assert.assertEquals(0, results.size());

		opt.setFolderDescription("EsT");
		search.search();
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(1201, results.get(0).getId());
		Assert.assertEquals(1200, results.get(0).getFolder().getId());
	}
}