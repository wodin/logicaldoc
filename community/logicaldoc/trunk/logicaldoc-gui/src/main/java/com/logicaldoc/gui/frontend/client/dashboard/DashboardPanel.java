package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.I18N;
import com.logicaldoc.gui.common.client.widgets.FeatureDisabled;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * This is the dashboard container
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class DashboardPanel extends VLayout {

	private static DashboardPanel instance;

	private DashboardPanel() {
		TabSet tabSet = new TabSet();

		Tab userTab = new Tab(I18N.getMessage("user"));
		userTab.setPane(new UserDashboard());

		Tab workflowTab = new Tab(I18N.getMessage("workflow"));
		workflowTab.setPane(new FeatureDisabled());

		tabSet.setTabs(userTab, workflowTab);
		setMembers(tabSet);
	}

	public static DashboardPanel get() {
		if (instance == null)
			instance = new DashboardPanel();
		return instance;
	}
}
