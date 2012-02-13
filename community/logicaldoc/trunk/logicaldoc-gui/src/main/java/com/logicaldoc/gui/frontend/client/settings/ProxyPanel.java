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
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the Proxy settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ProxyPanel extends VLayout {

	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	public ProxyPanel(GUIParameter[] parameters) {
		setWidth100();
		setMembersMargin(10);
		setMargin(30);
		setHeight(300);

		TabSet tabs = new TabSet();
		tabs.setWidth(380);
		tabs.setHeight(250);
		Tab tab = new Tab();
		tab.setTitle(I18N.message("proxy"));
		tabs.setTabs(tab);

		DynamicForm proxySettingsForm = new DynamicForm();
		proxySettingsForm.setWidth(300);
		proxySettingsForm.setColWidths(1, "*");
		proxySettingsForm.setValuesManager(vm);
		proxySettingsForm.setTitleOrientation(TitleOrientation.LEFT);

		List<FormItem> items = new ArrayList<FormItem>();

		for (GUIParameter f : parameters) {
			if (f.getName().equals("password")) {
				PasswordItem item = ItemFactory.newPasswordItem(f.getName(), f.getName(), f.getValue());
				item.setRequired(true);
				item.setWidth(250);
				items.add(item);
			} else if (f.getName().equals("port")) {
				IntegerItem item = ItemFactory.newValidateIntegerItem(f.getName(), f.getName(),
						Integer.parseInt(f.getValue()), 1, null);
				item.setRequired(true);
				item.setWidth(250);
				items.add(item);
			} else {
				TextItem item = ItemFactory.newTextItem(f.getName(), f.getName(), f.getValue());
				item.setRequired(true);
				item.setWidth(250);
				items.add(item);
			}

		}

		proxySettingsForm.setItems(items.toArray(new FormItem[0]));

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					List<GUIParameter> proxySettings = new ArrayList<GUIParameter>();
					for (String name : values.keySet()) {
						GUIParameter proxyParam = new GUIParameter(name, values.get(name).toString());
						proxySettings.add(proxyParam);
					}

					service.saveProxySettings(Session.get().getSid(), proxySettings.toArray(new GUIParameter[0]),
							new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void result) {
									Log.info(I18N.message("settingssaved"), null);
								}
							});
				}
			}
		});

		tab.setPane(proxySettingsForm);
		setMembers(tabs, save);
	}
}