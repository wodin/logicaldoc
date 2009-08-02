package com.logicaldoc.workflow.editor.model;

import java.util.List;

import com.logicaldoc.workflow.editor.controll.EditController;
import com.logicaldoc.workflow.editor.message.DeployMessage;

public class EndState extends BaseWorkflowModel{

	@Override
	public EditController getController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImage() {
		return "endState.png";
	}

	@Override
	public String getTemplate() {
		return "endState";
	}

	@Override
	public boolean isPossibleStartState() {
		return false;
	}
	
	@Override
	public String getType() {
		return "endState";
	}
	
	@Override
	public void checkForDeploy(List<DeployMessage> failures) {
		
	}
}
