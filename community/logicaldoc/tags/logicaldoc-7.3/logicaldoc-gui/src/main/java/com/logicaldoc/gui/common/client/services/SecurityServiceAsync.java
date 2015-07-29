package com.logicaldoc.gui.common.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMenu;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;

public interface SecurityServiceAsync {
	void login(String username, String password, String locale, String tenant, AsyncCallback<GUISession> callback);

	void logout(String sid, AsyncCallback<Void> callback);

	void changePassword(long userId, String oldPassword, String newPassword, boolean notify,
			AsyncCallback<Integer> callback);

	void deleteUser(String sid, long userId, AsyncCallback<Void> callback);

	void saveUser(String sid, GUIUser user, GUIInfo info, AsyncCallback<GUIUser> callback);

	void getUser(String sid, long userId, AsyncCallback<GUIUser> callback);

	void getGroup(String sid, long groupId, AsyncCallback<GUIGroup> callback);

	void deleteGroup(String sid, long groupId, AsyncCallback<Void> callback);

	void saveGroup(String sid, GUIGroup group, AsyncCallback<GUIGroup> callback);

	void removeFromGroup(String sid, long groupId, long[] docIds, AsyncCallback<Void> callback);

	void addUserToGroup(String sid, long groupId, long userId, AsyncCallback<Void> callback);

	void loadSettings(String sid, AsyncCallback<GUISecuritySettings> callback);

	void saveSettings(String sid, GUISecuritySettings settings, AsyncCallback<Void> callback);

	void kill(String sid, AsyncCallback<Void> callback);

	void saveProfile(String sid, GUIUser user, AsyncCallback<GUIUser> callback);

	void applyRights(String sid, GUIMenu menu, AsyncCallback<Void> callback);

	void getMenu(String sid, long id, AsyncCallback<GUIMenu> callback);

	void resetPassword(String username, String emailAddress, String productName, AsyncCallback<Void> callback);

	void searchUsers(String sid, String username, String groupId, AsyncCallback<GUIUser[]> callback);

	void login(String sid, AsyncCallback<GUISession> callback);
}
