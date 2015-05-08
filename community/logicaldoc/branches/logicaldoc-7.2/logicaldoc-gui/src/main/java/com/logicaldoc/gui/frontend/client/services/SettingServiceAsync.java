package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIDashlet;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

public interface SettingServiceAsync {

	void loadClientSettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void saveSettings(String sid, GUIParameter[] settings, AsyncCallback<Void> callback);

	void loadEmailSettings(String sid, AsyncCallback<GUIEmailSettings> callback);

	void saveEmailSettings(String sid, GUIEmailSettings settings, AsyncCallback<Void> callback);

	void loadRepositories(String sid, AsyncCallback<GUIParameter[][]> callback);

	void saveRepositories(String sid, GUIParameter[][] repos, AsyncCallback<Void> callback);

	void loadOcrSettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void computeStoragesSize(String sid, AsyncCallback<GUIParameter[]> callback);

	void loadGUISettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void loadSettingsByNames(String sid, String[] names, AsyncCallback<GUIParameter[]> callback);

	void loadSettings(String sid, AsyncCallback<GUIParameter[]> callback);

	void saveDashlets(String sid, GUIDashlet[] dashlets, AsyncCallback<Void> callback);

	void testEmail(String sid, String email, AsyncCallback<Boolean> callback);
}
