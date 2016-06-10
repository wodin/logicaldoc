package com.logicaldoc.gui.login.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.RequestInfo;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.MessageLabel;
import com.logicaldoc.gui.login.client.services.LoginService;
import com.logicaldoc.gui.login.client.services.LoginServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * The panel showing the login form
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 7.5
 */
public class LoginPanel extends VLayout {

	protected static final int FORM_WIDTH = 280;

	private static final int COLUMN_WIDTH = FORM_WIDTH + 20;

	protected static final String PARAM_SUCCESSURL = "j_successurl";

	protected static final String PARAM_FAILUREURL = "j_failureurl";

	protected LoginServiceAsync loginService = (LoginServiceAsync) GWT.create(LoginService.class);

	protected TextItem username = new TextItem();

	protected PasswordItem password = new PasswordItem();

	protected SelectItem language;

	protected CheckboxItem rememberMe = new CheckboxItem();

	protected Window messagesWindow = new Window();

	protected VLayout mainPanel = null;

	// Flag used to handle double clicks on the login button
	protected static boolean loggingIn = false;

	protected DynamicForm form = new DynamicForm();

	protected GUIInfo info = null;

	public LoginPanel(GUIInfo info) {
		setDefaultLayoutAlign(Alignment.CENTER);
		setWidth100();
		setHeight100();
		setMembersMargin(0);

		this.info = info;
	}

