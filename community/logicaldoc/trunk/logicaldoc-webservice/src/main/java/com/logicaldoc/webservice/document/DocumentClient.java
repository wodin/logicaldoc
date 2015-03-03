package com.logicaldoc.webservice.document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.commons.lang.StringUtils;

import com.logicaldoc.webservice.SoapClient;

/**
 * Document Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class DocumentClient extends SoapClient<DocumentService> implements DocumentService {

	public DocumentClient(String endpoint, int gzipThreshold, boolean log, int timeout) throws IOException {
		super(endpoint, DocumentService.class, gzipThreshold, log, timeout);
	}

	public DocumentClient(String endpoint) throws IOException {
		super(endpoint, DocumentService.class, -1, true, -1);
	}

	@Override
	public WSDocument create(String sid, WSDocument document, DataHandler content) throws Exception {
		return client.create(sid, document, content);
	}

	public WSDocument create(String sid, WSDocument document, File content) throws Exception {
		if (StringUtils.isEmpty(document.getFileName()))
			document.setFileName(content.getName());
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
	public WSDocument getDocumentByCustomId(String sid, String customId) throws Exception {
		return client.getDocumentByCustomId(sid, customId);
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

	public void checkin(String sid, long docId, String comment, String filename, boolean release, File content)
			throws Exception {
		this.checkin(sid, docId, comment, filename, release, new DataHandler(new FileDataSource(content)));
	}

	@Override
	@Deprecated
	public WSDocument[] list(String sid, long folderId) throws Exception {
		return client.listDocuments(sid, folderId, null);
	}

	@Override
	public DataHandler getContent(String sid, long docId) throws Exception {
		return client.getContent(sid, docId);
	}

	@Override
	public DataHandler getVersionContent(String sid, long docId, String version) throws Exception {
		return client.getVersionContent(sid, docId, version);
	}

	@Override
	public DataHandler getResource(String sid, long docId, String fileVersion, String suffix) throws Exception {
		return client.getResource(sid, docId, fileVersion, suffix);
	}

	public void getResourceContent(String sid, long docId, String fileVersion, String suffix, File out)
			throws Exception {
		DataHandler data = client.getResource(sid, docId, fileVersion, suffix);
		data.writeTo(new FileOutputStream(out));
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
	public WSDocument createAlias(String sid, long docId, long folderId, String type) throws Exception {
		return client.createAlias(sid, docId, folderId, type);
	}

	@Override
	public void renameFile(String sid, long docId, String name) throws Exception {
		client.renameFile(sid, docId, name);
	}

	@Override
	public void reindex(String sid, long docId, String content) throws Exception {
		client.reindex(sid, docId, content);
	}

	@Override
	public WSDocument[] listDocuments(String sid, long folderId, String fileName) throws Exception {
		return client.listDocuments(sid, folderId, fileName);
	}

	@Override
	public WSDocument[] getAliases(String sid, long docId) throws Exception {
		return client.getAliases(sid, docId);
	}

	@Override
	public long upload(String sid, Long docId, Long folderId, boolean release, String filename, DataHandler content)
			throws Exception {
		return client.upload(sid, docId, folderId, release, filename, content);
	}

	@Override
	public WSLink link(String sid, long doc1, long doc2, String type) throws Exception {
		return client.link(sid, doc1, doc2, type);
	}

	@Override
	public WSLink[] getLinks(String sid, long docId) throws Exception {
		return client.getLinks(sid, docId);
	}

	@Override
	public void deleteLink(String sid, long id) throws Exception {
		client.deleteLink(sid, id);
	}

	@Override
	public void createPdf(String sid, long docId, String fileVersion) throws Exception {
		client.createPdf(sid, docId, fileVersion);
	}

	@Override
	public void uploadResource(String sid, long docId, String fileVersion, String suffix, DataHandler content)
			throws Exception {
		client.uploadResource(sid, docId, fileVersion, suffix, content);
	}

	public void uploadResource(String sid, long docId, String fileVersion, String suffix, File content)
			throws Exception {
		uploadResource(sid, docId, fileVersion, suffix, new DataHandler(new FileDataSource(content)));
	}

	@Override
	public String getExtractedText(String sid, long docId) throws Exception {
		return client.getExtractedText(sid, docId);
	}
}