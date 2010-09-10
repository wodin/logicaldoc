package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowHistoryDialog;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.VLayout;

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

	private static WorkflowDashboard instance;

	private Portlet assignedTasks = null;

	private Portlet canOwnTasks = null;

	private Portlet suspendedTasks = null;

	private Portlet adminTasks = null;

	private Portlet supervisorTasks = null;

	private Button historyButton = null;

	private Button refreshButton = null;

	private VLayout buttonsLayout = null;

	public WorkflowDashboard() {
		setShowColumnMenus(false);
		setShowEdges(false);
		setShowShadow(false);
		setColumnBorder("0px");

		refresh();
	}

	public void refresh() {
		if (assignedTasks != null)
			removePortlet(assignedTasks);

		if (canOwnTasks != null)
			removePortlet(canOwnTasks);

		if (suspendedTasks != null)
			removePortlet(suspendedTasks);

		if (adminTasks != null)
			removePortlet(adminTasks);

		if (supervisorTasks != null)
			removePortlet(supervisorTasks);

		if (buttonsLayout != null)
			removeMember(buttonsLayout);

		if (historyButton != null)
			removeMember(historyButton);

		if (refreshButton != null)
			removeMember(refreshButton);

		// Place the portlets
		assignedTasks = new WorkflowPortlet(this, TASKS_ASSIGNED);
		addPortlet(assignedTasks, 0, 0);
		canOwnTasks = new WorkflowPortlet(this, TASKS_I_CAN_OWN);
		addPortlet(canOwnTasks, 0, 1);
		suspendedTasks = new WorkflowPortlet(this, TASKS_SUSPENDED);
		addPortlet(suspendedTasks, 1, 0);
		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)) {
			adminTasks = new WorkflowPortlet(this, TASKS_ADMIN);
			addPortlet(adminTasks, 1, 1);
		} else {
			supervisorTasks = new WorkflowPortlet(this, TASKS_SUPERVISOR);
			addPortlet(supervisorTasks, 1, 1);
		}

		buttonsLayout = new VLayout(10);
		buttonsLayout.setWidth(80);

		refreshButton = new Button(I18N.message("refresh"));
		refreshButton.setMargin(2);
		refreshButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				refresh();
			}
		});

		historyButton = new Button(I18N.message("history"));
		historyButton.setMargin(2);
		historyButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				WorkflowHistoryDialog dialog = new WorkflowHistoryDialog();
				dialog.show();
			}
		});

		buttonsLayout.addMember(refreshButton);
		buttonsLayout.addMember(historyButton);

		addMember(buttonsLayout);
	}

	public static WorkflowDashboard get() {
		if (instance == null)
			instance = new WorkflowDashboard();
		return instance;
	}
}
