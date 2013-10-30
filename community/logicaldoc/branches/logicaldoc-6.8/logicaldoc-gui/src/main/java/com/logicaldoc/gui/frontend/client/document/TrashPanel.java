package com.logicaldoc.gui.frontend.client.document;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.GarbageDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.folder.Navigator;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.logicaldoc.gui.frontend.client.services.FolderService;
import com.logicaldoc.gui.frontend.client.services.FolderServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
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
 * This panel shows the current user's garbage
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class TrashPanel extends VLayout {
	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private FolderServiceAsync folderService = (FolderServiceAsync) GWT.create(FolderService.class);

	private ListGrid list;

	private static TrashPanel instance;

	public static TrashPanel get() {
		if (instance == null)
			instance = new TrashPanel();
		return instance;
	}

	public TrashPanel() {
		setMembersMargin(3);

		refresh();
	}

	public void refresh() {
		if (list != null) {
			removeMember(list);
		}

		ListGridField id = new ListGridField("id");
		id.setHidden(true);

		ListGridField title = new ListGridField("title", I18N.message("title"), 150);
		title.setCanFilter(true);

		ListGridField icon = new ListGridField("icon", " ", 24);
		icon.setType(ListGridFieldType.IMAGE);
		icon.setCanSort(false);
		icon.setAlign(Alignment.CENTER);
		icon.setShowDefaultContextMenu(false);
		icon.setImageURLPrefix(Util.imagePrefix());
		icon.setImageURLSuffix(".png");
		icon.setCanFilter(false);

		ListGridField lastModified = new ListGridField("lastModified", I18N.message("lastmodified"), 110);
		lastModified.setAlign(Alignment.CENTER);
		lastModified.setType(ListGridFieldType.DATE);
		lastModified.setCellFormatter(new DateCellFormatter(false));
		lastModified.setCanFilter(false);

		ListGridField customId = new ListGridField("customId", I18N.message("customid"), 110);
		customId.setType(ListGridFieldType.TEXT);
		customId.setCanFilter(true);
		customId.setHidden(true);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setWidth100();
		list.setHeight100();
		list.setAutoFetchData(true);
		list.setFields(icon, title, customId, lastModified);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setDataSource(new GarbageDS());
		list.setShowFilterEditor(true);
		list.setFilterOnKeypress(true);
		addMember(list);

		if (Session.get().getCurrentFolder() != null && Session.get().getCurrentFolder().isWrite())
			list.addCellContextClickHandler(new CellContextClickHandler() {
				@Override
				public void onCellContextClick(CellContextClickEvent event) {
					showContextMenu();
					event.cancel();
				}
			});
	}

	private void restoreDocument(final long id) {
		documentService.restore(Session.get().getSid(), id, Session.get().getCurrentFolder().getId(),
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void ret) {
						list.removeSelectedData();
						Log.info(I18N.message("documentrestored"),
								I18N.message("documentrestoreddetail", Long.toString(id)));

						// Force a refresh
						Session.get().setCurrentFolder(Session.get().getCurrentFolder());
					}
				});
	}

	private void restoreFolder(final long id) {
		folderService.restore(Session.get().getSid(), id, Session.get().getCurrentFolder().getId(),
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void ret) {
						list.removeSelectedData();
						Log.info(I18N.message("folderrestored"),
								I18N.message("folderrestoreddetail", Long.toString(id)));

						// Force a reload
						Navigator.get().reload();
					}
				});
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem execute = new MenuItem();
		execute.setTitle(I18N.message("restore"));
		execute.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord record = list.getSelectedRecord();
				if ("document".equals(record.getAttribute("type")))
					restoreDocument(Long.parseLong(record.getAttribute("id")));
				else
					restoreFolder(Long.parseLong(record.getAttribute("id")));
			}
		});

		contextMenu.setItems(execute);
		contextMenu.showContextMenu();
	}
}