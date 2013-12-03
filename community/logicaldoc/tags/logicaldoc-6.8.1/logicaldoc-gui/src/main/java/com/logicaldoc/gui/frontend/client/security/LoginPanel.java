package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
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
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLFlow;
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
		setMembersMargin(0);

		addMember(prepareLogoPanel(info));

		VLayout mainPanel = new VLayout();
		mainPanel.setWidth100();
		mainPanel.setHeight100();
		mainPanel.setStyleName("loginMain");

		String productInfoHtml = "<b>" + info.getProductName() + " " + info.getRelease() + "</b>";
		if (info.getUrl() != null && !"-".equals(info.getUrl()))
			productInfoHtml = "<a href='" + info.getUrl() + "'>" + productInfoHtml + "</a>";
		if (info.getLicensee() != null && !"".equals(info.getLicensee().trim()))
			productInfoHtml += "&nbsp;&nbsp;" + I18N.message("licensedto") + " " + info.getLicensee();
		HTMLFlow productInfo = new HTMLFlow(productInfoHtml);
		productInfo.setStyleName("loginProductInfo");
		productInfo.setHeight(13);
		productInfo.setMargin(5);

		// Panel containing the login form
		VLayout formLayout = new VLayout();
		formLayout.setLayoutAlign(VerticalAlignment.TOP);
		formLayout.setLayoutAlign(Alignment.CENTER);
		formLayout.setWidth(300);

		VStack messages = new VStack();
		if (info.getMessages().length > 0) {
			for (GUIMessage message : info.getMessages()) {
				MessageLabel label = new MessageLabel(message);
				label.setStyleName("loginMemesage");
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
		username.setWidth(200);
		username.setTitleStyle("loginFormTitle");
		username.setTextBoxStyle("loginTextItem");
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
		password.setWidth(200);
		password.setTitleStyle("loginFormTitle");
		password.setTextBoxStyle("loginTextItem");
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
		savelogin.setTextBoxStyle("saveLogin");
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
		language.setTitleStyle("loginFormTitle");
		language.setTextBoxStyle("loginSelectItemText");
		language.setWidth(205);

		if ("true".equals(info.getConfig("gui.savelogin")))
			form.setFields(username, password, language, savelogin);
		else
			form.setFields(username, password, language);

		Button loginButton = new Button(I18N.message("login"));
		loginButton.setBaseStyle("loginButton");
		loginButton.setLayoutAlign(Alignment.RIGHT);
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onLogin();
			}
		});

		addMember(mainPanel);

		Img spacer20 = ItemFactory.newImg("blank.png");
		spacer20.setWidth100();
		spacer20.setHeight(20);
		formLayout.setMembers(spacer20, form, loginButton, prepareForgotPassword(info), spacer20, messages);

		String copyrightHtml = "\u00A9 " + info.getYear() + " " + info.getVendor();
		if (info.getUrl() != null && !"-".equals(info.getUrl()))
			copyrightHtml = "<a href='" + info.getUrl() + "'>" + copyrightHtml + "</a>";
		HTMLFlow copyright = new HTMLFlow(copyrightHtml);
		copyright.setStyleName("loginCopyright");
		copyright.setLayoutAlign(VerticalAlignment.BOTTOM);
		copyright.setMargin(10);

		mainPanel.setMembers(productInfo, formLayout, copyright);

		form.focusInItem(username);
		form.setAutoFocus(true);
		form.focus();
	}

	protected MessageLabel prepareForgotPassword(GUIInfo info) {
		GUIMessage forgotPwd = new GUIMessage();
		forgotPwd.setMessage(I18N.message("forgotpassword"));
		MessageLabel forgotMessage = new MessageLabel(forgotPwd);
		forgotMessage.setAlign(Alignment.RIGHT);
		forgotMessage.setIcon(null);
		forgotMessage.setStyleName("loginForgotpwd");
		final String productName = info.getProductName();
		forgotMessage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onForgottenPwd(productName);
			}
		});
		return forgotMessage;
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
		Cookies.setCookie(Constants.COOKIE_SID, session.getSid());
		
		
		GUIUser user = session.getUser();
		if (user.getQuotaCount() >= user.getQuota() && user.getQuota() >= 0)
			Log.warn(I18N.message("quotadocsexceeded"), null);
	}

	private Layout prepareLogoPanel(GUIInfo info) {
		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight(85);

		HLayout logos = new HLayout();
		logos.setWidth100();
		logos.setHeight(82);
		logos.setStyleName("loginTop");

		// Prepare the logo image to be shown inside the banner
		Img logoImage = ItemFactory.newBrandImg("logo.png", info);
		logoImage.setWidth(215);
		logoImage.setHeight(55);
		logoImage.setMargin(10);

		Img separator = ItemFactory.newImg("blank.png");
		separator.setWidth100();

		// Prepare the OEM logo image to be shown inside the banner
		Img logoOemImage = ItemFactory.newBrandImg("logo_oem.png", info);
		logoOemImage.setWidth(215);
		logoOemImage.setHeight(55);
		logoOemImage.setMargin(10);

		// Prepare a small separator
		HLayout sep = new HLayout();
		sep.setWidth100();
		sep.setHeight(3);
		sep.setStyleName("loginSep");

		logos.setMembers(logoImage, separator, logoOemImage);
		layout.setMembers(logos, sep);

		return layout;
	}
}