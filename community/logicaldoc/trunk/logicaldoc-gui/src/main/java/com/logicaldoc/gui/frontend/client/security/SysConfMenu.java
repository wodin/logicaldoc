package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIWebServiceSettings;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
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
public class SysConfMenu extends VLayout {
	private SecurityServiceAsync service = (SecurityServiceAsync) GWT.create(SecurityService.class);

	public SysConfMenu() {
		setMargin(10);
		setMembersMargin(5);

		Button clientTools = new Button(I18N.getMessage("clienttools"));
		clientTools.setWidth100();
		clientTools.setHeight(25);

		Button parameters = new Button(I18N.getMessage("parameters"));
		parameters.setWidth100();
		parameters.setHeight(25);

		Button email = new Button(I18N.getMessage("email"));
		email.setWidth100();
		email.setHeight(25);

		setMembers(clientTools, parameters, email);

		clientTools.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.loadWSSettings(Session.get().getSid(), new AsyncCallback<GUIWebServiceSettings[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(GUIWebServiceSettings[] settings) {
						AdminPanel.get().setContent(new ClientToolsSettingsPanel(settings));
					}

				});
			}
		});

		// parameters.addClickHandler(new ClickHandler() {
		// @Override
		// public void onClick(ClickEvent event) {
		// service.loadSettings(Session.get().getSid(), new
		// AsyncCallback<GUISecuritySettings>() {
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// Log.serverError(caught);
		// }
		//
		// @Override
		// public void onSuccess(GUISecuritySettings settings) {
		// AdminPanel.get().setContent(new SecuritySettingsPanel(settings));
		// }
		//
		// });
		// }
		// });

		// email.addClickHandler(new ClickHandler() {
		// @Override
		// public void onClick(ClickEvent event) {
		// service.loadExtAuthSettings(Session.get().getSid(), new
		// AsyncCallback<GUILdapSettings[]>() {
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// Log.serverError(caught);
		// }
		//
		// @Override
		// public void onSuccess(GUILdapSettings[] settings) {
		// AdminPanel.get().setContent(new ExtAuthPanel(settings));
		// }
		//
		// });
		// }
		// });
	}
}
