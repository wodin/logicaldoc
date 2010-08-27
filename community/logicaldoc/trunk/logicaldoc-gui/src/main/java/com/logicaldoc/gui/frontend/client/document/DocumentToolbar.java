package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.FolderObserver;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIArchive;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.AuditService;
import com.logicaldoc.gui.frontend.client.services.AuditServiceAsync;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
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

	private ToolStripButton edit = new ToolStripButton();

	private ToolStripButton workflow = new ToolStripButton();

	private GUIDocument document;

	private AuditServiceAsync audit = (AuditServiceAsync) GWT.create(AuditService.class);

	public DocumentToolbar() {
		download.setTooltip(I18N.message("download"));
		download.setIcon(ItemFactory.newImgIcon("download.png").getSrc());
		download.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (document == null)
					return;
				Window.open(
						GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
								+ document.getId(), "_self", "");
			}
		});

		rss.setIcon(ItemFactory.newImgIcon("rss.png").getSrc());
		rss.setTooltip(I18N.message("rssfeed"));
		rss.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(
						GWT.getHostPageBaseURL() + "doc_rss?sid=" + Session.get().getSid() + "&docId="
								+ document.getId(), "_blank", "");
			}
		});

		pdf.setIcon(ItemFactory.newImgIcon("pdf.png").getSrc());
		pdf.setTooltip(I18N.message("exportpdf"));
		pdf.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(GWT.getHostPageBaseURL() + "convertpdf?sid=" + Session.get().getSid() + "&docId="
						+ document.getId() + "&version=" + document.getVersion(), "_blank", "");
			}
		});

		add.setIcon(ItemFactory.newImgIcon("document_add.png").getSrc());
		add.setTooltip(I18N.message("adddocuments"));
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final DocumentsUploader uploader = new DocumentsUploader();
				uploader.show();
				event.cancel();
			}
		});

		subscribe.setIcon(ItemFactory.newImgIcon("accept.png").getSrc());
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

				SC.ask(I18N.message("question"), I18N.message("confirmsubscribe"), new BooleanCallback() {
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
								}
							});
						}
					}
				});
			}
		});

		scan.setIcon(ItemFactory.newImgIcon("scanner.png").getSrc());
		scan.setTooltip(I18N.message("scandocument"));
		scan.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ScanDialog scanner = new ScanDialog();
				scanner.show();
			}
		});

		archive.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
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

		archiveDematerialization.setIcon(ItemFactory.newImgIcon("data_time.png").getSrc());
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

		edit.setTooltip(I18N.message("editonline"));
		edit.setIcon(ItemFactory.newImgIcon("edit.png").getSrc());
		edit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (document == null)
					return;
				Window.open("ldedit:" + GWT.getHostPageBaseURL() + "download?action=edit&sid=" + Session.get().getSid()
						+ "&docId=" + document.getId(), "_self", "");
			}
		});

		workflow.setIcon(ItemFactory.newImgIcon("data_into.png").getSrc());
		workflow.setTooltip(I18N.message("startworkflow"));
		workflow.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ListGrid list = DocumentsPanel.get().getList();
				ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;

				String ids = "";
				for (ListGridRecord rec : selection) {
					ids += "," + rec.getAttributeAsString("id");
				}
				if (ids.startsWith(","))
					ids = ids.substring(1);

				WorkflowDialog workflowDialog = new WorkflowDialog(ids);
				workflowDialog.show();
			}
		});

		setHeight(27);
		addButton(download);

		if (Feature.visible(Feature.PDF)) {
			addButton(pdf);
			if (!Feature.enabled(Feature.PDF)) {
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

		if (Feature.visible(Feature.OFFICE)) {
			addButton(edit);
			if (document != null)
				edit.setDisabled(!Util.isOfficeFile(document.getFileName()));
			if (!Feature.enabled(Feature.OFFICE)) {
				edit.setDisabled(true);
				edit.setTooltip(I18N.message("featuredisabled"));
			}
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

		addSeparator();

		if (Feature.visible(Feature.WORKFLOW)) {
			addSeparator();
			addButton(workflow);
			if (!Feature.enabled(Feature.WORKFLOW)) {
				workflow.setDisabled(true);
				workflow.setTooltip(I18N.message("featuredisabled"));
			}
		}

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

		update(null);
		Session.get().addFolderObserver(this);
	}

	/**
	 * Updates the toolbar state on the basis of the passed document
	 */
	public void update(final GUIDocument document) {
		try {
			this.document = document;

			if (document == null) {
				download.setDisabled(true);
			} else {
				download.setDisabled(false);
			}

			if (document != null) {
				rss.setDisabled(!Feature.enabled(Feature.RSS));
				pdf.setDisabled(!Feature.enabled(Feature.PDF));
				subscribe.setDisabled(!Feature.enabled(Feature.AUDIT));
				edit.setDisabled(!Feature.enabled(Feature.OFFICE));
			} else {
				rss.setDisabled(true);
				pdf.setDisabled(true);
				subscribe.setDisabled(true);
				archive.setDisabled(true);
				archiveDematerialization.setDisabled(true);
				edit.setDisabled(true);
				workflow.setDisabled(true);
			}

			GUIFolder folder = Session.get().getCurrentFolder();

			if (folder != null) {
				add.setDisabled(!folder.hasPermission(Constants.PERMISSION_WRITE));
				scan.setDisabled(!folder.hasPermission(Constants.PERMISSION_WRITE) || !Feature.enabled(Feature.SCAN));
				archive.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_ARCHIVE)
						|| !Feature.enabled(Feature.ARCHIVES));
				archiveDematerialization.setDisabled(document == null
						|| !folder.hasPermission(Constants.PERMISSION_ARCHIVE)
						|| !Feature.enabled(Feature.PAPER_DEMATERIALIZATION));
				workflow.setDisabled(document == null || !folder.hasPermission(Constants.PERMISSION_WORKFLOW)
						|| !Feature.enabled(Feature.WORKFLOW));
			} else {
				add.setDisabled(true);
				scan.setDisabled(true);
				archive.setDisabled(true);
				archiveDematerialization.setDisabled(true);
				workflow.setDisabled(true);
			}
		} catch (Throwable t) {

		}
	}

	@Override
	public void onFolderSelect(GUIFolder folder) {
		update(null);
	}
}