package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.events.DropOutEvent;
import com.smartgwt.client.widgets.events.DropOutHandler;
import com.smartgwt.client.widgets.events.DropOverEvent;
import com.smartgwt.client.widgets.events.DropOverHandler;
import com.smartgwt.client.widgets.layout.HStack;

/**
 * A single workflow element.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowComponent extends HStack {

	private TaskBox taskBox;

	private Label dropArea;

	public WorkflowComponent() {
		super();
		setLayoutMargin(10);
		setMembersMargin(5);
		setHeight(65);
		
		setCanDrag(true);
		setCanDrop(true);
		setDragType("component");

		setAnimateMembers(true);

		setBorder("1px dotted #4040ff");

		addDropOverHandler(new DropOverHandler() {
			public void onDropOver(DropOverEvent event) {
				setBackgroundColor("FFFF88");
			}
		});

		addDropOutHandler(new DropOutHandler() {
			public void onDropOut(DropOutEvent event) {
				setBackgroundColor("");
			}
		});

		taskBox = new TaskBox();
		addMember(taskBox);

		dropArea = new Label("Drop a Task");
		dropArea.setHeight(40);
		dropArea.setWidth(100);
		dropArea.setBackgroundColor("#cccccc");
		dropArea.setAlign(Alignment.CENTER);
		dropArea.setDropTypes("task");
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
				Canvas target = EventHandler.getDragTarget();
				SC.say("You dropped the " + target.getID());
				addMember(new TransitionBox(), 1);
			}
		});

		addMember(dropArea);
	}
}