package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIResult;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;

public interface SearchServiceAsync {

	void search(String sid, GUISearchOptions options, AsyncCallback<GUIResult> callback);

	void save(String sid, GUISearchOptions options, AsyncCallback<Boolean> callback);

	void load(String sid, String name, AsyncCallback<GUISearchOptions> callback);

	void delete(String sid, String[] names, AsyncCallback<Void> callback);
}
