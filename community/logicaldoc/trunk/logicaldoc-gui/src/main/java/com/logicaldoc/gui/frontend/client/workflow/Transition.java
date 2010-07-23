package com.logicaldoc.gui.frontend.client.workflow;

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

	public Transition() {
		super();

		setMembersMargin(3);
		setHeight(50);
		setAnimateMembers(true);

		// transition line
		Label line = new Label("transition");
		line.setHeight(12);
		line.setStyleName("s");
		line.setAlign(Alignment.RIGHT);
		line.setWidth(100);

		addMember(line);

		initDropArea();
	}

	private void initDropArea() {
		dropArea = new Label("Drop an Element");
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
				removeMember(dropArea);
				addMember(new WorkflowDraggedState(target.getDesigner(), target.getType()));
			}
		});
		addMember(dropArea);
	}
}
