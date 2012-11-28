package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIMenu;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.data.MenuesDS;
import com.logicaldoc.gui.common.client.formatters.I18NCellFormatter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.widgets.HTMLPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the list of menues visible by the current user allowing for
 * security management.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MenuesPanel extends VLayout {
	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ListGrid list;

	private Layout listing = new VLayout();

	private Layout rightsContainer = new VLayout();

	final static Canvas SELECT_MENU = new HTMLPanel("&nbsp;" + I18N.message("selectmenu"));

	private Canvas rights = SELECT_MENU;

	public MenuesPanel() {
		setWidth100();

		// Initialize the listing panel as placeholder
		listing.setAlign(Alignment.CENTER);
		listing.setHeight("55%");
		listing.setShowResizeBar(true);

		ListGridField id = new ListGridField("id", 40);
		id.setHidden(true);

		ListGridField name = new ListGridField("name", I18N.message("name"), 350);
		name.setCanFilter(true);
		name.setCellFormatter(new I18NCellFormatter());

		list = new ListGrid();
		list.setEmptyMessage(I18N.message("notitemstoshow"));
		list.setShowRecordComponents(true);
		list.setShowRecordComponentsByCell(true);
		list.setCanFreezeFields(true);
		list.setAutoFetchData(true);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setFilterOnKeypress(true);
		list.setDataSource(new MenuesDS());
		list.setFields(id, name);

		listing.addMember(list);

		rightsContainer.setAlign(Alignment.CENTER);
		rightsContainer.addMember(rights);

		setMembers(listing, rightsContainer);

		list.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Record record = list.getSelectedRecord();
				if (record != null)
					service.getMenu(Session.get().getSid(), Long.parseLong(record.getAttributeAsString("id")),
							new AsyncCallback<GUIMenu>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(GUIMenu menu) {
									showRights(menu);
								}
							});
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

	public void showRights(GUIMenu menu) {
		rightsContainer.removeMember(rights);
		rights = new MenuRightsPanel(menu);
		rightsContainer.addMember(rights);
	}
}