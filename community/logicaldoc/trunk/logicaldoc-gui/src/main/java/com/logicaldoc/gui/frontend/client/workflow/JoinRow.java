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

/**
 * A row containing a join primitive
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class JoinRow extends WorkflowRow {

	private Label dropArea;

	private GUIWFState fromState = null;

	private WorkflowDesigner workflowDesigner = null;

	public JoinRow(WorkflowDesigner designer, GUIWFState wfState) {
		super();

		state = new WorkflowState(designer, wfState);
		addMember(state);

		this.fromState = wfState;
		this.workflowDesigner = designer;

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
						if (trans.getTargetState().getId().equals(target.getWfState().getId())) {
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
					SC.warn(I18N.message("workflow.error.sametarget"));
					event.cancel();
				}
				if (sameObjectFound) {
					SC.warn(I18N.message("workflow.error.sameobject"));
					event.cancel();
				}

				if (!sameElementFound && !sameObjectFound) {
					addMember(new WorkflowDraggedState(workflowDesigner, fromState, target.getWfState(), null), 1);

					// Associate the target wfState to the fromState
					// transition
					workflowDesigner.onAddTransition(fromState, target.getWfState(), "join");
				}
			}
		});

		if (wfState.getTransitions() != null && wfState.getTransitions().length > 0) {
			for (GUITransition transition : wfState.getTransitions()) {
				addMember(new Transition(designer, transition, wfState));
			}
		} else
			addMember(dropArea);
	}
}