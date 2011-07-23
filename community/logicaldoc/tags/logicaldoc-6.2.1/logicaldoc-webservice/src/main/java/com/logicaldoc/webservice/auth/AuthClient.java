package com.logicaldoc.webservice.auth;

import java.io.IOException;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

/**
 * Auth Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class AuthClient implements AuthService {

	private AuthService client;

	public AuthClient(String endpoint) throws IOException {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();

		factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.getOutInterceptors().add(new LoggingOutInterceptor());
		factory.setServiceClass(AuthService.class);
		factory.setAddress(endpoint);
		client = (AuthService) factory.create();
	}

	@Override
	public String login(String username, String password) throws Exception {
		return client.login(username, password);
	}

	@Override
	public void logout(String sid) {
		client.logout(sid);
	}
}