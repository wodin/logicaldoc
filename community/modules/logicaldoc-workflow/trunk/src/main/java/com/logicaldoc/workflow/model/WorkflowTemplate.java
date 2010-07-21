package com.logicaldoc.workflow.model;

import java.util.LinkedList;
import java.util.List;

import com.logicaldoc.workflow.editor.model.BaseWorkflowModel;
import com.logicaldoc.workflow.editor.model.Transition;

public class WorkflowTemplate extends BaseWorkflowModel {
	private static final long serialVersionUID = 1L;

	private String name;

	private String description;

	private String startState;

	private WorkflowMessage assignmentMessage;

	private WorkflowMessage reminderMessage;

	private String supervisor;

	private List<BaseWorkflowModel> workflowComponents = new LinkedList<BaseWorkflowModel>();

	private List<Transition> transitions;

	public List<Transition> getTransitions() {
		return transitions;
	}

	public WorkflowTemplate() {
		this.assignmentMessage = new WorkflowMessage("", "");
		this.reminderMessage = new WorkflowMessage("", "");
	}

	public void setWorkflowComponents(List<BaseWorkflowModel> workflowComponents) {
		this.workflowComponents = workflowComponents;
	}

	public List<BaseWorkflowModel> getWorkflowComponents() {
		return workflowComponents;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartState() {
		return startState;
	}

	public void setStartState(String startState) {
		this.startState = startState;
	}

	public void setAssignmentMessage(WorkflowMessage assignmentMessage) {
		this.assignmentMessage = assignmentMessage;
	}

	public WorkflowMessage getAssignmentMessage() {
		return assignmentMessage;
	}

	public void setReminderMessage(WorkflowMessage reminderMessage) {
		this.reminderMessage = reminderMessage;
	}

	public WorkflowMessage getReminderMessage() {
		return reminderMessage;
	}

	public BaseWorkflowModel getWorkflowComponentById(String id) {

		if (this.workflowComponents == null)
			return null;

		for (BaseWorkflowModel baseWorkflowModel : this.workflowComponents) {
			if (baseWorkflowModel.getId().equals(id))
				return baseWorkflowModel;
		}

		return null;
	}

	@Override
	public String getImage() {
		throw new UnsupportedOperationException("Image does not exists for this type of WorkflowComponent");
	}

	@Override
	public String getTemplate() {
		throw new UnsupportedOperationException("template does not exists for this type of WorkflowComponent");
	}

	@Override
	public String getType() {
		return "workflowTemplate";
	}

	@Override
	public boolean isPossibleStartState() {
		throw new UnsupportedOperationException("not enddstate can be entered by this component");
	}

	public String getSupervisor() {
		return supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}
}
