package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.beans.GUIVersion;
import com.logicaldoc.gui.common.client.data.VersionsDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows a list of versions of a document in a tabular way.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class VersionsPanel extends DocumentDetailTab {

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private VersionsDS dataSource;

	private ListGrid listGrid;

	public VersionsPanel(final GUIDocument document) {
		super(document, null);
		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		ListGridField user = new ListGridField("user", I18N.message("user"), 100);
		ListGridField event = new ListGridField("event", I18N.message("event"), 200);
		ListGridField version = new ListGridField("version", I18N.message("version"), 70);
		ListGridField fileVersion = new ListGridField("fileVersion", I18N.message("fileversion"), 70);
		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(false));
		date.setCanFilter(false);
		ListGridField comment = new ListGridField("comment", I18N.message("comment"));

		ListGridField type = new ListGridField("type", I18N.message("type"), 55);
		type.setType(ListGridFieldType.TEXT);
		type.setAlign(Alignment.CENTER);

		listGrid = new ListGrid();
		listGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		dataSource = new VersionsDS(document.getId(), null, 100);
		listGrid.setDataSource(dataSource);
		listGrid.setFields(user, event, type, fileVersion, version, date, comment);
		addMember(listGrid);

		listGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
			@Override
			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = event.getRecord();
				if (Session.get().getCurrentFolder().isDownload()
						&& "download".equals(Session.get().getInfo().getConfig("gui.doubleclick")))
					onDownload(document, record);
				else
					onPreview(document, record);
			}
		});

		listGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				setupContextMenu().showContextMenu();
				event.cancel();
			}
		});
	}

	protected void onDownload(final GUIDocument document, ListGridRecord record) {
		if (document.getFolder().isDownload())
			Window.open(
					GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + document.getId()
							+ "&versionId=" + record.getAttribute("id") + "&open=true", "_blank", "");
	}

	protected void onPreview(final GUIDocument document, ListGridRecord record) {
		long id = document.getId();
		String filename = document.getFileName();
		String fileVersion = record.getAttribute("fileVersion");

		if (filename == null)
			filename = record.getAttribute("title") + "." + record.getAttribute("type");

		GUIFolder folder = document.getFolder();
		PreviewPopup iv = new PreviewPopup(id, fileVersion, filename, folder != null && folder.isDownload());
		iv.show();
	}

	/**
	 * Prepares the context menu.
	 */
	private Menu setupContextMenu() {
		final ListGridRecord[] selection = listGrid.getSelectedRecords();

		Menu contextMenu = new Menu();
		MenuItem compare = new MenuItem();
		compare.setTitle(I18N.message("compare"));
		compare.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				documentService.getVersionsById(Session.get().getSid(),
						Long.parseLong(selection[0].getAttribute("id")),
						Long.parseLong(selection[1].getAttribute("id")), new AsyncCallback<GUIVersion[]>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIVersion[] result) {
								VersionsDiff diffWinfow = new VersionsDiff(result[0], result[1]);
								diffWinfow.show();
							}
						});
			}
		});

		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onDownload(document, selection[0]);
			}
		});
		download.setEnabled(document.getFolder().isDownload());

		MenuItem preview = new MenuItem();
		preview.setTitle(I18N.message("preview"));
		preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onPreview(document, selection[0]);
			}
		});

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				long[] ids = new long[selection.length];
				int i = 0;
				for (ListGridRecord record : selection)
					ids[i++] = Long.parseLong(record.getAttribute("id"));

				documentService.deleteVersions(Session.get().getSid(), ids, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						listGrid.removeSelectedData();
					}
				});
			}
		});

		compare.setEnabled(selection != null && selection.length == 2);
		delete.setEnabled(updateEnabled && selection != null && selection.length > 0
				&& !document.getVersion().equals(selection[0].getAttribute("version")));

		if (selection == null || selection.length < 1) {
			preview.setEnabled(false);
			download.setEnabled(false);
			delete.setEnabled(false);
			compare.setEnabled(false);
		}

		contextMenu.setItems(preview, download, compare, delete);

		return contextMenu;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}