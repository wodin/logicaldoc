package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;

public interface SearchEngineServiceAsync {

	void getInfo(String sid, AsyncCallback<GUISearchEngine> callback);

	void rescheduleAll(String sid, boolean dropIndex, AsyncCallback<Void> callback);

	void unlocks(String sid, AsyncCallback<Void> callback);

	void save(String sid, GUISearchEngine searchEngine, AsyncCallback<Void> callback);

	void setLanguageStatus(String sid, String language, boolean active, AsyncCallback<Void> callback);

	void check(String sid, AsyncCallback<String> callback);

	void setAliases(String sid, String extension, String aliases, AsyncCallback<Void> callback);
}
