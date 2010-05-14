package com.logicaldoc.gui.frontend.client.settings;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.htmlunit.corejs.javascript.Context;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
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
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.IntegerItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This panel shows the Email settings.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class EmailPanel extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private ValuesManager vm = new ValuesManager();

	private TabSet tabs = new TabSet();

	private GUIEmailSettings emailSettings;

	public EmailPanel(GUIEmailSettings settings) {
		this.emailSettings = settings;

		setWidth100();
		setMembersMargin(10);
		setMargin(30);

		tabs.setWidth(400);
		tabs.setHeight(250);

		Tab email = new Tab();
		email.setTitle(I18N.getMessage("email"));

		DynamicForm emailForm = new DynamicForm();
		emailForm.setValuesManager(vm);
		emailForm.setTitleOrientation(TitleOrientation.TOP);
		emailForm.setNumCols(2);
		emailForm.setColWidths(100, 100);

		// SMTP Server
		TextItem smtpServer = new TextItem();
		smtpServer.setName("smtpServer");
		smtpServer.setTitle(I18N.getMessage("smtpserver"));
		smtpServer.setValue(this.emailSettings.getSmtpServer());
		smtpServer.setRequired(true);

		// Port
		IntegerItem port = new IntegerItem();
		port.setName("port");
		port.setTitle(I18N.getMessage("port"));
		port.setValue(this.emailSettings.getPort());
		port.setRequired(true);
		IntegerRangeValidator portValidator = new IntegerRangeValidator();
		portValidator.setMin(1);
		port.setValidators(portValidator);

		// Username
		TextItem username = new TextItem();
		username.setName("username");
		username.setTitle(I18N.getMessage("username"));
		username.setValue(this.emailSettings.getUsername());

		// Password
		PasswordItem password = new PasswordItem("password", I18N.getMessage("password"));
		password.setName("password");
		password.setValue(this.emailSettings.getPwd());

		// Connection Security
		SelectItem connSecurity = new SelectItem();
		LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
		opts.put("none", I18N.getMessage("none"));
		opts.put("tlsAvailable", I18N.getMessage("tlsavailable"));
		opts.put("tls", I18N.getMessage("tls"));
		opts.put("ssl", I18N.getMessage("ssl"));
		connSecurity.setValueMap(opts);
		connSecurity.setName("connSecurity");
		connSecurity.setTitle(I18N.getMessage("connsecurity"));
		connSecurity.setValue(this.emailSettings.getConnSecurity());

		// Use Secure Authentication
		CheckboxItem secureAuth = new CheckboxItem();
		secureAuth.setName("secureAuth");
		secureAuth.setTitle(I18N.getMessage("secureauth"));
		secureAuth.setRedrawOnChange(true);
		secureAuth.setWidth(50);
		secureAuth.setValue(emailSettings.isSecureAuth());

		// Sender Email
		TextItem senderEmail = ItemFactory.newEmailItem("senderEmail", I18N.getMessage("senderemail"), false);
		senderEmail.setValue(this.emailSettings.getSenderEmail());

		emailForm.setItems(smtpServer, port, username, password, connSecurity, secureAuth, senderEmail);

		email.setPane(emailForm);

		tabs.setTabs(email);

		IButton save = new IButton();
		save.setTitle(I18N.getMessage("save"));
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				final Map<String, Object> values = vm.getValues();

				if (vm.validate()) {
					EmailPanel.this.emailSettings.setSmtpServer((String) values.get("smtpServer"));
					if (values.get("port") instanceof Integer)
						EmailPanel.this.emailSettings.setPort((Integer) values.get("port"));
					EmailPanel.this.emailSettings.setUsername((String) values.get("username"));
					EmailPanel.this.emailSettings.setPwd((String) values.get("password"));
					EmailPanel.this.emailSettings.setConnSecurity((String) values.get("connSecurity"));
					EmailPanel.this.emailSettings.setSecureAuth(values.get("secureAuth").equals("yes") ? true : false);
					EmailPanel.this.emailSettings.setSenderEmail((String) values.get("senderEmail"));

					service.saveEmailSettings(Session.get().getSid(), EmailPanel.this.emailSettings,
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
}