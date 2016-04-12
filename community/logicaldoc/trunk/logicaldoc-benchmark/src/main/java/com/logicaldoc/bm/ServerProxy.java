package com.logicaldoc.bm;

import com.logicaldoc.webservice.soap.client.SoapSearchClient;
import com.logicaldoc.webservice.soap.client.SoapFolderClient;
import com.logicaldoc.webservice.soap.client.SoapAuthClient;
import com.logicaldoc.webservice.soap.client.SoapDocumentClient;
import com.logicaldoc.webservice.soap.client.SoapSystemClient;

/**
 * Helper class to store remote service connections.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 6.5
 */
public class ServerProxy {
	
	public final String url;

	public String sid;

	public SoapAuthClient authClient;

	public final SoapDocumentClient documentClient;

	public final SoapFolderClient folderClient;

	public final SoapSystemClient systemClient;

	public final SoapSearchClient searchClient;

	public ServerProxy(String url, SoapAuthClient authClient, SoapFolderClient folderClient,
			SoapDocumentClient documentClient, SoapSystemClient systemClient, SoapSearchClient searchClient) {
		this.url = url;
		this.authClient = authClient;
		this.folderClient = folderClient;
		this.documentClient = documentClient;
		this.systemClient = systemClient;
		this.searchClient = searchClient;
	}

	public void logout() {
		System.out.println("logout sid: " + sid);
		authClient.logout(sid);
	}

	public String login(String username, String password) throws Exception {
		sid = authClient.login(username, password);
		System.out.println("login sid: " + sid);
		return sid;
	}
}
