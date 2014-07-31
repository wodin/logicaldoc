package com.logicaldoc.gui.frontend.client.settings;

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
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the OCR settings.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class OCRSettingsPanel extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs = new TabSet();

	public OCRSettingsPanel(GUIParameter[] settings) {
		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);

		tabs.setWidth100();
		tabs.setHeight100();

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setNumCols(2);
		form.setColWidths(1, "*");
		form.setPadding(5);

		// OCR Enabled
		RadioGroupItem enabled = ItemFactory.newBooleanSelector("ocr_enabled", "enabled");
		enabled.setRequired(true);
		enabled.setValue(settings[0].getValue().equals("true") ? "yes" : "no");
		enabled.setDisabled(!Session.get().isDefaultTenant());

		TextItem includes = ItemFactory.newTextItem("ocr_includes", "include", settings[3].getValue());
		TextItem excludes = ItemFactory.newTextItem("ocr_excludes", "exclude", settings[4].getValue());

		IntegerItem timeout = ItemFactory.newIntegerItem("ocr_timeout", I18N.message("timeout"),
				Integer.parseInt(settings[5].getValue()));
		timeout.setRequired(true);
		timeout.setWrapTitle(false);
		timeout.setHint(I18N.message("seconds"));

		IntegerItem textThreshold = ItemFactory.newIntegerItem("ocr_text_threshold", I18N.message("textthreshold"),
				Integer.parseInt(settings[2].getValue()));
		textThreshold.setRequired(true);
		textThreshold.setWrapTitle(false);
		textThreshold.setHint("%");

		IntegerItem resolutionThreshold = ItemFactory.newIntegerItem("ocr_resolution_threshold",
				I18N.message("resolutionthreshold"), Integer.parseInt(settings[1].getValue()));
		resolutionThreshold.setRequired(true);
		resolutionThreshold.setWrapTitle(false);
		resolutionThreshold.setHint("pixels");

		IntegerItem ocrrendres = ItemFactory.newIntegerItem("ocr_rendres", I18N.message("ocrrendres"),
				Integer.parseInt(settings[10].getValue()));
		ocrrendres.setRequired(true);
		ocrrendres.setWrapTitle(false);
		ocrrendres.setHint("dpi");

		IntegerItem barcoderendres = ItemFactory.newIntegerItem("ocr_rendres_barcode", I18N.message("barcoderendres"),
				Integer.parseInt(settings[11].getValue()));
		barcoderendres.setRequired(true);
		barcoderendres.setWrapTitle(false);
		barcoderendres.setHint("dpi");

		IntegerItem batch = ItemFactory.newIntegerItem("ocr_batch", I18N.message("batch"),
				Integer.parseInt(settings[12].getValue()));
		batch.setRequired(true);
		batch.setWrapTitle(false);
		batch.setHint("pages");

		RadioGroupItem engine = ItemFactory.newBooleanSelector("ocr_engine", "engine");
		engine.setRequired(true);
		engine.setValueMap("tesseract", "omnipage");
		engine.setValue(settings[6].getValue());

		TextItem tesseract = ItemFactory.newTextItem("command_tesseract", "Tesseract", settings[7].getValue());

		TextItem omnipagePath = ItemFactory.newTextItem("omnipage_path", "OmniPage path", settings[8].getValue());

		StaticTextItem count = ItemFactory.newStaticTextItem("ocr_count", I18N.message("monthlycounter"),
				settings[9].getValue());

		if (Session.get().isDefaultTenant())
			form.setItems(enabled, timeout, includes, excludes, textThreshold, resolutionThreshold, ocrrendres,
					barcoderendres, batch, engine, tesseract, omnipagePath, count);
		else
			form.setItems(enabled, includes, excludes, textThreshold, resolutionThreshold);

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					GUIParameter[] params = new GUIParameter[12];

					if (Session.get().isDefaultTenant())
						params[0] = new GUIParameter("ocr.enabled", values.get("ocr_enabled").equals("yes") ? "true"
								: "false");
					params[1] = new GUIParameter(Session.get().getTenantName() + ".ocr.includes", (String) values
							.get("ocr_includes"));
					params[2] = new GUIParameter(Session.get().getTenantName() + ".ocr.excludes", (String) values
							.get("ocr_excludes"));
					if (values.get("ocr_text_threshold") instanceof Integer)
						params[3] = new GUIParameter(Session.get().getTenantName() + ".ocr.text.threshold",
								((Integer) values.get("ocr_text_threshold")).toString());
					else
						params[3] = new GUIParameter(Session.get().getTenantName() + ".ocr.text.threshold",
								(String) values.get("ocr_text_threshold"));

					if (values.get("ocr_resolution_threshold") instanceof Integer)
						params[4] = new GUIParameter(Session.get().getTenantName() + ".ocr.resolution.threshold",
								((Integer) values.get("ocr_resolution_threshold")).toString());
					else
						params[4] = new GUIParameter(Session.get().getTenantName() + ".ocr.resolution.threshold",
								(String) values.get("ocr_resolution_threshold"));

					if (Session.get().isDefaultTenant()) {
						if (values.get("ocr_timeout") instanceof Integer)
							params[5] = new GUIParameter("ocr.timeout", ((Integer) values.get("ocr_timeout"))
									.toString());
						else
							params[5] = new GUIParameter("ocr.timeout", (String) values.get("ocr_timeout"));

						if (values.get("ocr_rendres") instanceof Integer)
							params[6] = new GUIParameter("ocr.rendres", ((Integer) values.get("ocr_rendres"))
									.toString());
						else
							params[6] = new GUIParameter("ocr.rendres", (String) values.get("ocr_rendres"));

						if (values.get("ocr_rendres_barcode") instanceof Integer)
							params[7] = new GUIParameter("ocr.rendres.barcode", ((Integer) values
									.get("ocr_rendres_barcode")).toString());
						else
							params[7] = new GUIParameter("ocr.rendres.barcode", (String) values
									.get("ocr_rendres_barcode"));

						if (values.get("ocr_batch") instanceof Integer)
							params[11] = new GUIParameter("ocr.batch", ((Integer) values.get("ocr_batch")).toString());
						else
							params[11] = new GUIParameter("ocr.batch", (String) values.get("ocr_batch"));

						params[8] = new GUIParameter("ocr.engine", (String) values.get("ocr_engine"));
						params[9] = new GUIParameter("command.tesseract", (String) values.get("command_tesseract"));
						params[10] = new GUIParameter("omnipage.path", (String) values.get("omnipage_path"));
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
				}
			}
		});

		Tab ocrTab = new Tab();
		ocrTab.setPane(form);
		ocrTab.setTitle(I18N.message("ocr"));
		tabs.setTabs(ocrTab);

		setMembers(tabs, save);
	}
}