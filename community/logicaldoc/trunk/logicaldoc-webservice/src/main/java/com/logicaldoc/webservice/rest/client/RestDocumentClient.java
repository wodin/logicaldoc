package com.logicaldoc.webservice.rest.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.activation.DataHandler;
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
	
	DocumentService proxy = null;

	public RestDocumentClient(String endpoint) {
		super(endpoint);
		
        JacksonJsonProvider provider = new JacksonJsonProvider();	        
        proxy = JAXRSClientFactory.create(endpoint, DocumentService.class, Arrays.asList(provider));		
	}
	
	public RestDocumentClient(String endpoint, int timeout) {
		super(endpoint, timeout);
		
        JacksonJsonProvider provider = new JacksonJsonProvider();	        
        proxy = JAXRSClientFactory.create(endpoint, DocumentService.class, Arrays.asList(provider));	
	}
	
	public WSDocument create(String sid, WSDocument document, File packageFile) throws Exception {
		
		WebClient.client(proxy).type(MediaType.MULTIPART_FORM_DATA);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
        
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(document);		      
        
        Attachment sidAttachment = new AttachmentBuilder().id("sid").object(sid).contentDisposition(new ContentDisposition("form-data; name=\"sid\"")).build();
		Attachment docAttachment = new AttachmentBuilder().id("document").object(jsonStr).mediaType("application/json").contentDisposition(new ContentDisposition("form-data; name=\"document\"")).build();
		Attachment fileAttachment = new Attachment("content", new FileInputStream(packageFile), new ContentDisposition("form-data; name=\"content\"; filename=\"" + packageFile.getName() + "\""));
		
		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(sidAttachment);
		atts.add(docAttachment);
		atts.add(fileAttachment);
        
		return proxy.create(atts);
	}
	
	public WSDocument create(String sid, WSDocument document, DataHandler dataHandler) throws Exception {
		
		WebClient.client(proxy).type(MediaType.MULTIPART_FORM_DATA);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
        
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(document);		      
        
        Attachment sidAttachment = new AttachmentBuilder().id("sid").object(sid).contentDisposition(new ContentDisposition("form-data; name=\"sid\"")).build();
		Attachment docAttachment = new AttachmentBuilder().id("document").object(jsonStr).mediaType("application/json").contentDisposition(new ContentDisposition("form-data; name=\"document\"")).build();
		Attachment fileAttachment = new AttachmentBuilder().id("content").dataHandler(dataHandler).mediaType("application/octet-stream").contentDisposition(new ContentDisposition("form-data; name=\"content\"")).build();
		
		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(sidAttachment);
		atts.add(docAttachment);
		atts.add(fileAttachment);
        
		return proxy.create(atts);
	}			

	public WSDocument[] list(String sid, long folderId) throws Exception {

        WebClient.client(proxy).type("*/*");
        
		return proxy.list(sid, folderId);
	}
	
	public WSDocument[] listDocuments(String sid, long folderId, String fileName) throws Exception {

        WebClient.client(proxy).type("*/*");
        //WebClient.client(proxy).header("Content-Type", "*/*");
        //WebClient.client(proxy).accept("application/json");
        
		return proxy.listDocuments(sid, folderId, fileName);
	}	
	
	
	public void update(String sid, WSDocument document) throws Exception {
		
		WebClient.client(proxy).type(MediaType.MULTIPART_FORM_DATA);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
        
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(document);		      
        
        Attachment sidAttachment = new AttachmentBuilder().id("sid").object(sid).contentDisposition(new ContentDisposition("form-data; name=\"sid\"")).build();
		Attachment fldAttachment = new AttachmentBuilder().id("document").object(jsonStr).mediaType("application/json").contentDisposition(new ContentDisposition("form-data; name=\"document\"")).build();
		
		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(sidAttachment);
		atts.add(fldAttachment);
        
		proxy.update(atts);
	}

	public WSDocument getDocument(String sid, long docId) throws Exception {
		
		WebClient.client(proxy).type("*/*");
		//WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
		WebClient.client(proxy).accept(MediaType.APPLICATION_XML);
		
		return proxy.getDocument(sid, docId);
	}	
}
