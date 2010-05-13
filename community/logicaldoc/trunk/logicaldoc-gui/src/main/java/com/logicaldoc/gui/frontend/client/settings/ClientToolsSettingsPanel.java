package com.logicaldoc.gui.frontend.client.settings;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.Log;
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

	private GUIWebServiceSettings wsSettings;

	private GUIWebServiceSettings webDavSettings;

	public ClientToolsSettingsPanel(GUIWebServiceSettings[] settings) {
		this.wsSettings = settings[0];
		this.webDavSettings = settings[1];

		setWidth100();
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(400);
		tabs.setHeight(200);

		Tab webService = new Tab();
		webService.setTitle(I18N.getMessage("webservice"));

		DynamicForm webServiceForm = new DynamicForm();
		webServiceForm.setValuesManager(vm);
		webServiceForm.setTitleOrientation(TitleOrientation.TOP);
		webServiceForm.setNumCols(1);

		// Enabled
		RadioGroupItem enabled = ItemFactory.newBooleanSelector("wsEnabled", I18N.getMessage("enabled"));
		enabled.setName("wsEnabled");
		enabled.setValue(this.wsSettings.isEnabled() ? "yes" : "no");

		// Url
		LinkItem url = new LinkItem();
		url.setName(I18N.getMessage("url"));
		url.setLinkTitle(this.wsSettings.getUrl());
		url.setValue(this.wsSettings.getUrl());

		// Descriptor
		LinkItem descriptor = new LinkItem();
		descriptor.setName(I18N.getMessage("descriptor"));
		descriptor.setLinkTitle(this.wsSettings.getDescriptor());
		descriptor.setValue(this.wsSettings.getDescriptor());

		webServiceForm.setItems(url, descriptor, enabled);
		webService.setPane(webServiceForm);

		Tab webDav = new Tab();
		webDav.setTitle(I18N.getMessage("webdav"));
		DynamicForm webDavForm = new DynamicForm();
		webDavForm.setValuesManager(vm);
		webDavForm.setTitleOrientation(TitleOrientation.TOP);
		webDavForm.setNumCols(1);

		// Status
		RadioGroupItem wdEnabled = ItemFactory.newBooleanSelector("wdEnabled", I18N.getMessage("enabled"));
		wdEnabled.setName("wdEnabled");
		wdEnabled.setValue(this.webDavSettings.isEnabled() ? "yes" : "no");

		// Url
		LinkItem wdUrl = new LinkItem();
		wdUrl.setName(I18N.getMessage("url"));
		wdUrl.setLinkTitle(this.webDavSettings.getUrl());
		wdUrl.setValue(this.webDavSettings.getUrl());

		webDavForm.setItems(wdUrl, wdEnabled);
		webDav.setPane(webDavForm);

		tabs.setTabs(webService, webDav);

		IButton save = new IButton();
		save.setTitle(I18N.getMessage("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					ClientToolsSettingsPanel.this.wsSettings.setEnabled(values.get("wsEnabled").equals("yes") ? true
							: false);

					ClientToolsSettingsPanel.this.webDavSettings
							.setEnabled(values.get("wdEnabled").equals("yes") ? true : false);

					service.saveWSSettings(Session.get().getSid(), ClientToolsSettingsPanel.this.wsSettings,
							ClientToolsSettingsPanel.this.webDavSettings, new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void ret) {
									Log.info(I18N.getMessage("settingssaved"), null);
								}
							});
				}
			}
		});

		setMembers(tabs, save);
	}
}