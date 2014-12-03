package com.logicaldoc.gui.frontend.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.logicaldoc.gui.common.client.ServerException;
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
	public GUIWorkflow get(String sid, String workflowName) throws ServerException;

	/**
	 * Deletes a given workflow
	 */
	public void delete(String sid, String workflowName) throws ServerException;

	/**
	 * Deletes a given workflow instance
	 */
	public void deleteInstance(String sid, String id) throws ServerException;

	/**
	 * Imports a new workflow schema.
	 */
	public GUIWorkflow importSchema(String sid) throws ServerException;

	/**
	 * Creates or updates a workflow
	 */
	public GUIWorkflow save(String sid, GUIWorkflow workflow) throws ServerException;

	/**
	 * Deploys a given workflow
	 */
	public void deploy(String sid, GUIWorkflow workflow) throws ServerException;

	/**
	 * Undeploys a given workflow
	 */
	public void undeploy(String sid, String workflowName) throws ServerException;

	/**
	 * Lists all the workflows on the database
	 */
	public GUIWorkflow[] list(String sid) throws ServerException;

	/**
	 * Deletes a workflow trigger
	 */
	public void deleteTrigger(String sid, long id) throws ServerException;

	/**
	 * Applies the triggers on a root folder to all the subtree
	 */
	public void applyTriggersToTree(String sid, long rootId) throws ServerException;

	/**
	 * Save a new workflow trigger on the given folder with the given workflowId
	 * and templateId.
	 */
	public void saveTrigger(String sid, String folderId, String workflowId, String templateId, int startAtCheckin)
			throws ServerException;

	/**
	 * Start a workflow with the given name and associated to the documents with
	 * the given doc ids.
	 */
	public void startWorkflow(String sid, String workflowName, String workflowDescription, long[] docIds)
			throws ServerException;

	/**
	 * Retrieves all the info of the workflow of the given task.
	 */
	public GUIWorkflow getWorkflowDetailsByTask(String sid, String taskId) throws ServerException;

	/**
	 * The given user take the ownership of the task. If the task is already
	 * claimed you cannot claim again.
	 */
	public GUIWorkflow claimTask(String sid, String taskId, String userId) throws ServerException;

	/**
	 * The task is assigned to another user
	 */
	public GUIWorkflow reassignTask(String sid, String taskId, String userId) throws ServerException;

	/**
	 * The task is reassigned to the pooled users.
	 */
	public void turnBackTaskToPool(String sid, String taskId) throws ServerException;

	/**
	 * Ends a task invoking the transition.
	 */
	public void endTask(String sid, String taskId, String transitionName) throws ServerException;

	/**
	 * Counts all the tasks assigned to the given user.
	 */
	public int countActiveUserTasks(String sid, String username) throws ServerException;

	/**
	 * Appends to the workflow of the given taskId the documents with the given
	 * doc ids.
	 */
	public void appendDocuments(String sid, String taskId, Long[] docIds) throws ServerException;
}