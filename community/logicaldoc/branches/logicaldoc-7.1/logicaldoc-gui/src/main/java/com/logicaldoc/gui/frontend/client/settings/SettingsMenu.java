package com.logicaldoc.gui.frontend.client.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIEmailSettings;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.beans.GUISearchEngine;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.widgets.ContactingServer;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SearchEngineService;
import com.logicaldoc.gui.frontend.client.services.SearchEngineServiceAsync;
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
	private SettingServiceAsync settingService = (SettingServiceAsync) GWT.create(SettingService.class);

	private SearchEngineServiceAsync seService = (SearchEngineServiceAsync) GWT.create(SearchEngineService.class);

	public SettingsMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button searchAndIndexing = new Button(I18N.message("searchandindexing"));
		searchAndIndexing.setWidth100();
		searchAndIndexing.setHeight(25);
		addMember(searchAndIndexing);

		Button folders = new Button(I18N.message("repositories"));
		folders.setWidth100();
		folders.setHeight(25);
		if (Session.get().isDefaultTenant())
			addMember(folders);

		Button guiSettings = new Button(I18N.message("guisettings"));
		guiSettings.setWidth100();
		guiSettings.setHeight(25);
		addMember(guiSettings);

		Button ocr = new Button(I18N.message("ocr"));
		ocr.setWidth100();
		ocr.setHeight(25);
		if (Feature.visible(Feature.OCR)) {
			addMember(ocr);
			if (!Feature.enabled(Feature.OCR)) {
				ocr.setDisabled(true);
				ocr.setTooltip(I18N.message("featuredisabled"));
			}
		}

		Button smtp = new Button(I18N.message("outgoingemail"));
		smtp.setWidth100();
		smtp.setHeight(25);
		if (Session.get().isDemo()) {
			smtp.setDisabled(true);
			smtp.setTooltip(I18N.message("featuredisabled"));
		}

		Button clientTools = new Button(I18N.message("clienandextapps"));
		clientTools.setWidth100();
		clientTools.setHeight(25);

		Button parameters = new Button(I18N.message("parameters"));
		parameters.setWidth100();
		parameters.setHeight(25);

		if (Feature.visible(Feature.CLIENT_TOOLS) && Menu.enabled(Menu.CLIENTS)) {
			addMember(clientTools);
			if (!Feature.enabled(Feature.CLIENT_TOOLS)) {
				clientTools.setDisabled(true);
				clientTools.setTooltip(I18N.message("featuredisabled"));
			}
		}

		clientTools.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				settingService.loadClientSettings(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] settings) {
						AdminPanel.get().setContent(new ExternalAppsPanel(settings));
					}

				});
			}
		});

		addMember(smtp);

		if (Session.get().isDefaultTenant() && Menu.enabled(Menu.PARAMETERS))
			addMember(parameters);

		searchAndIndexing.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ContactingServer.get().show();

				seService.getInfo(Session.get().getSid(), new AsyncCallback<GUISearchEngine>() {

					@Override
					public void onFailure(Throwable caught) {
						ContactingServer.get().hide();
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUISearchEngine searchEngine) {
						ContactingServer.get().hide();
						AdminPanel.get().setContent(new SearchIndexingPanel(searchEngine));
					}

				});
			}
		});

		guiSettings.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				settingService.loadGUISettings(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] settings) {
						AdminPanel.get().setContent(new GUISettingsPanel(settings));
					}
				});
			}
		});

		ocr.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				settingService.loadOcrSettings(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[] settings) {
						AdminPanel.get().setContent(new OCRSettingsPanel(settings));
					}
				});
			}
		});

		parameters.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				settingService.loadSettings(Session.get().getSid(), new AsyncCallback<GUIParameter[]>() {

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

		smtp.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				settingService.loadEmailSettings(Session.get().getSid(), new AsyncCallback<GUIEmailSettings>() {

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

		folders.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				settingService.loadRepositories(Session.get().getSid(), new AsyncCallback<GUIParameter[][]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIParameter[][] repos) {
						AdminPanel.get().setContent(new RepositoriesPanel(repos));
					}
				});
			}
		});
	}
}