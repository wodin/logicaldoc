package com.logicaldoc.webservice.soap.client;

import java.io.IOException;

import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.model.WSTagCloud;
import com.logicaldoc.webservice.services.SearchService;

/**
 * Search Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class SoapSearchClient extends SoapClient<SearchService> implements SearchService {

	public SoapSearchClient(String endpoint, int gzipThreshold, boolean log, int timeout) throws IOException {
		super(endpoint, SearchService.class, gzipThreshold, log, timeout);
	}

	public SoapSearchClient(String endpoint) throws IOException {
		super(endpoint, SearchService.class, -1, true, -1);
	}

	@Override
	public WSSearchResult find(String sid, WSSearchOptions options) throws Exception {
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
	public WSTagCloud[] getTagCloud(String sid) throws Exception {
		return client.getTagCloud(sid);
	}

	@Override
	public String[] getTags(String sid) throws Exception {
		return client.getTags(sid);
	}
}