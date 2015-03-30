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
import com.logicaldoc.gui.common.client.util.LD;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.logicaldoc.gui.frontend.client.services.LdapService;
import com.logicaldoc.gui.frontend.client.services.LdapServiceAsync;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
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
import com.smartgwt.client.widgets.layout.HLayout;
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

	private LdapBrowser browser = new LdapBrowser();

	public LdapPanel(GUILdapSettings settings) {
		this.ldapSettings = settings;

		setWidth100();
		setMembersMargin(5);
		setMargin(20);

		tabs.setWidth(620);
		tabs.setHeight(400);

		Tab ldapTab = new Tab();
		ldapTab.setTitle(I18N.message("ldap"));

		Tab browserTab = new Tab();
		browserTab.setTitle(I18N.message("browser"));

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
		enabled.setCellStyle("warn");

		// Anonymous Login
		RadioGroupItem anon = ItemFactory.newBooleanSelector("anon", "anonymous");
		anon.setValue(this.ldapSettings.isAnonymous() ? "yes" : "no");

		// Url
		TextItem url = ItemFactory.newTextItem("url", "url", this.ldapSettings.getUrl());
		url.setRequired(true);
		url.setCellStyle("warn");
		url.setWidth(300);

		// Username
		TextItem username = ItemFactory.newTextItem("username", "user", this.ldapSettings.getUsername());
		username.setCellStyle("warn");
		username.setWidth(300);

		// Password
		PasswordItem password = new PasswordItem("password", I18N.message("password"));
		password.setName("password");
		password.setValue(this.ldapSettings.getPwd());
		password.setCellStyle("warn");
		password.setWidth(300);

		// Realm
		TextItem realm = ItemFactory.newTextItem("realm", "realm", this.ldapSettings.getRealm());
		realm.setWidth(300);
		
		// User identifier attr.
		TextItem userIdentifierAttr = ItemFactory.newTextItem("useridentifierattr", "useridentifierattr",
				this.ldapSettings.getUserIdentifierAttr());
		userIdentifierAttr.setWidth(300);

		// Group identifier attr.
		TextItem grpIdentifierAttr = ItemFactory.newTextItem("grpidentifierattr", "grpidentifierattr",
				this.ldapSettings.getGrpIdentifierAttr());
		grpIdentifierAttr.setWidth(300);
		
		// Logon attr.
		TextItem logonAttr = ItemFactory.newTextItem("logonattr", "logonattr", this.ldapSettings.getLogonAttr());
		logonAttr.setWidth(300);
		
		// User class
		TextItem userClass = ItemFactory.newTextItem("userclass", "userclass", this.ldapSettings.getUserClass());
		userClass.setWidth(300);
		
		// Group class
		TextItem groupClass = ItemFactory.newTextItem("grpclass", "grpclass", this.ldapSettings.getGrpClass());
		groupClass.setWidth(300);
		
		// Users base node
		TextItem usersBaseNode = ItemFactory.newTextItem("usersbasenode", "usersbasenode",
				this.ldapSettings.getUsersBaseNode());
		usersBaseNode.setWidth(300);
		
		// Groups base node
		TextItem groupsBaseNode = ItemFactory.newTextItem("grpsbasenode", "grpsbasenode",
				this.ldapSettings.getGrpsBaseNode());
		groupsBaseNode.setWidth(300);
		
		// Page size
		IntegerItem pageSize = ItemFactory.newIntegerItem("pagesize", "pagesize", this.ldapSettings.getPageSize());
		pageSize.setRequired(true);

		// Language
		SelectItem language = ItemFactory.newLanguageSelector("language", false, true);
		language.setName("language");
		language.setValue(this.ldapSettings.getLanguage());

		ldapForm.setItems(enabled, url, username, password, implementation, anon, language, realm, userIdentifierAttr,
				grpIdentifierAttr, userClass, groupClass, usersBaseNode, groupsBaseNode, logonAttr, pageSize);

		ldapTab.setPane(ldapForm);

		if (Feature.visible(Feature.LDAP)) {
			tabs.addTab(ldapTab);
			tabs.addTab(browserTab);
			if (!Feature.enabled(Feature.LDAP)) {
				ldapTab.setPane(new FeatureDisabled());
				browserTab.setPane(new FeatureDisabled());
			} else {
				ldapTab.setPane(ldapForm);
				browserTab.setPane(browser);
			}
		}

		IButton save = new IButton();
		save.setAutoFit(true);
		save.setTitle(I18N.message("save"));
		save.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					LdapPanel.this.ldapSettings.setImplementation((String) values.get("implementation"));
					LdapPanel.this.ldapSettings.setEnabled(values.get("eenabled").equals("yes") ? true : false);
					LdapPanel.this.ldapSettings.setAnonymous(values.get("anon").equals("yes") ? true : false);
					LdapPanel.this.ldapSettings.setUrl((String) values.get("url"));
					LdapPanel.this.ldapSettings.setUsername((String) values.get("username"));
					LdapPanel.this.ldapSettings.setPwd((String) values.get("password"));
					LdapPanel.this.ldapSettings.setRealm((String) values.get("realm"));
					LdapPanel.this.ldapSettings.setUserIdentifierAttr((String) values.get("useridentifierattr"));
					LdapPanel.this.ldapSettings.setGrpIdentifierAttr((String) values.get("grpidentifierattr"));
					LdapPanel.this.ldapSettings.setLogonAttr((String) values.get("logonattr"));
					LdapPanel.this.ldapSettings.setUserClass((String) values.get("userclass"));
					LdapPanel.this.ldapSettings.setGrpClass((String) values.get("grpclass"));
					LdapPanel.this.ldapSettings.setUsersBaseNode((String) values.get("usersbasenode"));
					LdapPanel.this.ldapSettings.setGrpsBaseNode((String) values.get("grpsbasenode"));
					LdapPanel.this.ldapSettings.setLanguage((String) values.get("language"));
					LdapPanel.this.ldapSettings.setPageSize(Integer.parseInt(values.get("pagesize").toString()));

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

		IButton test = new IButton();
		test.setAutoFit(true);
		test.setTitle(I18N.message("testconnection"));
		test.addClickHandler(new ClickHandler() {
			@SuppressWarnings("unchecked")
			public void onClick(ClickEvent event) {
				Map<String, Object> values = (Map<String, Object>) vm.getValues();

				if (vm.validate()) {
					LdapPanel.this.ldapSettings.setImplementation((String) values.get("implementation"));
					LdapPanel.this.ldapSettings.setEnabled(values.get("eenabled").equals("yes") ? true : false);
					LdapPanel.this.ldapSettings.setAnonymous(values.get("anon").equals("yes") ? true : false);
					LdapPanel.this.ldapSettings.setUrl((String) values.get("url"));
					LdapPanel.this.ldapSettings.setUsername((String) values.get("username"));
					LdapPanel.this.ldapSettings.setPwd((String) values.get("password"));
					LdapPanel.this.ldapSettings.setRealm((String) values.get("realm"));
					LdapPanel.this.ldapSettings.setUserIdentifierAttr((String) values.get("useridentifierattr"));
					LdapPanel.this.ldapSettings.setGrpIdentifierAttr((String) values.get("grpidentifierattr"));
					LdapPanel.this.ldapSettings.setLogonAttr((String) values.get("logonattr"));
					LdapPanel.this.ldapSettings.setUserClass((String) values.get("userclass"));
					LdapPanel.this.ldapSettings.setGrpClass((String) values.get("grpclass"));
					LdapPanel.this.ldapSettings.setUsersBaseNode((String) values.get("usersbasenode"));
					LdapPanel.this.ldapSettings.setGrpsBaseNode((String) values.get("grpsbasenode"));
					LdapPanel.this.ldapSettings.setLanguage((String) values.get("language"));

					service.testConnection(Session.get().getSid(), LdapPanel.this.ldapSettings,
							new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									Log.serverError(caught);
								}

								@Override
								public void onSuccess(Boolean ret) {
									if (ret) {
										LD.ask(I18N.message("testconnection"), I18N.message("connectionestablished")
												+ ".\n" + I18N.message("openldapexplorer"), new BooleanCallback() {
											@Override
											public void execute(Boolean value) {
												if (value) {
													LdapExplorer explorer = new LdapExplorer(
															LdapPanel.this.ldapSettings);
													explorer.show();
												}
											}
										});
									} else
										SC.warn(I18N.message("connectionfailed"));
								}
							});
				}
			}
		});

		IButton activedir = new IButton();
		activedir.setAutoFit(true);
		activedir.setTitle(I18N.message("activedirectory"));
		activedir.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LD.askforValue(I18N.message("activedirectory"), I18N.message("addomain"), "", "200",
						new ValueCallback() {
							@Override
							public void execute(String value) {
								if (value == null)
									return;
								String node = value.replaceAll("\\.", ",DC=");
								node = "DC=" + node;
								vm.setValue("url", "ldap://AD_SERVER:389");
								vm.setValue("username", "CN=Administrator,CN=Users," + node);
								vm.setValue("useridentifierattr", "CN");
								vm.setValue("grpidentifierattr", "CN");
								vm.setValue("logonattr", "sAMAccountName");
								vm.setValue("userclass", "person");
								vm.setValue("grpclass", "group");
								vm.setValue("userclass", "person");
								vm.setValue("usersbasenode", "CN=Users," + node);
								vm.setValue("grpsbasenode", "CN=Builtin," + node);
								vm.setValue("anon", "no");
							}
						});
			}
		});

		HLayout buttons = new HLayout();
		buttons.setMembersMargin(3);
		buttons.setMembers(save, activedir, test);
		setMembers(tabs, buttons);
	}
}