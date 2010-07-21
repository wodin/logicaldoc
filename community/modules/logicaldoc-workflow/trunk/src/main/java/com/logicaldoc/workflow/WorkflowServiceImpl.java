package com.logicaldoc.workflow;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.security.UserSession;
import com.logicaldoc.core.security.dao.UserDAO;
import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.editor.WorkflowHistoryDAO;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplate;
import com.logicaldoc.workflow.editor.WorkflowPersistenceTemplateDAO;
import com.logicaldoc.workflow.exception.WorkflowException;
import com.logicaldoc.workflow.model.FetchModel;
import com.logicaldoc.workflow.model.FetchModel.FETCH_TYPE;
import com.logicaldoc.workflow.model.WorkflowDefinition;
import com.logicaldoc.workflow.model.WorkflowInstance;
import com.logicaldoc.workflow.model.WorkflowInstanceInfo;
import com.logicaldoc.workflow.model.WorkflowTaskInstance;
import com.logicaldoc.workflow.model.WorkflowTaskInstanceInfo;
import com.logicaldoc.workflow.model.WorkflowTemplate;
import com.logicaldoc.workflow.transform.WorkflowTransformService;

public class WorkflowServiceImpl implements WorkflowService {

	protected static Log log = LogFactory.getLog(WorkflowServiceImpl.class);

	private WorkflowEngine workflowComponent;

	private WorkflowTransformService workflowTransformService;

	public void setWorkflowTransformService(WorkflowTransformService workflowTransformService) {
		this.workflowTransformService = workflowTransformService;
	}

	public void setWorkflowComponent(WorkflowEngine workflowComponent) {
		this.workflowComponent = workflowComponent;
	}

	@Override
	public void deployWorkflow(WorkflowTemplate workflowTemplate) {

		Serializable definition = (Serializable) this.workflowTransformService
				.fromObjectToWorkflowDefinition(workflowTemplate);

		try {
			workflowComponent.deployWorkflow(workflowTemplate, definition);
		} catch (Exception e) {
			throw new WorkflowException(e);
		}

	}

	@Override
	public void endTask(String taskId, String transitionName) {

		WorkflowTaskInstance taskInstance = workflowComponent.getTaskInstanceById(taskId);
		taskInstance.getProperties().put(WorkflowConstants.VAR_OUTCOME, transitionName);

		workflowComponent.updateTaskInstance(taskInstance);

		workflowComponent.processTaskToEnd(taskId, transitionName);
	}

	@Override
	public void undeployWorkflow(String processId) {
		workflowComponent.undeployWorkflow(processId);
	}

