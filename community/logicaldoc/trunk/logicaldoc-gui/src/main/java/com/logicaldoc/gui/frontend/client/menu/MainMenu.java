package com.logicaldoc.gui.frontend.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.Util;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.frontend.client.personal.ChangePassword;
import com.logicaldoc.gui.frontend.client.personal.Profile;
import com.logicaldoc.gui.frontend.client.services.SecurityService;
import com.logicaldoc.gui.frontend.client.services.SecurityServiceAsync;
import com.logicaldoc.gui.frontend.client.services.SystemService;
import com.logicaldoc.gui.frontend.client.services.SystemServiceAsync;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;

/**
 * Main program menu
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MainMenu extends ToolStrip {
	protected SystemServiceAsync systemService = (SystemServiceAsync) GWT.create(SystemService.class);

	protected SecurityServiceAsync securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

	public MainMenu() {
		super();
		setWidth100();

		ToolStripMenuButton menu = getFileMenu();
		addMenuButton(menu);

		addMenuButton(getPersonalMenu());

		if (Session.get().getUser().isMemberOf("admin")) {
			menu = getToolsMenu();
			addMenuButton(menu);
		}

		addMenuButton(getHelpMenu());

		addFill();
		addSeparator();

		StaticTextItem userInfo = new StaticTextItem();
		userInfo.setTitle(I18N.message("loggedin"));
		userInfo.setValue(Session.get().getUser().getFullName());
		userInfo.setWrap(false);
		userInfo.setWrapTitle(false);

		addFormItem(userInfo);
		addSeparator();

		addFormItem(new SearchBox());
	}

	private ToolStripMenuButton getFileMenu() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem exitItem = new MenuItem(I18N.message("exit"));
		exitItem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				securityService.logout(Session.get().getSid(), new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Log.serverError(caught);
						SC.warn(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						WindowUtils.setNotAskForExit();
						String base = GWT.getHostPageBaseURL();
						Util.redirect(base
								+ (base.endsWith("/") ? GWT.getModuleName() + ".jsp" : "/" + GWT.getModuleName()
										+ ".jsp"));
					}
				});
			}
		});

		menu.setItems(exitItem);

		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("file"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}

	private ToolStripMenuButton getToolsMenu() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem develConsole = new MenuItem(I18N.message("develconsole"));
		develConsole.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				SC.showConsole();
			}
		});

		menu.setItems(develConsole);

		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("tools"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}

	private ToolStripMenuButton getPersonalMenu() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem profile = new MenuItem(I18N.message("profile"));
		profile.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				securityService.getUser(Session.get().getSid(), Session.get().getUser().getId(),
						new AsyncCallback<GUIUser>() {

							@Override
							public void onFailure(Throwable caught) {
								Log.serverError(caught);
							}

							@Override
							public void onSuccess(GUIUser user) {
								Profile profile = new Profile(user);
								profile.show();
							}
						});
			}
		});

		MenuItem changePswd = new MenuItem(I18N.message("changepassword"));
		changePswd.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				ChangePassword cp = new ChangePassword(Session.get().getUser(), null);
				cp.show();
			}
		});

		menu.setItems(profile, changePswd);

		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("personal"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}

	private ToolStripMenuButton getHelpMenu() {

		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem documentation = new MenuItem(I18N.message("documentation"));
		documentation.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Window.open(Session.get().getInfo().getHelp(), "_blank",
						"location=no,status=no,toolbar=no,menubar=no,resizable=yes");
			}
		});

		MenuItem bugReport = new MenuItem(I18N.message("bug.report"));
		bugReport.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Window.open(Session.get().getInfo().getBugs(), "_blank",
						"location=no,status=no,toolbar=no,menubar=no,resizable=yes");
			}
		});

		MenuItem forum = new MenuItem(I18N.message("forum"));
		forum.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				Window.open(Session.get().getInfo().getBugs(), "_blank",
						"location=no,status=no,toolbar=no,menubar=no,resizable=yes");
			}
		});

		MenuItem about = new MenuItem(I18N.message("about") + " " + Session.get().getInfo().getProduct());
		about.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				AboutDialog dialog = new AboutDialog();
				dialog.show();
			}
		});
		menu.setItems(documentation, forum, bugReport, about);

		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("help"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}
}
