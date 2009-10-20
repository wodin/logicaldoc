package com.logicaldoc.workflow.editor.model;

import java.util.List;

import com.logicaldoc.workflow.editor.controll.EditController;
import com.logicaldoc.workflow.editor.message.DeployMessage;

public class EndState extends BaseWorkflowModel{

	private static final long serialVersionUID = 1L;

	@Override
	public EditController getController() {
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
