package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.WorkflowTemplate;

/**
 * The interface "WorkflowEngine" described one component that has to be
 * implemented as its responsible to build up the bridge between LogicalDOC and
 * the BPM-Engine. Thus, the Engine incorporates directly with an BPM-System.
 * Furthermore, the Engine must provide the mapping of the BPM-Specific classes
 * to LogicalDOC. The WorkflowService gets injected one particular
 * WorkflowEngine that manages the relying processes.
 * 
 * @author Sebastian Wenzky
 */
public interface WorkflowEngine {

	/**
	 * A valid deployed Workflow-Processdefinition gets undeployed.
	 * 
	 * @param processId The process
	 */
	public void undeployWorkflow(final String workflowname);

	/**
	 * Deploying of a workflow-process definition.
	 * 
	 * @param _processDefinition The serialized content of the
	 *        processedefinition.
	 */
	public void deployWorkflow(WorkflowTemplate template, Serializable _processDefinition);

	/**
	 * End the task using the default-transition.
	 * 
	 * @param taskId TaskId
	 */
	public void processTaskToEnd(final String taskId);

	/**
	 * End the task using the passed transition
	 * 
	 * @param taskId the Taskid
	 * @param transitionName Transition
	 */
	public void processTaskToEnd(final String taskId, final String transitionName);

	/**
	 * 
	 * @param defId
	 * @return
	 */
	public WorkflowDefinition getWorkflowDefinitionById(final String defId);

	/**
	 * 
	 * @param processdefinitionName
	 * @return
	 */
	public WorkflowDefinition getWorkflowDefinitionByName(final String workflowDefinitionName);

	/**
	 * Starts a workflow on using the deployed definition name.
	 * 
	 * @param processdefinitionName
	 * @return The newly started Workflow
	 */
	public WorkflowInstance startWorkflow(final String processdefinitionName, Map<String, Serializable> properties);

	/**
	 * Stops a workflow on using the corresponding processid
	 * 
	 * @param processId Processid
	 */
	public void stopWorkflow(final String processId);

	/**
	 * Retrieves all valid deployed ProcessDefinitions
	 * 
	 * @return Al process-definitions
	 */
	public List<WorkflowDefinition> getAllProcessDefinitions();

	/**
	 * Retrieves a particular TaskInstance given by a valid taskid The
	 * taskInstance must not be active to get a valid reference on it.
	 * 
	 * @param id The TaskId
	 * @return the TaskInstance
	 */
	public WorkflowTaskInstance getTaskInstanceById(String id);

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

	/**
	 * Signales the process instance to set the token onto the next node
	 * 
	 * @param id the processinstance id
	 */
	public void signal(final String id);

	/**
	 * Retrieves all Tasks that has been started yet (but must not be ended).
	 * 
	 * @param id the processinstance id
	 * @return List of TaskInstances
	 */
	public List<WorkflowTaskInstance> getTasksByActiveWorkflowId(final String id);

	/**
	 * Gets an WorkflowInstance by the id
	 * 
	 * @param processInstanceId the processinstance id
	 * @return
	 */
	public WorkflowInstance getWorkflowInstanceById(final String processInstanceId);

	/**
	 * Updates a taskinstance. Be clear that only localy properties will be
	 * updates. No process-wide properties are updated!
	 * 
	 * @param wti The TaskInstance
	 */
	public void updateTaskInstance(WorkflowTaskInstance wti);

	/**
	 * 
	 * @param workflowInstance
	 */
	public void updateWorkflowInstance(WorkflowInstance workflowInstance);

	/**
	 * Retrieves users tasklist
	 * 
	 * @param username The Username
	 * @return List of tasks owned by the user
	 */
	public List<WorkflowTaskInstance> getAllActionTasksByUser(String username);

	/**
	 * Retrieves users pooled-tasklist
	 * 
	 * @param username The Username
	 * @return List of tasks owned by the user
	 */
	public List<WorkflowTaskInstance> getAllActionPooledTasksByUser(String username);

	/**
	 * its just like rm -rf / on unix-platforms ;-)
	 */
	public void deleteAllActiveWorkflows();

	/**
	 * 
	 * @param taskId
	 * @param assignee
	 */
	public void assignUserToTask(String taskId, String assignee);

	/**
	 * 
	 * @param workflow_id
	 * @return
	 */
	public List<WorkflowTaskInstance> getTaskInstancesByActiveWorkflow(final String workflow_id);

	/**
	 * 
	 * @param workflowTaskId
	 * @return
	 */
	public WorkflowInstance getWorkflowInstanceByTaskInstance(String workflowTaskId);

	/**
	 * 
	 * @param workflowTaskId
	 * @return
	 */
	public List<WorkflowInstance> getAllWorkflows();
	
	/**
	 * All suspended TaskInstances will be returned.
	 */
	public List<WorkflowTaskInstance> getAllSuspendedTaskInstances();

	/**
	 * All suspended TaskInstances will be returned for the given user name.
	 */
	public List<WorkflowTaskInstance> getAllSuspendedTaskInstances(final String actorId);
}
