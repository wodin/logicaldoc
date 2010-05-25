package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;

public interface SearchEngineServiceAsync {

	void getInfos(String sid, AsyncCallback<GUISearchEngine> callback);

	void rescheduleAll(String sid, GUISearchEngine searchEngine, AsyncCallback<GUISearchEngine> callback);

	void unlocks(String sid, GUISearchEngine searchEngine, AsyncCallback<GUISearchEngine> callback);

	void save(String sid, GUISearchEngine searchEngine, AsyncCallback<Void> callback);
}
