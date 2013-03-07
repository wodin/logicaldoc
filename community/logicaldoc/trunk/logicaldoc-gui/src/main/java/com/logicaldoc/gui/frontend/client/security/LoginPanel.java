package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.MessageLabel;
import com.logicaldoc.gui.frontend.client.Frontend;
import com.logicaldoc.gui.frontend.client.personal.ChangePassword;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * The Login entry point
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LoginPanel extends VLayout {

	protected SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	protected TextItem username = new TextItem();

	protected PasswordItem password = new PasswordItem();

	protected CheckboxItem savelogin = new CheckboxItem();

	protected SelectItem language;

	// Flag used to handle double clicks on the login button
	protected static boolean loggingIn = false;

	public LoginPanel(GUIInfo info) {
		setDefaultLayoutAlign(Alignment.CENTER);
		setWidth100();
		setHeight100();

		// Panel for horizontal centering
		HLayout hPanel = new HLayout();
		hPanel.setDefaultLayoutAlign(Alignment.LEFT);
		hPanel.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		hPanel.setWidth(400);
		hPanel.setHeight100();

		// Panel for vertical centering
		VLayout content = new VLayout();
		content.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		content.setWidth100();
		content.setHeight(250);
		hPanel.addMember(content);

		addMember(hPanel);

		// Prepare the form header that contains the product name and version
		HTMLFlow header = new HTMLFlow(info.getProductName() + " " + info.getRelease());
		header.setStyleName("loginHeader");
		header.setHeight("12px");

		// Prepare the form footer that contains copyright and website link
		String htmlString = "\u00A9 " + info.getYear() + " " + info.getVendor();
		if (info.getUrl() != null && !"-".equals(info.getUrl()))
			htmlString += "  &#160; &#8226; &#160; <a href='" + info.getUrl() + "'>" + info.getUrl() + "</a>";
		HTMLFlow footer = new HTMLFlow(htmlString);
		footer.setStyleName("loginFooter");

		VStack messages = new VStack();
		if (info.getMessages().length > 0) {
			for (GUIMessage message : info.getMessages()) {
				MessageLabel label = new MessageLabel(message);
				messages.addMember(label);
			}
		}

		if (info.getRunLevel().equals("demo")) {
			GUIMessage demoRunLevelMessage = new GUIMessage();
			demoRunLevelMessage.setMessage(I18N.message("demomode"));
			demoRunLevelMessage.setPriority(GUIMessage.PRIO_WARN);
			MessageLabel demoRunLevel = new MessageLabel(demoRunLevelMessage);
			messages.addMember(demoRunLevel);
		}

		// Prepare the Form and all its fields
		final DynamicForm form = new DynamicForm();
		form.setAlign(Alignment.CENTER);

		username.setTitle(I18N.message("username"));
		username.setRequired(true);
		username.setWrapTitle(false);
		username.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onLogin();
			}
		});

		password = ItemFactory.newPasswordItem("password", "password", null);
		password.setRequired(true);
		password.setWrapTitle(false);
		password.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onLogin();
			}
		});

		savelogin.setTitle(I18N.message("savelogin"));
		savelogin.setRequired(false);
		savelogin.setWrapTitle(false);
		savelogin.setValue("true".equals(Offline.get(Constants.COOKIE_SAVELOGIN)));

		// If the case, initialize the credentials from client's cookies
		if ("true".equals(info.getConfig("gui.savelogin")) && savelogin.getValueAsBoolean()) {
			username.setValue(Offline.get(Constants.COOKIE_USER));
			password.setValue(Offline.get(Constants.COOKIE_PASSWORD));
		}

		if (I18N.getSupportedGuiLanguages(false).size() > 1) {
			language = ItemFactory.newLanguageSelector("language", true, true);
			language.setDefaultValue("");
		} else {
			language = ItemFactory.newLanguageSelector("language", false, true);
			language.setDefaultValue(I18N.getSupportedGuiLanguages(false).keySet().iterator().next());
		}

		if ("true".equals(info.getConfig("gui.savelogin")))
			form.setFields(username, password, language, savelogin);
		else
			form.setFields(username, password, language);

		IButton loginButton = new IButton(I18N.message("login"));
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onLogin();
			}
		});

		// Prepare the logo images to be shown inside the login form
		Img logoImage = ItemFactory.newBrandImg("logo.png", info);
		logoImage.setHeight(40);
		logoImage.setWidth(205);
		Img logoOemImage = ItemFactory.newBrandImg("logo_oem.png", info);
		logoOemImage.setHeight(40);
		logoOemImage.setWidth(205);

		HLayout logos = new HLayout();
		logos.setMembersMargin(10);
		logos.setMembers(logoImage, logoOemImage);

		Layout inner = new VLayout();
		inner.setShowEdges(true);
		inner.addMember(logos);
		Layout separator = new VLayout();
		separator.setHeight(8);
		inner.addMember(separator);
		inner.addMember(form);
		inner.addMember(separator);
		Layout bottom = new HLayout(60);
		bottom.setAlign(Alignment.LEFT);
		bottom.addMember(loginButton);
		retrievePwd(info, bottom);
		inner.addMember(bottom);
		inner.setPadding(5);

		Layout outer = new VStack();
		outer.addMember(header);
		outer.addMember(inner);
		outer.addMember(footer);
		outer.addMember(messages);
		outer.setPadding(2);

		content.addMember(outer);
		form.focusInItem(username);
		form.setAutoFocus(true);
		form.focus();
	}

	protected void retrievePwd(GUIInfo info, Layout bottom) {
		GUIMessage forgotPwd = new GUIMessage();
		forgotPwd.setMessage(I18N.message("forgotpassword"));
		MessageLabel forgotMessage = new MessageLabel(forgotPwd);
		forgotMessage.setAlign(Alignment.RIGHT);
		forgotMessage.setIcon(null);
		forgotMessage.setStyleName("forgotpwd");
		final String productName = info.getProductName();
		forgotMessage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onForgottenPwd(productName);
			}
		});
		bottom.addMember(forgotMessage);
	}

	protected void onLogin() {
		if (loggingIn == true)
			return;
		else
			loggingIn = true;

		securityService.login((String) username.getValue(), (String) password.getValue(), (String) language.getValue(),
				new AsyncCallback<GUISession>() {
					public void onFailure(Throwable caught) {
						loggingIn = false;
						Log.serverError(caught);
						SC.warn(I18N.message("accesdenied"));
					}

					@Override
					public void onSuccess(GUISession session) {
						loggingIn = false;
						if (session.isLoggedIn()) {
							onLoggedIn(session);
						} else if (session.getUser() != null && session.getUser().isPasswordExpired()) {
							new ChangePassword(session.getUser(), "needtochangepassword").show();
						} else {
							SC.warn(I18N.message("accesdenied"));
						}
					}
				});
	}

	private void onForgottenPwd(String productName) {
		PasswordReset pwdReset = new PasswordReset(productName);
		pwdReset.show();
	}

	public void onLoggedIn(GUISession session) {
		try {
			Session.get().init(session);
			Frontend.get().showMain();
		} catch (Throwable e) {
			SC.warn(e.getMessage());
		}

		// If the case, save the credentials into client cookies
		if ("true".equals(Session.get().getInfo().getConfig("gui.savelogin"))) {
			Offline.put(Constants.COOKIE_SAVELOGIN, (String) savelogin.getValueAsBoolean().toString());
			Offline.put(Constants.COOKIE_USER, savelogin.getValueAsBoolean() ? (String) username.getValue() : "");
			Offline.put(Constants.COOKIE_PASSWORD, savelogin.getValueAsBoolean() ? (String) password.getValue() : "");
		} else {
			Offline.put(Constants.COOKIE_SAVELOGIN, "false");
			Offline.put(Constants.COOKIE_USER, "");
			Offline.put(Constants.COOKIE_PASSWORD, "");
		}

		// In any case save the SID in the browser
		Offline.put(Constants.COOKIE_SID, session.getSid());

		GUIUser user = session.getUser();
		if (user.getQuotaCount() >= user.getQuota() && user.getQuota() >= 0)
			Log.warn(I18N.message("quotadocsexceeded"), null);
	}
}