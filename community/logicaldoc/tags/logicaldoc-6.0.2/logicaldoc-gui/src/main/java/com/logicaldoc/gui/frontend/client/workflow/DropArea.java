package com.logicaldoc.gui.frontend.client.workflow;

import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.DropEvent;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.events.DropOutEvent;
import com.smartgwt.client.widgets.events.DropOutHandler;
import com.smartgwt.client.widgets.events.DropOverEvent;
import com.smartgwt.client.widgets.events.DropOverHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This represent an area where states can be dropped.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DropArea extends VLayout {
	public DropArea(final Transition transition) {
		if(transition.getWorkflowDesigner().isReadOnly())
			return;
		
		final GUIWFState fromState = transition.getFromState();

		setHeight(40);
		setWidth(100);
		setBackgroundColor("#cccccc");
		setAlign(Alignment.CENTER);
		setDropTypes("state");
		setCanAcceptDrop(true);

		Label title = new Label(I18N.message("dropastate"));
		title.setHeight(15);
		title.setWrap(false);
		title.setAlign(Alignment.CENTER);

		HLayout commands = new HLayout(5);
		commands.setAlign(Alignment.LEFT);
		commands.setMembersMargin(7);

		addDropOverHandler(new DropOverHandler() {
			public void onDropOver(DropOverEvent event) {
				setBackgroundColor("#FFFF88");
			}
		});

		addDropOutHandler(new DropOutHandler() {
			public void onDropOut(DropOutEvent event) {
				setBackgroundColor("#cccccc");
			}
		});

		addDropHandler(new DropHandler() {
			public void onDrop(DropEvent event) {
				WorkflowState target = (WorkflowState) EventHandler.getDragTarget();
				boolean sameElementFound = false;
				boolean sameObjectFound = false;
				if (fromState.getTransitions() != null) {
					for (GUITransition trans : fromState.getTransitions()) {
						if (trans.getTargetState().getId().equals(target.getWfState().getId())
								&& fromState.getType() != GUIWFState.TYPE_TASK) {
							// The fork element cannot include two equal target
							// state
							sameElementFound = true;
							break;
						}
					}
				}
				if (fromState.getId().equals(target.getWfState().getId())) {
					sameObjectFound = true;
				}
				if (sameElementFound) {
					SC.warn(I18N.message("workflowsametarget", target.getWfState().getName()));
					event.cancel();
				}
				if (sameObjectFound) {
					SC.warn(I18N.message("workflowsameobject"));
					event.cancel();
				}

				if (fromState.getType() == GUIWFState.TYPE_FORK
						&& target.getWfState().getType() != GUIWFState.TYPE_TASK) {
					SC.warn(I18N.message("workflowonlytasksallowed"));
					event.cancel();
				} else if (!sameElementFound && !sameObjectFound) {
					transition.removeMember(DropArea.this);
					addMember(new WorkflowDraggedState(target.getDesigner(), fromState, target.getWfState(),
							transition.getTransition()));

					// Associate the target wfState to the fromState transition
					transition.getWorkflowDesigner().onAddTransition(fromState, target.getWfState(),
							transition.getTransition().getText());
				}
			}
		});

		Label delete = ItemFactory.newLinkLabel("ddelete");
		delete.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				transition.getWorkflowDesigner().onTransitionDelete(transition.getFromState(),
						transition.getTransition().getText());
			}
		});

		commands.setMembers(delete);
		addMember(title);
		if (transition.getFromState().getType() == GUIWFState.TYPE_TASK)
			addMember(commands);
	}
}