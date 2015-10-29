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
import com.smartgwt.client.widgets.form.fields.IntegerItem;
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
		setHeight100();
		setMembersMargin(5);
		setMargin(5);

		tabs.setWidth100();
		tabs.setHeight100();

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
		welcome.setWidth(400);

		RadioGroupItem savelogin = ItemFactory.newBooleanSelector("savelogin", I18N.message("savelogin"));
		savelogin.setHint(I18N.message("saveloginhint"));
		savelogin.setWrapTitle(false);

		TextItem previewPages = ItemFactory.newIntegerItem("previewpages", I18N.message("previewpages"), null);
		previewPages.setRequired(true);

		TextItem previewSize = ItemFactory.newIntegerItem("previewsize", I18N.message("previewwindow"), null);
		previewSize.setHint("%");
		previewSize.setRequired(true);

		TextItem previewZoom = ItemFactory.newIntegerItem("previewzoom", I18N.message("previewzoom"), null);
		previewZoom.setHint("%");
		previewZoom.setRequired(true);

		IntegerItem previewTimeout = ItemFactory.newIntegerItem("previewtimeout", "previewtimeout", null);
		previewTimeout.setHint(I18N.message("seconds"));
		previewTimeout.setWrapTitle(false);
		previewTimeout.setRequired(true);

		TextItem thumbSize = ItemFactory.newIntegerItem("thumbsize", I18N.message("thumbsize"), null);
		thumbSize.setHint("pixels");
		thumbSize.setRequired(true);

		TextItem thumbQuality = ItemFactory.newIntegerItem("thumbquality", I18N.message("thumbquality"), null);
		thumbQuality.setHint("%");
		thumbQuality.setRequired(true);
		thumbQuality.setWrapTitle(false);

		TextItem tileSize = ItemFactory.newIntegerItem("tilesize", I18N.message("tilesize"), null);
		tileSize.setHint("pixels");
		tileSize.setRequired(true);

		TextItem tileQuality = ItemFactory.newIntegerItem("tilequality", I18N.message("tilequality"), null);
		tileQuality.setHint("%");
		tileQuality.setRequired(true);
		tileQuality.setWrapTitle(false);

		TextItem uploadmax = ItemFactory.newIntegerItem("uploadmax", I18N.message("uploadmax"), null);
		uploadmax.setHint("MB");
		uploadmax.setRequired(true);
		uploadmax.setWrapTitle(false);

		TextItem disallow = ItemFactory.newTextItem("disallow", I18N.message("disallowedext"), null);
		disallow.setHint(I18N.message("separatedcomma"));
		disallow.setRequired(false);
		disallow.setWrapTitle(false);

		TextItem searchhits = ItemFactory.newIntegerItem("searchhits", I18N.message("searchhits"), null);
		searchhits.setRequired(true);
		searchhits.setWrapTitle(false);

		RadioGroupItem ondoubleclick = ItemFactory.newBooleanSelector("ondoubleclick", "ondoubleclick");
		ondoubleclick.setValueMap("download", "preview");

		RadioGroupItem doctab = ItemFactory.newBooleanSelector("doctab", "doctab");
		doctab.setValueMap("properties", "preview");

		TextItem extattr = ItemFactory.newTextItem("extattr", I18N.message("extendedattrs"), null);
		extattr.setHint(I18N.message("separatedcomma"));
		extattr.setWidth(400);

		TextItem webcontentfolders = ItemFactory.newTextItem("webcontentfolders", I18N.message("webcontentfolders"),
				null);
		webcontentfolders.setHint(I18N.message("commaseplistofids"));
		webcontentfolders.setWidth(400);

		TextItem sessiontimeout = ItemFactory.newIntegerItem("sessiontimeout", I18N.message("sessiontimeout"), null);
		sessiontimeout.setHint(I18N.message("minutes"));
		sessiontimeout.setRequired(true);
		sessiontimeout.setWrapTitle(false);

		TextItem sessionheartbeat = ItemFactory.newIntegerItem("sessionheartbeat", I18N.message("sessionheartbeat"),
				null);
		sessionheartbeat.setHint(I18N.message("seconds"));
		sessionheartbeat.setRequired(true);
		sessionheartbeat.setWrapTitle(false);

		parametersForm.setItems(welcome, previewPages, previewSize, previewZoom, previewTimeout, thumbSize,
				thumbQuality, tileSize, tileQuality, uploadmax, disallow, ondoubleclick, doctab, searchhits, extattr,
				webcontentfolders, savelogin, sessiontimeout, sessionheartbeat);

		for (GUIParameter p : settings) {
			if (p.getName().endsWith("gui.welcome"))
				welcome.setValue(p.getValue());
			if (p.getName().endsWith("gui.savelogin"))
				savelogin.setValue(p.getValue().equals("true") ? "yes" : "no");
			if (p.getName().endsWith("gui.preview.pages"))
				previewPages.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("gui.preview.size"))
				previewSize.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("gui.preview.zoom"))
				previewZoom.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("gui.preview.timeout"))
				previewTimeout.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("gui.thumbnail.size"))
				thumbSize.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("gui.thumbnail.quality"))
				thumbQuality.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("gui.tile.size"))
				tileSize.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("gui.tile.quality"))
				tileQuality.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("gui.doubleclick"))
				ondoubleclick.setValue(p.getValue());
			if (p.getName().endsWith("gui.document.tab"))
				doctab.setValue(p.getValue());
			if (p.getName().endsWith("upload.maxsize"))
				uploadmax.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("upload.disallow") && p.getValue() != null)
				disallow.setValue(p.getValue().trim());
			if (p.getName().endsWith("search.hits"))
				searchhits.setValue(Integer.parseInt(p.getValue().trim()));
			if (p.getName().endsWith("search.extattr"))
				extattr.setValue(p.getValue());
			if (p.getName().endsWith("gui.webcontent.folders"))
				webcontentfolders.setValue(p.getValue());
			if (p.getName().endsWith("session.timeout"))
				sessiontimeout.setValue(p.getValue());
			if (p.getName().endsWith("session.heartbeat"))
				sessionheartbeat.setValue(p.getValue());
		}

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					List<GUIParameter> params = new ArrayList<GUIParameter>();
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.welcome", (String) values
							.get("welcome")));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.savelogin", "yes".equals(values
							.get("savelogin")) ? "true" : "false"));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.preview.pages", values.get(
							"previewpages").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.preview.size", values.get(
							"previewsize").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.preview.zoom", values.get(
							"previewzoom").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.preview.timeout", values.get(
							"previewtimeout").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.thumbnail.size", values.get(
							"thumbsize").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.thumbnail.quality", values.get(
							"thumbquality").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.tile.size", values
							.get("tilesize").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.tile.quality", values.get(
							"tilequality").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.doubleclick", values.get(
							"ondoubleclick").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.document.tab", values.get(
							"doctab").toString()));
					params.add(new GUIParameter("upload.maxsize", values.get("uploadmax").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".upload.disallow", values.get(
							"disallow").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".search.hits", values
							.get("searchhits").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".search.extattr", values
							.get("extattr").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".gui.webcontent.folders", values.get(
							"webcontentfolders").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".session.timeout", values.get(
							"sessiontimeout").toString()));
					params.add(new GUIParameter(Session.get().getTenantName() + ".session.heartbeat", values.get(
							"sessionheartbeat").toString()));

					// Update the current session parameters.
					for (GUIParameter p : params)
						Session.get().getInfo().setConfig(p.getName(), p.getValue());

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