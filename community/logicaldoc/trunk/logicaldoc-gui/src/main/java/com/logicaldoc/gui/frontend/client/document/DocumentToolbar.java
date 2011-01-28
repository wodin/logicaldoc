package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Config;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
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
	private ToolStripButton download = new ToolStripButton();

	private ToolStripButton rss = new ToolStripButton();

	private ToolStripButton pdf = new ToolStripButton();

	private ToolStripButton add = new ToolStripButton();

	private ToolStripButton subscribe = new ToolStripButton();

	private ToolStripButton scan = new ToolStripButton();

	private ToolStripButton archive = new ToolStripButton();

	private ToolStripButton archiveDematerialization = new ToolStripButton();

	private ToolStripButton office = new ToolStripButton();

	private ToolStripButton startWorkflow = new ToolStripButton();

	private ToolStripButton addToWorkflow = new ToolStripButton();

	private GUIDocument document;

	private AuditServiceAsync audit = (AuditServiceAsync) GWT.create(AuditService.class);

	private WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);

	public DocumentToolbar() {
		GUIFolder folder = Session.get().getCurrentFolder();
		boolean downloadEnabled = folder != null && folder.isDownload();

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
				ListGridRecord[] selection = list.getSelection();
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
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				ArchiveDialog archiveDialod = new ArchiveDialog(ids, GUIArchive.TYPE_DEFAULT);
				archiveDialod.show();
			}
		});

		archiveDematerialization.setIcon(ItemFactory.newImgIcon("server_cd.png").getSrc());
		archiveDematerialization.setTooltip(I18N.message("sendtostoragearchive"));
		archiveDematerialization.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				ArchiveDialog archiveDialod = new ArchiveDialog(ids, GUIArchive.TYPE_STORAGE);
				archiveDialod.show();
			}
		});

		office.setTooltip(I18N.message("editwithoffice"));
		office.setIcon(ItemFactory.newImgIcon("page_white_office.png").getSrc());
		office.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (document == null)
					return;

				WindowUtils.openUrl("ldedit:" + GWT.getHostPageBaseURL() + "ldeditnow?action=edit&sid="
						+ Session.get().getSid() + "&docId=" + document.getId());
			}
		});

		startWorkflow.setIcon(ItemFactory.newImgIcon("cog_go.png").getSrc());
		startWorkflow.setTooltip(I18N.message("startworkflow"));
		startWorkflow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelection();
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
				ListGridRecord[] selection = list.getSelection();
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

		addSeparator();
		addButton(add);

		if (Feature.visible(Feature.SCAN)) {
			addButton(scan);
			if (!Feature.enabled(Feature.SCAN)) {
				scan.setDisabled(true);
				scan.setTooltip(I18N.message("featuredisabled"));
			}
		}

		if (Feature.visible(Feature.OFFICE) && "true".equals(Config.getProperty(Constants.OFFICE_ENABLED))) {
			addButton(office);
			if (!Feature.enabled(Feature.OFFICE) || (document != null && !Util.isOfficeFile(document.getFileName()))
					|| !downloadEnabled) {
				office.setDisabled(true);
			}
			if (!Feature.enabled(Feature.OFFICE))
				office.setTooltip(I18N.message("featuredisabled"));
		}

		final IntegerItem max = ItemFactory.newValidateIntegerItem("max", "", null, 1, null);
		max.setHint(I18N.message("elements"));
		max.setShowTitle(false);
		max.setDefaultValue(100);
		max.setWidth(40);

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
			addButton(archiveDematerialization);
			if (!Feature.enabled(Feature.ARCHIVES)) {
				archive.setDisabled(true);
				archive.setTooltip(I18N.message("featuredisabled"));
			}
			if (!Feature.enabled(Feature.PAPER_DEMATERIALIZATION)) {
				archiveDematerialization.setDisabled(true);
				archiveDematerialization.setTooltip(I18N.message("featuredisabled"));
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
		filter.setActionType(SelectionType.CHECKBOX);
		filter.setTitle(I18N.message("filter"));
		addButton(filter);
		filter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				DocumentsPanel.get().toggleFilters();
			}
		});

		addSeparator();
		ToolStripButton saveGrid = new ToolStripButton(I18N.message("savegrid"));
		saveGrid.setAutoFit(true);
		addButton(saveGrid);
		saveGrid.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DocumentsPanel.get().saveGrid();
			}
		});

		update(null);
		Session.get().addFolderObserver(this);
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
				archiveDematerialization.setDisabled(true);
				office.setDisabled(true);
				startWorkflow.setDisabled(true);
				addToWorkflow.setDisabled(true);
			}

			if (folder != null) {
				add.setDisabled(!folder.hasPermission(Constants.PERMISSION_WRITE));
				scan.setDisabled(!folder.hasPermission(Constants.PERMISSION_WRITE) || !Feature.enabled(Feature.SCAN));
				archive.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_ARCHIVE)
						|| !Feature.enabled(Feature.ARCHIVES));
				archiveDematerialization.setDisabled(document == null
						|| !folder.hasPermission(Constants.PERMISSION_ARCHIVE)
						|| !Feature.enabled(Feature.PAPER_DEMATERIALIZATION));
				startWorkflow.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_WORKFLOW)
						|| !Feature.enabled(Feature.WORKFLOW));
				addToWorkflow.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_WORKFLOW)
						|| !Feature.enabled(Feature.WORKFLOW) || Session.get().getCurrentWorkflow() == null);
			} else {
				add.setDisabled(true);
				scan.setDisabled(true);
				archive.setDisabled(true);
				archiveDematerialization.setDisabled(true);
				startWorkflow.setDisabled(true);
				addToWorkflow.setDisabled(true);
			}
		} catch (Throwable t) {

		}
	}

	@Override
	public void onFolderSelect(GUIFolder folder) {
		update(null);
	}
}