package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIFolder;

public interface FolderServiceAsync {

	void save(String sid, GUIFolder folder, AsyncCallback<GUIFolder> callback);

	void applyRightsToTree(String sid, long folderId, AsyncCallback<Void> callback);

	void delete(String sid, long folderId, AsyncCallback<Void> callback);

	void getFolder(String sid, long folderId, boolean computePath, AsyncCallback<GUIFolder> callback);

	void move(String sid, long folderId, long targetId, AsyncCallback<Void> callback);

	void rename(String sid, long folderId, String name, AsyncCallback<Void> callback);

	void paste(String sid, long[] docIds, long folderId, String action, AsyncCallback<Void> callback);

	void pasteAsAlias(String sid, long[] docIds, long folderId, AsyncCallback<Void> callback);
}
