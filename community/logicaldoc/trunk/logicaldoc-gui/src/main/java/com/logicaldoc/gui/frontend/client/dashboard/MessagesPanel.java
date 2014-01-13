package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.data.MessagesDS;
import com.logicaldoc.gui.common.client.formatters.DateCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.MessageService;
import com.logicaldoc.gui.frontend.client.services.MessageServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.BooleanCallback;
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
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel shows the list of system messages and allows the selection.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MessagesPanel extends VLayout {
	private MessageServiceAsync service = (MessageServiceAsync) GWT.create(MessageService.class);

	private ListGrid grid;

	private Layout listing;

	private HTMLPane body;

	public MessagesPanel() {
		setWidth100();
		setHeight100();

		// Initialize the listing panel as placeholder
		refresh();
	}

	private void refresh() {
		if (grid != null) {
			listing.removeMember(grid);
			grid.destroy();
		}

		if (body != null) {
			removeMember(body);
			body.destroy();
		}

		listing = new VLayout();
		listing.setHeight("75%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField priority = new ListGridField("priority", I18N.message("priority"), 50);
		priority.setType(ListGridFieldType.IMAGE);
		priority.setCanSort(false);
		priority.setAlign(Alignment.CENTER);
		priority.setShowDefaultContextMenu(false);
		priority.setImageURLPrefix(Util.imagePrefix());
		priority.setImageURLSuffix(".gif");
		priority.setCanFilter(false);

		ListGridField subject = new ListGridField("subject", I18N.message("subject"));
		subject.setCanFilter(true);

		ListGridField from = new ListGridField("from", I18N.message("from"), 150);
		from.setCanFilter(true);

		ListGridField sent = new ListGridField("sent", I18N.message("sent"), 110);
		sent.setAlign(Alignment.CENTER);
		sent.setType(ListGridFieldType.DATE);
		sent.setCellFormatter(new DateCellFormatter(false));
		sent.setCanFilter(false);

		grid = new ListGrid() {
			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
				if (getFieldName(colNum).equals("subject")) {
					if ("false".equals(record.getAttributeAsString("read"))) {
						return "font-weight:bold;";
					} else {
						return super.getCellCSSText(record, rowNum, colNum);
					}
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};
		grid.setEmptyMessage(I18N.message("notitemstoshow"));
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);
		grid.setCanFreezeFields(true);
		grid.setAutoFetchData(true);
		grid.setSelectionType(SelectionStyle.MULTIPLE);
		grid.setFilterOnKeypress(true);
		grid.setShowFilterEditor(false);
		grid.setDataSource(new MessagesDS());
		grid.setFields(id, priority, subject, from, sent);
		grid.sort("sent", SortDirection.DESCENDING);

		// Count the total unread messages
		grid.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				Record[] records = grid.getRecordList().toArray();
				int unread = 0;
				for (Record record : records) {
					if ("false".equals(record.getAttributeAsString("read")))
						unread++;
				}

				Session.get().getUser().setUnreadMessages(unread);
			}
		});

		grid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				final Record record = grid.getSelectedRecord();
				if (record != null)
					service.getMessage(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")), true,
							new AsyncCallback<GUIMessage>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIMessage message) {
									record.setAttribute("read", "true");
									grid.refreshRow(grid.getRecordIndex(record));
									body.setContents(grid.getSelectedRecord().getAttributeAsString("text"));
								}
							});
			}
		});

		grid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton add = new ToolStripButton();
		add.setTitle(I18N.message("sendmessage"));
		toolStrip.addButton(add);
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MessageDialog dialog = new MessageDialog();
				dialog.show();
			}
		});
		ToolStripButton refresh = new ToolStripButton();
		refresh.setTitle(I18N.message("refresh"));
		toolStrip.addButton(refresh);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
		toolStrip.addFill();

		listing.setMembers(toolStrip, grid);

		body = new HTMLPane();
		body.setContentsType(ContentsType.PAGE);
		body.setShowEdges(true);
		setMembers(listing, body);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		ListGridRecord[] selection = grid.getSelectedRecords();
		if (selection == null || selection.length == 0)
			return;
		final long[] ids = new long[selection.length];
		for (int i = 0; i < selection.length; i++) {
			ids[i] = Long.parseLong(selection[i].getAttribute("id"));
		}

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.delete(Session.get().getSid(), ids, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									grid.removeSelectedData();
									// list.deselectAllRecords();
								}
							});
						}
					}
				});
			}
		});

		contextMenu.setItems(delete);
		contextMenu.showContextMenu();
	}
}