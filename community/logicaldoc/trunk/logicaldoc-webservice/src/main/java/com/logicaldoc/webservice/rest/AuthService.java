package com.logicaldoc.webservice.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Auth Web Service definition interface for Rest Services
 * 
 * @author Alessandro Gasparini - LogicalDOC
 * @since 7.5
 */
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public interface AuthService {

	@GET
	@Path("/login")
	String login(@QueryParam("u") String username, @QueryParam("pw") String password) throws Exception;

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	String loginPost(@FormParam("username") String username, @FormParam("password") String password) throws Exception;

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	String loginPostJSON(String jsonstr) throws Exception;

	@DELETE
	@Path("/logout")
	void logout();
}
