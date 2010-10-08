package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.events.DropOutEvent;
import com.smartgwt.client.widgets.events.DropOutHandler;
import com.smartgwt.client.widgets.events.DropOverEvent;
import com.smartgwt.client.widgets.events.DropOverHandler;
import com.smartgwt.client.widgets.layout.HStack;

public class Transition extends HStack {
	private Label dropArea;

	private GUIWFState fromState = null;

	private WorkflowDesigner workflowDesigner = null;

	private GUITransition transition = null;

	public Transition(WorkflowDesigner designer, GUITransition trans, GUIWFState from) {
		super();

		this.fromState = from;
		this.workflowDesigner = designer;
		this.transition = trans;

		setMembersMargin(3);
		setHeight(50);
		setAnimateMembers(true);

		if (from.getType() == GUIWFState.TYPE_TASK) {
			// transition line
			Label line = new Label(transition.getText());
			line.setHeight(12);
			line.setStyleName("s");
			line.setAlign(Alignment.RIGHT);
			line.setWidth(100);

			addMember(line);
		}

		if (transition.getTargetState() != null && transition.getTargetState().getType() == GUIWFState.TYPE_UNDEFINED)
			initDropArea();
		else
			addMember(new WorkflowDraggedState(designer, fromState, transition.getTargetState(), transition));
	}

	private void initDropArea() {
		dropArea = new Label(I18N.message("dropastate"));
		dropArea.setHeight(40);
		dropArea.setWidth(100);
		dropArea.setBackgroundColor("#cccccc");
		dropArea.setAlign(Alignment.CENTER);
		dropArea.setDropTypes("state");
		dropArea.setCanAcceptDrop(true);

		dropArea.addDropOverHandler(new DropOverHandler() {
			public void onDropOver(DropOverEvent event) {
				dropArea.setBackgroundColor("#FFFF88");
			}
		});

		dropArea.addDropOutHandler(new DropOutHandler() {
			public void onDropOut(DropOutEvent event) {
				dropArea.setBackgroundColor("#cccccc");
			}
		});

		dropArea.addDropHandler(new DropHandler() {
			public void onDrop(DropEvent event) {
				WorkflowState target = (WorkflowState) EventHandler.getDragTarget();
				boolean sameElementFound = false;
				boolean sameObjectFound = false;
				if (fromState.getTransitions() != null) {
					for (GUITransition trans : fromState.getTransitions()) {
						if (trans.getTargetState().getId().equals(target.getWfState().getId())
								&& fromState.getType() != GUIWFState.TYPE_TASK) {
							// The fork element cannot include two equal target
							// state
							sameElementFound = true;
							break;
						}
					}
				}
				if (fromState.getId().equals(target.getWfState().getId())) {
					sameObjectFound = true;
				}
				if (sameElementFound) {
					SC.warn(I18N.message("workflow.error.sametarget", target.getWfState().getName()));
					event.cancel();
				}
				if (sameObjectFound) {
					SC.warn(I18N.message("workflow.error.sameobject"));
					event.cancel();
				}

				if (fromState.getType() == GUIWFState.TYPE_FORK
						&& target.getWfState().getType() != GUIWFState.TYPE_TASK) {
					SC.warn(I18N.message("workflow.error.onlytasksallowed"));
					event.cancel();
				} else if (!sameElementFound && !sameObjectFound) {
					removeMember(dropArea);
					addMember(new WorkflowDraggedState(target.getDesigner(), fromState, target.getWfState(), transition));

					// Associate the target wfState to the fromState transition
					workflowDesigner.onAddTransition(fromState, target.getWfState(), transition.getText());
				}
			}
		});
		addMember(dropArea);
	}
}
