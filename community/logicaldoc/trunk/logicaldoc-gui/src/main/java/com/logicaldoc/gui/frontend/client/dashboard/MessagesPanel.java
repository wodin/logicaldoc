package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.data.MessagesDS;
import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
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
	private ListGrid list;

	private Layout listing = new VLayout();

	private Layout detailsContainer = new VLayout();

	final static Canvas SELECT_MESSAGE = new HTMLPanel("&nbsp;" + I18N.message("selectmessage"));

	private Canvas details = SELECT_MESSAGE;

	public MessagesPanel() {
		setWidth100();

		// Initialize the listing panel as placeholder
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("60%");
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
					if (record.getAttributeAsInt("status").intValue()==0) {
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
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setFilterOnKeypress(true);
		list.setShowFilterEditor(true);
		list.setDataSource(MessagesDS.get());
		list.setFields(id, priority, username, from, sent);

		listing.addMember(list);

		detailsContainer.setAlign(Alignment.CENTER);
		detailsContainer.addMember(details);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton add = new ToolStripButton();
		add.setTitle(I18N.message("newmessage"));
		toolStrip.addButton(add);
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// TODO visualizzare un popup per l'invio del messaggio
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
				Record record = list.getSelectedRecord();
				// TODO Implementare l'invocazione a un servizio

				// if (record != null)
				// service.getUser(Session.get().getSid(),
				// Long.parseLong(record.getAttributeAsString("id")),
				// new AsyncCallback<GUIUser>() {
				//
				// @Override
				// public void onFailure(Throwable caught) {
				// Log.serverError(caught);
				// }
				//
				// @Override
				// public void onSuccess(GUIUser user) {
				// showUserDetails(user);
				// }
				// });
			}
		});
	}

	private void showMessage(GUIUser user) {
		// TODO Implementare la visualizzazione del pannello di dettaglio
		// if (!(details instanceof UserDetailsPanel)) {
		// detailsContainer.removeMember(details);
		// details = new UserDetailsPanel(MessagesPanel.this);
		// detailsContainer.addMember(details);
		// }
		// ((UserDetailsPanel) details).setUser(user);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord record = list.getSelectedRecord();
		final long id = Long.parseLong(record.getAttributeAsString("id"));

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SC.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							// Implementare la cancellazione del messaggio
							// service.deleteUser(Session.get().getSid(), id,
							// new AsyncCallback<Void>() {
							// @Override
							// public void onFailure(Throwable caught) {
							// Log.serverError(caught);
							// }
							//
							// @Override
							// public void onSuccess(Void result) {
							// list.removeSelectedData();
							// list.deselectAllRecords();
							// showUserDetails(null);
							// }
							// });
						}
					}
				});
			}
		});

		contextMenu.setItems(delete);
		contextMenu.showContextMenu();
	}
}