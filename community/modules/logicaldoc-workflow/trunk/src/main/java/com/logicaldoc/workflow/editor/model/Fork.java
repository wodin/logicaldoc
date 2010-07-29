package com.logicaldoc.workflow.editor.model;

import java.util.LinkedList;
import java.util.List;

public class Fork extends BaseWorkflowModel {

	private static final long serialVersionUID = 1L;

	private List<WorkflowTask> workflowTasks = new LinkedList<WorkflowTask>();

	public Fork() {
		super();
	}

	public Fork(BaseWorkflowModel workflowModel) {
		super(workflowModel);
	}

	@Override
	public String getImage() {
		return "fork.png";
	}

	@Override
	public String getTemplate() {
		return "fork";
	}

	@Override
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		Fork fork = (Fork) baseWorkflowModel;
		workflowTasks = fork.workflowTasks;

		return null;
	}

	public List<WorkflowTask> getWorkflowTasks() {
		return workflowTasks;
	}

	@Override
	public boolean isPossibleStartState() {
		return true;
	}

	@Override
	public String getType() {
		return "fork";
	}
}