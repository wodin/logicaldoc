package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;

/**
 * Row containing the end state.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class EndRow extends WorkflowRow {

	public EndRow(WorkflowDesigner designer, GUIWFState wfState) {
		super();

		state = new WorkflowState(designer, wfState);
		addMember(state);
	}
}