package com.logicaldoc.gui.common.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

public interface InfoServiceAsync {

	public void getInfo(String locale, AsyncCallback<GUIInfo> callback);

	void getSessionInfo(String sid, AsyncCallback<GUIParameter[]> callback);

}
