package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.util.BooleanCallback;
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
 * Workflow Tools
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WorkflowToolstrip extends ToolStrip {
	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private GUIWorkflow currentWorkflow = null;

	public WorkflowToolstrip() {
		super();
		setWidth100();

		ToolStripButton newTemplate = new ToolStripButton(I18N.message("newwftemplate"));
		newTemplate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("New workflow template");
				workflowService.save(Session.get().getSid(), new GUIWorkflow(), new AsyncCallback<GUIWorkflow>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIWorkflow result) {
						currentWorkflow = result;
					}
				});
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
		workflow.setOptionDataSource(WorkflowsDS.get());
		workflow.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (workflow.getSelectedRecord() == null)
					return;
				workflowService.get(Session.get().getSid(),
						Long.parseLong(workflow.getSelectedRecord().getAttribute("id")),
						new AsyncCallback<GUIWorkflow>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIWorkflow result) {
								currentWorkflow = result;
								// TODO Update the workflow setting page
							}
						});
			}
		});
		addFormItem(workflow);

		ToolStripButton load = new ToolStripButton(I18N.message("load"));
		load.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("load the selected template");
				workflowService.get(Session.get().getSid(), currentWorkflow.getId(), new AsyncCallback<GUIWorkflow>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIWorkflow result) {
						currentWorkflow = result;
						// TODO Update the workflow setting page
					}
				});
			}
		});
		addButton(load);
		addSeparator();

		ToolStripButton save = new ToolStripButton(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("save the selected template");
				workflowService.save(Session.get().getSid(), currentWorkflow, new AsyncCallback<GUIWorkflow>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIWorkflow result) {
						currentWorkflow = result;
						// TODO Update the workflow setting page
					}
				});
			}
		});
		addButton(save);
		addSeparator();

		ToolStripButton deploy = new ToolStripButton(I18N.message("deploy"));
		deploy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("deploy the loaded template");
				workflowService.deploy(Session.get().getSid(), currentWorkflow.getId(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						// TODO Update the workflow setting page
					}
				});
			}
		});
		addButton(deploy);
		addSeparator();

		ToolStripButton delete = new ToolStripButton(I18N.message("ddelete"));
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("delete the loaded template");
				SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							workflowService.delete(Session.get().getSid(), currentWorkflow.getId(),
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void result) {
											// TODO Update the workflow setting
											// page
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
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SC.say("Close the current workflow");
			}
		});
		addButton(close);

		addFill();
	}
}
