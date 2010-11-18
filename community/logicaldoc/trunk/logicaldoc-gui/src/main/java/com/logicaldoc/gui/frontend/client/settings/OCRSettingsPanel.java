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
		setHeight(370);
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(450);
		tabs.setHeight(300);

		DynamicForm form = new DynamicForm();
		form.setValuesManager(vm);
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setNumCols(2);
		form.setColWidths(1, "*");
		form.setPadding(5);

		// OCR Enabled
		RadioGroupItem enabled = ItemFactory.newBooleanSelector("ocr.enabled", "enabled");
		enabled.setRequired(true);
		enabled.setValue(settings[0].getValue().equals("true") ? "yes" : "no");

		TextItem includes = ItemFactory.newTextItem("ocr.includes", I18N.message("include"), settings[3].getValue());
		TextItem excludes = ItemFactory.newTextItem("ocr.excludes", I18N.message("exclude"), settings[4].getValue());

		IntegerItem timeout = ItemFactory.newIntegerItem("ocr.timeout", I18N.message("timeout"),
				Integer.parseInt(settings[5].getValue()));
		timeout.setRequired(true);
		timeout.setWrapTitle(false);
		timeout.setHint(I18N.message("seconds"));

		IntegerItem textThreshold = ItemFactory.newIntegerItem("ocr.text.threshold", I18N.message("textthreshold"),
				Integer.parseInt(settings[2].getValue()));
		textThreshold.setRequired(true);
		textThreshold.setWrapTitle(false);
		textThreshold.setHint("%");

		IntegerItem resolutionThreshold = ItemFactory.newIntegerItem("ocr.resolution.threshold",
				I18N.message("resolutionthreshold"), Integer.parseInt(settings[1].getValue()));
		resolutionThreshold.setRequired(true);
		resolutionThreshold.setWrapTitle(false);
		resolutionThreshold.setHint("pixels");

		form.setItems(enabled, includes, excludes, textThreshold, resolutionThreshold);

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					GUIParameter[] params = new GUIParameter[4];
					params[0] = new GUIParameter("ocr.enabled", values.get("ocr.enabled").equals("yes") ? "true"
							: "false");
					params[1] = new GUIParameter("ocr.includes", (String) values.get("ocr.includes"));
					params[2] = new GUIParameter("ocr.excludes", (String) values.get("ocr.excludes"));
					if (values.get("ocr.text.threshold") instanceof Integer)
						params[3] = new GUIParameter("ocr.text.threshold", ((Integer) values.get("ocr.text.threshold"))
								.toString());
					else
						params[3] = new GUIParameter("ocr.text.threshold", (String) values.get("ocr.text.threshold"));

					if (values.get("ocr.resolution.threshold") instanceof Integer)
						params[4] = new GUIParameter("ocr.resolution.threshold", ((Integer) values
								.get("ocr.resolution.threshold")).toString());
					else
						params[4] = new GUIParameter("ocr.resolution.threshold", (String) values
								.get("ocr.resolution.threshold"));

					if (values.get("ocr.timeout") instanceof Integer)
						params[5] = new GUIParameter("ocr.timeout", ((Integer) values.get("ocr.timeout")).toString());
					else
						params[5] = new GUIParameter("ocr.timeout", (String) values.get("ocr.timeout"));

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