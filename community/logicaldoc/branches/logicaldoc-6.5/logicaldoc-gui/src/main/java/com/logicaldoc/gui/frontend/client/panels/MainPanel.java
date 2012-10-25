package com.logicaldoc.gui.frontend.client.panels;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.logicaldoc.gui.common.client.Menu;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.SessionObserver;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.common.client.log.Log;
import com.logicaldoc.gui.common.client.util.RequestInfo;
import com.logicaldoc.gui.common.client.util.WindowUtils;
import com.logicaldoc.gui.common.client.widgets.IncomingMessage;
import com.logicaldoc.gui.frontend.client.administration.AdminPanel;
import com.logicaldoc.gui.frontend.client.dashboard.DashboardPanel;
import com.logicaldoc.gui.frontend.client.document.DocumentsPanel;
import com.logicaldoc.gui.frontend.client.menu.MainMenu;
import com.logicaldoc.gui.frontend.client.search.Search;
import com.logicaldoc.gui.frontend.client.search.SearchMenu;
import com.logicaldoc.gui.frontend.client.search.SearchPanel;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;
import com.logicaldoc.gui.frontend.client.services.WorkflowServiceAsync;
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

	private Tab dashboardTab;

	private Tab administrationTab;

	private static MainPanel instance;

	private IncomingMessage incomingMessage = null;

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
		tabSet.setTabBarAlign(Side.LEFT);
		tabSet.setWidth100();
		tabSet.setHeight("*");
		documentsTab = new Tab(I18N.message("documents"));
		searchTab = new Tab(I18N.message("search"));
		dashboardTab = new Tab(I18N.message("dashboard"));
		administrationTab = new Tab(I18N.message("administration"));

		addMember(topPanel);
		incomingMessage = new IncomingMessage("", null);
		addMember(incomingMessage);
		addMember(new MainMenu(true, "embedded".equals(Session.get().getInfo().getConfig("gui.dropspot.mode"))));
		addMember(tabSet);
		addMember(new StatusBar(true));

		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				int width = Window.getClientWidth();
				int height = Window.getClientHeight();
				tabSet.setSize(width + "px", height - 95 + "px");
				redraw();
			}
		});
	}

	@Override
	public void onUserLoggedIn(final GUIUser user) {
		initGUI();

		int welcomeScreen = 1520;
		if (user.getWelcomeScreen() != null)
			welcomeScreen = user.getWelcomeScreen().intValue();

		if (Menu.enabled(Menu.DASHBOARD)) {
			dashboardTab.setPane(DashboardPanel.get());
			tabSet.addTab(dashboardTab);
		}

		if (Menu.enabled(Menu.DOCUMENTS)) {
			documentsTab.setPane(DocumentsPanel.get());
			tabSet.addTab(documentsTab);
		}

		if (Menu.enabled(Menu.SEARCH)) {
			searchTab.setPane(SearchPanel.get());
			tabSet.addTab(searchTab);
		}

		if (Menu.enabled(Menu.ADMINISTRATION)) {
			administrationTab.setPane(AdminPanel.get());
			tabSet.addTab(administrationTab);
		}

		RequestInfo loc = WindowUtils.getRequestInfo();
		if ((loc.getParameter("folderId") != null || loc.getParameter("docId") != null) && Menu.enabled(Menu.DOCUMENTS)) {
			/*
			 * The user clicked on a permanent link so we have to open the
			 * Documents tab
			 */
			tabSet.selectTab(documentsTab);
		} else {

			if (welcomeScreen == Menu.DOCUMENTS && Menu.enabled(Menu.DOCUMENTS))
				tabSet.selectTab(documentsTab);
			else if (welcomeScreen == Menu.SEARCH && Menu.enabled(Menu.SEARCH))
				tabSet.selectTab(searchTab);
			else if (welcomeScreen == Menu.DASHBOARD && Menu.enabled(Menu.DASHBOARD))
				tabSet.selectTab(dashboardTab);
		}

		WorkflowServiceAsync service = (WorkflowServiceAsync) GWT.create(WorkflowService.class);
		service.countActiveUserTasks(Session.get().getSid(), user.getUserName(), new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				if (Session.get().isDevel())
					Log.serverError(caught);
			}

			@Override
			public void onSuccess(Integer result) {
				int tasks = result;
				user.setActiveTasks(tasks);
			}
		});
	}

	public void selectSearchTab() {
		tabSet.selectTab(searchTab);
		if (Search.get().getOptions().isFulltext())
			SearchMenu.get().openFulltextSection();
	}

	public void selectDocumentsTab() {
		tabSet.selectTab(documentsTab);
	}

	public void selectDashboardTab() {
		tabSet.selectTab(dashboardTab);
	}

	public void selectUserTab() {
		DashboardPanel dp = DashboardPanel.get();
		dp.getTabSet().selectTab(DashboardPanel.get().getUserTab());
		dashboardTab.setPane(dp);
		tabSet.selectTab(dashboardTab);
	}

	public void selectWorkflowTab() {
		DashboardPanel dp = DashboardPanel.get();
		dp.getTabSet().selectTab(DashboardPanel.get().getWorkflowTab());
		dashboardTab.setPane(dp);
		tabSet.selectTab(dashboardTab);
	}

	public void selectMessagesTab() {
		DashboardPanel dp = DashboardPanel.get();
		dp.getTabSet().selectTab(DashboardPanel.get().getMessagesTab());
		dashboardTab.setPane(dp);
		tabSet.selectTab(dashboardTab);
	}

	public IncomingMessage getIncomingMessage() {
		return incomingMessage;
	}
}