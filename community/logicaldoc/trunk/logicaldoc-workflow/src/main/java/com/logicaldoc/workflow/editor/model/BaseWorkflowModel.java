package com.logicaldoc.workflow.editor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.logicaldoc.workflow.editor.controll.EditController;

public abstract class BaseWorkflowModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private List<Transition> transitions;
	
	private String name;
	
	public BaseWorkflowModel() {
		id = UUID.randomUUID().toString();
		this.transitions = new ArrayList<Transition>();
	}

	public BaseWorkflowModel(BaseWorkflowModel workflowModel){
		this.copy(workflowModel);
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	
	public void addTransition(Transition _transition) {

		for (Transition transition : this.transitions) {
			if (transition.getId().equals(_transition.getId()))
				throw new WorkflowEditorException(
						"You can not add the same transition twice");

			if (transition.getName().equals(_transition.getName()))
				throw new WorkflowEditorException(
						"You can not add the a transition with equal names");

		}

		this.transitions.add(_transition);

	}

	public List<Transition> getTransitions() {
		return this.transitions;
	}
	
	public abstract String getType();
	
	public abstract String getImage();

	public abstract String getTemplate();
	
	public abstract EditController getController();
	
	public abstract boolean isPossibleStartState();
	
	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		this.id = baseWorkflowModel.id;
		this.name = baseWorkflowModel.name;
		this.transitions = baseWorkflowModel.transitions;
		
		return this;
	}

}
