package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

public interface UpdateServiceAsync {

	void checkUpdate(String userNo, String currentRelease, AsyncCallback<GUIParameter[]> callback);

}
