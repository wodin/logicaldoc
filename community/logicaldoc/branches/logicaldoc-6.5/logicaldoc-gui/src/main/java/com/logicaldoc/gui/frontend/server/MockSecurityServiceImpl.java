package com.logicaldoc.gui.frontend.server;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMenu;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
import com.logicaldoc.gui.common.server.MockInfoServiceImpl;
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
	public GUISession login(String username, String password, String locale) {
		String loc = locale;
		if (StringUtils.isEmpty(loc))
			loc = "en";

		GUISession session = new GUISession();
		GUIInfo info = new MockInfoServiceImpl().getInfo(locale);
		session.setInfo(info);

		session.setLoggedIn(false);
		if ("admin".equals(username)) {
			GUIUser user = new GUIUser();
			user.setLanguage(loc);
			session.setUser(user);
			user.setUserName(username);
			session.setSid("sid" + new Date().getTime());

			GUIGroup group = new GUIGroup();
			group.setId(1);
			group.setName("admin");
			group.setDescription("Administrators");
			user.setGroups(new GUIGroup[] { group });
			user.setFirstName("Marco");
			user.setName("Meschieri");
			user.setEmail("m.mesdchieri@logicalobjects.it");
			user.setExpired(false);
			user.setPasswordMinLenght(8);
			user.setLockedDocs(5);
			user.setCheckedOutDocs(1);
			session.setLoggedIn(true);

			Long[] menues = new Long[1000];
			for (int i = 0; i < 1000; i++) {
				menues[i] = (long) i - 100;
			}

			user.setMenues(menues);
			return session;
		} else if ("author".equals(username)) {
			GUIUser user = new GUIUser();
			user.setId(100);
			user.setExpired(true);
			user.setPasswordMinLenght(8);
			user.setLanguage(loc);
			session.setUser(user);
			session.setLoggedIn(false);
			return session;
		} else {
			session.setLoggedIn(false);
			return session;
		}
	}

	public GUIValuePair[] getBundle(String locale) {
		System.out.println("** locale=" + locale);

		// In production, use our LocaleUtil to instantiate the locale
		Locale l = new Locale(locale);
		ResourceBundle rb = ResourceBundle.getBundle("i18n.messages", l);
		GUIValuePair[] buf = new GUIValuePair[rb.keySet().size()];
		int i = 0;
		for (String key : rb.keySet()) {
			GUIValuePair entry = new GUIValuePair();
			entry.setCode(key);
			entry.setValue(rb.getString(key));
			buf[i++] = entry;
		}
		return buf;
	}

	@Override
	public void logout(String sid) {

	}

	@Override
	public int changePassword(long userId, String oldPassword, String newPassword, boolean notify) {
		return 0;
	}

	@Override
	public void deleteUser(String sid, long userId) {

	}

	@Override
	public GUIUser saveUser(String sid, GUIUser user, GUIInfo info) {
		System.out.println("* save user:" + user);

		if (user != null && user.getId() == 0)
			user.setId(9999);
		return user;
	}

	@Override
	public GUIUser saveProfile(String sid, GUIUser user) {
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

	@Override
	public void kill(String sid) {

	}

	@Override
	public void applyRights(String sid, GUIMenu menu) throws InvalidSessionException {
	}

	@Override
	public GUIMenu getMenu(String sid, long id) throws InvalidSessionException {
		return null;
	}

	@Override
	public void resetPassword(String username, String emailAddress, String productName) throws Exception {
	}
}