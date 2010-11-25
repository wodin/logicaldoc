package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUIMessage;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.widgets.MessageLabel;
import com.logicaldoc.gui.frontend.client.Frontend;
import com.logicaldoc.gui.frontend.client.folder.FoldersNavigator;
import com.logicaldoc.gui.frontend.client.personal.ChangePassword;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
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

	protected SelectItem language;

	public LoginPanel(GUIInfo info) {
		setDefaultLayoutAlign(Alignment.CENTER);
		setWidth100();
		setHeight100();

		// Panel for horizontal centering
		HLayout hPanel = new HLayout();
		hPanel.setDefaultLayoutAlign(Alignment.LEFT);
		hPanel.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		hPanel.setWidth(350);
		hPanel.setHeight100();

		// Panel for vertical centering
		VLayout content = new VLayout();
		content.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		content.setWidth100();
		content.setHeight(200);
		hPanel.addMember(content);

		addMember(hPanel);

		// Prepare the form header that contains the product name and version
		HTML header = new HTML(info.getProductName() + " " + info.getRelease());
		header.setStyleName("loginHeader");
		header.setHeight("12px");

		// Prepare the logo image to be shown inside the login form
		Img logoImage = ItemFactory.newBrandImg("logo.png");
		logoImage.setHeight("40px");
		logoImage.setWidth("205px");

		// Prepare the form footer that contains copyright and website link
		String htmlString = "\u00A9 " + info.getYear() + " " + info.getVendor() + "  &#160; &#8226; &#160; <a href='"
				+ info.getUrl() + "'>" + info.getUrl() + "</a>";
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

		language = ItemFactory.newLanguageSelector("language", true, true);
		language.setDefaultValue("");

		form.setFields(username, password, language);

		IButton loginButton = new IButton(I18N.message("login"));
		loginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onLogin();
			}
		});

		Layout inner = new VLayout();
		inner.setShowEdges(true);
		inner.addMember(logoImage);
		Layout separator = new VLayout();
		separator.setHeight(8);
		inner.addMember(separator);
		inner.addMember(form);
		inner.addMember(separator);
		inner.addMember(loginButton);
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

		String img = "<img style='visibility:hidden' src='http://stat.logicaldoc.com/getimg.php?name="
				+ info.getProductName() + "&release=" + info.getRelease() + "&vendor=" + info.getVendor() + "&"
				+ info.getInstallationId() + "' width='1px' height='1px' border='0'/>";
		HTMLFlow f = new HTMLFlow(img);
		addMember(f);
	}

	protected void onLogin() {
		securityService.login((String) username.getValue(), (String) password.getValue(), (String) language.getValue(),
				new AsyncCallback<GUISession>() {
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						SC.warn(caught.getMessage());
					}

					@Override
					public void onSuccess(GUISession session) {
						if (session.isLoggedIn()) {
							try {
								Session.get().init(session);
								Frontend.get().showMain();
							} catch (Throwable e) {
								SC.warn(e.getMessage());
							}
							FoldersNavigator.get().selectFolder(Constants.DOCUMENTS_FOLDERID);
							
						} else if (session.getUser() != null && session.getUser().isExpired()) {
							new ChangePassword(session.getUser(), "needtochangepassword").show();
						} else {
							SC.warn(I18N.message("accesdenied"));
						}
					}
				});
	}
}