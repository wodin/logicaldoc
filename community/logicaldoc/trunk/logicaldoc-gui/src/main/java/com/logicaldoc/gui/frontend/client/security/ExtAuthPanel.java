package com.logicaldoc.gui.frontend.client.security;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.log.Log;
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
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
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
		tabs.setHeight(400);

		Tab ldap = new Tab();
		ldap.setTitle(I18N.getMessage("ldap"));

		DynamicForm ldapForm = new DynamicForm();
		ldapForm.setValuesManager(vm);
		ldapForm.setTitleOrientation(TitleOrientation.TOP);
		ldapForm.setColWidths(100, 100);

		// Implementation
		RadioGroupItem implementation = ItemFactory.newBooleanSelector("implementation", I18N
				.getMessage("implementation"));
		implementation.setName("implementation");
		implementation.setValueMap("basic", "md5");
		implementation.setValue(this.ldapSettings.getImplementation());

		// Enabled
		RadioGroupItem enabled = ItemFactory.newBooleanSelector("enabled", I18N.getMessage("enabled"));
		enabled.setName("enabled");
		enabled.setValue(this.ldapSettings.isEnabled() ? "yes" : "no");

		// Url
		TextItem url = new TextItem();
		url.setName("url");
		url.setTitle(I18N.getMessage("url"));
		url.setValue(this.ldapSettings.getUrl());
		url.setRequired(true);

		// Username
		TextItem username = new TextItem();
		username.setName("username");
		username.setTitle(I18N.getMessage("username"));
		username.setValue(this.ldapSettings.getUsername());

		// Password
		PasswordItem password = new PasswordItem("password", I18N.getMessage("password"));
		password.setName("password");
		password.setValue(this.ldapSettings.getPwd());

		// Realm
		TextItem realm = new TextItem();
		realm.setName("realm");
		realm.setTitle(I18N.getMessage("realm"));
		realm.setValue(this.ldapSettings.getRealm());

		// DN
		TextItem dn = new TextItem();
		dn.setName("dn");
		dn.setTitle(I18N.getMessage("dn"));
		dn.setValue(this.ldapSettings.getDN());

		// Base
		TextItem base = new TextItem();
		base.setName("base");
		base.setTitle(I18N.getMessage("base"));
		base.setValue(this.ldapSettings.getBase());

		// User identifier attr.
		TextItem userIdentifierAttr = new TextItem();
		userIdentifierAttr.setName("useridentifierattr");
		userIdentifierAttr.setTitle(I18N.getMessage("useridentifierattr"));
		userIdentifierAttr.setValue(this.ldapSettings.getUserIdentifierAttr());

		// Group identifier attr.
		TextItem grpIdentifierAttr = new TextItem();
		grpIdentifierAttr.setName("grpidentifierattr");
		grpIdentifierAttr.setTitle(I18N.getMessage("grpidentifierattr"));
		grpIdentifierAttr.setValue(this.ldapSettings.getGrpIdentifierAttr());

		// Logon attr.
		TextItem logonAttr = new TextItem();
		logonAttr.setName("logonattr");
		logonAttr.setTitle(I18N.getMessage("logonattr"));
		logonAttr.setValue(this.ldapSettings.getLogonAttr());

		// Auth. pattern
		TextItem authPattern = new TextItem();
		authPattern.setName("authpattern");
		authPattern.setTitle(I18N.getMessage("authpattern"));
		authPattern.setValue(this.ldapSettings.getAuthPattern());

		// User class
		TextItem userClass = new TextItem();
		userClass.setName("userclass");
		userClass.setTitle(I18N.getMessage("userclass"));
		userClass.setValue(this.ldapSettings.getUserClass());

		// Group class
		TextItem groupClass = new TextItem();
		groupClass.setName("grpclass");
		groupClass.setTitle(I18N.getMessage("grpclass"));
		groupClass.setValue(this.ldapSettings.getGrpClass());

		// Users base node
		TextItem usersBaseNode = new TextItem();
		usersBaseNode.setName("usersbasenode");
		usersBaseNode.setTitle(I18N.getMessage("usersbasenode"));
		usersBaseNode.setValue(this.ldapSettings.getUsersBaseNode());

		// Groups base node
		TextItem groupsBaseNode = new TextItem();
		groupsBaseNode.setName("grpsbasenode");
		groupsBaseNode.setTitle(I18N.getMessage("grpsbasenode"));
		groupsBaseNode.setValue(this.ldapSettings.getGrpsBaseNode());

		// Language
		SelectItem language = ItemFactory.newLanguageSelector("language", false);
		language.setName("language");
		language.setValue(this.ldapSettings.getLanguage());

		ldapForm.setItems(implementation, enabled, url, username, password, realm, dn, base, userIdentifierAttr,
				grpIdentifierAttr, logonAttr, authPattern, userClass, groupClass, usersBaseNode, groupsBaseNode,
				language);

		ldap.setPane(ldapForm);

		Tab activeDir = new Tab();
		activeDir.setTitle(I18N.getMessage("activedirectory"));
		// Checks if the active directory feature is enabled
		if (!Session.get().isFeatureEnabled("Feature_11")) {
			activeDir.setPane(new FeatureDisabled());
		} else {
			DynamicForm activeDirForm = new DynamicForm();
			activeDirForm.setValuesManager(vm);
			activeDirForm.setTitleOrientation(TitleOrientation.TOP);
			activeDirForm.setColWidths(100, 100);

			// Implementation
			RadioGroupItem adImplementation = ItemFactory.newBooleanSelector("adimplementation", I18N
					.getMessage("implementation"));
			adImplementation.setName("adimplementation");
			adImplementation.setValueMap("basic", "md5");
			adImplementation.setValue(this.adSettings.getImplementation());

			// Enabled
			RadioGroupItem adEnabled = ItemFactory.newBooleanSelector("adenabled", I18N.getMessage("enabled"));
			adEnabled.setName("adEnabled");
			adEnabled.setValue(this.adSettings.isEnabled() ? "yes" : "no");

			// Domain
			TextItem domain = new TextItem();
			domain.setName("domain");
			domain.setTitle(I18N.getMessage("domain"));
			domain.setValue(this.adSettings.getDomain());
			domain.setRequired(true);

			// Host
			TextItem host = new TextItem();
			host.setName("host");
			host.setTitle(I18N.getMessage("host"));
			host.setValue(this.adSettings.getHost());
			host.setRequired(true);

			// Port
			IntegerItem port = new IntegerItem();
			port.setName("port");
			port.setTitle(I18N.getMessage("port"));
			port.setValue(this.adSettings.getPort());
			port.setRequired(true);
			IntegerRangeValidator portValidator = new IntegerRangeValidator();
			portValidator.setMin(1);
			port.setValidators(portValidator);

			// Username
			TextItem adUsername = new TextItem();
			adUsername.setName("adUsername");
			adUsername.setTitle(I18N.getMessage("username"));
			adUsername.setValue(this.adSettings.getUsername());
			adUsername.setRequired(true);

			// Password
			PasswordItem adPassword = new PasswordItem("password", I18N.getMessage("password"));
			adPassword.setName("adPassword");
			adPassword.setValue(this.adSettings.getPwd());

			// Users base node
			TextItem adUsersBaseNode = new TextItem();
			adUsersBaseNode.setName("adUsersBaseNode");
			adUsersBaseNode.setTitle(I18N.getMessage("usersbasenode"));
			adUsersBaseNode.setValue(this.adSettings.getUsersBaseNode());
			adUsersBaseNode.setRequired(true);

			// Groups base node
			TextItem adGroupsBaseNode = new TextItem();
			adGroupsBaseNode.setName("adGroupsBaseNode");
			adGroupsBaseNode.setTitle(I18N.getMessage("grpsbasenode"));
			adGroupsBaseNode.setValue(this.adSettings.getGrpsBaseNode());

			// Language
			SelectItem adLanguage = ItemFactory.newLanguageSelector("language", false);
			adLanguage.setName("adLanguage");
			adLanguage.setValue(this.adSettings.getLanguage());

			activeDirForm.setItems(adImplementation, adEnabled, domain, host, port, adUsername, adPassword,
					adUsersBaseNode, adGroupsBaseNode, adLanguage);

			activeDir.setPane(activeDirForm);
		}

		tabs.setTabs(ldap, activeDir);

		IButton save = new IButton();
		save.setTitle(I18N.getMessage("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					ExtAuthPanel.this.ldapSettings.setImplementation((String) values.get("implementation"));
					ExtAuthPanel.this.ldapSettings.setEnabled(values.get("enabled").equals("yes") ? true : false);
					ExtAuthPanel.this.ldapSettings.setUrl((String) values.get("url"));
					ExtAuthPanel.this.ldapSettings.setUsername((String) values.get("username"));
					ExtAuthPanel.this.ldapSettings.setPwd((String) values.get("password"));
					ExtAuthPanel.this.ldapSettings.setRealm((String) values.get("realm"));
					ExtAuthPanel.this.ldapSettings.setDN((String) values.get("dn"));
					ExtAuthPanel.this.ldapSettings.setBase((String) values.get("base"));
					ExtAuthPanel.this.ldapSettings.setUserIdentifierAttr((String) values.get("useridentifierattr"));
					ExtAuthPanel.this.ldapSettings.setGrpIdentifierAttr((String) values.get("grpidentifierattr"));
					ExtAuthPanel.this.ldapSettings.setLogonAttr((String) values.get("logonattr"));
					ExtAuthPanel.this.ldapSettings.setAuthPattern((String) values.get("authpattern"));
					ExtAuthPanel.this.ldapSettings.setUserClass((String) values.get("userClass"));
					ExtAuthPanel.this.ldapSettings.setGrpClass((String) values.get("grpclass"));
					ExtAuthPanel.this.ldapSettings.setUsersBaseNode((String) values.get("usersbasenode"));
					ExtAuthPanel.this.ldapSettings.setGrpsBaseNode((String) values.get("grpsbasenode"));
					ExtAuthPanel.this.ldapSettings.setLanguage((String) values.get("language"));

					ExtAuthPanel.this.adSettings.setImplementation((String) values.get("adImplementation"));
					ExtAuthPanel.this.adSettings.setEnabled(values.get("adEnabled").equals("yes") ? true : false);
					ExtAuthPanel.this.adSettings.setDomain((String) values.get("domain"));
					ExtAuthPanel.this.adSettings.setHost((String) values.get("host"));
					if (values.get("port") instanceof Integer)
						ExtAuthPanel.this.adSettings.setPort((Integer) values.get("port"));
					ExtAuthPanel.this.adSettings.setUsername((String) values.get("adUsername"));
					ExtAuthPanel.this.adSettings.setPwd((String) values.get("adPassword"));
					ExtAuthPanel.this.adSettings.setUsersBaseNode((String) values.get("adUsersbasenode"));
					ExtAuthPanel.this.adSettings.setGrpsBaseNode((String) values.get("adGrpsbasenode"));
					ExtAuthPanel.this.adSettings.setLanguage((String) values.get("adLanguage"));

					service.saveExtAuthSettings(Session.get().getSid(), ExtAuthPanel.this.ldapSettings,
							ExtAuthPanel.this.adSettings, new AsyncCallback<Void>() {

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
}
