package com.logicaldoc.gui.frontend.client.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
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

		final SelectItem workflow = new SelectItem("workflow", " ");
		workflow.setShowTitle(false);
		workflow.setWidth(200);
		ListGridField name = new ListGridField("name");
		workflow.setValueField("name");
		workflow.setDisplayField("name");
		workflow.setPickListWidth(300);
		workflow.setPickListFields(name);
		workflow.setOptionDataSource(new WorkflowsDS(null, false, false));
		if (currentWorkflow != null && !currentWorkflow.getName().trim().isEmpty())
			workflow.setValue(currentWorkflow.getName());
		else
			workflow.setValue(I18N.message("workflowselect") + "...");
		workflow.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (event.getValue() != null && !"".equals((String) event.getValue())) {
					workflowService.get(Session.get().getSid(), (String) event.getValue(),
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

			}
		});
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

		final ToolStripButton save = new ToolStripButton(I18N.message("save"));
		save.setDisabled(currentWorkflow == null);
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		addButton(save);
		addSeparator();

		ToolStripButton clone = new ToolStripButton(I18N.message("clone"));
		clone.setDisabled(currentWorkflow == null);
		clone.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Ask for new name
				LD.askforValue(I18N.message("clone"), I18N.message("newname"), "", "200", new ValueCallback() {
					@Override
					public void execute(String value) {
						if (value == null || "".equals(value.trim()))
							return;
						// Set the new name in the designer, then
						// request a save
						currentWorkflow.setId(0);
						WorkflowToolstrip.this.designer.getAccordion().setWorkflowName(value);
						onSave();
					}
				});
			}
		});
		addButton(clone);
		addSeparator();

		ToolStripButton deploy = new ToolStripButton(I18N.message("deploy"));
		deploy.setDisabled(currentWorkflow == null);
		deploy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = WorkflowToolstrip.this.designer.getAccordion().getValues();
				if (values == null || ((String) values.get("workflowName")).trim().isEmpty())
					return;

				boolean taskFound = false;
				if (currentWorkflow.getStates() != null && currentWorkflow.getStates().length > 0)
					for (GUIWFState state : currentWorkflow.getStates()) {
						if (state.getType() == GUIWFState.TYPE_TASK) {
							taskFound = true;
							break;
						}
					}

				boolean transitionErrorFound = false;
				boolean stateWithoutAssigneeFound = false;
				if (currentWorkflow.getStates() != null && currentWorkflow.getStates().length > 0) {
					for (GUIWFState state : currentWorkflow.getStates()) {
						if (state.getType() == GUIWFState.TYPE_TASK
								&& (state.getParticipants() == null || state.getParticipants().length == 0)) {
							stateWithoutAssigneeFound = true;
							break;
						}
						if (transitionErrorFound) {
							break;
						}
						if (state.getType() != GUIWFState.TYPE_END) {
							if (state.getTransitions() == null) {
								transitionErrorFound = true;
								break;
							}
							for (GUITransition transition : state.getTransitions()) {
								if (transition.getTargetState() == null
										|| (transition.getTargetState() != null && transition.getTargetState()
												.getType() == GUIWFState.TYPE_UNDEFINED)) {
									transitionErrorFound = true;
									break;
								}
							}
						}
					}
				}

				if (!taskFound)
					SC.warn(I18N.message("workflowtaskatleast"));
				else if (stateWithoutAssigneeFound)
					SC.warn(I18N.message("workflowtaskparticipantatleast"));
				else if (transitionErrorFound)
					SC.warn(I18N.message("workflowtransitiontarget"));
				else {
					currentWorkflow.setName((String) values.get("workflowName"));
					if (values.get("workflowDescr") != null)
						currentWorkflow.setDescription((String) values.get("workflowDescr"));
					if (values.get("assignmentSubject") != null)
						currentWorkflow.setTaskAssignmentSubject((String) values.get("assignmentSubject"));
					if (values.get("assignmentBody") != null)
						currentWorkflow.setTaskAssignmentBody((String) values.get("assignmentBody"));
					if (values.get("reminderSubject") != null)
						currentWorkflow.setReminderSubject((String) values.get("reminderSubject"));
					if (values.get("reminderBody") != null)
						currentWorkflow.setReminderBody((String) values.get("reminderBody"));
					if (values.get("supervisor") != null)
						currentWorkflow.setSupervisor((String) values.get("supervisor"));

					// Order the rows as displayed to the user
					int i = 0;
					List<GUIWFState> states = new ArrayList<GUIWFState>();
					for (i = 0; i < WorkflowToolstrip.this.designer.getDrawingPanel().getMembers().length; i++) {
						WorkflowRow r = (WorkflowRow) WorkflowToolstrip.this.designer.getDrawingPanel().getMembers()[i];
						states.add(r.getState().getWfState());
					}
					currentWorkflow.setStates(states.toArray(new GUIWFState[0]));

					workflowService.deploy(Session.get().getSid(), currentWorkflow, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							SC.say(I18N.message("workflowdeployed", currentWorkflow.getName()));
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
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
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

	private void onSave() {
		final Map<String, Object> values = WorkflowToolstrip.this.designer.getAccordion().getValues();
		if (values == null || ((String) values.get("workflowName")).trim().isEmpty())
			return;
		currentWorkflow.setName((String) values.get("workflowName"));
		if (values.get("workflowDescr") != null)
			currentWorkflow.setDescription((String) values.get("workflowDescr"));
		if (values.get("assignmentSubject") != null)
			currentWorkflow.setTaskAssignmentSubject((String) values.get("assignmentSubject"));
		if (values.get("assignmentBody") != null)
			currentWorkflow.setTaskAssignmentBody((String) values.get("assignmentBody"));
		if (values.get("reminderSubject") != null)
			currentWorkflow.setReminderSubject((String) values.get("reminderSubject"));
		if (values.get("reminderBody") != null)
			currentWorkflow.setReminderBody((String) values.get("reminderBody"));
		if (values.get("supervisor") != null)
			currentWorkflow.setSupervisor((String) values.get("supervisor"));

		// Order the rows as displayed to the user
		int i = 0;
		List<GUIWFState> states = new ArrayList<GUIWFState>();
		for (i = 0; i < designer.getDrawingPanel().getMembers().length; i++) {
			WorkflowRow r = (WorkflowRow) designer.getDrawingPanel().getMembers()[i];
			states.add(r.getState().getWfState());
		}
		currentWorkflow.setStates(states.toArray(new GUIWFState[0]));

		workflowService.save(Session.get().getSid(), currentWorkflow, new AsyncCallback<GUIWorkflow>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIWorkflow result) {
				if (result == null) {
					SC.warn(I18N.message("workflowalreadyexist"));
				} else {
					currentWorkflow = result;
					// Necessary reload to visualize a new saved
					// workflow of the workflows drop down menu.
					AdminPanel.get().setContent(new WorkflowDesigner(currentWorkflow, false));
				}
			}
		});
	}
}
