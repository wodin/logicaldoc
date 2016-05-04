package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the Antivirus settings
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
public class AntivirusSettingsPanel extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	public AntivirusSettingsPanel(GUIParameter[] settings) {
		setWidth100();
		setMembersMargin(10);
		setMargin(30);

		final DynamicForm form = new DynamicForm();
		form.setTitleOrientation(TitleOrientation.LEFT);
		form.setGroupTitle(I18N.message("antivirus"));
		form.setIsGroup(true);
		form.setAlign(Alignment.LEFT);
		form.setWidth(470);
		form.setPadding(5);

		RadioGroupItem enabled = ItemFactory.newBooleanSelector("enabled", I18N.message("enabled"));
		enabled.setWrapTitle(false);
		enabled.setRequired(true);

		TextItem command = ItemFactory.newTextItem("command", "ClamAV", null);
		command.setWidth(400);

		TextItem includes = ItemFactory.newTextItem("includes", "include", null);
		includes.setWidth(400);

		TextItem excludes = ItemFactory.newTextItem("excludes", "exclude", null);
		excludes.setWidth(400);

		TextItem timeout = ItemFactory.newSpinnerItem("timeout", "timeout", (Integer)null);
		timeout.setHint(I18N.message("seconds"));

		for (GUIParameter setting : settings) {
			if ((Session.get().getTenantName() + ".antivirus.enabled").equals(setting.getName()))
				enabled.setValue("true".equals(setting.getValue()) ? "yes" : "no");
			else if ("antivirus.command".equals(setting.getName()))
				command.setValue(setting.getValue());
			else if ((Session.get().getTenantName() + ".antivirus.excludes").equals(setting.getName()))
				excludes.setValue(setting.getValue());
			else if ((Session.get().getTenantName() + ".antivirus.includes").equals(setting.getName()))
				includes.setValue(setting.getValue());
			else if ((Session.get().getTenantName() + ".antivirus.timeout").equals(setting.getName()))
				timeout.setValue(Integer.parseInt(setting.getValue()));
		}

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (form.validate()) {
					GUIParameter[] params = new GUIParameter[Session.get().isDefaultTenant() ? 4 : 3];
					params[0] = new GUIParameter(Session.get().getTenantName() + ".antivirus.enabled", ""
							+ ("yes".equals(form.getValueAsString("enabled"))));
					params[1] = new GUIParameter(Session.get().getTenantName() + ".antivirus.excludes", form
							.getValueAsString("excludes").trim());
					params[2] = new GUIParameter(Session.get().getTenantName() + ".antivirus.includes", form
							.getValueAsString("includes").trim());
					params[3] = new GUIParameter(Session.get().getTenantName() + ".antivirus.timeout", form
							.getValueAsString("timeout").trim());

					if (Session.get().isDefaultTenant())
						params[4] = new GUIParameter("antivirus.command", form.getValueAsString("command").trim());

					service.saveSettings(params, new AsyncCallback<Void>() {

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
		if (Session.get().isDefaultTenant())
			form.setFields(enabled, command, includes, excludes, timeout);
		else
			form.setFields(enabled, includes, excludes, timeout);
		setMembers(form, save);
	}
}
