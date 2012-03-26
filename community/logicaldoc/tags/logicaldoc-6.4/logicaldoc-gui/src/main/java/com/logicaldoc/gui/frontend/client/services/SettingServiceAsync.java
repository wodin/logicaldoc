package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

public interface SettingServiceAsync {

	void loadClientSettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void saveClientSettings(String sid, GUIParameter[] settings, AsyncCallback<Void> callback);

	void loadSettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void saveSettings(String sid, GUIParameter[] settings, AsyncCallback<Void> callback);

	void loadEmailSettings(String sid, AsyncCallback<GUIEmailSettings> callback);

	void saveEmailSettings(String sid, GUIEmailSettings settings, AsyncCallback<Void> callback);

	void loadValues(String sid, String[] names, AsyncCallback<String[]> callback);

	void loadRepositories(String sid, AsyncCallback<GUIParameter[][]> callback);

	void saveRepositories(String sid, GUIParameter[][] repos, AsyncCallback<Void> callback);

	void loadProxySettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void saveProxySettings(String sid, GUIParameter[] proxySettings, AsyncCallback<Void> callback);

	void loadOcrSettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void loadQuotaSettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void saveQuotaSettings(String sid, GUIParameter[] quotaSettings, AsyncCallback<Void> callback);

	void computeStoragesSize(String sid, AsyncCallback<GUIParameter[]> callback);

	void loadGUISettings(String sid, AsyncCallback<GUIParameter[]> callback);
}
