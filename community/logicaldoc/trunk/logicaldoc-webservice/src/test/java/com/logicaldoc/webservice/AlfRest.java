package com.logicaldoc.webservice;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.logicaldoc.webservice.model.WSDocument;

public class AlfRest {

	public static String BASE_PATH = "http://localhost:8080/logicaldoc";
	
	public static void main(String[] args) throws HttpException, IOException {

		String sid = loginJSON();
		
		//createFolderSimple(sid);
		//createFolderSimpleJSON(sid);
		
		//createDocumentJSON( sid);
		//listChildrean(sid, 04L);
	}
	
	private static void listChildrean(String sid, long parentId) throws ClientProtocolException, IOException {
		
		System.out.println("sid: " +sid);
		
		List<NameValuePair> params = new LinkedList<NameValuePair>();
		params.add(new BasicNameValuePair("sid", sid));
		params.add(new BasicNameValuePair("folderId", String.valueOf(parentId)));

		StringBuilder requestUrl = new StringBuilder(BASE_PATH + "/services/rest/folder/listChildren");
		String querystring = URLEncodedUtils.format(params, "utf-8");
		requestUrl.append("?");
		requestUrl.append(querystring);
		
		System.out.println(requestUrl);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet method = new HttpGet(requestUrl.toString());
		method.setHeader("Accept", "application/json");
		
		CloseableHttpResponse response1 = httpclient.execute(method);
		
		try {
		    System.out.println(response1.getStatusLine());
		    HttpEntity entity2 = response1.getEntity();
		    
		    String respoBody = EntityUtils.toString(entity2, "UTF-8");
		    System.out.println(respoBody);
		    
		    // do something useful with the response body
		    // and ensure it is fully consumed
		    EntityUtils.consume(entity2);
		} finally {
			response1.close();
		}		
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
		
		// Alternatively you can just write your JSON String
  	    //String jsonStr = "{ \"title\" : \"" + document test +"\", \"fileName\" : \"" + InvoiceProcessing01-workflow.png +"\", \"customId\" : \"myXXXXX\"}";		
		
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
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		String input = "{ \"username\" : \"admin\", \"password\" : \"admin\" }";  	      	
  	    System.out.println(input);	
  	    
  	  StringEntity entity = new StringEntity(input, ContentType.create("application/json", Consts.UTF_8));
  	  HttpPost httppost = new HttpPost(BASE_PATH +"/services/rest/auth/login");
  	  httppost.setEntity(entity);

  	CloseableHttpResponse response = httpclient.execute(httppost);
  	try {
  	    HttpEntity rent = response.getEntity();
  	    if (rent != null) {
		    String respoBody = EntityUtils.toString(rent, "UTF-8");
		    System.out.println(respoBody);
		    return respoBody;
  	    }
  	} finally {
  	    response.close();
  	}  	  
  	
  	 return null;
	}

}
