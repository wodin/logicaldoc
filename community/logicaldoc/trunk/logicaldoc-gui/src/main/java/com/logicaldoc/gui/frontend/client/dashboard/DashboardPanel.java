package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.i18n.I18N;
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

		Tab userTab = new Tab(I18N.message("user"));
		userTab.setPane(new UserDashboard());

		Tab workflowTab = new Tab(I18N.message("workflow"));
		workflowTab.setPane(new FeatureDisabled());

		Tab messagesTab = new Tab(I18N.message("messages"));
		messagesTab.setPane(new MessagesPanel());

		Tab subscriptionsTab = new Tab(I18N.message("subscriptions"));
		subscriptionsTab.setPane(new SubscriptionsPanel());

		tabSet.addTab(userTab);

		if (Feature.visible(Feature.MESSAGES)) {
			tabSet.addTab(messagesTab);
			if (!Feature.enabled(Feature.MESSAGES))
				messagesTab.setPane(new FeatureDisabled());
		}

		if (Feature.visible(Feature.AUDIT)) {
			tabSet.addTab(subscriptionsTab);
			if (!Feature.enabled(Feature.AUDIT))
				subscriptionsTab.setPane(new FeatureDisabled());
		}

		if (Feature.visible(Feature.WORKFLOW)) {
			tabSet.addTab(workflowTab);
			if (!Feature.enabled(Feature.WORKFLOW))
				workflowTab.setPane(new FeatureDisabled());
		}

		setMembers(tabSet);
	}

	public static DashboardPanel get() {
		if (instance == null)
			instance = new DashboardPanel();
		return instance;
	}
}
