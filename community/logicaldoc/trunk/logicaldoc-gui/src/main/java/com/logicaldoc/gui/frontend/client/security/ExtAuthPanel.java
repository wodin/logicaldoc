package com.logicaldoc.gui.frontend.client.security;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.ValuesManager;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the LDAP and Active Directory settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class ExtAuthPanel extends VLayout {
	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs = new TabSet();

	private GUILdapSettings ldapSettings;

	private GUIADSettings adSettings;

	public ExtAuthPanel(GUILdapSettings[] settings) {
		this.ldapSettings = settings[0];
		this.adSettings = (GUIADSettings) settings[1];

		setWidth100();
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(400);
		tabs.setHeight(250);

		Tab ldap = new Tab();
		ldap.setTitle(I18N.getMessage("ldap"));

		DynamicForm ldapForm = new DynamicForm();
		ldapForm.setValuesManager(vm);
		ldapForm.setTitleOrientation(TitleOrientation.TOP);
		ldapForm.setNumCols(1);

		// Implementation
		RadioGroupItem implementation = ItemFactory.newBooleanSelector("implementation", I18N
				.getMessage("implementation"));
		implementation.setValueMap("basic", "md5");
		implementation.setValue(this.ldapSettings.getImplementation());

		// Enabled
		RadioGroupItem enabled = ItemFactory.newBooleanSelector("enabled", I18N.getMessage("enabled"));
		enabled.setValue(this.ldapSettings.isEnabled() ? "yes" : "no");

		// Url
		TextItem url = new TextItem();
		url.setTitle(I18N.getMessage("url") + "*");
		url.setValue(this.ldapSettings.getUrl());
		url.setRequired(true);

		// Username
		TextItem username = new TextItem();
		username.setTitle(I18N.getMessage("username"));
		username.setValue(this.ldapSettings.getUsername());

		// Password
		PasswordItem password = new PasswordItem("password", I18N.getMessage("password"));
		password.setValue(this.ldapSettings.getPwd());

		// Realm
		TextItem realm = new TextItem();
		realm.setTitle(I18N.getMessage("realm"));
		realm.setValue(this.ldapSettings.getRealm());

		// DN
		TextItem dn = new TextItem();
		dn.setTitle(I18N.getMessage("dn"));
		dn.setValue(this.ldapSettings.getDN());

		// Base
		TextItem base = new TextItem();
		base.setTitle(I18N.getMessage("base"));
		base.setValue(this.ldapSettings.getBase());

		// User identifier attr.
		TextItem userIdentifierAttr = new TextItem();
		userIdentifierAttr.setTitle(I18N.getMessage("useridentifierattr"));
		userIdentifierAttr.setValue(this.ldapSettings.getUserIdentifierAttr());

		// Group identifier attr.
		TextItem grpIdentifierAttr = new TextItem();
		grpIdentifierAttr.setTitle(I18N.getMessage("grpidentifierattr"));
		grpIdentifierAttr.setValue(this.ldapSettings.getGrpIdentifierAttr());

		// Logon attr.
		TextItem logonAttr = new TextItem();
		logonAttr.setTitle(I18N.getMessage("logonattr"));
		logonAttr.setValue(this.ldapSettings.getLogonAttr());

		// Auth. pattern
		TextItem authPattern = new TextItem();
		authPattern.setTitle(I18N.getMessage("authpattern"));
		authPattern.setValue(this.ldapSettings.getAuthPattern());

		// User class
		TextItem userClass = new TextItem();
		userClass.setTitle(I18N.getMessage("userclass"));
		userClass.setValue(this.ldapSettings.getUserClass());

		// Group class
		TextItem groupClass = new TextItem();
		groupClass.setTitle(I18N.getMessage("grpclass"));
		groupClass.setValue(this.ldapSettings.getGrpClass());

		// Users base node
		TextItem usersBaseNode = new TextItem();
		usersBaseNode.setTitle(I18N.getMessage("usersbasenode"));
		usersBaseNode.setValue(this.ldapSettings.getUsersBaseNode());

		// Groups base node
		TextItem groupsBaseNode = new TextItem();
		groupsBaseNode.setTitle(I18N.getMessage("grpsbasenode"));
		groupsBaseNode.setValue(this.ldapSettings.getGrpsBaseNode());

		// Language
		SelectItem language = ItemFactory.newLanguageSelector("language", false);
		language.setValue(this.ldapSettings.getLanguage());

		ldapForm.setItems(implementation, enabled, url, username, password, realm, dn, base, userIdentifierAttr,
				grpIdentifierAttr, logonAttr, authPattern, userClass, groupClass, usersBaseNode, groupsBaseNode,
				language);

		// final IntegerItem pwdSize = new IntegerItem();
		// pwdSize.setName("pwdSize");
		// pwdSize.setRequired(true);
		// pwdSize.setTitle(I18N.getMessage("passwdsize"));
		// pwdSize.setDefaultValue(settings.getPwdSize());
		// IntegerRangeValidator sizeValidator = new IntegerRangeValidator();
		// sizeValidator.setMin(4);
		// pwdSize.setValidators(sizeValidator);
		//
		// final IntegerItem pwdExp = new IntegerItem();
		// pwdExp.setName("pwdExp");
		// pwdExp.setTitle(I18N.getMessage("passwdexpiration"));
		// pwdExp.setHint(I18N.getMessage("days"));
		// pwdExp.setDefaultValue(settings.getPwdExpiration());
		// pwdExp.setWrapTitle(false);
		// pwdExp.setRequired(true);
		// IntegerRangeValidator expireValidator = new IntegerRangeValidator();
		// expireValidator.setMin(1);
		// pwdExp.setValidators(expireValidator);
		//		
		// pwdForm.setFields(pwdSize, pwdExp);
		ldap.setPane(ldapForm);

		Tab activeDir = new Tab();
		activeDir.setTitle(I18N.getMessage("activedirectory"));
		// Checks if the active directory feature is enabled
		if (Session.get().isFeatureEnabled("Feature_11")) {
			activeDir.setPane(new FeatureDisabled());
		} else {
			DynamicForm activeDirForm = new DynamicForm();
			activeDirForm.setValuesManager(vm);
			activeDirForm.setTitleOrientation(TitleOrientation.TOP);
			activeDirForm.setNumCols(1);

			activeDir.setPane(activeDirForm);
		}

		// final IntegerItem pwdSize = new IntegerItem();
		// pwdSize.setName("pwdSize");
		// pwdSize.setRequired(true);
		// pwdSize.setTitle(I18N.getMessage("passwdsize"));
		// pwdSize.setDefaultValue(settings.getPwdSize());
		// IntegerRangeValidator sizeValidator = new IntegerRangeValidator();
		// sizeValidator.setMin(4);
		// pwdSize.setValidators(sizeValidator);
		//
		// final IntegerItem pwdExp = new IntegerItem();
		// pwdExp.setName("pwdExp");
		// pwdExp.setTitle(I18N.getMessage("passwdexpiration"));
		// pwdExp.setHint(I18N.getMessage("days"));
		// pwdExp.setDefaultValue(settings.getPwdExpiration());
		// pwdExp.setWrapTitle(false);
		// pwdExp.setRequired(true);
		// IntegerRangeValidator expireValidator = new IntegerRangeValidator();
		// expireValidator.setMin(1);
		// pwdExp.setValidators(expireValidator);
		//		
		// pwdForm.setFields(pwdSize, pwdExp);

		tabs.setTabs(ldap, activeDir);

		IButton save = new IButton();
		save.setTitle(I18N.getMessage("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				// if (vm.validate()) {
				// SecuritySettingsPanel.this.settings.setPwdExpiration((Integer)
				// values.get("pwdExp"));
				// SecuritySettingsPanel.this.settings.setPwdSize((Integer)
				// values.get("pwdSize"));
				//
				// service.saveSettings(Session.get().getSid(),
				// SecuritySettingsPanel.this.settings,
				// new AsyncCallback<Void>() {
				//
				// @Override
				// public void onFailure(Throwable caught) {
				// Log.serverError(caught);
				// }
				//
				// @Override
				// public void onSuccess(Void ret) {
				// Log.info(I18N.getMessage("settingssaved"), null);
				// }
				// });
				// }
			}
		});

		setMembers(tabs, save);
	}

}
