package com.logicaldoc.webservice.auth;

import java.io.IOException;

import com.logicaldoc.webservice.AbstractClient;

/**
 * Auth Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 5.2
 */
public class AuthClient extends AbstractClient<AuthService> implements AuthService {

	public AuthClient(String endpoint) throws IOException {
		super(endpoint, AuthService.class, -1, true, -1);
	}

	@Override
	public String login(String username, String password) throws Exception {
		return client.login(username, password);
	}

	@Override
	public void logout(String sid) {
		client.logout(sid);
	}

	@Override
	public boolean valid(String sid) {
		return client.valid(sid);
	}

	@Override
	public void renew(String sid) {
		client.renew(sid);
	}
}