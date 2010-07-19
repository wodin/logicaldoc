package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.widgets.layout.VStack;

/**
 * Where the workflow diagram is drawn
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowDesigner extends VStack {
	public WorkflowDesigner() {
		setHeight(50);
		setMembersMargin(5);

		addMember(new WorkflowToolstrip());
		addMember(new StateToolstrip());
		addMember(new DrawingPanel());
	}
}