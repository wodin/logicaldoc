package com.logicaldoc.gui.frontend.client.settings;

import java.util.ArrayList;
import java.util.List;
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
import com.logicaldoc.gui.frontend.client.system.GUILanguagesPanel;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
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
public class GUISettingsPanel extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs = new TabSet();

	public GUISettingsPanel(GUIParameter[] settings) {
		setWidth100();
		setHeight(370);
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(450);
		tabs.setHeight(400);

		Tab parameters = new Tab();
		parameters.setTitle(I18N.message("parameters"));

		DynamicForm parametersForm = new DynamicForm();
		parametersForm.setValuesManager(vm);
		parametersForm.setTitleOrientation(TitleOrientation.LEFT);
		parametersForm.setNumCols(2);
		parametersForm.setColWidths(1, "*");
		parametersForm.setPadding(5);

		parameters.setPane(parametersForm);

		Tab languages = new Tab();
		languages.setTitle(I18N.message("guilanguages"));
		languages.setPane(new GUILanguagesPanel());

		if (Feature.visible(Feature.GUI_LANGUAGES)) {
			tabs.setTabs(parameters, languages);
			if (!Feature.enabled(Feature.GUI_LANGUAGES)) {
				languages.setPane(new FeatureDisabled());
			}
		} else
			tabs.setTabs(parameters);

		TextItem welcome = ItemFactory.newTextItem("welcome", I18N.message("welcomemessage"), null);
		welcome.setWidth(250);

		RadioGroupItem dropspot = ItemFactory.newBooleanSelector("dropspot", "dropspot");
		dropspot.setValueMap("default", "embedded");

		parametersForm.setItems(welcome, dropspot);

		for (GUIParameter p : settings) {
			if (p.getName().equals("gui.welcome"))
				welcome.setValue(p.getValue());
			if (p.getName().equals("gui.dropspot.mode"))
				dropspot.setValue(p.getValue());
		}

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					List<GUIParameter> params = new ArrayList<GUIParameter>();
					params.add(new GUIParameter("gui.welcome", (String) values.get("welcome")));
					params.add(new GUIParameter("gui.dropspot.mode", (String) values.get("dropspot")));
					
					// GUISettingsPanel.this.wsSettings.setValue(values.get("wsEnabled").equals("yes")
					// ? "true"
					// : "false");
					//
					// GUISettingsPanel.this.wdSettings.setValue(values.get("wdEnabled").equals("yes")
					// ? "true"
					// : "false");
					//
					// GUISettingsPanel.this.wdCache.setValue(values.get("wdCache").equals("yes")
					// ? "true"
					// : "false");
					// GUISettingsPanel.this.convert.setValue(values.get("convertCommand").toString());
					// GUISettingsPanel.this.ghost.setValue(values.get("ghostCommand").toString());
					// GUISettingsPanel.this.tesseract.setValue(values.get("tesseractCommand").toString());
					// GUISettingsPanel.this.swftoolsPath.setValue(values.get("swftools").toString());
					// GUISettingsPanel.this.openofficePath.setValue(values.get("openOffice").toString());
					//
					// GUIParameter[] params = new GUIParameter[7];
					// params[0] = GUISettingsPanel.this.wsSettings;
					// params[1] = GUISettingsPanel.this.wdSettings;
					// params[2] = GUISettingsPanel.this.wdCache;
					// params[3] = GUISettingsPanel.this.convert;
					// params[4] = GUISettingsPanel.this.swftoolsPath;
					// params[5] = GUISettingsPanel.this.ghost;
					// params[6] = GUISettingsPanel.this.tesseract;
					// params[7] = GUISettingsPanel.this.openofficePath;

					service.saveSettings(Session.get().getSid(), params.toArray(new GUIParameter[0]),
							new AsyncCallback<Void>() {

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
	}
}