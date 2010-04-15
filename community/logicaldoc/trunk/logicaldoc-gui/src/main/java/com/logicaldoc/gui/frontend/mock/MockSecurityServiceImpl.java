package com.logicaldoc.gui.frontend.mock;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIRight;
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
	public GUIUser login(String username, String password, String lang) {
		if ("admin".equals(username)) {
			GUIUser user = new GUIUser();
			user.setUserName(username);
			user.setSid("sid" + new Date().getTime());

			String[] features = new String[30];
			for (int i = 0; i < 30; i++) {
				features[i] = "Feature_" + i;
			}
			user.setFeatures(features);
			user.setGroups(new String[] { "admin" });
			user.setFirstName("Marco");
			user.setName("Meschieri");
			return user;
		} else {
			return null;
		}
	}

	@Override
	public void logout(String sid) {

	}

	@Override
	public GUIRight[] getSecurityEntities(String sid) {
		return null;
	}
}