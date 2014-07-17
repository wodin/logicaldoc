package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;

public interface WorkflowServiceAsync {

	void delete(String sid, String name, AsyncCallback<Void> callback);

	void get(String sid, String workflowName, AsyncCallback<GUIWorkflow> callback);

	void deploy(String sid, GUIWorkflow workflow, AsyncCallback<Void> callback);

	void list(String sid, AsyncCallback<GUIWorkflow[]> callback);

	void save(String sid, GUIWorkflow workflow, AsyncCallback<GUIWorkflow> callback);

	void deleteTrigger(String sid, long id, AsyncCallback<Void> callback);

	void saveTrigger(String sid, String folderId, String workflowId, String templateId, int startAtCheckin,
			AsyncCallback<Void> callback);

	void startWorkflow(String sid, String workflowName, String workflowDescription, long[] docIds,
			AsyncCallback<Void> callback);

	void getWorkflowDetailsByTask(String sid, String taskId, AsyncCallback<GUIWorkflow> callback);

	void endTask(String sid, String taskId, String transitionName, AsyncCallback<Void> callback);

	void claimTask(String sid, String taskId, String userId, AsyncCallback<GUIWorkflow> callback);

	void turnBackTaskToPool(String sid, String taskId, AsyncCallback<Void> callback);

	void countActiveUserTasks(String sid, String username, AsyncCallback<Integer> callback);

	void appendDocuments(String sid, String taskId, Long[] docIds, AsyncCallback<Void> callback);

	void importSchema(String sid, AsyncCallback<GUIWorkflow> callback);

	void applyTriggersToTree(String sid, long rootId, AsyncCallback<Void> callback);

	void deleteInstance(String sid, String id, AsyncCallback<Void> callback);

	void reassignTask(String sid, String taskId, String userId, AsyncCallback<GUIWorkflow> callback);

	void undeploy(String sid, String workflowName, AsyncCallback<Void> callback);

}
