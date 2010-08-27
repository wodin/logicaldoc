package com.logicaldoc.gui.frontend.client.workflow;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Workflow Tools
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowToolstrip extends ToolStrip {
	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private GUIWorkflow currentWorkflow = null;

	private WorkflowDesigner designer = null;

	public WorkflowToolstrip(WorkflowDesigner designer) {
		super();

		this.designer = designer;
		if (designer.getWorkflow() != null)
			currentWorkflow = designer.getWorkflow();

		setWidth100();

		ToolStripButton newTemplate = new ToolStripButton(I18N.message("newwftemplate"));
		newTemplate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentWorkflow = new GUIWorkflow();
				AdminPanel.get().setContent(new WorkflowDesigner(currentWorkflow, false));
			}
		});
		addButton(newTemplate);
		addSeparator();

		final ComboBoxItem workflow = new ComboBoxItem("workflow", " ");
		workflow.setShowTitle(false);
		ListGridField name = new ListGridField("name");
		workflow.setValueField("id");
		workflow.setDisplayField("name");
		workflow.setPickListWidth(300);
		workflow.setPickListFields(name);
		workflow.setOptionDataSource(new WorkflowsDS(null, false));
		if (currentWorkflow != null)
			workflow.setValue(currentWorkflow.getName());
		addFormItem(workflow);

		ToolStripButton load = new ToolStripButton(I18N.message("load"));
		load.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = workflow.getSelectedRecord();
				if (selectedRecord == null)
					return;

				workflowService.get(Session.get().getSid(), selectedRecord.getAttributeAsString("name"),
						new AsyncCallback<GUIWorkflow>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIWorkflow result) {
								currentWorkflow = result;
								AdminPanel.get().setContent(new WorkflowDesigner(currentWorkflow, false));
							}
						});
			}
		});
		addButton(load);
		addSeparator();

		ToolStripButton save = new ToolStripButton(I18N.message("save"));
		save.setDisabled(currentWorkflow == null);
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = WorkflowToolstrip.this.designer.getAccordion().getValues();
				currentWorkflow.setName((String) values.get("workflowName"));
				currentWorkflow.setDescription((String) values.get("workflowDescr"));
				currentWorkflow.setTaskAssignmentSubject((String) values.get("assignmentSubject"));
				currentWorkflow.setTaskAssignmentBody((String) values.get("assignmentBody"));
				currentWorkflow.setReminderSubject((String) values.get("reminderSubject"));
				currentWorkflow.setReminderBody((String) values.get("reminderBody"));
				currentWorkflow.setSupervisor((String) values.get("supervisor"));

				workflowService.save(Session.get().getSid(), currentWorkflow, new AsyncCallback<GUIWorkflow>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIWorkflow result) {
						if (result == null) {
							SC.warn("A workflow with the same name already exists!");
						} else {
							currentWorkflow = result;
							// Necessary reload to visualize a new saved
							// workflow of
							// the workflows drop down menu.
							AdminPanel.get().setContent(new WorkflowDesigner(currentWorkflow, false));
						}
					}
				});
			}
		});
		addButton(save);
		addSeparator();

		ToolStripButton deploy = new ToolStripButton(I18N.message("deploy"));
		deploy.setDisabled(currentWorkflow == null);
		deploy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean taskFound = false;
				if (currentWorkflow.getStates() != null && currentWorkflow.getStates().length > 0)
					for (GUIWFState state : currentWorkflow.getStates()) {
						if (state.getType() == GUIWFState.TYPE_TASK) {
							taskFound = true;
							break;
						}
					}

				if (!taskFound)
					SC.warn("A workflow must have at least one task!");
				else {
					final Map<String, Object> values = WorkflowToolstrip.this.designer.getAccordion().getValues();
					currentWorkflow.setName((String) values.get("workflowName"));
					currentWorkflow.setDescription((String) values.get("workflowDescr"));
					currentWorkflow.setTaskAssignmentSubject((String) values.get("assignmentSubject"));
					currentWorkflow.setTaskAssignmentBody((String) values.get("assignmentBody"));
					currentWorkflow.setReminderSubject((String) values.get("reminderSubject"));
					currentWorkflow.setReminderBody((String) values.get("reminderBody"));
					currentWorkflow.setSupervisor((String) values.get("supervisor"));

					workflowService.deploy(Session.get().getSid(), currentWorkflow, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							SC.say("Workflow " + currentWorkflow.getName() + " correctly deployed!!!");
						}
					});
				}
			}
		});
		addButton(deploy);
		addSeparator();

		ToolStripButton delete = new ToolStripButton(I18N.message("ddelete"));
		delete.setDisabled(currentWorkflow == null);
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							workflowService.delete(Session.get().getSid(), currentWorkflow.getName(),
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void result) {
											currentWorkflow = null;
											AdminPanel.get().setContent(new WorkflowDesigner(new GUIWorkflow(), false));
										}
									});
						}
					}
				});
			}
		});
		addButton(delete);
		addSeparator();

		ToolStripButton close = new ToolStripButton(I18N.message("close"));
		close.setDisabled(currentWorkflow == null);
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new WorkflowDesigner(null, false));
			}
		});
		addButton(close);

		addFill();
	}

	public WorkflowDesigner getDesigner() {
		return designer;
	}
}
