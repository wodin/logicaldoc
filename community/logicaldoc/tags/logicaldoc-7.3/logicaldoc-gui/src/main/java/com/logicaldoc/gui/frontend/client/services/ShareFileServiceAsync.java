package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ShareFileServiceAsync {

	void exportDocuments(String sid, String targetFolder, long[] folderIds, long[] docIds,
			AsyncCallback<Boolean> callback);

	void importDocuments(String sid, long targetFolder, String[] itemIds, AsyncCallback<Integer> callback);

	void loadSettings(String sid, AsyncCallback<String[]> callback);

	void saveSettings(String sid, String hostname, String username, String password, AsyncCallback<Void> callback);

}
