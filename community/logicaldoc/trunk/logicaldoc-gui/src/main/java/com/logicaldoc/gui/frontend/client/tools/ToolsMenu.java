package com.logicaldoc.gui.frontend.client.tools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIParameter;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SettingService;
import com.logicaldoc.gui.frontend.client.services.SettingServiceAsync;
import com.logicaldoc.gui.frontend.client.system.LastChangesPanel;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the tools configurations menu
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.1
 */
public class ToolsMenu extends VLayout {
	private SettingServiceAsync service = (SettingServiceAsync) GWT.create(SettingService.class);

	public ToolsMenu() {
		setMargin(10);
		setMembersMargin(5);
		
		Button lastChanges = new Button(I18N.message("lastchanges"));
		lastChanges.setWidth100();
		lastChanges.setHeight(25);
		if (Menu.enabled(Menu.LAST_CHANGES))
			addMember(lastChanges);

		Button duplicates = new Button(I18N.message("searchduplicates"));
		duplicates.setWidth100();
		duplicates.setHeight(25);

		Button clientTools = new Button(I18N.message("clienttools"));
		clientTools.setWidth100();
		clientTools.setHeight(25);
		
		lastChanges.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new LastChangesPanel());
			}
		});

		if (Feature.visible(Feature.DUPLICATES_DISCOVERY)) {
			addMember(duplicates);
			if (!Feature.enabled(Feature.DUPLICATES_DISCOVERY)) {
				duplicates.setDisabled(true);
				duplicates.setTooltip(I18N.message("featuredisabled"));
			}
		}
		
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

		duplicates.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdminPanel.get().setContent(new DuplicatesPanel());
			}
		});
	}
}
