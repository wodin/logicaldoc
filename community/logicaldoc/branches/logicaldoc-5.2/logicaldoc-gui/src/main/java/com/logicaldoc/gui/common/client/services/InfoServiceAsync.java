package com.logicaldoc.gui.common.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIInfo;

public interface InfoServiceAsync {

	void getInfo(String locale, AsyncCallback<GUIInfo> callback);

}
