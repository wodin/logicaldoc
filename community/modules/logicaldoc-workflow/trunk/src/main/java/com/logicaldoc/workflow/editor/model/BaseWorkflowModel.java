package com.logicaldoc.workflow.editor.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public abstract class BaseWorkflowModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private List<Transition> transitions = new LinkedList<Transition>();

	private boolean selected = false;

	public List<Transition> getTransitions() {
		return transitions;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public BaseWorkflowModel() {
		id = UUID.randomUUID().toString();
	}

	public BaseWorkflowModel(BaseWorkflowModel workflowModel) {
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public abstract String getType();

	public abstract String getImage();

	public abstract String getTemplate();

	public abstract boolean isPossibleStartState();

	public BaseWorkflowModel copy(BaseWorkflowModel baseWorkflowModel) {
		this.id = baseWorkflowModel.id;
		this.name = baseWorkflowModel.name;

		return this;
	}

}
