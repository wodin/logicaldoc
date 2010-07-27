package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
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

		if (designer.getWorkflow() != null) {
			for (GUIWFState state : designer.getWorkflow().getStates()) {
				if (state == null)
					continue;
				if (state.getType() == GUIWFState.TYPE_TASK)
					addMember(new TaskRow(designer, state));
				else if (state.getType() == GUIWFState.TYPE_FORK)
					addMember(new ForkRow(designer, state));
				else if (state.getType() == GUIWFState.TYPE_JOIN)
					addMember(new JoinRow(designer, state));
				else if (state.getType() == GUIWFState.TYPE_END)
					addMember(new EndRow(designer, state));
			}
		}

		addDropHandler(new DropHandler() {
			public void onDrop(DropEvent event) {
				WorkflowRow row = (WorkflowRow) EventHandler.getDragTarget();
				// TODO Alert avvisando l'utente dell'errore se viene messo al primo posto non un TaskRow
				event.cancel();
			}
		});
	}
}
