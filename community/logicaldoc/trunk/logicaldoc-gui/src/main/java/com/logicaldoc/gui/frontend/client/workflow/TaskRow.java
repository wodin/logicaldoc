package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.widgets.layout.VStack;

/**
 * A single task row, with transitions
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TaskRow extends WorkflowRow {
	public TaskRow() {
		super();

		state = new WorkflowState(WorkflowState.TYPE_TASK);
		addMember(state);

		VStack transitionsPanel = new VStack();
		for (int i = 0; i < 3; i++) {
			transitionsPanel.addMember(new Transition());
		}
		addMember(transitionsPanel);
	}
}