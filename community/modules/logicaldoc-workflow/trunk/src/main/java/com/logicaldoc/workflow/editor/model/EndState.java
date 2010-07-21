package com.logicaldoc.workflow.editor.model;

public class EndState extends BaseWorkflowModel {

	private static final long serialVersionUID = 1L;

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
