package com.logicaldoc.workflow.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WorkflowTaskInstance {
	
	public static enum STATE {CANCELED, DONE, ALL, ACTIVE};
	
	public String id;

	public String name;

	private String description;
	
	public HashMap<String, Object> properties = new HashMap<String, Object>();

	public List<Transition> transitions = new LinkedList<Transition>(); 
	
	public STATE state;
	
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
	
	public String getDescription() {
		return description;
	}

}
