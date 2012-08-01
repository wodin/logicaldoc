package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIMessageTemplate;

public interface MessageServiceAsync {

	void delete(String sid, long[] ids, AsyncCallback<Void> callback);

	void getMessage(String sid, long messageId, boolean markAsRead, AsyncCallback<GUIMessage> callback);

	void save(String sid, GUIMessage message, AsyncCallback<Void> callback);

	void loadTemplates(String sid, String language, AsyncCallback<GUIMessageTemplate[]> callback);

	void saveTemplates(String sid, GUIMessageTemplate[] templates, AsyncCallback<Void> callback);

	void deleteTemplates(String sid, long[] ids, AsyncCallback<Void> callback);

}
