package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;

public interface LdapServiceAsync {

	void loadExtAuthSettings(String sid, AsyncCallback<GUILdapSettings[]> callback);

	void saveExtAuthSettings(String sid, GUILdapSettings ldapSettings, GUIADSettings adSettings,
			AsyncCallback<Void> callback);

}
