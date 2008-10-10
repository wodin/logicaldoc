package com.logicaldoc.webservice;

import java.io.IOException;

import javax.activation.DataHandler;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * Web Service client.
 * 
 * @author Matteo Caruso
 * @version $Id:$
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

	/**
	 * @see com.logicaldoc.webservice.DmsService#checkin(java.lang.String,
	 *      java.lang.String, int, java.lang.String, java.lang.String,
	 *      java.lang.String, javax.activation.DataHandler)
	 */
	public String checkin(String username, String password, int id, String filename, String description, String type,
			DataHandler content) throws Exception {
		return client.checkin(username, password, id, filename, description, type, content);
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#checkout(java.lang.String,
	 *      java.lang.String, int)
	 */
	public String checkout(String username, String password, int id) throws Exception {
		return client.checkout(username, password, id);
	}
	
	/**
	 * @see com.logicaldoc.webservice.DmsService#createDocument(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, javax.activation.DataHandler)
	 */
	public String createDocument(String username, String password, int parent, String docName, String source,
			String sourceDate, String author, String sourceType, String coverage, String language, String keywords,
			String versionDesc, String filename, String groups, DataHandler content) throws Exception {
		return client.createDocument(username, password, parent, docName, source, sourceDate, author, sourceType,
				coverage, language, keywords, versionDesc, filename, groups, content);
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#createFolder(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public String createFolder(String username, String password, String name, int parent) throws Exception {
		return client.createFolder(username, password, name, parent);
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#deleteDocument(java.lang.String, java.lang.String, int)
	 */
	public String deleteDocument(String username, String password, int id) throws Exception {
		return client.deleteDocument(username, password, id);
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#deleteFolder(java.lang.String, java.lang.String, int)
	 */
	public String deleteFolder(String username, String password, int folder) throws Exception {
		return client.deleteFolder(username, password, folder);
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#downloadDocument(java.lang.String, java.lang.String, int, java.lang.String)
	 */
	public DataHandler downloadDocument(String username, String password, int id, String version) throws Exception {
		return client.downloadDocument(username, password, id, version);
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#downloadDocumentInfo(java.lang.String, java.lang.String, int)
	 */
	public DocumentInfo downloadDocumentInfo(String username, String password, int id) throws Exception {
		return client.downloadDocumentInfo(username, password, id);
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#downloadFolderContent(java.lang.String, java.lang.String, int)
	 */
	public FolderContent downloadFolderContent(String username, String password, int folder) throws Exception {
		return client.downloadFolderContent(username, password, folder);
	}

	/**
	 * @see com.logicaldoc.webservice.DmsService#search(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public SearchResult search(String username, String password, String query, String indexLanguage,
			String queryLanguage, int maxHits) throws Exception {
		return client.search(username, password, query, indexLanguage, queryLanguage, maxHits);
	}

}