package com.logicaldoc.gui.frontend.client.security;

import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.services.LdapService;
import com.logicaldoc.gui.frontend.client.services.LdapServiceAsync;
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
public class LdapPanel extends VLayout {
	private LdapServiceAsync service = (LdapServiceAsync) GWT.create(LdapService.class);

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs = new TabSet();

	private GUILdapSettings ldapSettings;

	public LdapPanel(GUILdapSettings settings) {
		this.ldapSettings = settings;

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
		TextItem usersBaseNode = ItemFactory.newTextItem("usersbasenode", "usersbasenode",
				this.ldapSettings.getUsersBaseNode());

		// Groups base node
		TextItem groupsBaseNode = ItemFactory.newTextItem("grpsbasenode", "grpsbasenode",
				this.ldapSettings.getGrpsBaseNode());

		// Language
		SelectItem language = ItemFactory.newLanguageSelector("language", false, true);
		language.setName("language");
		language.setValue(this.ldapSettings.getLanguage());

		ldapForm.setItems(implementation, enabled, url, username, password, realm, dn, base, userIdentifierAttr,
				grpIdentifierAttr, logonAttr, authPattern, userClass, groupClass, usersBaseNode, groupsBaseNode,
				language);

		ldap.setPane(ldapForm);

		if (Feature.visible(Feature.LDAP)) {
			tabs.addTab(ldap);
			if (!Feature.enabled(Feature.LDAP))
				ldap.setPane(new FeatureDisabled());
			else
				ldap.setPane(ldapForm);
		}

		IButton save = new IButton();
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					LdapPanel.this.ldapSettings.setImplementation((String) values.get("implementation"));
					LdapPanel.this.ldapSettings.setEnabled(values.get("eenabled").equals("yes") ? true : false);
					LdapPanel.this.ldapSettings.setUrl((String) values.get("url"));
					LdapPanel.this.ldapSettings.setUsername((String) values.get("username"));
					LdapPanel.this.ldapSettings.setPwd((String) values.get("password"));
					LdapPanel.this.ldapSettings.setRealm((String) values.get("realm"));
					LdapPanel.this.ldapSettings.setDN((String) values.get("dn"));
					LdapPanel.this.ldapSettings.setBase((String) values.get("base"));
					LdapPanel.this.ldapSettings.setUserIdentifierAttr((String) values.get("useridentifierattr"));
					LdapPanel.this.ldapSettings.setGrpIdentifierAttr((String) values.get("grpidentifierattr"));
					LdapPanel.this.ldapSettings.setLogonAttr((String) values.get("logonattr"));
					LdapPanel.this.ldapSettings.setAuthPattern((String) values.get("authpattern"));
					LdapPanel.this.ldapSettings.setUserClass((String) values.get("userclass"));
					LdapPanel.this.ldapSettings.setGrpClass((String) values.get("grpclass"));
					LdapPanel.this.ldapSettings.setUsersBaseNode((String) values.get("usersbasenode"));
					LdapPanel.this.ldapSettings.setGrpsBaseNode((String) values.get("grpsbasenode"));
					LdapPanel.this.ldapSettings.setLanguage((String) values.get("language"));

					service.saveSettings(Session.get().getSid(), LdapPanel.this.ldapSettings,
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

		if (Feature.enabled(Feature.LDAP) || Feature.enabled(Feature.ACTIVEDIR))
			setMembers(tabs, save);
		else
			setMembers(tabs);
	}
}