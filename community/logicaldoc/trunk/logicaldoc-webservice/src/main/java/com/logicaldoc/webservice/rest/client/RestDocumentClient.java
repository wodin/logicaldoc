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
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
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
		
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
		return proxy.list(sid, folderId);
	}
	
	public WSDocument[] listDocuments(String sid, long folderId, String fileName) throws Exception {

        WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
		return proxy.listDocuments(sid, folderId, fileName);
	}	
	
	
	/**
	 * This method can produce errors because sometimes CXF-rest is not able to recognize the attendend body type
	 * 
	 *  [Loader-Update] 22.04.2016 12:04:16,044 ERROR (com.logicaldoc.bm.AbstractLoader:  85) - No message body writer has been found for class java.util.List, ContentType: *\/*
org.apache.cxf.interceptor.Fault: No message body writer has been found for class java.util.List, ContentType: *\/*
        at org.apache.cxf.jaxrs.client.ClientProxyImpl$BodyWriter.doWriteBody(ClientProxyImpl.java:882)
        at org.apache.cxf.jaxrs.client.AbstractClient$AbstractBodyWriter.handleMessage(AbstractClient.java:1094)
        at org.apache.cxf.phase.PhaseInterceptorChain.doIntercept(PhaseInterceptorChain.java:308)
        at org.apache.cxf.jaxrs.client.AbstractClient.doRunInterceptorChain(AbstractClient.java:652)
        at org.apache.cxf.jaxrs.client.ClientProxyImpl.doChainedInvocation(ClientProxyImpl.java:747)
        at org.apache.cxf.jaxrs.client.ClientProxyImpl.invoke(ClientProxyImpl.java:228)
        at com.sun.proxy.$Proxy20.update(Unknown Source)
        at com.logicaldoc.webservice.rest.client.RestDocumentClient.update(RestDocumentClient.java:138)
        at com.logicaldoc.bm.RestServerProxy.update(RestServerProxy.java:67)
        at com.logicaldoc.bm.loaders.Update.updateDocument(Update.java:120)
        at com.logicaldoc.bm.loaders.Update.doLoading(Update.java:79)
        at com.logicaldoc.bm.AbstractLoader.run(AbstractLoader.java:77)
Caused by: javax.ws.rs.ProcessingException: No message body writer has been found for class java.util.List, ContentType: *\/*
        at org.apache.cxf.jaxrs.client.AbstractClient.reportMessageHandlerProblem(AbstractClient.java:783)
        at org.apache.cxf.jaxrs.client.AbstractClient.writeBody(AbstractClient.java:494)
        at org.apache.cxf.jaxrs.client.ClientProxyImpl$BodyWriter.doWriteBody(ClientProxyImpl.java:872)
        ... 11 more
	 * 
	 * @param sid
	 * @param document
	 * @throws Exception
	 */
	public void updateBAD(String sid, WSDocument document) throws Exception {
		
		log.info("update docID: {}", document.getId() );
		WebClient.client(proxy).type(MediaType.MULTIPART_FORM_DATA);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
        
		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(document);		      
        
        Attachment sidAttachment = new AttachmentBuilder().id("sid").object(sid).contentDisposition(new ContentDisposition("form-data; name=\"sid\"")).build();
		Attachment docAttachment = new AttachmentBuilder().id("document").object(jsonStr).mediaType("application/json").contentDisposition(new ContentDisposition("form-data; name=\"document\"")).build();
		
		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(sidAttachment);
		atts.add(docAttachment);
        
		proxy.update(atts);
	}	
	
	public void update(String sid, WSDocument wsDoc) throws Exception {

		log.debug("update docID: {}", wsDoc.getId() );
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
        HttpPost httppost = new HttpPost(endpoint + "/update");
        httppost.addHeader(new BasicHeader("Accept", "application/json"));

        ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(wsDoc);		
        
		StringBody sidPart = new StringBody(sid, ContentType.TEXT_PLAIN);
		StringBody jsonPart = new StringBody(jsonStr, ContentType.APPLICATION_JSON);      

        HttpEntity reqEntity = MultipartEntityBuilder.create()
        		.addPart("sid", sidPart)
                .addPart("document", jsonPart)
                .build();
		
		httppost.setEntity(reqEntity);
		
		CloseableHttpResponse response = httpclient.execute(httppost);
		try {
			int code = response.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				EntityUtils.consumeQuietly(response.getEntity());
				/*
				HttpEntity rent = response.getEntity();
				if (rent != null) {
					String respoBody = EntityUtils.toString(rent, "UTF-8");
					//System.out.println(respoBody);
				} */
			} else if (code == HttpStatus.SC_NO_CONTENT) {
				// it is also OK
			} else {
				log.warn("status code is invalid: {}", code);
				throw new Exception(response.getStatusLine().getReasonPhrase());
			}
		} finally {
			response.close();
		}
	}	

	public WSDocument getDocument(String sid, long docId) throws Exception {
		
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
		//WebClient.client(proxy).accept(MediaType.APPLICATION_XML);
		
		return proxy.getDocument(sid, docId);
	}	
	
	public void delete(String sid, long docId) throws Exception {
		
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
		
		proxy.delete(sid, docId);
	}
	
	public DataHandler getContent(String sid, long docId) throws Exception {
		
		//WebClient.client(proxy).type("*/*");
		WebClient.client(proxy).accept(MediaType.APPLICATION_OCTET_STREAM);
		
		return proxy.getContent(sid, docId);
	}
	
}