	public WorkflowInstance startWorkflow(WorkflowDefinition workflowDefinition, Map<String, Serializable> properties,
			UserSession session) {
		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
				.getBean(WorkflowPersistenceTemplateDAO.class);

		WorkflowInstance workflowInstance = null;
		if (workflowDefinition != null && !workflowDefinition.getDefinitionId().isEmpty()) {
			log.info("workflowComponent: " + workflowComponent);
			log.info("workflowDefinition: " + workflowDefinition);
			log.info("workflowDefinition.getDefinitionId()" + workflowDefinition.getDefinitionId());
			log.info("properties size: " + properties.size());

			workflowInstance = workflowComponent.startWorkflow(workflowDefinition.getDefinitionId(), properties);

			// Create the workflow history event
			WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
					WorkflowHistoryDAO.class);
			WorkflowHistory transaction = new WorkflowHistory();

			WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(workflowInstance.getName());

			transaction.setTemplateId(template.getId());
			transaction.setTemplateId(template.getId());
			transaction.setInstanceId(workflowInstance.getId());
			transaction.setDate(new Date());
			transaction.setEvent(WorkflowHistory.EVENT_WORKFLOW_START);
			transaction.setComment("");
			if (session != null) {
				transaction.setUser(userDao.findById(session.getUserId()));
				transaction.setSessionId(session.getId());
			}

			workflowHistoryDao.store(transaction);

			// Create a workflow history for each document associated to
			// this
			// workflow instance
			Set<Long> docIds = (Set<Long>) workflowInstance.getProperties().get(WorkflowConstants.VAR_DOCUMENTS);

			for (Long docId : docIds) {
				// Create the workflow history event
				WorkflowHistory docAppended = new WorkflowHistory();
				docAppended.setTemplateId(template.getId());
				docAppended.setInstanceId(workflowInstance.getId());
				docAppended.setDate(new Date());
				docAppended.setEvent(WorkflowHistory.EVENT_WORKFLOW_DOCAPPENDED);
				docAppended.setDocId(docId);
				docAppended.setComment("");
				if (session != null) {
					docAppended.setUser(userDao.findById(session.getUserId()));
					docAppended.setSessionId(session.getId());
				}
				workflowHistoryDao.store(docAppended);
			}

			return workflowInstance;
		} else
			return null;
	}

	@Override
	public List<WorkflowDefinition> getAllDefinitions() {
		List<WorkflowDefinition> processDefinitions = workflowComponent.getAllProcessDefinitions();

		return processDefinitions;
	}

	@Override
	public WorkflowTaskInstance getTaskInstanceByTaskId(String id, FETCH_TYPE fetch_type) {

		WorkflowTaskInstance taskInstance = workflowComponent.getTaskInstanceById(id);

		if (fetch_type.equals(FETCH_TYPE.FORUPDATE))
			return taskInstance;

		else
			return new WorkflowTaskInstanceInfo(taskInstance);
	}

	@Override
	public List<WorkflowTaskInstance> getAllActiveTaskInstances() {
		List<WorkflowTaskInstance> taskInstances = workflowComponent.getAllActiveTaskInstances();

		return taskInstances;

	}

	@Override
	public List<WorkflowTaskInstance> getAllTaskInstances() {
		List<WorkflowTaskInstance> taskInstances = workflowComponent.getAllTaskInstances();

		return taskInstances;

	}

	public void signal(String workflowInstanceId) {
		workflowComponent.signal(workflowInstanceId);
	}

	@Override
	public List<WorkflowTaskInstance> getTaskInstancesByWorkflowInstanceId(String workflowInstanceId) {
		List<WorkflowTaskInstance> workflowInstances = workflowComponent.getTasksByActiveWorkflowId(workflowInstanceId);
		return workflowInstances;
	}

	@Override
	public WorkflowInstance getWorkflowInstanceById(String workflowinstanceId, FetchModel.FETCH_TYPE fetch_type) {

		WorkflowInstance workflowInstance = workflowComponent.getWorkflowInstanceById(workflowinstanceId);

		if (fetch_type.equals(FetchModel.FETCH_TYPE.FORUPDATE))
			return workflowInstance;

		else
			return new WorkflowInstanceInfo(workflowInstance);
	}

	@Override
	public void stopWorkflow(String processInstanceId, UserSession session) {
		workflowComponent.stopWorkflow(processInstanceId);

		UserDAO userDao = (UserDAO) Context.getInstance().getBean(UserDAO.class);

		// Create the workflow history event
		WorkflowHistoryDAO workflowHistoryDao = (WorkflowHistoryDAO) Context.getInstance().getBean(
				WorkflowHistoryDAO.class);
		WorkflowHistory transaction = new WorkflowHistory();
		WorkflowInstance instance = getWorkflowInstanceById(processInstanceId, FETCH_TYPE.INFO);
		WorkflowPersistenceTemplateDAO workflowTemplateDao = (WorkflowPersistenceTemplateDAO) Context.getInstance()
				.getBean(WorkflowPersistenceTemplateDAO.class);
		WorkflowPersistenceTemplate template = workflowTemplateDao.findByName(instance.getName());

		transaction.setTemplateId(template.getId());
		transaction.setInstanceId(processInstanceId);
		transaction.setDate(new Date());
		transaction.setSessionId(session.getId());
		transaction.setEvent(WorkflowHistory.EVENT_WORKFLOW_END);
		transaction.setComment("");
		transaction.setUser(userDao.findById(session.getUserId()));

		workflowHistoryDao.store(transaction);
	}

	@Override
	public void updateWorkflow(WorkflowInstance workflowInstance) {
		workflowComponent.updateWorkflowInstance(workflowInstance);
	}

	@Override
	public void updateWorkflow(WorkflowTaskInstance taskInstance) {
		workflowComponent.updateTaskInstance(taskInstance);
	}

	@Override
	public void deleteAllActiveWorkflows() {
		this.workflowComponent.deleteAllActiveWorkflows();
	}

	public List<WorkflowTaskInstance> getTaskInstancesForUser(String username) {
		return this.workflowComponent.getAllActionTasksByUser(username);
	}

	public List<WorkflowTaskInstance> getSuspendedTaskInstances() {
		return this.workflowComponent.getAllSuspendedTaskInstances();
	}

	public List<WorkflowTaskInstance> getSuspendedTaskInstancesForUser(String username) {
		return this.workflowComponent.getAllSuspendedTaskInstances(username);
	}

	@Override
	public List<WorkflowTaskInstance> getPooledTaskInstancesForUser(String username) {
		return this.workflowComponent.getAllActionPooledTasksByUser(username);
	}

	public void assign(String taskId, String assignee) {
		this.workflowComponent.assignUserToTask(taskId, assignee);
	}

	public List<WorkflowTaskInstance> getWorkflowTasks(WorkflowInstance workflowInstance,
			WorkflowTaskInstance.STATE taskState, TASK_SORT sort) {

		List<WorkflowTaskInstance> workflowTaskInstances = this.workflowComponent
				.getTaskInstancesByActiveWorkflow(workflowInstance.getId());
		List<WorkflowTaskInstance> instances = new LinkedList<WorkflowTaskInstance>();

		for (WorkflowTaskInstance taskInstance : workflowTaskInstances) {

			if (taskInstance.getState().equals(WorkflowTaskInstance.STATE.ALL) == false) {
				if (taskInstance.getState().equals(taskState) == false)
					continue;
			}

			instances.add(taskInstance);

		}

		for (int i = 0; i < instances.size() - 1; i++) {

			for (int j = 0; j < instances.size() - 1; j++) {

				WorkflowTaskInstance currentElement = instances.get(j);
				WorkflowTaskInstance nextElement = instances.get(j + 1);

				Date currentDate = (Date) currentElement.getProperties().get(WorkflowConstants.VAR_ENDDATE);
				Date nextDate = (Date) nextElement.getProperties().get(WorkflowConstants.VAR_ENDDATE);

				if (currentDate == null || nextDate == null)
					continue;

				if (sort.equals(TASK_SORT.ASC)) {

					// the current date is before the second date
					if (nextDate.after(currentDate))
						continue;

					instances.set(j, nextElement);
					instances.set(j + 1, currentElement);

				} else {
					if (nextDate.before(currentDate))
						continue;

					instances.set(j, nextElement);
					instances.set(j + 1, currentElement);

				}

			}
		}

		return instances;

	}

	public List<WorkflowTaskInstance> getWorkflowHistory(WorkflowInstance workflowInstance) {
		return this.getWorkflowTasks(workflowInstance, WorkflowTaskInstance.STATE.DONE, TASK_SORT.ASC);
	}

	public WorkflowInstance getWorkflowInstanceByTaskInstance(String workflowTaskId, FetchModel.FETCH_TYPE fetch_type) {
		WorkflowInstance workflowInstance = this.workflowComponent.getWorkflowInstanceByTaskInstance(workflowTaskId);

		if (fetch_type.equals(FetchModel.FETCH_TYPE.FORUPDATE))
			return workflowInstance;

		else
			return new WorkflowInstanceInfo(workflowInstance);
	}

	public List<WorkflowInstance> getAllWorkflows() {
		return this.workflowComponent.getAllWorkflows();
	}

}
