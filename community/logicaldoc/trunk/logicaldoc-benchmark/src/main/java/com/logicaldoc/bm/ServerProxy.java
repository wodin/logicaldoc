package com.logicaldoc.bm;

import com.logicaldoc.webservice.auth.AuthClient;
import com.logicaldoc.webservice.document.DocumentClient;
import com.logicaldoc.webservice.folder.FolderClient;
import com.logicaldoc.webservice.search.SearchClient;
import com.logicaldoc.webservice.system.SystemClient;

/**
 * Helper class to store remote service connections.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 6.5
 */
public class ServerProxy {
	
	public final String url;

	public String sid;

	public AuthClient authClient;

	public final DocumentClient documentClient;

	public final FolderClient folderClient;

	public final SystemClient systemClient;

	public final SearchClient searchClient;

	public ServerProxy(String url, AuthClient authClient, FolderClient folderClient,
			DocumentClient documentClient, SystemClient systemClient, SearchClient searchClient) {
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
