package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.data.UsersDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.logicaldoc.gui.common.client.widgets.InfoPanel;
import com.logicaldoc.gui.frontend.client.personal.MySignature;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.Canvas;
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
 * This panel shows the list of users and a detail area.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UsersPanel extends VLayout {
	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ListGrid list;

	private InfoPanel infoPanel;

	private Layout listing = new VLayout();

	private Layout detailsContainer = new VLayout();

	final static Canvas SELECT_USER = new HTMLPanel("&nbsp;" + I18N.message("selectuser"));

	private Canvas details = SELECT_USER;

	public UsersPanel() {
		setWidth100();

		infoPanel = new InfoPanel("");

		// Initialize the listing panel as placeholder
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("55%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 50);
		id.setHidden(true);

		ListGridField username = new ListGridField("username", I18N.message("username"), 100);
		username.setCanFilter(true);

		ListGridField name = new ListGridField("name", I18N.message("lastname"), 100);
		name.setCanFilter(true);

		ListGridField firstName = new ListGridField("firstName", I18N.message("firstname"), 100);
		firstName.setCanFilter(true);

		ListGridField phone = new ListGridField("phone", I18N.message("phone"), 90);
		phone.setCanFilter(true);

		ListGridField cell = new ListGridField("cell", I18N.message("cell"), 90);
		cell.setCanFilter(true);

		ListGridField email = new ListGridField("email", I18N.message("email"), 200);
		email.setCanFilter(true);

		ListGridField enabled = new ListGridField("eenabled", " ", 24);
		enabled.setType(ListGridFieldType.IMAGE);
		enabled.setCanSort(false);
		enabled.setAlign(Alignment.CENTER);
		enabled.setShowDefaultContextMenu(false);
		enabled.setImageURLPrefix(Util.imagePrefix());
		enabled.setImageURLSuffix(".gif");
		enabled.setCanFilter(false);

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setFilterOnKeypress(true);
		list.setShowFilterEditor(true);
		list.setDataSource(new UsersDS(null, true));
		list.setFields(id, enabled, username, firstName, name, email, phone, cell);

		listing.addMember(infoPanel);
		listing.addMember(list);

		detailsContainer.setAlign(Alignment.CENTER);
		detailsContainer.addMember(details);

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton add = new ToolStripButton();
		add.setTitle(I18N.message("adduser"));
		toolStrip.addButton(add);
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				list.deselectAllRecords();
				showUserDetails(new GUIUser());
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
				if (record != null)
					service.getUser(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
							new AsyncCallback<GUIUser>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIUser user) {
									showUserDetails(user);
								}
							});
			}
		});

		list.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				infoPanel.setMessage(I18N.message("showusers", Integer.toString(list.getTotalRows())));
			}
		});
	}

	/**
	 * Updates the selected record with new data
	 */
	public void updateRecord(GUIUser user) {
		ListGridRecord record = list.getSelectedRecord();
		if (record == null)
			record = new ListGridRecord();

		record.setAttribute("username", user.getUserName());
		record.setAttribute("name", user.getName());
		record.setAttribute("firstName", user.getFirstName());
		record.setAttribute("email", user.getEmail());
		record.setAttribute("cell", user.getCell());
		record.setAttribute("phone", user.getPhone());
		if (user.isEnabled())
			record.setAttribute("eenabled", "0");
		else
			record.setAttribute("eenabled", "2");

		if (record.getAttributeAsString("id") != null
				&& (user.getId() == Long.parseLong(record.getAttributeAsString("id")))) {
			list.refreshRow(list.getRecordIndex(record));
		} else {
			// Append a new record
			record.setAttribute("id", user.getId());
			list.addData(record);
			list.selectRecord(record);
		}
	}

	public void showUserDetails(GUIUser user) {
		if (!(details instanceof UserDetailsPanel)) {
			detailsContainer.removeMember(details);
			details = new UserDetailsPanel(UsersPanel.this);
			detailsContainer.addMember(details);
		}
		((UserDetailsPanel) details).setUser(user);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord record = list.getSelectedRecord();
		final long id = Long.parseLong(record.getAttributeAsString("id"));

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.deleteUser(Session.get().getSid(), id, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									list.removeSelectedData();
									list.deselectAllRecords();
									details = SELECT_USER;
									detailsContainer.setMembers(details);
								}
							});
						}
					}
				});
			}
		});

		MenuItem password = new MenuItem();
		password.setTitle(I18N.message("changepassword"));
		password.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SetPassword dialog = new SetPassword(Long.parseLong(record.getAttributeAsString("id")));
				dialog.show();
			}
		});
		password.setEnabled(!Session.get().isDemo());

		MenuItem signature = new MenuItem(I18N.message("signature"));
		signature.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				service.getUser(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
						new AsyncCallback<GUIUser>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIUser user) {
								MySignature mysign = new MySignature(user, true);
								mysign.show();
							}
						});
			}
		});

		contextMenu.setItems(password, delete);
		if (Feature.visible(Feature.DIGITAL_SIGN)) {
			contextMenu.addItem(signature);
			if (!Feature.enabled(Feature.DIGITAL_SIGN))
				signature.setEnabled(false);
			else
				signature.setEnabled(Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN));
		}

		if ("admin".equals(record.getAttributeAsString("username"))) {
			delete.setEnabled(false);
			if (!Session.get().getUser().getUserName().equalsIgnoreCase("admin")) {
				password.setEnabled(false);
				signature.setEnabled(false);
			}
		}

		contextMenu.showContextMenu();
	}
}