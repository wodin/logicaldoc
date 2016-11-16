package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.WorkflowHistoriesDS;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
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
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

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

	private VLayout instancesContainer = new VLayout();

	private ListGrid instancesGrid;

	private VLayout historiesContainer = new VLayout();

	private ListGrid historiesGrid;

	private Long selectedWorkflowInstance = null;

	public WorkflowHistoryDialog() {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("workflowhistory"));
		setWidth(950);
		setHeight(530);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		setAutoSize(true);
		centerInPage();

		final ComboBoxItem workflow = new ComboBoxItem("workflowSelection", I18N.message("workflowselect"));
		workflow.setWrapTitle(false);
		ListGridField name = new ListGridField("name");
		workflow.setValueField("id");
		workflow.setDisplayField("name");
		workflow.setPickListFields(name);
		workflow.setOptionDataSource(new WorkflowsDS(false, true, false));
		if (selectedWorkflow != null)
			workflow.setValue(selectedWorkflow.getName());

		ToolStripButton search = new ToolStripButton();
		search.setAutoFit(true);
		search.setTitle(I18N.message("search"));
		search.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord selectedRecord = workflow.getSelectedRecord();
				if (selectedRecord == null)
					return;

				service.get(selectedRecord.getAttributeAsString("name"), new AsyncCallback<GUIWorkflow>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIWorkflow result) {
						selectedWorkflow = result;
						loadInstancesGrid();
					}
				});
			}
		});

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.addFormItem(workflow);
		toolStrip.addButton(search);
		toolStrip.addFill();
		toolStrip.setWidth100();

		instancesContainer.setWidth100();
		instancesContainer.setHeight("40%");
		instancesContainer.setShowResizeBar(true);

		historiesContainer.setWidth100();
		historiesContainer.setHeight("60%");

		setMembers(toolStrip, instancesContainer, historiesContainer);
		loadInstancesGrid();
	}

	private void loadInstancesGrid() {
		if (instancesGrid != null)
			instancesContainer.removeMember(instancesGrid);

		if (historiesGrid != null)
			historiesContainer.removeMember(historiesGrid);

		ListGridField id = new ListGridField("id", I18N.message("instance"), 60);
		ListGridField startDate = new ListGridField("startdate", I18N.message("startdate"), 120);
		startDate.setAlign(Alignment.CENTER);
		startDate.setType(ListGridFieldType.DATE);
		startDate.setCellFormatter(new DateCellFormatter(false));
		startDate.setCanFilter(false);
		ListGridField endDate = new ListGridField("enddate", I18N.message("enddate"), 120);
		endDate.setAlign(Alignment.CENTER);
		endDate.setType(ListGridFieldType.DATE);
		endDate.setCellFormatter(new DateCellFormatter(false));
		endDate.setCanFilter(false);
		ListGridField documents = new ListGridField("documents", I18N.message("documents"), 250);
		ListGridField documentIds = new ListGridField("documentIds", I18N.message("documentIds"), 300);
		documentIds.setHidden(true);

		instancesGrid = new ListGrid();
		instancesGrid.setCanFreezeFields(true);
		instancesGrid.setAutoFetchData(true);
		instancesGrid.setShowHeader(true);
		instancesGrid.setCanSelectAll(false);
		instancesGrid.setSelectionType(SelectionStyle.SINGLE);
		instancesGrid.setHeight100();
		instancesGrid.setWidth100();
		instancesGrid.setBorder("1px solid #E1E1E1");
		instancesGrid.sort("startdate", SortDirection.DESCENDING);
		if (selectedWorkflow != null)
			instancesGrid.setDataSource(new WorkflowHistoriesDS(null, Long.parseLong(selectedWorkflow.getId()), null));
		instancesGrid.setFields(id, startDate, endDate, documents, documentIds);

		instancesGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				onSelectedInstance();
			}
		});
		instancesGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		instancesContainer.addMember(instancesGrid);
	}

	private void onSelectedInstance() {
		Record record = instancesGrid.getSelectedRecord();
		selectedWorkflowInstance = Long.parseLong(record.getAttributeAsString("id"));

		if (historiesGrid != null)
			historiesContainer.removeMember(historiesGrid);

		ListGridField historyId = new ListGridField("id", I18N.message("id"), 60);
		historyId.setHidden(true);

		ListGridField historyEvent = new ListGridField("event", I18N.message("event"), 200);
		ListGridField historyName = new ListGridField("name", I18N.message("task"), 200);
		historyName.setHidden(true);

		ListGridField historyDate = new ListGridField("startdate", I18N.message("startdate"), 120);
		historyDate.setAlign(Alignment.CENTER);
		historyDate.setType(ListGridFieldType.DATE);
		historyDate.setCellFormatter(new DateCellFormatter(false));
		historyDate.setCanFilter(false);
		ListGridField historyUser = new ListGridField("user", I18N.message("user"), 120);
		ListGridField historyComment = new ListGridField("comment", I18N.message("comment"));
		historyComment.setWidth("*");
		ListGridField historyFilename = new ListGridField("filename", I18N.message("document"), 180);
		ListGridField historySid = new ListGridField("sessionid", I18N.message("sid"), 240);
		historySid.setHidden(true);

		historiesGrid = new ListGrid();
		historiesGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		historiesGrid.setCanFreezeFields(true);
		historiesGrid.setAutoFetchData(true);
		historiesGrid.setShowHeader(true);
		historiesGrid.setCanSelectAll(false);
		historiesGrid.setSelectionType(SelectionStyle.SINGLE);
		historiesGrid.setHeight100();
		historiesGrid.setWidth100();
		historiesGrid.sort("startdate", SortDirection.ASCENDING);
		historiesGrid.setBorder("1px solid #E1E1E1");
		historiesGrid.setDataSource(new WorkflowHistoriesDS(selectedWorkflowInstance, Long.parseLong(selectedWorkflow
				.getId()), null));
		historiesGrid.setFields(historyId, historyEvent, historyName, historyDate, historyUser, historyComment,
				historyFilename, historySid);

		historiesContainer.addMember(historiesGrid);
	}

	public GUIWorkflow getSelectedWorkflow() {
		return selectedWorkflow;
	}

	public void setUser(String id) {
		user.setValue(id);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord selection = instancesGrid.getSelectedRecord();
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
							service.deleteInstance(selection.getAttributeAsString("id"), new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									instancesGrid.removeSelectedData();
									instancesGrid.deselectAllRecords();
									historiesGrid.removeSelectedData();
									historiesGrid.deselectAllRecords();
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
