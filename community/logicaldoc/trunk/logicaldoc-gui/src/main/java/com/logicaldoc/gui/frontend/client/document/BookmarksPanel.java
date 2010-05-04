package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.data.BookmarksDS;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

/**
 * This panel shows the current user's bookmarks
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class BookmarksPanel extends VLayout {
	private DocumentServiceAsync service = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private ListGrid list;

	private static BookmarksPanel instance;

	public static BookmarksPanel get() {
		if (instance == null)
			instance = new BookmarksPanel();
		return instance;
	}

	private BookmarksPanel() {
		setMembersMargin(3);

		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		LengthRangeValidator validator = new LengthRangeValidator();
		validator.setMin(1);

		ListGridField name = new ListGridField("name", I18N.getMessage("name"), 80);
		name.setCanEdit(true);
		name.setValidators(validator);

		ListGridField description = new ListGridField("description", I18N.getMessage("description"));
		description.setCanEdit(true);
		description.setValidators(validator);

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix() + "/application/");
		icon.setImageURLSuffix(".png");
		icon.setCanEdit(false);
		icon.setCanFilter(false);

		list = new ListGrid();
		list.setCanEdit(true);
		list.setEditEvent(ListGridEditEvent.DOUBLECLICK);
		list.setModalEditing(true);
		list.setWidth100();
		list.setHeight100();
		list.setAutoFetchData(true);
		list.setFields(icon, name, description);
		list.setDataSource(BookmarksDS.get());
		list.setShowFilterEditor(true);
		list.setFilterOnKeypress(true); 
		addMember(list);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		list.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				DocumentsPanel.get().onSelectedDocument(Long.parseLong(record.getAttributeAsString("docId")));
			}

		});

		list.addEditCompleteHandler(new EditCompleteHandler() {
			@Override
			public void onEditComplete(EditCompleteEvent event) {
				Record record = event.getOldRecord();
				GUIBookmark bookmark = new GUIBookmark();
				bookmark.setId(Long.parseLong(record.getAttributeAsString("id")));
				bookmark.setName((String) event.getNewValues().get("name"));
				bookmark.setDescription((String) event.getNewValues().get("description"));
				service.updateBookmark(Session.get().getSid(), bookmark, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void ret) {

					}
				});
			}
		});

	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem download = new MenuItem();
		download.setTitle(I18N.getMessage("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				download();
			}
		});

		MenuItem openInFolder = new MenuItem();
		openInFolder.setTitle(I18N.getMessage("openinfolder"));
		openInFolder.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
						Long.parseLong(record.getAttributeAsString("docId")));
			}
		});

		if (!(list.getSelection() != null && list.getSelection().length == 1)) {
			download.setEnabled(false);
			openInFolder.setEnabled(false);
		}

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.getMessage("delete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord[] selection = list.getSelection();
				if (selection == null || selection.length == 0)
					return;
				final long[] ids = new long[selection.length];
				for (int i = 0; i < selection.length; i++) {
					ids[i] = Long.parseLong(selection[i].getAttribute("id"));
				}

				SC.ask(I18N.getMessage("question"), I18N.getMessage("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.deleteBookmarks(Session.get().getSid(), ids, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									list.removeSelectedData();
								}
							});
						}
					}
				});
			}
		});

		contextMenu.setItems(download, delete, openInFolder);
		contextMenu.showContextMenu();
	}

	public void reload() {
		list.fetchData();
	}

	private void download() {
		String id = list.getSelectedRecord().getAttribute("docId");
		Window.open("download?sid=" + Session.get().getSid() + "&docId=" + id + "&open=true", "_blank", "");
	}
}