package com.logicaldoc.gui.frontend.client.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the Folders settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class RepositoriesPanel extends VLayout {

	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private DynamicForm foldersForm = new DynamicForm();

	private Tab tab1 = null;

	private Tab tab2 = null;

	private TabSet tabs = new TabSet();

	public RepositoriesPanel() {
		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);

		tabs = new TabSet();
		tabs.setWidth100();
		tabs.setHeight100();

		// The Folders Tab
		tab2 = new Tab();
		tab2.setTitle(I18N.message("folders"));
		foldersForm.setWidth(400);
		foldersForm.setColWidths(1, "*");
		foldersForm.setTitleOrientation(TitleOrientation.LEFT);
		tab2.setPane(foldersForm);

		// The Storages Tab
		tab1 = new Tab();
		tab1.setTitle(I18N.message("stores"));
		tab1.setID("repos");
		tab1.setPane(new StoresPanel());

		tabs.setTabs(tab1, tab2);

		setMembers(tabs);

		service.loadSettingsByNames(new String[] { "conf.dbdir", "conf.exportdir", "conf.importdir", "conf.logdir",
				"conf.plugindir", "conf.userdir" }, new AsyncCallback<GUIParameter[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.serverError(caught);
			}

			@Override
			public void onSuccess(GUIParameter[] folderParameters) {
				List<FormItem> items = new ArrayList<FormItem>();

				for (GUIParameter f : folderParameters) {
					TextItem item = ItemFactory.newTextItem(f.getName(),
							f.getName().substring(f.getName().indexOf('.') + 1), f.getValue());
					item.setValue(f.getValue());
					item.setRequired(true);
					item.setWidth(400);
					items.add(item);
				}

				ButtonItem save = new ButtonItem("save", I18N.message("save"));
				save.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

					@Override
					public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
						onSaveFolders();
					}
				});
				items.add(save);

				save.setDisabled(Session.get().isDemo() && Session.get().getUser().getId() == 1);

				foldersForm.setItems(items.toArray(new FormItem[0]));
			}
		});
	}

	private void onSaveFolders() {
		final List<GUIParameter> settings = new ArrayList<GUIParameter>();
		@SuppressWarnings("unchecked")
		Map<String, Object> values = foldersForm.getValues();
		for (String name : values.keySet()) {
			if (!"save".equals(name))
				settings.add(new GUIParameter(ItemFactory.originalItemName(name), values.get(name).toString().trim()));
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