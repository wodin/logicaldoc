package com.logicaldoc.gui.frontend.client.document;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.DocumentsDS;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowDesigner;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This popup window is used to start a workflow on the selected documents.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowDialog extends Window {

	private WorkflowServiceAsync service = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	private VLayout layout = null;

	private TabSet tabs = new TabSet();

	private Tab workflowSettings = null;

	private Tab workflowAssignment = null;

	private Tab chooseWorkflow = null;

	private ListGrid deployedWorkflowsList;

	private ListGrid docsAppendedList;

	private String wflName = null;

	private String wflDescription = "";

	private String docIds = "";

	private IButton startWorkflow = null;

	private GUIWorkflow selectedWorkflow = null;

	private ValuesManager vm = new ValuesManager();

	public WorkflowDialog(String ids) {
		this.docIds = ids;

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("startworkflow"));
		setWidth(600);
		setHeight(400);
		setCanDragResize(true);
		setIsModal(true);
		setShowModalMask(true);
		centerInPage();

		layout = new VLayout(20);
		layout.setMargin(25);

		refreshTabs(null, null, 0);
	}

	public void refreshTabs(String workflowName, String workflowDescription, int selectedTab) {
		if (tabs != null) {
			layout.removeMember(tabs);
		}

		if (startWorkflow != null) {
			layout.removeMember(startWorkflow);
		}

		wflName = workflowName;
		wflDescription = workflowDescription;

		// When the first tab is selected, the others are disabled.
		// When the second tab is selected, the first one is disabled.

		SC.warn("wflName: " + wflName + " --- wflDescription: " + wflDescription);

		tabs = new TabSet();
		tabs.setWidth(550);
		tabs.setHeight(300);

		chooseWorkflow = new Tab();
		chooseWorkflow.setTitle(I18N.message("chooseworkflow"));

		DynamicForm chooseWorkflowForm = new DynamicForm();
		chooseWorkflowForm.setTitleOrientation(TitleOrientation.TOP);
		chooseWorkflowForm.setWidth100();
		chooseWorkflowForm.setHeight100();

		ListGridField name = new ListGridField("name", I18N.message("name"), 100);
		ListGridField descr = new ListGridField("description", I18N.message("description"), 150);

		deployedWorkflowsList = new ListGrid();
		deployedWorkflowsList.setCanFreezeFields(true);
		deployedWorkflowsList.setAutoFetchData(true);
		deployedWorkflowsList.setShowHeader(false);
		deployedWorkflowsList.setCanSelectAll(false);
		deployedWorkflowsList.setSelectionType(SelectionStyle.NONE);
		deployedWorkflowsList.setHeight100();
		deployedWorkflowsList.setBorder("2px");
		deployedWorkflowsList.setDataSource(new WorkflowsDS(null, true));
		deployedWorkflowsList.setFields(name, descr);

		deployedWorkflowsList.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = event.getRecord();
				refreshTabs(record.getAttributeAsString("name"), record.getAttributeAsString("description"), 1);
			}
		});

		chooseWorkflowForm.addChild(deployedWorkflowsList);
		chooseWorkflow.setPane(chooseWorkflowForm);

		workflowSettings = new Tab();
		workflowSettings.setTitle(I18N.message("workflowsettings"));

		VLayout settingLayout = new VLayout(10);
		settingLayout.setMargin(15);

		DynamicForm workflowSettingsForm = new DynamicForm();
		workflowSettingsForm.setTitleOrientation(TitleOrientation.TOP);
		workflowSettingsForm.setWidth100();
		workflowSettingsForm.setHeight100();
		workflowSettingsForm.setNumCols(1);
		workflowSettingsForm.setMargin(10);
		workflowSettingsForm.setValuesManager(vm);

		// Workflow Definition Description
		TextAreaItem wflDescr = ItemFactory.newTextAreaItem("wfldescr", I18N.message("description"), wflDescription);

		// Workflow Priority
		SelectItem priority = ItemFactory.newPrioritySelector("priority", I18N.message("workflowpriority"));

		workflowSettingsForm.setItems(wflDescr, priority);
		settingLayout.addMember(workflowSettingsForm);

		// Workflow appended Documents list
		ListGridField docName = new ListGridField("title", I18N.message("name"), 100);
		ListGridField docLastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 150);
		docLastModified.setAlign(Alignment.CENTER);
		docLastModified.setType(ListGridFieldType.DATE);
		docLastModified.setCellFormatter(new DateCellFormatter());

		docsAppendedList = new ListGrid();
		docsAppendedList.setCanFreezeFields(true);
		docsAppendedList.setAutoFetchData(true);
		docsAppendedList.setShowHeader(false);
		docsAppendedList.setCanSelectAll(false);
		docsAppendedList.setSelectionType(SelectionStyle.NONE);
		docsAppendedList.setHeight100();
		docsAppendedList.setBorder("0px");
		docsAppendedList.setDataSource(new DocumentsDS(docIds));
		docsAppendedList.setFields(docName, docLastModified);

		settingLayout.addMember(docsAppendedList);
		workflowSettings.setPane(settingLayout);

		workflowAssignment = new Tab();
		workflowAssignment.setTitle(I18N.message("workflowassignment"));

		DynamicForm workflowAssignmentForm = new DynamicForm();
		workflowAssignmentForm.setTitleOrientation(TitleOrientation.TOP);
		workflowAssignmentForm.setNumCols(1);

		if (wflName != null) {
			service.get(Session.get().getSid(), wflName, new AsyncCallback<GUIWorkflow>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIWorkflow result) {
					if (result != null)
						selectedWorkflow = result;
				}
			});

			workflowAssignment.setPane(new WorkflowDesigner(selectedWorkflow, true));
		}

		tabs.setTabs(chooseWorkflow, workflowSettings, workflowAssignment);

		tabs.setSelectedTab(selectedTab);

		layout.addMember(tabs);

		startWorkflow = new IButton();
		startWorkflow.setTitle(I18N.message("startworkflow"));
		startWorkflow.setDisabled(wflName == null);
		startWorkflow.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (wflName == null)
					return;

				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					service.startWorkflow(Session.get().getSid(), wflName, values.get("wfldescr").toString(), docIds,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void ret) {
									destroy();
								}
							});
				}
			}
		});
		layout.addMember(startWorkflow);

		addChild(layout);
	}
}
