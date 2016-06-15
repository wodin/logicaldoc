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
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.folder.FolderNavigator;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.TagsForm;
import com.smartgwt.client.util.SC;

/**
 * The Frontend entry point
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Frontend implements EntryPoint {

	private static Frontend instance;

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
		if (RootPanel.get("loadingwrapper-frontend") == null)
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

		// Tries to capture locale parameter
		final String locale = Util.detectLocale();
		I18N.setLocale(locale);

		// Tries to capture tenant parameter
		final String tenant = Util.detectTenant();

		final String sid = Util.detectSid();

		// Get grid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");

		mainPanel = MainPanel.get();

		declareReloadTrigger(this);
		declareSearchTag(this);
		declareGetCurrentFolderId(this);
		declareCheckPermission(this);

		infoService.getInfo(locale, tenant, new AsyncCallback<GUIInfo>() {
			@Override
			public void onFailure(Throwable error) {
				SC.warn(error.getMessage());
			}

			@Override
			public void onSuccess(final GUIInfo info) {
				// Store the release information
				Cookies.setCookie(Constants.COOKIE_VERSION, info.getRelease(), null, null, null, WindowUtils
						.getRequestInfo().isSecure());

				I18N.init(info);

				WindowUtils.setTitle(info, null);

				Feature.init(info);
				Session.get().setInfo(info);

				WindowUtils.setFavicon(info);

				if (sid == null || "".equals(sid))
					SC.warn(I18N.message("accessdenied"));

				securityService.getSession(Util.getLocaleInRequest(), new AsyncCallback<GUISession>() {

					@Override
					public void onFailure(Throwable caught) {
						SC.warn(I18N.message("accessdenied"));
					}

					@Override
					public void onSuccess(GUISession session) {
						if (session == null || !session.isLoggedIn()) {
							SC.warn(I18N.message("accessdenied"));
						} else {
							Session.get().init(session);
							I18N.setLocale(session.getUser().getLanguage());
							showMain();
							declareReloadTrigger(Frontend.this);
						}
					}
				});

			}
		});
	}

	public void showMain() {
		// Remove the loading frame
		RootPanel.getBodyElement().removeChild(RootPanel.get("loadingwrapper-frontend").getElement());
		mainPanel.show();
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
}