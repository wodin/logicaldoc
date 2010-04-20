package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.Main;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * The Login entry point
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LoginPanel extends VLayout {

	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private TextItem usernameItem = new TextItem();

	private PasswordItem passwordItem = new PasswordItem();

	public LoginPanel() {
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
		VLayout vPanel = new VLayout();
		vPanel.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		vPanel.setWidth100();
		vPanel.setHeight(200);
		hPanel.addMember(vPanel);

		addMember(hPanel);

		// Collect some context infos to define window's and form's title
		Dictionary context = Util.getContext();
		WindowUtils.setTitle(context.get("product_name") + " " + context.get("product_release"));

		// Prepare the form header that contains the product name and version
		HTML header = new HTML(context.get("product_name") + " " + context.get("product_release"));
		header.setStyleName("loginHeader");
		header.setHeight("10px");

		// Prepare the logo image to be shown inside the login form
		Image logoImage = new Image(Util.imageUrl("brand/logo.png"));
		logoImage.setHeight("40px");

		// Prepare the form footer that contains copyright and website link
		HTML footer = new HTML("\u00A9 " + context.get("product_year") + " " + context.get("product_vendor")
				+ "  &#160; &#8226; &#160; <a href='" + context.get("product_url") + "'>" + context.get("product_url")
				+ "</a>");
		footer.setStyleName("loginFooter");

		// Prepare the Form and all its fields
		final DynamicForm form = new DynamicForm();
		form.setAlign(Alignment.CENTER);
		form.setAutoFocus(true);

		usernameItem.setTitle(I18N.getMessage("username"));
		usernameItem.setRequired(true);
		usernameItem.setWrapTitle(false);
		usernameItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ("enter".equals(event.getKeyName().toLowerCase()))
					onLogin();
			}
		});

		passwordItem.setTitle(I18N.getMessage("password"));
		passwordItem.setRequired(true);
		passwordItem.setWrapTitle(false);
		passwordItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if ("enter".equals(event.getKeyName().toLowerCase()))
					onLogin();
			}
		});

		form.setFields(new FormItem[] { usernameItem, passwordItem });

		IButton loginButton = new IButton(I18N.getMessage("login"));
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

		Layout outer = new VLayout();
		outer.addMember(header);
		outer.addMember(inner);
		outer.addMember(footer);
		outer.setPadding(2);

		vPanel.addMember(outer);

		usernameItem.setSelectOnFocus(true);
	}

	private void onLogin() {
		securityService.login((String) usernameItem.getValue(), (String) passwordItem.getValue(),
				new AsyncCallback<GUIUser>() {
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIUser user) {
						if (user != null) {
							if (user.isExpired()) {
								if (user.isExpired()) {
									new ChangePassword(user).show();
								}
							} else {
								Session.get().setUser(user);
								Main.get().showMain();
							}
						} else {
							SC.warn(I18N.getMessage("accesdenied"));
						}
					}
				});
	}
}