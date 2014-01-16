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
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the System Quota settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class SystemQuotaPanel extends VLayout {

	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	private GUIParameter[] settings;

	public SystemQuotaPanel(GUIParameter[] params) {
		this.settings = params;
		setWidth100();
		setHeight100();
		setMembersMargin(5);
		setMargin(5);

		TabSet tabs = new TabSet();
		tabs.setWidth100();
		tabs.setHeight100();
		Tab systemQuota = new Tab();
		systemQuota.setTitle(I18N.message("docsquota"));

		DynamicForm systemQuotaForm = new DynamicForm();
		systemQuotaForm.setValuesManager(vm);
		systemQuotaForm.setTitleOrientation(TitleOrientation.TOP);
		systemQuotaForm.setNumCols(1);

		final IntegerItem quotaSize = ItemFactory.newIntegerItem("quota", "quota", null);
		if (!settings[0].getValue().isEmpty())
			quotaSize.setDefaultValue(Integer.parseInt(settings[0].getValue()));
		quotaSize.setRequired(true);
		quotaSize.setWrapTitle(false);
		quotaSize.setHint("MB");

		final IntegerItem quotaThreshold = ItemFactory.newIntegerItem("quotaThreshold", "threshold", null);
		if (!settings[1].getValue().isEmpty())
			quotaThreshold.setDefaultValue(Integer.parseInt(settings[1].getValue()));
		quotaThreshold.setRequired(true);
		quotaThreshold.setWrapTitle(false);
		quotaThreshold.setHint("MB");

		systemQuotaForm.setFields(quotaSize, quotaThreshold);
		systemQuota.setPane(systemQuotaForm);
		tabs.setTabs(systemQuota);

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					String quota = "";
					String quotaThreshold = "";
					if (values.get("quota") instanceof String)
						quota = (String) values.get("quota");
					else
						quota = values.get("quota").toString();

					if (values.get("quotaThreshold") instanceof String)
						quotaThreshold = (String) values.get("quotaThreshold");
					else
						quotaThreshold = values.get("quotaThreshold").toString();

					// Checks if quotaThreshold value is greater than quota
					// value
					if (Integer.parseInt(quotaThreshold) > Integer.parseInt(quota)) {
						Log.warn(I18N.message("quotavaluescheck"), "");
						return;
					}

					SystemQuotaPanel.this.settings[0].setValue(quota);
					SystemQuotaPanel.this.settings[1].setValue(quotaThreshold);

					service.saveQuotaSettings(Session.get().getSid(), SystemQuotaPanel.this.settings,
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
		save.setDisabled(Session.get().isDemo() && Session.get().getUser().getId() == 1);

		setMembers(tabs, save);
	}
}
