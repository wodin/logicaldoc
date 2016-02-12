package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DropboxServiceAsync {

	void finishAuthorization(String sid, String authorizationCode, AsyncCallback<String> callback);

	void isConnected(String sid, AsyncCallback<Boolean> callback);

	void startAuthorization(String sid, AsyncCallback<String> callback);

	void exportDocuments(String sid, String targetPath, long[] folderIds, long[] docIds, AsyncCallback<Boolean> callback);

	void importDocuments(String sid, long targetFolder, String[] paths, AsyncCallback<Integer> callback);

}
