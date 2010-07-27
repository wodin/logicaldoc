package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.smartgwt.client.types.Alignment;
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
		commands = new HLayout();
		commands.setHeight(12);
		commands.setWidth(1);
		commands.setAlign(Alignment.RIGHT);
		addMember(commands);

		HTML delete = new HTML("<a href='#'>" + I18N.message("ddelete").toLowerCase() + "</a>");
		delete.setWidth("1px");
		delete.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				if (fromState.getType() == GUIWFState.TYPE_TASK)
					getDesigner().onDraggedStateDelete(fromState, targetState);
				else
					getDesigner().onTransitionDelete(fromState, targetState);
			}
		});
		commands.addMember(delete);

		if (fromState.getType() == GUIWFState.TYPE_TASK) {
			HTML deleteTransition = new HTML("&nbsp;&nbsp;<a href='#'>"
					+ I18N.message("deletetransition").toLowerCase() + "</a>");
			deleteTransition.setWidth("1px");
			deleteTransition.setWordWrap(false);
			deleteTransition.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
				@Override
				public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
					getDesigner().onTransitionDelete(fromState, targetState);
				}
			});
			commands.addMember(deleteTransition);
		}
	}

}
