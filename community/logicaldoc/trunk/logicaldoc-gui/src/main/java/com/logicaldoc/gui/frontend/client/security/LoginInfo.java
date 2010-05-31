package com.logicaldoc.gui.frontend.client.security;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * This panel shows the login informations and the logout button
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class LoginInfo extends VLayout {
	private SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	public LoginInfo(String stylePrefix) {
		setHeight(30);
		setAlign(Alignment.RIGHT);

		Label user = new Label(Session.get().getUser().getFullName());
		user.setHeight(10);
		user.setAlign(Alignment.RIGHT);
		user.setMargin(2);
		user.setStyleName(stylePrefix + "loginInfo");

		Label logout = new Label(I18N.message("logout"));
		logout.setHeight(10);
		logout.setAlign(Alignment.RIGHT);
		logout.setStyleName(stylePrefix + "logout");
		logout.setCursor(Cursor.HAND);
		logout.setMargin(2);
		logout.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				securityService.logout(Session.get().getSid(), new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void arg0) {
						Session.get().close();
						String base = GWT.getHostPageBaseURL();
						redirect(base
								+ (base.endsWith("/") ? GWT.getModuleName() + ".jsp" : "/" + GWT.getModuleName()
										+ ".jsp"));
					}
				});
			}

		});
		// logout.addClickHandler(new
		// com.google.gwt.event.dom.client.ClickHandler() {
		// @Override
		// public void onClick(com.google.gwt.event.dom.client.ClickEvent event)
		// {
		// securityService.logout(Session.get().getSid(), new
		// AsyncCallback<Void>() {
		// public void onFailure(Throwable caught) {
		// Log.serverError(caught);
		// }
		//
		// @Override
		// public void onSuccess(Void arg0) {
		// Session.get().close();
		// String base = GWT.getHostPageBaseURL();
		// redirect(base
		// + (base.endsWith("/") ? GWT.getModuleName() + ".jsp" : "/" +
		// GWT.getModuleName()
		// + ".jsp"));
		// }
		// });
		// }
		// });

		addMember(user);
		addMember(logout);
	}

	native void redirect(String url)
	/*-{
		$wnd.location.replace(url);
	}-*/;
}