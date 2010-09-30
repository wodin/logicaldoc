package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Feature;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.UserObserver;
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
public class DashboardPanel extends VLayout implements UserObserver {

	private static DashboardPanel instance;

	private TabSet tabSet = new TabSet();

	private Tab workflowTab = null;

	private Tab messagesTab = null;

	private Tab subscriptionsTab = null;

	private Tab userTab = null;

	private DashboardPanel() {
		userTab = new Tab(I18N.message("user"));
		userTab.setPane(new UserDashboard());

		messagesTab = new Tab(I18N.message("messages"));
		messagesTab.setPane(new MessagesPanel());

		subscriptionsTab = new Tab(I18N.message("subscriptions"));
		subscriptionsTab.setPane(new SubscriptionsPanel());

		workflowTab = new Tab(I18N.message("workflow"));
		workflowTab.setPane(new WorkflowDashboard());

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

		Session.get().getUser().addObserver(this);
	}

	public static DashboardPanel get() {
		if (instance == null)
			instance = new DashboardPanel();
		return instance;
	}

	public TabSet getTabSet() {
		return tabSet;
	}

	public Tab getWorkflowTab() {
		return workflowTab;
	}

	public Tab getMessagesTab() {
		return messagesTab;
	}

	public Tab getUserTab() {
		return userTab;
	}

	public Tab getSubscriptionsTab() {
		return subscriptionsTab;
	}

	public void updateUserTab() {
		tabSet.removeTab(userTab);
		userTab = new Tab(I18N.message("user"));
		userTab.setPane(new UserDashboard());
		tabSet.addTab(userTab, 0);
	}

	public void updateMessageTab() {
		tabSet.removeTab(messagesTab);
		messagesTab = new Tab(I18N.message("messages"));
		messagesTab.setPane(new MessagesPanel());
		tabSet.addTab(messagesTab, 1);
	}

	public void updateSubscriptionsTab() {
		tabSet.removeTab(subscriptionsTab);
		subscriptionsTab = new Tab(I18N.message("subscriptions"));
		subscriptionsTab.setPane(new SubscriptionsPanel());
		tabSet.addTab(subscriptionsTab, 2);
	}

	public void updateWorkflowTab() {
		tabSet.removeTab(workflowTab);
		workflowTab = new Tab(I18N.message("workflow"));
		workflowTab.setPane(new WorkflowDashboard());
		tabSet.addTab(workflowTab, 3);
	}

	@Override
	public void onUserChanged(GUIUser user, String attribute) {
		if (attribute.equals(GUIUser.CHECKED_OUT_DOCS) || attribute.equals(GUIUser.LOCKED_DOCS)) {
			updateUserTab();
		} else if (attribute.equals(GUIUser.ALL_MESSAGES) || attribute.equals(GUIUser.UNREAD_MESSAGES)) {
			updateMessageTab();
		} else if (attribute.equals(GUIUser.ALL_SUBSCRIPTIONS)) {
			updateSubscriptionsTab();
		} else if (attribute.equals(GUIUser.ALL_TASKS)) {
			updateWorkflowTab();
		}
	}
}