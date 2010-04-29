package com.logicaldoc.gui.frontend.mock;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.services.SecurityService;

/**
 * Implementation of the SecurityService
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MockSecurityServiceImpl extends RemoteServiceServlet implements SecurityService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUISession login(String username, String password) {
		GUISession session = new GUISession();
		session.setLoggedIn(false);
		if ("admin".equals(username)) {
			GUIUser user = new GUIUser();
			session.setUser(user);
			user.setUserName(username);
			session.setSid("sid" + new Date().getTime());

			String[] features = new String[31];
			for (int i = 0; i < 30; i++) {
				features[i] = "Feature_" + i;
			}
			features[30] = "ENTERPRISE";
			session.setFeatures(features);
			user.setGroups(new String[] { "admin" });
			user.setFirstName("Marco");
			user.setName("Meschieri");
			user.setExpired(false);
			user.setPasswordMinLenght(8);
			session.setLoggedIn(true);
			return session;
		} else if ("author".equals(username)) {
			GUIUser user = new GUIUser();
			user.setId(100);
			user.setExpired(true);
			user.setPasswordMinLenght(8);
			session.setUser(user);
			session.setLoggedIn(false);
			return session;
		} else {
			session.setLoggedIn(false);
			return session;
		}
	}

	@Override
	public void logout(String sid) {

	}

	@Override
	public GUIRight[] getSecurityEntities(String sid) {
		return null;
	}

	@Override
	public int changePassword(long userId, String oldPassword, String newPassword) {
		return 0;
	}
}