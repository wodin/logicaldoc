package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;

public interface LdapServiceAsync {

	void loadSettings(String sid, AsyncCallback<GUILdapSettings> callback);

	void saveSettings(String sid, GUILdapSettings ldapSettings, AsyncCallback<Void> callback);

}
