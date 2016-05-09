package com.logicaldoc.webservice.rest;

import java.util.List;

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

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.logicaldoc.webservice.model.WSFolder;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public interface FolderService {

	@POST
	@Path("/create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// The "folder" parameter comes in the POST request body (encoded as XML or
	// JSON).
	WSFolder create(List<Attachment> atts) throws Exception;

	@POST
	@Path("/createSimple")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	// The "folderPath" parameter comes in the POST request body.
	WSFolder createSimpleForm(@FormParam("folderPath") String folderPath) throws Exception;

	@POST
	@Path("/createSimple")
	@Consumes({ MediaType.APPLICATION_JSON })
	// The "folderPath" parameter comes in the POST request body.
	WSFolder createSimpleJSON(String jsonstr) throws Exception;

	// The parameters come in the POST request body.
	@POST
	@Path("/createPath")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	WSFolder createPath(@FormParam("parentId") long parentId, @FormParam("path") String path) throws Exception;

	@POST
	@Path("/createFolder")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	// The parameters come in the POST request body.
	long createFolder(@FormParam("parentId") long parentId, @FormParam("name") String name) throws Exception;

	@GET
	@Path("/getFolder")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	WSFolder getFolder(@QueryParam("folderId") long folderId) throws Exception;

	@DELETE
	@Path("/delete")
	void delete(@QueryParam("folderId") long folderId) throws Exception;

	@GET
	@Path("/listChildren")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	WSFolder[] listChildren(@QueryParam("folderId") long folderId) throws Exception;

	@GET
	@Path("/getPath")
	WSFolder[] getPath(@QueryParam("folderId") long folderId) throws Exception;

	@GET
	@Path("/getPathString")
	String getPathString(@QueryParam("folderId") long folderId) throws Exception;

	@POST
	@Path("/update")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	void update(List<Attachment> atts) throws Exception;

	@PUT
	@Path("/rename")
	void rename(@QueryParam("folderId") long folderId, @QueryParam("name") String name) throws Exception;

	@PUT
	@Path("/move")
	void move(@QueryParam("folderId") long folderId, @QueryParam("parentId") long parentId) throws Exception;
}