package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * A States toolbar.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class StateToolstrip extends ToolStrip {
	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private WorkflowDesigner workflowDesigner = null;

	public StateToolstrip(WorkflowDesigner designer) {
		super();

		this.workflowDesigner = designer;
		setWidth100();

		ToolStripButton newTask = new ToolStripButton(I18N.message("addtask"));
		newTask.setIcon(Util.imageUrl("task.png"));
		newTask.setDisabled(workflowDesigner.getWorkflow() == null);
		newTask.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				workflowDesigner.onAddState(GUIWFState.TYPE_TASK);
			}
		});
		addButton(newTask);
		addSeparator();

		ToolStripButton newEndState = new ToolStripButton(I18N.message("addendstate"));
		newEndState.setIcon(Util.imageUrl("endState.png"));
		newEndState.setDisabled(workflowDesigner.getWorkflow() == null);
		newEndState.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				workflowDesigner.onAddState(GUIWFState.TYPE_END);
			}
		});
		addButton(newEndState);
		addSeparator();

		ToolStripButton newFork = new ToolStripButton(I18N.message("addfork"));
		newFork.setIcon(Util.imageUrl("fork.png"));
		newFork.setDisabled(workflowDesigner.getWorkflow() == null);
		newFork.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				workflowDesigner.onAddState(GUIWFState.TYPE_FORK);
			}
		});
		addButton(newFork);
		addSeparator();

		ToolStripButton newJoin = new ToolStripButton(I18N.message("addjoin"));
		newJoin.setIcon(Util.imageUrl("join.png"));
		newJoin.setDisabled(workflowDesigner.getWorkflow() == null);
		newJoin.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				workflowDesigner.onAddState(GUIWFState.TYPE_JOIN);
			}
		});
		addButton(newJoin);

		addFill();
	}

	public WorkflowDesigner getDesigner() {
		return workflowDesigner;
	}
}
