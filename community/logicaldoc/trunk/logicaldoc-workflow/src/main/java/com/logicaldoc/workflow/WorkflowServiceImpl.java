package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.Token;

import com.logicaldoc.workflow.exception.WorkflowException;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.persistence.WorkflowPersistenceTemplate;

public class WorkflowServiceImpl implements WorkflowService {

	protected static Log logger = LogFactory.getLog(WorkflowServiceImpl.class);

	private WorkflowEngine workflowComponent;

	public void setWorkflowComponent(WorkflowEngine workflowComponent) {
		this.workflowComponent = workflowComponent;
	}

	@Override
	public void deployWorkflow(WorkflowPersistenceTemplate persistenceTemplate,
			Serializable definition) {

		try {
			workflowComponent.deployWorkflow(persistenceTemplate, definition);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkflowException(e);
		}

	}

	@Override
	public void endTask(String taskId) {
		workflowComponent.processTaskToEnd(taskId);
	}

	@Override
	public void endTask(String taskId, String transitionName) {
		workflowComponent.processTaskToEnd(taskId, transitionName);
	}

	@Override
	public void undeployWorkflow(String processId) {
		workflowComponent.undeployWorkflow(processId);
	}

	public WorkflowInstance startWorkflow(String processdefinitionName) {
		WorkflowInstance workflowInstance = workflowComponent
				.startWorkflow(processdefinitionName);
		return workflowInstance;
	}

	@Override
	public List<WorkflowDefinition> getAllDefinitions() {
		List<WorkflowDefinition> processDefinitions = workflowComponent
				.getAllProcessDefinitions();

		return processDefinitions;
	}

	@Override
	public WorkflowTaskInstance getTaskInstanceByTaskId(String id) {

		WorkflowTaskInstance taskInstance = workflowComponent
				.getTaskInstanceById(id);
		return taskInstance;

	}

	@Override
	public List<WorkflowTaskInstance> getAllActiveTaskInstances() {
		List<WorkflowTaskInstance> taskInstances = workflowComponent
				.getAllTaskInstances();

		return taskInstances;

	}

	public Token getTokenForWorkflowInstance(String workflowInstance) {
		return workflowComponent.getToken(Long.parseLong(workflowInstance));
	}

	public void signal(String workflowInstanceId) {
		workflowComponent.signal(workflowInstanceId);
	}

	@Override
	public List<WorkflowTaskInstance> getTaskInstancesByWorkflowInstanceId(
			String workflowInstanceId) {
		List<WorkflowTaskInstance> workflowInstances = workflowComponent
				.getTasksByActiveWorkflowId(workflowInstanceId);
		return workflowInstances;
	}

	@Override
	public WorkflowInstance getWorkflowInstancesById(String workflowinstanceId) {

		WorkflowInstance workflowInstance = workflowComponent
				.getWorkflowInstanceById(workflowinstanceId);
		return workflowInstance;
	}

	@Override
	public void stopWorkflow(String processInstanceId) {
		workflowComponent.stopWorkflow(processInstanceId);
	}

	@Override
	public void updateTaskInstace(WorkflowTaskInstance taskInstance) {
		workflowComponent.updateTaskInstance(taskInstance);
	}

	@Override
	public void deleteAllActiveWorkflows() {
		this.workflowComponent.deleteAllActiveWorkflows();
	}
	
	public List<WorkflowTaskInstance> getTaskInstancesForUser(String username){
		return this.workflowComponent.getAllActionTasksByUser(username);
	}
}
