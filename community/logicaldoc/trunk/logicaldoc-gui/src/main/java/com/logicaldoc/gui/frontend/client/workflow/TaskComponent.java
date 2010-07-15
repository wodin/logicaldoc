package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.widgets.events.DropOutEvent;
import com.smartgwt.client.widgets.events.DropOutHandler;
import com.smartgwt.client.widgets.events.DropOverEvent;
import com.smartgwt.client.widgets.events.DropOverHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * A single task element, with transitions
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TaskComponent extends HStack {

	private TaskBox taskBox;

	public TaskComponent() {
		super();
		setLayoutMargin(10);
		setMembersMargin(5);
		setHeight(60);

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

		VStack transitionsPanel = new VStack();
		for (int i = 0; i < 3; i++) {
			transitionsPanel.addMember(new Transition());
		}
		addMember(transitionsPanel);
	}
}