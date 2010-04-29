package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.LinksDS;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
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
		super(document, null);

		ListGridField type = new ListGridField("type", I18N.getMessage("type"), 100);
		type.setCanEdit(true);

		ListGridField direction = new ListGridField("direction", I18N.getMessage("direction"), 50);
		direction.setCanSort(false);
		direction.setType(ListGridFieldType.IMAGE);
		direction.setCanSort(false);
		direction.setAlign(Alignment.CENTER);
		direction.setShowDefaultContextMenu(false);
		direction.setImageURLPrefix(Util.imagePrefix() + "application/document_");
		direction.setImageURLSuffix(".png");
		direction.setCanEdit(false);

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix() + "application/");
		icon.setImageURLSuffix(".png");
		icon.setCanEdit(false);

		ListGridField title = new ListGridField("title", I18N.getMessage("title"), 200);
		title.setCanEdit(false);

		GUIFolder folder = Session.get().getCurrentFolder();

		listGrid = new ListGrid();
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
					long id = Long.parseLong(event.getOldRecord().getAttribute("id"));
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
					MenuItem deleteItem = new MenuItem();
					deleteItem.setTitle(I18N.getMessage("delete"));
					deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
						public void onClick(MenuItemClickEvent event) {
							ListGridRecord[] selection = listGrid.getSelection();
							if (selection == null || selection.length == 0)
								return;
							final long[] ids = new long[selection.length];
							for (int i = 0; i < selection.length; i++) {
								ids[i] = Long.parseLong(selection[i].getAttribute("id"));
							}

							SC.ask(I18N.getMessage("question"), I18N.getMessage("confirmdelete"),
									new BooleanCallback() {
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
					contextMenu.setItems(deleteItem);

					contextMenu.showContextMenu();
					event.cancel();
				}
			});
		}

		listGrid.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				String id = listGrid.getSelectedRecord().getAttribute("id");
				Window.open("download?sid=" + Session.get().getSid() + "&docId=" + id + "&open=true", "_blank", "");
			}
		});
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}