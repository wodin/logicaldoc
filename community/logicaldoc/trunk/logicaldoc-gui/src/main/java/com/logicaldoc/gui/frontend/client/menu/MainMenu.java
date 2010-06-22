package com.logicaldoc.gui.frontend.client.menu;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
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

	public MainMenu() {
		super();
		setWidth100();

		ToolStripMenuButton menu = getFileMenu();
		addMenuButton(menu);

		menu = getToolsMenu();
		addMenuButton(menu);

		addFill();
		addSeparator();

		StaticTextItem userInfo = new StaticTextItem();
		userInfo.setTitle(I18N.message("loggedas"));
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
				Session.get().close();
				String base = GWT.getHostPageBaseURL();
				Util.redirect(base
						+ (base.endsWith("/") ? GWT.getModuleName() + ".jsp" : "/" + GWT.getModuleName() + ".jsp"));
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

		if (!Session.get().getUser().isMemberOf("admin")) {
			develConsole.setEnabled(false);
		}

		menu.setItems(develConsole);

		ToolStripMenuButton menuButton = new ToolStripMenuButton(I18N.message("tools"), menu);
		menuButton.setWidth(100);
		return menuButton;
	}
}
