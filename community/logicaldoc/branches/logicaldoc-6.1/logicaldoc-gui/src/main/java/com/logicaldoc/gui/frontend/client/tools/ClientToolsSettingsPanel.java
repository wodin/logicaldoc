package com.logicaldoc.gui.frontend.client.tools;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the Web Service and WebDAV settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ClientToolsSettingsPanel extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs = new TabSet();

	private GUIParameter wsSettings = null;

	private GUIParameter wdSettings = null;

	private GUIParameter wdCache = null;

	private GUIParameter officeSettings = null;

	public ClientToolsSettingsPanel(GUIParameter[] settings) {
		for (GUIParameter parameter : settings) {
			if (parameter.getName().startsWith("webservice"))
				wsSettings = parameter;
			else if (parameter.getName().equals("webdav.enabled"))
				wdSettings = parameter;
			else if (parameter.getName().equals("webdav.usecache"))
				wdCache = parameter;
			else if (parameter.getName().startsWith("office"))
				officeSettings = parameter;
		}

		setWidth100();
		setHeight(370);
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(450);
		tabs.setHeight(350);

		Tab webService = new Tab();
		webService.setTitle(I18N.message("webservice"));

		DynamicForm webServiceForm = new DynamicForm();
		webServiceForm.setValuesManager(vm);
		webServiceForm.setTitleOrientation(TitleOrientation.LEFT);
		webServiceForm.setNumCols(2);
		webServiceForm.setColWidths(1, "*");
		webServiceForm.setPadding(5);

		// Url
		LinkItem url = new LinkItem(I18N.message("url"));
		url.setRequired(true);
		url.setLinkTitle(GWT.getHostPageBaseURL() + "services");
		url.setValue(GWT.getHostPageBaseURL() + "services");

		// Web Service Enabled
		RadioGroupItem wsEnabled = ItemFactory.newBooleanSelector("wsEnabled", "enabled");
		wsEnabled.setName("wsEnabled");
		wsEnabled.setRequired(true);
		wsEnabled.setValue(wsSettings.getValue().equals("true") ? "yes" : "no");

		webServiceForm.setItems(url, wsEnabled);
		webService.setPane(webServiceForm);

		Tab webDav = new Tab();
		webDav.setTitle(I18N.message("webdav"));
		DynamicForm webDavForm = new DynamicForm();
		webDavForm.setValuesManager(vm);
		webDavForm.setTitleOrientation(TitleOrientation.LEFT);
		webDavForm.setNumCols(2);
		webDavForm.setColWidths(1, "*");
		webDavForm.setPadding(5);

		// Url
		StaticTextItem wdUrl = ItemFactory.newStaticTextItem("wdUrl", "WebDAV", GWT.getHostPageBaseURL()
				+ "webdav/store");

		// Status
		RadioGroupItem wdEnabled = ItemFactory.newBooleanSelector("wdEnabled", "enabled");
		wdEnabled.setName("wdEnabled");
		wdEnabled.setRequired(true);
		wdEnabled.setValue(wdSettings.getValue().equals("true") ? "yes" : "no");

		// Webdav Cache
		RadioGroupItem cache = ItemFactory.newBooleanSelector("wdCache", "usecache");
		cache.setName("wdCache");
		cache.setRequired(true);
		cache.setWrap(false);
		cache.setWrapTitle(false);
		cache.setValue(wdCache.getValue().equals("true") ? "yes" : "no");

		webDavForm.setItems(wdUrl, wdEnabled, cache);
		webDav.setPane(webDavForm);

		Tab office = new Tab();
		office.setTitle(I18N.message("office"));
		DynamicForm officeForm = new DynamicForm();
		officeForm.setValuesManager(vm);
		officeForm.setTitleOrientation(TitleOrientation.LEFT);
		officeForm.setNumCols(2);
		officeForm.setColWidths(1, "*");
		officeForm.setPadding(5);

		// Status
		RadioGroupItem officeEnabled = ItemFactory.newBooleanSelector("officeEnabled", "enabled");
		officeEnabled.setName("officeEnabled");
		officeEnabled.setRequired(true);
		officeEnabled.setValue(officeSettings.getValue().equals("true") ? "yes" : "no");

		officeForm.setItems(officeEnabled);
		office.setPane(officeForm);

		if (Feature.visible(Feature.WEBSERVICE)) {
			tabs.addTab(webService);
			if (!Feature.enabled(Feature.WEBSERVICE))
				webService.setPane(new FeatureDisabled());
		}

		if (Feature.visible(Feature.WEBDAV)) {
			tabs.addTab(webDav);
			if (!Feature.enabled(Feature.WEBDAV))
				webDav.setPane(new FeatureDisabled());
		}

		if (Feature.visible(Feature.OFFICE)) {
			tabs.addTab(office);
			if (!Feature.enabled(Feature.OFFICE))
				office.setPane(new FeatureDisabled());
		}

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					ClientToolsSettingsPanel.this.wsSettings.setValue(values.get("wsEnabled").equals("yes") ? "true"
							: "false");

					ClientToolsSettingsPanel.this.wdSettings.setValue(values.get("wdEnabled").equals("yes") ? "true"
							: "false");

					ClientToolsSettingsPanel.this.wdCache.setValue(values.get("wdCache").equals("yes") ? "true"
							: "false");

					ClientToolsSettingsPanel.this.officeSettings
							.setValue(values.get("officeEnabled").equals("yes") ? "true" : "false");

					GUIParameter[] params = new GUIParameter[3];
					params[0] = ClientToolsSettingsPanel.this.wsSettings;
					params[1] = ClientToolsSettingsPanel.this.wdSettings;
					params[2] = ClientToolsSettingsPanel.this.wdCache;
					params[3] = ClientToolsSettingsPanel.this.officeSettings;

					service.saveClientSettings(Session.get().getSid(), params, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void ret) {
							Log.info(I18N.message("settingssaved"), null);
						}
					});
				}
			}
		});

		addMember(tabs);
		if (Feature.enabled(Feature.WEBSERVICE) || Feature.enabled(Feature.WEBDAV) || Feature.enabled(Feature.OFFICE))
			addMember(save);
	}
}