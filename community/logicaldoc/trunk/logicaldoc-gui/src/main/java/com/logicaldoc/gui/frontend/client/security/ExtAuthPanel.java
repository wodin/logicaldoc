package com.logicaldoc.gui.frontend.client.security;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIADSettings;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.i18n.I18N;
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

		if (Feature.enabled(11))
			this.adSettings = (GUIADSettings) settings[1];
		else
			this.adSettings = new GUIADSettings();

		setWidth100();
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(400);
		tabs.setHeight(400);

		Tab ldap = new Tab();
		ldap.setTitle(I18N.message("ldap"));

		DynamicForm ldapForm = new DynamicForm();
		ldapForm.setValuesManager(vm);
		ldapForm.setTitleOrientation(TitleOrientation.TOP);
		ldapForm.setColWidths(100, 100);

		// Implementation
		RadioGroupItem implementation = ItemFactory.newBooleanSelector("implementation", "implementation");
		implementation.setName("implementation");
		implementation.setValueMap("basic", "md5");
		implementation.setValue(this.ldapSettings.getImplementation());

		// Enabled
		RadioGroupItem enabled = ItemFactory.newBooleanSelector("eenabled", "enabled");
		enabled.setValue(this.ldapSettings.isEnabled() ? "yes" : "no");

		// Url
		TextItem url = ItemFactory.newTextItem("url", "url", this.ldapSettings.getUrl());
		url.setRequired(true);

		// Username
		TextItem username = ItemFactory.newTextItem("username", "username", this.ldapSettings.getUsername());

		// Password
		PasswordItem password = new PasswordItem("password", I18N.message("password"));
		password.setName("password");
		password.setValue(this.ldapSettings.getPwd());

		// Realm
		TextItem realm = ItemFactory.newTextItem("realm", "realm", this.ldapSettings.getRealm());

		// DN
		TextItem dn = ItemFactory.newTextItem("dn", "dn", this.ldapSettings.getDN());

		// Base
		TextItem base = ItemFactory.newTextItem("base", "base", this.ldapSettings.getBase());

		// User identifier attr.
		TextItem userIdentifierAttr = ItemFactory.newTextItem("useridentifierattr", "useridentifierattr",
				this.ldapSettings.getUserIdentifierAttr());

		// Group identifier attr.
		TextItem grpIdentifierAttr = ItemFactory.newTextItem("grpidentifierattr", "grpidentifierattr",
				this.ldapSettings.getGrpIdentifierAttr());

		// Logon attr.
		TextItem logonAttr = ItemFactory.newTextItem("logonattr", "logonattr", this.ldapSettings.getLogonAttr());

		// Auth. pattern
		TextItem authPattern = ItemFactory
				.newTextItem("authpattern", "authpattern", this.ldapSettings.getAuthPattern());

		// User class
		TextItem userClass = ItemFactory.newTextItem("userclass", "userclass", this.ldapSettings.getUserClass());

		// Group class
		TextItem groupClass = ItemFactory.newTextItem("grpclass", "grpclass", this.ldapSettings.getGrpClass());

		// Users base node
		TextItem usersBaseNode = ItemFactory.newTextItem("usersbasenode", "usersbasenode", this.ldapSettings
				.getUsersBaseNode());

		// Groups base node
		TextItem groupsBaseNode = ItemFactory.newTextItem("grpsbasenode", "grpsbasenode", this.ldapSettings
				.getGrpsBaseNode());

		// Language
		SelectItem language = ItemFactory.newLanguageSelector("language", false);
		language.setName("language");
		language.setValue(this.ldapSettings.getLanguage());

		ldapForm.setItems(implementation, enabled, url, username, password, realm, dn, base, userIdentifierAttr,
				grpIdentifierAttr, logonAttr, authPattern, userClass, groupClass, usersBaseNode, groupsBaseNode,
				language);

		ldap.setPane(ldapForm);

		Tab activeDir = new Tab();
		activeDir.setTitle(I18N.message("activedirectory"));

		DynamicForm activeDirForm = new DynamicForm();
		activeDirForm.setValuesManager(vm);
		activeDirForm.setTitleOrientation(TitleOrientation.TOP);
		activeDirForm.setColWidths(100, 100);

		// Implementation
		RadioGroupItem adImplementation = ItemFactory.newBooleanSelector("adImplementation", "implementation");
		adImplementation.setName("adimplementation");
		adImplementation.setValueMap("basic", "md5");
		adImplementation.setValue(this.adSettings.getImplementation());

		// Enabled
		RadioGroupItem adEnabled = ItemFactory.newBooleanSelector("adEnabled", "enabled");
		adEnabled.setName("adEnabled");
		adEnabled.setValue(this.adSettings.isEnabled() ? "yes" : "no");

		// Domain
		TextItem domain = ItemFactory.newTextItem("domain", "domain", this.adSettings.getDomain());
		domain.setRequired(true);

		// Host
		TextItem host = ItemFactory.newTextItem("host", "host", this.adSettings.getHost());
		host.setRequired(true);

		// Port
		IntegerItem port = ItemFactory.newValidateIntegerItem("port", "port", this.adSettings.getPort(), 1, null);
		port.setRequired(true);

		// Username
		TextItem adUsername = ItemFactory.newTextItem("adUsername", "username", this.adSettings.getUsername());
		adUsername.setRequired(true);

		// Password
		PasswordItem adPassword = new PasswordItem("password", I18N.message("password"));
		adPassword.setName("adPassword");
		adPassword.setValue(this.adSettings.getPwd());

		// Users base node
		TextItem adUsersBaseNode = ItemFactory.newTextItem("adUsersBaseNode", "usersbasenode", this.adSettings
				.getUsersBaseNode());
		adUsersBaseNode.setRequired(true);

		// Groups base node
		TextItem adGroupsBaseNode = ItemFactory.newTextItem("adGroupsBaseNode", "grpsbasenode", this.adSettings
				.getGrpsBaseNode());

		// Language
		SelectItem adLanguage = ItemFactory.newLanguageSelector("language", false);
		adLanguage.setName("adLanguage");
		adLanguage.setValue(this.adSettings.getLanguage());

		activeDirForm.setItems(adImplementation, adEnabled, domain, host, port, adUsername, adPassword,
				adUsersBaseNode, adGroupsBaseNode, adLanguage);

		if (Feature.visible(Feature.LDAP)) {
			tabs.addTab(ldap);
			if (!Feature.enabled(Feature.LDAP))
				ldap.setPane(new FeatureDisabled());
			else
				ldap.setPane(ldapForm);
		}

		if (Feature.visible(Feature.ACTIVEDIR)) {
			tabs.addTab(activeDir);
			if (!Feature.enabled(Feature.ACTIVEDIR))
				activeDir.setPane(new FeatureDisabled());
			else
				activeDir.setPane(activeDirForm);
		}

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					ExtAuthPanel.this.ldapSettings.setImplementation((String) values.get("implementation"));
					ExtAuthPanel.this.ldapSettings.setEnabled(values.get("eenabled").equals("yes") ? true : false);
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
					ExtAuthPanel.this.ldapSettings.setUserClass((String) values.get("userclass"));
					ExtAuthPanel.this.ldapSettings.setGrpClass((String) values.get("grpclass"));
					ExtAuthPanel.this.ldapSettings.setUsersBaseNode((String) values.get("usersbasenode"));
					ExtAuthPanel.this.ldapSettings.setGrpsBaseNode((String) values.get("grpsbasenode"));
					ExtAuthPanel.this.ldapSettings.setLanguage((String) values.get("language"));

					// Checks if the active directory feature is enabled
					if (Feature.enabled(11)) {
						ExtAuthPanel.this.adSettings.setImplementation((String) values.get("adImplementation"));
						ExtAuthPanel.this.adSettings.setEnabled(values.get("adEnabled").equals("yes") ? true : false);
						ExtAuthPanel.this.adSettings.setDomain((String) values.get("domain"));
						ExtAuthPanel.this.adSettings.setHost((String) values.get("host"));
						if (values.get("port") instanceof Integer)
							ExtAuthPanel.this.adSettings.setPort((Integer) values.get("port"));
						ExtAuthPanel.this.adSettings.setUsername((String) values.get("adUsername"));
						ExtAuthPanel.this.adSettings.setPwd((String) values.get("adPassword"));
						ExtAuthPanel.this.adSettings.setUsersBaseNode((String) values.get("adUsersBaseNode"));
						ExtAuthPanel.this.adSettings.setGrpsBaseNode((String) values.get("adGroupsBaseNode"));
						ExtAuthPanel.this.adSettings.setLanguage((String) values.get("adLanguage"));
					}

					service.saveExtAuthSettings(Session.get().getSid(), ExtAuthPanel.this.ldapSettings,
							ExtAuthPanel.this.adSettings, new AsyncCallback<Void>() {

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

		if (Feature.enabled(Feature.LDAP) || Feature.enabled(Feature.ACTIVEDIR))
			setMembers(tabs, save);
		else
			setMembers(tabs);
	}
}