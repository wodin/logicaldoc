package com.logicaldoc.gui.frontend.client.security;

import java.util.ArrayList;
import java.util.List;

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
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.widgets.MessageLabel;
import com.logicaldoc.gui.frontend.client.Frontend;
import com.logicaldoc.gui.frontend.client.personal.ChangePassword;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.Offline;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
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

	protected CheckboxItem rememberMe = new CheckboxItem();

	protected SelectItem language;

	protected Window messagesWindow = new Window();

	// Flag used to handle double clicks on the login button
	protected static boolean loggingIn = false;

	protected HTMLFlow reflections = null;

	protected Canvas licensingCanvas = new Canvas();

	protected VLayout formLayout = new VLayout();

	protected GUIInfo info = null;

	public LoginPanel(GUIInfo info) {
		setDefaultLayoutAlign(Alignment.CENTER);
		setWidth100();
		setHeight100();
		setMembersMargin(0);
		setStyleName("login-body");

		this.info = info;
	}

	public void initGUI() {
		// Prepare the logo to show on the top
		Img logoTop = ItemFactory.newBrandImg(info.isLogoOemCustomized() ? "logo_head.png" : "logo_oem.png", info);
		logoTop.setWidth(205);
		logoTop.setHeight(40);

		// Prepare the logo to show in the login form
		Img logoLogin = ItemFactory.newBrandImg(!info.isLogoOemCustomized() ? "logo.png" : "logo_oem.png", info);
		logoLogin.setWidth(205);
		logoLogin.setHeight(40);
		logoLogin.setAlign(Alignment.CENTER);
		logoLogin.setLayoutAlign(Alignment.CENTER);

		/*
		 * This panel stays on top of the page
		 */
		HLayout top = new HLayout();
		top.setMargin(10);
		top.setHeight(100);
		top.setMembersMargin(10);
		top.setMembers(logoTop);

		/*
		 * Panel containing product name and version
		 */
		String productInfoHtml = "<b>" + info.getProductName() + " " + info.getRelease() + "</b>";
		if (info.getUrl() != null && !"-".equals(info.getUrl()))
			productInfoHtml = "<a href='" + info.getUrl() + "' target='_blank'>" + productInfoHtml + "</a>";
		HTMLFlow productInfo = new HTMLFlow(productInfoHtml);
		productInfo.setStyleName("login-product-info");
		productInfo.setHeight(16);
		productInfo.setWidth100();

		HLayout spacer15 = new HLayout();
		spacer15.setHeight(15);
		spacer15.setWidth(15);

		HLayout spacer10 = new HLayout();
		spacer10.setHeight(10);
		spacer10.setWidth(10);

		Label loginLabel = new Label(I18N.message("login"));
		loginLabel.setStyleName("login-label");
		loginLabel.setHeight(35);
		loginLabel.setWidth(270);

		// Prepare the Form and all its fields
		final DynamicForm form = new DynamicForm();
		form.setAlign(Alignment.CENTER);
		form.setWidth100();
		form.setNumCols(1);
		form.setTitleWidth(0);
		form.setMargin(0);
		form.setCellPadding(0);

		username.setTitle(I18N.message("username"));
		username.setShowTitle(false);
		username.setHint(I18N.message("username").toLowerCase());
		username.setShowHintInField(true);
		username.setWrapTitle(false);
		username.setHeight(45);
		username.setWidth(280);
		username.setAlign(Alignment.CENTER);
		username.setTextBoxStyle("login-input");
		username.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onLogin();
			}
		});

		password = ItemFactory.newPasswordItem("password", "password", null);
		password.setShowTitle(false);
		password.setHeight(45);
		password.setWidth(280);
		password.setTextBoxStyle("login-input");
		password.setAlign(Alignment.CENTER);
		password.setWrapTitle(false);
		password.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onLogin();
			}
		});

		language = ItemFactory.newLanguageSelector("language", true, true);
		language.setShowTitle(false);
		language.setDefaultValue("");
		language.setWidth(280);
		language.setHeight(36);
		language.setAlign(Alignment.CENTER);
		language.setHint(I18N.message("chooseyourlanguage"));
		language.setShowHintInField(true);
		language.setControlStyle("login-lang");
		language.setTextBoxStyle("login-lang-text");
		language.setPickerIconStyle("login-lang-picker");
		language.setPickerIconName("login-lang-picker");
		language.setPickerIconSrc(Util.imageUrl("langpick.png"));
		language.setPickerIconHeight(36);
		language.setPickerIconWidth(50);

		form.setFields(username, password, language);

		Button signIn = new Button(I18N.message("signin"));
		signIn.setBaseStyle("login-signin");
		signIn.setLayoutAlign(Alignment.CENTER);
		signIn.setWidth(283);
		signIn.setHeight(43);
		signIn.setAlign(Alignment.CENTER);
		signIn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onLogin();
			}
		});

		rememberMe.setTitle(I18N.message("rememberme"));
		rememberMe.setRequired(false);
		rememberMe.setWrapTitle(false);
		rememberMe.setValue("true".equals(Offline.get(Constants.COOKIE_SAVELOGIN)));
		rememberMe.setDisabled(!"true".equals(info.getConfig("gui.savelogin")));
		rememberMe.setTextBoxStyle("login-remember");
		rememberMe.setEndRow(false);
		rememberMe.setAlign(Alignment.LEFT);
		rememberMe.setWidth(140);
		rememberMe.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				updateReflections(false);
			}
		});

		HTMLFlow forgot = new HTMLFlow("<div class='login-forgot'><a href='#'>" + I18N.message("forgotpassword")
				+ "</a></div>");
		forgot.setAlign(Alignment.RIGHT);
		forgot.setWidth(140);
		forgot.setHoverDelay(0);
		forgot.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onForgottenPwd(info.getProductName());
			}
		});
		forgot.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				updateReflections(true);
			}
		});
		forgot.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				updateReflections(false);
			}
		});

		final DynamicForm footerForm1 = new DynamicForm();
		footerForm1.setTitleWidth(0);
		footerForm1.setWidth(140);
		footerForm1.setFields(rememberMe);
		footerForm1.setMargin(0);
		footerForm1.setNumCols(1);
		footerForm1.setShowTitlesWithErrorMessages(false);
		footerForm1.setColWidths(0, 140);

		// This panel contains the Forgot password link
		HLayout footer = new HLayout();
		footer.setWidth(283);
		footer.setMargin(0);
		footer.setMembersMargin(0);
		footer.setMembers(footerForm1, forgot);

		// If the case, initialize the credentials from client's cookies
		if ("true".equals(info.getConfig("gui.savelogin")) && rememberMe.getValueAsBoolean()) {
			username.setValue(Offline.get(Constants.COOKIE_USER));
			password.setValue(Offline.get(Constants.COOKIE_PASSWORD));
		}

		// Panel containing the login form
		formLayout.setLayoutAlign(VerticalAlignment.TOP);
		formLayout.setLayoutAlign(Alignment.CENTER);
		formLayout.setAlign(VerticalAlignment.TOP);
		formLayout.setWidth(298);
		formLayout.setHeight(420);
		formLayout.setMembersMargin(0);
		formLayout.setMembers(productInfo, spacer15, spacer10, logoLogin, spacer15, spacer15, loginLabel, form,
				spacer15, spacer15, signIn, spacer10, footer);
		updateReflections(false);

		VLayout mainPanel = new VLayout();
		mainPanel.setWidth100();
		mainPanel.setHeight100();
		mainPanel.setStyleName("login-body");
		mainPanel.setLayoutAlign(VerticalAlignment.TOP);
		mainPanel.setMembers(top, formLayout);
		addMember(mainPanel);

		form.focusInItem(username);
		form.setAutoFocus(true);
		form.focus();

		/*
		 * Prepare the licensing canvas
		 */
		String copyrightHtml = "\u00A9 " + info.getYear() + " " + info.getVendor();
		if (info.getUrl() != null && !"-".equals(info.getUrl()))
			copyrightHtml = "<a href='" + info.getUrl() + "' target='_blank'>" + copyrightHtml + "</a>";
		String licenseeHtml = "<br/>";
		if (info.getLicensee() != null && !"".equals(info.getLicensee().trim()))
			licenseeHtml += I18N.message("licensedto") + " " + info.getLicensee();

		HTMLFlow licensing = new HTMLFlow(copyrightHtml + licenseeHtml);
		licensing.setStyleName("login-copyright");
		licensing.setMargin(10);
		licensing.setLayoutAlign(VerticalAlignment.BOTTOM);
		licensing.setTop(420);
		licensing.setLeft(10);

		licensingCanvas.addChild(licensing);
		licensingCanvas.draw();
		licensingCanvas.bringToFront();

		showMessages(info);
	}

	private void updateReflections(boolean underline) {
		if (reflections != null) {
			try {
				formLayout.removeMember(reflections);
			} catch (Throwable t) {

			}
		}

		String forgotString = I18N.message("forgotpassword");
		forgotString = forgotString.length() <= 16 ? forgotString : forgotString.substring(0, 16);

		reflections = new HTMLFlow("<div id='"
				+ (rememberMe.getValueAsBoolean() ? "login-checked-reflection" : "login-unchecked-reflection")
				+ "'></div>" + "<div id='login-remember-reflection'>" + I18N.message("rememberme") + "</div>"
				+ "<div id='" + (underline ? "login-forgot-reflection-underline" : "login-forgot-reflection") + "'>"
				+ forgotString + "</div></div>" + "<div id='login-signin-reflection'>" + I18N.message("signin")
				+ "</div>");
		reflections.setStyleName("login-reflections");
		reflections.setMargin(0);
		reflections.setWidth100();
		formLayout.addMember(reflections);
	}

	private void showMessages(final GUIInfo info) {
		List<MessageLabel> messages = new ArrayList<MessageLabel>();
		if (info.getMessages().length > 0) {
			for (GUIMessage message : info.getMessages()) {
				MessageLabel label = new MessageLabel(message);
				label.setStyleName("loginMemesage");
				messages.add(label);
			}
		}

		if (info.getRunLevel().equals("demo")) {
			GUIMessage demoRunLevelMessage = new GUIMessage();
			demoRunLevelMessage.setMessage(I18N.message("demomode"));
			demoRunLevelMessage.setPriority(GUIMessage.PRIO_WARN);
			MessageLabel demoRunLevel = new MessageLabel(demoRunLevelMessage);
			messages.add(demoRunLevel);
		}

		GUIMessage testMessage = new GUIMessage();
		testMessage.setMessage("Messaggio di prova numero 1");
		testMessage.setPriority(GUIMessage.PRIO_WARN);
		messages.add(new MessageLabel(testMessage));

		testMessage = new GUIMessage();
		testMessage.setMessage("Messaggio di prova numero 2");
		testMessage.setPriority(GUIMessage.PRIO_INFO);
		messages.add(new MessageLabel(testMessage));

		testMessage = new GUIMessage();
		testMessage.setMessage("Messaggio di prova numero 3");
		testMessage.setPriority(GUIMessage.PRIO_WARN);
		messages.add(new MessageLabel(testMessage));

		if (!messages.isEmpty()) {
			VStack messagesPanel = new VStack();
			messagesPanel.setMembers(messages.toArray(new MessageLabel[0]));
			messagesPanel.setMargin(6);
			messagesPanel.setTop(15);
			messagesPanel.setLeft(2);

			messagesWindow.setTitle(I18N.message("alerts"));
			messagesWindow.setHeaderControls(HeaderControls.HEADER_LABEL, HeaderControls.CLOSE_BUTTON);
			messagesWindow.setShowHeader(true);
			messagesWindow.setShowFooter(true);
			messagesWindow.setShowTitle(true);
			messagesWindow.setCanDragResize(true);
			messagesWindow.setIsModal(false);
			messagesWindow.setMargin(5);
			messagesWindow.setWidth(Math.max(
					Math.round((float) com.google.gwt.user.client.Window.getClientWidth() / 4F), 250));
			messagesWindow.setHeight(200);
			messagesWindow.setMinHeight(200);
			messagesWindow.setMinWidth(250);
			messagesWindow.setTop(180);
			messagesWindow.setLeft(10);
			// messagesWindow.setOpacity(90);
			messagesWindow.addChild(messagesPanel);
			messagesWindow.show();
		}
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
			licensingCanvas.destroy();
			messagesWindow.destroy();
			Session.get().init(session);
			Frontend.get().showMain();
		} catch (Throwable e) {
			SC.warn(e.getMessage());
		}

		// If the case, save the credentials into client cookies
		if ("true".equals(Session.get().getInfo().getConfig("gui.savelogin"))) {
			Offline.put(Constants.COOKIE_SAVELOGIN, (String) rememberMe.getValueAsBoolean().toString());
			Offline.put(Constants.COOKIE_USER, rememberMe.getValueAsBoolean() ? (String) username.getValue() : "");
			Offline.put(Constants.COOKIE_PASSWORD, rememberMe.getValueAsBoolean() ? (String) password.getValue() : "");
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
}