package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUITag;

public interface TagServiceAsync {
	void delete(String sid, String tag, AsyncCallback<Void> callback);

	void getTagCloud(String sid, AsyncCallback<GUITag[]> callback);

	void rename(String sid, String tag, String newTag, AsyncCallback<Void> callback);

	void addTag(String sid, String tag, AsyncCallback<Void> callback);

	void removeTag(String sid, String tag, AsyncCallback<Void> callback);

	void getSettings(String sid, AsyncCallback<GUIParameter[]> callback);
}