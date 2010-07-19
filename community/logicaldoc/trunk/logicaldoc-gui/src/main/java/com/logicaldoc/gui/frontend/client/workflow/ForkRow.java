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

/**
 * A single workflow element.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class ForkRow extends WorkflowRow {

	private Label dropArea;

	public ForkRow() {
		super();

		state = new WorkflowState(WorkflowState.TYPE_FORK);
		addMember(state);

		dropArea = new Label("Drop a Task");
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
				WorkflowState target = (WorkflowState)EventHandler.getDragTarget();
				WorkflowState drag = new WorkflowDraggedState(target.getType());
				addMember(drag, 1);
			}
		});

		addMember(dropArea);
	}
}