package com.logicaldoc.webservice.rest.document;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.logicaldoc.webservice.document.WSDocument;

/**
 * Document Web Service Implementation (RESTful)
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class DocumentServiceImpl extends com.logicaldoc.webservice.document.DocumentServiceImpl {

	@GET
	@Path("/getDocument")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	@Override
	public WSDocument getDocument(@FormParam("sid") String sid, @FormParam("docId")long docId) throws Exception {
		return super.getDocument(sid, docId);
	}
}