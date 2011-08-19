package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuditServiceAsync {

	void subscribeFolder(String sid, long folderId, boolean currentOnly, AsyncCallback<Void> callback);

	void subscribeDocuments(String sid, long[] docIds, AsyncCallback<Void> callback);

	void deleteSubscriptions(String sid, long[] ids, AsyncCallback<Void> callback);

}
