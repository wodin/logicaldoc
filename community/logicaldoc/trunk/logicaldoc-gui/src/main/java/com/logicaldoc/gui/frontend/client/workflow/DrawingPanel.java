package com.logicaldoc.gui.frontend.client.workflow;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * In this panel the grapical design of the workflow takes place.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DrawingPanel extends VStack {

	public DrawingPanel(WorkflowDesigner designer) {
		super();
		setHeight(557);
		setMembersMargin(5);
		setCanAcceptDrop(true);
		setAnimateMembers(true);
		setShowDragPlaceHolder(true);
		setDropTypes("row");
		setShowCustomScrollbars(true);
		setOverflow(Overflow.SCROLL);

		addMember(new TaskRow(designer));
		addMember(new ForkRow(designer));
		addMember(new JoinRow(designer));
		addMember(new EndRow(designer));
	}
}
