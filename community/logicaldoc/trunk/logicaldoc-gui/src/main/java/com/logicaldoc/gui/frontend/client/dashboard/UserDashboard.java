package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Feature;
import com.smartgwt.client.widgets.layout.PortalLayout;

/**
 * User dashboard that displays several portlets like a portal page.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class UserDashboard extends PortalLayout {

	private StatusPortlet locked = null;

	private StatusPortlet checkedOut = null;

	private HistoryPortlet checkedIn = null;

	private HistoryPortlet downloaded = null;

	private HistoryPortlet changed = null;

	private static UserDashboard instance;

	public UserDashboard() {
		setShowColumnMenus(false);
		setShowEdges(false);
		setShowShadow(false);
		setCanDrag(false);
		setCanDrop(false);
		setColumnBorder("0px");

		refresh();

		if (Feature.visible(Feature.NOTES))
			addPortlet(new PostsPortlet(), 1, 2);

	}

	public void refresh() {
		if (locked != null)
			removePortlet(locked);

		if (checkedOut != null)
			removePortlet(checkedOut);

		if (checkedIn != null)
			removePortlet(checkedIn);

		if (downloaded != null)
			removePortlet(downloaded);

		if (changed != null)
			removePortlet(changed);

		// Place the portlets
		locked = new StatusPortlet(Constants.EVENT_LOCKED);
		addPortlet(locked, 0, 0);
		checkedOut = new StatusPortlet(Constants.EVENT_CHECKEDOUT);
		addPortlet(checkedOut, 0, 1);
		downloaded = new HistoryPortlet(Constants.EVENT_DOWNLOADED);
		addPortlet(downloaded, 0, 2);
		changed = new HistoryPortlet(Constants.EVENT_CHANGED);
		addPortlet(changed, 1, 0);
		checkedIn = new HistoryPortlet(Constants.EVENT_CHECKEDIN);
		addPortlet(checkedIn, 1, 1);
	}

	public static UserDashboard get() {
		if (instance == null)
			instance = new UserDashboard();
		return instance;
	}
}