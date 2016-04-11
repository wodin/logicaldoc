package com.logicaldoc.webservice.rest;

import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.soap.SoapDocumentService;

@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class RestDocumentService extends SoapDocumentService {
	
	private static Logger log = LoggerFactory.getLogger(RestDocumentService.class);
	
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "doc" and "content" parameters comes in the POST request body (encoded as XML or JSON).
	public WSDocument create(List<Attachment> atts) throws Exception {
		
		String sid = null;
		WSDocument document = null;
		DataHandler content = null;
		
		for (Attachment att : atts) {
			if ("sid".equals(att.getContentDisposition().getParameter("name"))) {
				sid = att.getObject(String.class);
			} else if ("document".equals(att.getContentDisposition().getParameter("name"))) {
				document = att.getObject(WSDocument.class);
			} else if ("content".equals(att.getContentDisposition().getParameter("name"))) {
				content = att.getDataHandler();
			}
		}	

		return super.create(sid, document, content);
	}	
	
	
	@GET
	@Path("/getDocument")	
	public WSDocument getDocument(@QueryParam("sid") String sid, @QueryParam("docId") long docId) throws Exception  {
		return super.getDocument(sid, docId);
	}
	
	@POST
	@Path("/checkout")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Override
	public void checkout(@FormParam("sid") String sid, @FormParam("docId") long docId) throws Exception {
		super.checkout(sid, docId);
	}
	
	@POST
	@Path("/checkin")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response checkin(List<Attachment> attachments) throws Exception {
		try {
			String sid = null;
			long docId = 0L;
			String comment = null;
			boolean release = false;
			String filename = null;
			DataHandler datah = null;

			for (Attachment att : attachments) {
				Map<String, String> params = att.getContentDisposition().getParameters();
				if ("sid".equals(params.get("name"))) {
					sid = att.getObject(String.class);
				} else if ("docId".equals(params.get("name"))) {
					docId = Long.parseLong(att.getObject(String.class));
				} else if ("comment".equals(params.get("name"))) {
					comment = att.getObject(String.class);
				} else if ("release".equals(params.get("name"))) {
					release = Boolean.parseBoolean(att.getObject(String.class));
				} else if ("filename".equals(params.get("name"))) {
					filename = att.getObject(String.class);
				} else if ("filedata".equals(params.get("name"))) { 
					datah = att.getDataHandler();
				}
			}

			super.checkin(sid, docId, comment, filename, release, datah);
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
	public Response upload(List<Attachment> attachments) throws Exception {
		try {
			String sid = null;
			Long docId = null;
			Long folderId = null;
			boolean release = false;
			String filename = null;
			String language = null;
			DataHandler datah = null;

			for (Attachment att : attachments) {
				Map<String, String> params = att.getContentDisposition().getParameters();
				if ("sid".equals(params.get("name"))) {
					sid = att.getObject(String.class);
				} else if ("docId".equals(params.get("name"))) {
					docId = Long.parseLong(att.getObject(String.class));
				} else if ("folderId".equals(params.get("name"))) {
					folderId = Long.parseLong(att.getObject(String.class));
				} else if ("release".equals(params.get("name"))) {
					release = Boolean.parseBoolean(att.getObject(String.class));
				} else if ("filename".equals(params.get("name"))) {
					filename = att.getObject(String.class);
				} else if ("language".equals(params.get("language"))) {
					language = att.getObject(String.class);
				} else if ("filedata".equals(params.get("language"))) {
					datah = att.getDataHandler();
				} 
			}

			long documentId = super.upload(sid, docId, folderId, release, filename, language, datah);
			return Response.ok("" +documentId).build();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return Response.serverError().build();
		}
	}

}