	protected void initGUI(boolean saveLoginEnabled) {
		HLayout spacer15 = new HLayout();
		spacer15.setHeight(15);
		spacer15.setWidth(15);

		HLayout spacer10 = new HLayout();
		spacer10.setHeight(10);
		spacer10.setWidth(10);

		// Prepare the logo to show on the top of the page
		Img logoTop = ItemFactory.newBrandImg(info.isLogoOemCustomized() ? "logo_head.png" : "logo_oem.png", info);
		logoTop.setWidth(205);
		logoTop.setHeight(40);

		/*
		 * This panel stays on top of the page
		 */
		HLayout top = new HLayout();
		top.setMargin(10);
		top.setHeight(60);
		top.setMembersMargin(0);
		top.setMembers(logoTop);

		// Prepare the logo to show in the login form
		Img logoLogin = ItemFactory.newBrandImg(!info.isLogoOemCustomized() ? "logo.png" : "logo_oem.png", info);
		logoLogin.setWidth(205);
		logoLogin.setHeight(40);
		logoLogin.setAlign(Alignment.CENTER);
		logoLogin.setLayoutAlign(Alignment.CENTER);

		/*
		 * Panel containing product name and version
		 */
		String productInfoHtml = "<b>" + info.getProductName() + " " + info.getRelease() + "</b>";
		if (info.getUrl() != null && !"-".equals(info.getUrl()))
			productInfoHtml = "<a href='" + info.getUrl() + "' target='_blank' class='login-link'>" + productInfoHtml
					+ "</a>";
		HTMLFlow productInfo = new HTMLFlow(productInfoHtml);
		productInfo.setHeight(16);
		productInfo.setWidth(COLUMN_WIDTH);
		productInfo.setStyleName("login-product");

		// Prepare the Form and all its fields
		form = new DynamicForm();
		form.setAlign(Alignment.CENTER);
		form.setWidth(FORM_WIDTH);
		form.setNumCols(saveLoginEnabled ? 3 : 1);
		form.setTitleWidth(0);
		form.setMargin(0);
		form.setCellPadding(0);

		username.setTitle(I18N.message("username"));
		username.setShowTitle(false);
		username.setHint(I18N.message("username").toLowerCase());
		username.setShowHintInField(true);
		username.setWrapTitle(false);
		username.setRequired(true);
		username.setHeight(34);
		username.setWidth(FORM_WIDTH);
		username.setAlign(Alignment.LEFT);
		username.setTextBoxStyle("login-field");
		username.setColSpan(saveLoginEnabled ? 3 : 1);
		username.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onSigninClicked();
			}
		});

		password = ItemFactory.newPasswordItem("password", "password", null);
		password.setShowTitle(false);
		password.setHint(I18N.message("password").toLowerCase());
		password.setShowHintInField(true);
		password.setRequired(true);
		password.setHeight(34);
		password.setWidth(FORM_WIDTH);
		password.setTextBoxStyle("login-field");
		password.setAlign(Alignment.LEFT);
		password.setWrapTitle(false);
		password.setColSpan(saveLoginEnabled ? 3 : 1);
		password.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onSigninClicked();
			}
		});

		// If the case, initialize the credentials from client's cookies
		if (saveLoginEnabled && rememberMe.getValueAsBoolean()) {
			username.setValue(Offline.get(Constants.COOKIE_USER));
			password.setValue(Offline.get(Constants.COOKIE_PASSWORD));
		}

		language = ItemFactory.newLanguageSelector("language", true, true);
		language.setShowTitle(false);
		language.setDefaultValue("");
		language.setControlStyle("login-language");
		language.setWidth(FORM_WIDTH - 4);
		language.setHeight(34);
		language.setAlign(Alignment.LEFT);
		language.setHint(I18N.message("chooseyourlanguage"));
		language.setShowHintInField(true);
		language.setControlStyle("login-language");
		language.setTextBoxStyle("login-language-text");
		language.setPickerIconStyle("login-language-picker");
		language.setColSpan(saveLoginEnabled ? 3 : 1);

		RequestInfo request = WindowUtils.getRequestInfo();

		// If a parameter specifies a locale, we initialize the language
		// selector
		if (request.getParameter(Constants.LOCALE) != null && !request.getParameter(Constants.LOCALE).equals("")) {
			String lang = request.getParameter(Constants.LOCALE);
			Map<String, String> languages = I18N.getSupportedGuiLanguages(false);
			for (String l : languages.keySet()) {
				if (lang.equals(l)) {
					language.setValue(l);
					break;
				}
			}
		}

		SpacerItem spacerItem12 = new SpacerItem();
		spacerItem12.setHeight(12);
		spacerItem12.setColSpan(saveLoginEnabled ? 3 : 1);

		ButtonItem signIn = new ButtonItem(I18N.message("signin"));
		signIn.setBaseStyle("btn");
		signIn.setHoverStyle("btn");
		signIn.setHeight(34);
		signIn.setAlign(Alignment.RIGHT);
		signIn.setStartRow(false);
		signIn.setWidth(saveLoginEnabled ? 120 : FORM_WIDTH);
		signIn.setColSpan(saveLoginEnabled ? 1 : 3);
		signIn.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				onSigninClicked();
			}
		});

		rememberMe.setTitle(I18N.message("rememberme"));
		rememberMe.setRequired(false);
		rememberMe.setShowTitle(false);
		rememberMe.setValue("true".equals(Offline.get(Constants.COOKIE_SAVELOGIN)));
		rememberMe.setTextBoxStyle("login-field");
		rememberMe.setAlign(Alignment.LEFT);
		rememberMe.setEndRow(false);
		rememberMe.setColSpan(2);

		if (saveLoginEnabled)
			form.setFields(username, spacerItem12, password, spacerItem12, language, spacerItem12, rememberMe, signIn);
		else
			form.setFields(username, spacerItem12, password, spacerItem12, language, spacerItem12, signIn);

		HTMLFlow lostPassword = new HTMLFlow("<div><a href=\"javascript:showLostDialog('" + info.getProductName()
				+ "')\" class='login-lost'>" + I18N.message("lostpassword") + "</a></div>");
		lostPassword.setLayoutAlign(Alignment.RIGHT);
		lostPassword.setHoverDelay(0);
		lostPassword.setMargin(0);
		lostPassword.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showLostDialog(info.getProductName());
			}
		});

		/*
		 * Prepare the licensing canvas
		 */
		String copyrightHtml = "<div>\u00A9 " + info.getYear() + " " + info.getVendor();
		if (info.getUrl() != null && !"-".equals(info.getUrl()))
			copyrightHtml = "<a href='" + info.getUrl() + "' target='_blank' class='login-copyright-link'>"
					+ copyrightHtml + "</a></div>";
		String licenseeHtml = "";
		if (info.getLicensee() != null && !"".equals(info.getLicensee().trim()))
			licenseeHtml += "<div>" + I18N.message("licensedto") + " <b>" + info.getLicensee() + "</b></div>";

		HTMLFlow licensing = new HTMLFlow(copyrightHtml + licenseeHtml);
		licensing.setStyleName("login-copyright");

		// Panel containing the inputs
		VLayout inputsForm = new VLayout();
		inputsForm.setMargin(0);
		inputsForm.setWidth(FORM_WIDTH);
		inputsForm.setStyleName("control-group");
		inputsForm.setMembers(logoLogin, spacer15, form, lostPassword, licensing);

		// Panel containing the login form
		VLayout loginForm = new VLayout();
		loginForm.setLayoutAlign(VerticalAlignment.TOP);
		loginForm.setLayoutAlign(Alignment.CENTER);
		loginForm.setAlign(VerticalAlignment.TOP);
		loginForm.setMembersMargin(0);
		loginForm.setMargin(0);
		loginForm.setWidth(FORM_WIDTH);
		loginForm.setStyleName("login-form");
		loginForm.setMembers(inputsForm);

		// The login screen
		VLayout loginScreen = new VLayout();
		loginScreen.setMembersMargin(0);
		loginScreen.setMargin(0);
		loginScreen.setStyleName("login-screen");
		loginScreen.setMembers(loginForm);

		// The center column with login screen and product info
		VLayout centerColumn = new VLayout();
		centerColumn.setMembersMargin(0);
		centerColumn.setMargin(0);
		centerColumn.setWidth(COLUMN_WIDTH);
		centerColumn.setHeight(330);
		centerColumn.setLayoutAlign(VerticalAlignment.TOP);
		centerColumn.setLayoutAlign(Alignment.CENTER);
		centerColumn.setAlign(VerticalAlignment.TOP);
		centerColumn.setMembers(productInfo, loginScreen);

		// Main Panel (covers 100% of the screen)
		mainPanel = new VLayout();
		mainPanel.setLayoutAlign(VerticalAlignment.TOP);
		mainPanel.setMembers(top, centerColumn);
		addMember(mainPanel);

		prepareMessages();

		prepareSwitchViewLink();
	}

	protected void initGUI() {
		boolean saveLoginEnabled = "true".equals(info.getConfig("gui.savelogin"));
		initGUI(saveLoginEnabled);
	}

	protected void prepareSwitchViewLink() {
		String url = "mobile".equals(Util.getJavascriptVariable("j_layout")) ? (Util.contextPath() + "login.jsp")
				: (Util.contextPath() + "login-mobile.jsp");
		String label = "mobile".equals(Util.getJavascriptVariable("j_layout")) ? I18N.message("viewclassicweb") : I18N
				.message("viewmobileweb");

		/*
		 * A link to the alternative login page
		 */
		HTMLFlow switchLink = new HTMLFlow("<a href='" + url + "' class='login-switchview'>" + label + "</a>");
		switchLink.setHeight(16);
		switchLink.setWidth(COLUMN_WIDTH + 20);
		switchLink.setStyleName("login-switchview");

		HLayout spacer10 = new HLayout();
		spacer10.setHeight(10);
		spacer10.setWidth(10);

		VLayout link = new VLayout();
		link.setMembersMargin(0);
		link.setMargin(0);
		link.setStyleName("login-switchview");
		link.setMembers(spacer10, switchLink);
		link.setLayoutAlign(VerticalAlignment.TOP);
		link.setLayoutAlign(Alignment.CENTER);
		link.setWidth(COLUMN_WIDTH + 20);
		link.setHeight(18);

		mainPanel.addMember(link);
	}

	/**
	 * Prepares the panel to show messages
	 */
	protected void prepareMessages() {
		if (info.getMessages() == null || info.getMessages().length == 0)
			return;

		int height = 0;

		List<MessageLabel> messages = new ArrayList<MessageLabel>();
		if (info.getMessages().length > 0) {
			for (GUIMessage message : info.getMessages()) {
				MessageLabel label = new MessageLabel(message, info.getTenant().getId() == 1L);
				label.setStyleName("loginMemesage");
				messages.add(label);
				height += label.getHeight();
			}
		}

		if (info.getRunLevel().equals("demo")) {
			GUIMessage demoRunLevelMessage = new GUIMessage();
			demoRunLevelMessage.setMessage(I18N.message("demomode"));
			demoRunLevelMessage.setPriority(GUIMessage.PRIO_WARN);
			MessageLabel demoRunLevel = new MessageLabel(demoRunLevelMessage, info.getTenant().getId() == 1L);
			messages.add(demoRunLevel);
			height += demoRunLevel.getHeight();
		}

		HLayout spacer15 = new HLayout();
		spacer15.setHeight(15);
		spacer15.setWidth(15);

		// The messages screen
		VLayout messagesScreen = new VLayout();
		messagesScreen.setMembersMargin(0);
		messagesScreen.setMargin(0);
		messagesScreen.setStyleName("login-screen");
		messagesScreen.setMembers(messages.toArray(new MessageLabel[0]));
		messagesScreen.setLayoutAlign(VerticalAlignment.TOP);
		messagesScreen.setLayoutAlign(Alignment.CENTER);
		messagesScreen.setWidth(COLUMN_WIDTH + 20);
		messagesScreen.setHeight(height);

		mainPanel.addMember(spacer15);
		mainPanel.addMember(messagesScreen);
	}

	protected void onSigninClicked() {
		if (!form.validate()){
			onAuthenticationFailure();
			return;
		}

		if (loggingIn == true)
			return;
		else
			loggingIn = true;

		removeLoginCookies();

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, Util.contextPath() + "j_spring_security_check");
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		try {
			String data = "j_username=" + URL.encodeQueryString((String) username.getValue());
			data += "&j_password=" + URL.encodeQueryString((String) password.getValue());
			data += "&" + PARAM_SUCCESSURL + "=" + URL.encodeQueryString(Util.getJavascriptVariable(PARAM_SUCCESSURL));
			data += "&" + PARAM_FAILUREURL + "=" + URL.encodeQueryString(Util.getJavascriptVariable(PARAM_FAILUREURL));
			
			builder.sendRequest(data, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					loggingIn = false;
					onAuthenticationFailure();
				}

				public void onResponseReceived(Request request, Response response) {
					loggingIn = false;
					String sid = Cookies.getCookie(Constants.COOKIE_SID);
					if (sid != null && !"".equals(sid)) {
						onAuthenticationSuccess(sid);
					} else {
						onAuthenticationFailure();
					}
				}
			});
		} catch (RequestException e) {
			SC.warn("Login request error: "+e.getMessage());
		}
	}

	private void removeLoginCookies() {
		try {
			Offline.remove(Constants.COOKIE_SID);
			Cookies.removeCookie(Constants.COOKIE_SID);
			Cookies.removeCookie(Constants.COOKIE_FAILURE);
		} catch (Throwable t) {

		}
	}

	public static void showLostDialog(String productName) {
		ResetPassword pwdReset = new ResetPassword(productName);
		pwdReset.show();
	}

	protected void onAuthenticationSuccess(String sid) {
		try {
			messagesWindow.destroy();
		} catch (Throwable e) {
		}

		// If the case, save the credentials into client cookies
		if ("true".equals(Session.get().getConfig("gui.savelogin"))) {
			Offline.put(Constants.COOKIE_SAVELOGIN, (String) rememberMe.getValueAsBoolean().toString());
			Offline.put(Constants.COOKIE_USER, rememberMe.getValueAsBoolean() ? (String) username.getValue() : "");
			Offline.put(Constants.COOKIE_PASSWORD, rememberMe.getValueAsBoolean() ? (String) password.getValue() : "");
		} else {
			Offline.put(Constants.COOKIE_SAVELOGIN, "false");
			Offline.put(Constants.COOKIE_USER, "");
			Offline.put(Constants.COOKIE_PASSWORD, "");
		}

		Util.redirectToSuccessUrl(language.getValueAsString());
	}

	protected void onAuthenticationFailure() {
		final String failure = Cookies.getCookie(Constants.COOKIE_FAILURE);
		removeLoginCookies();

		if (failure != null && !"".equals(failure)) {
			loginService.getUser((String) username.getValue(), new AsyncCallback<GUIUser>() {

				@Override
				public void onFailure(Throwable caught) {
					SC.warn(I18N.message("accesdenied"));
				}

				@Override
				public void onSuccess(GUIUser user) {
					if (user != null && (user.getQuotaCount() >= user.getQuota() && user.getQuota() >= 0)) {
						SC.warn(I18N.message("quotadocsexceeded"));
					} else if ("passwordexpired".equals(failure)) {
						ChangePassword change = new ChangePassword(user);
						change.show();
					} else {
						SC.warn(I18N.message("accesdenied"));
					}
				}
			});
		} else {
			SC.warn(I18N.message("accesdenied"));
		}
	}
}