package com.logicaldoc.gui.frontend.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.RequestInfo;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.i18n.FrontendMessages;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.TagsForm;
import com.logicaldoc.gui.frontend.client.security.LoginPanel;

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

	private static FrontendMessages messages;

	public static FrontendMessages messages() {
		return messages;
	}

	/**
	 * @return singleton Main instance
	 */
	public static Frontend get() {
		return instance;
	}

	private static final String LOCALE = "locale";

	@Override
	public void onModuleLoad() {
		messages = GWT.create(FrontendMessages.class);

		// Export some javascripts
		TagsForm.exportStearchTag();

		if (RootPanel.get("loadingWrapper") == null)
			return;

		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(Throwable t) {
				Log.serverError(t);
			}

		});

		instance = this;

		// Setup the language for localization
		RequestInfo loc = WindowUtils.getRequestInfo();

		// Tries to capture locale parameter
		String lang;
		if (loc.getParameter(LOCALE) != null && !loc.getParameter(LOCALE).equals("")) {
			lang = loc.getParameter(LOCALE);
		} else {
			// First we initialize language values
			lang = Util.getBrowserLanguage();
		}
		I18N.setLanguage(lang);

		// Get grid of scrollbars, and clear out the window's built-in margin,
		// because we want to take advantage of the entire client area.
		Window.enableScrolling(false);
		Window.setMargin("0px");

		loginPanel = new LoginPanel();
		mainPanel = MainPanel.get();

		RootPanel.get().add(loginPanel);

		// Remove the loading frame
		RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());
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