package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.widgets.events.DropOutEvent;
import com.smartgwt.client.widgets.events.DropOutHandler;
import com.smartgwt.client.widgets.events.DropOverEvent;
import com.smartgwt.client.widgets.events.DropOverHandler;
import com.smartgwt.client.widgets.layout.HStack;

/**
 * A generic workflow row.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowRow extends HStack {

	protected WorkflowState state;

	public WorkflowRow() {
		super();
		setLayoutMargin(10);
		setMembersMargin(5);
		setHeight(65);

		setCanDrag(true);
		setCanDrop(true);
		setDragType("row");

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
	}

	public WorkflowState getState() {
		return state;
	}

	public void setState(WorkflowState state) {
		this.state = state;
	}
}