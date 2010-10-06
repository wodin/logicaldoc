package com.logicaldoc.gui.frontend.client.dashboard;

import com.logicaldoc.gui.common.client.Constants;
import com.logicaldoc.gui.common.client.Session;
import com.logicaldoc.gui.common.client.i18n.I18N;
import com.logicaldoc.gui.frontend.client.workflow.WorkflowHistoryDialog;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

/**
 * Workflow dashboard that displays several portlets like a portal page.
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class WorkflowDashboard extends VLayout {

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

	private PortalLayout portalLayout = null;

	public WorkflowDashboard() {
		setWidth100();

		ToolStrip toolStrip = new ToolStrip();
		toolStrip.setHeight(20);
		toolStrip.setWidth100();
		toolStrip.addSpacer(2);
		ToolStripButton refresh = new ToolStripButton();
		refresh.setTitle(I18N.message("refresh"));
		toolStrip.addButton(refresh);
		refresh.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
		ToolStripButton history = new ToolStripButton();
		history.setTitle(I18N.message("history"));
		toolStrip.addButton(history);
		history.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				WorkflowHistoryDialog dialog = new WorkflowHistoryDialog();
				dialog.show();
			}
		});
		toolStrip.addFill();

		addMember(toolStrip, 0);

		refresh();
	}

	public void refresh() {
		if (portalLayout != null)
			removeMember(portalLayout);

		portalLayout = new PortalLayout();
		portalLayout.setShowColumnMenus(false);
		portalLayout.setShowEdges(false);
		portalLayout.setShowShadow(false);
		portalLayout.setColumnBorder("0px");

		// Place the portlets
		assignedTasks = new WorkflowPortlet(this, TASKS_ASSIGNED);
		portalLayout.addPortlet(assignedTasks, 0, 0);
		canOwnTasks = new WorkflowPortlet(this, TASKS_I_CAN_OWN);
		portalLayout.addPortlet(canOwnTasks, 0, 1);
		suspendedTasks = new WorkflowPortlet(this, TASKS_SUSPENDED);
		portalLayout.addPortlet(suspendedTasks, 1, 0);
		if (Session.get().getUser().isMemberOf(Constants.GROUP_ADMIN)) {
			adminTasks = new WorkflowPortlet(this, TASKS_ADMIN);
			portalLayout.addPortlet(adminTasks, 1, 1);
		} else {
			supervisorTasks = new WorkflowPortlet(this, TASKS_SUPERVISOR);
			portalLayout.addPortlet(supervisorTasks, 1, 1);
		}

		addMember(portalLayout, 1);
	}

	public static WorkflowDashboard get() {
		if (instance == null)
			instance = new WorkflowDashboard();
		return instance;
	}
}
