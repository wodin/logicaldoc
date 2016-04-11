package com.logicaldoc.webservice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionParams;

public class AlfRest {

	public static void main(String[] args) throws HttpException, IOException {

		String sid = loginJSON();
		
		createFolderSimple(sid);
		createFolderSimpleJSON(sid);
	}
	
	private static void createFolderSimple(String sid) throws UnsupportedEncodingException, IOException, HttpException {
		
		HttpClient client = new HttpClient();
		HttpConnectionParams params = client.getHttpConnectionManager().getParams();
		params.setConnectionTimeout(30 * 1000);
		params.setSoTimeout(20 * 1000);
  
       //replace the host name and port with the your host and port.
  	    PostMethod method = new PostMethod("http://localhost:8080/services/rest/folder/createSimple");
  	  
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
  	    PostMethod method = new PostMethod("http://localhost:8080/services/rest/folder/createSimple");
  	  
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
  	    PostMethod method = new PostMethod("http://localhost:8080/services/rest/auth/login");
  		
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
