package com.logicaldoc.workflow.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WorkflowTaskInstance {
		
	public String id;

	public String name;
	
	public HashMap<String, Object> properties = new HashMap<String, Object>();

	public List<Transition> transitions = new LinkedList<Transition>(); 

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
}
