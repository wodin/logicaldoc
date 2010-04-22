package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.SessionObserver;
import com.logicaldoc.gui.common.client.beans.GUIUser;
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

	private static final int TAB_DOCUMENTS = 0;

	private static final int TAB_SEARCH = 1;

	private TabSet tabSet = new TabSet();

	public MainPanel() {
		setWidth100();
		setHeight100();

		Layout topPanel = new TopPanel();

		tabSet.setTabBarPosition(Side.TOP);
		tabSet.setTabBarAlign(Side.RIGHT);
		tabSet.setWidth100();
		tabSet.setHeight("*");
		Tab documentsTab = new Tab(I18N.getMessage("documents"));
		tabSet.addTab(documentsTab);
		Tab searchTab = new Tab(I18N.getMessage("search"));
		tabSet.addTab(searchTab);

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

		Session.get().addSessionObserver(this);
	}

	@Override
	public void onUserLoggedIn(GUIUser user) {
		tabSet.getTabs()[TAB_DOCUMENTS].setPane(DocumentsPanel.get());
		tabSet.getTabs()[TAB_SEARCH].setPane(SearchPanel.get());
		tabSet.selectTab(TAB_DOCUMENTS);
	}

	public void selectSearchTab() {
		tabSet.selectTab(TAB_SEARCH);
		if (Search.get().getOptions().isFulltext())
			SearchMenu.get().openFulltextSection();
	}
}