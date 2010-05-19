package com.logicaldoc.gui.frontend.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITask;

public interface SystemServiceAsync {

	void getStatistics(String sid, AsyncCallback<GUIParameter[][]> callback);

	void search(String sid, String userName, Date from, Date till, int maxResult, String historySid, String[] event, AsyncCallback<GUIHistory[]> callback);

	void loadTasks(String sid, AsyncCallback<GUITask[]> callback);
}