package com.logicaldoc.gui.frontend.client.workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.DocumentsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.clipboard.Clipboard;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This popup window is used to visualize the details of a selected workflow
 * task.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowDetailsDialog extends Window {

	private WorkflowServiceAsync service = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private GUIWorkflow workflow = null;

	private ListGrid appendedDocs;

	private SelectItem user = null;

	private ValuesManager vm = new ValuesManager();

	private HLayout mainPanel = null;

	private HLayout form = null;

	private VLayout sxLayout = null;

	private DynamicForm workflowForm = null;

	private DynamicForm taskForm = null;

	private WorkflowDashboard workflowDashboard;

	private TabSet tabs = new TabSet();

	private VLayout buttonsPanel = null;

	private Tab docsTab = null;

	private Tab workflowTab = null;

	private VLayout appendedDocsPanel = null;

	private StaticTextItem taskStartDate = null;

	private StaticTextItem taskDueDate = null;

	private StaticTextItem taskEndDate = null;

	public WorkflowDetailsDialog(final WorkflowDashboard dashboard, GUIWorkflow wfl) {
		this.workflow = wfl;
		this.workflowDashboard = dashboard;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("workflow"));
		setWidth(580);
		setHeight(440);
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
		tabs.setWidth100();
		tabs.setHeight100();

		workflowTab = new Tab(I18N.message("workflow"));
		tabs.addTab(workflowTab, 0);

		buttonsPanel = new VLayout();

		mainPanel = new HLayout();
		mainPanel.setMembersMargin(5);
		mainPanel.setMembers(tabs, buttonsPanel);

		addItem(mainPanel);

		form = new HLayout(25);
		form.setMargin(20);
		form.setWidth100();
		form.setHeight100();

		sxLayout = new VLayout(10);
		appendedDocsPanel = new VLayout(15);
		appendedDocsPanel.setMargin(20);

		reload(wfl);
	}

	private void reload(final GUIWorkflow wfl) {
		this.workflow = wfl;

		Canvas[] members = sxLayout.getMembers();
		for (Canvas canvas : members) {
			sxLayout.removeMember(canvas);
		}

		members = buttonsPanel.getMembers();
		for (Canvas canvas : members) {
			buttonsPanel.removeMember(canvas);
		}

		members = form.getMembers();
		for (Canvas canvas : members) {
			form.removeMember(canvas);
		}

		members = appendedDocsPanel.getMembers();
		for (Canvas canvas : members) {
			appendedDocsPanel.removeMember(canvas);
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

		StaticTextItem startDate = ItemFactory.newStaticTextItem("startdate", "startdate", null);
		if (workflow.getStartDate() != null)
			startDate.setValue(I18N.formatDate((Date) workflow.getStartDate()));

		StaticTextItem endDate = ItemFactory.newStaticTextItem("enddate", "enddate", null);
		if (workflow.getEndDate() != null)
			endDate.setValue(I18N.formatDate((Date) workflow.getEndDate()));

		workflowForm.setItems(workflowTitle, workflowName, workflowDescription, startDate, endDate);
		sxLayout.addMember(workflowForm);

		// Task section
		taskForm = new DynamicForm();
		taskForm.setColWidths(60, "*");
		taskForm.setValuesManager(vm);

		StaticTextItem taskTitle = ItemFactory
				.newStaticTextItem("taskTitle", "", "<b>" + I18N.message("task") + "</b>");
		taskTitle.setWrapTitle(false);

		StaticTextItem taskId = ItemFactory.newStaticTextItem("taskId", I18N.message("id"), workflow.getSelectedTask()
				.getId());

		StaticTextItem taskName = ItemFactory.newStaticTextItem("taskName", I18N.message("name"), workflow
				.getSelectedTask().getName());

		StaticTextItem taskDescription = ItemFactory.newStaticTextItem("taskDescription", I18N.message("description"),
				workflow.getSelectedTask().getDescription());
		taskDescription.setShouldSaveValue(false);

		StaticTextItem taskAssignee = ItemFactory.newStaticTextItem("taskAssignee", I18N.message("assignee"), "");
		if (workflow.getSelectedTask().getOwner() != null && !workflow.getSelectedTask().getOwner().trim().isEmpty())
			taskAssignee.setValue(workflow.getSelectedTask().getOwner());
		else if (workflow.getSelectedTask().getPooledActors() != null
				&& !workflow.getSelectedTask().getPooledActors().trim().isEmpty())
			taskAssignee.setValue(workflow.getSelectedTask().getPooledActors());

		taskStartDate = ItemFactory.newStaticTextItem("taskStartDate", "startdate", null);
		if (workflow.getSelectedTask().getStartDate() != null)
			taskStartDate.setValue(I18N.formatDate((Date) workflow.getSelectedTask().getStartDate()));

		taskDueDate = ItemFactory.newStaticTextItem("taskDueDate", "duedate", null);
		if (workflow.getSelectedTask().getDueDate() != null)
			taskDueDate.setValue(I18N.formatDate((Date) workflow.getSelectedTask().getDueDate()));

		taskEndDate = ItemFactory.newStaticTextItem("taskEndDate", "enddate", null);
		if (workflow.getSelectedTask().getEndDate() != null)
			taskEndDate.setValue(I18N.formatDate((Date) workflow.getSelectedTask().getEndDate()));

		taskForm.setItems(taskTitle, taskId, taskName, taskDescription, taskAssignee, taskStartDate, taskDueDate,
				taskEndDate);

		sxLayout.addMember(taskForm);

		HLayout spacer=new HLayout();
		spacer.setHeight(5);
		
		Button reassignButton = new Button(I18N.message("workflowtaskreassign"));
		reassignButton.setAutoFit(true);
		reassignButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				final Window window = new Window();
				window.setTitle(I18N.message("workflowtaskreassign"));
				window.setWidth(250);
				window.setHeight(120);
				window.setCanDragResize(true);
				window.setIsModal(true);
				window.setShowModalMask(true);
				window.centerInPage();

				DynamicForm reassignUserForm = new DynamicForm();
				reassignUserForm.setTitleOrientation(TitleOrientation.TOP);
				reassignUserForm.setNumCols(1);
				reassignUserForm.setValuesManager(vm);
				user = ItemFactory.newUserSelector("user", I18N.message("user"), null, true);
				user.setShowTitle(true);
				user.setDisplayField("username");

				SubmitItem saveButton = new SubmitItem("save", I18N.message("save"));
				saveButton.setAlign(Alignment.LEFT);
				saveButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (user.getSelectedRecord() == null)
							return;
						setUser(user.getSelectedRecord().getAttribute("id"));

						service.reassignTask(Session.get().getSid(), workflow.getSelectedTask().getId(), user
								.getSelectedRecord().getAttribute("id"), new AsyncCallback<GUIWorkflow>() {

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

				reassignUserForm.setItems(user, saveButton);

				window.addItem(reassignUserForm);
				window.show();
			}
		});

		Button takeButton = new Button(I18N.message("workflowtasktake"));
		takeButton.setAutoFit(true);
		takeButton.setMargin(2);
		takeButton.setVisible(!(workflow.getSelectedTask().getPooledActors() == null || workflow.getSelectedTask()
				.getPooledActors().isEmpty())
				&& (workflow.getSelectedTask().getOwner() == null || workflow.getSelectedTask().getOwner().trim()
						.isEmpty()));
		takeButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				service.claimTask(Session.get().getSid(), workflow.getSelectedTask().getId(),
						Long.toString(Session.get().getUser().getId()), new AsyncCallback<GUIWorkflow>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIWorkflow result) {
								workflow = result;
								result.getSelectedTask().setOwner(Session.get().getUser().getUserName());
								reload(workflow);
								WorkflowDetailsDialog.this.workflowDashboard.refresh();
							}
						});
			}
		});

		Button turnBackButton = new Button(I18N.message("workflowtaskturnback"));
		turnBackButton.setAutoFit(true);
		turnBackButton.setMargin(2);
		turnBackButton.setVisible(!(workflow.getSelectedTask().getPooledActors() == null || workflow.getSelectedTask()
				.getPooledActors().isEmpty())
				&& !(workflow.getSelectedTask().getOwner() == null || workflow.getSelectedTask().getOwner().trim()
						.isEmpty()));
		turnBackButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {

				service.turnBackTaskToPool(Session.get().getSid(), workflow.getSelectedTask().getId(),
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
										destroy();
										WorkflowDetailsDialog.this.workflowDashboard.refresh();
									}
								});
							}
						});
			}
		});

		if (workflow.getSelectedTask().getEndDate() == null) {
			buttonsPanel.addMember(spacer);
			buttonsPanel.addMember(reassignButton);
			buttonsPanel.addMember(takeButton);
			buttonsPanel.addMember(turnBackButton);

			if (workflow.getSelectedTask().getTaskState().equals("started")
					&& workflow.getSelectedTask().getOwner() != null) {
				DynamicForm transitionsForm = new DynamicForm();
				transitionsForm.setWidth(150);
				transitionsForm.setIsGroup(true);
				transitionsForm.setGroupTitle(I18N.message("actions"));

				List<FormItem> items = new ArrayList<FormItem>();
				// Add Transitions buttons
				if (workflow.getSelectedTask().getTransitions() != null)
					for (GUITransition transition : workflow.getSelectedTask().getTransitions()) {
						final String transitionName = transition.getText();
						if (transitionName == null || transitionName.trim().isEmpty())
							continue;
						ButtonItem transitionButton = new ButtonItem(transition.getText());
						transitionButton.setAutoFit(true);
						transitionButton.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								service.endTask(Session.get().getSid(), getWorkflow().getSelectedTask().getId(),
										transitionName, new AsyncCallback<Void>() {
											@Override
											public void onFailure(Throwable caught) {
												Log.serverError(caught);
											}

											@Override
											public void onSuccess(Void result) {
												WorkflowDetailsDialog.this.workflowDashboard.refresh();
												destroy();
											}
										});
							}
						});
						items.add(transitionButton);
					}
				transitionsForm.setItems(items.toArray(new FormItem[0]));
				buttonsPanel.addMember(transitionsForm);
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
			
			buttonsPanel.addMember(spacer);
			buttonsPanel.addMember(taskEndedForm);
		}

		form.addMember(sxLayout);

		workflowTab.setPane(form);

		refreshAppendedDocsTab();
	}

	private void refreshAppendedDocsTab() {
		if (docsTab != null)
			tabs.removeTab(docsTab);
		docsTab = new Tab(I18N.message("appendeddocuments"));
		tabs.addTab(docsTab, 1);
		prepareAppendedDocsPanel();
		docsTab.setPane(appendedDocsPanel);
	}

	private void prepareAppendedDocsPanel() {
		ListGridField docTitle = new ListGridField("title", I18N.message("name"));
		docTitle.setWidth("*");
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

		appendedDocs = new ListGrid();
		appendedDocs.setEmptyMessage(I18N.message("notitemstoshow"));
		appendedDocs.setWidth100();
		appendedDocs.setHeight100();
		appendedDocs.setCanFreezeFields(true);
		appendedDocs.setAutoFetchData(true);
		appendedDocs.setShowHeader(true);
		appendedDocs.setCanSelectAll(false);
		appendedDocs.setSelectionType(SelectionStyle.SINGLE);
		appendedDocs.setBorder("1px solid #E1E1E1");
		appendedDocs.setDataSource(new DocumentsDS(workflow.getAppendedDocIds()));
		appendedDocs.setFields(icon, docTitle, docLastModified);

		appendedDocs.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				destroy();
				Record record = event.getRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
						Long.parseLong(record.getAttributeAsString("id")));
			}
		});

		appendedDocs.setContextMenu(setupContextMenu());
		appendedDocsPanel.addMember(appendedDocs);

		Button addDocuments = new Button(I18N.message("adddocuments"));
		addDocuments.setAutoFit(true);
		addDocuments.setVisible(workflow.getSelectedTask().getTaskState().equals("started"));
		addDocuments.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				Clipboard clipboard = Clipboard.getInstance();
				if (clipboard.isEmpty()) {
					SC.warn(I18N.message("nodocsinclipboard"));
					return;
				}

				Long[] ids = new Long[clipboard.size()];
				int i = 0;
				for (GUIDocument doc : clipboard)
					ids[i++] = doc.getId();

				service.appendDocuments(Session.get().getSid(), workflow.getSelectedTask().getId(), ids,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void ret) {
								service.getWorkflowDetailsByTask(Session.get().getSid(), workflow.getSelectedTask()
										.getId(), new AsyncCallback<GUIWorkflow>() {

									@Override
									public void onFailure(Throwable caught) {
										Log.serverError(caught);
									}

									@Override
									public void onSuccess(GUIWorkflow result) {
										WorkflowDetailsDialog.this.workflow.setAppendedDocIds(result
												.getAppendedDocIds());
										refreshAppendedDocsTab();
										tabs.selectTab(1);
										Clipboard.getInstance().clear();
									}
								});
							}
						});
			}
		});

		if (workflow.getSelectedTask().getEndDate() == null) {
			appendedDocsPanel.addMember(addDocuments);
		}
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

	/**
	 * Prepares the context menu for the documents grid.
	 */
	private Menu setupContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem preview = new MenuItem();
		preview.setTitle(I18N.message("preview"));
		preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				// Detect the two selected records
				ListGridRecord selection = appendedDocs.getSelectedRecord();

				long id = Long.parseLong(selection.getAttribute("id"));
				String filename = selection.getAttribute("filename");
				String fileVersion = selection.getAttribute("fileVersion");

				if (filename == null)
					filename = selection.getAttribute("title") + "." + selection.getAttribute("type");

				PreviewPopup iv = new PreviewPopup(id, fileVersion, filename, true);
				iv.show();
			}
		});

		contextMenu.setItems(preview);

		return contextMenu;
	}
}
