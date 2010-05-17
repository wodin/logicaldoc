package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIParameter;

public interface SystemServiceAsync {

	void getStatistics(String sid, AsyncCallback<GUIParameter[][]> callback);

}
