package com.logicaldoc.webservice.rest.endpoint;

import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.model.WSDocument;
import com.logicaldoc.webservice.rest.DocumentService;
import com.logicaldoc.webservice.soap.endpoint.SoapDocumentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

 
@Path("/")
//@Api(value = "document", authorizations = {@Authorization(value = "basic")} )
@Api(value = "document")
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class RestDocumentService extends SoapDocumentService implements DocumentService {

	private static Logger log = LoggerFactory.getLogger(RestDocumentService.class);

	@Override
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@ApiOperation(value = "Creates a new document", 
	notes = "Creates a new document using the metadata 'document' provided as JSON/XML")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "document", value = "The document metadata provided as WSDocument object encoded in JSON/XML format", required = true, dataType = "com.logicaldoc.webservice.model.WSDocument", paramType = "body"),
	    @ApiImplicitParam(name = "content", value = "File data", required = true, dataType = "file", paramType = "form")
	  })		
	public WSDocument create(List<Attachment> atts) throws Exception {
		log.debug("create()");

		String sid = validateSession();

		WSDocument document = null;
		DataHandler content = null;

		for (Attachment att : atts) {
			if ("document".equals(att.getContentDisposition().getParameter("name"))) {
				document = att.getObject(WSDocument.class);
			} else if ("content".equals(att.getContentDisposition().getParameter("name"))) {
				content = att.getDataHandler();
			}
		}

		log.debug("document: {}", document);
		log.debug("content: {}", content);

		return super.create(sid, document, content);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicaldoc.webservice.rest.DocumentService#getDocument(java.lang.String,
	 * long)
	 */
	@Override
	@GET
	@Path("/getDocument")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public WSDocument getDocument(@QueryParam("docId") long docId) throws Exception {
		String sid = validateSession();
		return super.getDocument(sid, docId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicaldoc.webservice.rest.DocumentService#checkout(java.lang.String,
	 * long)
	 */
	@Override
	@POST
	@Path("/checkout")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public void checkout(@FormParam("docId") long docId) throws Exception {
		String sid = validateSession();
		super.checkout(sid, docId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.webservice.rest.DocumentService#checkin(java.util.List)
	 */
	@Override
	@POST
	@Path("/checkin")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Check-in an existing document", 
	notes = "Performs a check-in (commit) operation of new content over an existing document. The document must be in checked-out status")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "docId", value = "The id of an existing document to update", required = true, dataType = "integer", paramType = "form"),
	    @ApiImplicitParam(name = "comment", value = "An optional comment", required = false, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "release", value = "Indicates whether to create or not a new major release of the updated document", required = false, dataType = "string", paramType = "form", allowableValues = "true, false"),
	    @ApiImplicitParam(name = "filename", value = "File name", required = true, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "filedata", value = "File data", required = true, dataType = "file", paramType = "form")
	  })		
	public Response checkin(List<Attachment> attachments) throws Exception {
		String sid = validateSession();
		try {
			long docId = 0L;
			String comment = null;
			boolean release = false;
			String filename = null;
			DataHandler datah = null;

			for (Attachment att : attachments) {
				Map<String, String> params = att.getContentDisposition().getParameters();
				if ("docId".equals(params.get("name"))) {
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


	@Override
	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@ApiOperation(value = "Uploads a document", 
	notes = "Creates or updates an existing document, if used in update mode docId must be provided, when used in create mode folderId is required")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "docId", value = "The id of an existing document to update", required = false, dataType = "integer", paramType = "form"),
	    @ApiImplicitParam(name = "folderId", value = "Folder id where to place the document", required = false, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "release", value = "Indicates whether to create or not a new major release of an updated document", required = false, dataType = "string", paramType = "form", allowableValues = "true, false"),
	    @ApiImplicitParam(name = "filename", value = "File name", required = true, dataType = "string", paramType = "form"),
	    @ApiImplicitParam(name = "language", value = "Language of the document (ISO 639-2)", required = false, dataType = "string", paramType = "form", defaultValue = "en"),
	    @ApiImplicitParam(name = "filedata", value = "File data", required = true, dataType = "file", paramType = "form")
	  })	
	public Response upload(List<Attachment> attachments) throws Exception {
		String sid = validateSession();
		try {
			Long docId = null;
			Long folderId = null;
			boolean release = false;
			String filename = null;
			String language = null;
			DataHandler datah = null;

			for (Attachment att : attachments) {
				Map<String, String> params = att.getContentDisposition().getParameters();
                //log.debug("keys: {}", params.keySet());
				//log.debug("name: {}", params.get("name"));
        
				if ("docId".equals(params.get("name"))) {
					docId = Long.parseLong(att.getObject(String.class));
				} else if ("folderId".equals(params.get("name"))) {
					folderId = Long.parseLong(att.getObject(String.class));
				} else if ("release".equals(params.get("name"))) {
					release = Boolean.parseBoolean(att.getObject(String.class));
				} else if ("filename".equals(params.get("name"))) {
					filename = att.getObject(String.class);
				} else if ("language".equals(params.get("name"))) {
					language = att.getObject(String.class);
				} else if ("filedata".equals(params.get("name"))) {
					datah = att.getDataHandler();
				}
			}

			long documentId = super.upload(sid, docId, folderId, release, filename, language, datah);
			return Response.ok("" + documentId).build();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
			return Response.serverError().build();
		}
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.webservice.rest.DocumentService#delete(java.lang.String,
	 * long)
	 */
	@Override
	@DELETE
	@Path("/delete")	
    @ApiOperation(value = "Deletes a document")	
	public void delete(@ApiParam(value = "Document id to delete", required = true) @QueryParam("docId") long docId) throws Exception {
		String sid = validateSession();
		super.delete(sid, docId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.logicaldoc.webservice.rest.DocumentService#list(java.lang.String,
	 * long)
	 */
	@Override
	@GET
	@Path("/list")
	@Produces({ MediaType.APPLICATION_JSON })
	public WSDocument[] list(@QueryParam("folderId") long folderId) throws Exception {
		String sid = validateSession();
		return super.listDocuments(sid, folderId, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.logicaldoc.webservice.rest.DocumentService#listDocuments(java.lang.String
	 * , long, java.lang.String)
	 */
	@Override
	@GET
	@Path("/listDocuments")
	public WSDocument[] listDocuments(@QueryParam("folderId") long folderId, @QueryParam("fileName") String fileName)
			throws Exception {
		String sid = validateSession();
		return super.listDocuments(sid, folderId, fileName);
	}

	@GET
	@Path("/getContent")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public DataHandler getContent(@QueryParam("docId") long docId) throws Exception {
		String sid = validateSession();
		return super.getContent(sid, docId);
	}

	@Override
	@PUT
	@Path("/update")
    @ApiOperation(value = "Update an existing document")	
	public void update(@ApiParam(value = "Document object that needs to be updated", required = true) WSDocument document) throws Exception {
		String sid = validateSession();
		super.update(sid, document);
	}
}