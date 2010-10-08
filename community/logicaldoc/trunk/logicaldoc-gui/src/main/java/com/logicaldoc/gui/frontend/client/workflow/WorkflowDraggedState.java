package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUITransition;
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

	private GUITransition transition = null;

	public WorkflowDraggedState(WorkflowDesigner designer, GUIWFState from, GUIWFState target, GUITransition trans) {
		super(designer, target);

		this.fromState = from;
		this.targetState = target;
		if (trans != null)
			this.transition = trans;
		else
			this.transition = new GUITransition("", null);

		setCanDrag(false);
		setCanDrop(false);

		removeMember(commands);
		commands = new HLayout(5);
		commands.setAlign(Alignment.LEFT);
		addMember(commands);

		if (fromState.getType() == GUIWFState.TYPE_TASK) {
			Label unlink = ItemFactory.newLinkLabel("unlink");
			unlink.setWidth(38);
			unlink.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

				@Override
				public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
					getDesigner().onDraggedStateDelete(fromState, targetState, transition.getText());
				}
			});
			if (!designer.isOnlyVisualization())
				commands.addMember(unlink);
		}

		Label delete = ItemFactory.newLinkLabel("ddelete");
		delete.setWidth(38);
		delete.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				getDesigner().onTransitionDelete(fromState, targetState, transition.getText());
			}
		});
		if (!designer.isOnlyVisualization())
			commands.addMember(delete);
	}

}
