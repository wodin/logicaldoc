package com.logicaldoc.gui.frontend.client.reports.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIReport;
import com.logicaldoc.gui.common.client.data.ReportsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.impex.folders.ImportFolderDetailsPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.ReportService;
import com.logicaldoc.gui.frontend.client.services.ReportServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Panel showing the list of all custom reports.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.3.1
 */
public class ReportsPanel extends VLayout {
	private ReportServiceAsync service = (ReportServiceAsync) GWT.create(ReportService.class);

	protected DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private Layout listing = new VLayout();

	private Layout detailsContainer = new VLayout();

	private ListGrid grid;

	private Canvas details = SELECT_FOLDER;

	private InfoPanel infoPanel;

	final static Canvas SELECT_FOLDER = new HTMLPanel("&nbsp;" + I18N.message("selectareport"));

	private Timer timer;

	public ReportsPanel() {
		setWidth100();
		infoPanel = new InfoPanel("");
		refresh();

		/*
		 * Create the timer that synchronize the view
		 */
		timer = new Timer() {
			public void run() {
				update();
			}
		};

		timer.scheduleRepeating(5 * 1000);
	}

	public void update() {
		service.getReports(new AsyncCallback<GUIReport[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIReport[] reports) {
				for (GUIReport report : reports) {
					for (ListGridRecord record : grid.getRecords()) {
						if (Long.parseLong(record.getAttributeAsString("id")) != report.getId())
							continue;

						long oldVersion = record.getAttributeAsLong("recordVersion");

						record.setAttribute("runningIcon", record.getAttribute("name").equals(report.getName())
								&& report.getStatus() != GUIReport.STATUS_IDLE ? "running_task" : "idle_task");
						record.setAttribute("status", report.getStatus());
						record.setAttribute("lastRun", report.getLastRun());
						record.setAttribute("lastModified", report.getLastModified());
						record.setAttribute("recordVersion", report.getRecordVersion());

						if (report.getOutputDocId() != null)
							record.setAttribute("outputDocId", "" + report.getOutputDocId());
						else
							record.setAttribute("outputDocId", (String) null);
						grid.refreshRow(grid.getRecordIndex(record));

						boolean selected = grid.getSelectedRecord() != null ? record.equals(grid.getSelectedRecord())
								: false;

						// Decide if we have to refresh the properties
						// panel
						if (selected && report.getRecordVersion() != oldVersion) {
							onSelectedReport();
						}

						break;
					}
				}
			}
		});
	}

	public void refresh() {
		detailsContainer.clear();
		listing.clear();
		if (grid != null)
			listing.removeMember(grid);
		if (details != null && details instanceof ImportFolderDetailsPanel) {
			detailsContainer.removeMember(details);
			details = SELECT_FOLDER;
		}

		// Initialize the listing panel
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("70%");
		listing.setShowResizeBar(true);

		listing.addMember(infoPanel);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField name = new ListGridField("name", I18N.message("name"), 200);
		name.setCanFilter(true);

		ListGridField outputFormat = new ListGridField("outputFormat", I18N.message("format"), 50);
		outputFormat.setCanFilter(true);

		ListGridField enabledIcon = new ListGridField("enabledIcon", " ", 24);
		enabledIcon.setType(ListGridFieldType.IMAGE);
		enabledIcon.setCanSort(false);
		enabledIcon.setAlign(Alignment.CENTER);
		enabledIcon.setShowDefaultContextMenu(false);
		enabledIcon.setImageURLPrefix(Util.imagePrefix());
		enabledIcon.setImageURLSuffix(".png");
		enabledIcon.setCanFilter(false);

		ListGridField runningIcon = new ListGridField("runningIcon", " ", 24);
		runningIcon.setType(ListGridFieldType.IMAGE);
		runningIcon.setCanSort(false);
		runningIcon.setAlign(Alignment.CENTER);
		runningIcon.setImageURLPrefix(Util.imagePrefix());
		runningIcon.setImageURLSuffix(".gif");
		runningIcon.setCanFilter(false);

		ListGridField lastRun = new ListGridField("lastRun", I18N.message("lastrun"), 110);
		lastRun.setType(ListGridFieldType.DATE);
		lastRun.setCellFormatter(new DateCellFormatter(false));
		lastRun.setCanFilter(false);
		lastRun.setAlign(Alignment.CENTER);

		grid = new ListGrid();
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setWidth100();
		grid.setHeight100();
		grid.setFields(enabledIcon, runningIcon, id, name, outputFormat, lastRun);
		grid.setSelectionType(SelectionStyle.SINGLE);
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);
		grid.setCanFreezeFields(true);
		grid.setFilterOnKeypress(true);
		grid.setDataSource(new ReportsDS());

		listing.addMember(grid);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);

		ToolStripButton refresh = new ToolStripButton();
		refresh.setTitle(I18N.message("refresh"));
		toolStrip.addButton(refresh);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

		ToolStripButton newReport = new ToolStripButton();
		newReport.setTitle(I18N.message("newreport"));
		newReport.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				grid.deselectAllRecords();
				ReportUploader dialog = new ReportUploader(ReportsPanel.this, null);
				dialog.show();
			}
		});

		/**
		 * Only admin users of the default tenant can upload new reports
		 */
		if (canUploadDesign())
			toolStrip.addButton(newReport);

		detailsContainer.setAlign(Alignment.CENTER);
		detailsContainer.addMember(details);

		setMembers(toolStrip, listing, detailsContainer);

		grid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		grid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				onSelectedReport();
			}
		});

		grid.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showreports", Integer.toString(grid.getTotalRows())));
			}
		});
	}

	private boolean canUploadDesign() {
		return Session.get().getUser().getTenantId() == Constants.TENANT_DEFAULTID
				&& Session.get().getUser().isMemberOf("admin");
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord record = grid.getSelectedRecord();
		final long selectedId = Long.parseLong(record.getAttributeAsString("id"));
		final Long outputDocId = record.getAttribute("outputDocId") != null ? Long.parseLong(record
				.getAttributeAsString("outputDocId")) : null;
		final long outputFolderId = Long.parseLong(record.getAttributeAsString("outputFolderId"));

		MenuItem execute = new MenuItem();
		execute.setTitle(I18N.message("execute"));
		execute.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.getReport(selectedId, false, new AsyncCallback<GUIReport>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIReport report) {
						ReportParametersForm form = new ReportParametersForm(report, ReportsPanel.this);
						form.show();
					}
				});
			}
		});

		if (GUIReport.STATUS_IDLE != grid.getSelectedRecord().getAttributeAsInt("status")
				|| !grid.getSelectedRecord().getAttributeAsBoolean("eenabled"))
			execute.setEnabled(false);

		MenuItem upload = new MenuItem();
		upload.setTitle(I18N.message("uploadnewdesign"));
		upload.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				GUIReport report = new GUIReport();
				report.setId(selectedId);
				report.setName(record.getAttributeAsString("name"));
				ReportUploader uploader = new ReportUploader(ReportsPanel.this, report);
				uploader.show();
			}
		});
		upload.setEnabled(canUploadDesign());

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.delete(selectedId, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									grid.removeSelectedData();
									grid.deselectAllRecords();
									showReportDetails(null);
								}
							});
						}
					}
				});
			}
		});

		MenuItem enable = new MenuItem();
		enable.setTitle(I18N.message("enable"));
		enable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.changeStatus(Long.parseLong(record.getAttributeAsString("id")), true,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								record.setAttribute("eenabled", true);
								record.setAttribute("enabledIcon", "bullet_green");
								grid.refreshRow(grid.getRecordIndex(record));
							}
						});
			}
		});
		enable.setEnabled(!grid.getSelectedRecord().getAttributeAsBoolean("eenabled"));

		MenuItem disable = new MenuItem();
		disable.setTitle(I18N.message("disable"));
		disable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				service.changeStatus(Long.parseLong(record.getAttributeAsString("id")), false,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(Void result) {
								record.setAttribute("eenabled", false);
								record.setAttribute("enabledIcon", "bullet_red");
								grid.refreshRow(grid.getRecordIndex(record));
							}
						});
			}
		});
		disable.setEnabled(grid.getSelectedRecord().getAttributeAsBoolean("eenabled"));

		MenuItem openInFolder = new MenuItem();
		openInFolder.setTitle(I18N.message("openinfolder"));
		openInFolder.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				DocumentsPanel.get().openInFolder(outputFolderId, outputDocId);
			}
		});

		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				WindowUtils.openUrl(Util.downloadURL(outputDocId));
			}
		});

		MenuItem preview = new MenuItem();
		preview.setTitle(I18N.message("preview"));
		preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				documentService.getById(outputDocId, new AsyncCallback<GUIDocument>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIDocument doc) {
						PreviewPopup iv = new PreviewPopup(doc);
						iv.show();
					}
				});
			}
		});

		if (outputDocId != null)
			contextMenu.setItems(execute, upload, enable, disable, delete, openInFolder, download, preview);
		else
			contextMenu.setItems(execute, upload, enable, disable, delete, openInFolder);
		contextMenu.showContextMenu();
	}

	public void showReportDetails(GUIReport report) {
		if (!(details instanceof ReportDetailsPanel)) {
			detailsContainer.removeMember(details);
			details = new ReportDetailsPanel(null);
			detailsContainer.addMember(details);
		}
		((ReportDetailsPanel) details).setReport(report);
	}

	public ListGrid getList() {
		return grid;
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateRecord(GUIReport report) {
		ListGridRecord record = grid.getSelectedRecord();
		if (record == null)
			record = new ListGridRecord();

		record.setAttribute("name", report.getName());
		record.setAttribute("eenabled", report.getEnabled() == 1 ? "0" : "2");
		record.setAttribute("outputFormat", report.getOutputFormat());
		if (report.getOutputFolder() != null) {
			record.setAttribute("outputFolder", report.getOutputFolder().getName());
			record.setAttribute("outputFolderId", report.getOutputFolder().getId());
		}

		if (record.getAttributeAsString("id") != null
				&& (report.getId() == Long.parseLong(record.getAttributeAsString("id")))) {
			grid.refreshRow(grid.getRecordIndex(record));
		} else {
			// Append a new record
			record.setAttribute("id", report.getId());
			grid.addData(record);
			grid.selectRecord(record);
		}
	}

	private void onSelectedReport() {
		Record record = grid.getSelectedRecord();
		if (record != null)
			service.getReport(Long.parseLong(record.getAttributeAsString("id")), true, new AsyncCallback<GUIReport>() {

				@Override
				public void onFailure(Throwable caught) {
					Log.serverError(caught);
				}

				@Override
				public void onSuccess(GUIReport report) {
					showReportDetails(report);
				}
			});
	}
}