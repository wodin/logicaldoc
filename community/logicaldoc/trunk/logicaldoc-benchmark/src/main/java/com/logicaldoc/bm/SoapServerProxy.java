package com.logicaldoc.bm;

import com.logicaldoc.webservice.soap.client.SoapSearchClient;
import com.logicaldoc.webservice.soap.client.SoapFolderClient;

import java.io.File;
import java.io.IOException;

import javax.activation.DataHandler;

import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.model.WSSearchOptions;
import com.logicaldoc.webservice.model.WSSearchResult;
import com.logicaldoc.webservice.soap.client.SoapAuthClient;
import com.logicaldoc.webservice.soap.client.SoapDocumentClient;
import com.logicaldoc.webservice.soap.client.SoapSystemClient;

/**
 * Helper class to store remote service connections.
 * 
 * @author Alessandro Gasparini - LogicalDOC
 * @since 7.5
 */
public class SoapServerProxy extends AbstractServerProxy {

	public SoapAuthClient authClient;
	public SoapDocumentClient documentClient;
	public SoapFolderClient folderClient;
	public SoapSearchClient searchClient;
	
	public SoapServerProxy(String url, ContextProperties config) throws IOException {
		
		SoapAuthClient auth = new SoapAuthClient(url + "/services/Auth");
		SoapDocumentClient documentClient = new SoapDocumentClient(url + "/services/Document",
				config.getInt("webservice.gzip"), false, 40);
		SoapFolderClient folderClient = new SoapFolderClient(url + "/services/Folder", config.getInt("webservice.gzip"),
				false, 40);
		SoapSystemClient systemClient = new SoapSystemClient(url + "/services/System");
		SoapSearchClient searchClient = new SoapSearchClient(url + "/services/Search", config.getInt("webservice.gzip"),
				false, 40);		
		
		this.url = url;
		this.authClient = auth;
		this.folderClient = folderClient;
		this.documentClient = documentClient;
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
