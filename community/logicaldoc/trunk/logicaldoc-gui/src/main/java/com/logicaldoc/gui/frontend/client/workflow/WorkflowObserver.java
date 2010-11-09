package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;

/**
 * Listener workflow events
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public interface WorkflowObserver {
	/**
	 * Invoked when the user selects a workflow state 'edit' link.
	 * 
	 * @param wfState The selected workflowState
	 */
	public void onStateSelect(GUIWFState wfState);

	/**
	 * Invoked when the user wants to delete a workflow state clicking on
	 * 'delete' link.
	 * 
	 * @param wfState The selected workflowState
	 */
	public void onStateDelete(GUIWFState wfState);

	/**
	 * Invoked when the user wants to delete a workflow transition clicking on
	 * 'delete Transition' link.
	 * 
	 * @param fromState The original state of the transition associated to the
	 *        dragged state
	 * @param transitionText The transition text
	 */
	public void onTransitionDelete(GUIWFState fromState, String transitionText);

	/**
	 * Invoked when the user wants to delete a workflow dragged state clicking
	 * on 'delete' link.
	 * 
	 * @param fromState The original state of the transition associated to the
	 *        dragged state
	 * @param transitionText The transition text
	 */
	public void onDraggedStateDelete(GUIWFState fromState, String transition);

	/**
	 * Invoked when the user wants to add a new task transition.
	 * 
	 * @param fromState The original state of the transition associated to the
	 *        dragged state
	 * @param targetState The new workflowState, target of the new transition
	 * @param transitionText The text of transition on which is dragged the
	 *        targetState
	 */
	public void onAddTransition(GUIWFState fromState, GUIWFState targetState, String transitionText);

	/**
	 * Invoked when the user wants to add a new workflow state.
	 * 
	 * @param workflow The workflow.
	 * @param type The workflow state type.
	 */
	public void onAddState(GUIWorkflow workflow, int type);
}
