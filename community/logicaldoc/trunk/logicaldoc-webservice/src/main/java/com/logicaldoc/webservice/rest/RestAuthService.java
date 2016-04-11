package com.logicaldoc.webservice.rest;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.auth.AuthServiceImpl;

@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class RestAuthService extends AuthServiceImpl {
	
	private static Logger log = LoggerFactory.getLogger(RestAuthService.class);
	
	@GET
	@Path("/login")
	public String login(@QueryParam("u") String username, @QueryParam("pw") String password) throws Exception {
		return super.login(username, password);
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String loginPost(@FormParam("username") String username, @FormParam("password") String password) throws Exception {
		return super.login(username, password);
	}
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public String loginPostJSON(String jsonstr) throws Exception {
		log.debug("loginPostJSON({})", jsonstr);		
		
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String,String>> typeRef = new TypeReference<HashMap<String,String>>(){};
		HashMap<String,String> hm = mapper.readValue(jsonstr, typeRef); 
		
		String username = hm.get("username");
		String password = hm.get("password");
		
		return super.login(username, password);
	}		
	
	@DELETE
	@Path("/logout/{sid}")	
	public void logout(@PathParam("sid") String sid) {
		log.debug("logout({})", sid);
		super.logout(sid);
	}	
}
