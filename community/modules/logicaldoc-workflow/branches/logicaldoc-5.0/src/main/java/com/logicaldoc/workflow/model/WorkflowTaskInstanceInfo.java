package com.logicaldoc.workflow.model;

import com.logicaldoc.util.Context;
import com.logicaldoc.workflow.TemplateService;
import com.logicaldoc.workflow.WorkflowConstants;
import com.logicaldoc.workflow.WorkflowService;
import com.logicaldoc.workflow.editor.model.WorkflowTask;

public class WorkflowTaskInstanceInfo extends WorkflowTaskInstance {

	public WorkflowTaskInstanceInfo(WorkflowTaskInstance workflowTaskInstance) {
		super(workflowTaskInstance);

		WorkflowService workflowService = (WorkflowService) Context.getInstance().getBean("workflowService");

		TemplateService templateService = (TemplateService) Context.getInstance().getBean("templateService");

		WorkflowInstance workflowInstance = workflowService.getWorkflowInstanceByTaskInstance(workflowTaskInstance
				.getId(), FETCH_TYPE.INFO);

		WorkflowTemplate workflowTemplate = (WorkflowTemplate) workflowInstance.getProperties().get(
				WorkflowConstants.VAR_TEMPLATE);

		WorkflowTask workflowTask = (WorkflowTask) workflowTemplate.getWorkflowComponentById((String) getProperties()
				.get(WorkflowConstants.VAR_TASKID));

		workflowInstance.getProperties().get(WorkflowConstants.VAR_TEMPLATE);

		if (workflowTask != null) {
			String taskDescription = templateService.transformWorkflowTask(workflowTask, workflowInstance, this,
					workflowTask.getDescription());

			getProperties().put(WorkflowConstants.VAR_DESCRIPTION, taskDescription);

			String taskDueDateValue = templateService.transformWorkflowTask(workflowTask, workflowInstance, this,
					workflowTask.getDueDateValue().toString());

			String taskDueDateUnit = templateService.transformWorkflowTask(workflowTask, workflowInstance, this,
					workflowTask.getDueDateUnit());

			if (taskDueDateValue != null && taskDueDateUnit != null && !taskDueDateUnit.equals(""))
				getProperties().put(WorkflowConstants.VAR_DUEDATE, taskDueDateValue + " " + taskDueDateUnit);
			else
				getProperties().put(WorkflowConstants.VAR_DUEDATE, "");
		} else {
			getProperties().put(WorkflowConstants.VAR_DESCRIPTION, "");
			getProperties().put(WorkflowConstants.VAR_DUEDATE, "");
		}
	}

	@Override
	public final boolean isUpdateable() {
		return false;
	}
}
