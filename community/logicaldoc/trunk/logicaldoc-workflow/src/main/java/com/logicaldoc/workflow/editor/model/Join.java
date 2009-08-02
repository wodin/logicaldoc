package com.logicaldoc.workflow.editor.model;

import java.util.List;

import com.logicaldoc.workflow.editor.controll.EditController;
import com.logicaldoc.workflow.editor.controll.JoinController;
import com.logicaldoc.workflow.editor.message.DeployMessage;

public class Join extends BaseWorkflowModel {

	private BaseWorkflowModel destination;

	@Override
	public String getImage() {
		return "join.png";
	}

	@Override
	public String getTemplate() {
		return "join";
	}

	@Override
	public EditController getController() {
		// TODO: Implement Controller
		return new JoinController();
	}

	@Override
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDestination(BaseWorkflowModel destination) {
		this.destination = destination;
	}

	public BaseWorkflowModel getDestination() {
		return destination;
	}
	
	@Override
	public boolean isPossibleStartState() {
		return false;
	}
	
	@Override
	public String getType() {
		return "join";
	}

	@Override
	public void checkForDeploy(List<DeployMessage> failures) {
		
		if(this.getDestination() == null)
			failures.add(new DeployMessage(this, "No destination has been specified"));
		
	}
}
