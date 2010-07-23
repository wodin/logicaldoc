package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWorkflow;

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
	// TODO Not type, but the GUIWFState
	public void onStateSelect(int type);

	/**
	 * Invoked when the user selects a workflow template.
	 * 
	 * @param workflow The selected workflow
	 */
	public void onWorkflowSelect(GUIWorkflow workflow);
}
