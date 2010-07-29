package com.logicaldoc.workflow.editor.model;

public class Join extends BaseWorkflowModel {

	private static final long serialVersionUID = 1L;

	private BaseWorkflowModel destination;

	public Join() {
		super();
	}

	public Join(BaseWorkflowModel workflowModel) {
		super(workflowModel);
	}

	@Override
	public String getImage() {
		return "join.png";
	}

	@Override
	public String getTemplate() {
		return "join";
	}

	@Override
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
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
}