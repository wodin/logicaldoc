package com.logicaldoc.gui.frontend.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.beans.GUISession;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.gui.common.client.services.InfoServiceAsync;
import com.logicaldoc.gui.common.client.services.SecurityService;
import com.logicaldoc.gui.common.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.common.client.util.RequestInfo;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.folder.FolderNavigator;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.TagsForm;
import com.logicaldoc.gui.frontend.client.security.LoginPanel;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

/**
 * The Frontend entry point
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Frontend implements EntryPoint {

	// True when the user alreafy entered the main screen
	static boolean entered = false;

	private static Frontend instance;

	private LoginPanel loginPanel;

	private MainPanel mainPanel;

	protected InfoServiceAsync infoService = (InfoServiceAsync) GWT.create(InfoService.class);

	protected SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	/**
	 * @return singleton Main instance
	 */
	public static Frontend get() {
		return instance;
	}

	@Override
	public void onModuleLoad() {
		if (RootPanel.get("loadingWrapper") == null)
			return;

		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable caught) {
				// Log unhandled errors only when in devel mode
				if (Session.get().isDevel())
					Log.error(caught.getMessage(), null, caught);
			}

		});

		instance = this;

		// Setup the language for localization
		final RequestInfo request = WindowUtils.getRequestInfo();

		// Tries to capture locale parameter
		final String lang;
		if (request.getParameter(Constants.LOCALE) != null && !request.getParameter(Constants.LOCALE).equals("")) {
			lang = request.getParameter(Constants.LOCALE);
		} else {
			// First we initialize language values
			lang = Util.getBrowserLanguage();
		}
		I18N.setLocale(lang);

		// Tries to capture tenant parameter
		final String tenant = Util.detectTenant();

		// Get grid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");

		mainPanel = MainPanel.get();

		declareReloadTrigger(this);
		declareSearchTag(this);
		declareGetCurrentFolderId(this);
		declareCheckPermission(this);
		declareShowForgotDialog(this);

		infoService.getInfo(I18N.getLocale(), tenant, new AsyncCallback<GUIInfo>() {
			@Override
			public void onFailure(Throwable error) {
				SC.warn(error.getMessage());
			}

			@Override
			public void onSuccess(final GUIInfo info) {
				// Store the release information
				Cookies.setCookie(Constants.COOKIE_VERSION, info.getRelease());

				I18N.init(info);

				WindowUtils.setTitle(info, null);

				Feature.init(info);
				Session.get().setInfo(info);

				WindowUtils.setFavicon(info);

				String sid = Util.detectSid();

				final boolean anonymousLogin = request.getParameter(Constants.ANONYMOUS) != null
						&& "true".equals(info.getConfig("anonymous.enabled"));

				loginPanel = new LoginPanel(info);
				if (sid == null || "".equals(sid.trim())) {
					if (anonymousLogin) {
						/*
						 * Simulate a login with the anonymous user
						 */
						anonymousLogin(lang, info);
					} else
						Frontend.this.showInitialLogin();
				} else {
					securityService.login(sid, new AsyncCallback<GUISession>() {

						@Override
						public void onFailure(Throwable caught) {
							Frontend.this.showInitialLogin();
						}

						@Override
						public void onSuccess(GUISession session) {
							if (session == null || !session.isLoggedIn()) {
								if (anonymousLogin) {
									/*
									 * Simulate a login with the anonymous user
									 */
									anonymousLogin(lang, info);
								} else
									Frontend.this.showInitialLogin();
							} else {
								MainPanel.get();
								loginPanel.onLoggedIn(session);

								// Remove the loading frame
								RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
								declareReloadTrigger(Frontend.this);
							}
						}
					});
				}
			}

			private void anonymousLogin(final String lang, final GUIInfo info) {
				securityService.login(info.getConfig("anonymous.user"), "", lang, tenant,
						new AsyncCallback<GUISession>() {

							@Override
							public void onFailure(Throwable caught) {
								Frontend.this.showInitialLogin();
							}

							@Override
							public void onSuccess(GUISession session) {
								if (session == null || !session.isLoggedIn()) {
									Frontend.this.showInitialLogin();
								} else {
									MainPanel.get();
									loginPanel.onLoggedIn(session);

									// Remove the loading frame
									RootPanel.getBodyElement()
											.removeChild(RootPanel.get("loadingWrapper").getElement());
									declareReloadTrigger(Frontend.this);
								}
							}
						});
			}
		});
	}

	// Switch to the login panel
	public void showLogin() {
		mainPanel.hide();
		loginPanel.initGUI();
		loginPanel.show();
		entered = false;
	}

	// Setup the initial visualization of the login panel
	private void showInitialLogin() {
		RootPanel.get().add(loginPanel);

		// Remove the loading frame
		RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
		declareReloadTrigger(Frontend.this);

		showLogin();
	}

	public void showMain() {
		if (entered)
			return;
		if (Session.get().getIncomingMessage() != null) {
			mainPanel.getIncomingMessage().setMessage(Session.get().getIncomingMessage());
			mainPanel.getIncomingMessage().setClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					mainPanel.getIncomingMessage().setVisible(false);
				}
			});
			mainPanel.getIncomingMessage().setVisible(true);
		}

		mainPanel.show();
		loginPanel.hide();
		entered = true;
	}

	/**
	 * Triggers the load of the last uploaded files
	 */
	public void triggerReload() {
		FolderNavigator.get().reload();
	}

	public String getCurrentFolderId() {
		return Long.toString(Session.get().getCurrentFolder().getId());
	}

	public String checkPermission(String permission) {
		return Boolean.toString(Session.get().getCurrentFolder().hasPermission(permission));
	}

	public void searchTag(String tag) {
		TagsForm.searchTag(tag, false);
	}

	public void addTagInCloud(String tag, String weight, String link) {
		TagsForm.searchTag(tag, false);
	}

	public void showForgotDialog(String productName) {
		LoginPanel.onForgottenPwd(productName);
	}

	/**
	 * Declares the javascript function used to check a permission in the
	 * current folder.
	 */
	public static native void declareCheckPermission(Frontend frontend) /*-{
		$wnd.checkPermission = function(permission) {
			return frontend.@com.logicaldoc.gui.frontend.client.Frontend::checkPermission(Ljava/lang/String;)(permission);
		};
	}-*/;

	/**
	 * Declares the javascript function used to retrieve the current folder ID.
	 */
	public static native void declareGetCurrentFolderId(Frontend frontend) /*-{
		$wnd.getCurrentFolderId = function() {
			return frontend.@com.logicaldoc.gui.frontend.client.Frontend::getCurrentFolderId()();
		};
	}-*/;

	/**
	 * Declares the javascript function used to trigger the reload of the
	 * current folder.
	 */
	public static native void declareReloadTrigger(Frontend frontend) /*-{
		$wnd.triggerReload = function() {
			frontend.@com.logicaldoc.gui.frontend.client.Frontend::triggerReload()();
		};
	}-*/;

	/**
	 * Declares the javascript function used to trigger the search for a
	 * specific tag.
	 */
	public static native void declareSearchTag(Frontend frontend) /*-{
		$wnd.searchTag = function(tag) {
			frontend.@com.logicaldoc.gui.frontend.client.Frontend::searchTag(Ljava/lang/String;)(tag);
		};
	}-*/;

	/**
	 * Declares the javascript function used to display the forgot password
	 * popup
	 */
	public static native void declareShowForgotDialog(Frontend frontend) /*-{
		$wnd.showForgotDialog = function(productName) {
			return frontend.@com.logicaldoc.gui.frontend.client.Frontend::showForgotDialog(Ljava/lang/String;)(productName);
		};
	}-*/;
}