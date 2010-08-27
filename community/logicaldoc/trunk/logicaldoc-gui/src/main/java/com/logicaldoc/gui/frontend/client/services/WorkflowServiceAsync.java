package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;

public interface WorkflowServiceAsync {

	void delete(String sid, String workflowName, AsyncCallback<Void> callback);

	void get(String sid, String workflowName, AsyncCallback<GUIWorkflow> callback);

	void deploy(String sid, GUIWorkflow workflow, AsyncCallback<Void> callback);

	void list(String sid, AsyncCallback<GUIWorkflow[]> callback);

	void save(String sid, GUIWorkflow workflow, AsyncCallback<GUIWorkflow> callback);

	void deleteTrigger(String sid, String subtype, AsyncCallback<Void> callback);

	void saveTrigger(String sid, String folderId, String workflowId, String templateId, AsyncCallback<Void> callback);

	void startWorkflow(String sid, String workflowName, String workflowDescription, String docIds,
			AsyncCallback<Void> callback);
}
