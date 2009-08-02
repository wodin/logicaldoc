package com.logicaldoc.workflow.editor.model;

import java.util.LinkedList;
import java.util.List;

import com.logicaldoc.workflow.editor.controll.EditController;
import com.logicaldoc.workflow.editor.controll.ForkController;
import com.logicaldoc.workflow.editor.message.DeployMessage;

public class Fork extends BaseWorkflowModel{
	
	private List<WorkflowTask> workflowTasks = new LinkedList<WorkflowTask>();
	
	@Override
	public String getImage() {
		return "fork.png";
	}
	
	
	@Override
	public String getTemplate() {
		return "fork";
	}
	
	@Override
	public EditController getController() {
		return new ForkController();
	}
	
	@Override
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		Fork fork = (Fork)baseWorkflowModel;
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
	
	@Override
	public void checkForDeploy(List<DeployMessage> failures) {
		if(getWorkflowTasks().size() == 0)
			failures.add(new DeployMessage(this, "No element has been attached to this element"));
		
	}
}
