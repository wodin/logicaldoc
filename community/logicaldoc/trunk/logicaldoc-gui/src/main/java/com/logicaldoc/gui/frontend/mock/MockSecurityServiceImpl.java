package com.logicaldoc.gui.frontend.mock;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
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
			GUIGroup group = new GUIGroup();
			group.setId(1);
			group.setName("admin");
			group.setDescription("Administrators");
			user.setGroups(new GUIGroup[] { group });
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

	@Override
	public void deleteUser(String sid, long userId) {

	}

	@Override
	public GUIUser saveUser(String sid, GUIUser user) {
		if (user.getId() == 0)
			user.setId(9999);
		return user;
	}

	@Override
	public GUIUser getUser(String sid, long userId) {
		GUIUser user = new GUIUser();
		user.setId(userId);
		user.setUserName("admin");
		user.setName("Meschieri");
		user.setFirstName("Marco");
		user.setEmail("m.meschieri@logicalobjects.it");
		GUIGroup group = new GUIGroup();
		group.setId(1);
		group.setName("admin");
		group.setDescription("Administrators");
		user.setGroups(new GUIGroup[] { group });
		return user;
	}

	@Override
	public void deleteGroup(String sid, long groupId) {

	}

	@Override
	public GUIGroup getGroup(String sid, long groupId) {
		GUIGroup group = new GUIGroup();
		group.setId(groupId);
		group.setName("Group " + groupId);
		group.setDescription("Description " + groupId);
		return group;
	}

	@Override
	public GUIGroup saveGroup(String sid, GUIGroup group) {
		if (group.getId() == 0)
			group.setId(9999);
		return group;
	}

	@Override
	public void addUserToGroup(String sid, long groupId, long userId) {

	}

	@Override
	public void removeFromGroup(String sid, long groupId, long[] docIds) {

	}

	@Override
	public GUISecuritySettings loadSettings(String sid) {
		GUISecuritySettings settings = new GUISecuritySettings();
		settings.setPwdExpiration(60);
		settings.setPwdSize(8);
		GUIUser user = new GUIUser();
		user.setId(1);
		user.setUserName("admin");
		user.setName("Caruso");
		user.setFirstName("Matteo");
		settings.setNotifiedUsers(new GUIUser[] { user });
		return settings;
	}

	@Override
	public void saveSettings(String sid, GUISecuritySettings settings) {
		
	}
}