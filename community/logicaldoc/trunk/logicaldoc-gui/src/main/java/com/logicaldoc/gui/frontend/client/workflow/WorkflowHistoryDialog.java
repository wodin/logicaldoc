package com.logicaldoc.gui.frontend.client.workflow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.data.WorkflowHistoriesDS;
import com.logicaldoc.gui.common.client.data.WorkflowsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.DocUtil;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
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
import com.smartgwt.client.widgets.form.fields.TextItem;
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

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private GUIWorkflow selectedWorkflow = null;

	private ComboBoxItem user = null;

	private VLayout instancesContainer = new VLayout();

	private ListGrid instancesGrid;

	private VLayout historiesContainer = new VLayout();

	private ListGrid historiesGrid;

	private Long selectedWorkflowInstance = null;

	private String tagFilter = null;

	public WorkflowHistoryDialog() {
		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);

		setTitle(I18N.message("workflowhistory"));
		setWidth(950);
		setHeight100();
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
		workflow.setOptionDataSource(new WorkflowsDS(false, false));
		if (selectedWorkflow != null)
			workflow.setValue(selectedWorkflow.getName());

		final TextItem tagItem = ItemFactory.newTextItem("tag", "tag", null);

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
						tagFilter = tagItem.getValueAsString();
						loadInstancesGrid();
					}
				});
			}
		});

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.addFormItem(workflow);
		toolStrip.addFormItem(tagItem);
		toolStrip.addButton(search);
		toolStrip.addFill();
		toolStrip.setWidth100();

		instancesContainer.setWidth100();
		instancesContainer.setHeight("40%");
		instancesContainer.setShowResizeBar(true);

		historiesContainer.setWidth100();
		historiesContainer.setHeight100();

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
		ListGridField tag = new ListGridField("tag", I18N.message("tag"), 150);
		ListGridField documents = new ListGridField("documents", I18N.message("documents"), 250);
		ListGridField documentIds = new ListGridField("documentIds", I18N.message("documentids"), 300);
		documentIds.setHidden(true);
		ListGridField initiator = new ListGridField("initiator", I18N.message("initiator"), 100);

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
			instancesGrid.setDataSource(new WorkflowHistoriesDS(null, Long.parseLong(selectedWorkflow.getId()), null,
					tagFilter));
		instancesGrid.setFields(id, tag, startDate, endDate, documents, initiator, documentIds);

		instancesGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				onSelectedInstance();
			}
		});
		instancesGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showInstanceContextMenu();
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

		ListGridField historyDate = new ListGridField("date", I18N.message("date"), 120);
		historyDate.setAlign(Alignment.CENTER);
		historyDate.setType(ListGridFieldType.DATE);
		historyDate.setCellFormatter(new DateCellFormatter(false));
		historyDate.setCanFilter(false);
		ListGridField historyUser = new ListGridField("user", I18N.message("user"), 120);
		ListGridField historyComment = new ListGridField("comment", I18N.message("comment"));
		historyComment.setWidth("*");
		ListGridField historyFilename = new ListGridField("filename", I18N.message("document"), 180);
		ListGridField documentId = new ListGridField("documentId", I18N.message("docid"), 80);
		documentId.setHidden(true);
		ListGridField historySid = new ListGridField("sessionid", I18N.message("sid"), 240);
		historySid.setHidden(true);
		ListGridField transition = new ListGridField("transition", I18N.message("transition"), 120);
		transition.setHidden(true);

		historiesGrid = new ListGrid();
		historiesGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		historiesGrid.setCanFreezeFields(true);
		historiesGrid.setAutoFetchData(true);
		historiesGrid.setShowHeader(true);
		historiesGrid.setCanSelectAll(false);
		historiesGrid.setSelectionType(SelectionStyle.SINGLE);
		historiesGrid.setHeight100();
		historiesGrid.setWidth100();
		historiesGrid.sort("date", SortDirection.ASCENDING);
		historiesGrid.setBorder("1px solid #E1E1E1");
		historiesGrid.setDataSource(new WorkflowHistoriesDS(selectedWorkflowInstance, Long.parseLong(selectedWorkflow
				.getId()), null, null));
		historiesGrid.setFields(historyId, historyEvent, historyName, historyDate, historyUser, historyComment,
				historyFilename, transition, documentId, historySid);
		historiesGrid.addCellContextClickHandler(new CellContextClickHandler() {

			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				event.cancel();
				showHistoryContextMenu();
			}
		});

		historiesContainer.addMember(historiesGrid);
	}

	public GUIWorkflow getSelectedWorkflow() {
		return selectedWorkflow;
	}

	public void setUser(String id) {
		user.setValue(id);
	}

	private void showHistoryContextMenu() {
		final ListGridRecord selection = historiesGrid.getSelectedRecord();
		if (selection.getAttributeAsString("documentId") == null
				|| selection.getAttributeAsString("documentId").isEmpty())
			return;

		documentService.getById(Long.parseLong(selection.getAttributeAsString("documentId")),
				new AsyncCallback<GUIDocument>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(final GUIDocument doc) {
						if (doc == null)
							return;

						final Menu contextMenu = new Menu();

						final MenuItem preview = new MenuItem();
						preview.setTitle(I18N.message("preview"));
						preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
							public void onClick(MenuItemClickEvent event) {
								PreviewPopup iv = new PreviewPopup(doc);
								iv.show();
							}
						});

						final MenuItem download = new MenuItem();
						download.setTitle(I18N.message("download"));
						download.setEnabled(doc.getFolder().isDownload());
						download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
							public void onClick(MenuItemClickEvent event) {
								if (doc.getFolder().isDownload())
									DocUtil.download(doc.getId(), null);
							}
						});

						final MenuItem open = new MenuItem();
						open.setTitle(I18N.message("openinfolder"));
						open.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
							public void onClick(MenuItemClickEvent event) {
								destroy();
								DocumentsPanel.get().openInFolder(doc.getFolder().getId(), doc.getId());
							}
						});

						contextMenu.setItems(preview, download, open);
						contextMenu.showContextMenu();
					}
				});
	}

	private void showInstanceContextMenu() {
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
