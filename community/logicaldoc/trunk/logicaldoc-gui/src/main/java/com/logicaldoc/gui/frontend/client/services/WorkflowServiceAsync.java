package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;

public interface WorkflowServiceAsync {

	void delete(String name, AsyncCallback<Void> callback);

	void get(String workflowName, AsyncCallback<GUIWorkflow> callback);

	void deploy(GUIWorkflow workflow, AsyncCallback<Void> callback);

	void list(AsyncCallback<GUIWorkflow[]> callback);

	void save(GUIWorkflow workflow, AsyncCallback<GUIWorkflow> callback);

	void deleteTrigger(long id, AsyncCallback<Void> callback);

	void saveTrigger(String folderId, String workflowId, String templateId, int startAtCheckin,
			AsyncCallback<Void> callback);

	void startWorkflow(String workflowName, String workflowDescription, long[] docIds,
			AsyncCallback<Void> callback);

	void getWorkflowDetailsByTask(String taskId, AsyncCallback<GUIWorkflow> callback);

	void endTask(String taskId, String transitionName, AsyncCallback<Void> callback);

	void claimTask(String taskId, String userId, AsyncCallback<GUIWorkflow> callback);

	void turnBackTaskToPool(String taskId, AsyncCallback<Void> callback);

	void countActiveUserTasks(String username, AsyncCallback<Integer> callback);

	void appendDocuments(String taskId, Long[] docIds, AsyncCallback<Void> callback);

	void importSchema(AsyncCallback<GUIWorkflow> callback);

	void applyTriggersToTree(long rootId, AsyncCallback<Void> callback);

	void deleteInstance(String id, AsyncCallback<Void> callback);

	void reassignTask(String taskId, String userId, AsyncCallback<GUIWorkflow> callback);

	void undeploy(String workflowName, AsyncCallback<Void> callback);

}
