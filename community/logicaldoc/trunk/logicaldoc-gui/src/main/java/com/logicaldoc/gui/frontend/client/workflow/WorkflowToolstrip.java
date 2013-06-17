package com.logicaldoc.gui.frontend.client.workflow;

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
import com.logicaldoc.gui.common.client.util.WindowUtils;
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

	private ToolStripButton export = null;

	private ToolStripButton _import = null;

	private ToolStripButton save = null;

	private ToolStripButton clone = null;

	private ToolStripButton deploy = null;

	private ToolStripButton delete = null;

	private ToolStripButton close = null;

	private SelectItem workflowSelect = null;

	public WorkflowToolstrip(final WorkflowDesigner designer) {
		super();

		this.designer = designer;
		this.currentWorkflow = designer.getWorkflow();

		setWidth100();

		ToolStripButton newTemplate = new ToolStripButton(I18N.message("newwftemplate"));
		newTemplate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentWorkflow = new GUIWorkflow();
				AdminPanel.get().setContent(new WorkflowDesigner(currentWorkflow));
				update();
			}
		});
		addButton(newTemplate);
		addSeparator();

		workflowSelect = new SelectItem("workflow", " ");
		workflowSelect.setShowTitle(false);
		workflowSelect.setWidth(200);
		ListGridField name = new ListGridField("name");
		workflowSelect.setValueField("name");
		workflowSelect.setDisplayField("name");
		workflowSelect.setPickListWidth(300);
		workflowSelect.setPickListFields(name);

		workflowSelect.addChangedHandler(new ChangedHandler() {
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
									WorkflowToolstrip.this.designer.redraw(currentWorkflow);
									update();
								}
							});
				}

			}
		});
		addFormItem(workflowSelect);

		ToolStripButton load = new ToolStripButton(I18N.message("load"));
		load.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = workflowSelect.getSelectedRecord();
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
								WorkflowToolstrip.this.designer.redraw(result);
								update();
							}
						});
			}
		});
		addButton(load);

		save = new ToolStripButton(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});
		addButton(save);

		deploy = new ToolStripButton(I18N.message("deploy"));
		deploy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				WorkflowToolstrip.this.designer.saveModel();
				currentWorkflow = WorkflowToolstrip.this.designer.getWorkflow();

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
					workflowService.deploy(Session.get().getSid(), currentWorkflow, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							SC.say(I18N.message("workflowdeployed", currentWorkflow.getName()));
							update();
						}
					});
				}
			}
		});
		addButton(deploy);

		delete = new ToolStripButton(I18N.message("ddelete"));
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
											currentWorkflow = new GUIWorkflow();
											AdminPanel.get().setContent(new WorkflowDesigner(currentWorkflow));
											update();
										}
									});
						}
					}
				});
			}
		});
		addButton(delete);
		addSeparator();

		clone = new ToolStripButton(I18N.message("clone"));
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
						currentWorkflow.setId(null);
						currentWorkflow.setName(value.trim());
						WorkflowToolstrip.this.designer.getAccordion().setWorkflowName(value.trim());
						save();
					}
				});
			}
		});
		addButton(clone);

		_import = new ToolStripButton(I18N.message("iimport"));
		_import.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				WorkflowUploader uploader = new WorkflowUploader(WorkflowToolstrip.this.designer);
				uploader.show();
				update();
			}
		});
		addButton(_import);

		export = new ToolStripButton(I18N.message("eexport"));
		export.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid()
						+ "&pluginId=logicaldoc-workflow&resourcePath=templates/" + currentWorkflow.getId() + ".jbpm");
			}
		});
		addButton(export);
		addSeparator();

		close = new ToolStripButton(I18N.message("close"));
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					currentWorkflow = new GUIWorkflow();
					AdminPanel.get().setContent(new WorkflowDesigner(currentWorkflow));
					update();
				} catch (Throwable t) {

				}
			}
		});
		addButton(close);

		addFill();

		update();
	}

	public WorkflowDesigner getDesigner() {
		return designer;
	}

	public void update() {
		GUIWorkflow wf = currentWorkflow;
		export.setDisabled(wf == null || wf.getId() == null || "0".equals(wf.getId()));
		_import.setDisabled(wf == null);
		save.setDisabled(currentWorkflow == null);
		clone.setDisabled(currentWorkflow == null || wf.getId() == null || "0".equals(wf.getId()));
		deploy.setDisabled(currentWorkflow == null || wf.getId() == null || "0".equals(wf.getId()));
		delete.setDisabled(currentWorkflow == null || wf.getId() == null || "0".equals(wf.getId()));
		close.setDisabled(currentWorkflow == null);
		workflowSelect.setOptionDataSource(new WorkflowsDS(false, false));
		if (currentWorkflow != null && !currentWorkflow.getName().trim().isEmpty())
			workflowSelect.setValue(currentWorkflow.getName());
		else
			workflowSelect.setValue(I18N.message("workflowselect") + "...");
		workflowSelect.redraw();
	}

	private void save() {
		try {
			if (!designer.saveModel())
				return;
		} catch (Throwable t) {
		}

		currentWorkflow = designer.getWorkflow();

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
					if (currentWorkflow.getId() == null || "0".equals(currentWorkflow.getId())) {
						currentWorkflow = result;
						designer.redraw(currentWorkflow);
					} else
						currentWorkflow = result;
				}
				update();
			}
		});
	}
}