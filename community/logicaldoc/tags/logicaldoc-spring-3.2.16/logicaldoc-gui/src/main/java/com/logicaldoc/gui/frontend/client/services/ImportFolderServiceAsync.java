package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIImportFolder;

public interface ImportFolderServiceAsync {

	void delete(String sid, long id, AsyncCallback<Void> callback);

	void save(String sid, GUIImportFolder share, AsyncCallback<GUIImportFolder> callback);

	void test(String sid, long id, AsyncCallback<Boolean> callback);

	void changeStatus(String sid, long id, boolean enabled, AsyncCallback<Void> callback);

	void resetCache(String sid, long id, AsyncCallback<Void> callback);

	void getImportFolder(String sid, long id, AsyncCallback<GUIImportFolder> callback);
}
