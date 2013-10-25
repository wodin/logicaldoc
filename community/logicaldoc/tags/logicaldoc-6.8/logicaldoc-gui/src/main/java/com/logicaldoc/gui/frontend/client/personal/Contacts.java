package com.logicaldoc.gui.frontend.client.personal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIContact;
import com.logicaldoc.gui.common.client.data.ContactsDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.frontend.client.services.ContactService;
import com.logicaldoc.gui.frontend.client.services.ContactServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DoubleClickEvent;
import com.smartgwt.client.widgets.events.DoubleClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * This panel shows the list of the user's contacts.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Contacts extends com.smartgwt.client.widgets.Window {
	private ContactServiceAsync service = (ContactServiceAsync) GWT.create(ContactService.class);

	private ListGrid list;

	private Layout listing = new VLayout();

	public Contacts() {
		super();

		setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
		setTitle(I18N.message("contacts"));
		setWidth(500);
		setHeight(400);
		setIsModal(true);
		setShowModalMask(true);
		setCanDragResize(true);
		centerInPage();
		setMembersMargin(5);
		setAutoSize(true);

		// Initialize the listing panel as placeholder
		listing.setAlign(Alignment.CENTER);
		listing.setHeight100();
		listing.setWidth100();
		refresh();

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);

		ToolStripButton refresh = new ToolStripButton();
		refresh.setTitle(I18N.message("refresh"));
		toolStrip.addButton(refresh);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

		ToolStripButton add = new ToolStripButton();
		add.setTitle(I18N.message("addcontact"));
		toolStrip.addButton(add);
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ContactDetails details = new ContactDetails(new GUIContact(), Contacts.this);
				details.show();
			}
		});
		toolStrip.addFill();

		VStack content = new VStack();
		content.setWidth100();
		content.setHeight100();
		content.setMembersMargin(5);
		content.setTop(20);
		content.setMargin(4);
		content.setAlign(Alignment.CENTER);
		content.setDefaultLayoutAlign(Alignment.CENTER);
		content.setBackgroundColor("#ffffff");
		content.setMembers(toolStrip, listing);

		addChild(content);
	}

	public void refresh() {
		if (list != null) {
			listing.removeMember(list);
			list.destroy();
		}

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField email = new ListGridField("email", I18N.message("email"));
		email.setWidth("*");
		email.setCanFilter(true);

		ListGridField firstName = new ListGridField("firstName", I18N.message("firstname"));
		firstName.setCanFilter(true);
		firstName.setWidth(80);

		ListGridField lastName = new ListGridField("lastName", I18N.message("lastname"));
		firstName.setCanFilter(true);
		lastName.setWidth(80);

		ListGridField company = new ListGridField("company", I18N.message("company"));
		company.setCanFilter(true);
		company.setWidth(110);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.MULTIPLE);
		list.setFilterOnKeypress(true);
		list.setShowFilterEditor(true);
		list.setDataSource(new ContactsDS());
		list.setFields(id, email, firstName, lastName, company);
		list.sort("email", SortDirection.ASCENDING);

		listing.addMember(list);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		list.addDoubleClickHandler(new DoubleClickHandler() {
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				onEdit();
			}
		});
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord[] selection = list.getSelectedRecords();
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
									list.removeSelectedData();
									list.deselectAllRecords();
								}
							});
						}
					}
				});
			}
		});

		MenuItem edit = new MenuItem();
		edit.setTitle(I18N.message("edit"));
		edit.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				onEdit();
			}
		});

		contextMenu.setItems(edit, delete);
		contextMenu.showContextMenu();
	}

	private void onEdit() {
		final ListGridRecord[] selection = list.getSelectedRecords();
		service.load(Session.get().getSid(), Long.parseLong(selection[0].getAttribute("id")),
				new AsyncCallback<GUIContact>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIContact result) {
						if (result != null) {
							ContactDetails dialog = new ContactDetails(result, Contacts.this);
							dialog.show();
						}
					}
				});
	}
}