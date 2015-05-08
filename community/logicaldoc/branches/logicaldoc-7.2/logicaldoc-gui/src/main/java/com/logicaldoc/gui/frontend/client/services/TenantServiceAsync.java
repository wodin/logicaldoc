package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUITenant;

public interface TenantServiceAsync {
	void delete(String sid, long tenantId, AsyncCallback<Void> callback);

	void save(String sid, GUITenant tenant, AsyncCallback<GUITenant> callback);

	void load(String sid, long tenantId, AsyncCallback<GUITenant> callback);

	void changeAdminPassword(String sid, String password, String tenantName, AsyncCallback<Void> callback);

	void changeSessionTenant(String sid, long tenantId, AsyncCallback<GUITenant> callback);
}
