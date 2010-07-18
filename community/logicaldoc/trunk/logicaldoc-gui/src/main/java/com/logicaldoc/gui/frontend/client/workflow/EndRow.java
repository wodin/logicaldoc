package com.logicaldoc.gui.frontend.client.workflow;

/**
 * Row containing the end state.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class EndRow extends WorkflowRow {

	public EndRow() {
		super();

		state = new WorkflowState(WorkflowState.TYPE_END);
		addMember(state);
	}
}