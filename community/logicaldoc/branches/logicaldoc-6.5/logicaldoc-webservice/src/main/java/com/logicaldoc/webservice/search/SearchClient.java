package com.logicaldoc.webservice.search;

import java.io.IOException;

import com.logicaldoc.core.document.TagCloud;
import com.logicaldoc.core.searchengine.FulltextSearchOptions;
import com.logicaldoc.webservice.AbstractClient;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * Search Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class SearchClient extends AbstractClient<SearchService> implements SearchService {

	public SearchClient(String endpoint, int gzipThreshold, boolean log) throws IOException {
		super(endpoint, SearchService.class, gzipThreshold, log);
	}

	public SearchClient(String endpoint) throws IOException {
		super(endpoint, SearchService.class, -1, true);
	}

	@Override
	public WSSearchResult find(String sid, FulltextSearchOptions options) throws Exception {
		return client.find(sid, options);
	}

	@Override
	public WSDocument[] findByFilename(String sid, String filename) throws Exception {
		return client.findByFilename(sid, filename);
	}

	@Override
	public WSDocument[] findByTag(String sid, String tag) throws Exception {
		return client.findByTag(sid, tag);
	}

	@Override
	public WSFolder[] findFolders(String sid, String name) throws Exception {
		return client.findFolders(sid, name);
	}

	@Override
	public TagCloud[] getTagCloud(String sid) throws Exception {
		return client.getTagCloud(sid);
	}

	@Override
	public String[] getTags(String sid) throws Exception {
		return client.getTags(sid);
	}
}