package com.logicaldoc.gui.frontend.client.menu;

import com.google.gwt.core.client.GWT;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.util.Util;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;
import com.smartgwt.client.widgets.menu.MenuItemStringFunction;
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

	public MainMenu() {
		super();
		setWidth100();

		ToolStripMenuButton menuButton = getFileMenu();
		addMenuButton(menuButton);

		addSeparator();

		addFill();
	}

	private ToolStripMenuButton getFileMenu() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem newItem = new MenuItem("New", "icons/16/document_plain_new.png", "Ctrl+N");
		MenuItem openItem = new MenuItem("Open", "icons/16/folder_out.png", "Ctrl+O");
		MenuItem saveItem = new MenuItem("Save", "icons/16/disk_blue.png", "Ctrl+S");
		MenuItem saveAsItem = new MenuItem("Save As", "icons/16/save_as.png");

		MenuItem recentDocItem = new MenuItem("Recent Documents", "icons/16/folder_document.png");

		Menu recentDocSubMenu = new Menu();
		MenuItem dataSM = new MenuItem("data.xml");
		dataSM.setChecked(true);
		MenuItem componentSM = new MenuItem("Component Guide.doc");
		MenuItem ajaxSM = new MenuItem("AJAX.doc");
		recentDocSubMenu.setItems(dataSM, componentSM, ajaxSM);

		recentDocItem.setSubmenu(recentDocSubMenu);

		MenuItem exportItem = new MenuItem("Export as...", "icons/16/export1.png");
		Menu exportSM = new Menu();
		exportSM.setItems(new MenuItem("XML"), new MenuItem("CSV"), new MenuItem("Plain text"));
		exportItem.setSubmenu(exportSM);

		MenuItem exitItem = new MenuItem(I18N.message("exit"), "icons/16/printer3.png", "Ctrl+Q");
		exitItem.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(MenuItemClickEvent event) {
				Session.get().close();
				String base = GWT.getHostPageBaseURL();
				Util.redirect(base
						+ (base.endsWith("/") ? GWT.getModuleName() + ".jsp" : "/" + GWT.getModuleName()
								+ ".jsp"));
			}	
		});

		MenuItemSeparator separator = new MenuItemSeparator();

		final MenuItem activateMenu = new MenuItem("Activate");
		activateMenu.setDynamicTitleFunction(new MenuItemStringFunction() {

			public String execute(final Canvas aTarget, final Menu aMenu, final MenuItem aItem) {
				if (Math.random() > 0.5) {
					return "De-Activate Blacklist";
				} else {
					return "Activate Blacklist";
				}
			}
		});

		menu.setItems(activateMenu, newItem, openItem, separator, saveItem, saveAsItem, separator, recentDocItem,
				separator, exportItem, separator, exitItem);

		ToolStripMenuButton menuButton = new ToolStripMenuButton("File", menu);
		menuButton.setWidth(100);
		return menuButton;
	}
}
