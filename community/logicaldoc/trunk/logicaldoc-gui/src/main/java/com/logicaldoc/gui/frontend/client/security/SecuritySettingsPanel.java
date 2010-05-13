package com.logicaldoc.gui.frontend.client.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the password and notification settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SecuritySettingsPanel extends VLayout {
	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs = new TabSet();

	private GUISecuritySettings settings;

	private Tab notifications = new Tab();

	private VLayout notificationsPane = new VLayout();

	private DynamicForm notificationsForm;

	public SecuritySettingsPanel(GUISecuritySettings settings) {
		this.settings = settings;
		setWidth100();
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(400);
		tabs.setHeight(250);

		Tab password = new Tab();
		password.setTitle(I18N.getMessage("password"));

		DynamicForm pwdForm = new DynamicForm();
		pwdForm.setValuesManager(vm);
		pwdForm.setTitleOrientation(TitleOrientation.TOP);
		pwdForm.setNumCols(1);

		final IntegerItem pwdSize = new IntegerItem();
		pwdSize.setName("pwdSize");
		pwdSize.setRequired(true);
		pwdSize.setTitle(I18N.getMessage("passwdsize"));
		pwdSize.setDefaultValue(settings.getPwdSize());
		IntegerRangeValidator sizeValidator = new IntegerRangeValidator();
		sizeValidator.setMin(4);
		pwdSize.setValidators(sizeValidator);

		final IntegerItem pwdExp = new IntegerItem();
		pwdExp.setName("pwdExp");
		pwdExp.setTitle(I18N.getMessage("passwdexpiration"));
		pwdExp.setHint(I18N.getMessage("days"));
		pwdExp.setDefaultValue(settings.getPwdExpiration());
		pwdExp.setWrapTitle(false);
		pwdExp.setRequired(true);
		IntegerRangeValidator expireValidator = new IntegerRangeValidator();
		expireValidator.setMin(1);
		pwdExp.setValidators(expireValidator);

		pwdForm.setFields(pwdSize, pwdExp);
		password.setPane(pwdForm);

		notifications.setTitle(I18N.getMessage("notifications"));
		notifications.setPane(notificationsPane);

		refreshNotifications();

		tabs.setTabs(password, notifications);

		IButton save = new IButton();
		save.setTitle(I18N.getMessage("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				vm.validate();
				if (notificationsForm.hasErrors()) {
					tabs.selectTab(1);
				} else {
					tabs.selectTab(0);
				}

				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					SecuritySettingsPanel.this.settings.setPwdExpiration((Integer) values.get("pwdExp"));
					SecuritySettingsPanel.this.settings.setPwdSize((Integer) values.get("pwdSize"));

					service.saveSettings(Session.get().getSid(), SecuritySettingsPanel.this.settings,
							new AsyncCallback<Void>() {

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

	private void refreshNotifications() {
		if (notificationsForm != null && notificationsPane.contains(notificationsForm)) {
			notificationsPane.removeMember(notificationsForm);
			notificationsForm.destroy();
		}

		notificationsForm = new DynamicForm();
		notificationsForm.setColWidths(1, "*");
		notificationsForm.setMargin(3);

		final ComboBoxItem user = ItemFactory.newUserSelector("notificationUsers", I18N.getMessage("user"));
		List<FormItem> items = new ArrayList<FormItem>();
		user.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (user.getSelectedRecord() == null)
					return;
				GUIUser u = new GUIUser();
				u.setId(Long.parseLong(user.getSelectedRecord().getAttribute("id")));
				u.setUserName(user.getSelectedRecord().getAttribute("username"));
				settings.addNotifiedUser(u);
				user.clearValue();
				refreshNotifications();
			}

		});
		items.add(user);

		FormItemIcon icon = new FormItemIcon();
		icon.setSrc(Util.imageUrl("application/delete.png"));
		int i = 0;

		for (GUIUser u : settings.getNotifiedUsers()) {
			final StaticTextItem usrItem = new StaticTextItem();
			usrItem.setDefaultValue(u.getUserName());
			usrItem.setName("usr" + i++);
			usrItem.setIcons(icon);
			usrItem.setTitle(I18N.getMessage("user"));
			usrItem.addIconClickHandler(new IconClickHandler() {
				public void onIconClick(IconClickEvent event) {
					settings.removeNotifiedUser((String) usrItem.getValue());
					refreshNotifications();
				}
			});
			items.add(usrItem);
		}

		notificationsForm.setItems(items.toArray(new FormItem[0]));

		notificationsPane.setMembers(notificationsForm);
	}
}
