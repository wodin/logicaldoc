package com.logicaldoc.webservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

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

	public String checkin(String sid, long id, String filename, String description, boolean release, DataHandler content)
			throws Exception {
		return client.checkin(sid, id, filename, description, release, content);
	}

	public String checkin(String sid, long id, String filename, String description, boolean release, File content)
			throws Exception {
		return client.checkin(sid, id, filename, description, release, new DataHandler(new FileDataSource(content)));
	}

	public String checkout(String sid, long id) throws Exception {
		return client.checkout(sid, id);
	}

	public String createDocument(String sid, long folder, String docTitle, String source, String sourceDate,
			String author, String sourceType, String coverage, String language, String tags, String versionDesc,
			String filename, DataHandler content, String templateName, Attribute[] extendedAttributes, String sourceId,
			String object, String recipient, String customId) throws Exception {
		return client.createDocument(sid, folder, docTitle, source, sourceDate, author, sourceType, coverage, language,
				tags, versionDesc, filename, content, templateName, extendedAttributes, sourceId, object, recipient,
				customId);
	}

	public String createFolder(String sid, String name, long parent) throws Exception {
		return client.createFolder(sid, name, parent);
	}

	public String deleteDocument(String sid, long id) throws Exception {
		return client.deleteDocument(sid, id);
	}

	public String deleteFolder(String sid, long folder) throws Exception {
		return client.deleteFolder(sid, folder);
	}

	public DataHandler downloadDocument(String sid, long id, String version) throws Exception {
		return client.downloadDocument(sid, id, version);
	}

	public void downloadDocumentToFile(String sid, long id, String version, File file) throws Exception {
		DataHandler dh = downloadDocument(sid, id, version);
		InputStream is = dh.getInputStream();
		FileOutputStream os = new FileOutputStream(file);
		byte[] buf = new byte[1024];
		int read = 1;
		while (read > 0) {
			read = is.read(buf, 0, buf.length);
			if (read > 0)
				os.write(buf, 0, read);
		}
		os.flush();
		os.close();
	}

	public DocumentInfo downloadDocumentInfo(String sid, long id) throws Exception {
		return client.downloadDocumentInfo(sid, id);
	}

	public FolderContent downloadFolderContent(String sid, long folder) throws Exception {
		return client.downloadFolderContent(sid, folder);
	}

	public String login(String username, String password) throws Exception {
		return client.login(username, password);
	}

	public void logout(String sid) {
		client.logout(sid);
	}

	public String renameFolder(String sid, long folder, String name) throws Exception {
		return client.renameFolder(sid, folder, name);
	}

	public SearchResult search(String sid, String query, String indexLanguage, String queryLanguage, int maxHits,
			String templateName, String[] templateFields) throws Exception {
		return client.search(sid, query, indexLanguage, queryLanguage, maxHits, templateName, templateFields);
	}

	public String update(String sid, long id, String title, String source, String sourceAuthor, String sourceDate,
			String sourceType, String coverage, String language, String[] tags, String sourceId, String object,
			String recipient, String templateName, Attribute[] extendedAttribute) throws Exception {
		return client.update(sid, id, title, source, sourceAuthor, sourceDate, sourceType, coverage, language, tags,
				sourceId, object, recipient, templateName, extendedAttribute);
	}

	@Override
	public void indexDocument(String sid, long id) throws Exception {
		client.indexDocument(sid, id);
	}
}