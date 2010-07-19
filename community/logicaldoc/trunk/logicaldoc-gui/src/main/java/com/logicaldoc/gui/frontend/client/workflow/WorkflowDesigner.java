package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * Where the workflow diagram is drawn
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowDesigner extends VStack {
	public WorkflowDesigner() {
		setMembersMargin(5);

		addMember(new WorkflowToolstrip());
		addMember(new StateToolstrip());

		// HStack or HLayout with Accordion e Drawing Panel
		HLayout layout = new HLayout();
		layout.addMember(new Accordion());
		layout.addMember(new DrawingPanel());
		addMember(layout);
	}
}