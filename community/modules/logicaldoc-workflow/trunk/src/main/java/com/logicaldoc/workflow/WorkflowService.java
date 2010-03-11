package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.logicaldoc.workflow.model.FetchModel;
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

	public static enum TASK_SORT {
		ASC, DESC
	};

	public void undeployWorkflow(final String processId);

	public void deployWorkflow(WorkflowTemplate workflowTemplate);

	public void endTask(final String taskId, final String transitionName);

	public WorkflowInstance getWorkflowInstanceById(String workflowinstanceId, FetchModel.FETCH_TYPE fetch_type);

	public List<WorkflowDefinition> getAllDefinitions();

	public WorkflowTaskInstance getTaskInstanceByTaskId(String id, FetchModel.FETCH_TYPE fetch_type);

	public List<WorkflowTaskInstance> getTaskInstancesByWorkflowInstanceId(String workflowInstanceId);

	/**
	 * All suspended TaskInstances will be returned.
	 */
	public List<WorkflowTaskInstance> getSuspendedTaskInstances();

	/**
	 * All suspended TaskInstances will be returned for the given user name.
	 */
	public List<WorkflowTaskInstance> getSuspendedTaskInstancesForUser(String username);

	/**
	 * All current active TaskInstances will be returned. The suspended task
	 * instances will be not returned.
	 * 
	 * @return All active TaskInstances
	 */
	public List<WorkflowTaskInstance> getAllActiveTaskInstances();

	/**
	 * This methods is similar to 'getAllActiveTaskInstances()', but also the
	 * suspended task instances will be returned.
	 * 
	 * @return All TaskInstances
	 */
	public List<WorkflowTaskInstance> getAllTaskInstances();

	public WorkflowInstance startWorkflow(WorkflowDefinition workflowDefinition, Map<String, Serializable> properties);

	public void signal(String workflowInstance);

	public void stopWorkflow(String processInstanceId);

	public void updateWorkflow(WorkflowInstance taskInstance);

	public void updateWorkflow(WorkflowTaskInstance taskInstance);

	public void deleteAllActiveWorkflows();

	public List<WorkflowTaskInstance> getTaskInstancesForUser(String username);

	public List<WorkflowTaskInstance> getPooledTaskInstancesForUser(String username);

	public void assign(String taskId, String assignee);

	public List<WorkflowTaskInstance> getWorkflowTasks(WorkflowInstance workflowInstance,
			WorkflowTaskInstance.STATE taskState, TASK_SORT sort);

	public List<WorkflowTaskInstance> getWorkflowHistory(WorkflowInstance workflowInstance);

	public WorkflowInstance getWorkflowInstanceByTaskInstance(String workflowTaskId, FetchModel.FETCH_TYPE fetch_type);

	public List<WorkflowInstance> getAllWorkflows();

}
