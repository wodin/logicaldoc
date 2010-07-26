package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.events.DropOutEvent;
import com.smartgwt.client.widgets.events.DropOutHandler;
import com.smartgwt.client.widgets.events.DropOverEvent;
import com.smartgwt.client.widgets.events.DropOverHandler;
import com.smartgwt.client.widgets.layout.VStack;

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
				// Remove the drop area, because the 'Join' state can have only
				// one transition.
				removeMember(dropArea);
				addMember(new WorkflowDraggedState(target.getDesigner(), fromState, target.getWfState()), 1);

				// Add a new transition on the parent state
				workflowDesigner.onAddTransition(fromState, target.getWfState());
			}
		});

		if (wfState.getTransitions() != null && wfState.getTransitions().size() > 0) {
			VStack transitionsPanel = new VStack();
			for (String transitionLabel : wfState.getTransitions().keySet()) {
				transitionsPanel.addMember(new Transition(designer, transitionLabel, wfState));
			}
			addMember(transitionsPanel);
		} else
			addMember(dropArea);
	}
}