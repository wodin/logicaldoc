package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIEmailAccount;

public interface EmailAccountServiceAsync {

	void changeStatus(String sid, long id, boolean enabled, AsyncCallback<Void> callback);

	void delete(String sid, long id, AsyncCallback<Void> callback);

	void get(String sid, long id, AsyncCallback<GUIEmailAccount> callback);

	void resetCache(String sid, long id, AsyncCallback<Void> callback);

	void save(String sid, GUIEmailAccount account, AsyncCallback<GUIEmailAccount> callback);

	void test(String sid, long id, AsyncCallback<Boolean> callback);

}
