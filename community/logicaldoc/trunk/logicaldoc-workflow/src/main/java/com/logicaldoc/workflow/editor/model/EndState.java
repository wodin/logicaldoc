package com.logicaldoc.workflow.editor.model;

import com.logicaldoc.workflow.editor.controll.EditController;

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
}
