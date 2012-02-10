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

	private TabSet tabSet = new TabSet();

	private Tab workflowTab = null;

	private Tab messagesTab = null;

	private Tab subscriptionsTab = null;

	private Tab userTab = null;

	private Tab tagsTab = null;

	private DashboardPanel() {
		userTab = new Tab(I18N.message("user"));
		userTab.setID("user");
		userTab.setPane(new UserDashboard());

		tagsTab = new Tab(I18N.message("tags"));
		tagsTab.setID("tags");
		tagsTab.setPane(new TagsDashboard());

		messagesTab = new Tab(I18N.message("messages"));
		messagesTab.setID("messages");
		messagesTab.setPane(new MessagesPanel());

		subscriptionsTab = new Tab(I18N.message("subscriptions"));
		subscriptionsTab.setID("subscriptions");
		subscriptionsTab.setPane(new SubscriptionsPanel());

		workflowTab = new Tab(I18N.message("workflow"));
		workflowTab.setID("workflow");
		workflowTab.setPane(new WorkflowDashboard());

		tabSet.addTab(userTab);

		if (Feature.visible(Feature.TAGS)) {
			tabSet.addTab(tagsTab);
			if (!Feature.enabled(Feature.TAGS))
				tagsTab.setPane(new TagsDashboard());
		}

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
		tabSet.setTabPane("user", new UserDashboard());
		tabSet.selectTab("user");
	}

	public void updateTagsTab() {
		tabSet.setTabPane("tags", new TagsDashboard());
		tabSet.selectTab("tags");
	}

	public void updateMessageTab() {
		tabSet.setTabPane("messages", new MessagesPanel());
		tabSet.selectTab("messages");
	}

	public void updateSubscriptionsTab() {
		tabSet.setTabPane("subscriptions", new SubscriptionsPanel());
		tabSet.selectTab("subscriptions");
	}

	public void updateWorkflowTab() {
		tabSet.setTabPane("workflow", new WorkflowDashboard());
		tabSet.selectTab("workflow");
	}
}