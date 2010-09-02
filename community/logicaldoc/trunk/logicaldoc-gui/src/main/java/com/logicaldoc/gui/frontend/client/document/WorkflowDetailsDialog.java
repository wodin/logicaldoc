package com.logicaldoc.gui.frontend.client.document;

import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.DocumentsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This popup window is used to visualize the details of a selected workflow.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowDetailsDialog extends Window {

	private WorkflowServiceAsync service = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private GUIWorkflow workflow = null;

	private ListGrid docsAppendedList;

	private ComboBoxItem user = null;

	private ValuesManager vm = new ValuesManager();

	// private ListGrid historyTasksList;
	//
	// private WorkflowHistoriesDS dataSource;

	private HLayout form = null;

	private VLayout sxLayout = null;

	private VLayout dxLayout = null;

	private VLayout infoLayout = null;

	private DynamicForm workflowForm = null;

	private DynamicForm taskForm = null;

	public WorkflowDetailsDialog(GUIWorkflow wfl) {
		this.workflow = wfl;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("workflow"));
		setWidth(800);
		setHeight(700);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		form = new HLayout(25);
		form.setMargin(40);
		form.setWidth(790);
		form.setHeight100();

		addMember(form);

		reload();
	}

	private void reload() {
//		if (infoLayout != null) {
//			infoLayout.removeMember(workflowForm);
//			infoLayout.removeMember(taskForm);
//			sxLayout.removeMember(infoLayout);
//		}
//
//		if (sxLayout != null) {
//			sxLayout.destroy();
//			form.removeMember(sxLayout);
//		}
//
//		if (dxLayout != null) {
//			dxLayout.destroy();
//			form.removeMember(dxLayout);
//		}
		
		Canvas[] members = form.getMembers();
		for (Canvas canvas : members) {
			removeMember(canvas);
		}

		// if (form != null) {
		// form.destroy();
		// removeMember(form);
		// }
		//
		// form = new HLayout(25);
		// form.setMargin(40);
		// form.setWidth(780);
		// form.setHeight(680);

		sxLayout = new VLayout(25);
		// sxLayout.setMargin(25);

		infoLayout = new VLayout(10);
		// infoLayout.setMargin(25);

		// Workflow section
		workflowForm = new DynamicForm();
		// workflowForm.setColWidths(1, "*");

		StaticTextItem workflowTitle = ItemFactory.newStaticTextItem("workflowTitle", "",
				"<b>" + I18N.message("workflow") + "</b>");
		workflowTitle.setShouldSaveValue(false);
		workflowTitle.setWrapTitle(false);

		StaticTextItem workflowName = ItemFactory.newStaticTextItem("workflowName", I18N.message("name"),
				workflow.getName());
		workflowName.setShouldSaveValue(false);

		StaticTextItem workflowDescription = ItemFactory.newStaticTextItem("workflowDescription",
				I18N.message("description"), workflow.getDescription());
		workflowDescription.setShouldSaveValue(false);

		DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.message("format_date"));

		StaticTextItem startDate = ItemFactory.newStaticTextItem("startdate", "startdate", null);
		if (workflow.getStartDate() != null)
			startDate.setValue(formatter.format((Date) workflow.getStartDate()));

		StaticTextItem endDate = ItemFactory.newStaticTextItem("enddate", "enddate", null);
		if (workflow.getEndDate() != null)
			endDate.setValue(formatter.format((Date) workflow.getEndDate()));

		workflowForm.setItems(workflowTitle, workflowName, workflowDescription, startDate, endDate);
		infoLayout.addMember(workflowForm);

		// Task section
		taskForm = new DynamicForm();
		taskForm.setWidth(300);
		// taskForm.setColWidths(1, "*");

		StaticTextItem taskTitle = ItemFactory
				.newStaticTextItem("taskTitle", "", "<b>" + I18N.message("task") + "</b>");
		taskTitle.setShouldSaveValue(false);
		taskTitle.setWrapTitle(false);

		StaticTextItem taskId = ItemFactory.newStaticTextItem("taskId", I18N.message("id"), workflow.getSelectedTask()
				.getId());
		taskId.setShouldSaveValue(false);

		StaticTextItem taskName = ItemFactory.newStaticTextItem("taskName", I18N.message("name"), workflow
				.getSelectedTask().getName());
		taskName.setShouldSaveValue(false);

		StaticTextItem taskDescription = ItemFactory.newStaticTextItem("taskDescription", I18N.message("description"),
				workflow.getSelectedTask().getDescription());
		taskDescription.setShouldSaveValue(false);

		StaticTextItem taskAssignee = ItemFactory.newStaticTextItem("taskAssignee", I18N.message("assignee"), workflow
				.getSelectedTask().getOwner());
		taskAssignee.setShouldSaveValue(false);

		StaticTextItem taskStartDate = ItemFactory.newStaticTextItem("taskStartDate", "startdate", null);
		if (workflow.getSelectedTask().getStartDate() != null)
			taskStartDate.setValue(formatter.format((Date) workflow.getSelectedTask().getStartDate()));

		StaticTextItem taskEndDate = ItemFactory.newStaticTextItem("taskEndDate", "enddate", null);
		if (workflow.getSelectedTask().getEndDate() != null)
			endDate.setValue(formatter.format((Date) workflow.getSelectedTask().getEndDate()));

		TextAreaItem taskComment = ItemFactory.newTextAreaItem("taskComment", I18N.message("comment"), workflow
				.getSelectedTask().getComment());

		taskForm.setItems(taskTitle, taskId, taskName, taskDescription, taskAssignee, taskStartDate, taskEndDate,
				taskComment);

		infoLayout.addMember(taskForm);

		// DynamicForm historyTasksForm = new DynamicForm();
		// historyTasksForm.setWidth(300);
		// // historyTasksForm.setHeight(50);
		// // historyTasksForm.setColWidths(1, "*");
		//
		// StaticTextItem historyTasksTitle =
		// ItemFactory.newStaticTextItem("historyTasks", "",
		// "<b>" + I18N.message("history") + "</b>");
		// historyTasksTitle.setShouldSaveValue(false);
		// historyTasksTitle.setWrapTitle(false);
		// historyTasksForm.setItems(historyTasksTitle);
		// infoLayout.addMember(historyTasksForm);
		//
		// // sxLayout.addMember(infoLayout);
		//
		// ListGridField id = new ListGridField("id", I18N.message("id"), 70);
		// ListGridField name = new ListGridField("name", I18N.message("name"),
		// 100);
		// ListGridField pooledAssignees = new ListGridField("pooledassignees",
		// I18N.message("pooledassignees"), 150);
		//
		// historyTasksList = new ListGrid();
		// historyTasksList.setWidth(400);
		// // historyTasksList.setHeight(200);
		// historyTasksList.setCanFreezeFields(true);
		// historyTasksList.setAutoFetchData(true);
		// historyTasksList.setShowHeader(true);
		// historyTasksList.setCanSelectAll(false);
		// historyTasksList.setSelectionType(SelectionStyle.NONE);
		// historyTasksList.setBorder("0px");
		// dataSource = new WorkflowHistoriesDS(workflow.getId(), null);
		// historyTasksList.setDataSource(dataSource);
		// historyTasksList.setFields(id, name, pooledAssignees);
		//
		// infoLayout.addMember(historyTasksList);
		// // sxLayout.addMember(historyTasksList);
		// // sxLayout.addMember(infoLayout);

		// VLayout appendedDocsLayout = new VLayout();

		DynamicForm appendedDocsForm = new DynamicForm();
		appendedDocsForm.setWidth(250);
		// appendedDocsForm.setHeight(50);
		// appendedDocsForm.setColWidths(1, "*");

		StaticTextItem appendedDocsTitle = ItemFactory.newStaticTextItem("appendedDocs", "",
				"<b>" + I18N.message("appendeddocuments") + "</b>");
		appendedDocsTitle.setShouldSaveValue(false);
		appendedDocsTitle.setWrapTitle(false);

		appendedDocsForm.setItems(appendedDocsTitle);
		// appendedDocsLayout.addMember(appendedDocsForm);
		infoLayout.addMember(appendedDocsForm);
		// sxLayout.addMember(appendedDocsLayout);

		// Appended documents section
		ListGridField docTitle = new ListGridField("title", I18N.message("name"), 100);
		ListGridField docLastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 150);
		docLastModified.setAlign(Alignment.CENTER);
		docLastModified.setType(ListGridFieldType.DATE);
		docLastModified.setCellFormatter(new DateCellFormatter());

		docsAppendedList = new ListGrid();
		docsAppendedList.setWidth(300);
		docsAppendedList.setHeight(200);
		docsAppendedList.setCanFreezeFields(true);
		docsAppendedList.setAutoFetchData(true);
		docsAppendedList.setShowHeader(true);
		docsAppendedList.setCanSelectAll(false);
		docsAppendedList.setSelectionType(SelectionStyle.NONE);
		docsAppendedList.setBorder("0px");
		docsAppendedList.setDataSource(new DocumentsDS(workflow.getAppendedDocIds()));
		docsAppendedList.setFields(docTitle, docLastModified);

		infoLayout.addMember(docsAppendedList);
		sxLayout.addMember(infoLayout);

		// sxLayout.addMember(docsAppendedList);

		dxLayout = new VLayout(10);
		dxLayout.setMargin(40);

		Button reassignButton = new Button(I18N.message("workflowtaskreassign"));
		reassignButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				final Window window = new Window();
				window.setTitle(I18N.message("workflowtaskreassign"));
				window.setWidth(350);
				window.setHeight(200);
				window.setCanDragResize(true);
				window.setIsModal(true);
				window.setShowModalMask(true);
				window.centerInPage();

				DynamicForm reassignUserForm = new DynamicForm();
				reassignUserForm.setTitleOrientation(TitleOrientation.TOP);
				reassignUserForm.setNumCols(1);
				reassignUserForm.setValuesManager(vm);
