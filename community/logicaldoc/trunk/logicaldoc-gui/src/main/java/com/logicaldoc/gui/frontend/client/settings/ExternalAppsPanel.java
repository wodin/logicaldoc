package com.logicaldoc.gui.frontend.client.settings;

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
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the Web Service and WebDAV settings, and also the external
 * applications.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ExternalAppsPanel extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs = new TabSet();

	private GUIParameter wsSettings = null;

	private GUIParameter cmisSettings = null;

	private GUIParameter wdSettings = null;

	private GUIParameter wdCache = null;

	private GUIParameter convert = null;

	private GUIParameter ghost = null;

	private GUIParameter tesseract = null;

	private GUIParameter openofficePath = null;

	private GUIParameter swftoolsPath = null;

	public ExternalAppsPanel(GUIParameter[] settings) {
		for (GUIParameter parameter : settings) {
			if (parameter.getName().startsWith("webservice"))
				wsSettings = parameter;
			else if (parameter.getName().startsWith("cmis"))
				cmisSettings = parameter;
			else if (parameter.getName().equals("webdav.enabled"))
				wdSettings = parameter;
			else if (parameter.getName().equals("webdav.usecache"))
				wdCache = parameter;
			else if (parameter.getName().equals("command.convert"))
				convert = parameter;
			else if (parameter.getName().equals("command.gs"))
				ghost = parameter;
			else if (parameter.getName().equals("command.tesseract"))
				tesseract = parameter;
			else if (parameter.getName().equals("openoffice.path"))
				openofficePath = parameter;
			else if (parameter.getName().equals("swftools.path"))
				swftoolsPath = parameter;
		}

		setWidth100();
		setHeight(250);
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(450);
		tabs.setHeight(240);

		Tab webService = new Tab();
		webService.setTitle(I18N.message("webservice"));

		DynamicForm webServiceForm = new DynamicForm();
		webServiceForm.setValuesManager(vm);
		webServiceForm.setTitleOrientation(TitleOrientation.LEFT);
		webServiceForm.setNumCols(2);
		webServiceForm.setColWidths(1, "*");
		webServiceForm.setPadding(5);

		// Url
		StaticTextItem url = ItemFactory.newStaticTextItem("wsUrl", I18N.message("url"), GWT.getHostPageBaseURL()
				+ "services");

		// Web Service Enabled
		RadioGroupItem wsEnabled = ItemFactory.newBooleanSelector("wsEnabled", "enabled");
		wsEnabled.setName("wsEnabled");
		wsEnabled.setRequired(true);
		wsEnabled.setValue(wsSettings.getValue().equals("true") ? "yes" : "no");

		webServiceForm.setItems(url, wsEnabled);
		webService.setPane(webServiceForm);

		Tab cmis = new Tab();
		cmis.setTitle("CMIS");

		DynamicForm cmisForm = new DynamicForm();
		cmisForm.setValuesManager(vm);
		cmisForm.setTitleOrientation(TitleOrientation.LEFT);
		cmisForm.setNumCols(2);
		cmisForm.setColWidths(1, "*");
		cmisForm.setPadding(5);

		// Url
		StaticTextItem cmisUrl = ItemFactory.newStaticTextItem("cmisUrl", I18N.message("url"), GWT.getHostPageBaseURL()
				+ "cmis");

		// Web Service Enabled
		RadioGroupItem cmisEnabled = ItemFactory.newBooleanSelector("cmisEnabled", "enabled");
		cmisEnabled.setName("cmisEnabled");
		cmisEnabled.setRequired(true);
		cmisEnabled.setValue(cmisSettings.getValue().equals("true") ? "yes" : "no");

		cmisForm.setItems(cmisUrl, cmisEnabled);
		cmis.setPane(cmisForm);

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

		Tab extApps = new Tab();
		extApps.setTitle(I18N.message("extapps"));
		DynamicForm extAppForm = new DynamicForm();
		extAppForm.setValuesManager(vm);
		extAppForm.setTitleOrientation(TitleOrientation.LEFT);
		extAppForm.setPadding(5);

		TextItem convertCommand = ItemFactory.newTextItem("convertCommand", "Convert", convert.getValue());
		TextItem ghostCommand = ItemFactory.newTextItem("ghostCommand", "Ghostscript", ghost.getValue());
		TextItem tesseractCommand = ItemFactory.newTextItem("tesseractCommand", "Tesseract", tesseract.getValue());
		TextItem openOffice = ItemFactory.newTextItem("openOffice", "OpenOffice path", openofficePath.getValue());
		TextItem swftools = ItemFactory.newTextItem("swftools", "SWFTools path", swftoolsPath.getValue());

		extAppForm.setItems(convertCommand, ghostCommand, tesseractCommand, swftools, openOffice);
		extApps.setPane(extAppForm);

		tabs.addTab(extApps);

		if (Feature.visible(Feature.WEBSERVICE)) {
			tabs.addTab(webService);
			if (!Feature.enabled(Feature.WEBSERVICE))
				webService.setPane(new FeatureDisabled());
		}

		if (Feature.visible(Feature.CMIS)) {
			tabs.addTab(cmis);
			if (!Feature.enabled(Feature.CMIS))
				cmis.setPane(new FeatureDisabled());
		}

		if (Feature.visible(Feature.WEBDAV)) {
			tabs.addTab(webDav);
			if (!Feature.enabled(Feature.WEBDAV))
				webDav.setPane(new FeatureDisabled());
		}

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					ExternalAppsPanel.this.wsSettings
							.setValue(values.get("wsEnabled").equals("yes") ? "true" : "false");

					ExternalAppsPanel.this.cmisSettings.setValue(values.get("cmisEnabled").equals("yes") ? "true"
							: "false");

					ExternalAppsPanel.this.wdSettings
							.setValue(values.get("wdEnabled").equals("yes") ? "true" : "false");

					ExternalAppsPanel.this.wdCache.setValue(values.get("wdCache").equals("yes") ? "true" : "false");
					ExternalAppsPanel.this.convert.setValue(values.get("convertCommand").toString());
					ExternalAppsPanel.this.ghost.setValue(values.get("ghostCommand").toString());
					ExternalAppsPanel.this.tesseract.setValue(values.get("tesseractCommand").toString());
					ExternalAppsPanel.this.swftoolsPath.setValue(values.get("swftools").toString());
					ExternalAppsPanel.this.openofficePath.setValue(values.get("openOffice").toString());

					GUIParameter[] params = new GUIParameter[9];
					params[0] = ExternalAppsPanel.this.wsSettings;
					params[1] = ExternalAppsPanel.this.wdSettings;
					params[2] = ExternalAppsPanel.this.wdCache;
					params[3] = ExternalAppsPanel.this.convert;
					params[4] = ExternalAppsPanel.this.swftoolsPath;
					params[5] = ExternalAppsPanel.this.ghost;
					params[6] = ExternalAppsPanel.this.tesseract;
					params[7] = ExternalAppsPanel.this.openofficePath;
					params[8] = ExternalAppsPanel.this.cmisSettings;

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
		addMember(save);

		if (Session.get().isDemo()) {
			// In demo mode you cannot alter the client configurationss
			save.setDisabled(true);
		}
	}
}