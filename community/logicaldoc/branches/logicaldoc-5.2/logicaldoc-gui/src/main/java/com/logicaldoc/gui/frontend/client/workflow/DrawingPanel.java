package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.widgets.layout.VStack;

/**
 * Where the workflow diagram is drawn
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DrawingPanel extends VStack {
	public DrawingPanel() {
		setHeight(50);
		setMembersMargin(5);
		setCanAcceptDrop(true);
		setAnimateMembers(true);
		setShowDragPlaceHolder(true);
		setDropTypes("component");

		addMember(new WorkflowComponent());
		addMember(new TaskComponent());
	}
}