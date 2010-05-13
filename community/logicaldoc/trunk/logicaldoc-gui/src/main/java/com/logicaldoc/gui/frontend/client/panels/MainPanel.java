package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.SessionObserver;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.search.Search;
import com.logicaldoc.gui.frontend.client.search.SearchMenu;
import com.logicaldoc.gui.frontend.client.search.SearchPanel;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This is the main panel that collects all other GUI panels
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class MainPanel extends VLayout implements SessionObserver {

	private TabSet tabSet = new TabSet();

	private Tab documentsTab;

	private Tab searchTab;

	private Tab administrationTab;

	private static MainPanel instance;

	public static MainPanel get() {
		if (instance == null)
			instance = new MainPanel();
		return instance;
	}

	private MainPanel() {
		setWidth100();
		setHeight100();

		Session.get().addSessionObserver(this);
	}

	private void initGUI() {
		Layout topPanel = new TopPanel();

		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.RIGHT);
		tabSet.setWidth100();
		tabSet.setHeight("*");
		documentsTab = new Tab(I18N.getMessage("documents"));
		tabSet.addTab(documentsTab);
		searchTab = new Tab(I18N.getMessage("search"));
		tabSet.addTab(searchTab);
		administrationTab = new Tab(I18N.getMessage("administration"));

		addMember(topPanel);
		addMember(tabSet);
		addMember(new FooterPanel());

		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				int width = Window.getClientWidth();
				int height = Window.getClientHeight();
				tabSet.setSize(width + "px", height - 65 + "px");
				redraw();
			}
		});
	}

	@Override
	public void onUserLoggedIn(GUIUser user) {
		initGUI();
		
		documentsTab.setPane(DocumentsPanel.get());
		searchTab.setPane(SearchPanel.get());
		administrationTab.setPane(AdminPanel.get());

		if (user.isMemberOf("admin")) {
			tabSet.addTab(administrationTab);
		}

		tabSet.selectTab(documentsTab);
	}

	public void selectSearchTab() {
		tabSet.selectTab(searchTab);
		if (Search.get().getOptions().isFulltext())
			SearchMenu.get().openFulltextSection();
	}

	public void selectDocumentsTab() {
		tabSet.selectTab(documentsTab);
	}
}