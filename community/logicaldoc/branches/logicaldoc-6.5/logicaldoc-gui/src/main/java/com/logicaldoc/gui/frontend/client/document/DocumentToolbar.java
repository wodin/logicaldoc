package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.dashboard.WorkflowDashboard;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.services.AuditService;
import com.logicaldoc.gui.frontend.client.services.AuditServiceAsync;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowDetailsDialog;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * The toolbar to handle some documents aspects
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DocumentToolbar extends ToolStrip implements FolderObserver {
	protected ToolStripButton download = new ToolStripButton();

	protected ToolStripButton rss = new ToolStripButton();

	protected ToolStripButton pdf = new ToolStripButton();

	protected ToolStripButton add = new ToolStripButton();

	protected ToolStripButton dropSpot = new ToolStripButton();

	protected ToolStripButton subscribe = new ToolStripButton();

	protected ToolStripButton scan = new ToolStripButton();

	protected ToolStripButton bulkUpdate = new ToolStripButton();

	protected ToolStripButton archive = new ToolStripButton();

	protected ToolStripButton startWorkflow = new ToolStripButton();

	protected ToolStripButton addToWorkflow = new ToolStripButton();

	protected ToolStripButton office = new ToolStripButton();

	protected ToolStripButton print = new ToolStripButton();

	protected ToolStripButton export = new ToolStripButton();

	protected GUIDocument document;

	protected AuditServiceAsync audit = (AuditServiceAsync) GWT.create(AuditService.class);

	protected WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	public DocumentToolbar() {
		GUIFolder folder = Session.get().getCurrentFolder();
		boolean downloadEnabled = folder != null && folder.isDownload();

		prepareButtons(downloadEnabled);

		update(null);
		Session.get().addFolderObserver(this);
	}

	protected void prepareButtons(boolean downloadEnabled) {
		download.setTooltip(I18N.message("download"));
		download.setIcon(ItemFactory.newImgIcon("download.png").getSrc());
		download.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (document == null)
					return;
				WindowUtils.openUrl(GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
						+ document.getId());
			}
		});

		rss.setIcon(ItemFactory.newImgIcon("feed_add.png").getSrc());
		rss.setTooltip(I18N.message("rssfeed"));
		rss.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(
						GWT.getHostPageBaseURL() + "doc_rss?sid=" + Session.get().getSid() + "&docId="
								+ document.getId() + "&locale=" + I18N.getLocale(), "_blank", "");
			}
		});

		pdf.setIcon(ItemFactory.newImgIcon("pdf.png").getSrc());
		pdf.setTooltip(I18N.message("exportpdf"));
		pdf.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				WindowUtils.openUrl(GWT.getHostPageBaseURL() + "convertpdf?sid=" + Session.get().getSid() + "&docId="
						+ document.getId() + "&version=" + document.getVersion());
			}
		});

		add.setIcon(ItemFactory.newImgIcon("page_white_add.png").getSrc());
		add.setTooltip(I18N.message("adddocuments"));
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final DocumentsUploader uploader = new DocumentsUploader();
				uploader.show();
				event.cancel();
			}
		});

		subscribe.setIcon(ItemFactory.newImgIcon("subscription_add.png").getSrc());
		subscribe.setTooltip(I18N.message("subscribe"));
		subscribe.setDisabled(true);
		subscribe.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				LD.ask(I18N.message("question"), I18N.message("confirmsubscribe"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							audit.subscribeDocuments(Session.get().getSid(), ids, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									Log.info(I18N.message("documentssubscribed"), null);
									Session.get().getUser()
											.setSubscriptions(Session.get().getUser().getSubscriptions() + 1);
								}
							});
						}
					}
				});
			}
		});

		dropSpot.setIcon(ItemFactory.newImgIcon("drive_add.png").getSrc());
		dropSpot.setTooltip("Drop Spot");
		dropSpot.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Util.openDropSpot();
			}
		});

		scan.setIcon(ItemFactory.newImgIcon("image_add.png").getSrc());
		scan.setTooltip(I18N.message("scandocument"));
		scan.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ScanDialog scanner = new ScanDialog();
				scanner.show();
			}
		});

		archive.setIcon(ItemFactory.newImgIcon("server_add.png").getSrc());
		archive.setTooltip(I18N.message("sendtoarchive"));
		archive.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				SendDocsToArchiveDialog archiveDialog = new SendDocsToArchiveDialog(ids);
				archiveDialog.show();
			}
		});

		startWorkflow.setIcon(ItemFactory.newImgIcon("cog_go.png").getSrc());
		startWorkflow.setTooltip(I18N.message("startworkflow"));
		startWorkflow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;

				String ids = "";
				for (int i = 0; i < selection.length; i++) {
					ids += "," + selection[i].getAttribute("id");
				}
				if (ids.startsWith(","))
					ids = ids.substring(1);

				WorkflowDialog workflowDialog = new WorkflowDialog(ids);
				workflowDialog.show();
			}
		});

		addToWorkflow.setIcon(ItemFactory.newImgIcon("cog_add.png").getSrc());
		addToWorkflow.setTooltip(I18N.message("addtoworkflow"));
		addToWorkflow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;

				String ids = "";
				for (int i = 0; i < selection.length; i++) {
					ids += "," + selection[i].getAttribute("id");
				}
				if (ids.startsWith(","))
					ids = ids.substring(1);

				workflowService.appendDocuments(Session.get().getSid(), Session.get().getCurrentWorkflow()
						.getSelectedTask().getId(), ids, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void ret) {
						MainPanel.get().selectWorkflowTab();
						workflowService.getWorkflowDetailsByTask(Session.get().getSid(), Session.get()
								.getCurrentWorkflow().getSelectedTask().getId(), new AsyncCallback<GUIWorkflow>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIWorkflow result) {
								if (result != null) {
									WorkflowDetailsDialog workflowDetailsDialog = new WorkflowDetailsDialog(
											WorkflowDashboard.get(), result);
									workflowDetailsDialog.getTabs().setSelectedTab(1);
									workflowDetailsDialog.show();
									Session.get().setCurrentWorkflow(null);
								}
							}
						});
					}
				});
			}
		});

		setHeight(27);
		addButton(download);

		if (Feature.visible(Feature.PDF)) {
			addButton(pdf);
			if (!Feature.enabled(Feature.PDF) || !downloadEnabled) {
				pdf.setDisabled(true);
				pdf.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Feature.visible(Feature.OFFICE)) {
			addButton(office);
			if (!Feature.enabled(Feature.OFFICE) || (document != null && !Util.isOfficeFile(document.getFileName()))
					|| !downloadEnabled) {
				office.setDisabled(true);
			}
			if (!Feature.enabled(Feature.OFFICE))
				office.setTooltip(I18N.message("featuredisabled"));
		}

		addSeparator();
		addButton(add);

		if (Feature.visible(Feature.DROP_SPOT)
				&& !"embedded".equals(Session.get().getInfo().getConfig("gui.dropspot.mode"))) {
			addButton(dropSpot);
			if (!Feature.enabled(Feature.DROP_SPOT)) {
				dropSpot.setDisabled(true);
				dropSpot.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Feature.visible(Feature.SCAN)) {
			addButton(scan);
			if (!Feature.enabled(Feature.SCAN)) {
				scan.setDisabled(true);
				scan.setTooltip(I18N.message("featuredisabled"));
			}
		}

		office.setTooltip(I18N.message("editwithoffice"));
		office.setIcon(ItemFactory.newImgIcon("page_white_office.png").getSrc());
		office.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (document == null)
					return;

				WindowUtils.openUrl("ldedit:" + GWT.getHostPageBaseURL() + "ldedit?action=edit&sid="
						+ Session.get().getSid() + "&docId=" + document.getId());
			}
		});

		final IntegerItem max = ItemFactory.newValidateIntegerItem("max", "", null, 1, null);
		max.setHint(I18N.message("elements"));
		max.setHintStyle("hint");
		max.setShowTitle(false);
		max.setDefaultValue(100);
		max.setWidth(40);

		bulkUpdate.setIcon(ItemFactory.newImgIcon("application_form_edit.png").getSrc());
		bulkUpdate.setTooltip(I18N.message("bulkupdate"));
		bulkUpdate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelectedRecords();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				BulkUpdateDialog dialog = new BulkUpdateDialog(ids, null);
				dialog.show();
			}
		});

		if (Feature.visible(Feature.AUDIT)) {
			addSeparator();
			addButton(subscribe);
			if (!Feature.enabled(Feature.AUDIT)) {
				subscribe.setDisabled(true);
				subscribe.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Feature.visible(Feature.RSS)) {
			addButton(rss);
			if (!Feature.enabled(Feature.RSS)) {
				rss.setDisabled(true);
				rss.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Feature.visible(Feature.ARCHIVES)) {
			addSeparator();
			addButton(archive);
			if (!Feature.enabled(Feature.ARCHIVES)) {
				archive.setDisabled(true);
				archive.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Feature.visible(Feature.BULK_UPDATE)) {
			addSeparator();
			addButton(bulkUpdate);
			if (!Feature.enabled(Feature.BULK_UPDATE)) {
				bulkUpdate.setDisabled(true);
				bulkUpdate.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Feature.visible(Feature.WORKFLOW)) {
			addSeparator();
			addButton(startWorkflow);
			addButton(addToWorkflow);
			if (!Feature.enabled(Feature.WORKFLOW)) {
				startWorkflow.setDisabled(true);
				startWorkflow.setTooltip(I18N.message("featuredisabled"));
				addToWorkflow.setDisabled(true);
				addToWorkflow.setTooltip(I18N.message("featuredisabled"));
			}
		}

		addSeparator();
		ToolStripButton display = new ToolStripButton();
		display.setTitle(I18N.message("display"));
		addButton(display);
		addFormItem(max);
		display.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (max.validate()) {
					DocumentsPanel.get().refresh((Integer) max.getValue());
				}
			}
		});

		addSeparator();
		ToolStripButton filter = new ToolStripButton();
		filter.setIcon(ItemFactory.newImgIcon("filter.png").getSrc());
		filter.setTooltip(I18N.message("filter"));
		filter.setActionType(SelectionType.CHECKBOX);
		addButton(filter);
		filter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DocumentsPanel.get().toggleFilters();
			}
		});

		addSeparator();
		ToolStripButton saveGrid = new ToolStripButton();
		saveGrid.setIcon(ItemFactory.newImgIcon("table_save.png").getSrc());
		saveGrid.setTooltip(I18N.message("savegrid"));
		saveGrid.setAutoFit(true);
		addButton(saveGrid);
		saveGrid.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DocumentsPanel.get().saveGrid();
			}
		});

		addSeparator();
		print.setIcon(ItemFactory.newImgIcon("printer.png").getSrc());
		print.setTooltip(I18N.message("print"));
		print.setAutoFit(true);
		addButton(print);
		print.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DocumentsPanel.get().printPreview();
			}
		});

		if (Feature.visible(Feature.EXPORT_CSV)) {
			addSeparator();
			export.setIcon(ItemFactory.newImgIcon("table_row_insert.png").getSrc());
			export.setTooltip(I18N.message("export"));
			export.setAutoFit(true);
			addButton(export);
			export.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					DocumentsPanel.get().export();
				}
			});
			if (!Feature.enabled(Feature.EXPORT_CSV)) {
				export.setDisabled(true);
				export.setTooltip(I18N.message("featuredisabled"));
			}
		}
	}

	/**
	 * Updates the toolbar state on the basis of the passed document
	 */
	public void update(final GUIDocument document) {
		try {
			GUIFolder folder = Session.get().getCurrentFolder();
			boolean downloadEnabled = folder != null && folder.isDownload();

			this.document = document;

			if (document == null) {
				download.setDisabled(true);
			} else {
				download.setDisabled(false);
			}

			if (document != null) {
				download.setDisabled(!downloadEnabled);
				rss.setDisabled(!Feature.enabled(Feature.RSS) || !downloadEnabled);
				pdf.setDisabled(!Feature.enabled(Feature.PDF) || !downloadEnabled);
				if (!pdf.isDisabled())
					pdf.setTooltip(I18N.message("exportpdf"));
				subscribe.setDisabled(!Feature.enabled(Feature.AUDIT));
				bulkUpdate.setDisabled(!Feature.enabled(Feature.BULK_UPDATE));

				boolean isOfficeFile = false;
				if (document.getFileName() != null)
					isOfficeFile = Util.isOfficeFile(document.getFileName());
				else if (document.getType() != null)
					isOfficeFile = Util.isOfficeFileType(document.getType());

				office.setDisabled(!Feature.enabled(Feature.OFFICE) || !isOfficeFile || !downloadEnabled);
				if (document.getStatus() != Constants.DOC_UNLOCKED
						&& !Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)) {
					if (document.getLockUserId() != null
							&& Session.get().getUser().getId() != document.getLockUserId().longValue())
						office.setDisabled(true);
				}

				if (!office.isDisabled())
					office.setTooltip(I18N.message("editwithoffice"));
			} else {
				download.setDisabled(true);
				rss.setDisabled(true);
				pdf.setDisabled(true);
				subscribe.setDisabled(true);
				archive.setDisabled(true);
				startWorkflow.setDisabled(true);
				addToWorkflow.setDisabled(true);
				bulkUpdate.setDisabled(true);
			}

			if (folder != null) {
				add.setDisabled(!folder.hasPermission(Constants.PERMISSION_WRITE));
				dropSpot.setDisabled(!folder.hasPermission(Constants.PERMISSION_WRITE)
						|| !Feature.enabled(Feature.DROP_SPOT));
				scan.setDisabled(!folder.hasPermission(Constants.PERMISSION_WRITE) || !Feature.enabled(Feature.SCAN));
				archive.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_ARCHIVE)
						|| !Feature.enabled(Feature.ARCHIVES));
				startWorkflow.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_WORKFLOW)
						|| !Feature.enabled(Feature.WORKFLOW));
				addToWorkflow.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_WORKFLOW)
						|| !Feature.enabled(Feature.WORKFLOW) || Session.get().getCurrentWorkflow() == null);
				addToWorkflow.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_WRITE)
						|| !folder.hasPermission(Constants.PERMISSION_IMPORT) || !Feature.enabled(Feature.BULK_UPDATE));
			} else {
				add.setDisabled(true);
				scan.setDisabled(true);
				archive.setDisabled(true);
				startWorkflow.setDisabled(true);
				addToWorkflow.setDisabled(true);
				bulkUpdate.setDisabled(true);
				dropSpot.setDisabled(true);
			}
		} catch (Throwable t) {

		}
	}

	@Override
	public void onFolderSelected(GUIFolder folder) {
		update(null);
	}

	@Override
	public void onFolderSaved(GUIFolder folder) {
		// Nothing to do
	}
}