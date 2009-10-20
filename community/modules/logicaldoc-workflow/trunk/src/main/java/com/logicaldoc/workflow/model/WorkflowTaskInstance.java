package com.logicaldoc.workflow.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WorkflowTaskInstance implements FetchModel {

	public static enum STATE {
		OPEN("open"), CANCELED("canceled"), DONE("done"), ALL("all"), NOT_YET_STARTED(
				"not_yet_started"), SUSPENDED("suspended"), STARTED("started"), RESUME(
				"resume");
		private final String val; // Message string

		STATE(String val) {

			this.val = val;

		}

		public String getVal() {
			return val;
		}

	};

	private String id;

	private String name;

	private HashMap<String, Object> properties = new HashMap<String, Object>();

	private List<Transition> transitions = new LinkedList<Transition>();

	private STATE state;

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public List<Transition> getTransitions() {
		return transitions;
	}

	public HashMap<String, Object> getProperties() {
		return properties;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProperties(HashMap<String, Object> properties) {
		this.properties = properties;
	}

	public void setState(STATE state) {
		this.state = state;
	}

	public void setTransitions(List<Transition> transitions) {
		this.transitions = transitions;
	}

	public STATE getState() {
		return state;
	}

	public WorkflowTaskInstance() {

	}

	public WorkflowTaskInstance(WorkflowTaskInstance workflowTaskInstance) {
		this.id = workflowTaskInstance.id;
		this.name = workflowTaskInstance.id;
		this.state = workflowTaskInstance.state;
		this.transitions = workflowTaskInstance.transitions;

		if (workflowTaskInstance.properties != null) {
			for (Map.Entry<String, Object> entry : workflowTaskInstance.properties
					.entrySet()) {
				this.properties.put((String) entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	public boolean isUpdateable() {
		return true;
	}

}
