package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUITransition;
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

		if (wfState.getTransitions() != null && wfState.getTransitions().length > 0) {
			VStack transitionsPanel = new VStack();
			for (GUITransition transition : wfState.getTransitions()) {
				if (transition.getTargetState().getType() != GUIWFState.TYPE_UNDEFINED)
					transitionsPanel.addMember(new Transition(designer, transition, wfState));
				else
					transitionsPanel.addMember(new Transition(designer, transition, wfState));
			}
			addMember(transitionsPanel);
		}
	}
}