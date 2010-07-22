package com.logicaldoc.gui.frontend.client.workflow;

/**
 * Listener on folders events
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public interface WorkflowObserver {
	/**
	 * Invoked when the user selects a workflow state 'edit' link.
	 * 
	 * @param workflowState The selected workflowState
	 */
	// public void onFolderSelect(GUIFolder workflowState);
	public void onStateSelect(int type);

	/**
	 * Invoked when the user selects a workflow template.
	 * 
	 * @param workflow The selected workflow
	 */
	// public void onFolderSelect(GUIWorkflow workflow);
	public void onWorkflowSelect();
}
