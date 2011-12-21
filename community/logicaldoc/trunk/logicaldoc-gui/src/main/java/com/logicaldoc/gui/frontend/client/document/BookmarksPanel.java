package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIBookmark;
import com.logicaldoc.gui.common.client.beans.GUIFolder;
import com.logicaldoc.gui.common.client.data.BookmarksDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.folder.FoldersNavigator;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.validator.LengthRangeValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
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
	private DocumentServiceAsync docService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private ListGrid list;

	private static BookmarksPanel instance;

	public static BookmarksPanel get() {
		if (instance == null)
			instance = new BookmarksPanel();
		return instance;
	}

	public BookmarksPanel() {
		setMembersMargin(3);
		reloadList();
	}

	public void reloadList() {
		if (list != null)
			removeMember(list);

		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		LengthRangeValidator validator = new LengthRangeValidator();
		validator.setMin(1);

		ListGridField name = new ListGridField("name", I18N.message("name"), 90);
		name.setCanEdit(true);
		name.setValidators(validator);

		ListGridField description = new ListGridField("description", I18N.message("description"));
		description.setCanEdit(true);
		description.setValidators(validator);

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanEdit(false);
		icon.setCanFilter(false);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanEdit(true);
		list.setEditEvent(ListGridEditEvent.DOUBLECLICK);
		list.setModalEditing(true);
		list.setWidth100();
		list.setHeight100();
		list.setAutoFetchData(true);
		list.setFields(icon, name, description);
		list.setDataSource(new BookmarksDS());
		list.setShowFilterEditor(true);
		list.setFilterOnKeypress(true);
		addMember(list);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				folderService.getFolder(Session.get().getSid(),
						Long.parseLong(record.getAttributeAsString("folderId")), false, new AsyncCallback<GUIFolder>() {

							@Override
							public void onSuccess(GUIFolder folder) {
								showContextMenu(folder);
							}

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}
						});
				event.cancel();
			}
		});

		list.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final ListGridRecord record = list.getSelectedRecord();
				folderService.getFolder(Session.get().getSid(),
						Long.parseLong(record.getAttributeAsString("folderId")), false, new AsyncCallback<GUIFolder>() {

							@Override
							public void onSuccess(GUIFolder folder) {
								if (folder != null) {
									if (record.getAttributeAsString("type").equals("0"))
										DocumentsPanel.get().onFolderSelect(
												Long.parseLong(record.getAttributeAsString("folderId")),
												Long.parseLong(record.getAttributeAsString("targetId")));
									else
										FoldersNavigator.get().openFolder(
												Long.parseLong(record.getAttributeAsString("targetId")));
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}
						});
			}
		});
	}

	private void showContextMenu(GUIFolder folder) {
		Menu contextMenu = new Menu();

		MenuItem edit = new MenuItem();
		edit.setTitle(I18N.message("edit"));
		edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				GUIBookmark bookmark = new GUIBookmark();
				bookmark.setId(Long.parseLong(record.getAttributeAsString("id")));
				bookmark.setName(record.getAttributeAsString("name"));
				bookmark.setDescription(record.getAttributeAsString("description"));
				BookmarkDialog dialog = new BookmarkDialog(bookmark);
				dialog.show();
			}
		});

		MenuItem download = new MenuItem();
		download.setTitle(I18N.message("download"));
		download.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				download();
			}
		});

		MenuItem openInFolder = new MenuItem();
		openInFolder.setTitle(I18N.message("openinfolder"));
		openInFolder.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				DocumentsPanel.get().openInFolder(Long.parseLong(record.getAttributeAsString("folderId")),
						Long.parseLong(record.getAttributeAsString("docId")));
			}
		});

		if (!(list.getSelectedRecords() != null && list.getSelectedRecords().length == 1)) {
			download.setEnabled(false);
			openInFolder.setEnabled(false);
		}

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				final ListGridRecord[] selection = list.getSelectedRecords();
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
							docService.deleteBookmarks(Session.get().getSid(), ids, new AsyncCallback<Void>() {
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

		if (folder != null && !folder.isDownload())
			download.setEnabled(false);
		else if (folder == null) {
			download.setEnabled(false);
			openInFolder.setEnabled(false);
		}

		contextMenu.setItems(edit, download, delete, openInFolder);
		contextMenu.showContextMenu();
	}

	public void reload() {
		list.fetchData();
	}

	private void download() {
		String id = list.getSelectedRecord().getAttribute("docId");
		Window.open(
				GWT.getHostPageBaseURL() + "download?sid=" + Session.get().getSid() + "&docId=" + id + "&open=true",
				"_blank", "");
	}
}