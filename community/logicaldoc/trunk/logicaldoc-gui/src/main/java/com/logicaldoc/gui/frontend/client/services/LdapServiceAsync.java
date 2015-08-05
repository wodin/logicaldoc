package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIValue;

public interface LdapServiceAsync {

	void loadSettings(String sid, AsyncCallback<GUILdapSettings> callback);

	void saveSettings(String sid, GUILdapSettings ldapSettings, AsyncCallback<Void> callback);

	void testConnection(String sid, GUILdapSettings ldapSettings, AsyncCallback<Boolean> callback);

	void listUsers(String sid, String login, AsyncCallback<GUIUser[]> callback);

	void importUsers(String sid, String[] usernames, long tenantId, AsyncCallback<GUIValue[]> callback);
}
