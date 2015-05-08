package com.logicaldoc.webservice.rest.document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.auth.AuthServiceImpl;
import com.logicaldoc.webservice.document.DocumentService;
import com.logicaldoc.webservice.document.WSDocument;
import com.logicaldoc.webservice.document.WSLink;
import com.logicaldoc.webservice.rest.RestClient;

public class DocumentClient extends RestClient implements DocumentService {

	protected static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

	public DocumentClient(String endpoint) {
		super(endpoint);
	}

	@Override
	public WSDocument create(String sid, WSDocument document, DataHandler content) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void lock(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void unlock(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String sid, long docId, String name) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void renameFile(String sid, long docId, String name) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(String sid, long docId, long folderId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public WSDocument getDocument(String sid, long docId) throws Exception {
		String url = endpoint + "/getDocument";

		PostMethod post = preparePostMethod(url);
		try {
			post.setParameter("sid", sid);
			post.setParameter("docId", Long.toString(docId));

			try {
				client.executeMethod(post);
				System.out.println(post.getResponseBodyAsString());
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
			return null;
		} finally {
			post.releaseConnection();
		}
	}

	@Override
	public WSDocument getDocumentByCustomId(String sid, String customId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSDocument[] getDocuments(String sid, Long[] docIds) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSDocument[] getAliases(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(String sid, WSDocument document) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public DataHandler getContent(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataHandler getVersionContent(String sid, long docId, String version) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void checkout(String sid, long docId) throws Exception {
		String url = endpoint + "/checkout";

		PostMethod post = preparePostMethod(url);
		try {
			post.setParameter("sid", sid);
			post.setParameter("docId", Long.toString(docId));

			try {
				client.executeMethod(post);
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			}
		} finally {
			post.releaseConnection();
		}
	}

	@Override
	public void checkin(String sid, long docId, String comment, String filename, boolean release, DataHandler content)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public long upload(String sid, Long docId, Long folderId, boolean release, String filename, DataHandler content)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadable(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void restore(String sid, long docId, long folderId) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public WSDocument[] getVersions(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSDocument[] list(String sid, long folderId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSDocument[] listDocuments(String sid, long folderId, String fileName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSDocument[] getRecentDocuments(String sid, Integer maxHits) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendEmail(String sid, Long[] docIds, String recipients, String subject, String message)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public WSDocument createAlias(String sid, long docId, long folderId, String type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reindex(String sid, long docId, String content) throws Exception {
		// TODO Auto-generated method stub
	}

	public void checkin(String sid, long docId, String comment, String filename, boolean release, File file)
			throws Exception {
		String output = null;
		String url = endpoint + "/checkin";

		PostMethod post = preparePostMethod(url);
		try {
			Part[] parts = { new FilePart("filedata", filename, file, null, null), new StringPart("sid", sid),
					new StringPart("docId", "" + docId), new StringPart("comment", comment),
					new StringPart("filename", filename), new StringPart("release", "" + release) };

			post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
			int statusCode = client.executeMethod(post);

			if (statusCode == HttpStatus.SC_OK)
				output = post.getResponseBodyAsString();
			else
				throw new Exception("Server Error");
		} finally {
			post.releaseConnection();
		}

		if (StringUtils.isEmpty(output))
			throw new Exception("Invalid checkin");
	}

	public long upload(String sid, Long docId, Long folderId, boolean release, String filename, File file)
			throws Exception {
		String output = null;
		String url = endpoint + "/upload";

		PostMethod post = preparePostMethod(url);
		try {
			List<Part> parts = new ArrayList<Part>();
			parts.add(new FilePart("filedata", filename, file, null, null));
			parts.add(new StringPart("sid", sid));
			if (docId != null)
				parts.add(new StringPart("docId", "" + docId));
			if (folderId != null)
				parts.add(new StringPart("folderId", "" + folderId));
			parts.add(new StringPart("filename", filename));
			parts.add(new StringPart("release", "" + release));

			post.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[0]), post.getParams()));
			int statusCode = client.executeMethod(post);

			if (statusCode == HttpStatus.SC_OK)
				output = post.getResponseBodyAsString();
			else
				throw new Exception("Server Error");
		} finally {
			post.releaseConnection();
		}

		if (StringUtils.isEmpty(output))
			throw new Exception("Invalid checkin");
		else
			return Long.parseLong(output);
	}

	@Override
	public WSLink link(String sid, long doc1, long doc2, String type) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WSLink[] getLinks(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteLink(String sid, long id) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public DataHandler getResource(String sid, long docId, String fileVersion, String suffix) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createPdf(String sid, long docId, String fileVersion) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void uploadResource(String sid, long docId, String fileVersion, String suffix, DataHandler content)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getExtractedText(String sid, long docId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
