package com.logicaldoc.gui.frontend.client.settings;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICustomId;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.CustomIdService;
import com.logicaldoc.gui.frontend.client.services.CustomIdServiceAsync;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Shows the set of filters associated to the curent account
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class CustomIdPanel extends VLayout {
	private CustomIdServiceAsync service = (CustomIdServiceAsync) GWT.create(CustomIdService.class);

	private ListGrid list;

	public CustomIdPanel(GUICustomId[] data) {
		setWidth100();
		setHeight100();
		setMembersMargin(5);

		TabSet tabs = new TabSet();
		Tab tab = new Tab();
		tab.setTitle(I18N.message("customid"));
		tabs.setTabs(tab);
		addMember(tabs);

		ListGridField template = new ListGridField("templateName", I18N.message("template"));
		template.setWidth(120);
		template.setCanEdit(false);

		ListGridField scheme = new ListGridField("scheme", I18N.message("scheme"));
		scheme.setWidth(200);
		scheme.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				return Util.strip(record.getAttributeAsString("scheme"));
			}
		});

		final ListGridField regenerate = new ListGridField("regenerate", I18N.message("regenerateaftercheckin"));
		regenerate.setWidth(150);
		regenerate.setType(ListGridFieldType.BOOLEAN);

		list = new ListGrid();
		list.setShowAllRecords(true);
		list.setCanEdit(true);
		list.setWidth100();
		list.setHeight100();
		list.setFields(template);
		list.setSelectionType(SelectionStyle.SINGLE);
		list.setModalEditing(true);

		List<ListGridRecord> records = new ArrayList<ListGridRecord>();
		if (data != null)
			for (GUICustomId cid : data) {
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("templateId", Long.toString(cid.getTemplateId()));
				record.setAttribute("templateName", Util.strip(cid.getTemplateName()));
				if (cid.getScheme() != null)
					record.setAttribute("scheme", cid.getScheme());
				record.setAttribute("regenerate", cid.isRegenerate());
				records.add(record);
			}
		list.setData(records.toArray(new ListGridRecord[0]));

		list.setFields(template, scheme, regenerate);

		list.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		list.addEditCompleteHandler(new EditCompleteHandler() {
			@Override
			public void onEditComplete(EditCompleteEvent event) {
				GUICustomId cid = new GUICustomId();
				ListGridRecord record = list.getRecord(event.getRowNum());
				cid.setTemplateId(Long.parseLong(record.getAttribute("templateId")));
				cid.setRegenerate(record.getAttributeAsBoolean("regenerate"));
				cid.setScheme(record.getAttributeAsString("regenerate"));
				service.save(Session.get().getSid(), cid, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void ret) {
					}
				});
			}
		});

		tab.setPane(list);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem reset = new MenuItem();
		reset.setTitle(I18N.message("resetnumeration"));
		reset.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SC.ask(I18N.message("question"), I18N.message("confirmreset"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							ListGridRecord record = list.getSelectedRecord();
							service.reset(Session.get().getSid(),
									Long.parseLong(record.getAttributeAsString("templateId")),
									new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void ret) {
										}
									});
						}
					}
				});
			}
		});

		MenuItem clean = new MenuItem();
		clean.setTitle(I18N.message("clean"));
		clean.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				SC.ask(I18N.message("question"), I18N.message("confirmclean"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							final ListGridRecord record = list.getSelectedRecord();
							service.delete(Session.get().getSid(),
									Long.parseLong(record.getAttributeAsString("templateId")),
									new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void ret) {
											list.getSelectedRecord().setAttribute("scheme", (String) null);
											list.getSelectedRecord().setAttribute("regenerate", false);
											list.updateData(record);
											list.refreshRow(list.getRecordIndex(record));
										}
									});
						}
					}
				});
			}
		});

		contextMenu.setItems(clean, reset);
		contextMenu.showContextMenu();
	}
}