package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Constants;
import com.smartgwt.client.widgets.layout.PortalLayout;

/**
 * User dashboard that displays several portlets like a portal page.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UserDashboard extends PortalLayout {

	public UserDashboard() {
		setShowColumnMenus(false);
		setShowEdges(false);
		setShowShadow(false);
		setColumnBorder("0px");

		// Place the portlets
		addPortlet(new HistoryPortlet(Constants.EVENT_LOCKED), 0, 0);
		addPortlet(new HistoryPortlet(Constants.EVENT_CHECKEDOUT), 0, 1);
		addPortlet(new HistoryPortlet(Constants.EVENT_DOWNLOADED), 0, 2);
		addPortlet(new HistoryPortlet(Constants.EVENT_CHANGED), 1, 0);
		addPortlet(new HistoryPortlet(Constants.EVENT_CHECKEDIN), 1, 1);
		addPortlet(new TagCloudPortlet(), 1, 2);
	}
}