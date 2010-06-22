package com.logicaldoc.gui.frontend.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;

public interface SystemServiceAsync {

	void getStatistics(String sid, AsyncCallback<GUIParameter[][]> callback);

	void search(String sid, String userName, Date from, Date till, int maxResult, String historySid, String[] event,
			AsyncCallback<GUIHistory[]> callback);

	void loadTasks(String sid, AsyncCallback<GUITask[]> callback);

	void startTask(String taskName, AsyncCallback<Boolean> callback);

	void stopTask(String taskName, AsyncCallback<Boolean> callback);

	void getTaskByName(String sid, String taskName, AsyncCallback<GUITask> callback);

	void disableTask(String sid, String taskName, AsyncCallback<Boolean> callback);

	void enableTask(String sid, String taskName, AsyncCallback<Boolean> callback);

	void saveTask(String sid, GUITask task, AsyncCallback<GUITask> callback);

	void saveFolders(String sid, GUIParameter[] folders, AsyncCallback<Void> callback);

	void getInfo(String locale, AsyncCallback<GUIInfo> callback);

	void getBundle(String locale, AsyncCallback<GUIValuePair[]> callback);
}