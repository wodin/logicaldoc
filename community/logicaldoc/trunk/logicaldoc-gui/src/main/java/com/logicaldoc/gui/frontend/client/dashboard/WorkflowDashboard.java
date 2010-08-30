package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.smartgwt.client.widgets.layout.PortalLayout;

/**
 * Workflow dashboard that displays several portlets like a portal page.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowDashboard extends PortalLayout {

	public static int TASKS_ASSIGNED = 0;

	public static int TASKS_I_CAN_OWN = 1;

	public static int TASKS_SUSPENDED = 2;

	public static int TASKS_ADMIN = 3;

	public static int TASKS_SUPERVISOR = 4;

	public WorkflowDashboard() {
		setShowColumnMenus(false);
		setShowEdges(false);
		setShowShadow(false);
		setColumnBorder("0px");

		// Place the portlets
		addPortlet(new WorkflowPortlet(TASKS_ASSIGNED), 0, 0);
		addPortlet(new WorkflowPortlet(TASKS_I_CAN_OWN), 0, 1);
		addPortlet(new WorkflowPortlet(TASKS_SUSPENDED), 1, 0);
		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN))
			addPortlet(new WorkflowPortlet(TASKS_ADMIN), 1, 1);
		else
			addPortlet(new WorkflowPortlet(TASKS_SUPERVISOR), 1, 1);
	}
}
