package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIResult;
import com.logicaldoc.gui.common.client.beans.GUISearchOptions;

public interface SearchServiceAsync {

	void search(String sid, GUISearchOptions options, AsyncCallback<GUIResult> callback);
}
