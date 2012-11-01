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
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
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
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
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

	private Tab chooseWorkflow = null;

	private ListGrid deployedWorkflowsList;

	private ListGrid docsAppendedList;

	private String docIds = "";

	private IButton startWorkflow = null;

	private GUIWorkflow selectedWorkflow = null;

	private ValuesManager vm = new ValuesManager();

	private DataSource datasource = null;

	private VLayout workflowSettingsLayout = null;

	private DynamicForm workflowSettingsForm = new DynamicForm();;

	private TextAreaItem wflDescriptionItem = null;

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

		layout = new VLayout(10);
		layout.setMargin(30);

		workflowSettingsLayout = new VLayout(5);
		workflowSettingsLayout.setMargin(5);

		refreshTabs(0);
	}

	public void refreshTabs(int selectedTab) {
		Canvas[] members = workflowSettingsLayout.getMembers();
		for (Canvas canvas : members) {
			workflowSettingsLayout.removeMember(canvas);
		}

		if (tabs != null) {
			layout.removeMember(tabs);
		}

		if (startWorkflow != null)
			layout.removeMember(startWorkflow);

		tabs = new TabSet();
		tabs.setWidth(550);
		tabs.setHeight(300);

		chooseWorkflow = new Tab(I18N.message("chooseworkflow"));
		tabs.addTab(chooseWorkflow, 0);

		workflowSettings = new Tab(I18N.message("workflowsettings"));
		tabs.addTab(workflowSettings, 1);

		DynamicForm chooseWorkflowForm = new DynamicForm();
		chooseWorkflowForm.setTitleOrientation(TitleOrientation.TOP);
		chooseWorkflowForm.setWidth(500);
		chooseWorkflowForm.setHeight(280);
		chooseWorkflowForm.setMargin(15);

		ListGridField name = new ListGridField("name", I18N.message("name"), 100);
		ListGridField descr = new ListGridField("description", I18N.message("description"), 150);

		deployedWorkflowsList = new ListGrid();
		deployedWorkflowsList.setEmptyMessage(I18N.message("notitemstoshow"));
		deployedWorkflowsList.setCanFreezeFields(true);
		deployedWorkflowsList.setAutoFetchData(true);
		deployedWorkflowsList.setShowHeader(true);
		deployedWorkflowsList.setCanSelectAll(false);
		deployedWorkflowsList.setSelectionType(SelectionStyle.SINGLE);
		deployedWorkflowsList.setWidth(300);
		deployedWorkflowsList.setHeight(200);
		deployedWorkflowsList.setBorder("1px solid #E1E1E1");
		datasource = new WorkflowsDS(true, false);
		if (datasource != null)
			deployedWorkflowsList.setDataSource(datasource);
		deployedWorkflowsList.setFields(name, descr);

		deployedWorkflowsList.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				ListGridRecord record = deployedWorkflowsList.getSelectedRecord();
				service.get(Session.get().getSid(), record.getAttributeAsString("name"),
						new AsyncCallback<GUIWorkflow>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIWorkflow result) {
								if (result != null) {
									selectedWorkflow = result;
									refreshTabs(1);
								}
							}
						});
			}
		});

		chooseWorkflowForm.addChild(deployedWorkflowsList);
		chooseWorkflow.setPane(chooseWorkflowForm);

		workflowSettingsForm = new DynamicForm();
		workflowSettingsForm.setTitleOrientation(TitleOrientation.TOP);
		workflowSettingsForm.setWidth(500);
		workflowSettingsForm.setHeight(110);
		workflowSettingsForm.setNumCols(1);
		workflowSettingsForm.setValuesManager(vm);

		// Workflow Definition Description
		wflDescriptionItem = new TextAreaItem("wfldescr", I18N.message("description"));
		if (selectedWorkflow != null)
			wflDescriptionItem.setValue(selectedWorkflow.getDescription());
		wflDescriptionItem.setWidth(250);
		wflDescriptionItem.setHeight(40);

		workflowSettingsForm.setItems(wflDescriptionItem);
		workflowSettingsLayout.addMember(workflowSettingsForm);

		// Workflow appended Documents list
		ListGridField docName = new ListGridField("title", I18N.message("name"), 180);
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
		docsAppendedList.setCanFreezeFields(true);
		docsAppendedList.setAutoFetchData(true);
		docsAppendedList.setShowHeader(true);
		docsAppendedList.setCanSelectAll(false);
		docsAppendedList.setSelectionType(SelectionStyle.NONE);
		docsAppendedList.setWidth(380);
		docsAppendedList.setHeight(130);
		docsAppendedList.setBorder("1px solid #E1E1E1");
		docsAppendedList.setDataSource(new DocumentsDS(docIds));
		docsAppendedList.setFields(icon, docName, docLastModified);

		workflowSettingsLayout.addMember(docsAppendedList);
		workflowSettings.setPane(workflowSettingsLayout);

		tabs.setSelectedTab(selectedTab);

		layout.addMember(tabs);

		startWorkflow = new IButton();
		startWorkflow.setTitle(I18N.message("startworkflow"));
		startWorkflow.setDisabled(selectedWorkflow == null);
		startWorkflow.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				if (selectedWorkflow == null)
					return;

				final Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					selectedWorkflow.setDescription((String) values.get("wfldescr"));
					service.startWorkflow(Session.get().getSid(), selectedWorkflow.getName(), values.get("wfldescr")!=null ? values.get("wfldescr")
							.toString() : "", docIds, new AsyncCallback<Void>() {

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
