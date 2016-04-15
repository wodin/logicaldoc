package com.logicaldoc.webservice.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import com.logicaldoc.webservice.model.WSFolder;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public interface FolderService {

	// The "folder" parameter comes in the POST request body (encoded as XML or
	// JSON).
	WSFolder create(List<Attachment> atts) throws Exception;

	// The "folderPath" parameter comes in the POST request body.
	WSFolder createSimpleForm(String sid, String folderPath) throws Exception;

	// The "folderPath" parameter comes in the POST request body.
	WSFolder createSimpleJSON(String jsonstr) throws Exception;


	// The parameters come in the POST request body.
	@POST
	@Path("/createPath")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	WSFolder createPath(@FormParam("sid") String sid, @FormParam("parentId") long parentId,
			@FormParam("path") String path) throws Exception;

	// The parameters come in the POST request body.
	long createFolder(String sid, long parentId, String name) throws Exception;

	WSFolder getFolder(String sid, long folderId) throws Exception;

	void delete(String sid, long folderId) throws Exception;

	@GET
	@Path("/listChildren")
	WSFolder[] listChildren(@QueryParam("sid") String sid, @QueryParam("folderId") long folderId) throws Exception;	

	WSFolder[] getPath(String sid, long folderId) throws Exception;

	String getPathString(String sid, long folderId) throws Exception;

}