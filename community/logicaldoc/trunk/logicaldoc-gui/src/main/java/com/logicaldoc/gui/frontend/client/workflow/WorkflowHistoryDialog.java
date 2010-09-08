package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.WorkflowHistoriesDS;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

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
		setWidth(1000);
		setHeight(650);
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

		form = new VLayout(10);
		form.setMargin(20);
//		form.setWidth(990);
//		form.setHeight(640);

		HLayout selectionWorkflowLayout = new HLayout(15);
		selectionWorkflowLayout.setMargin(20);
		selectionWorkflowLayout.setWidth(150);
		selectionWorkflowLayout.setHeight(100);

		// Workflow section
		DynamicForm workflowForm = new DynamicForm();
		// workflowForm.setWidth(150);
		workflowForm.setColWidths(1, "*");

		final ComboBoxItem workflow = new ComboBoxItem("workflowSelection", I18N.message("workflowselect"));
		workflow.setWrapTitle(false);
		ListGridField name = new ListGridField("name");
		workflow.setValueField("id");
		workflow.setDisplayField("name");
		workflow.setPickListFields(name);
		workflow.setOptionDataSource(new WorkflowsDS(null, false));
		if (selectedWorkflow != null)
			workflow.setValue(selectedWorkflow.getName());

		workflowForm.setItems(workflow);
		selectionWorkflowLayout.addMember(workflowForm);

		Button load = new Button(I18N.message("load"));
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

		// Workflow instances section
		DynamicForm workflowInstancesForm = new DynamicForm();
		workflowInstancesForm.setColWidths(1, "*");
		workflowInstancesForm.setWidth(150);

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
		ListGridField endDate = new ListGridField("enddate", I18N.message("enddate"), 150);
		endDate.setAlign(Alignment.CENTER);
		endDate.setType(ListGridFieldType.DATE);
		ListGridField documents = new ListGridField("documents", I18N.message("documents"), 220);

		instancesList = new ListGrid();
		instancesList.setCanFreezeFields(true);
		instancesList.setAutoFetchData(true);
		instancesList.setShowHeader(true);
		instancesList.setCanSelectAll(false);
		instancesList.setSelectionType(SelectionStyle.SINGLE);
		instancesList.setHeight(200);
		instancesList.setBorder("0px");
		if (selectedWorkflow != null) {
			instancesDataSource = new WorkflowHistoriesDS(selectedWorkflow.getId(), null);
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
		workflowInstancesLayout.addMember(instancesList);

		form.addMember(workflowInstancesLayout);

		// Workflow histories section
		VLayout workflowHistoriesLayout = new VLayout(10);

		ListGridField historyEvent = new ListGridField("event", I18N.message("event"), 200);
		ListGridField historyDate = new ListGridField("date", I18N.message("date"), 150);
		historyDate.setAlign(Alignment.CENTER);
		historyDate.setType(ListGridFieldType.DATE);
		ListGridField historyUser = new ListGridField("user", I18N.message("user"), 120);
		ListGridField historyDoc = new ListGridField("document", I18N.message("document"), 120);
		ListGridField historySid = new ListGridField("sessionid", I18N.message("sid"), 250);

		historiesList = new ListGrid();
		historiesList.setCanFreezeFields(true);
		historiesList.setAutoFetchData(true);
		historiesList.setShowHeader(true);
		historiesList.setCanSelectAll(false);
		historiesList.setSelectionType(SelectionStyle.NONE);
		historiesList.setHeight(250);
		historiesList.setBorder("0px");
		if (selectedWorkflowInstance != null) {
			historiesDataSource = new WorkflowHistoriesDS(selectedWorkflow.getId(), selectedWorkflowInstance);
			historiesList.setDataSource(historiesDataSource);
		}

		historiesList.setFields(historyEvent, historyDate, historyUser, historyDoc, historySid);
		workflowHistoriesLayout.addMember(historiesList);

		form.addMember(workflowHistoriesLayout);

		addMember(form);
	}

	public GUIWorkflow getSelectedWorkflow() {
		return selectedWorkflow;
	}

	public void setUser(String id) {
		user.setValue(id);
	}
}
