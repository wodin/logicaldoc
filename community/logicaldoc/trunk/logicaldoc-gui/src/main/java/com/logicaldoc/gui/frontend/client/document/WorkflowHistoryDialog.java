package com.logicaldoc.gui.frontend.client.document;

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
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
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

	private WorkflowHistoriesDS dataSource1;

	private WorkflowHistoriesDS dataSource2;

	private VLayout form = null;

	private Long selectedWorkflowInstance = null;

	public WorkflowHistoryDialog() {

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("history"));
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
		form.setMargin(25);
		form.setWidth100();
		form.setHeight100();

		HLayout selectionWorkflowLayout = new HLayout(10);
		selectionWorkflowLayout.setMargin(5);

		// Workflow section
		DynamicForm workflowForm = new DynamicForm();
		workflowForm.setWidth(200);
		workflowForm.setHeight(150);
		workflowForm.setColWidths(100, 100);

		final ComboBoxItem workflow = new ComboBoxItem("workflow", " ");
		workflow.setShowTitle(false);
		ListGridField name = new ListGridField("name");
		workflow.setValueField("id");
		workflow.setDisplayField("name");
//		workflow.setPickListWidth(300);
		workflow.setPickListFields(name);
		workflow.setOptionDataSource(new WorkflowsDS(null, false));
		if (selectedWorkflow != null)
			workflow.setValue(selectedWorkflow.getName());

		SC.warn("1111111111111111111111111111111");

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
		SC.warn("22222222222222222222222222222222");

		// Workflow instances section
		DynamicForm workflowInstancesForm = new DynamicForm();
		// workflowInstancesForm.setWidth(300);
		// workflowInstancesForm.setColWidths(1, "*");

		StaticTextItem workflowTitle = ItemFactory.newStaticTextItem("workflow", "", "<b>" + I18N.message("workflow")
				+ "</b>");
		workflowTitle.setShouldSaveValue(false);
		workflowTitle.setWrapTitle(false);

		workflowInstancesForm.setItems(workflowTitle);
		workflowInstancesLayout.addMember(workflowInstancesForm);

		ListGridField id = new ListGridField("id", I18N.message("id"), 70);
		ListGridField startDate = new ListGridField("startdate", I18N.message("startdate"), 150);
		startDate.setAlign(Alignment.CENTER);
		startDate.setType(ListGridFieldType.DATE);
		startDate.setCellFormatter(new DateCellFormatter());
		ListGridField endDate = new ListGridField("enddate", I18N.message("enddate"), 150);
		endDate.setAlign(Alignment.CENTER);
		endDate.setType(ListGridFieldType.DATE);
		endDate.setCellFormatter(new DateCellFormatter());
		ListGridField documents = new ListGridField("documents", I18N.message("documents"), 150);

		instancesList = new ListGrid();
		instancesList.setCanFreezeFields(true);
		instancesList.setAutoFetchData(true);
		instancesList.setShowHeader(true);
		instancesList.setCanSelectAll(false);
		instancesList.setSelectionType(SelectionStyle.SINGLE);
		instancesList.setHeight100();
		instancesList.setBorder("0px");
		if (selectedWorkflow != null) {
			dataSource1 = new WorkflowHistoriesDS(selectedWorkflow.getId(), null);
			instancesList.setDataSource(dataSource1);
		}

		SC.warn("3333333333333333333333333333333333");

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

		// Workflow hisotries section
		DynamicForm workflowHistoriesForm = new DynamicForm();
		workflowHistoriesForm.setWidth(300);
		workflowHistoriesForm.setColWidths(1, "*");

		VLayout workflowHistoriesLayout = new VLayout(10);

		StaticTextItem workflowHistoriesTitle = ItemFactory.newStaticTextItem("workflowHistoriesTitle", "", "<b>"
				+ I18N.message("history") + "</b>");
		workflowHistoriesTitle.setShouldSaveValue(false);
		workflowHistoriesTitle.setWrapTitle(false);

		workflowHistoriesForm.setItems(workflowHistoriesTitle);
		workflowHistoriesLayout.addMember(workflowHistoriesForm);

		ListGridField historyEvent = new ListGridField("event", I18N.message("event"), 200);
		ListGridField historyDate = new ListGridField("date", I18N.message("date"), 150);
		historyDate.setAlign(Alignment.CENTER);
		historyDate.setType(ListGridFieldType.DATE);
		historyDate.setCellFormatter(new DateCellFormatter());
		ListGridField historyUser = new ListGridField("user", I18N.message("user"), 150);
		ListGridField historyDoc = new ListGridField("document", I18N.message("document"), 150);
		ListGridField historySid = new ListGridField("sessionid", I18N.message("sid"), 250);

		SC.warn("4444444444444444444444444444444444");

		historiesList = new ListGrid();
		historiesList.setCanFreezeFields(true);
		historiesList.setAutoFetchData(true);
		historiesList.setShowHeader(true);
		historiesList.setCanSelectAll(false);
		historiesList.setSelectionType(SelectionStyle.NONE);
		historiesList.setHeight100();
		historiesList.setBorder("0px");
		if (selectedWorkflowInstance != null) {
			dataSource2 = new WorkflowHistoriesDS(selectedWorkflow.getId(), selectedWorkflowInstance);
			historiesList.setDataSource(dataSource2);
		}

		SC.warn("555555555555555555555555555555555555555555555555555555");

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
