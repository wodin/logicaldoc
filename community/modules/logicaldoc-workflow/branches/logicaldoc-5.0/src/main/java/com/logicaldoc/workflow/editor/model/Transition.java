package com.logicaldoc.workflow.editor.model;

import java.util.List;

import com.logicaldoc.workflow.editor.controll.EditController;
import com.logicaldoc.workflow.editor.controll.TransitionController;
import com.logicaldoc.workflow.editor.message.DeployMessage;

public class Transition extends BaseWorkflowModel {

	private static final long serialVersionUID = 1L;

	private boolean finishedTransition;

	private BaseWorkflowModel baseWorkflowModel;

	private boolean terminatesParallelProcess;

	public Transition() {

	}

	public Transition(Transition transition) {

	}

	public void setDestination(BaseWorkflowModel baseWorkflowModel) {
		this.baseWorkflowModel = baseWorkflowModel;
	}

	public BaseWorkflowModel getDestination() {
		return baseWorkflowModel;
	}

	public void setFinishedTransition(boolean finishedTransition) {
		this.finishedTransition = finishedTransition;
	}

	public boolean isFinishedTransition() {
		return finishedTransition;
	}

	@Override
	public String getImage() {
		return null;
	}

	@Override
	public String getTemplate() {
		return "transition";
	}

	@Override
	public EditController getController() {
		return new TransitionController();
	}

	@Override
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		super.copy(baseWorkflowModel);

		this.baseWorkflowModel = ((Transition) baseWorkflowModel).baseWorkflowModel;
		this.finishedTransition = ((Transition) baseWorkflowModel).finishedTransition;

		return this;
	}

	@Override
	public boolean isPossibleStartState() {
		return false;
	}

	@Override
	public String getType() {
		return "transition";
	}

	public void setTerminatesParallelProcess(boolean terminatesParallelProcess) {
		this.terminatesParallelProcess = terminatesParallelProcess;
	}

	public boolean isTerminatesParallelProcess() {
		return terminatesParallelProcess;
	}

	public boolean getTerminatesParallelProcess() {
		return this.terminatesParallelProcess;
	}

	@Override
	public void checkForDeploy(List<DeployMessage> failures) {

		if (getName().equals(""))
			failures.add(new DeployMessage(this,
					"Specifiy a name for this transition"));

		if (getDestination() == null)
			failures.add(new DeployMessage(this, "The destination is not set"));

	}

}
