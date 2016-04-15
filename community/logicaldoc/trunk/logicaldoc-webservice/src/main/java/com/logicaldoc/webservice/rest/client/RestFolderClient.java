package com.logicaldoc.webservice.rest.client;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
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
import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.rest.FolderService;
import com.logicaldoc.webservice.rest.endpoint.RestFolderService;

public class RestFolderClient extends AbstractRestClient {

	protected static Logger log = LoggerFactory.getLogger(RestFolderClient.class);

	public RestFolderClient(String endpoint) {
		super(endpoint);
	}
	
	public RestFolderClient(String endpoint, int timeout) {
		super(endpoint, timeout);
	}
	
	public WSFolder[] listChildren(String sid, long folderId) throws Exception {

        JacksonJsonProvider provider = new JacksonJsonProvider();
        
        FolderService proxy = JAXRSClientFactory.create(endpoint, FolderService.class, Arrays.asList(provider));
        WebClient.client(proxy).type("*/*");
        //WebClient.client(proxy).accept("application/json");
        
		return proxy.listChildren(sid, folderId);
	}
	
	public WSFolder create(String sid, WSFolder folder) throws Exception {
				
        JacksonJsonProvider provider = new JacksonJsonProvider();	
        
        FolderService proxy = JAXRSClientFactory.create(endpoint, FolderService.class, Arrays.asList(provider));
		WebClient.client(proxy).type(MediaType.MULTIPART_FORM_DATA);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
        
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(folder);		      
        
        Attachment sidAttachment = new AttachmentBuilder().id("sid").object(sid).contentDisposition(new ContentDisposition("form-data; name=\"sid\"")).build();
		Attachment fldAttachment = new AttachmentBuilder().id("folder").object(jsonStr).mediaType("application/json").contentDisposition(new ContentDisposition("form-data; name=\"folder\"")).build();
		
		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(sidAttachment);
		atts.add(fldAttachment);
        
		return proxy.create(atts);
	}
	
	public WSFolder createPath(String sid, long rootFolder, String path) throws Exception {
		
        JacksonJsonProvider provider = new JacksonJsonProvider();
        
        FolderService proxy = JAXRSClientFactory.create(endpoint, FolderService.class, Arrays.asList(provider));
        WebClient.client(proxy).type(MediaType.APPLICATION_FORM_URLENCODED);
        WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);      
        
		return proxy.createPath(sid, rootFolder, path);	
	}	

}
