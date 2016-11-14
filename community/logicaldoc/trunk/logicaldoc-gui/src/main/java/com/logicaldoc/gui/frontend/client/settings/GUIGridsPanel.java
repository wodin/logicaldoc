package com.logicaldoc.gui.frontend.client.settings;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridComponent;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellContextClickEvent;
import com.smartgwt.client.widgets.grid.events.CellContextClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Displays the settings for the documents grids.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5.4
 */
public class GUIGridsPanel extends VLayout {

	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ListGrid documentsGrid;

	private ListGrid searchGrid;

	private HLayout body = new HLayout();

	public GUIGridsPanel() {
		setMembersMargin(3);
		body.setMembersMargin(3);
		body.setWidth100();
		body.setHeight100();
		setMembers(body);

		ToolStrip toolbar = new ToolStrip();
		toolbar.setWidth100();
		ToolStripButton save = new ToolStripButton(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		toolbar.addButton(save);

		SectionStack documentsStack = prepareDocumentsGrid();
		SectionStack searchStack = prepareSearchGrid();
		body.setMembers(documentsStack, searchStack);

		setMembers(toolbar, body);
	}

	private SectionStack prepareDocumentsGrid() {
		ListGridField attribute = new ListGridField("label", I18N.message("attribute"));
		attribute.setCanEdit(false);

		documentsGrid = new ListGrid();
		documentsGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		documentsGrid.setCanEdit(false);
		documentsGrid.setWidth100();
		documentsGrid.setHeight100();
		documentsGrid.setSelectionType(SelectionStyle.MULTIPLE);
		documentsGrid.setCanReorderRecords(true);
		documentsGrid.setShowRowNumbers(true);
		documentsGrid.setFields(attribute);

		documentsGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = new Menu();
				MenuItem delete = new MenuItem();
				delete.setTitle(I18N.message("delete"));
				delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						documentsGrid.removeSelectedData();
					}
				});
				contextMenu.setItems(delete);
				contextMenu.showContextMenu();
				event.cancel();
			}
		});

		ToolStrip controls = new ToolStrip();
		controls.setWidth100();
		controls.setHeight(24);
		final SelectItem selector = ItemFactory.newAttributesSelector();
		selector.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				ListGridRecord[] seletion = selector.getSelectedRecords();
				for (ListGridRecord sel : seletion) {
					// Skip search-specific attributes
					if (sel.getAttributeAsString("name").equals("folder")
							|| sel.getAttributeAsString("name").equals("score"))
						continue;

					Record record = documentsGrid.getRecordList().find("name", sel.getAttributeAsString("name"));
					if (record == null) {
						ListGridRecord newRec = new ListGridRecord();
						newRec.setAttribute("name", sel.getAttributeAsString("name"));
						newRec.setAttribute("label", sel.getAttributeAsString("label"));
						documentsGrid.addData(newRec);
					}
				}
				selector.clearValue();
			}

		});
		controls.addFormItem(selector);

		documentsGrid.setGridComponents(new Object[] { ListGridComponent.HEADER, ListGridComponent.BODY, controls });

		String columns = Session.get().getConfig("gui.document.columns");

		if (columns != null) {
			String[] attributes = columns.split("\\,");
			for (String att : attributes) {
				ListGridRecord record = new ListGridRecord();
				String n = att.trim();
				record.setAttribute("name", n);
				record.setAttribute("label", Session.get().getInfo().getAttributeLabel(n));
				documentsGrid.addData(record);
			}
		}

		SectionStack stack = new SectionStack();
		stack.setWidth(300);
		stack.setHeight(500);

		SectionStackSection section = new SectionStackSection(I18N.message("columnsindocuments"));
		section.setCanCollapse(false);
		section.setExpanded(true);

		section.setItems(documentsGrid);
		stack.setSections(section);

		return stack;
	}

	private SectionStack prepareSearchGrid() {
		ListGridField attribute = new ListGridField("label", I18N.message("attribute"));
		attribute.setCanEdit(false);

		searchGrid = new ListGrid();
		searchGrid.setEmptyMessage(I18N.message("notitemstoshow"));
		searchGrid.setCanEdit(false);
		searchGrid.setWidth100();
		searchGrid.setHeight100();
		searchGrid.setSelectionType(SelectionStyle.MULTIPLE);
		searchGrid.setCanReorderRecords(true);
		searchGrid.setShowRowNumbers(true);
		searchGrid.setFields(attribute);

		searchGrid.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				Menu contextMenu = new Menu();
				MenuItem delete = new MenuItem();
				delete.setTitle(I18N.message("delete"));
				delete.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
					public void onClick(MenuItemClickEvent event) {
						searchGrid.removeSelectedData();
					}
				});
				contextMenu.setItems(delete);
				contextMenu.showContextMenu();
				event.cancel();
			}
		});

		ToolStrip controls = new ToolStrip();
		controls.setWidth100();
		controls.setHeight(24);
		final SelectItem selector = ItemFactory.newAttributesSelector();
		selector.addChangedHandler(new ChangedHandler() {

			@Override
			public void onChanged(ChangedEvent event) {
				ListGridRecord[] seletion = selector.getSelectedRecords();
				for (ListGridRecord sel : seletion) {
					Record record = searchGrid.getRecordList().find("name", sel.getAttributeAsString("name"));
					if (record == null) {
						ListGridRecord newRec = new ListGridRecord();
						newRec.setAttribute("name", sel.getAttributeAsString("name"));
						newRec.setAttribute("label", sel.getAttributeAsString("label"));
						searchGrid.addData(newRec);
					}
				}
				selector.clearValue();
			}

		});
		controls.addFormItem(selector);

		searchGrid.setGridComponents(new Object[] { ListGridComponent.HEADER, ListGridComponent.BODY, controls });

		String columns = Session.get().getConfig("gui.search.columns");

		if (columns != null) {
			String[] attributes = columns.split("\\,");
			for (String att : attributes) {
				ListGridRecord record = new ListGridRecord();
				String n = att.trim();
				record.setAttribute("name", n);
				record.setAttribute("label", Session.get().getInfo().getAttributeLabel(n));
				searchGrid.addData(record);
			}
		}

		SectionStack stack = new SectionStack();
		stack.setWidth(300);
		stack.setHeight(500);

		SectionStackSection section = new SectionStackSection(I18N.message("columnsinsearch"));
		section.setCanCollapse(false);
		section.setExpanded(true);

		section.setItems(searchGrid);
		stack.setSections(section);

		return stack;
	}

	private void onSave() {
		List<String> extendedAttributes = new ArrayList<String>();

		/*
		 * Prepare the list of columns for the documents screen
		 */
		StringBuffer value = new StringBuffer();
		ListGridRecord[] attrs = documentsGrid.getRecords();
		for (ListGridRecord att : attrs) {
			if (value.length() > 0)
				value.append(",");
			String name = att.getAttributeAsString("name").trim();
			value.append(name);

			// Meanwhile collect the extended attributes
			if (name.startsWith("ext_")) {
				String n = name.substring(4);
				if (!extendedAttributes.contains(n))
					extendedAttributes.add(n);
			}
		}
		List<GUIParameter> parameters = new ArrayList<GUIParameter>();
		GUIParameter param = new GUIParameter(Session.get().getTenantName() + ".gui.document.columns", value.toString());
		parameters.add(param);
		Session.get().setConfig(param.getName(), param.getValue());

		/*
		 * Prepare the list of columns for the search screen
		 */
		value = new StringBuffer();
		attrs = searchGrid.getRecords();
		for (ListGridRecord att : attrs) {
			if (value.length() > 0)
				value.append(",");
			String name = att.getAttributeAsString("name").trim();
			value.append(name);

			// Meanwhile collect the extended attributes
			if (name.startsWith("ext_")) {
				String n = name.substring(4);
				if (!extendedAttributes.contains(n))
					extendedAttributes.add(n);
			}
		}
		param = new GUIParameter(Session.get().getTenantName() + ".gui.search.columns", value.toString());
		parameters.add(param);
		Session.get().setConfig(param.getName(), param.getValue());

		/*
		 * Now taking care of define what extended attributes have to be
		 * retrieved when searching for the documents
		 */
		value = new StringBuffer();
		for (String att : extendedAttributes) {
			if (value.length() > 0)
				value.append(",");
			value.append(att);
		}
		param = new GUIParameter(Session.get().getTenantName() + ".search.extattr", value.toString());
		parameters.add(param);
		Session.get().setConfig(param.getName(), param.getValue());

		// Save all
		service.saveSettings(parameters.toArray(new GUIParameter[0]), new AsyncCallback<Void>() {

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
}