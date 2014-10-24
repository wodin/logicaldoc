package com.logicaldoc.gui.frontend.client.metadata;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUICustomId;
import com.logicaldoc.gui.common.client.beans.GUISequence;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.CustomIdService;
import com.logicaldoc.gui.frontend.client.services.CustomIdServiceAsync;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.widgets.HTMLFlow;
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

	private ListGrid schemes;

	private ListGrid sequences;

	public CustomIdPanel(GUICustomId[] schemesData, GUISequence[] sequencesData) {
		setWidth100();
		setHeight100();
		setMembersMargin(5);

		TabSet tabs = new TabSet();

		Tab schemesTab = new Tab();
		schemesTab.setTitle(I18N.message("customid"));
		Tab sequencesTab = new Tab();
		sequencesTab.setTitle(I18N.message("sequences"));

		setupSchemesPanel(schemesData);
		setupSequencesPanel(sequencesData);

		VLayout sc = new VLayout();
		HTMLFlow hint = new HTMLFlow(I18N.message("customidhint"));
		hint.setMargin(3);
		sc.addMember(hint);
		sc.addMember(schemes);

		schemesTab.setPane(sc);
		sequencesTab.setPane(sequences);

		tabs.setTabs(schemesTab, sequencesTab);
		addMember(tabs);
	}

	private void setupSchemesPanel(GUICustomId[] data) {
		ListGridField template = new ListGridField("templateName", I18N.message("template"));
		template.setWidth(120);
		template.setCanEdit(false);

		ListGridField scheme = new ListGridField("scheme", I18N.message("scheme"));
		scheme.setWidth(200);
		scheme.setRequired(true);
		scheme.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				return Util.strip(record.getAttributeAsString("scheme"));
			}
		});

		final ListGridField regenerate = new ListGridField("regenerate", I18N.message("regenerateaftercheckin"));
		regenerate.setWidth(150);
		regenerate.setType(ListGridFieldType.BOOLEAN);

		schemes = new ListGrid();
		schemes.setEmptyMessage(I18N.message("notitemstoshow"));
		schemes.setShowAllRecords(true);
		schemes.setCanEdit(true);
		schemes.setWidth100();
		schemes.setHeight100();
		schemes.setFields(template);
		schemes.setSelectionType(SelectionStyle.SINGLE);
		schemes.setModalEditing(true);

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
		schemes.setData(records.toArray(new ListGridRecord[0]));

		schemes.setFields(template, scheme, regenerate);

		schemes.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showCustomIdContextMenu();
				event.cancel();
			}
		});

		schemes.addEditCompleteHandler(new EditCompleteHandler() {
			@Override
			public void onEditComplete(EditCompleteEvent event) {
				GUICustomId cid = new GUICustomId();
				ListGridRecord record = schemes.getRecord(event.getRowNum());
				cid.setTemplateId(Long.parseLong(record.getAttribute("templateId")));
				cid.setRegenerate(record.getAttributeAsBoolean("regenerate"));
				cid.setScheme(record.getAttributeAsString("scheme"));

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
	}

	private void setupSequencesPanel(GUISequence[] data) {
		ListGridField id = new ListGridField("id", I18N.message("id"));
		id.setWidth(60);
		id.setCanEdit(false);
		id.setHidden(true);

		ListGridField frequency = new ListGridField("frequency", I18N.message("frequency"));
		frequency.setWidth(80);
		frequency.setCanEdit(false);

		ListGridField template = new ListGridField("template", I18N.message("template"));
		template.setWidth(200);
		template.setCanEdit(false);

		ListGridField folder = new ListGridField("folder", I18N.message("folder"));
		folder.setWidth(200);
		folder.setCanEdit(false);

		final ListGridField value = new ListGridField("value", I18N.message("value"));
		value.setWidth(80);
		value.setType(ListGridFieldType.INTEGER);
		value.setRequired(true);

		sequences = new ListGrid();
		sequences.setShowAllRecords(true);
		sequences.setCanEdit(true);
		sequences.setWidth100();
		sequences.setHeight100();
		sequences.setFields(template);
		sequences.setSelectionType(SelectionStyle.SINGLE);
		sequences.setModalEditing(true);

		List<ListGridRecord> records = new ArrayList<ListGridRecord>();
		if (data != null)
			for (GUISequence cid : data) {
				ListGridRecord record = new ListGridRecord();
				record.setAttribute("template", Util.strip(cid.getTemplate()));
				record.setAttribute("frequency", I18N.message(cid.getFrequency()));
				record.setAttribute("year", cid.getYear());
				record.setAttribute("month", cid.getMonth());
				record.setAttribute("folder", cid.getFolder());
				record.setAttribute("id", cid.getId());
				record.setAttribute("value", cid.getValue());
				records.add(record);
			}
		sequences.setData(records.toArray(new ListGridRecord[0]));

		sequences.setFields(id, frequency, template, folder, value);

		sequences.addEditCompleteHandler(new EditCompleteHandler() {
			@Override
			public void onEditComplete(EditCompleteEvent event) {
				ListGridRecord record = sequences.getRecord(event.getRowNum());
				service.resetSequence(Session.get().getSid(), Long.parseLong(record.getAttribute("id")),
						(Integer) record.getAttributeAsInt("value"), new AsyncCallback<Void>() {
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

		sequences.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showSequencesContextMenu();
				event.cancel();
			}
		});
	}

	private void showCustomIdContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem clean = new MenuItem();
		clean.setTitle(I18N.message("clean"));
		clean.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmclean"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							final ListGridRecord record = schemes.getSelectedRecord();
							service.delete(Session.get().getSid(),
									Long.parseLong(record.getAttributeAsString("templateId")),
									new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											Log.serverError(caught);
										}

										@Override
										public void onSuccess(Void ret) {
											schemes.getSelectedRecord().setAttribute("scheme", (String) null);
											schemes.getSelectedRecord().setAttribute("regenerate", false);
											schemes.refreshRow(schemes.getRecordIndex(record));
											schemes.refreshRow(schemes.getRecordIndex(record));
										}
									});
						}
					}
				});
			}
		});

		contextMenu.setItems(clean);
		contextMenu.showContextMenu();
	}

	private void showSequencesContextMenu() {
		Menu contextMenu = new Menu();

		final ListGridRecord record = sequences.getSelectedRecord();
		final long id = Long.parseLong(record.getAttributeAsString("id"));

		MenuItem delete = new MenuItem();
		delete.setTitle(I18N.message("ddelete"));
		delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				LD.ask(I18N.message("question"), I18N.message("confirmdelete"), new BooleanCallback() {
					@Override
					public void execute(Boolean value) {
						if (value) {
							service.deleteSequence(Session.get().getSid(), id, new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									sequences.removeSelectedData();
									sequences.deselectAllRecords();
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