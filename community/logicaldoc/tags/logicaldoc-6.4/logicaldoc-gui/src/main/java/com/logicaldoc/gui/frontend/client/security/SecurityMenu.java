package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUILdapSettings;
import com.logicaldoc.gui.common.client.beans.GUISecuritySettings;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.LdapService;
import com.logicaldoc.gui.frontend.client.services.LdapServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the administration security menu
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class SecurityMenu extends VLayout {
	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private LdapServiceAsync ldapService = (LdapServiceAsync) GWT.create(LdapService.class);

	public SecurityMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button users = new Button(I18N.message("users"));
		users.setWidth100();
		users.setHeight(25);

		Button groups = new Button(I18N.message("groups"));
		groups.setWidth100();
		groups.setHeight(25);

		Button security = new Button(I18N.message("security"));
		security.setWidth100();
		security.setHeight(25);

		Button extAuth = new Button(I18N.message("extauth"));
		extAuth.setWidth100();
		extAuth.setHeight(25);

		if (Feature.visible(Feature.LDAP)) {
			setMembers(users, groups, security, extAuth);
			if (!Feature.enabled(Feature.LDAP)) {
				extAuth.setDisabled(true);
				extAuth.setTooltip(I18N.message("featuredisabled"));
			}
		} else
			setMembers(users, groups, security);

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
				ldapService.loadSettings(Session.get().getSid(), new AsyncCallback<GUILdapSettings>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUILdapSettings settings) {
						AdminPanel.get().setContent(new LdapPanel(settings));
					}

				});
			}
		});
	}
}