package com.logicaldoc.workflow.editor.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WorkflowTask extends BaseWorkflowModel {

	private static final long serialVersionUID = 1L;

	private String description;

	private Integer dueDateValue = 0;

	private String dueDateUnit;

	private Integer remindTimeValue = 0;

	private String remindTimeUnit;

	private List<Assignee> assignees;

	private List<Transition> transitions = new LinkedList<Transition>();

	private boolean parallelProcessingSupported;

	public List<Transition> getTransitions() {
		return transitions;
	}

	public WorkflowTask() {
		this.assignees = new ArrayList<Assignee>();
	}

	public WorkflowTask(WorkflowTask workflowTask) {
		this.copy(workflowTask);

	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getDueDateUnit() {
		return dueDateUnit;
	}

	public void setRemindTimeUnit(String remindTimeUnit) {
		this.remindTimeUnit = remindTimeUnit;
	}

	public void setDueDateUnit(String dueDateUnit) {
		this.dueDateUnit = dueDateUnit;
	}

	public String getRemindTimeUnit() {
		return remindTimeUnit;
	}

	public void addAssignee() {
		this.assignees.add(new Assignee());
	}

	public void setAssignees(List<Assignee> assignes) {
		this.assignees = assignes;
	}

	public List<Assignee> getAssignees() {
		return assignees;
	}

	public void setRemindTimeValue(Integer remindTimeValue) {
		this.remindTimeValue = remindTimeValue;
	}

	public Integer getRemindTimeValue() {
		return remindTimeValue;
	}

	public void setDueDateValue(Integer dueDateValue) {
		this.dueDateValue = dueDateValue;
	}

	public Integer getDueDateValue() {
		return dueDateValue;
	}

	public void setParallelProcessingSupported(boolean parallelProcessingSupported) {
		this.parallelProcessingSupported = parallelProcessingSupported;
	}

	public boolean getParallelProcessingSupported() {
		return parallelProcessingSupported;
	}

	public boolean isParallelProcessingSupported() {
		return parallelProcessingSupported;
	}

	@Override
	public String getImage() {
		return "task.png";
	}

	@Override
	public String getTemplate() {
		return "task";
	}

	public void addTransition(Transition _transition) {

		for (Transition transition : this.transitions) {
			if (transition.getId().equals(_transition.getId()))
				throw new WorkflowEditorException("You can not add the same transition twice");

			if (transition.getName().equals(_transition.getName()))
				throw new WorkflowEditorException("You can not add the a transition with equal names");

		}

		this.transitions.add(_transition);

	}

	@Override
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		super.copy(baseWorkflowModel);

		WorkflowTask workflowTask = (WorkflowTask) baseWorkflowModel;
		this.dueDateValue = workflowTask.dueDateValue;
		this.dueDateUnit = workflowTask.dueDateUnit;
		this.remindTimeUnit = workflowTask.remindTimeUnit;
		this.remindTimeValue = workflowTask.remindTimeValue;
		this.assignees = workflowTask.assignees;
		this.parallelProcessingSupported = workflowTask.parallelProcessingSupported;
		this.transitions = workflowTask.transitions;
		return this;
	}

	@Override
	public boolean isPossibleStartState() {
		return true;
	}

	@Override
	public String getType() {
		return "task";
	}

}
