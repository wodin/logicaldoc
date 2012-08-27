package com.logicaldoc.webservice.security;

import java.io.IOException;

import com.logicaldoc.webservice.AbstractClient;

/**
 * Security Web Service client.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class SecurityClient extends AbstractClient<SecurityService> implements SecurityService {

	public SecurityClient(String endpoint) throws IOException {
		super(endpoint, SecurityService.class, -1, true);
	}

	@Override
	public WSUser[] listUsers(String sid) throws Exception {
		return client.listUsers(sid);
	}

	@Override
	public WSGroup[] listGroups(String sid) throws Exception {
		return client.listGroups(sid);
	}

	@Override
	public long storeUser(String sid, WSUser user) throws Exception {
		return client.storeUser(sid, user);
	}

	@Override
	public long storeGroup(String sid, WSGroup group) throws Exception {
		return client.storeGroup(sid, group);
	}

	@Override
	public void deleteUser(String sid, long userId) throws Exception {
		client.deleteUser(sid, userId);
	}

	@Override
	public void deleteGroup(String sid, long groupId) throws Exception {
		client.deleteGroup(sid, groupId);
	}

	@Override
	public int changePassword(String sid, long userId, String oldPassword, String newPassword) throws Exception {
		return client.changePassword(sid, userId, oldPassword, newPassword);
	}

	@Override
	public WSUser getUser(String sid, long userId) throws Exception {
		return client.getUser(sid, userId);
	}

	@Override
	public WSGroup getGroup(String sid, long groupId) throws Exception {
		return client.getGroup(sid, groupId);
	}
}