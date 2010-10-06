package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Represents a dragged workflow state.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowDraggedState extends WorkflowState {

	private GUIWFState fromState = null;

	private GUIWFState targetState = null;

	public WorkflowDraggedState(WorkflowDesigner designer, GUIWFState from, GUIWFState target) {
		super(designer, target);

		this.fromState = from;
		this.targetState = target;

		setCanDrag(false);
		setCanDrop(false);

		removeMember(commands);
		commands = new HLayout(5);
		commands.setAlign(Alignment.LEFT);
		addMember(commands);

		Label unlink = ItemFactory.newLinkLabel("unlink");
		unlink.setWidth(38);
		unlink.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				if (fromState.getType() == GUIWFState.TYPE_TASK)
					getDesigner().onDraggedStateDelete(fromState, targetState);
				else
					getDesigner().onTransitionDelete(fromState, targetState);
			}
		});
		if (!designer.isOnlyVisualization())
			commands.addMember(unlink);

		if (fromState.getType() == GUIWFState.TYPE_TASK) {
			Label delete = ItemFactory.newLinkLabel("ddelete");
			delete.setWidth(38);
			delete.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

				@Override
				public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
					getDesigner().onTransitionDelete(fromState, targetState);
				}
			});
			if (!designer.isOnlyVisualization())
				commands.addMember(delete);
		}
	}

}
