package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIRight;
import com.logicaldoc.gui.common.client.beans.GUISession;

public interface SecurityServiceAsync {
	public void login(String username, String password, AsyncCallback<GUISession> callback);

	void logout(String sid, AsyncCallback<Void> callback);

	void getSecurityEntities(String sid, AsyncCallback<GUIRight[]> callback);

	void changePassword(long userId, String oldPassword, String newPassword, AsyncCallback<Integer> callback);
}
