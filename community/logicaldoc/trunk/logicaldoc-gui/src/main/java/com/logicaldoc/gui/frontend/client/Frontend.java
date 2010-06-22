package com.logicaldoc.gui.frontend.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.RequestInfo;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.TagsForm;
import com.logicaldoc.gui.frontend.client.security.LoginPanel;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.util.SC;

/**
 * The Frontend entry point
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class Frontend implements EntryPoint {

	private static Frontend instance;

	private LoginPanel loginPanel;

	private MainPanel mainPanel;

	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	/**
	 * @return singleton Main instance
	 */
	public static Frontend get() {
		return instance;
	}

	@Override
	public void onModuleLoad() {
		// Export some javascripts
		TagsForm.exportStearchTag();

		if (RootPanel.get("loadingWrapper") == null)
			return;

		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable caught) {
				Log.serverError(caught);
			}

		});

		instance = this;

		// Setup the language for localization
		RequestInfo loc = WindowUtils.getRequestInfo();

		// Tries to capture locale parameter
		String lang;
		if (loc.getParameter(Constants.LOCALE) != null && !loc.getParameter(Constants.LOCALE).equals("")) {
			lang = loc.getParameter(Constants.LOCALE);
		} else {
			// First we initialize language values
			lang = Util.getBrowserLanguage();
		}
		I18N.setLocale(lang);

		// Get grid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");

		mainPanel = MainPanel.get();

		systemService.getInfo(I18N.getLocale(), new AsyncCallback<GUIInfo>() {
			@Override
			public void onFailure(Throwable error) {
				SC.warn(error.getMessage());
			}

			@Override
			public void onSuccess(GUIInfo info) {
				I18N.init(info);

				loginPanel = new LoginPanel(info);
				RootPanel.get().add(loginPanel);

				// Remove the loading frame
				RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
			}
		});
	}

	public void showLogin() {
		mainPanel.hide();
		loginPanel.show();
	}

	public void showMain() {
		mainPanel.show();
		loginPanel.hide();
	}
}