package com.logicaldoc.workflow.model;

import java.io.Serializable;
import java.util.Map;

public class WorkflowDefinition {
	
	private String definitionId;
	
	private String name; 
	
	private String description;
	
	private Map<String, Serializable> properties;
	
	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}
	
	public String getDefinitionId() {
		return definitionId;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getName() {
		return name;
	}
	
	public void setProperties(Map<String, Serializable> properties) {
		this.properties = properties;
	}
	
	public Map<String, Serializable> getProperties() {
		return properties;
	}
	
}
