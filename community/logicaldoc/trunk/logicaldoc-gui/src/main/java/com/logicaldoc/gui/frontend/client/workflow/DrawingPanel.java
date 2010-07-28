package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * In this panel the grapical design of the workflow takes place.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DrawingPanel extends VStack {

	private WorkflowDesigner workflowDesigner;

	public DrawingPanel(WorkflowDesigner designer) {
		super();
		setHeight(557);
		setMembersMargin(5);
		setCanAcceptDrop(true);
		setAnimateMembers(true);
		setShowDragPlaceHolder(true);
		setDropTypes("row");
		setShowCustomScrollbars(true);
		setOverflow(Overflow.SCROLL);

		this.workflowDesigner = designer;

		if (workflowDesigner.getWorkflow() != null) {
			// The first element must be the workflow task startState
			GUIWorkflow workflow = workflowDesigner.getWorkflow();
			GUIWFState startState = null;
			if (workflow.getStartState() != null && !workflow.getStartState().trim().isEmpty()) {
				startState = workflow.getStateByName(workflow.getStartState());
				addMember(new TaskRow(designer, startState));
			}
			if (workflow.getStates() != null) {
				for (GUIWFState state : workflow.getStates()) {
					if (state == null || (startState != null && state.getName() == startState.getName()))
						continue;

					if (state.getType() == GUIWFState.TYPE_TASK)
						addMember(new TaskRow(designer, state));
					else if (state.getType() == GUIWFState.TYPE_FORK)
						addMember(new ForkRow(designer, state));
					else if (state.getType() == GUIWFState.TYPE_JOIN)
						addMember(new JoinRow(designer, state));
					else if (state.getType() == GUIWFState.TYPE_END)
						addMember(new EndRow(designer, state));
				}
			}
		}

		addDropHandler(new DropHandler() {
			public void onDrop(DropEvent event) {
				WorkflowRow row = null;
				if (EventHandler.getDragTarget() instanceof WorkflowRow) {
					row = (WorkflowRow) EventHandler.getDragTarget();
					if (getDropPosition() == 0 && row.getState().getWfState().getType() != GUIWFState.TYPE_TASK) {
						SC.say("The element at first position must be a Task!");
						event.cancel();
					}
					if (getDropPosition() == 0 && row.getState().getWfState().getType() == GUIWFState.TYPE_TASK) {
						// The task must be the workflow start state
						if (workflowDesigner.getWorkflow() != null) {
							workflowDesigner.getWorkflow().setStartState(row.getState().getWfState().getName());
							workflowDesigner.reloadDrawingPanel();
							// AdminPanel.get().setContent(new
							// WorkflowDesigner(workflowDesigner.getWorkflow()));
						}
					}
				}
			}
		});
	}
}
