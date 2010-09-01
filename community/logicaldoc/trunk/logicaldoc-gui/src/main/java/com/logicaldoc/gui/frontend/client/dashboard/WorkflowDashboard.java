package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.document.WorkflowHistoryDialog;
import com.smartgwt.client.widgets.Button;
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

		Button historyButton = new Button(I18N.message("history"));
		historyButton.setMargin(2);
		historyButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				WorkflowHistoryDialog dialog = new WorkflowHistoryDialog();
				dialog.show();
			}
		});
		addMember(historyButton);
	}
}
