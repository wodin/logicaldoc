package com.logicaldoc.gui.frontend.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIWFState;
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
		workflow.setStartState("TaskName");

		GUIUser user = new GUIUser();
		user.setId(9999);
		user.setUserName("john scott");
		user.setName("John");
		user.setFirstName("Scott");
		user.setEmail("john.scott@acme.com");
		workflow.setSupervisor(user);

		GUIUser user1 = new GUIUser();
		user1.setId(8888);
		user1.setUserName("alan murphy");
		user1.setName("Alan");
		user1.setFirstName("Murphy");
		user1.setEmail("alan.murphy@acme.com");

		GUIUser user2 = new GUIUser();
		user2.setId(7777);
		user2.setUserName("sam mitchell");
		user2.setName("Sam");
		user2.setFirstName("Mitchell");
		user2.setEmail("sam.mitchell@acme.com");

		GUIWFState task = new GUIWFState();
		task.setType(GUIWFState.TYPE_TASK);
		task.setName("Task logical");
		task.setDescription("Task logical description");
		task.setDuedate(new Date());
		task.setRemindTime(new Date());
		GUIUser[] participants = new GUIUser[2];
		participants[0] = user1;
		participants[1] = user2;
		task.setParticipants(participants);

		GUIWFState fork = new GUIWFState();
		fork.setType(GUIWFState.TYPE_FORK);
		fork.setName("Fork logical");
		fork.setDescription("Fork logical description");

		GUIWFState join = new GUIWFState();
		join.setType(GUIWFState.TYPE_JOIN);
		join.setName("Join logical");
		join.setDescription("Join logical description");

		GUIWFState endState = new GUIWFState();
		endState.setType(GUIWFState.TYPE_END);
		endState.setName("End State logical");
		endState.setDescription("End State logical description");

//		Map<String, GUIWFState> transitions = new HashMap<String, GUIWFState>();
//		transitions.put("to the end", endState);
//
//		task.setTransitions(transitions);

		GUIWFState[] states = new GUIWFState[4];
		states[0] = task;
		states[1] = fork;
		states[2] = join;
		states[3] = endState;

		workflow.setStates(states);

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
