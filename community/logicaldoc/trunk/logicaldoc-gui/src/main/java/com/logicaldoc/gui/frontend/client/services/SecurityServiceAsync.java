package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUIGroup;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;

public interface SecurityServiceAsync {
	public void login(String username, String password, AsyncCallback<GUISession> callback);

	void logout(String sid, AsyncCallback<Void> callback);

	void getSecurityEntities(String sid, AsyncCallback<GUIRight[]> callback);

	void changePassword(long userId, String oldPassword, String newPassword, AsyncCallback<Integer> callback);

	void deleteUser(String sid, long userId, AsyncCallback<Void> callback);

	void saveUser(String sid, GUIUser user, AsyncCallback<GUIUser> callback);

	void getUser(String sid, long userId, AsyncCallback<GUIUser> callback);

	void getGroup(String sid, long groupId, AsyncCallback<GUIGroup> callback);

	void deleteGroup(String sid, long groupId, AsyncCallback<Void> callback);

	void saveGroup(String sid, GUIGroup group, AsyncCallback<GUIGroup> callback);

	void removeFromGroup(String sid, long groupId, long[] docIds, AsyncCallback<Void> callback);

	void addUserToGroup(String sid, long groupId, long userId, AsyncCallback<Void> callback);

	void loadSettings(String sid, AsyncCallback<GUISecuritySettings> callback);

	void saveSettings(String sid, GUISecuritySettings settings, AsyncCallback<Void> callback);

	void saveExtAuthSettings(String sid, GUILdapSettings ldapSettings, GUIADSettings adSettings,
			AsyncCallback<Void> callback);

	void loadExtAuthSettings(String sid, AsyncCallback<GUILdapSettings[]> callback);
}
