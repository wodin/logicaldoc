package com.logicaldoc.gui.frontend.client.settings;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.data.StoragesDS;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
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
 * This class shows the stores list and informations.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class StoresPanel extends VLayout {

	public static final int OPERATION_NONE = 0;

	public static final int OPERATION_ADD = 1;

	public static final int OPERATION_CUMPUTESIZE = 2;

	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ListGrid stores;

	private ToolStrip toolStrip;

	private FormItem compression;

	public StoresPanel() {
		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);

		toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);

		ToolStripButton add = new ToolStripButton();
		add.setTitle(I18N.message("addstore"));
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// onAddingTemplate();
			}
		});

		ToolStripButton refresh = new ToolStripButton();
		refresh.setTitle(I18N.message("refresh"));
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

		ToolStripButton save = new ToolStripButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSave();
			}
		});
		save.setDisabled(Session.get().isDemo() && Session.get().getUser().getId() == 1);

		compression = ItemFactory.newBooleanSelector("compression", "compression");

		if (!Feature.enabled(Feature.MULTI_STORAGE)) {
			add.setDisabled(true);
			add.setTooltip(I18N.message("featuredisabled"));
		}

		toolStrip.addFormItem(compression);
		toolStrip.addSeparator();

		if (Feature.visible(Feature.MULTI_STORAGE)) {
			toolStrip.addButton(add);
			toolStrip.addSeparator();
		}

		toolStrip.addButton(save);
		toolStrip.addSeparator();
		toolStrip.addButton(refresh);
		toolStrip.addFill();

		addMember(toolStrip);
		refresh();
	}

	private void refresh() {
		if (stores != null)
			removeMember(stores);

		compression.setValue("true".equals(Session.get().getConfig("store.compress")) ? "yes" : "no");

		stores = new ListGrid();
		stores.setEmptyMessage(I18N.message("notitemstoshow"));
		stores.setEmptyMessage(I18N.message("norecords"));
		stores.setSelectionType(SelectionStyle.SINGLE);
		ListGridField id = new ListGridField("id", " ", 20);
		ListGridField name = new ListGridField("name", I18N.message("name"), 100);
		ListGridField path = new ListGridField("path", I18N.message("path"));
		path.setWidth("100%");
		path.setCanEdit(true);

		ListGridField write = new ListGridField("write", " ", 20);
		write.setType(ListGridFieldType.IMAGE);
		write.setCanSort(false);
		write.setAlign(Alignment.CENTER);
		write.setShowDefaultContextMenu(false);
		write.setImageURLPrefix(Util.imagePrefix());
		write.setImageURLSuffix(".png");
		write.setCanFilter(false);
		stores.setFields(id, write, name, path);
		stores.setDataSource(new StoragesDS());
		stores.setAutoFetchData(true);
		stores.addCellContextClickHandler(new CellContextClickHandler() {
			@Override
			public void onCellContextClick(CellContextClickEvent event) {
				showContextMenu();
				event.cancel();
			}
		});

		addMember(stores);
	}

	/**
	 * Prepares the context menu
	 */
	private void showContextMenu() {
		MenuItem makeWrite = new MenuItem();
		makeWrite.setTitle(I18N.message("makedefwritestore"));
		makeWrite.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
			public void onClick(MenuItemClickEvent event) {
				ListGridRecord[] recs = stores.getRecords();
				for (ListGridRecord rec : recs) {
					rec.setAttribute("write", "blank");
					stores.refreshRow(stores.getRowNum(rec));
				}
				stores.getSelectedRecord().setAttribute("write", "database_edit");
				stores.refreshRow(stores.getRowNum(stores.getSelectedRecord()));
			}
		});

		Menu contextMenu = new Menu();
		contextMenu.setItems(makeWrite);
		contextMenu.showContextMenu();
	}

	private void onSave() {
		final List<GUIParameter> settings = new ArrayList<GUIParameter>();
		settings.add(new GUIParameter("store.compress", "yes".equals(compression.getValue()) ? "true" : "false"));
		ListGridRecord[] records = stores.getRecords();
		for (ListGridRecord rec : records) {
			String id = rec.getAttributeAsString("id").trim();
			settings.add(new GUIParameter("store." + id + ".dir", rec.getAttributeAsString("path").trim()));
			if ("database_edit".equals(rec.getAttributeAsString("write"))) {
				settings.add(new GUIParameter("store.write", id));
			}
		}

		service.saveSettings(settings.toArray(new GUIParameter[0]), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(Void arg) {
				Log.info(I18N.message("settingssaved"), null);

				// Replicate the settings in the current session
				for (GUIParameter setting : settings) {
					Session.get().setConfig(setting.getName(), setting.getValue());
				}
			}
		});
	}
}
