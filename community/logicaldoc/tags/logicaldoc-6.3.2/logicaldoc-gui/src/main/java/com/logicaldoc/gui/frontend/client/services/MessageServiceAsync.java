package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIMessage;

public interface MessageServiceAsync {

	void delete(String sid, long[] ids, AsyncCallback<Void> callback);

	void getMessage(String sid, long messageId, boolean markAsRead, AsyncCallback<GUIMessage> callback);

	void save(String sid, GUIMessage message, AsyncCallback<Void> callback);

}
