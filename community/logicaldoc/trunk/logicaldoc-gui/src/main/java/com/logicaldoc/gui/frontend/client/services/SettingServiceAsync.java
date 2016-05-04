package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIDashlet;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

public interface SettingServiceAsync {

	void loadClientSettings(AsyncCallback<GUIParameter[]> callback);

	void saveSettings(GUIParameter[] settings, AsyncCallback<Void> callback);

	void loadEmailSettings(AsyncCallback<GUIEmailSettings> callback);

	void saveEmailSettings(GUIEmailSettings settings, AsyncCallback<Void> callback);

	void loadRepositories(AsyncCallback<GUIParameter[][]> callback);

	void saveRepositories(GUIParameter[][] repos, AsyncCallback<Void> callback);

	void loadOcrSettings(AsyncCallback<GUIParameter[]> callback);

	void computeStoragesSize(AsyncCallback<GUIParameter[]> callback);

	void loadGUISettings(AsyncCallback<GUIParameter[]> callback);

	void loadSettingsByNames(String[] names, AsyncCallback<GUIParameter[]> callback);

	void loadSettings(AsyncCallback<GUIParameter[]> callback);

	void saveDashlets(GUIDashlet[] dashlets, AsyncCallback<Void> callback);

	void testEmail(String email, AsyncCallback<Boolean> callback);
}
