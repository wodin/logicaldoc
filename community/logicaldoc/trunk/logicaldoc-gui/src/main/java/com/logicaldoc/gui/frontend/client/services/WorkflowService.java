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
	public void startWorkflow(String sid, String workflowName, String workflowDescription, String docIds) throws InvalidSessionException;
}
