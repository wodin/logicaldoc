package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUICustomId;
import com.logicaldoc.gui.common.client.beans.GUISequence;

public interface CustomIdServiceAsync {

	void delete(String sid, long templateId, AsyncCallback<Void> callback);

	void get(String sid, long id, AsyncCallback<GUICustomId> callback);

	void load(String sid, AsyncCallback<GUICustomId[]> callback);

	void save(String sid, GUICustomId customid, AsyncCallback<Void> callback);

	void resetSequence(String sid, long sequenceId, long value, AsyncCallback<Void> callback);

	void loadSequences(String sid, AsyncCallback<GUISequence[]> callback);

	void deleteSequence(String sid, long sequenceId, AsyncCallback<Void> callback);

}
