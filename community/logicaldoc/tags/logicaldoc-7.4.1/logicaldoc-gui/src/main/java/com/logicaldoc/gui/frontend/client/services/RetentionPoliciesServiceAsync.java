package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIRetentionPolicy;

public interface RetentionPoliciesServiceAsync {

	void delete(String sid, long id, AsyncCallback<Void> callback);

	void save(String sid, GUIRetentionPolicy policy, AsyncCallback<GUIRetentionPolicy> callback);

	void getPolicy(String sid, long id, AsyncCallback<GUIRetentionPolicy> callback);

	void reorder(String sid, long[] ids, AsyncCallback<Void> callback);

	void changeStatus(String sid, long id, boolean enabled, AsyncCallback<Void> callback);
}
