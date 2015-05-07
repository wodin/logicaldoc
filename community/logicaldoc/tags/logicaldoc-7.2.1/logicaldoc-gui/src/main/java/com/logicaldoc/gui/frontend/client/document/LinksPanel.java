package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.LinksDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.PreviewPopup;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows the links of a document
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LinksPanel extends DocumentDetailTab {

	private LinksDS dataSource;

	private ListGrid listGrid;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	public LinksPanel(final GUIDocument document) {
		super(document, null, null);

		ListGridField type = new ListGridField("type", I18N.message("type"), 100);
		type.setCanEdit(true);

		ListGridField direction = new ListGridField("direction", I18N.message("direction"), 60);
		direction.setCanSort(false);
		direction.setType(ListGridFieldType.IMAGE);
		direction.setCanSort(false);
		direction.setAlign(Alignment.CENTER);
		direction.setShowDefaultContextMenu(false);
		direction.setImageURLPrefix(Util.imagePrefix() + "document_");
		direction.setImageURLSuffix(".png");
		direction.setCanEdit(false);

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanEdit(false);

		ListGridField title = new ListGridField("title", I18N.message("title"), 250);
		title.setCanEdit(false);

		GUIFolder folder = Session.get().getCurrentFolder();

		listGrid = new ListGrid();
		listGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		dataSource = new LinksDS(document.getId());
		listGrid.setDataSource(dataSource);
		listGrid.setFields(type, direction, icon, title);
		addMember(listGrid);

		if (folder != null && folder.hasPermission(Constants.PERMISSION_WRITE)) {
			listGrid.setCanEdit(true);
			listGrid.setEditEvent(ListGridEditEvent.CLICK);
			listGrid.setEditByCell(true);
			listGrid.addEditCompleteHandler(new EditCompleteHandler() {
				@Override
				public void onEditComplete(EditCompleteEvent event) {
					long id = Long.parseLong(event.getOldValues().getAttribute("id"));
					String type = (String) event.getNewValues().get("type");
					documentService.updateLink(Session.get().getSid(), id, type, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							// Nothing to do
						}
					});
				}
			});

			listGrid.addCellContextClickHandler(new CellContextClickHandler() {
				@Override
				public void onCellContextClick(CellContextClickEvent event) {
					Menu contextMenu = new Menu();

					MenuItem delete = new MenuItem();
					delete.setTitle(I18N.message("ddelete"));
					delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
						public void onClick(MenuItemClickEvent event) {
							ListGridRecord[] selection = listGrid.getSelectedRecords();
							if (selection == null || selection.length == 0)
								return;
							final long[] ids = new long[selection.length];
							for (int i = 0; i < selection.length; i++) {
								ids[i] = Long.parseLong(selection[i].getAttribute("id"));
							}

							LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
								@Override
								public void execute(Boolean value) {
									if (value) {
										documentService.deleteLinks(Session.get().getSid(), ids,
												new AsyncCallback<Void>() {
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
								}
							});
						}
					});

					MenuItem preview = new MenuItem();
					preview.setTitle(I18N.message("preview"));
					preview.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
						public void onClick(MenuItemClickEvent event) {
							onPreview(listGrid.getSelectedRecord());
						}
					});

					MenuItem download = new MenuItem();
					download.setTitle(I18N.message("download"));
					download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
						public void onClick(MenuItemClickEvent event) {
							onDownload(listGrid.getSelectedRecord());
						}
					});

					contextMenu.setItems(preview, download, delete);
					contextMenu.showContextMenu();
					event.cancel();
				}
			});
		}

		listGrid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				listGrid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
					@Override
					public void onCellDoubleClick(CellDoubleClickEvent event) {
						ListGridRecord record = event.getRecord();
						if (Session.get().getCurrentFolder().isDownload()
								&& "download".equals(Session.get().getInfo().getConfig("gui.doubleclick")))
							onDownload(record);
						else
							onPreview(record);
					}
				});
			}
		});
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}

	protected void onDownload(ListGridRecord record) {
		if (document.getFolder().isDownload())
			Window.open(
					GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId="
							+ record.getAttribute("documentId") + "&open=true", "_blank", "");
	}

	protected void onPreview(ListGridRecord record) {
		long id = Long.parseLong(record.getAttribute("documentId"));
		String filename = record.getAttribute("title") + "." + record.getAttribute("type");

		GUIFolder folder = document.getFolder();
		PreviewPopup iv = new PreviewPopup(id, null, filename, folder != null && folder.isDownload());
		iv.show();
	}
}