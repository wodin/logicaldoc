package com.logicaldoc.gui.frontend.client.services;

import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIHistory;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITask;
import com.logicaldoc.gui.common.client.beans.GUIValue;

public interface SystemServiceAsync {

	void getStatistics(String sid, String locale, AsyncCallback<GUIParameter[][]> callback);

	void search(String sid, String userName, Date from, Date till, int maxResult, String historySid, String[] event,
			AsyncCallback<GUIHistory[]> callback);

	void loadTasks(String sid, String locale, AsyncCallback<GUITask[]> callback);

	void startTask(String taskName, AsyncCallback<Boolean> callback);

	void stopTask(String taskName, AsyncCallback<Boolean> callback);

	void getTaskByName(String sid, String taskName, String locale, AsyncCallback<GUITask> callback);

	void disableTask(String sid, String taskName, AsyncCallback<Boolean> callback);

	void enableTask(String sid, String taskName, AsyncCallback<Boolean> callback);

	void saveTask(String sid, GUITask task, String locale, AsyncCallback<GUITask> callback);

	void setGUILanguageStatus(String sid, String language, boolean active, AsyncCallback<Void> callback);

	void deleteFeedMessages(String sid, long[] ids, AsyncCallback<Void> callback);

	void markFeedMsgAsNotRead(String sid, long[] ids, AsyncCallback<Void> callback);

	void markFeedMsgAsRead(String sid, long[] ids, AsyncCallback<Void> callback);

	void getPlugins(String sid, AsyncCallback<GUIValue[]> callback);
}