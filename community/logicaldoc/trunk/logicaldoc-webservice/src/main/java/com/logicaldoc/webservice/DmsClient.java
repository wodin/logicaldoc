package com.logicaldoc.webservice;

import java.io.IOException;

import javax.activation.DataHandler;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * Web Service client.
 * 
 * @author Matteo Caruso - Logical Object
 * @since 3.6
 */
public class DmsClient implements DmsService {

	private DmsService client;

	public DmsClient(String endpoint) throws IOException {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.setServiceClass(DmsService.class);
		factory.setAddress(endpoint);
		client = (DmsService) factory.create();
	}

	public String checkin(String username, String password, long id, String filename, String description, String type,
			DataHandler content) throws Exception {
		return client.checkin(username, password, id, filename, description, type, content);
	}

	public String checkout(String username, String password, long id) throws Exception {
		return client.checkout(username, password, id);
	}

	public String createDocument(String username, String password, long folder, String docTitle, String source,
			String sourceDate, String author, String sourceType, String coverage, String language, String keywords,
			String versionDesc, String filename, DataHandler content, String templateName,
			ExtendedAttribute[] extendedAttribute, String sourceId, String object) throws Exception {
		return client.createDocument(username, password, folder, docTitle, source, sourceDate, author, sourceType,
				coverage, language, keywords, versionDesc, filename, content, templateName, extendedAttribute,
				sourceId, object);
	}

	public String createFolder(String username, String password, String name, long parent) throws Exception {
		return client.createFolder(username, password, name, parent);
	}

	public String deleteDocument(String username, String password, long id) throws Exception {
		return client.deleteDocument(username, password, id);
	}

	public String deleteFolder(String username, String password, long folder) throws Exception {
		return client.deleteFolder(username, password, folder);
	}

	public DataHandler downloadDocument(String username, String password, long id, String version) throws Exception {
		return client.downloadDocument(username, password, id, version);
	}

	public DocumentInfo downloadDocumentInfo(String username, String password, long id) throws Exception {
		return client.downloadDocumentInfo(username, password, id);
	}

	public FolderContent downloadFolderContent(String username, String password, long folder) throws Exception {
		return client.downloadFolderContent(username, password, folder);
	}

	public SearchResult search(String username, String password, String query, String indexLanguage,
			String queryLanguage, int maxHits, String templateName, String[] templateFields) throws Exception {
		return client.search(username, password, query, indexLanguage, queryLanguage, maxHits, templateName,
				templateFields);
	}

}