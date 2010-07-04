package com.logicaldoc.gui.frontend.client.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.data.MessagesDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.MessageService;
import com.logicaldoc.gui.frontend.client.services.MessageServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
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

	private ListGrid list;

	private Layout listing = new VLayout();

	private Layout detailsContainer = new VLayout();

	private HTMLFlow messageText = new HTMLFlow();

	public MessagesPanel() {
		setWidth100();

		// Initialize the listing panel as placeholder
		listing.setAlign(Alignment.CENTER);
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

		ListGridField username = new ListGridField("subject", I18N.message("subject"), 250);
		username.setCanFilter(true);

		ListGridField from = new ListGridField("from", I18N.message("from"), 150);
		from.setCanFilter(true);

		ListGridField sent = new ListGridField("sent", I18N.message("sent"), 90);
		sent.setCanFilter(false);

		list = new ListGrid() {
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
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		list.setFilterOnKeypress(true);
		list.setShowFilterEditor(true);
		list.setDataSource(MessagesDS.get());
		list.setFields(id, priority, username, from, sent);

		listing.addMember(list);

		detailsContainer.setAlign(Alignment.CENTER);
		detailsContainer.addMember(messageText);

		messageText.setContents(I18N.message("selectmessage"));

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
		toolStrip.addFill();

		setMembers(toolStrip, listing, detailsContainer);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		list.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				final Record record = list.getSelectedRecord();
				if (record != null)
					service.getMessage(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")), true,
							new AsyncCallback<GUIMessage>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIMessage message) {
									messageText.setContents(message.getMessage());
									record.setAttribute("read", "true");
									list.updateData(record);
								}
							});
			}
		});
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		ListGridRecord[] selection = list.getSelection();
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
				SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
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
									list.removeSelectedData();
									list.deselectAllRecords();
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