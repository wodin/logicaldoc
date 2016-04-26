package com.logicaldoc.webservice.rest;

import java.util.List;

import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.logicaldoc.webservice.model.WSDocument;

@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public interface DocumentService {
		
	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "document" comes in the POST request body (encoded as JSON).
	WSDocument create(List<Attachment> atts) throws Exception;

	@GET
	@Path("/getDocument")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	WSDocument getDocument(@QueryParam("sid") String sid, @QueryParam("docId") long docId) throws Exception;

	@POST
	@Path("/checkout")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })	
	void checkout(@FormParam("sid") String sid, @FormParam("docId") long docId) throws Exception;

	@POST
	@Path("/checkin")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })	
	Response checkin(List<Attachment> attachments) throws Exception;

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })	
	Response upload(List<Attachment> attachments) throws Exception;

	@DELETE
	@Path("/delete")
	void delete(@QueryParam("sid") String sid, @QueryParam("docId") long docId) throws Exception;

	@GET
	@Path("/list")
	@Produces({ MediaType.APPLICATION_JSON })
	WSDocument[] list(@QueryParam("sid") String sid, @QueryParam("folderId") long folderId) throws Exception;	

	@GET
	@Path("/listDocuments")
	WSDocument[] listDocuments(@QueryParam("sid") String sid, @QueryParam("folderId") long folderId, @QueryParam("fileName") String fileName) throws Exception;
	
	
	/**
	 * Updates an existing document with the value object containing the document's metadata.
	 * @throws Exception
	 */
	@POST
	@Path("/update")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	void update(List<Attachment> attachments) throws Exception;
	
	@GET
	@Path("/getContent")	
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	DataHandler getContent(@QueryParam("sid") String sid, @QueryParam("docId") long docId) throws Exception;

}