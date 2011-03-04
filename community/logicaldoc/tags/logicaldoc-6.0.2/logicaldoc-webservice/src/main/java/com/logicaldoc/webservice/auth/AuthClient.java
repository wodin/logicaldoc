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
	public Right[] getGrantedGroups(String sid, long folderId) throws Exception {
		return client.getGrantedGroups(sid, folderId);
	}

	@Override
	public Right[] getGrantedUsers(String sid, long folderId) throws Exception {
		return client.getGrantedUsers(sid, folderId);
	}

	@Override
	public long[] getGroups(String sid) throws Exception {
		return client.getGroups(sid);
	}

	@Override
	public long[] getUsers(String sid) throws Exception {
		return client.getUsers(sid);
	}

	@Override
	public void grantGroup(String sid, long folderId, long groupId, int permissions, boolean recursive)
			throws Exception {
		client.grantGroup(sid, folderId, groupId, permissions, recursive);
	}

	@Override
	public void grantUser(String sid, long folderId, long userId, int permissions, boolean recursive) throws Exception {
		client.grantUser(sid, folderId, userId, permissions, recursive);
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