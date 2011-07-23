package com.logicaldoc.webservice.search;

import java.io.IOException;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.logicaldoc.core.document.TagCloud;
import com.logicaldoc.core.searchengine.FulltextSearchOptions;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.folder.WSFolder;

/**
 * Search Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class SearchClient implements SearchService {

	private SearchService client;

	public SearchClient(String endpoint) throws IOException {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.setServiceClass(SearchService.class);
		factory.setAddress(endpoint);
		client = (SearchService) factory.create();
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
