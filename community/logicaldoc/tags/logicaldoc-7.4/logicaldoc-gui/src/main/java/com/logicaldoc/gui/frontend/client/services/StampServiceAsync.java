package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIStamp;

public interface StampServiceAsync {

	void delete(String sid, long id, AsyncCallback<Void> callback);

	void save(String sid, GUIStamp stamp, AsyncCallback<GUIStamp> callback);

	void getStamp(String sid, long id, AsyncCallback<GUIStamp> callback);

	void changeStatus(String sid, long id, boolean enabled, AsyncCallback<Void> callback);

	void saveImage(String sid, long stampId, AsyncCallback<Void> callback);

	void applyStamp(String sid, long[] docIds, long stampId, AsyncCallback<Void> callback);

	void removeUsers(String sid, long[] userIds, long stampId, AsyncCallback<Void> callback);

	void addUsers(String sid, long[] userIds, long stampId, AsyncCallback<Void> callback);
}