package com.logicaldoc.gui.frontend.client.services;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SelectionServiceAsync {

	void getLanguages(AsyncCallback<Map<String, String>> callback);

}
