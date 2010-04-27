package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIFolder;

public interface FolderServiceAsync {

	void save(String sid, GUIFolder folder, AsyncCallback<GUIFolder> callback);

	void applyRightsToTree(String sid, long folderId, AsyncCallback<Void> callback);

	void delete(String sid, long folderId, AsyncCallback<Void> callback);

	void getFolder(String sid, long folderId, boolean computePath, AsyncCallback<GUIFolder> callback);
}
