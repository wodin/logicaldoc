package com.logicaldoc.webservice.document;

import java.io.File;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * Document Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class DocumentClient implements DocumentService {

	private DocumentService client;

	public DocumentClient(String endpoint) throws IOException {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.setServiceClass(DocumentService.class);
		factory.setAddress(endpoint);
		client = (DocumentService) factory.create();
	}

	@Override
	public WSDocument create(String sid, WSDocument document, DataHandler content) throws Exception {
		return client.create(sid, document, content);
	}

	public WSDocument create(String sid, WSDocument document, File content) throws Exception {
		return create(sid, document, new DataHandler(new FileDataSource(content)));
	}

	@Override
	public void checkout(String sid, long docId) throws Exception {
		client.checkout(sid, docId);
	}

	@Override
	public void delete(String sid, long docId) throws Exception {
		client.delete(sid, docId);
	}

	@Override
	public WSDocument getDocument(String sid, long docId) throws Exception {
		return client.getDocument(sid, docId);
	}

	@Override
	public boolean isReadable(String sid, long docId) throws Exception {
		return client.isReadable(sid, docId);
	}

	@Override
	public void lock(String sid, long docId) throws Exception {
		client.lock(sid, docId);
	}

	@Override
	public void move(String sid, long docId, long folderId) throws Exception {
		client.move(sid, docId, folderId);
	}

	@Override
	public void unlock(String sid, long docId) throws Exception {
		client.unlock(sid, docId);
	}

	@Override
	public void update(String sid, WSDocument document) throws Exception {
		client.update(sid, document);
	}

	@Override
	public void checkin(String sid, long docId, String comment, String filename, boolean release, DataHandler content)
			throws Exception {
		client.checkin(sid, docId, comment, filename, release, content);

	}

	@Override
	public WSDocument[] list(String sid, long folderId) throws Exception {
		return client.list(sid, folderId);
	}

	@Override
	public DataHandler getContent(String sid, long docId) throws Exception {
		return client.getContent(sid, docId);
	}

	@Override
	public WSDocument[] getVersions(String sid, long docId) throws Exception {
		return client.getVersions(sid, docId);
	}

	@Override
	public void restore(String sid, long docId, long folderId) throws Exception {
		client.restore(sid, docId, folderId);
	}

	@Override
	public void rename(String sid, long docId, String name) throws Exception {
		client.rename(sid, docId, name);
	}

	@Override
	public WSDocument[] getDocuments(String sid, Long[] docIds) throws Exception {
		return client.getDocuments(sid, docIds);
	}

	@Override
	public WSDocument[] getRecentDocuments(String sid, Integer max) throws Exception {
		return client.getRecentDocuments(sid, max);
	}

	@Override
	public void sendEmail(String sid, Long[] docIds, String recipients, String subject, String message)
			throws Exception {
		client.sendEmail(sid, docIds, recipients, subject, message);
	}

	@Override
	public WSDocument createAlias(String sid, long docId, long folderId) throws Exception {
		return client.createAlias(sid, docId, folderId);
	}


	@Override
	public void renameFile(String sid, long docId, String name) throws Exception {
		client.renameFile(sid, docId, name);
	}
	
	@Override
	public void reindex(String sid, long docId) throws Exception {
		client.reindex(sid, docId);
	}
}