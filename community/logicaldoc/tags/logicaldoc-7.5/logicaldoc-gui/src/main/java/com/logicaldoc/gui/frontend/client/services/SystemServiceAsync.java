package com.logicaldoc.gui.frontend.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.beans.GUIValue;

public interface SystemServiceAsync {

	void getStatistics(String locale, AsyncCallback<GUIParameter[][]> callback);

	void search(String userName, Date from, Date till, int maxResult, String historySid, String[] event,
			AsyncCallback<GUIHistory[]> callback);

	void loadTasks(String locale, AsyncCallback<GUITask[]> callback);

	void startTask(String taskName, AsyncCallback<Boolean> callback);

	void stopTask(String taskName, AsyncCallback<Boolean> callback);

	void getTaskByName(String taskName, String locale, AsyncCallback<GUITask> callback);

	void disableTask(String taskName, AsyncCallback<Boolean> callback);

	void enableTask(String taskName, AsyncCallback<Boolean> callback);

	void saveTask(GUITask task, String locale, AsyncCallback<GUITask> callback);

	void setGUILanguageStatus(String language, boolean active, AsyncCallback<Void> callback);

	void deleteFeedMessages(long[] ids, AsyncCallback<Void> callback);

	void markFeedMsgAsNotRead(long[] ids, AsyncCallback<Void> callback);

	void markFeedMsgAsRead(long[] ids, AsyncCallback<Void> callback);

	void getPlugins(AsyncCallback<GUIValue[]> callback);
}