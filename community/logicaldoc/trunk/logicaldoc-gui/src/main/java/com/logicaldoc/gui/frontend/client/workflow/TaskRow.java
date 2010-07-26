package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * A single task row, with transitions
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TaskRow extends WorkflowRow {
	public TaskRow(WorkflowDesigner designer, GUIWFState wfState) {
		super();

		state = new WorkflowState(designer, wfState);
		addMember(state);

		if (wfState.getTransitions() != null && wfState.getTransitions().size() > 0) {
			VStack transitionsPanel = new VStack();
			for (String transitionLabel : wfState.getTransitions().keySet()) {
				transitionsPanel.addMember(new Transition(designer, transitionLabel, wfState, wfState.getTransitions()
						.get(transitionLabel)));
			}
			addMember(transitionsPanel);
		}
	}
}