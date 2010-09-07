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
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Canvas;
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
import com.smartgwt.client.widgets.layout.Layout;
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

	private String wflName = "";

	private String wflDescription = "";

	private String docIds = "";

	private IButton startWorkflow = null;

	private GUIWorkflow selectedWorkflow = null;

	private ValuesManager vm = new ValuesManager();

	private DataSource datasource = null;

	private VLayout workflowSettingsLayout = null;

	private DynamicForm workflowSettingsForm = new DynamicForm();;

	private TextAreaItem wflDescriptionItem = null;

	private Layout wflLayout = null;

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

		refreshTabs("", "", 0);
	}

	public void refreshTabs(String workflowName, String workflowDescription, int selectedTab) {
		if (workflowSettingsLayout != null) {
			workflowSettingsLayout.removeMember(workflowSettingsForm);
		}

		if (wflLayout != null)
			wflLayout.clear();

		if (tabs != null) {
			layout.removeMember(tabs);
		}

		if (startWorkflow != null) {
			layout.removeMember(startWorkflow);
		}

		wflName = workflowName;
		wflDescription = workflowDescription;

		tabs = new TabSet();
		tabs.setWidth(550);
		tabs.setHeight(300);

		chooseWorkflow = new Tab(I18N.message("chooseworkflow"));
		tabs.addTab(chooseWorkflow, 0);

		workflowSettings = new Tab(I18N.message("workflowsettings"));
		tabs.addTab(workflowSettings, 1);

		workflowAssignment = new Tab(I18N.message("workflowassignment"));
		workflowAssignment.setPane(new Canvas());
		tabs.addTab(workflowAssignment, 2);

		// tabs.setTabs(chooseWorkflow, workflowSettings, workflowAssignment);

		// SC.warn("create tabs!!!");

		DynamicForm chooseWorkflowForm = new DynamicForm();
		chooseWorkflowForm.setTitleOrientation(TitleOrientation.TOP);
		chooseWorkflowForm.setWidth100();
		chooseWorkflowForm.setHeight100();

		ListGridField name = new ListGridField("name", I18N.message("name"), 100);
		ListGridField descr = new ListGridField("description", I18N.message("description"), 150);

		deployedWorkflowsList = new ListGrid();
		deployedWorkflowsList.setCanFreezeFields(true);
		deployedWorkflowsList.setAutoFetchData(true);
		deployedWorkflowsList.setShowHeader(true);
		deployedWorkflowsList.setCanSelectAll(false);
		deployedWorkflowsList.setSelectionType(SelectionStyle.NONE);
		deployedWorkflowsList.setHeight100();
		deployedWorkflowsList.setBorder("2px");
		datasource = new WorkflowsDS(null, true);
		if (datasource != null)
			deployedWorkflowsList.setDataSource(datasource);
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

		workflowSettingsLayout = new VLayout(10);
		workflowSettingsLayout.setMargin(15);

		workflowSettingsForm = new DynamicForm();
		workflowSettingsForm.setTitleOrientation(TitleOrientation.TOP);
		workflowSettingsForm.setWidth100();
		workflowSettingsForm.setHeight100();
		workflowSettingsForm.setNumCols(1);
		workflowSettingsForm.setMargin(10);
		workflowSettingsForm.setValuesManager(vm);

		// Workflow Definition Description
		wflDescriptionItem = new TextAreaItem("wfldescr", I18N.message("description"));
		wflDescriptionItem.setValue(wflDescription);

		// Workflow Priority
		SelectItem priority = ItemFactory.newPrioritySelector("priority", I18N.message("workflowpriority"));

		workflowSettingsForm.setItems(wflDescriptionItem, priority);
		workflowSettingsLayout.addMember(workflowSettingsForm);

		// Workflow appended Documents list
		ListGridField docName = new ListGridField("title", I18N.message("name"), 100);
		ListGridField docLastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 150);
		docLastModified.setAlign(Alignment.CENTER);
		docLastModified.setType(ListGridFieldType.DATE);
		docLastModified.setCellFormatter(new DateCellFormatter());

		docsAppendedList = new ListGrid();
		docsAppendedList.setCanFreezeFields(true);
		docsAppendedList.setAutoFetchData(true);
		docsAppendedList.setShowHeader(true);
		docsAppendedList.setCanSelectAll(false);
		docsAppendedList.setSelectionType(SelectionStyle.NONE);
		docsAppendedList.setHeight100();
		docsAppendedList.setBorder("0px");
		docsAppendedList.setDataSource(new DocumentsDS(docIds));
		docsAppendedList.setFields(docName, docLastModified);

		// SC.warn("docsAppendedList !!!");

		workflowSettingsLayout.addMember(docsAppendedList);
		workflowSettings.setPane(workflowSettingsLayout);

		// SC.warn("before assign !!!");

		wflLayout = new Layout();
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

		if (selectedWorkflow != null)
			wflLayout.addMember(new WorkflowDesigner(selectedWorkflow, true));
		else
			wflLayout.addMember(new WorkflowDesigner(new GUIWorkflow(), true));

		workflowAssignment.setPane(wflLayout);

		// SC.warn("after assign !!!");

		tabs.setSelectedTab(selectedTab);

		layout.addMember(tabs);
		layout.redraw();

		// SC.warn("before button !!!");

		startWorkflow = new IButton();
		startWorkflow.setTitle(I18N.message("startworkflow"));
		startWorkflow.setDisabled(wflName.trim().isEmpty());
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