//				StaticTextItem userItem = ItemFactory.newStaticTextItem("userItem", "", "<b>" + I18N.message("user")
//						+ "</b>");
//				userItem.setShouldSaveValue(false);
//				userItem.setWrapTitle(false);
				user = ItemFactory.newUserSelector("user", I18N.message("user"));
				user.setShowTitle(true);
				user.setDisplayField("username");
				user.addChangedHandler(new ChangedHandler() {
					@Override
					public void onChanged(ChangedEvent event) {
						try {
							setUser(user.getSelectedRecord().getAttribute("id"));
						} catch (Throwable t) {
						}
					}
				});

				FormItemIcon icon = new FormItemIcon();
				icon.setSrc("[SKIN]/actions/remove.png");
				user.addIconClickHandler(new IconClickHandler() {
					public void onIconClick(IconClickEvent event) {
						user.setValue("");
					}
				});
				user.setIcons(icon);

				SubmitItem saveButton = new SubmitItem("save", I18N.message("save"));
				saveButton.setAlign(Alignment.LEFT);
				saveButton.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						final Map<String, Object> values = vm.getValues();

						if ((values.get("user") == null) || values.get("user").toString().trim().isEmpty()) {
							return;
						}

						if (vm.validate()) {
							service.saveTaskAssignment(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
									values.get("user").toString(), new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void ret) {
											reload();
											window.destroy();
										}
									});
						}
					}
				});

				reassignUserForm.setItems(user, saveButton);

				window.addItem(reassignUserForm);
				window.show();
			}
		});

		Button startButton = new Button(I18N.message("workflowtaskstart"));
		startButton.setMargin(2);
		startButton.setVisible((workflow.getSelectedTask().getStartDate() == null));
		startButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				service.startTask(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								reload();
								destroy();
								WorkflowDetailsDialog workflowDetailsDialog = new WorkflowDetailsDialog(workflow);
								workflowDetailsDialog.show();
							}
						});
			}
		});

		// SC.warn("startButton added!!!");

		Button suspendButton = new Button(I18N.message("workflowtasksuspend"));
		suspendButton.setMargin(2);
		suspendButton.setVisible(workflow.getSelectedTask().getTaskState().equals("started"));
		suspendButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				service.suspendTask(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								reload();
								destroy();
								WorkflowDetailsDialog workflowDetailsDialog = new WorkflowDetailsDialog(workflow);
								workflowDetailsDialog.show();
							}
						});
			}
		});

		Button resumeButton = new Button(I18N.message("workflowtaskresume"));
		resumeButton.setMargin(2);
		resumeButton.setVisible(workflow.getSelectedTask().getTaskState().equals("suspended"));
		resumeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				service.resumeTask(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								reload();
								destroy();
								WorkflowDetailsDialog workflowDetailsDialog = new WorkflowDetailsDialog(workflow);
								workflowDetailsDialog.show();
								// destroy();
							}
						});
			}
		});

		Button saveTaskStateButton = new Button(I18N.message("save"));
		saveTaskStateButton.setMargin(2);
		saveTaskStateButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				service.saveTaskState(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								reload();
								// destroy();
								// WorkflowDetailsDialog workflowDetailsDialog =
								// new WorkflowDetailsDialog(workflow);
								// workflowDetailsDialog.show();
								// destroy();
							}
						});
			}
		});

		Button takeButton = new Button(I18N.message("workflowtasktake"));
		takeButton.setMargin(2);
		takeButton.setVisible(!workflow.getSelectedTask().getPooledActors().isEmpty()
				&& workflow.getSelectedTask().getOwner().trim().isEmpty());
		takeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				service.takeTaskOwnerShip(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
						Long.toString(Session.get().getUser().getId()), new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								reload();
								// destroy();
								// WorkflowDetailsDialog workflowDetailsDialog =
								// new WorkflowDetailsDialog(workflow);
								// workflowDetailsDialog.show();
								// destroy();
							}
						});
			}
		});

		Button turnBackButton = new Button(I18N.message("workflowtaskturnback"));
		turnBackButton.setMargin(2);
		turnBackButton.setVisible(!workflow.getSelectedTask().getPooledActors().isEmpty()
				&& !workflow.getSelectedTask().getOwner().trim().isEmpty());
		turnBackButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				service.turnBackTaskToPool(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								reload();
								// destroy();
								// WorkflowDetailsDialog workflowDetailsDialog =
								// new WorkflowDetailsDialog(workflow);
								// workflowDetailsDialog.show();
								// destroy();
							}
						});
			}
		});

		if (workflow.getSelectedTask().getEndDate() == null) {
			dxLayout.addMember(reassignButton);
			dxLayout.addMember(startButton);
			dxLayout.addMember(suspendButton);
			dxLayout.addMember(resumeButton);
			dxLayout.addMember(saveTaskStateButton);
			dxLayout.addMember(takeButton);
			dxLayout.addMember(turnBackButton);

			// SC.warn("before transitions buttons!!!");

			if (workflow.getSelectedTask().getTaskState().equals("started")) {
				// Add Transitions buttons
				Button transitionButton = null;
				for (GUITransition transition : workflow.getSelectedTask().getTransitions()) {
					final String transitionName = transition.getText();
					transitionButton = new Button(transition.getText());
					transitionButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
						@Override
						public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
							service.endTask(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
									transitionName, new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void result) {
											reload();
											// destroy();
											// WorkflowDetailsDialog
											// workflowDetailsDialog = new
											// WorkflowDetailsDialog(
											// workflow);
											// workflowDetailsDialog.show();
											// destroy();
										}
									});
						}
					});
					dxLayout.addMember(transitionButton);
				}
			}
		} else {
			DynamicForm taskEndedForm = new DynamicForm();
			taskEndedForm.setWidth(300);
			taskEndedForm.setColWidths(1, "*");

			StaticTextItem taskEndedTitle = ItemFactory.newStaticTextItem("taskEndedTitle", "",
					"<b>" + I18N.message("workflowtaskended") + "</b>");
			taskEndedTitle.setShouldSaveValue(false);
			taskEndedTitle.setWrapTitle(false);

			taskEndedForm.setItems(taskEndedTitle);
			dxLayout.addMember(taskEndedForm);
		}

		form.addMember(sxLayout);
		form.addMember(dxLayout);

		// addMember(form);
	}

	public GUIWorkflow getWorkflow() {
		return workflow;
	}

	public void setUser(String id) {
		user.setValue(id);
	}
}
