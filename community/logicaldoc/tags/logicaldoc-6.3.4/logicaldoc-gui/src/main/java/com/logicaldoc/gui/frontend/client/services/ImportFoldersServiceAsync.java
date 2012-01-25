package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIShare;

public interface ImportFoldersServiceAsync {

	void delete(String sid, long id, AsyncCallback<Void> callback);

	void getShare(String sid, long id, AsyncCallback<GUIShare> callback);

	void save(String sid, GUIShare share, AsyncCallback<GUIShare> callback);

	void test(String sid, long id, AsyncCallback<Boolean> callback);

	void changeStatus(String sid, long id, boolean enabled, AsyncCallback<Void> callback);

	void resetCache(String sid, long id, AsyncCallback<Void> callback);
}
