package com.logicaldoc.webservice.rest.client;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.AttachmentBuilder;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.model.WSFolder;
import com.logicaldoc.webservice.rest.FolderService;

public class RestFolderClient extends AbstractRestClient {

	protected static Logger log = LoggerFactory.getLogger(RestFolderClient.class);

	FolderService proxy = null;

	public RestFolderClient(String endpoint, String username, String password) {
		this(endpoint, username, password, -1);
	}

	public RestFolderClient(String endpoint, String username, String password, int timeout) {
		super(endpoint, username, password, timeout);

		JacksonJsonProvider provider = new JacksonJsonProvider();
		
		if ((username == null) || (password == null)) {
			proxy = JAXRSClientFactory.create(endpoint, FolderService.class, Arrays.asList(provider));
		} else {
			//proxy = JAXRSClientFactory.create(endpoint, FolderService.class, Arrays.asList(provider));
			//create(String baseAddress, Class<T> cls, List<?> providers, String username, String password, String configLocation)
			proxy = JAXRSClientFactory.create(endpoint, FolderService.class, Arrays.asList(provider), username, password, null);
		}
		
		if (timeout > 0) {
			HTTPConduit conduit = WebClient.getConfig(proxy).getHttpConduit();
			HTTPClientPolicy policy = new HTTPClientPolicy();
			policy.setReceiveTimeout(timeout);
			conduit.setClient(policy);
		}		
	}

	public WSFolder[] listChildren(long folderId) throws Exception {
		WebClient.client(proxy).type(MediaType.APPLICATION_JSON);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);

		return proxy.listChildren(folderId);
	}

	public WSFolder create(WSFolder folder) throws Exception {

		WebClient.client(proxy).type(MediaType.MULTIPART_FORM_DATA);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);

		ObjectWriter ow = new ObjectMapper().writer();
		String jsonStr = ow.writeValueAsString(folder);

		Attachment fldAttachment = new AttachmentBuilder().id("folder").object(jsonStr).mediaType("application/json")
				.contentDisposition(new ContentDisposition("form-data; name=\"folder\"")).build();

		List<Attachment> atts = new LinkedList<Attachment>();
		atts.add(fldAttachment);

		return proxy.create(atts);
	}

	public WSFolder createPath(long rootFolder, String path) throws Exception {

		WebClient.client(proxy).type(MediaType.APPLICATION_FORM_URLENCODED);
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);

		return proxy.createPath(rootFolder, path);
	}

	public WSFolder getFolder(long folderId) throws Exception {
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
		return proxy.getFolder(folderId);
	}

	public long createFolder(long parentId, String folderName) throws Exception {
		WebClient.client(proxy).accept(MediaType.TEXT_PLAIN);
		return proxy.createFolder(parentId, folderName);
	}

	public void delete(long folderId) throws Exception {
		WebClient.client(proxy).accept(MediaType.APPLICATION_JSON);
		proxy.delete(folderId);
	}

}
