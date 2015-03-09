package com.logicaldoc.gui.frontend.client.settings;

import java.util.ArrayList;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIExternalCall;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
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

	private GUIParameter acmecad = null;

	private CheckboxItem extCallParamUser;

	private CheckboxItem extCallParamTitle;

	private GUIParameter openssl = null;

	private GUIParameter pdftohtml = null;

	public ExternalAppsPanel(GUIParameter[] settings) {
		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);
		tabs.setWidth100();
		tabs.setHeight100();

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
			else if (parameter.getName().equals("command.openssl"))
				openssl = parameter;
			else if (parameter.getName().equals("command.pdftohtml"))
				pdftohtml = parameter;
			else if (parameter.getName().equals("openoffice.path"))
				openofficePath = parameter;
			else if (parameter.getName().equals("swftools.path"))
				swftoolsPath = parameter;
			else if (parameter.getName().equals("acmecad.command"))
				acmecad = parameter;
		}

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

		if (Session.get().isDefaultTenant())
			webServiceForm.setItems(url, wsEnabled);
		else
			webServiceForm.setItems(url);
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
				+ "service/cmis");

		// Web Service Enabled
		RadioGroupItem cmisEnabled = ItemFactory.newBooleanSelector("cmisEnabled", "enabled");
		cmisEnabled.setName("cmisEnabled");
		cmisEnabled.setRequired(true);
		cmisEnabled.setValue(cmisSettings.getValue().equals("true") ? "yes" : "no");

		if (Session.get().isDefaultTenant())
			cmisForm.setItems(cmisUrl, cmisEnabled);
		else
			cmisForm.setItems(cmisUrl);
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

		if (Session.get().isDefaultTenant())
			webDavForm.setItems(wdUrl, wdEnabled, cache);
		else
			webDavForm.setItems(wdUrl);
		webDav.setPane(webDavForm);

		Tab extApps = new Tab();
		extApps.setTitle(I18N.message("extapps"));
		DynamicForm extAppForm = new DynamicForm();
		extAppForm.setValuesManager(vm);
		extAppForm.setTitleOrientation(TitleOrientation.LEFT);
		extAppForm.setPadding(5);

		TextItem convertCommand = ItemFactory.newTextItem("convertCommand", "Convert", convert.getValue());
		convertCommand.setWidth(400);
		TextItem ghostCommand = ItemFactory.newTextItem("ghostCommand", "Ghostscript", ghost.getValue());
		ghostCommand.setWidth(400);
		TextItem tesseractCommand = ItemFactory.newTextItem("tesseractCommand", "Tesseract", tesseract.getValue());
		tesseractCommand.setWidth(400);
		TextItem openOffice = ItemFactory.newTextItem("openOffice", "OpenOffice path", openofficePath.getValue());
		openOffice.setWidth(400);
		TextItem swftools = ItemFactory.newTextItem("swftools", "SWFTools path", swftoolsPath.getValue());
		swftools.setWidth(400);
		TextItem acmecadCommand = ItemFactory.newTextItem("acmecadCommand", "AcmeCADConverter", acmecad.getValue());
		acmecadCommand.setWidth(400);
		TextItem pdftohtmlCommand = ItemFactory.newTextItem("pdftohtmlCommand", "Pdftohtml", pdftohtml.getValue());
		pdftohtmlCommand.setWidth(400);
		TextItem opensslCommand = ItemFactory.newTextItem("opensslCommand", "OpenSSL", openssl.getValue());
		opensslCommand.setWidth(400);

		extAppForm.setItems(convertCommand, ghostCommand, tesseractCommand, swftools, openOffice, pdftohtmlCommand,
				acmecadCommand, opensslCommand);
		extApps.setPane(extAppForm);

		if (Session.get().isDefaultTenant())
			tabs.addTab(extApps);

		// External Call
		Tab extCall = prepareExternalCall(settings);

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

		if (Feature.visible(Feature.EXTERNAL_CALL)) {
			tabs.addTab(extCall);
			if (!Feature.enabled(Feature.EXTERNAL_CALL))
				extCall.setPane(new FeatureDisabled());
		}

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					if (Session.get().isDefaultTenant()) {
						ExternalAppsPanel.this.wsSettings.setValue(values.get("wsEnabled").equals("yes") ? "true"
								: "false");

						ExternalAppsPanel.this.cmisSettings.setValue(values.get("cmisEnabled").equals("yes") ? "true"
								: "false");

						ExternalAppsPanel.this.wdSettings.setValue(values.get("wdEnabled").equals("yes") ? "true"
								: "false");

						ExternalAppsPanel.this.wdCache.setValue(values.get("wdCache").equals("yes") ? "true" : "false");
						ExternalAppsPanel.this.convert.setValue(values.get("convertCommand").toString());
						ExternalAppsPanel.this.ghost.setValue(values.get("ghostCommand").toString());
						ExternalAppsPanel.this.tesseract.setValue(values.get("tesseractCommand").toString());
						ExternalAppsPanel.this.swftoolsPath.setValue(values.get("swftools").toString());
						ExternalAppsPanel.this.openofficePath.setValue(values.get("openOffice").toString());
						ExternalAppsPanel.this.acmecad.setValue(values.get("acmecadCommand").toString());
						ExternalAppsPanel.this.openssl.setValue(values.get("opensslCommand").toString());
						ExternalAppsPanel.this.pdftohtml.setValue(values.get("pdftohtmlCommand").toString());
					}

					GUIParameter[] params = new GUIParameter[18];
					params[0] = ExternalAppsPanel.this.wsSettings;
					params[1] = ExternalAppsPanel.this.wdSettings;
					params[2] = ExternalAppsPanel.this.wdCache;
					params[3] = ExternalAppsPanel.this.convert;
					params[4] = ExternalAppsPanel.this.swftoolsPath;
					params[5] = ExternalAppsPanel.this.ghost;
					params[6] = ExternalAppsPanel.this.tesseract;
					params[7] = ExternalAppsPanel.this.openofficePath;
					params[8] = ExternalAppsPanel.this.acmecad;
					params[9] = ExternalAppsPanel.this.openssl;
					params[10] = ExternalAppsPanel.this.pdftohtml;
					params[11] = ExternalAppsPanel.this.cmisSettings;

					// External Call
					try {
						GUIExternalCall extCall = new GUIExternalCall();
						extCall.setName(values.get("extCallName") == null ? "" : values.get("extCallName").toString());
						extCall.setBaseUrl(values.get("extCallBaseUrl") == null ? "" : values.get("extCallBaseUrl")
								.toString());
						extCall.setSuffix(values.get("extCallSuffix") == null ? "" : values.get("extCallSuffix")
								.toString());
						extCall.setTargetWindow(values.get("extCallWindow") == null ? "" : values.get("extCallWindow")
								.toString());
						if ("yes".equals(values.get("extCallEnabled")))
							Session.get().getSession().setExternalCall(extCall);
						else
							Session.get().getSession().setExternalCall(null);

						String tenant = Session.get().getTenantName();
						params[12] = new GUIParameter(tenant + ".extcall.enabled", "yes".equals(values
								.get("extCallEnabled")) ? "true" : "false");
						params[13] = new GUIParameter(tenant + ".extcall.name", extCall.getName());
						params[14] = new GUIParameter(tenant + ".extcall.baseurl", extCall.getBaseUrl());
						params[15] = new GUIParameter(tenant + ".extcall.suffix", extCall.getSuffix());
						params[16] = new GUIParameter(tenant + ".extcall.window", extCall.getTargetWindow());

						ArrayList<String> buf = new ArrayList<String>();
						if (extCallParamUser.getValueAsBoolean() != null
								&& extCallParamUser.getValueAsBoolean().booleanValue())
							buf.add("user");
						if (extCallParamTitle.getValueAsBoolean() != null
								&& extCallParamTitle.getValueAsBoolean().booleanValue())
							buf.add("title");
						String paramsStr = buf.toString().substring(1, buf.toString().length() - 1);

						extCall.setParametersStr(paramsStr);
						params[17] = new GUIParameter(tenant + ".extcall.params", buf.isEmpty() ? "" : paramsStr);
					} catch (Throwable t) {
					}

					service.saveSettings(Session.get().getSid(), params, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.serverError(caught);
						}

						@Override
						public void onSuccess(Void ret) {
							Log.info(I18N.message("settingssaved"), null);
						}
					});
				} else {
					SC.warn(I18N.message("invalidsettings"));
				}
			}
		});

		addMember(tabs);
		addMember(save);

		if (Session.get().isDemo()) {
			// In demo mode you cannot alter the client configurations
			save.setDisabled(true);
		}
	}

	private Tab prepareExternalCall(GUIParameter[] settings) {
		VLayout pane = new VLayout();

		Tab extCall = new Tab();
		extCall.setTitle(I18N.message("externalcall"));
		DynamicForm extCallForm = new DynamicForm();
		extCallForm.setWidth(400);
		extCallForm.setIsGroup(true);
		extCallForm.setNumCols(2);
		extCallForm.setPadding(2);
		extCallForm.setGroupTitle(I18N.message("externalcall"));
		extCallForm.setValuesManager(vm);
		extCallForm.setTitleOrientation(TitleOrientation.LEFT);
		final RadioGroupItem extCallEnabled = ItemFactory.newBooleanSelector("extCallEnabled", "enabled");
		extCallEnabled.setRequired(true);
		extCallEnabled.setRedrawOnChange(true);
		extCallEnabled.setValue("no");

		TextItem extCallName = ItemFactory.newTextItem("extCallName", I18N.message("name"), null);
		TextItem extCallBaseUrl = ItemFactory.newTextItem("extCallBaseUrl", I18N.message("baseurl"), null);
		extCallBaseUrl.setWidth(300);
		TextItem extCallSuffix = ItemFactory.newTextItem("extCallSuffix", I18N.message("suffix"), null);
		extCallSuffix.setWidth(300);
		TextItem extCallWindow = ItemFactory.newTextItem("extCallWindow", I18N.message("targetwindow"), "_blank");

		extCallForm.setItems(extCallEnabled, extCallName, extCallBaseUrl, extCallSuffix, extCallWindow);

		// Use a second form to group the parameters section
		DynamicForm parametersForm = new DynamicForm();
		parametersForm.setWidth(400);
		parametersForm.setIsGroup(true);
		parametersForm.setGroupTitle(I18N.message("parameters"));
		parametersForm.setNumCols(4);
		extCallForm.setPadding(2);
		parametersForm.setValuesManager(vm);
		extCallParamUser = ItemFactory.newCheckbox("extCallParamUser", "user");
		extCallParamTitle = ItemFactory.newCheckbox("extCallParamTitle", "title");
		parametersForm.setItems(extCallParamUser, extCallParamTitle);

		pane.setMembers(extCallForm, parametersForm);
		extCall.setPane(pane);

		String tenant = Session.get().getTenantName();
		for (GUIParameter s : settings) {
			if ((tenant + ".extcall.enabled").equals(s.getName()))
				extCallEnabled.setValue("true".equals(s.getValue()) ? "yes" : "no");
			else if ((tenant + ".extcall.name").equals(s.getName()))
				extCallName.setValue(s.getValue());
			else if ((tenant + ".extcall.baseurl").equals(s.getName()))
				extCallBaseUrl.setValue(s.getValue());
			else if ((tenant + ".extcall.suffix").equals(s.getName()))
				extCallSuffix.setValue(s.getValue());
			else if ((tenant + ".extcall.window").equals(s.getName()))
				extCallWindow.setValue(s.getValue());
			else if ((tenant + ".extcall.params").equals(s.getName())) {
				String[] tokens = s.getValue().split(",");
				for (String param : tokens) {
					if ("user".equals(param.trim()))
						extCallParamUser.setValue("true");
					else if ("title".equals(param.trim()))
						extCallParamTitle.setValue("true");
				}
			}
		}

		return extCall;
	}
}