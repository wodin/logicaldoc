package com.logicaldoc.gui.frontend.client.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
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

	private DynamicForm anonymousForm;

	private FiltersPanel filtersPanel = new FiltersPanel();

	public SecuritySettingsPanel(GUISecuritySettings settings) {
		this.settings = settings;
		setWidth100();
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(510);
		tabs.setHeight(350);

		Tab menues = new Tab();
		menues.setTitle(I18N.message("menues"));
		menues.setPane(new MenuesPanel());

		Tab filters = new Tab();
		filters.setTitle(I18N.message("filters"));
		if (Feature.enabled(Feature.IP_FILTERS))
			filters.setPane(filtersPanel);
		else
			filters.setPane(new FeatureDisabled());

		Tab password = new Tab();
		password.setTitle(I18N.message("password"));

		DynamicForm pwdForm = new DynamicForm();
		pwdForm.setValuesManager(vm);
		pwdForm.setTitleOrientation(TitleOrientation.TOP);
		pwdForm.setNumCols(1);

		final IntegerItem pwdSize = ItemFactory.newValidateIntegerItem("pwdSize", "passwdsize", null, 4, null);
		pwdSize.setRequired(true);
		pwdSize.setValue(settings.getPwdSize());

		final IntegerItem pwdExp = ItemFactory.newValidateIntegerItem("pwdExp", "passwdexpiration", null, 1, null);
		pwdExp.setHint(I18N.message("days"));
		pwdExp.setValue(settings.getPwdExpiration());
		pwdExp.setWrapTitle(false);
		pwdExp.setRequired(true);

		final RadioGroupItem savelogin = ItemFactory.newBooleanSelector("savelogin", I18N.message("savelogin"));
		savelogin.setHint(I18N.message("saveloginhint"));
		savelogin.setValue(settings.isSaveLogin() ? "yes" : "no");
		savelogin.setWrapTitle(false);
		savelogin.setRequired(true);

		pwdForm.setFields(pwdSize, pwdExp, savelogin);
		password.setPane(pwdForm);

		Tab anonymous = prepareAnonymousTab(settings);

		notifications.setTitle(I18N.message("notifications"));
		notifications.setPane(notificationsPane);
		refreshNotifications();

		if (Feature.visible(Feature.IP_FILTERS))
			tabs.setTabs(password, anonymous, filters, menues, notifications);
		else
			tabs.setTabs(password, anonymous, menues, notifications);

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				vm.validate();

				final Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					SecuritySettingsPanel.this.settings.setPwdExpiration((Integer) values.get("pwdExp"));
					SecuritySettingsPanel.this.settings.setPwdSize((Integer) values.get("pwdSize"));
					SecuritySettingsPanel.this.settings.setSaveLogin(values.get("savelogin").equals("yes") ? true
							: false);
					SecuritySettingsPanel.this.settings.setEnableAnonymousLogin(values.get("enableanonymous").equals(
							"yes") ? true : false);
					if (!SecuritySettingsPanel.this.settings.isEnableAnonymousLogin())
						SecuritySettingsPanel.this.settings.setAnonymousUser(null);
					else if (SecuritySettingsPanel.this.settings.getAnonymousUser() == null) {
						SC.warn(I18N.message("selectanonymoususer"));
						return;
					}

					service.saveSettings(Session.get().getSid(), SecuritySettingsPanel.this.settings,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Void ret) {
									Log.info(
											I18N.message("settingssaved") + "  "
													+ I18N.message("settingsaffectnewsessions"), null);
								}
							});
				}

				if (Feature.enabled(Feature.IP_FILTERS)) {
					filtersPanel.save();
				}
			}
		});

		setMembers(tabs, save);
	}

	private Tab prepareAnonymousTab(GUISecuritySettings settings) {
		Tab anonymous = new Tab(I18N.message("anonymous"));

		anonymousForm = new DynamicForm();
		anonymousForm.setValuesManager(vm);
		anonymousForm.setTitleOrientation(TitleOrientation.TOP);
		anonymousForm.setNumCols(1);
		final RadioGroupItem enableAnonymous = ItemFactory.newBooleanSelector("enableanonymous",
				I18N.message("enableanonymous"));
		enableAnonymous.setValue(settings.isSaveLogin() ? "yes" : "no");
		enableAnonymous.setWrapTitle(false);
		enableAnonymous.setRequired(true);

		final SelectItem user = ItemFactory.newUserSelector("anonymousUser", "user", null);
		user.setHint(I18N.message("anonymoususerhint"));
		user.setHintStyle("hint");
		user.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				if (user.getSelectedRecord() == null) {
					SecuritySettingsPanel.this.settings.setAnonymousUser(null);
				} else {
					GUIUser u = new GUIUser();
					u.setId(Long.parseLong(user.getSelectedRecord().getAttribute("id")));
					u.setUserName(user.getSelectedRecord().getAttribute("username"));
					SecuritySettingsPanel.this.settings.setAnonymousUser(u);
				}
			}
		});
		if (SecuritySettingsPanel.this.settings.getAnonymousUser() != null)
			user.setValue(Long.toString(SecuritySettingsPanel.this.settings.getAnonymousUser().getId()));

		anonymousForm.setItems(enableAnonymous, user);
		anonymous.setPane(anonymousForm);
		return anonymous;
	}

	private void refreshNotifications() {
		if (notificationsForm != null && notificationsPane.contains(notificationsForm)) {
			notificationsPane.removeMember(notificationsForm);
			notificationsForm.destroy();
		}

		notificationsForm = new DynamicForm();
		notificationsForm.setColWidths(1, "*");
		notificationsForm.setMargin(3);

		final SelectItem user = ItemFactory.newUserSelector("notificationUsers", "user", null);
		List<FormItem> items = new ArrayList<FormItem>();
		user.setHint(I18N.message("usernotification"));
		user.setHintStyle("hint");
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

		FormItemIcon icon = ItemFactory.newItemIcon("delete.png");
		int i = 0;

		for (GUIUser u : settings.getNotifiedUsers()) {
			final StaticTextItem usrItem = ItemFactory.newStaticTextItem("usr" + i++, "user", null);
			usrItem.setValue(u.getUserName());
			usrItem.setIcons(icon);
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
