package com.logicaldoc.gui.frontend.client.workflow;

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
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.dashboard.WorkflowDashboard;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
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
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

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

	private SelectItem user = null;

	private ValuesManager vm = new ValuesManager();

	private HLayout form = null;

	private VLayout sxLayout = null;

	private VLayout dxLayout = null;

	private DynamicForm workflowForm = null;

	private DynamicForm taskForm = null;

	private WorkflowDashboard workflowDashboard;

	private TabSet tabs = new TabSet();

	private Tab docsTab = null;

	private Tab workflowTab = null;

	private HLayout appendedDocsLayout = null;

	private StaticTextItem taskStartDate = null;

	private StaticTextItem taskEndDate = null;

	private StaticTextItem endDate = null;

	private DateTimeFormat formatter = DateTimeFormat.getFormat(I18N.message("format_date"));

	public WorkflowDetailsDialog(WorkflowDashboard dashboard, GUIWorkflow wfl) {
		this.workflow = wfl;
		this.workflowDashboard = dashboard;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("workflow"));
		setWidth(580);
		setHeight(460);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(CloseClickEvent event) {
				workflowDashboard.refresh();
				destroy();
			}
		});

		tabs = new TabSet();
		tabs.setTop(30);
		tabs.setLeft(5);
		tabs.setWidth(540);
		tabs.setHeight(400);

		workflowTab = new Tab(I18N.message("workflow"));
		tabs.addTab(workflowTab, 0);
		docsTab = new Tab(I18N.message("appendeddocuments"));
		tabs.addTab(docsTab, 1);
		tabs.setSelectedTab(0);
		addChild(tabs);

		form = new HLayout(35);
		form.setMargin(20);
		form.setWidth(500);
		form.setHeight(360);

		sxLayout = new VLayout(10);
		dxLayout = new VLayout(10);
		appendedDocsLayout = new HLayout(15);
		appendedDocsLayout.setMargin(20);

		reload(wfl);
	}

	private void reload(GUIWorkflow wfl) {
		this.workflow = wfl;

		Canvas[] members = sxLayout.getMembers();
		for (Canvas canvas : members) {
			sxLayout.removeMember(canvas);
		}

		members = dxLayout.getMembers();
		for (Canvas canvas : members) {
			dxLayout.removeMember(canvas);
		}

		members = form.getMembers();
		for (Canvas canvas : members) {
			form.removeMember(canvas);
		}

		members = appendedDocsLayout.getMembers();
		for (Canvas canvas : members) {
			appendedDocsLayout.removeMember(canvas);
		}

		// Workflow section
		workflowForm = new DynamicForm();
		workflowForm.setColWidths(60, "*");

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

		StaticTextItem startDate = ItemFactory.newStaticTextItem("startdate", "startdate", null);
		if (workflow.getStartDate() != null)
			startDate.setValue(formatter.format((Date) workflow.getStartDate()));

		endDate = ItemFactory.newStaticTextItem("enddate", "enddate", null);
		if (workflow.getEndDate() != null)
			endDate.setValue(formatter.format((Date) workflow.getEndDate()));

		workflowForm.setItems(workflowTitle, workflowName, workflowDescription, startDate, endDate);
		sxLayout.addMember(workflowForm);

		// Task section
		taskForm = new DynamicForm();
		taskForm.setColWidths(60, "*");
		taskForm.setValuesManager(vm);

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

		StaticTextItem taskAssignee = ItemFactory.newStaticTextItem("taskAssignee", I18N.message("assignee"), "");
		if (workflow.getSelectedTask().getOwner() != null && !workflow.getSelectedTask().getOwner().trim().isEmpty())
			taskAssignee.setValue(workflow.getSelectedTask().getOwner());
		else if (workflow.getSelectedTask().getPooledActors() != null
				&& !workflow.getSelectedTask().getPooledActors().trim().isEmpty())
			taskAssignee.setValue(workflow.getSelectedTask().getPooledActors());
		taskAssignee.setShouldSaveValue(false);

		taskStartDate = ItemFactory.newStaticTextItem("taskStartDate", "startdate", null);
		if (workflow.getSelectedTask().getStartDate() != null)
			taskStartDate.setValue(formatter.format((Date) workflow.getSelectedTask().getStartDate()));

		StaticTextItem taskDueDate = ItemFactory.newStaticTextItem("taskDueDate", "duedate", null);
		if (workflow.getSelectedTask().getDueDate() != null
				&& !workflow.getSelectedTask().getDueDate().trim().isEmpty()) {
			String dueDate = workflow.getSelectedTask().getDueDate();
			String[] elements = dueDate.split(" ");
			// This code is mandatory to use the correct key label
			elements[1] = elements[1] + "s";
			if (elements[1].startsWith("d"))
				elements[1] = "d" + elements[1];
			taskDueDate.setValue(elements[0] + " " + I18N.message(elements[1]));
		}

		taskEndDate = ItemFactory.newStaticTextItem("taskEndDate", "enddate", null);
		if (workflow.getSelectedTask().getEndDate() != null)
			taskEndDate.setValue(formatter.format((Date) workflow.getSelectedTask().getEndDate()));

		TextAreaItem taskComment = ItemFactory.newTextAreaItem("taskComment", I18N.message("comment"), workflow
				.getSelectedTask().getComment());

		taskForm.setItems(taskTitle, taskId, taskName, taskDescription, taskAssignee, taskStartDate, taskDueDate,
				taskEndDate, taskComment);

		sxLayout.addMember(taskForm);

		// Appended documents section
		ListGridField docTitle = new ListGridField("title", I18N.message("name"), 150);
		ListGridField docLastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 150);
		docLastModified.setAlign(Alignment.CENTER);
		docLastModified.setType(ListGridFieldType.DATE);
		docLastModified.setCellFormatter(new DateCellFormatter(false));
		docLastModified.setCanFilter(false);
		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		docsAppendedList = new ListGrid();
		docsAppendedList.setEmptyMessage(I18N.message("notitemstoshow"));
		docsAppendedList.setWidth(350);
		docsAppendedList.setHeight(200);
		docsAppendedList.setCanFreezeFields(true);
		docsAppendedList.setAutoFetchData(true);
		docsAppendedList.setShowHeader(true);
		docsAppendedList.setCanSelectAll(false);
		docsAppendedList.setSelectionType(SelectionStyle.NONE);
		docsAppendedList.setBorder("1px solid #E1E1E1");
		docsAppendedList.setDataSource(new DocumentsDS(workflow.getAppendedDocIds()));
		docsAppendedList.setFields(icon, docTitle, docLastModified);

		docsAppendedList.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				destroy();
				Record record = event.getRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
						Long.parseLong(record.getAttributeAsString("id")));
			}
		});

		Button appendDocsButton = new Button(I18N.message("appenddocuments"));
		appendDocsButton.setAutoFit(true);
		appendDocsButton.setVisible(workflow.getSelectedTask().getTaskState().equals("started"));
		appendDocsButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				destroy();
				SC.confirm(I18N.message("confirmation"), I18N.message("addtoworkflowinfo"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							Session.get().setCurrentWorkflow(workflow);
							MainPanel.get().selectDocumentsTab();
							DocumentsPanel.get().refresh();
							Log.info(I18N.message("addtoworkflowinfo"), null);
						}
					}
				});
			}
		});

		appendedDocsLayout.addMember(docsAppendedList);
		if (workflow.getSelectedTask().getEndDate() == null) {
			appendedDocsLayout.addMember(appendDocsButton);
		}

		Button reassignButton = new Button(I18N.message("workflowtaskreassign"));
		reassignButton.setAutoFit(true);
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
					@SuppressWarnings("unchecked")
					public void onClick(ClickEvent event) {
						Map<String, Object> values = (Map<String, Object>) vm.getValues();

						if ((values.get("user") == null) || values.get("user").toString().trim().isEmpty()) {
							return;
						}

						if (vm.validate()) {
							service.saveTaskAssignment(Session.get().getSid(), workflow.getSelectedTask().getId(),
									values.get("user").toString(), new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void ret) {
											service.getWorkflowDetailsByTask(Session.get().getSid(), workflow
													.getSelectedTask().getId(), new AsyncCallback<GUIWorkflow>() {

												@Override
												public void onFailure(Throwable caught) {
													Log.serverError(caught);
												}

												@Override
												public void onSuccess(GUIWorkflow result) {
													if (result != null) {
														window.destroy();
														workflow = result;
														reload(workflow);

													}
												}
											});
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
		startButton.setAutoFit(true);
		startButton.setMargin(2);
		startButton.setVisible((workflow.getSelectedTask().getStartDate() == null));
		startButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				final Map<String, Object> values = (Map<String, Object>) vm.getValues();

				String comment = "";
				if (values.get("taskComment") != null) {
					comment = (String) values.get("taskComment");
				}

				service.startTask(Session.get().getSid(), workflow.getSelectedTask().getId(), comment,
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								service.getWorkflowDetailsByTask(Session.get().getSid(), workflow.getSelectedTask()
										.getId(), new AsyncCallback<GUIWorkflow>() {

									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUIWorkflow result) {
										if (result != null) {
											workflow = result;
											reload(workflow);
											taskStartDate.setValue(formatter.format((Date) workflow.getSelectedTask()
													.getStartDate()));
										}
									}
								});
							}
						});
			}
		});

		Button suspendButton = new Button(I18N.message("workflowtasksuspend"));
		suspendButton.setAutoFit(true);
		suspendButton.setMargin(2);
		suspendButton.setVisible(workflow.getSelectedTask().getTaskState().equals("started"));
		suspendButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				final Map<String, Object> values = (Map<String, Object>) vm.getValues();

				String comment = "";
				if (values.get("taskComment") != null) {
					comment = (String) values.get("taskComment");
				}

				service.suspendTask(Session.get().getSid(), workflow.getSelectedTask().getId(), comment,
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								service.getWorkflowDetailsByTask(Session.get().getSid(), workflow.getSelectedTask()
										.getId(), new AsyncCallback<GUIWorkflow>() {

									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUIWorkflow result) {
										if (result != null) {
											workflow = result;
											reload(workflow);

										}
									}
								});
							}
						});
			}
		});

		Button resumeButton = new Button(I18N.message("workflowtaskresume"));
		resumeButton.setAutoFit(true);
		resumeButton.setMargin(2);
		resumeButton.setVisible(workflow.getSelectedTask().getTaskState().equals("suspended"));
		resumeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				final Map<String, Object> values = (Map<String, Object>) vm.getValues();

				String comment = "";
				if (values.get("taskComment") != null) {
					comment = (String) values.get("taskComment");
				}

				service.resumeTask(Session.get().getSid(), workflow.getSelectedTask().getId(), comment,
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								service.getWorkflowDetailsByTask(Session.get().getSid(), workflow.getSelectedTask()
										.getId(), new AsyncCallback<GUIWorkflow>() {

									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUIWorkflow result) {
										if (result != null) {
											workflow = result;
											reload(workflow);

										}
									}
								});
							}
						});
			}
		});

		Button saveTaskStateButton = new Button(I18N.message("save"));
		saveTaskStateButton.setAutoFit(true);
		saveTaskStateButton.setMargin(2);
		saveTaskStateButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				final Map<String, Object> values = (Map<String, Object>) vm.getValues();

				String comment = "";
				if (values.get("taskComment") != null) {
					comment = (String) values.get("taskComment");
				}

				service.saveTaskState(Session.get().getSid(), workflow.getSelectedTask().getId(), comment,
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								service.getWorkflowDetailsByTask(Session.get().getSid(), workflow.getSelectedTask()
										.getId(), new AsyncCallback<GUIWorkflow>() {

									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUIWorkflow result) {
										if (result != null) {
											workflow = result;
											reload(workflow);

										}
									}
								});
							}
						});
			}
		});

		Button takeButton = new Button(I18N.message("workflowtasktake"));
		takeButton.setAutoFit(true);
		takeButton.setMargin(2);
		takeButton.setVisible(!workflow.getSelectedTask().getPooledActors().isEmpty()
				&& workflow.getSelectedTask().getOwner().trim().isEmpty());
		takeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				final Map<String, Object> values = (Map<String, Object>) vm.getValues();

				String comment = "";
				if (values.get("taskComment") != null) {
					comment = (String) values.get("taskComment");
				}

				service.takeTaskOwnerShip(Session.get().getSid(), workflow.getSelectedTask().getId(),
						Long.toString(Session.get().getUser().getId()), comment, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								service.getWorkflowDetailsByTask(Session.get().getSid(), workflow.getSelectedTask()
										.getId(), new AsyncCallback<GUIWorkflow>() {

									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUIWorkflow result) {
										if (result != null) {
											workflow = result;
											reload(workflow);

										}
									}
								});
							}
						});
			}
		});

		Button turnBackButton = new Button(I18N.message("workflowtaskturnback"));
		turnBackButton.setAutoFit(true);
		turnBackButton.setMargin(2);
		turnBackButton.setVisible(!workflow.getSelectedTask().getPooledActors().isEmpty()
				&& !workflow.getSelectedTask().getOwner().trim().isEmpty());
		turnBackButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				final Map<String, Object> values = (Map<String, Object>) vm.getValues();

				String comment = "";
				if (values.get("taskComment") != null) {
					comment = (String) values.get("taskComment");
				}

				service.turnBackTaskToPool(Session.get().getSid(), workflow.getSelectedTask().getId(), comment,
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								service.getWorkflowDetailsByTask(Session.get().getSid(), workflow.getSelectedTask()
										.getId(), new AsyncCallback<GUIWorkflow>() {

									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUIWorkflow result) {
										if (result != null) {
											workflow = result;
											reload(workflow);

										}
									}
								});
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

			if (workflow.getSelectedTask().getTaskState().equals("started")) {
				// Add Transitions buttons
				Button transitionButton = null;
				for (GUITransition transition : workflow.getSelectedTask().getTransitions()) {
					final String transitionName = transition.getText();
					transitionButton = new Button(transition.getText());
					transitionButton.setAutoFit(true);
					transitionButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
						@Override
						@SuppressWarnings("unchecked")
						public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
							final Map<String, Object> values = (Map<String, Object>) vm.getValues();

							String comment = "";
							if (values.get("taskComment") != null) {
								comment = (String) values.get("taskComment");
							}

							service.endTask(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
									transitionName, comment, new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void result) {
											service.getWorkflowDetailsByTask(Session.get().getSid(), workflow
													.getSelectedTask().getId(), new AsyncCallback<GUIWorkflow>() {

												@Override
												public void onFailure(Throwable caught) {
													Log.serverError(caught);
												}

												@Override
												public void onSuccess(GUIWorkflow result) {
													if (result != null) {
														workflow = result;
														reload(workflow);
														taskEndDate.setValue(formatter.format((Date) workflow
																.getSelectedTask().getEndDate()));
														if (workflow.getEndDate() != null)
															endDate.setValue(formatter.format((Date) workflow
																	.getEndDate()));
													}
												}
											});
										}
									});
						}
					});
					dxLayout.addMember(transitionButton);
				}
			}
		} else {
			DynamicForm taskEndedForm = new DynamicForm();
			taskEndedForm.setWidth(180);
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

		workflowTab.setPane(form);
		docsTab.setPane(appendedDocsLayout);
	}

	public GUIWorkflow getWorkflow() {
		return workflow;
	}

	public void setUser(String id) {
		user.setValue(id);
	}

	public TabSet getTabs() {
		return tabs;
	}
}
