package com.logicaldoc.gui.frontend.client.system;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.data.LanguagesDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
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
 * Displays a list of languages available for the GUI, allowing for
 * enable/disable single languages.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class GUILanguagesPanel extends VLayout {

	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ListGrid list;

	public GUILanguagesPanel() {
		setMembersMargin(3);

		ListGridField enabled = new ListGridField("eenabled", " ", 24);
		enabled.setType(ListGridFieldType.IMAGE);
		enabled.setCanSort(false);
		enabled.setAlign(Alignment.CENTER);
		enabled.setShowDefaultContextMenu(false);
		enabled.setImageURLPrefix(Util.imagePrefix());
		enabled.setImageURLSuffix(".gif");
		enabled.setCanFilter(false);

		ListGridField code = new ListGridField("code", I18N.message("code"), 80);
		code.setCanEdit(false);

		ListGridField name = new ListGridField("name", I18N.message("name"));
		name.setCanEdit(false);

		list = new ListGrid();
		list.setCanEdit(false);
		list.setWidth100();
		list.setHeight100();
		list.setAutoFetchData(true);
		list.setDataSource(new LanguagesDS(true));
		list.setShowFilterEditor(true);
		list.setFilterOnKeypress(true);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setFields(enabled, code, name);

		addMember(list);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});
	}

	private void showContextMenu() {
		final ListGridRecord record = list.getSelectedRecord();

		Menu contextMenu = new Menu();
		MenuItem enable = new MenuItem();
		enable.setTitle(I18N.message("enable"));
//		enable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
//			public void onClick(MenuItemClickEvent event) {
//				service.setGUILanguageStatus(Session.get().getSid(), record.getAttributeAsString("code"), true,
//						new AsyncCallback<Void>() {
//
//							@Override
//							public void onFailure(Throwable caught) {
//								Log.serverError(caught);
//							}
//
//							@Override
//							public void onSuccess(Void result) {
//								record.setAttribute("eenabled", "0");
//								langsList.updateData(record);
//							}
//						});
//			}
//		});
//
//		MenuItem disable = new MenuItem();
//		disable.setTitle(I18N.message("disable"));
//		disable.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
//			public void onClick(MenuItemClickEvent event) {
//				service.setGUILanguageStatus(Session.get().getSid(), record.getAttributeAsString("code"), false,
//						new AsyncCallback<Void>() {
//
//							@Override
//							public void onFailure(Throwable caught) {
//								Log.serverError(caught);
//							}
//
//							@Override
//							public void onSuccess(Void result) {
//								record.setAttribute("eenabled", "2");
//								langsList.updateData(record);
//							}
//						});
//			}
//		});

//		if ("0".equals(record.getAttributeAsString("eenabled")))
//			contextMenu.setItems(disable);
//		else
//			contextMenu.setItems(enable);
		contextMenu.showContextMenu();
	}
}
