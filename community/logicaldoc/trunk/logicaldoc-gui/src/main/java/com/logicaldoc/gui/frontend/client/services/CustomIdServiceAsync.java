package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUICustomId;

public interface CustomIdServiceAsync {

	void delete(String sid, long templateId, AsyncCallback<Void> callback);

	void get(String sid, long id, AsyncCallback<GUICustomId> callback);

	void load(String sid, AsyncCallback<GUICustomId[]> callback);

	void save(String sid, GUICustomId customid, AsyncCallback<Void> callback);

	void reset(String sid, long templateId, AsyncCallback<Void> callback);

}
