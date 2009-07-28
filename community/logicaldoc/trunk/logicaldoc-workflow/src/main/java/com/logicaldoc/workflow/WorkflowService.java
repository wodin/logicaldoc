package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.exe.Token;

import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.WorkflowTemplate;

/**
 * Please see {@link WorkflowEngine} for more informations about the functions.
 * 
 * @author Sebastian Wenzky
 * 
 */
public interface WorkflowService {

	public static enum TASK_SORT {ASC, DESC};
	
	public void undeployWorkflow(final String processId);

	public void deployWorkflow(WorkflowTemplate workflowTemplate);

	public void endTask(final String taskId, final String transitionName);

	public WorkflowInstance getWorkflowInstancesById(String workflowinstanceId);

	public List<WorkflowDefinition> getAllDefinitions();

	public WorkflowTaskInstance getTaskInstanceByTaskId(String id);

	public List<WorkflowTaskInstance> getTaskInstancesByWorkflowInstanceId(
			String workflowInstanceId);

	public List<WorkflowTaskInstance> getAllActiveTaskInstances();

	public Token getTokenForWorkflowInstance(String workflowInstance);

	public WorkflowInstance startWorkflow(WorkflowDefinition workflowDefinition, Map<String, Serializable> properties);

	public void signal(String workflowInstance);

	public void stopWorkflow(String processInstanceId);

	public void updateTaskInstace(WorkflowTaskInstance taskInstance);
	
	public void deleteAllActiveWorkflows();
	
	public List<WorkflowTaskInstance> getTaskInstancesForUser(String username);
	
	public List<WorkflowTaskInstance> getPooledTaskInstancesForUser(String username);
	
	public void assign(String taskId, String assignee);
	
	public List<WorkflowTaskInstance> getWorkflowTasks(WorkflowInstance workflowInstance, WorkflowTaskInstance.STATE taskState,  TASK_SORT sort);
	
	public List<WorkflowTaskInstance> getWorkflowHistory(WorkflowInstance workflowInstance);
	
	public WorkflowInstance getWorkflowInstanceByTaskInstance(String workflowTaskId);
}
