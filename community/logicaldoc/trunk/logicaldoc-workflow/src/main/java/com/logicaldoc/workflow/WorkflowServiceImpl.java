package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.Token;

import com.logicaldoc.workflow.editor.WorkflowTemplateLoader;
import com.logicaldoc.workflow.exception.WorkflowException;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;

public class WorkflowServiceImpl implements WorkflowService {

	protected static Log logger = LogFactory.getLog(WorkflowServiceImpl.class);

	private WorkflowEngine workflowComponent;
	
	private WorkflowTransformService workflowTransformService;

	private WorkflowTemplateLoader workflowTemplateLoader;
	
	public void setWorkflowTransformService(
			WorkflowTransformService workflowTransformService) {
		this.workflowTransformService = workflowTransformService;
	}
	
	public void setWorkflowComponent(WorkflowEngine workflowComponent) {
		this.workflowComponent = workflowComponent;
	}

	@Override
	public void deployWorkflow(WorkflowTemplate workflowTemplate) {
		
		Serializable definition = (Serializable)this.workflowTransformService.fromObjectToWorkflowDefinition(workflowTemplate);
		
		try {
			workflowComponent.deployWorkflow(workflowTemplate, definition);
		} catch (Exception e) {
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

	public WorkflowInstance startWorkflow(WorkflowDefinition workflowDefinition, Map<String, Serializable> properties) {
		
		WorkflowInstance workflowInstance = workflowComponent
				.startWorkflow(workflowDefinition.getDefinitionId(), properties);
		
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
	
	@Override
	public List<WorkflowTaskInstance> getPooledTaskInstancesForUser(
			String username) {
		return this.workflowComponent.getAllActionPooledTasksByUser(username);
	}
	
	public void assign(String taskId, String assignee){
		this.workflowComponent.assignUserToTask(taskId, assignee);	
	}
}
