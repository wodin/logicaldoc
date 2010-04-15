package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.SessionObserver;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.Log;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * This panel shows the login informations and the logout button
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LoginInfo extends HLayout implements SessionObserver {
	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	private Label loginLabel;

	public LoginInfo() {
		loginLabel = new Label(I18N.getMessage("loggedin"));
		loginLabel.setWordWrap(false);
		loginLabel.setStyleName("loginInfo");
		addMember(loginLabel);

		Anchor logout = new Anchor();
		logout.setText(I18N.getMessage("logout"));
		logout.setStyleName("logout");
		logout.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				securityService.logout(Session.getInstance().getSid(), new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void arg0) {
						Session.getInstance().close();
						String base = GWT.getHostPageBaseURL();
						redirect(base + (base.endsWith("/") ? "frontend.jsp" : "/frontend.jsp"));
					}
				});
			}
		});
		addMember(logout);
		Session.getInstance().addSessionObserver(this);
	}

	@Override
	public void onUserLoggedIn(GUIUser user) {
		loginLabel.setText(loginLabel.getText() + " " + user.getUserName() + " |");
	}

	native void redirect(String url)
	/*-{
		$wnd.location.replace(url);
	}-*/;
}