package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;

public interface SearchEngineServiceAsync {

	void getInfo(String sid, AsyncCallback<GUISearchEngine> callback);

	void rescheduleAll(String sid, AsyncCallback<Void> callback);

	void unlocks(String sid, AsyncCallback<Void> callback);

	void save(String sid, GUISearchEngine searchEngine, AsyncCallback<Void> callback);
}
