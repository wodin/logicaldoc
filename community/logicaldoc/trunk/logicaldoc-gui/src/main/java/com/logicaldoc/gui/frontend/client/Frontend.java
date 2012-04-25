package com.logicaldoc.gui.frontend.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.logicaldoc.gui.common.client.Config;
import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIInfo;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.services.InfoService;
import com.logicaldoc.gui.common.client.services.InfoServiceAsync;
import com.logicaldoc.gui.common.client.util.RequestInfo;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.panels.MainPanel;
import com.logicaldoc.gui.frontend.client.search.TagsForm;
import com.logicaldoc.gui.frontend.client.security.LoginPanel;
import com.logicaldoc.gui.frontend.client.services.DocumentService;
import com.logicaldoc.gui.frontend.client.services.DocumentServiceAsync;
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

		setUploadTrigger(this);
		setSearchTag(this);

		infoService.getInfo(I18N.getLocale(), new AsyncCallback<GUIInfo>() {
			@Override
			public void onFailure(Throwable error) {
				SC.warn(error.getMessage());
			}

			@Override
			public void onSuccess(GUIInfo info) {
				Config.init(info);
				I18N.init(info);

				WindowUtils.setTitle(info, null);

				Feature.init(info);
				Session.get().setInfo(info);

				loginPanel = new LoginPanel(info);
				RootPanel.get().add(loginPanel);

				// Remove the loading frame
				RootPanel.getBodyElement().removeChild(RootPanel.get("loadingWrapper").getElement());

				setUploadTrigger(Frontend.this);
			}
		});
	}

	public void showLogin() {
		mainPanel.hide();
		loginPanel.show();
		entered = false;
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
	public void triggerUpload() {
		DocumentServiceAsync documentService = (DocumentServiceAsync) GWT.create(DocumentService.class);
		documentService.addDocuments(Session.get().getSid(), I18N.getLocale(),
				Session.get().getCurrentFolder().getId(), "UTF-8", false, null, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
					}

					@Override
					public void onSuccess(Void result) {
						DocumentsPanel.get().refresh();
					}
				});
	}

	public void searchTag(String tag) {
		TagsForm.searchTag(tag, false);
	}

	/**
	 * Declares the javascript function used to trigger the upload.
	 */
	public static native void setUploadTrigger(Frontend frontend) /*-{
		$wnd.triggerUpload = function() {
			frontend.@com.logicaldoc.gui.frontend.client.Frontend::triggerUpload()();
		};
	}-*/;

	/**
	 * Declares the javascript function used to trigger the search for a
	 * specific tag.
	 */
	public static native void setSearchTag(Frontend frontend) /*-{
		$wnd.searchTag = function(tag) {
			frontend.@com.logicaldoc.gui.frontend.client.Frontend::searchTag(Ljava/lang/String;)(tag);
		};
	}-*/;
}