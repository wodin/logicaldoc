package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIWorkflow;
import com.logicaldoc.gui.frontend.client.services.WorkflowService;

/**
 * Implementation of the WorkflowService
 * 
 * @author Matteo Caruso - Logical Objects
 * @since 6.0
 */
public class MockWorkflowServiceImpl extends RemoteServiceServlet implements WorkflowService {

	private static final long serialVersionUID = 1L;

	@Override
	public GUIWorkflow get(String sid, long workflowId) throws InvalidSessionException {
		GUIWorkflow workflow = new GUIWorkflow();
		workflow.setId(workflowId);
		workflow.setName("Workflow_test");
		workflow.setDescription("Workflow di test");
		workflow.setTaskAssignmentSubject("task assign subject");
		workflow.setTaskAssignmentBody("This is the assignment body");
		workflow.setReminderSubject("reminder subject");
		workflow.setReminderBody("This is the reminder body");
		GUIUser user = new GUIUser();
		user.setId(9999);
		user.setUserName("john scott");
		user.setName("John");
		user.setFirstName("Scott");
		user.setEmail("john.scott@acme.com");
		workflow.setSupervisor(user);

		return workflow;
	}

	@Override
	public void delete(String sid, long workflowId) throws InvalidSessionException {

	}

	@Override
	public GUIWorkflow save(String sid, GUIWorkflow workflow) throws InvalidSessionException {
		if (workflow.getId() == 0)
			workflow.setId(9999);
		return workflow;
	}

	@Override
	public void deploy(String sid, long workflowId) throws InvalidSessionException {

	}

	@Override
	public GUIWorkflow[] list(String sid) throws InvalidSessionException {
		GUIWorkflow[] workflows = new GUIWorkflow[3];

		GUIWorkflow workflow = new GUIWorkflow();
		workflow.setId(1);
		workflow.setName("Workflow_test");
		workflow.setDescription("Workflow di test");
		workflow.setTaskAssignmentSubject("task assign subject");
		workflow.setTaskAssignmentBody("This is the assignment body");
		workflow.setReminderSubject("reminder subject");
		workflow.setReminderBody("This is the reminder body");
		GUIUser user = new GUIUser();
		user.setId(9999);
		user.setUserName("john scott");
		user.setName("John");
		user.setFirstName("Scott");
		user.setEmail("john.scott@acme.com");
		workflow.setSupervisor(user);

		workflows[0] = workflow;

		workflow = new GUIWorkflow();
		workflow.setId(2);
		workflow.setName("Workflow_New");
		workflow.setDescription("Workflow new");
		workflow.setTaskAssignmentSubject("task assign subject new");
		workflow.setTaskAssignmentBody("This is the assignment body new ");
		workflow.setReminderSubject("reminder subject new ");
		workflow.setReminderBody("This is the reminder body new");
		user = new GUIUser();
		user.setId(8888);
		user.setUserName("alan murphy");
		user.setName("Alan");
		user.setFirstName("Murphy");
		user.setEmail("alan.murphy@acme.com");
		workflow.setSupervisor(user);

		workflows[1] = workflow;

		workflow = new GUIWorkflow();
		workflow.setId(3);
		workflow.setName("Workflow_Logical");
		workflow.setDescription("Workflow di logical");
		workflow.setTaskAssignmentSubject("task assign subject logical");
		workflow.setTaskAssignmentBody("This is the assignment body logical");
		workflow.setReminderSubject("reminder subject logical");
		workflow.setReminderBody("This is the reminder body logical");
		user = new GUIUser();
		user.setId(7777);
		user.setUserName("sam mitchell");
		user.setName("Sam");
		user.setFirstName("Mitchell");
		user.setEmail("sam.mitchell@acme.com");
		workflow.setSupervisor(user);

		workflows[2] = workflow;

		return workflows;
	}
}
