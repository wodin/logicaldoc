package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the saved searches of the user
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityMenu extends VLayout {
	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	public SecurityMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button users = new Button(I18N.getMessage("users"));
		users.setWidth100();
		users.setHeight(25);

		Button groups = new Button(I18N.getMessage("groups"));
		groups.setWidth100();
		groups.setHeight(25);

		Button security = new Button(I18N.getMessage("security"));
		security.setWidth100();
		security.setHeight(25);

		Button extAuth = new Button(I18N.getMessage("extauth"));
		extAuth.setWidth100();
		extAuth.setHeight(25);

		setMembers(users, groups, security, extAuth);

		users.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new UsersPanel());
			}
		});

		groups.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new GroupsPanel());
			}
		});

		security.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.loadSettings(Session.get().getSid(), new AsyncCallback<GUISecuritySettings>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUISecuritySettings settings) {
						AdminPanel.get().setContent(new SecuritySettingsPanel(settings));
					}

				});
			}
		});

		extAuth.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.loadExtAuthSettings(Session.get().getSid(), new AsyncCallback<GUILdapSettings[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUILdapSettings[] settings) {
						AdminPanel.get().setContent(new ExtAuthPanel(settings));
					}

				});
			}
		});
	}
}