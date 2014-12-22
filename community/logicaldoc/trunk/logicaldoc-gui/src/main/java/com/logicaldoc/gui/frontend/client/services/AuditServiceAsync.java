package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuditServiceAsync {

	void subscribeFolder(String sid, long folderId, boolean currentOnly, String[] events, Long user, Long group,
			AsyncCallback<Void> callback);

	void subscribeDocuments(String sid, long[] docIds, String[] events, Long user, Long group,
			AsyncCallback<Void> callback);

	void deleteSubscriptions(String sid, long[] ids, AsyncCallback<Void> callback);

	void update(String sid, long[] ids, boolean currentOnly, String[] events, AsyncCallback<Void> callback);
}
