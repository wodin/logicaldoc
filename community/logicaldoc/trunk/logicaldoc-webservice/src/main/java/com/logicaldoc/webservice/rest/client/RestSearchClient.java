package com.logicaldoc.webservice.rest.client;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.cxf.common.util.Base64Utility;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.AttachmentBuilder;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.rest.SearchService;

public class RestSearchClient extends AbstractRestClient {

	protected static Logger log = LoggerFactory.getLogger(RestSearchClient.class);

	SearchService proxy = null;

	public RestSearchClient(String endpoint, String username, String password) {
		this(endpoint, username, password, -1);
	}

	/**
	 * Constructor
	 * 
	 * @param endpoint  Connection URL
	 * @param username
	 * @param password
	 * @param timeout Timeout for the RESTful requests
	 */
	public RestSearchClient(String endpoint, String username, String password, int timeout) {
		super(endpoint, username, password, timeout);

		JacksonJsonProvider provider = new JacksonJsonProvider();
		
		if ((username == null) || (password == null)) {
			proxy = JAXRSClientFactory.create(endpoint, SearchService.class, Arrays.asList(provider));
		} else {
			proxy = JAXRSClientFactory.create(endpoint, SearchService.class, Arrays.asList(provider), username, password, null);
		}
		
		if (timeout > 0) {
			HTTPConduit conduit = WebClient.getConfig(proxy).getHttpConduit();
			HTTPClientPolicy policy = new HTTPClientPolicy();
			policy.setReceiveTimeout(timeout);
			conduit.setClient(policy);
		}
	}

	public WSSearchResult find(WSSearchOptions owd) throws Exception {
		
//		if (credentials != null) {
//			String authorizationHeader = "Basic " + Base64Utility.encode(credentials.getBytes());
//			WebClient.client(proxy).header("Authorization", authorizationHeader);
//		}	
		
		WebClient.client(proxy).type("multipart/form-data");
		WebClient.client(proxy).accept("application/json");
		// WebClient.client(proxy).accept("*/*"); // This also works

		// ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(owd);

		Attachment optAttachment = new AttachmentBuilder().id("opt").object(jsonStr).mediaType("application/json")
				.contentDisposition(new ContentDisposition("form-data; name=\"opt\"")).build();

		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(optAttachment);

		return proxy.find(atts);
	}

}
