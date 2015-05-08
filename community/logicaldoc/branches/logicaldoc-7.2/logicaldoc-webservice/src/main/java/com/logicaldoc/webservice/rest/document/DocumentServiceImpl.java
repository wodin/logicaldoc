package com.logicaldoc.webservice.rest.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.logicaldoc.webservice.document.WSDocument;

/**
 * Document Web Service Implementation (RESTful)
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class DocumentServiceImpl extends com.logicaldoc.webservice.document.DocumentServiceImpl {

	@POST
	@Path("/getDocument")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public Response getDocumentRest(@FormParam("sid") String sid, @FormParam("docId") long docId) throws Exception {
		try {
			WSDocument doc = super.getDocument(sid, docId);
			if (doc == null)
				throw new Exception("Unexisting document " + docId);
			return Response.ok(doc).build();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return Response.serverError().build();
		}
	}

	@POST
	@Path("/checkout")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Override
	public void checkout(@FormParam("sid") String sid, @FormParam("docId") long docId) throws Exception {
		super.checkout(sid, docId);
	}

	@POST
	@Path("/checkin")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response checkin(MultipartBody multipartBody) throws Exception {
		try {
			String sid = null;
			long docId = 0L;
			String comment = null;
			boolean release = false;
			String filename = null;

			List<Attachment> attachments = multipartBody.getAllAttachments();
			for (Attachment attachment : attachments) {
				Map<String, String> params = attachment.getContentDisposition().getParameters();
				if ("sid".equals(params.get("name"))) {
					sid = getContentAsString(attachment.getDataHandler());
				} else if ("docId".equals(params.get("name"))) {
					docId = Long.parseLong(getContentAsString(attachment.getDataHandler()));
				} else if ("comment".equals(params.get("name"))) {
					comment = getContentAsString(attachment.getDataHandler());
				} else if ("release".equals(params.get("name"))) {
					release = Boolean.parseBoolean(getContentAsString(attachment.getDataHandler()));
				} else if ("filename".equals(params.get("name"))) {
					filename = getContentAsString(attachment.getDataHandler());
				}
			}

			super.checkin(sid, docId, comment, filename, release, attachments.get(0).getDataHandler());
			return Response.ok("file checked-in").build();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return Response.serverError().build();
		}
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response upload(MultipartBody multipartBody) throws Exception {
		try {
			String sid = null;
			Long docId = null;
			Long folderId = null;
			boolean release = false;
			String filename = null;

			List<Attachment> attachments = multipartBody.getAllAttachments();
			for (Attachment attachment : attachments) {
				Map<String, String> params = attachment.getContentDisposition().getParameters();
				if ("sid".equals(params.get("name"))) {
					sid = getContentAsString(attachment.getDataHandler());
				} else if ("docId".equals(params.get("name"))) {
					docId = Long.parseLong(getContentAsString(attachment.getDataHandler()));
				} else if ("folderId".equals(params.get("name"))) {
					folderId = Long.parseLong(getContentAsString(attachment.getDataHandler()));
				} else if ("release".equals(params.get("name"))) {
					release = Boolean.parseBoolean(getContentAsString(attachment.getDataHandler()));
				} else if ("filename".equals(params.get("name"))) {
					filename = getContentAsString(attachment.getDataHandler());
				}
			}

			long documentId = super
					.upload(sid, docId, folderId, release, filename, attachments.get(0).getDataHandler());
			return Response.ok("" + documentId).build();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return Response.serverError().build();
		}
	}

	private String getContentAsString(DataHandler handler) throws IOException {
		if (handler == null)
			return null;

		BufferedReader br = new BufferedReader(new InputStreamReader(handler.getInputStream()));

		try {
			StringBuffer content = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				if (content.length() > 0)
					content.append("\n");
				content.append(line);
			}
			return content.toString();
		} finally {
			br.close();
		}
	}
}