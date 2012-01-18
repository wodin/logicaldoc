package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;

public interface WorkflowServiceAsync {

	void deleteInstance(String sid, long id, AsyncCallback<Void> callback);

	void delete(String sid, String name, AsyncCallback<Void> callback);

	void get(String sid, String workflowName, AsyncCallback<GUIWorkflow> callback);

	void deploy(String sid, GUIWorkflow workflow, AsyncCallback<Void> callback);

	void list(String sid, AsyncCallback<GUIWorkflow[]> callback);

	void save(String sid, GUIWorkflow workflow, AsyncCallback<GUIWorkflow> callback);

	void deleteTrigger(String sid, String subtype, AsyncCallback<Void> callback);

	void saveTrigger(String sid, String folderId, String workflowId, String templateId, AsyncCallback<Void> callback);

	void startWorkflow(String sid, String workflowName, String workflowDescription, String docIds,
			AsyncCallback<Void> callback);

	void getWorkflowDetailsByTask(String sid, String taskId, AsyncCallback<GUIWorkflow> callback);

	void saveTaskAssignment(String sid, String taskId, String userId, AsyncCallback<Void> callback);

	void startTask(String sid, String taskId, String comment, AsyncCallback<Void> callback);

	void resumeTask(String sid, String taskId, String comment, AsyncCallback<Void> callback);

	void suspendTask(String sid, String taskId, String comment, AsyncCallback<Void> callback);

	void saveTaskState(String sid, String taskId, String comment, AsyncCallback<Void> callback);

	void endTask(String sid, String taskId, String transitionName, String comment, AsyncCallback<Void> callback);

	void takeTaskOwnerShip(String sid, String taskId, String userId, String comment, AsyncCallback<Void> callback);

	void turnBackTaskToPool(String sid, String taskId, String comment, AsyncCallback<Void> callback);

	void countActiveUserTasks(String sid, String username, AsyncCallback<Integer> callback);

	void appendDocuments(String sid, String taskId, String docIds, AsyncCallback<Void> callback);

	void importSchema(String sid, AsyncCallback<GUIWorkflow> callback);

}
