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
import com.smartgwt.client.widgets.layout.HStack;

public class Transition extends HStack {
	private Label dropArea;

	private GUIWFState fromState = null;

	private GUIWFState targetState = null;

	private WorkflowDesigner workflowDesigner = null;

	public Transition(WorkflowDesigner designer, String transitionLabel, GUIWFState state) {
		super();

		this.fromState = state;
		this.workflowDesigner = designer;

		setMembersMargin(3);
		setHeight(50);
		setAnimateMembers(true);

		// transition line
		Label line = new Label(transitionLabel);
		line.setHeight(12);
		line.setStyleName("s");
		line.setAlign(Alignment.RIGHT);
		line.setWidth(100);

		addMember(line);

		initDropArea();
	}

	public Transition(WorkflowDesigner designer, String transitionLabel, GUIWFState from, GUIWFState target) {
		super();

		this.fromState = from;
		this.targetState = target;
		this.workflowDesigner = designer;

		setMembersMargin(3);
		setHeight(50);
		setAnimateMembers(true);

		// transition line
		Label line = new Label(transitionLabel);
		line.setHeight(12);
		line.setStyleName("s");
		line.setAlign(Alignment.RIGHT);
		line.setWidth(100);

		addMember(line);

		addMember(new WorkflowDraggedState(designer, fromState, targetState));
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
				if (fromState.getType() != GUIWFState.TYPE_FORK)
					removeMember(dropArea);
				addMember(new WorkflowDraggedState(target.getDesigner(), fromState, target.getWfState()));

				// Add a new transition on the parent state
				workflowDesigner.onAddTransition(fromState, target.getWfState());
			}
		});
		addMember(dropArea);
	}
}
