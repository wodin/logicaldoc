package com.logicaldoc.webservice.rest.folder;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Folder Web Service Implementation (RESTful)
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1.1
 */
public class FolderServiceImpl extends com.logicaldoc.webservice.folder.FolderServiceImpl {
	@POST
	@Path("/createFolder")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Override
	public long createFolder(@FormParam("sid") String sid, @FormParam("parentId") long parentId,
			@FormParam("name") String name) throws Exception {
		return super.createFolder(sid, parentId, name);
	}
}