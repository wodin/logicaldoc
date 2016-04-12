package com.logicaldoc.webservice;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.logicaldoc.webservice.model.WSDocument;

public class AlfRest {

	public static String BASE_PATH = "http://localhost:8080/logicaldoc";
	
	public static void main(String[] args) throws HttpException, IOException {

		String sid = loginJSON();
		
		//createFolderSimple(sid);
		//createFolderSimpleJSON(sid);
		
		//createDocumentJSON(sid);
		createDocumentJSON( sid);
	}
	
	private static void createDocumentJSON(String sid) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		HttpConnectionParams params = client.getHttpConnectionManager().getParams();
		params.setConnectionTimeout(30 * 1000);
		params.setSoTimeout(20 * 1000);
  
       //replace the host name and port with the your host and port.
  	    PostMethod method = new PostMethod(BASE_PATH +"/services/rest/document/create");
  	    method.setRequestHeader("Accept", "application/json");	    

  	    ArrayList<Part> mparts = new ArrayList<Part>();
  	    StringPart part1 = new StringPart("sid", sid);
  	    part1.setContentType("plain/text");
  	    
  	    File f = new File("C:/tmp/InvoiceProcessing01-workflow.png");
  	    System.out.println(f.getName());
  	  
  	    //FilePart part3 = new FilePart(f.getName(), f);
  	    FilePart part2 = new FilePart("content", f.getName(), f);  	    
  	 	part2.setContentType(MediaType.APPLICATION_OCTET_STREAM);
  	 	
		WSDocument wsDoc = new WSDocument();
		wsDoc.setId(0);
		wsDoc.setTitle("document test");
		wsDoc.setCustomId("xxxxx4444");
		wsDoc.setTags(new String[]{"Invoice", "Processing", "workflow"});
		wsDoc.setFolderId(4L);
		wsDoc.setFileName(f.getName());  	      	      	
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String jsonStr = ow.writeValueAsString(wsDoc);
		
		// Alternatively you can just write your piece of simple json
  	    //String jsonStr = "{ \"sid\" : \"" + sid +"\", \"folderPath\" : \"" + folderPath +"\" }";		
		
  	    System.out.println(jsonStr);
  	    StringPart part3 = new StringPart("document", jsonStr);
  	    part3.setContentType("application/json");  	 	
  	    
  	    mparts.add(part1);
  	    mparts.add(part2);
  	    mparts.add(part3);
  	    
  	    Part[] parts = new Part[0];
  	    parts = mparts.toArray(parts);
  	    
  	  	method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));	    
  	   
  	    int statusCode = client.executeMethod(method);
  
  	    if (statusCode != HttpStatus.SC_OK) {
  	    	System.err.println("Method failed: " + method.getStatusLine());
  	    } 

  	    System.out.println(method.getResponseBodyAsString());
	}

	private static void createFolderSimple(String sid) throws UnsupportedEncodingException, IOException, HttpException {
		
		HttpClient client = new HttpClient();
		HttpConnectionParams params = client.getHttpConnectionManager().getParams();
		params.setConnectionTimeout(30 * 1000);
		params.setSoTimeout(20 * 1000);
  
       //replace the host name and port with the your host and port.
  	    PostMethod method = new PostMethod(BASE_PATH +"/services/rest/folder/createSimple");
  	  
  	    method.setRequestHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED);
  	    method.setRequestHeader("Accept", "application/json");
  	    method.setParameter("sid", sid);
  	    method.setParameter("folderPath", "/LogicalDOC/USA/NJ/Fair Lawn/createSimple");

  	    int statusCode = client.executeMethod(method);
  
  	    if (statusCode != HttpStatus.SC_OK) {
  	    	System.err.println("Method failed: " + method.getStatusLine());
  	    } 

  	    System.out.println(method.getResponseBodyAsString());
	}
	
	private static void createFolderSimpleJSON(String sid) throws UnsupportedEncodingException, IOException, HttpException {
		
		HttpClient client = new HttpClient();
		HttpConnectionParams params = client.getHttpConnectionManager().getParams();
		params.setConnectionTimeout(30 * 1000);
		params.setSoTimeout(20 * 1000);
  
       //replace the host name and port with the your host and port.
  	    PostMethod method = new PostMethod(BASE_PATH +"/services/rest/folder/createSimple");
  	  
  	    String folderPath = "/LogicalDOC/USA/NJ/Fair Lawn/createSimple";
  	    String input = "{ \"sid\" : \"" + sid +"\", \"folderPath\" : \"" + folderPath +"\" }";  	      	
  	    System.out.println(input);
  	  
  	    method.setRequestEntity(new StringRequestEntity(input, "application/json", null));
  	    method.setRequestHeader("Accept", "application/json");

  	    int statusCode = client.executeMethod(method);
  
  	    if (statusCode != HttpStatus.SC_OK) {
  	    	System.err.println("Method failed: " + method.getStatusLine());
  	    } 

  	    System.out.println(method.getResponseBodyAsString());
	}	
	
	private static String loginJSON() throws UnsupportedEncodingException, IOException, HttpException {
		
		HttpClient client = new HttpClient();
		HttpConnectionParams params = client.getHttpConnectionManager().getParams();
		params.setConnectionTimeout(30 * 1000);
		params.setSoTimeout(20 * 1000);
		
		params.setParameter("username", "admin");
		params.setParameter("password", "admin");
  
       //replace the host name and port with the your host and port.
  	    PostMethod method = new PostMethod(BASE_PATH +"/services/rest/auth/login");
  	    method.setRequestHeader("Accept", "application/json");
  		
  	    String input = "{ \"username\" : \"" + params.getParameter("username") +"\", \"password\" : \"" + params.getParameter("password") +"\" }";  	      	
  	    System.out.println(input);
  	  
  	    method.setRequestEntity(new StringRequestEntity(input, "application/json", null));
  	    int statusCode = client.executeMethod(method);
  
  	    if (statusCode != HttpStatus.SC_OK) {
  	    	System.err.println("Method failed: " + method.getStatusLine());
  	    } 

  	    String responseStr = method.getResponseBodyAsString();
  	    System.out.println(responseStr);
  	    return responseStr;
	}

}
