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
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
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

	private ValuesManager vm = new ValuesManager();

	private Tab tab1 = null;

	private Tab tab2 = null;

	private GUIParameter[] foldersParameter = null;

	private GUIParameter[] storagesParameter = null;

	private TabSet tabs = new TabSet();

	public RepositoriesPanel(GUIParameter[][] repos) {
		this.foldersParameter = repos[0];
		this.storagesParameter = repos[1];

		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);


		tabs = new TabSet();
		tabs.setWidth100();
		tabs.setHeight100();

		// The Folders Tab
		tab1 = new Tab();
		tab1.setTitle(I18N.message("folders"));
		DynamicForm foldersForm = new DynamicForm();
		foldersForm.setWidth(300);
		foldersForm.setColWidths(1, "*");
		foldersForm.setValuesManager(vm);
		foldersForm.setTitleOrientation(TitleOrientation.LEFT);
		List<FormItem> items = new ArrayList<FormItem>();

		for (GUIParameter f : this.foldersParameter) {
			TextItem item = ItemFactory.newTextItem(f.getName(), f.getName(), f.getValue());
			item.setRequired(true);
			item.setWidth(250);
			items.add(item);
		}
		foldersForm.setItems(items.toArray(new FormItem[0]));
		tab1.setPane(foldersForm);

		// The Storages Tab
		tab2 = new Tab();
		tab2.setTitle(I18N.message("storages"));
		tab2.setID("repos");
		tab2.setPane(new StoragesPanel(storagesParameter, vm));

		tabs.setTabs(tab1, tab2);

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					final GUIParameter[][] repos = new GUIParameter[2][7];
					List<GUIParameter> folders = new ArrayList<GUIParameter>();
					List<GUIParameter> storages = new ArrayList<GUIParameter>();
					GUIParameter repo = null;
					for (String name : values.keySet()) {
						if (name.startsWith("isc"))
							continue;
						repo = new GUIParameter(name, (String) values.get(name));
						if (name.equals("compression")) {
							repo = new GUIParameter("store.compress", (String) values.get(name));
							storages.add(repo);
						} else if (name.startsWith("store")) {
							storages.add(repo);
						} else if (name.equals("writeto")) {
							String storeSelected = (String) values.get(name);
							storeSelected = storeSelected.replaceAll(".dir", "");
							repo = new GUIParameter("store.write", storeSelected.substring(storeSelected
									.lastIndexOf(".") + 1));
							storages.add(repo);
						} else {
							folders.add(repo);
						}
					}

					repos[0] = folders.toArray(new GUIParameter[0]);
					repos[1] = storages.toArray(new GUIParameter[0]);

					service.saveRepositories(Session.get().getSid(), repos, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void result) {
							tabs.setTabPane("repos", new StoragesPanel(repos[1], vm));
							Log.info(I18N.message("settingssaved"), null);
						}
					});
				}
			}
		});
		save.setDisabled(Session.get().isDemo() && Session.get().getUser().getId() == 1);

		setMembers(tabs, save);
	}
}