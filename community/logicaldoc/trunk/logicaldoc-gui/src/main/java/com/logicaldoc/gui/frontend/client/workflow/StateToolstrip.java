package com.logicaldoc.gui.frontend.client.workflow;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridField;
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

	private WorkflowDesigner designer = null;

	private ComboBoxItem startState = new ComboBoxItem("startState", I18N.message("startstate"));

	public StateToolstrip(WorkflowDesigner designer) {
		super();

		this.designer = designer;
		setWidth100();

		ToolStripButton newTask = new ToolStripButton(I18N.message("addtask"));
		newTask.setIcon(Util.imageUrl("task.png"));
		newTask.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New Task");
			}
		});
		addButton(newTask);
		addSeparator();

		ToolStripButton newEndState = new ToolStripButton(I18N.message("addendstate"));
		newEndState.setIcon(Util.imageUrl("endState.png"));
		newEndState.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New End State");
			}
		});
		addButton(newEndState);
		addSeparator();

		ToolStripButton newFork = new ToolStripButton(I18N.message("addfork"));
		newFork.setIcon(Util.imageUrl("fork.png"));
		newFork.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New Fork");
			}
		});
		addButton(newFork);
		addSeparator();

		ToolStripButton newJoin = new ToolStripButton(I18N.message("addjoin"));
		newJoin.setIcon(Util.imageUrl("join.png"));
		newJoin.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New Join");
			}
		});
		addButton(newJoin);
		addSeparator();

		startState = new ComboBoxItem("startState", I18N.message("startstate"));
		ListGridField name = new ListGridField("name");
		startState.setPickListWidth(300);
		startState.setPickListFields(name);
		startState.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (startState.getSelectedRecord() == null)
					return;
				workflowService.get(Session.get().getSid(),
						Long.parseLong(startState.getSelectedRecord().getAttribute("id")),
						new AsyncCallback<GUIWorkflow>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIWorkflow workflow) {
								LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
								int i = 0;
								for (GUIWFState state : workflow.getStates()) {
									if (state.getType() == GUIWFState.TYPE_TASK) {
										map.put("" + i, "" + state.getName() + "");
										i++;
									}
								}
								startState.setValueMap(map);
							}
						});
			}
		});
		if (this.designer.getWorkflow() != null) {
			startState.setValue(this.designer.getWorkflow().getStartState());
		}

		addFormItem(startState);

		addFill();
	}

	public WorkflowDesigner getDesigner() {
		return designer;
	}
}
