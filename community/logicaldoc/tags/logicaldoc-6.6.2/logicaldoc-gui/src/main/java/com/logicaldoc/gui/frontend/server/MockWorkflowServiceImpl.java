package com.logicaldoc.gui.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.logicaldoc.gui.common.client.InvalidSessionException;
import com.logicaldoc.gui.common.client.beans.GUITransition;
import com.logicaldoc.gui.common.client.beans.GUIUser;
import com.logicaldoc.gui.common.client.beans.GUIValuePair;
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
	public GUIWorkflow get(String sid, String workflowName) throws InvalidSessionException {
		GUIWorkflow workflow = new GUIWorkflow();
		workflow.setId(1);
		workflow.setName(workflowName);
		workflow.setDescription("Workflow di test");
		workflow.setStartStateId("1");

		GUIUser user = new GUIUser();
		user.setId(9999);
		user.setUserName("john scott");
		user.setName("John");
		user.setFirstName("Scott");
		user.setEmail("john.scott@acme.com");
		workflow.setSupervisor("john scott");

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
		task.setId("1");
		task.setType(GUIWFState.TYPE_TASK);
		task.setName("Task logical");
		task.setDescription("Task logical description");
		task.setDueDateNumber(11);
		task.setReminderNumber(13);
		GUIValuePair[] participants = new GUIValuePair[2];
		participants[0] = new GUIValuePair("" + user1.getId(), user1.getUserName());
		participants[1] = new GUIValuePair("" + user2.getId(), user2.getUserName());
		task.setParticipants(participants);

		GUIWFState task1 = new GUIWFState();
		task1.setId("2");
		task1.setType(GUIWFState.TYPE_TASK);
		task1.setName("Task test");
		task1.setDescription("Task test description");
		task1.setDueDateNumber(3);
		task1.setReminderNumber(43);

		GUIWFState task2 = new GUIWFState();
		task2.setId("3");
		task2.setType(GUIWFState.TYPE_TASK);
		task2.setName("Task doc");
		task2.setDescription("Task doc description");
		task2.setDueDateNumber(2);
		task2.setReminderNumber(5);

		GUIWFState fork = new GUIWFState();
		fork.setId("4");
		fork.setType(GUIWFState.TYPE_FORK);
		fork.setName("Fork logical");
		fork.setDescription("Fork logical description");

		GUIWFState join = new GUIWFState();
		join.setId("5");
		join.setType(GUIWFState.TYPE_JOIN);
		join.setName("Join logical");
		join.setDescription("Join logical description");

		GUIWFState endState = new GUIWFState();
		endState.setId("6");
		endState.setType(GUIWFState.TYPE_END);
		endState.setName("End State logical");
		endState.setDescription("End State logical description");

		GUITransition[] transitions = new GUITransition[3];
		transitions[0] = new GUITransition("to the end", endState);
		transitions[1] = new GUITransition("to task 1", task1);
		transitions[2] = new GUITransition("to task 2", task2);
		task.setTransitions(transitions);

		transitions = new GUITransition[1];
		transitions[0] = new GUITransition("ending", endState);
		task2.setTransitions(transitions);

		transitions = new GUITransition[2];
		transitions[0] = new GUITransition("to t1", task1);
		transitions[1] = new GUITransition("to t2", task2);
		fork.setTransitions(transitions);

		GUIWFState[] states = new GUIWFState[6];
		states[0] = task;
		states[1] = fork;
		states[2] = join;
		states[3] = endState;
		states[4] = task1;
		states[5] = task2;
		workflow.setStates(states);

		return workflow;
	}

	@Override
	public void delete(String sid, String workflowName) throws InvalidSessionException {

	}

	@Override
	public GUIWorkflow save(String sid, GUIWorkflow workflow) throws InvalidSessionException {
		// if (workflow.getId() == 0)
		// workflow.setId(9999);
		return workflow;
	}

	@Override
	public void deploy(String sid, GUIWorkflow workflow) throws InvalidSessionException {

	}

	@Override
	public GUIWorkflow[] list(String sid) throws InvalidSessionException {
		GUIWorkflow[] workflows = new GUIWorkflow[3];

		GUIWorkflow workflow = new GUIWorkflow();
		workflow.setId(1);
		workflow.setName("Workflow_test");
		workflow.setDescription("Workflow di test");
		GUIUser user = new GUIUser();
		user.setId(9999);
		user.setUserName("john scott");
		user.setName("John");
		user.setFirstName("Scott");
		user.setEmail("john.scott@acme.com");
		workflow.setSupervisor("john scott");

		workflows[0] = workflow;

		workflow = new GUIWorkflow();
		workflow.setId(2);
		workflow.setName("Workflow_New");
		workflow.setDescription("Workflow new");
		user = new GUIUser();
		user.setId(8888);
		user.setUserName("alan murphy");
		user.setName("Alan");
		user.setFirstName("Murphy");
		user.setEmail("alan.murphy@acme.com");
		workflow.setSupervisor("alan murphy");

		workflows[1] = workflow;

		workflow = new GUIWorkflow();
		workflow.setId(3);
		workflow.setName("Workflow_Logical");
		workflow.setDescription("Workflow di logical");
		user = new GUIUser();
		user.setId(7777);
		user.setUserName("sam mitchell");
		user.setName("Sam");
		user.setFirstName("Mitchell");
		user.setEmail("sam.mitchell@acme.com");
		workflow.setSupervisor("sam mitchell");

		workflows[2] = workflow;

		return workflows;
	}

	@Override
	public void deleteTrigger(String sid, long id) throws InvalidSessionException {
	}

	@Override
	public void saveTrigger(String sid, String folderId, String workflowId, String templateId, int startAtCheckin)
			throws InvalidSessionException {
	}

	@Override
	public void startWorkflow(String sid, String workflowName, String workflowDescription, long[] docIds)
			throws InvalidSessionException {
	}

	@Override
	public GUIWorkflow getWorkflowDetailsByTask(String sid, String taskId) throws InvalidSessionException {
		return null;
	}

	@Override
	public void saveTaskAssignment(String sid, String taskId, String userId) throws InvalidSessionException {
	}

	@Override
	public void startTask(String sid, String taskId, String comment) throws InvalidSessionException {
	}

	@Override
	public void suspendTask(String sid, String taskId, String comment) throws InvalidSessionException {
	}

	@Override
	public void resumeTask(String sid, String taskId, String comment) throws InvalidSessionException {
	}

	@Override
	public void saveTaskState(String sid, String taskId, String comment) throws InvalidSessionException {
	}

	@Override
	public void takeTaskOwnerShip(String sid, String taskId, String userId, String comment)
			throws InvalidSessionException {
	}

	@Override
	public void turnBackTaskToPool(String sid, String taskId, String comment) throws InvalidSessionException {
	}

	@Override
	public void endTask(String sid, String taskId, String transitionName, String comment)
			throws InvalidSessionException {
	}

	@Override
	public int countActiveUserTasks(String sid, String username) throws InvalidSessionException {
		return 0;
	}

	@Override
	public void appendDocuments(String sid, String taskId, long[] docIds) throws InvalidSessionException {
	}

	@Override
	public GUIWorkflow importSchema(String sid) throws InvalidSessionException {
		return new GUIWorkflow();
	}

	@Override
	public void deleteInstance(String sid, long id) throws InvalidSessionException {

	}

	@Override
	public void applyTriggersToTree(String sid, long rootId) throws InvalidSessionException {
		// TODO Auto-generated method stub

	}
}
