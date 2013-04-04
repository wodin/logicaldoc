package com.logicaldoc.gui.frontend.client.document;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
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
import com.smartgwt.client.types.ExpansionMode;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
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

	private Button addNote;

	private DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);

	private VLayout container = new VLayout();

	public NotesPanel(final GUIDocument document) {
		super(document, null);
		addMember(container);
		container.setMembersMargin(2);

		init();
	}

	private void init() {
		if (addNote != null)
			container.removeMember(addNote);
		if (listGrid != null)
			container.removeMember(listGrid);

		ListGridField id = new ListGridField("id", I18N.message("id"), 50);
		id.setHidden(true);

		ListGridField username = new ListGridField("username", I18N.message("author"), 200);
		ListGridField date = new ListGridField("date", I18N.message("date"));
		date.setAlign(Alignment.LEFT);
		date.setType(ListGridFieldType.DATE);
		date.setCellFormatter(new DateCellFormatter(false));
		date.setCanFilter(false);
		date.setWidth("*");

		listGrid = new ListGrid();
		listGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		listGrid.setCanFreezeFields(true);
		listGrid.setAutoFetchData(true);
		dataSource = new DocumentNotesDS(document.getId());
		listGrid.setDataSource(dataSource);
		listGrid.setFields(id, username, date);
		listGrid.setWidth100();
		listGrid.setCanExpandRecords(true);
		listGrid.setExpansionMode(ExpansionMode.DETAIL_FIELD);
		listGrid.setDetailField("message");

		container.setHeight100();
		container.setWidth100();
		container.addMember(listGrid);

		addNote = new Button(I18N.message("addnote"));
		addNote.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EditNoteWindow note = new EditNoteWindow(document.getId(), null, NotesPanel.this, "");
				note.show();
			}
		});

		if (document.getFolder().isWrite()) {
			addNote.setAutoFit(true);
			container.addMember(addNote);
		}

		listGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = new Menu();
				MenuItem delete = new MenuItem();
				delete.setTitle(I18N.message("ddelete"));
				delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						onDelete();
					}
				});

				MenuItem edit = new MenuItem();
				edit.setTitle(I18N.message("edit"));
				edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						EditNoteWindow note = new EditNoteWindow(document.getId(), listGrid.getSelectedRecord()
								.getAttributeAsLong("id"), NotesPanel.this, listGrid.getSelectedRecord().getAttribute(
								"message"));
						note.show();
					}
				});

				MenuItem print = new MenuItem();
				print.setTitle(I18N.message("print"));
				print.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						HTMLPane printContainer = new HTMLPane();
						printContainer.setContents(listGrid.getSelectedRecord().getAttribute("message"));
						Canvas.showPrintPreview(printContainer);
					}
				});

				ListGridRecord[] selection = listGrid.getSelectedRecords();

				delete.setEnabled(document.getFolder().hasPermission(Constants.PERMISSION_DELETE));
				edit.setEnabled(document.getFolder().isWrite() && selection.length == 1);
				print.setEnabled(selection.length == 1);

				contextMenu.setItems(edit, print, delete);
				contextMenu.showContextMenu();
				event.cancel();
			}
		});

		//Expand all notes after arrived
		listGrid.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				for (ListGridRecord rec : listGrid.getRecords()) {
					listGrid.expandRecord(rec);
				}
			}
		});
	}

	private void onDelete() {
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

	public void onAdded(long id, String message) {
		init();
	}

	public void onUpdated(String message) {
		ListGridRecord record = listGrid.getSelectedRecord();
		record.setAttribute("username", Session.get().getUser().getFullName());
		record.setAttribute("date", new Date());
		record.setAttribute("message", message);
		listGrid.refreshRow(listGrid.getRecordIndex(record));
	}

	@Override
	public void destroy() {
		super.destroy();
		if (dataSource != null)
			dataSource.destroy();
	}
}