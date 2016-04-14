package com.logicaldoc.bm;

import java.io.File;
import java.io.IOException;

import javax.activation.DataHandler;

import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.rest.client.RestAuthClient;
import com.logicaldoc.webservice.rest.client.RestDocumentClient;
import com.logicaldoc.webservice.rest.client.RestFolderClient;
import com.logicaldoc.webservice.soap.client.SoapSearchClient;

/**
 * Helper class to store remote service connections.
 * 
 * @author Alessandro Gasparini - LogicalDOC
 * @since 7.5
 */
public class RestServerProxy extends AbstractServerProxy {

	public RestAuthClient authClient;
	public RestDocumentClient documentClient;
	public RestFolderClient folderClient;
	public SoapSearchClient searchClient;
	
	public RestServerProxy(String url, ContextProperties config) throws IOException {
		
		SoapSearchClient searchClient = new SoapSearchClient(url + "/services/Search", config.getInt("webservice.gzip"),
				false, 40);		
		
		this.url = url;
		this.authClient = new RestAuthClient(url + "/services/rest/auth");
		this.folderClient = new RestFolderClient(url + "/services/rest/folder", 40);
		this.documentClient = new RestDocumentClient(url + "/services/rest/document", 40);
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
	
	public WSFolder[] listChildren(String sid2, long parentFolder) throws Exception {
		return folderClient.listChildren(sid2, parentFolder);
	}

	@Override
	public WSSearchResult find(String sid2, WSSearchOptions options) throws Exception {
		return searchClient.find(sid2, options);
	}

	@Override
	public WSDocument[] list(String sid2, long folderId) throws Exception {
		return documentClient.listDocuments(sid2, folderId, null);
	}

	@Override
	public void update(String sid2, WSDocument doc) throws Exception {
		documentClient.update(sid2, doc);
	}

	@Override
	public WSDocument create(String ticket, WSDocument doc, DataHandler dataHandler) throws Exception {
		return documentClient.create(ticket, doc, dataHandler);
	}

	@Override
	public WSDocument create(String ticket, WSDocument doc, File file) throws Exception {
		return documentClient.create(ticket, doc, file);
	}

	@Override
	public WSFolder create(String ticket, WSFolder newFolder) throws Exception {
		return folderClient.create(ticket, newFolder);
	}

	@Override
	public WSFolder createPath(String ticket, Long rootFolder, String currentKey) throws Exception {
		return folderClient.createPath(ticket, rootFolder, currentKey);
	}	
}
