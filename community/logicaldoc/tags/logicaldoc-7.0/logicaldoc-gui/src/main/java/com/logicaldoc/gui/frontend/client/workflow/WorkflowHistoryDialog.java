package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.WorkflowHistoriesDS;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This popup window is used to visualize the workflows histories.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowHistoryDialog extends Window {
	private WorkflowServiceAsync service = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private GUIWorkflow selectedWorkflow = null;

	private ComboBoxItem user = null;

	private ListGrid instancesList;

	private ListGrid historiesList;

	private WorkflowHistoriesDS instancesDataSource;

	private WorkflowHistoriesDS historiesDataSource;

	private VLayout form = null;

	private Long selectedWorkflowInstance = null;

	public WorkflowHistoryDialog() {

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("workflowhistory"));
		setWidth(950);
		setHeight(530);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		reload();
	}

	private void reload() {
		if (form != null) {
			removeMember(form);
		}

		form = new VLayout(20);
		form.setMargin(20);

		HLayout selectionWorkflowLayout = new HLayout(15);
		selectionWorkflowLayout.setMargin(20);
		selectionWorkflowLayout.setWidth(300);

		// Workflow section
		DynamicForm workflowForm = new DynamicForm();

		final ComboBoxItem workflow = new ComboBoxItem("workflowSelection", I18N.message("workflowselect"));
		workflow.setWrapTitle(false);
		ListGridField name = new ListGridField("name");
		workflow.setValueField("id");
		workflow.setDisplayField("name");
		workflow.setPickListFields(name);
		workflow.setOptionDataSource(new WorkflowsDS(false, true, false));
		if (selectedWorkflow != null)
			workflow.setValue(selectedWorkflow.getName());

		workflowForm.setItems(workflow);
		selectionWorkflowLayout.addMember(workflowForm);

		Button load = new Button(I18N.message("load"));
		load.setAutoFit(true);
		load.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				ListGridRecord selectedRecord = workflow.getSelectedRecord();
				if (selectedRecord == null)
					return;

				service.get(Session.get().getSid(), selectedRecord.getAttributeAsString("name"),
						new AsyncCallback<GUIWorkflow>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIWorkflow result) {
								selectedWorkflow = result;
								reload();
							}
						});
			}
		});

		selectionWorkflowLayout.addMember(load);
		form.addMember(selectionWorkflowLayout);

		VLayout workflowInstancesLayout = new VLayout(10);
		workflowInstancesLayout.setWidth(900);

		// Workflow instances section
		DynamicForm workflowInstancesForm = new DynamicForm();
		workflowInstancesForm.setColWidths(1, "*");

		StaticTextItem workflowTitle = ItemFactory.newStaticTextItem("workflowInstances", "",
				"<b>" + I18N.message("workflowinstances") + "</b>");
		workflowTitle.setShouldSaveValue(false);
		workflowTitle.setWrapTitle(false);

		workflowInstancesForm.setItems(workflowTitle);
		workflowInstancesLayout.addMember(workflowInstancesForm);

		ListGridField id = new ListGridField("id", I18N.message("id"), 60);
		ListGridField startDate = new ListGridField("startdate", I18N.message("startdate"), 150);
		startDate.setAlign(Alignment.CENTER);
		startDate.setType(ListGridFieldType.DATE);
		startDate.setCellFormatter(new DateCellFormatter(false));
		startDate.setCanFilter(false);
		ListGridField endDate = new ListGridField("enddate", I18N.message("enddate"), 150);
		endDate.setAlign(Alignment.CENTER);
		endDate.setType(ListGridFieldType.DATE);
		endDate.setCellFormatter(new DateCellFormatter(false));
		endDate.setCanFilter(false);
		ListGridField documents = new ListGridField("documents", I18N.message("documents"), 250);

		instancesList = new ListGrid();
		instancesList.setCanFreezeFields(true);
		instancesList.setAutoFetchData(true);
		instancesList.setShowHeader(true);
		instancesList.setCanSelectAll(false);
		instancesList.setSelectionType(SelectionStyle.SINGLE);
		instancesList.setHeight(140);
		instancesList.setBorder("1px solid #E1E1E1");
		instancesList.sort("startdate", SortDirection.DESCENDING);
		if (selectedWorkflow != null) {
			instancesDataSource = new WorkflowHistoriesDS(null, Long.parseLong(selectedWorkflow.getId()));
			instancesList.setDataSource(instancesDataSource);
		}

		instancesList.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				Record record = event.getRecord();
				selectedWorkflowInstance = Long.parseLong(record.getAttributeAsString("id"));
				reload();
			}
		});
		instancesList.setFields(id, startDate, endDate, documents);
		instancesList.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});
		workflowInstancesLayout.addMember(instancesList);

		form.addMember(workflowInstancesLayout);

		// Workflow histories section
		VLayout workflowHistoriesLayout = new VLayout(10);
		workflowHistoriesLayout.setWidth(900);

		ListGridField historyEvent = new ListGridField("event", I18N.message("event"), 200);
		ListGridField historyDate = new ListGridField("date", I18N.message("date"), 150);
		historyDate.setAlign(Alignment.CENTER);
		historyDate.setType(ListGridFieldType.DATE);
		historyDate.setCellFormatter(new DateCellFormatter(false));
		historyDate.setCanFilter(false);
		ListGridField historyUser = new ListGridField("user", I18N.message("user"), 120);
		ListGridField historyComment = new ListGridField("comment", I18N.message("comment"));
		historyComment.setWidth("*");
		ListGridField historyDoc = new ListGridField("document", I18N.message("document"), 180);
		ListGridField historySid = new ListGridField("sessionid", I18N.message("sid"), 240);
		historySid.setHidden(true);

		historiesList = new ListGrid();
		historiesList.setEmptyMessage(I18N.message("notitemstoshow"));
		historiesList.setCanFreezeFields(true);
		historiesList.setAutoFetchData(true);
		historiesList.setShowHeader(true);
		historiesList.setCanSelectAll(false);
		historiesList.setSelectionType(SelectionStyle.SINGLE);
		historiesList.setHeight(200);
		historiesList.sort("date", SortDirection.ASCENDING);
		historiesList.setBorder("1px solid #E1E1E1");
		if (selectedWorkflowInstance != null) {
			historiesDataSource = new WorkflowHistoriesDS(selectedWorkflowInstance, Long.parseLong(selectedWorkflow
					.getId()));
			historiesList.setDataSource(historiesDataSource);
		}

		historiesList.setFields(historyEvent, historyDate, historyUser, historyComment, historyDoc, historySid);
		workflowHistoriesLayout.addMember(historiesList);

		form.addMember(workflowHistoriesLayout);

		addChild(form);
	}

	public GUIWorkflow getSelectedWorkflow() {
		return selectedWorkflow;
	}

	public void setUser(String id) {
		user.setValue(id);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord selection = instancesList.getSelectedRecord();
		if (selection == null)
			return;

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.deleteInstance(Session.get().getSid(), selection.getAttributeAsString("id"),
									new AsyncCallback<Void>() {
										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void result) {
											instancesList.removeSelectedData();
											instancesList.deselectAllRecords();
											historiesList.removeSelectedData();
											historiesList.deselectAllRecords();
										}
									});
						}
					}
				});
			}
		});

		contextMenu.setItems(delete);
		contextMenu.showContextMenu();
	}
}
