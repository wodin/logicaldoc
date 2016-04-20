package com.logicaldoc.webservice.rest.client;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.AttachmentBuilder;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
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
	
	public RestSearchClient(String endpoint) {
		super(endpoint);
		
        JacksonJsonProvider provider = new JacksonJsonProvider();	
		proxy = JAXRSClientFactory.create(endpoint, SearchService.class, Arrays.asList(provider));
	}
	
	public RestSearchClient(String endpoint, int timeout) {
		super(endpoint, timeout);
		
        JacksonJsonProvider provider = new JacksonJsonProvider();	
		proxy = JAXRSClientFactory.create(endpoint, SearchService.class, Arrays.asList(provider));		
	}
    

	public WSSearchResult find(String sid, WSSearchOptions owd) throws Exception {
			
        WebClient.client(proxy).type("multipart/form-data");
		WebClient.client(proxy).accept("application/json");
		//WebClient.client(proxy).accept("*/*"); // This also works
        
		//ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(owd);		      
        		
        Attachment sidAttachment = new AttachmentBuilder().id("sid").object(sid).contentDisposition(new ContentDisposition("form-data; name=\"sid\"")).build();
		Attachment optAttachment = new AttachmentBuilder().id("opt").object(jsonStr).mediaType("application/json").contentDisposition(new ContentDisposition("form-data; name=\"opt\"")).build();
		
		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(sidAttachment);
		atts.add(optAttachment);

		return proxy.find(atts);
	}

}
