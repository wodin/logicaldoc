package com.logicaldoc.gui.login.client;

import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.ItemFactory;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
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
public class MinimalLoginPanel extends LoginPanel {

	public MinimalLoginPanel(GUIInfo info) {
		super(info);
	}

	@Override
	protected void initGUI() {
		// Prepare the logo to show in the login form
		Img logoLogin = ItemFactory.newBrandImg(!info.isLogoOemCustomized() ? "logo.png" : "logo_oem.png", info);
		logoLogin.setWidth(205);
		logoLogin.setHeight(40);
		logoLogin.setAlign(Alignment.CENTER);
		logoLogin.setLayoutAlign(Alignment.CENTER);

		HLayout spacer15 = new HLayout();
		spacer15.setHeight(15);
		spacer15.setWidth(15);

		HLayout spacer10 = new HLayout();
		spacer10.setHeight(10);
		spacer10.setWidth(10);

		HLayout spacer30 = new HLayout();
		spacer30.setHeight(30);
		spacer30.setWidth(30);

		HLayout ieSpacer15 = new HLayout();
		ieSpacer15.setHeight(15);
		ieSpacer15.setWidth(15);

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
		username.setHeight(34);
		username.setWidth(280);
		username.setAlign(Alignment.CENTER);
		username.setTextBoxStyle("login-input");
		username.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onLoginClicked();
			}
		});

		password = ItemFactory.newPasswordItem("password", "password", null);
		password.setShowTitle(false);
		password.setHeight(34);
		password.setWidth(280);
		password.setTextBoxStyle("login-input");
		password.setAlign(Alignment.CENTER);
		password.setWrapTitle(false);
		password.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName() != null && "enter".equals(event.getKeyName().toLowerCase()))
					onLoginClicked();
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

		SpacerItem spacerItem = new SpacerItem();
		spacerItem.setHeight(12);

		form.setFields(username, spacerItem, password, language);

		Button signIn = newBigButton("signin");
		signIn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				onLoginClicked();
			}
		});

		Label classicView = new Label(I18N.message("viewclassicweb"));
		classicView.setStyleName("mobile-classic-view");
		classicView.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Util.redirectToRoot("login", "skipdetectmobile=true");
			}
		});

		// Panel containing the login form
		formLayout.setLayoutAlign(VerticalAlignment.TOP);
		formLayout.setLayoutAlign(Alignment.CENTER);
		formLayout.setAlign(VerticalAlignment.TOP);
		formLayout.setWidth(298);
		formLayout.setHeight(480);
		formLayout.setMembersMargin(0);
		formLayout.setMembers(spacer10, logoLogin, spacer30, form, spacer30, signIn, spacer10, classicView);

		// Main Panel (covers 100% of the screen)
		VLayout mainPanel = new VLayout();
		mainPanel.setWidth100();
		mainPanel.setHeight100();
		mainPanel.setLayoutAlign(VerticalAlignment.TOP);
		mainPanel.setMembers(formLayout);
		addMember(mainPanel);

		form.focusInItem(username);
		form.setAutoFocus(true);
		form.focus();
	}
	
	private static Button newBigButton(String title) {
		Button button = new Button(I18N.message(title));
		button.setLayoutAlign(Alignment.CENTER);
		button.setWidth(283);
		button.setHeight(43);
		button.setBaseStyle("mobile-bigbutton");
		button.setAlign(Alignment.CENTER);

		return button;
	}
}