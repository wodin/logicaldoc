package com.logicaldoc.gui.frontend.client.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIMessageTemplate;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.MessageService;
import com.logicaldoc.gui.frontend.client.services.MessageServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Shows the patterns for generated messages.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.5
 */
public class MessageTemplatesPanel extends VLayout {
	private MessageServiceAsync service = (MessageServiceAsync) GWT.create(MessageService.class);

	private ListGrid templatesGrid;

	private SelectItem langSelector;

	public MessageTemplatesPanel() {
		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);

		HTMLFlow hint = new HTMLFlow(I18N.message("messagetemplatehint"));
		hint.setMargin(3);

		langSelector = ItemFactory.newLanguageSelector("language", false, true);
		langSelector.setWrapTitle(false);
		langSelector.setMultiple(false);
		langSelector.setEndRow(false);
		langSelector.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				reload();
			}
		});

		ToolStripButton save = new ToolStripButton();
		save.setAutoFit(true);
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				saveTemplates();
			}
		});

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.addFormItem(langSelector);
		toolStrip.addButton(save);
		toolStrip.addFill();
		toolStrip.setWidth100();

		setMembers(hint, toolStrip);
	}

	/**
	 * Sends the patterns in the grid to the server to save them.
	 */
	private void saveTemplates() {
		String lang = langSelector.getValueAsString();
		if (lang == null || lang.isEmpty())
			return;

		Record[] records = templatesGrid.getRecords();
		GUIMessageTemplate[] templates = new GUIMessageTemplate[records.length];
		int i = 0;
		for (Record record : records) {
			GUIMessageTemplate t = new GUIMessageTemplate();
			t.setId(new Long(record.getAttributeAsString("id")));
			t.setLanguage(lang);
			t.setName(record.getAttributeAsString("name"));
			t.setSubject(record.getAttributeAsString("subject"));
			t.setBody(record.getAttributeAsString("body"));

			templates[i++] = t;
		}

		service.saveTemplates(Session.get().getSid(), templates, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void arg0) {
				Log.info(I18N.message("settingssaved"), null);
			}
		});
	}

	private void reloadTemplates(GUIMessageTemplate[] templates) {
		if (templatesGrid != null)
			removeMember(templatesGrid);

		ListGridField name = new ListGridField("name", I18N.message("name"));
		name.setWidth(80);
		name.setRequired(true);
		name.setCanEdit(false);

		ListGridField subject = new ListGridField("subject", I18N.message("subject"));
		subject.setWidth(160);
		subject.setRequired(false);

		ListGridField body = new ListGridField("body", I18N.message("body"));
		body.setWidth("*");
		body.setRequired(false);
		body.setEditorType(new TextAreaItem());

		templatesGrid = new ListGrid();
		templatesGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		templatesGrid.setShowAllRecords(true);
		templatesGrid.setCanEdit(true);
		templatesGrid.setWidth100();
		templatesGrid.setHeight100();
		templatesGrid.setFields(name);
		templatesGrid.setSelectionType(SelectionStyle.MULTIPLE);
		templatesGrid.setModalEditing(true);
		templatesGrid.setFields(name, subject, body);
		templatesGrid.setCanReorderRecords(true);
		templatesGrid.setCanDragRecordsOut(true);
		templatesGrid.setCanAcceptDroppedRecords(true);
		templatesGrid.setDragDataAction(DragDataAction.MOVE);

		templatesGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		removeMember(templatesGrid);
		ListGridRecord[] records = new ListGridRecord[templates.length];
		int i = 0;
		for (GUIMessageTemplate pat : templates) {
			ListGridRecord record = new ListGridRecord();
			record.setAttribute("id", pat.getId());
			record.setAttribute("name", pat.getName());
			record.setAttribute("language", pat.getName());
			record.setAttribute("subject", pat.getSubject());
			record.setAttribute("body", pat.getBody());
			records[i++] = record;
		}
		templatesGrid.setData(records);

		addMember(templatesGrid);
	}

	private void showContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem clean = new MenuItem();
		clean.setTitle(I18N.message("copyfromdefault"));
		clean.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] records = templatesGrid.getSelectedRecords();
				long[] ids = new long[records.length];
				for (int i = 0; i < records.length; i++) {
					// Avoid deletion of default templates
					if (!"en".equals(records[i].getAttributeAsString("language")))
						ids[i] = Long.parseLong(records[i].getAttributeAsString("id"));
				}

				service.deleteTemplates(Session.get().getSid(), ids, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void arg0) {
						reload();
					}
				});
			}
		});

		clean.setEnabled(!"en".equals(langSelector.getValueAsString()));

		contextMenu.setItems(clean);
		contextMenu.showContextMenu();
	}

	private void reload() {
		String lang = langSelector.getValueAsString();

		service.loadTemplates(Session.get().getSid(), lang, new AsyncCallback<GUIMessageTemplate[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIMessageTemplate[] templates) {
				reloadTemplates(templates);
			}
		});
	}
}