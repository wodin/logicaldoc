package com.logicaldoc.gui.frontend.client.document;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIDocument;
import com.logicaldoc.gui.common.client.data.DocumentNotesDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
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
 * This panel shows the notes on a document
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.2
 */
public class NotesPanel extends DocumentDetailTab {

	private DataSource dataSource;

	// Table of all discussions
	private ListGrid listGrid;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private VLayout container = new VLayout();

	public NotesPanel(final GUIDocument document) {
		super(document, null);
		addMember(container);
		container.setMembersMargin(2);

		ListGridField id = new ListGridField("id", I18N.message("id"), 50);
		id.setHidden(true);

		ListGridField username = new ListGridField("username", I18N.message("author"), 140);
		ListGridField date = new ListGridField("date", I18N.message("date"), 110);
		date.setAlign(Alignment.CENTER);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(false));
		date.setCanFilter(false);
		ListGridField message = new ListGridField("message", I18N.message("message"), 500);

		listGrid = new ListGrid();
		listGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		dataSource = new DocumentNotesDS(document.getId());
		listGrid.setDataSource(dataSource);
		listGrid.setFields(id, username, date, message);
		container.setHeight100();
		container.addMember(listGrid);

		Button addNote = new Button(I18N.message("addnote"));
		addNote.setAutoFit(true);
		container.addMember(addNote);
		addNote.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AddNoteWindow note = new AddNoteWindow(document.getId(), NotesPanel.this);
				note.show();
			}
		});

		listGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = new Menu();
				MenuItem deleteItem = new MenuItem();
				deleteItem.setTitle(I18N.message("ddelete"));
				deleteItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						deleteSelection();
					}
				});

				// Only administrators or the note's author can delete a note
				boolean showDeleteItem = true;
				ListGridRecord[] selection = listGrid.getSelection();
				for (int i = 0; i < selection.length; i++) {
					if (!Session.get().getUser().isMemberOf("admin")
							&& Session.get().getUser().getId() != Long.parseLong(selection[i].getAttribute("userId"))) {
						showDeleteItem = false;
					}
				}

				deleteItem.setEnabled(showDeleteItem);

				contextMenu.setItems(deleteItem);
				contextMenu.showContextMenu();
				event.cancel();
			}
		});
	}

	private void deleteSelection() {
		ListGridRecord[] selection = listGrid.getSelection();
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
					documentService.deleteNotes(Session.get().getSid(), ids, new AsyncCallback<Void>() {
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

	public void onNoteAdded(long id, String message) {
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("id", Long.toString(id));
		record.setAttribute("username", Session.get().getUser().getFullName());
		record.setAttribute("date", new Date());
		record.setAttribute("message", message);
		listGrid.addData(record);
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}