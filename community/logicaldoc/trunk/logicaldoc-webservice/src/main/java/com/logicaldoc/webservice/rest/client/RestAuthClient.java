package com.logicaldoc.webservice.rest.client;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.logicaldoc.webservice.rest.AuthService;

/**
 * Auth Web Service client (RESTful).
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.9
 */
public class RestAuthClient extends AbstractRestClient {
	
	protected static Logger log = LoggerFactory.getLogger(RestAuthClient.class);

	public RestAuthClient(String endpoint) {
		super(endpoint);
	}
	
	public String login(String username, String password) throws Exception {
		
        System.out.println("Sending request to the service");
        System.out.println("endpoint: " +endpoint);
        AuthService proxy = JAXRSClientFactory.create(endpoint, AuthService.class);
		return proxy.login(username, password);   
	}	

	public void logout(String sid) {
        AuthService proxy = JAXRSClientFactory.create(endpoint, AuthService.class);
		proxy.logout(sid);
	}

}