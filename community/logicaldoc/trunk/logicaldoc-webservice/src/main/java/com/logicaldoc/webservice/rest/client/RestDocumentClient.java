package com.logicaldoc.webservice.rest.client;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;

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

import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.rest.DocumentService; 

public class RestDocumentClient extends AbstractRestClient {

	protected static Logger log = LoggerFactory.getLogger(RestDocumentClient.class);

	public RestDocumentClient(String endpoint) {
		super(endpoint);
	}
	
	public RestDocumentClient(String endpoint, int timeout) {
		super(endpoint, timeout);
	}

	public WSDocument[] list(String sid, long folderId) throws Exception {
				
        System.out.println("Sending request to the service");
        System.out.println("endpoint: " +endpoint);

        JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.addUntouchable(WSDocument.class);
        
        DocumentService proxy = JAXRSClientFactory.create(endpoint, DocumentService.class, Arrays.asList(provider));
        WebClient.client(proxy).type("*/*");
        
		return proxy.list(sid, folderId);
	}
	
	public WSDocument[] listDocuments(String sid, long folderId, String fileName) throws Exception {
		
        System.out.println("Sending request to the service");
        System.out.println("endpoint: " +endpoint);

        JacksonJsonProvider provider = new JacksonJsonProvider();
        provider.addUntouchable(WSDocument.class);
        
        DocumentService proxy = JAXRSClientFactory.create(endpoint, DocumentService.class, Arrays.asList(provider));
        WebClient.client(proxy).type("*/*");
        //WebClient.client(proxy).header("Content-Type", "*/*");
        //WebClient.client(proxy).accept("application/json");
        
		return proxy.listDocuments(sid, folderId, fileName);
	}	
	
	
	public void update(String sid, WSDocument document) throws Exception {
		
        JacksonJsonProvider provider = new JacksonJsonProvider();	
        
        DocumentService proxy = JAXRSClientFactory.create(endpoint, DocumentService.class, Arrays.asList(provider));
		WebClient.client(proxy).type(MediaType.MULTIPART_FORM_DATA);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
        
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(document);		      
        
        Attachment sidAttachment = new AttachmentBuilder().id("sid").object(sid).contentDisposition(new ContentDisposition("form-data; name=\"sid\"")).build();
		Attachment fldAttachment = new AttachmentBuilder().id("document").object(jsonStr).mediaType("application/json").contentDisposition(new ContentDisposition("form-data; name=\"folder\"")).build();
		
		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(sidAttachment);
		atts.add(fldAttachment);
        
		proxy.update(atts);
	}	
}
