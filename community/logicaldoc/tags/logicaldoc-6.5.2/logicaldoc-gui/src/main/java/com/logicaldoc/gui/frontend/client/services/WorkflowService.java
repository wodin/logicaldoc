package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;

/**
 * The client side stub for the Workflow Service. This service gives all needed
 * methods to handle workflows.
 */
@RemoteServiceRelativePath("workflow")
public interface WorkflowService extends RemoteService {
	/**
	 * Loads a given workflow from the database
	 */
	public GUIWorkflow get(String sid, String workflowName) throws InvalidSessionException;

	/**
	 * Deletes a given workflow
	 */
	public void delete(String sid, String workflowName) throws InvalidSessionException;

	/**
	 * Deletes a given workflow instance
	 */
	public void deleteInstance(String sid, long id) throws InvalidSessionException;
	
	/**
	 * Imports a new workflow schema.
	 */
	public GUIWorkflow importSchema(String sid) throws InvalidSessionException;

	/**
	 * Creates or updates a workflow
	 */
	public GUIWorkflow save(String sid, GUIWorkflow workflow) throws InvalidSessionException;

	/**
	 * Deploys a given workflow
	 */
	public void deploy(String sid, GUIWorkflow workflow) throws InvalidSessionException;

	/**
	 * Lists all the workflows on the database
	 */
	public GUIWorkflow[] list(String sid) throws InvalidSessionException;

	/**
	 * Deletes a workflow trigger with the given subtype.
	 */
	public void deleteTrigger(String sid, String subtype) throws InvalidSessionException;

	/**
	 * Save a new workflow trigger on the given folder with the given workflowId
	 * and templateId.
	 */
	public void saveTrigger(String sid, String folderId, String workflowId, String templateId)
			throws InvalidSessionException;

	/**
	 * Start a workflow with the given name and associated to the documents with
	 * the given doc ids.
	 */
	public void startWorkflow(String sid, String workflowName, String workflowDescription, String docIds)
			throws InvalidSessionException;

	/**
	 * Retrieves all the info of the workflow of the given task.
	 */
	public GUIWorkflow getWorkflowDetailsByTask(String sid, String taskId) throws InvalidSessionException;

	/**
	 * Save the new assignment on the selected task.
	 */
	public void saveTaskAssignment(String sid, String taskId, String userId) throws InvalidSessionException;

	/**
	 * Starts a workflow task.
	 */
	public void startTask(String sid, String taskId, String comment) throws InvalidSessionException;

	/**
	 * Suspends a workflow task.
	 */
	public void suspendTask(String sid, String taskId, String comment) throws InvalidSessionException;

	/**
	 * Resumes a workflow task.
	 */
	public void resumeTask(String sid, String taskId, String comment) throws InvalidSessionException;

	/**
	 * Saves a workflow task state.
	 */
	public void saveTaskState(String sid, String taskId, String comment) throws InvalidSessionException;

	/**
	 * The given user take the ownership of the task.
	 */
	public void takeTaskOwnerShip(String sid, String taskId, String userId, String comment)
			throws InvalidSessionException;

	/**
	 * The task is reassigned to the pooled users.
	 */
	public void turnBackTaskToPool(String sid, String taskId, String comment) throws InvalidSessionException;

	/**
	 * Ends a task invoking the transition.
	 */
	public void endTask(String sid, String taskId, String transitionName, String comment)
			throws InvalidSessionException;

	/**
	 * Counts all the tasks assigned to the given user.
	 */
	public int countActiveUserTasks(String sid, String username) throws InvalidSessionException;

	/**
	 * Appends to the workflow of the given taskId the documents with the given
	 * doc ids.
	 */
	public void appendDocuments(String sid, String taskId, String docIds) throws InvalidSessionException;
}
