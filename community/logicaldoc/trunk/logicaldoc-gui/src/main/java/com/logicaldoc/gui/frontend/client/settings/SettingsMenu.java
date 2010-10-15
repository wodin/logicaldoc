package com.logicaldoc.gui.frontend.client.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the administration system configurations menu
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class SettingsMenu extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	private SettingServiceAsync settingService = (SettingServiceAsync) GWT.create(SettingService.class);

	public SettingsMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button folders = new Button(I18N.message("folders"));
		folders.setWidth100();
		folders.setHeight(25);

		Button clientTools = new Button(I18N.message("clienttools"));
		clientTools.setWidth100();
		clientTools.setHeight(25);

		Button parameters = new Button(I18N.message("parameters"));
		parameters.setWidth100();
		parameters.setHeight(25);

		Button proxy = new Button(I18N.message("proxy"));
		proxy.setWidth100();
		proxy.setHeight(25);

		Button email = new Button(I18N.message("email"));
		email.setWidth100();
		email.setHeight(25);

		if (Feature.visible(Feature.CLIENT_TOOLS) && Menu.enabled(Menu.CLIENTS)) {
			addMember(clientTools);
			if (!Feature.enabled(Feature.CLIENT_TOOLS)) {
				clientTools.setDisabled(true);
				clientTools.setTooltip(I18N.message("featuredisabled"));
			}
		}

		addMember(email);
		addMember(proxy);
		addMember(folders);
		addMember(parameters);

		clientTools.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.loadClientSettings(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] settings) {
						AdminPanel.get().setContent(new ClientToolsSettingsPanel(settings));
					}

				});
			}
		});

		parameters.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.loadSettings(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] settings) {
						AdminPanel.get().setContent(new ParametersSettingsPanel(settings));
					}

				});
			}
		});

		email.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.loadEmailSettings(Session.get().getSid(), new AsyncCallback<GUIEmailSettings>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIEmailSettings settings) {
						AdminPanel.get().setContent(new EmailPanel(settings));
					}

				});
			}
		});

		proxy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.loadProxySettings(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] settings) {
						AdminPanel.get().setContent(new ProxyPanel(settings));
					}
				});
			}
		});

		folders.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				settingService.loadFolders(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] folders) {
						AdminPanel.get().setContent(new FoldersPanel(folders));
					}
				});
			}
		});
	}
}