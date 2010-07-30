package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;

public interface SettingServiceAsync {

	void loadWSSettings(String sid, AsyncCallback<GUIWebServiceSettings[]> callback);

	void saveWSSettings(String sid, GUIWebServiceSettings wsSettings, GUIWebServiceSettings webDavSettings,
			AsyncCallback<Void> callback);

	void loadSettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void saveSettings(String sid, GUIParameter[] settings, AsyncCallback<Void> callback);

	void loadEmailSettings(String sid, AsyncCallback<GUIEmailSettings> callback);

	void saveEmailSettings(String sid, GUIEmailSettings settings, AsyncCallback<Void> callback);

	void loadValues(String sid, String[] names, AsyncCallback<String[]> callback);
}
