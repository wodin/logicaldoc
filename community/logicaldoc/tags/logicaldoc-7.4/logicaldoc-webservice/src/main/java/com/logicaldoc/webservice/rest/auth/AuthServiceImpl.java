package com.logicaldoc.webservice.rest.auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Auth Web Service Implementation (RESTful)
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class AuthServiceImpl extends com.logicaldoc.webservice.auth.AuthServiceImpl {
	@POST
	@Path("/login")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String login(@FormParam("username") String username, @FormParam("password") String password)
			throws Exception {
		return super.login(username, password);
	}

	@POST
	@Path("/logout")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public void logout(@FormParam("sid") String sid) {
		super.logout(sid);
	}

	@POST
	@Path("/valid")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public boolean valid(@FormParam("sid") String sid) {
		return super.valid(sid);
	}

	@POST
	@Path("/renew")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public void renew(@FormParam("sid") String sid) {
		super.renew(sid);
	}
}